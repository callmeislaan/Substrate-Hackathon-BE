package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestFollowingRepository;
import com.backend.apiserver.utils.DateTimeUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestMapper {
	public static RequestResponse requestEntityToResponse(
			Request request,
			Long userId,
			RequestFollowingRepository requestFollowingRepository,
			MentorRequestRepository mentorRequestRepository
	) {
		RequestResponse requestResponse = new RequestResponse();
		requestResponse.setId(request.getId());
		requestResponse.setTitle(request.getTitle());
		requestResponse.setContent(request.getContent());
		if (request.getStatus() == Status.DOING) {
			requestResponse.setStartDoingTime(DateTimeUtils.toCurrentTimeMillis(request.getStartDoingTime()));
		}
		requestResponse.setDeadline(DateTimeUtils.toCurrentTimeMillis(request.getDeadline()));
		requestResponse.setStatus(request.getStatus().toString());
		requestResponse.setPrice(request.getPrice());
		requestResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(request.getCreatedDate()));
		if (Objects.nonNull(userId)) {
			if (requestFollowingRepository.existsByMentorIdAndRequestIdAndStatus(userId, request.getId(), Status.ACTIVE))
				requestResponse.setBookmarked(true);
			if (mentorRequestRepository.existsByRequestIdAndMentorIdAndStatus(request.getId(), userId, Status.PENDING))
				requestResponse.setReserved(true);
		}
		requestResponse.setSkills(
				request.getSkills().stream()
						.map(skill -> {
							SkillResponse skillResponse = new SkillResponse();
							skillResponse.setId(skill.getId());
							skillResponse.setName(skill.getName());
							return skillResponse;
						})
						.collect(Collectors.toList())
		);
		requestResponse.setUserInfoResponse(UserMapper.userEntityToResponse(request.getUser()));
		if (request.getStatus() == Status.DOING || request.getStatus() == Status.COMPLETE) {
			List<MentorRequest> filteredList = request.getMentorRequests().stream()
					.filter(mentorRequest -> mentorRequest.getStatus() == Status.DOING || mentorRequest.getStatus() == Status.COMPLETE)
					.collect(Collectors.toList());
			User mentorInfo = filteredList.get(0).getMentor().getUser();
			requestResponse.setMentorInfoResponse(UserMapper.userEntityToResponse(mentorInfo));
		}
		return requestResponse;
	}

	public static RequestAnnouncement createAnnouncement(Request request, Mentor mentor, Status typeOfStatus) {
		RequestAnnouncement requestAnnouncement = new RequestAnnouncement();
		requestAnnouncement.setRequest(request);
		requestAnnouncement.setMentor(mentor);
		requestAnnouncement.setStatus(typeOfStatus);
		return requestAnnouncement;
	}
}
