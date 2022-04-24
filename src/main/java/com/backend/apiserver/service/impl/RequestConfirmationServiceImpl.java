package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.FinishRequestRequest;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.configuration.CommonProperties;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRejection;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.RequestAnnouncementNotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.mapper.CommentMapper;
import com.backend.apiserver.mapper.MentorMapper;
import com.backend.apiserver.mapper.MoneyExchangeHistoryMapper;
import com.backend.apiserver.mapper.NotificationBuilder;
import com.backend.apiserver.mapper.RequestMapper;
import com.backend.apiserver.repository.CommentRepository;
import com.backend.apiserver.repository.MentorRejectionRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.service.RequestConfirmationService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.FirebaseUtils;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RequestConfirmationServiceImpl implements RequestConfirmationService {

	private JwtUtils jwtUtils;
	private RequestRepository requestRepository;
	private MentorRepository mentorRepository;
	private MentorRequestRepository mentorRequestRepository;
	private UserRepository userRepository;
	private CommentRepository commentRepository;
	private RequestAnnouncementRepository requestAnnouncementRepository;
	private MentorRejectionRepository mentorRejectionRepository;
	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;
	private UserDetailRepository userDetailRepository;
	private EmailSenderService emailSenderService;
	private CommonProperties commonProperties;

	@Override
	@AnestTransactional
	public void confirmFinishRequest(FinishRequestRequest finishRequestRequest) throws RequestNotFoundException, MentorRequestNotFoundException, MessagingException, IOException, TemplateException, URISyntaxException {
		confirmFinishRequest(
				jwtUtils.getUserId(),
				finishRequestRequest.getRequestId(),
				finishRequestRequest.getRating(),
				finishRequestRequest.getComment()
		);
	}

	private void confirmFinishRequest(Long userId, Long requestId, int rating, String comment) throws RequestNotFoundException, MentorRequestNotFoundException, MessagingException, IOException, TemplateException, URISyntaxException {
		//update request status
		Request request = requestRepository.findRequestByIdAndUserIdAndStatus(requestId, userId, Status.DOING);
		if (Objects.isNull(request)) {
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + requestId);
		}
		request.setCompleteTime(LocalDateTime.now());
		request.setStatus(Status.COMPLETE);
		requestRepository.saveAndFlush(request);

		removePendingMentors(requestId);

		requestAnnouncementRepository.deleteAllByRequestIdAndStatus(requestId, Status.CONFLICT);

		// Get by request id and pending status
		MentorRequest mentorRequest = mentorRequestRepository.findMentorRequestByRequestIdAndStatus(
				requestId,
				Status.DOING
		);

		//when mentor delete request
		if (Objects.isNull(mentorRequest)) {
			throw new MentorRequestNotFoundException("Mentor request is deleted by mentor or user creator");
		}
		mentorRequest.setStatus(Status.COMPLETE);
		mentorRequestRepository.saveAndFlush(mentorRequest);

		//calculate doing time of a request
		int doingMinutes = (int) Duration.between(request.getStartDoingTime(), LocalDateTime.now()).toMinutes();

		UserDetail userDetail = userDetailRepository.findByUserId(userId);
		userDetail.setTotalHoursHiredMentor(userDetail.getTotalHoursHiredMentor() + doingMinutes);
		userDetail.setTotalPeopleHired(userDetail.getTotalPeopleHired() + 1);
		userDetailRepository.saveAndFlush(userDetail);

		//insert rate and comments
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);

		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(mentorRequest.getMentor().getId(), Status.ACTIVE);

		if (Objects.isNull(mentor))
			throw new MentorRequestNotFoundException("Mentor might be inactive or unregister mentor role");

		if (!Strings.isNullOrEmpty(comment) && rating != 0) {
			//Optional insert rating and comment
			MentorMapper.setRating(mentor, rating);
			mentor.setAverageRating(MentorMapper.calculateAverageRating(mentor));
			commentRepository.saveAndFlush(
					CommentMapper.createCommentEntity(user, mentor, rating, comment)
			);
		}

		mentor.setTotalMoneyIn(mentor.getTotalMoneyIn() + request.getPrice());
		mentor.setTotalMoneyCurrent(mentor.getTotalMoneyCurrent() + request.getPrice());
		mentor.setTotalRequestFinish(mentor.getTotalRequestFinish() + 1);
		mentor.setTotalHoursBeHired(mentor.getTotalHoursBeHired() + doingMinutes);
		mentorRepository.saveAndFlush(mentor);
		moneyExchangeHistoryRepository.saveAndFlush(MoneyExchangeHistoryMapper.createMoneyExchangeHistoryEntity(user, mentor, request.getPrice()));

		//Push notification
		FirebaseUtils.pushNotification(
				NotificationBuilder.createCompleteRequest(requestId, request.getPrice()),
				mentor.getUser().getUsername()
		);

		//Send mail mentor cc user
		Map<String, Object> params = new HashMap<>();
		User mentorUser = mentor.getUser();
		params.put("mentorFullName", mentorUser.getUserDetail().getFullName());
		params.put("amount", request.getPrice());
		params.put("userFullName", user.getUserDetail().getFullName());
		params.put("requestId", String.valueOf(requestId));
		String baseURL = commonProperties.getFrontendURL();
		String requestURL = new URIBuilder(new URI(baseURL))
				.setPath(Constants.REQUEST_PATH + "/" + requestId)
				.toString();

		params.put("requestURL", requestURL);

		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(mentorUser.getEmail()),
				params,
				MailRequest.TEMPLATE_REQUEST_SUCCESS,
				MailRequest.TITLE_REQUEST_SUCCESS
		);
		mailRequest.setCc(FormatUtils.makeArray(MailRequest.CC, user.getEmail()));
		emailSenderService.sendEmailTemplate(mailRequest);
	}

	@Override
	@AnestTransactional
	public void confirmNotFinishRequest(FinishRequestRequest finishRequestRequest) throws MentorNotFoundException, RequestNotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findRequestByIdAndUserIdAndStatus(finishRequestRequest.getRequestId(), userId, Status.DOING);
		if (Objects.isNull(request))
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + finishRequestRequest.getRequestId());

		// Get by request id and pending status
		MentorRequest mentorRequest = mentorRequestRepository.findMentorRequestByRequestIdAndStatus(
				finishRequestRequest.getRequestId(),
				Status.DOING
		);
		if (Objects.isNull(mentorRequest)) {
			throw new MentorNotFoundException("Mentor request is deleted by mentor or user creator");
		}

		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(mentorRequest.getMentor().getId(), Status.ACTIVE);

		if (Objects.isNull(mentor))
			throw new MentorNotFoundException("Mentor might be inactive or deleted");

		if (!Strings.isNullOrEmpty(finishRequestRequest.getComment()) && finishRequestRequest.getRating() != 0) {
			//Optional insert rating and comment
			MentorMapper.setRating(mentor, finishRequestRequest.getRating());
			mentor.setAverageRating(MentorMapper.calculateAverageRating(mentor));
			mentorRepository.saveAndFlush(mentor);
			User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
			commentRepository.saveAndFlush(
					CommentMapper.createCommentEntity(user, mentor, finishRequestRequest.getRating(), finishRequestRequest.getComment())
			);
		}

		//calculate time doing
		Duration duration = Duration.between(request.getStartDoingTime(), LocalDateTime.now());
		if (duration.toMillis() <= Constants.CANCELLATION_MILLIS) {
			request.setStatus(Status.OPEN);
			requestRepository.saveAndFlush(request);
			confirmNotFinishOperations(request, mentor);
			//Push notification
			FirebaseUtils.pushNotification(NotificationBuilder.createCancellation(request.getId()), mentor.getUser().getUsername());
		} else {
			//Create announcement inform to mentor
			RequestAnnouncement requestAnnouncement = RequestMapper.createAnnouncement(request, mentor, Status.REJECT);
			requestAnnouncementRepository.saveAndFlush(requestAnnouncement);
			//Push notification
			FirebaseUtils.pushNotification(NotificationBuilder.createIncompleteRequest(request.getId()), mentor.getUser().getUsername());
		}
	}

	private void removePendingMentors(Long requestId) {
		//remove all other mentor request
		mentorRequestRepository.deleteAllByRequestIdAndStatus(requestId, Status.PENDING);
		//delete all notification pushed
		requestAnnouncementRepository.deleteAllByRequestIdAndStatus(requestId, Status.INVITE);
	}

	@Override
	@AnestTransactional
	public void resolveConflict(Long requestId, boolean forUser) throws
			RequestNotFoundException,
			MentorRequestNotFoundException,
			RequestAnnouncementNotFoundException,
			MentorNotFoundException,
			MessagingException,
			IOException,
			TemplateException, URISyntaxException {

		Request request = requestRepository.findByIdAndStatus(requestId, Status.DOING);
		if (Objects.isNull(request)) throw new RequestNotFoundException("not found request");

		RequestAnnouncement announcement = requestAnnouncementRepository.findByRequestIdAndStatus(requestId, Status.CONFLICT);
		if (Objects.isNull(announcement))
			throw new RequestAnnouncementNotFoundException("Not found any reject request from user");
		Mentor mentor = announcement.getMentor();
		if (forUser) {
			mentorConfirmNotFinishRequest(request, mentor.getId());
		} else {
			confirmFinishRequest(request.getUser().getId(), requestId, 0, null);
		}

		FirebaseUtils.pushNotification(NotificationBuilder.createResolveConflict(requestId), request.getUser().getUsername());
		FirebaseUtils.pushNotification(NotificationBuilder.createResolveConflict(requestId), mentor.getUser().getUsername());
	}

	@Override
	@AnestTransactional
	public void removeExpiredRejectRequest(Long requestId) throws MentorNotFoundException, RequestAnnouncementNotFoundException {
		RequestAnnouncement announcement = requestAnnouncementRepository.findByRequestIdAndStatus(requestId, Status.REJECT);
		if (Objects.isNull(announcement))
			throw new RequestAnnouncementNotFoundException("Not found any reject request from user");
		Request request = announcement.getRequest();
		mentorConfirmNotFinishRequest(request, announcement.getMentor().getId());
	}

	@Override
	@AnestTransactional
	public void mentorConfirmNotFinishRequest(Long requestId) throws RequestAnnouncementNotFoundException, MentorNotFoundException {
		Long userId = jwtUtils.getUserId();
		RequestAnnouncement announcement = requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(requestId, userId, Status.REJECT);
		if (Objects.isNull(announcement)) {
			throw new RequestAnnouncementNotFoundException("Not found any reject request from user");
		}
		mentorConfirmNotFinishRequest(announcement.getRequest(), userId);
	}

	private void mentorConfirmNotFinishRequest(Request request, Long mentorId) throws MentorNotFoundException {

		request.setStatus(Status.DELETE);
		requestRepository.saveAndFlush(request);

		Mentor mentor = mentorRepository.findByUserIdAndStatus(mentorId, Status.ACTIVE);

		if (Objects.isNull(mentor))
			throw new MentorNotFoundException("Mentor might be inactive or deleted");

		confirmNotFinishOperations(request, mentor);

		FirebaseUtils.pushNotification(NotificationBuilder.createConfirmation(request.getId(), mentor.getUser().getUserDetail().getFullName()), request.getUser().getUsername());
	}

	private void confirmNotFinishOperations(Request request, Mentor mentor) {
		mentorRequestRepository.deleteByRequestIdAndMentorId(request.getId(), mentor.getId());
		requestAnnouncementRepository.deleteByRequestIdAndMentorId(request.getId(), mentor.getId());

		mentor.setTotalRequestDeny(mentor.getTotalRequestDeny() + 1);
		mentorRepository.saveAndFlush(mentor);
		//record mentor rejection
		MentorRejection mentorRejection = new MentorRejection();
		mentorRejection.setMentor(mentor);
		mentorRejection.setRequest(request);
		mentorRejectionRepository.saveAndFlush(mentorRejection);

		UserDetail userDetail = userDetailRepository.findUserDetailByUserId(request.getUser().getId());
		userDetail.setTotalBudgetCurrent(userDetail.getTotalBudgetCurrent() + request.getPrice());
		userDetailRepository.saveAndFlush(userDetail);

		//delete subscribed channel
		String username = request.getUser().getUsername();
		User mentorUser = mentor.getUser();
		FirebaseUtils.deleteSubscribedChannel(request.getId(), username, mentorUser.getUsername());
	}

	@Override
	@AnestTransactional
	public void mentorConfirmFinishRequest(Long requestId) throws MessagingException, IOException, TemplateException, URISyntaxException {
		Long userId = jwtUtils.getUserId();
		RequestAnnouncement requestAnnouncement = requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(requestId, userId, Status.REJECT);
		requestAnnouncement.setStatus(Status.CONFLICT);
		requestAnnouncementRepository.saveAndFlush(requestAnnouncement);

		Request request = requestAnnouncement.getRequest();
		User user = request.getUser();
		Mentor mentor = requestAnnouncement.getMentor();

		//Push notification
		FirebaseUtils.pushNotification(NotificationBuilder.createConflict(requestId, user.getUserDetail().getFullName()), user.getUsername());

		//Send mail mentor cc user
		Map<String, Object> params = new HashMap<>();
		User mentorUser = mentor.getUser();
		params.put("mentorFullName", mentorUser.getUserDetail().getFullName());
		params.put("amount", request.getPrice());
		params.put("userFullName", user.getUserDetail().getFullName());
		params.put("requestId", String.valueOf(requestId));
		String baseURL = commonProperties.getFrontendURL();

		String requestURL = new URIBuilder(new URI(baseURL))
				.setPath(Constants.REQUEST_PATH + "/" + requestId)
				.toString();

		params.put("requestURL", requestURL);

		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(mentorUser.getEmail(), user.getEmail()),
				params,
				MailRequest.TEMPLATE_REQUEST_CONFLICT,
				MailRequest.TITLE_REQUEST_CONFLICT
		);
		mailRequest.setCc(FormatUtils.makeArray(MailRequest.CC, user.getEmail()));
		emailSenderService.sendEmailTemplate(mailRequest);
	}
}
