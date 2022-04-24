package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.mapper.MentorMapper;
import com.backend.apiserver.mapper.NotificationBuilder;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.service.RequestReservationService;
import com.backend.apiserver.utils.FirebaseUtils;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestReservationServiceImpl implements RequestReservationService {

	private JwtUtils jwtUtils;
	private RequestRepository requestRepository;
	private MentorRepository mentorRepository;
	private MentorRequestRepository mentorRequestRepository;
	private RequestAnnouncementRepository requestAnnouncementRepository;
	private UserDetailRepository userDetailRepository;
	private SkillRepository skillRepository;

	@Override
	@AnestTransactional
	public void reserveRequest(Long requestId) throws RequestNotFoundException, DataDuplicatedException, MentorNotFoundException {
		Long userId = jwtUtils.getUserId();
		if (requestRepository.existsByIdAndUserIdAndStatus(requestId, userId, Status.OPEN))
			throw new DataDuplicatedException("You can't take your own request: " + requestId);

		Request request = requestRepository.findByIdAndStatus(requestId, Status.OPEN);
		if (Objects.isNull(request))
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + requestId);

		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(mentor))
			throw new MentorNotFoundException("Mentor not found with user id: " + userId);

		MentorRequest mentorRequest = mentorRequestRepository.findByRequestIdAndMentorId(requestId, userId);

		if (Objects.isNull(mentorRequest)) {
			mentorRequest = new MentorRequest();
			mentorRequest.setRequest(request);
			mentorRequest.setMentor(mentor);
			mentorRequest.setStatus(Status.PENDING);
			mentorRequestRepository.saveAndFlush(mentorRequest);

			//Push notification
			UserDetail userDetail = userDetailRepository.findByUserId(userId);
			FirebaseUtils.pushNotification(NotificationBuilder.createReserveRequest(requestId, userDetail.getFullName()), request.getUser().getUsername());
		}
	}

	@Override
	@AnestTransactional
	public void undoReserveRequest(Long requestId) throws MentorRequestNotFoundException {
		Long userId = jwtUtils.getUserId();
		MentorRequest mentorRequest = mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(requestId, userId, Status.PENDING);
		if (Objects.isNull(mentorRequest))
			throw new MentorRequestNotFoundException("Not found mentor request with mentorId: " + userId + "requestId: " + requestId);
		mentorRequestRepository.delete(mentorRequest);
	}

	@Override
	public WrapperResponse findPendingMentors(Long requestId) throws RequestNotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findByIdAndUserIdAndStatus(requestId, userId, Status.OPEN);
		if (Objects.isNull(request))
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + requestId);

		List<Long> pendingMentorIds = mentorRequestRepository
				.findAllByRequestIdAndStatus(request.getId(), Status.PENDING)
				.stream()
				.map(mentorRequest -> mentorRequest.getMentor().getId())
				.collect(Collectors.toList());

		if (pendingMentorIds.isEmpty()) return new WrapperResponse(Collections.EMPTY_LIST);
		List<MentorShortDescriptionResponse> mentorShortDescriptionResponses = pendingMentorIds
				.stream()
				.map(pendingMentorId -> mentorRepository.findByUserIdAndStatus(pendingMentorId, Status.ACTIVE))
				.filter(Objects::nonNull)
				.map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString())))
				.collect(Collectors.toList());
		return new WrapperResponse(mentorShortDescriptionResponses);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void rentMentor(RentMentorRequest rentMentorRequest) throws MentorRequestNotFoundException, RequestNotFoundException, MoneyRelatedException, MentorNotFoundException {
		Long userId = jwtUtils.getUserId();
		String username = jwtUtils.getUsername();

		//update request status to doing
		Request request = requestRepository.findRequestByIdAndUserIdAndStatus(rentMentorRequest.getRequestId(), userId, Status.OPEN);
		if (Objects.isNull(request)) {
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + rentMentorRequest.getRequestId());
		}
		request.setStartDoingTime(LocalDateTime.now());
		request.setStatus(Status.DOING);
		requestRepository.saveAndFlush(request);

		// Get by request id, mentor id and pending status
		MentorRequest mentorRequest = mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(
				rentMentorRequest.getRequestId(),
				rentMentorRequest.getMentorId(),
				Status.PENDING
		);
		//when mentor delete request
		if (Objects.isNull(mentorRequest)) {
			throw new MentorRequestNotFoundException("Mentor request is deleted by mentor or user creator");
		}
		//update room chat and status
		mentorRequest.setStatus(Status.DOING);
		//save to db
		mentorRequestRepository.saveAndFlush(mentorRequest);

		//plus request receive for mentor
		Mentor mentor = mentorRepository.findByUserIdAndStatus(rentMentorRequest.getMentorId(), Status.ACTIVE);
		if (Objects.isNull(mentor))
			throw new MentorNotFoundException("Mentor not found with mentor id: " + rentMentorRequest.getMentorId());

		mentor.setTotalRequestReceive(mentor.getTotalRequestReceive() + 1);
		mentorRepository.saveAndFlush(mentor);

		//delete all notification pushed
		requestAnnouncementRepository.deleteAllByRequestIdAndStatus(rentMentorRequest.getRequestId(), Status.INVITE);

		//subtract budget money of user detail
		UserDetail userDetail = userDetailRepository.findUserDetailByUserId(userId);
		//total money user have
		int totalBudgetCurrent = userDetail.getTotalBudgetCurrent();
		//check if user have enough money for request price
		if (request.getPrice() > totalBudgetCurrent) {
			throw new MoneyRelatedException("You don't have enough money to create this request: " + totalBudgetCurrent);
		}
		// subtract total money of user when user create request
		userDetail.setTotalBudgetCurrent(totalBudgetCurrent - request.getPrice());
		//lock table and update money for user
		userDetailRepository.saveAndFlush(userDetail);

		//create subscribed channel
		String mentorUsername = mentor.getUser().getUsername();
		FirebaseUtils.createSubscribedChannel(rentMentorRequest.getRequestId(), username, mentorUsername);

		//Push notification
		FirebaseUtils.pushNotification(NotificationBuilder.createAcceptRequest(rentMentorRequest.getRequestId()), mentorUsername);
	}

	@Override
	@AnestTransactional
	public void deleteRequestReservation(Long requestId, Long mentorId) throws MentorRequestNotFoundException, RequestNotFoundException {
		Long userId = jwtUtils.getUserId();
		Request request = requestRepository.findByIdAndUserIdAndStatus(requestId, userId, Status.OPEN);
		if (Objects.isNull(request)) {
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + requestId);
		}
		MentorRequest mentorRequest = mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(request.getId(), mentorId, Status.PENDING);
		if (Objects.isNull(mentorRequest)) {
			throw new MentorRequestNotFoundException("Mentor request is deleted by mentor");
		}
		mentorRequestRepository.delete(mentorRequest);
	}
}
