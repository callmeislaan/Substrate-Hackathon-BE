package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.mapper.NotificationBuilder;
import com.backend.apiserver.mapper.RequestMapper;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestFollowingRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.service.RequestInvitationService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.FirebaseUtils;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestInvitationServiceImpl implements RequestInvitationService {

	private JwtUtils jwtUtils;
	private RequestRepository requestRepository;
	private MentorRepository mentorRepository;
	private RequestAnnouncementRepository requestAnnouncementRepository;
	private RequestFollowingRepository requestFollowingRepository;
	private MentorRequestRepository mentorRequestRepository;
	private UserDetailRepository userDetailRepository;

	@Override
	@AnestTransactional
	public void inviteMentor(RentMentorRequest rentMentorRequest) throws RequestNotFoundException, MentorNotFoundException {
		Long userId = jwtUtils.getUserId();
		Long requestId = rentMentorRequest.getRequestId();
		Request request = requestRepository.findByIdAndUserIdAndStatus(requestId, userId, Status.OPEN);
		if (Objects.isNull(request))
			throw new RequestNotFoundException("Request is accepted by another mentor, or is deleted: " + requestId);
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(rentMentorRequest.getMentorId(), Status.ACTIVE);
		if (Objects.isNull(mentor))
			throw new MentorNotFoundException("Mentor might be inactive or deleted: " + rentMentorRequest.getMentorId());
		RequestAnnouncement requestAnnouncement = requestAnnouncementRepository.
				findByRequestIdAndMentorIdAndStatus(rentMentorRequest.getRequestId(), rentMentorRequest.getMentorId(), Status.INVITE);
		if (Objects.isNull(requestAnnouncement)) {
			RequestAnnouncement newAnnouncement = RequestMapper.createAnnouncement(request, mentor, Status.INVITE);
			requestAnnouncementRepository.saveAndFlush(newAnnouncement);
		}
		//Push notification
		UserDetail userDetail = userDetailRepository.findByUserId(userId);
		String userFullName = userDetail.getFullName();
		String mentorUsername = mentor.getUser().getUsername();
		FirebaseUtils.pushNotification(NotificationBuilder.createInvitation(requestId, userFullName), mentorUsername);
	}

	@Override
	public PagingWrapperResponse findInvitationRequests(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		List<RequestAnnouncement> requestAnnouncements = requestAnnouncementRepository.findAllByMentorIdAndStatus(userId, Status.INVITE);
		if (requestAnnouncements.isEmpty()) return new PagingWrapperResponse(Collections.emptyList(), 0);
		List<Long> requestIds = requestAnnouncements
				.stream()
				.map(requestAnnouncement -> requestAnnouncement.getRequest().getId())
				.collect(Collectors.toList());
		Page<Request> requests = requestRepository.findAllByIdInAndStatus(requestIds, Status.OPEN, PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		return new PagingWrapperResponse(requests.stream().map(request -> RequestMapper.requestEntityToResponse(request, userId, requestFollowingRepository, mentorRequestRepository)).collect(Collectors.toList()), requests.getTotalElements());
	}

	@Override
	@AnestTransactional
	public void deleteInvitation(Long requestId) throws NotFoundException {
		Long userId = jwtUtils.getUserId();
		RequestAnnouncement requestAnnouncement = requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(requestId, userId, Status.INVITE);
		if (Objects.isNull(requestAnnouncement))
			throw new NotFoundException("Not found any invitation related to this request id and user id");
		requestAnnouncementRepository.delete(requestAnnouncement);
	}
}
