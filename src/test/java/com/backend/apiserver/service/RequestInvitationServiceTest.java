package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestFollowingRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.service.impl.RequestInvitationServiceImpl;
import com.backend.apiserver.service.impl.RequestReservationServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestInvitationServiceTest {
	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private RequestRepository requestRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private RequestAnnouncementRepository requestAnnouncementRepository;
	@Mock
	private RequestFollowingRepository requestFollowingRepository;
	@Mock
	private MentorRequestRepository mentorRequestRepository;
	@Mock
	private UserDetailRepository userDetailRepository;

	@Before
	public void setUp() throws Exception {
	}

	@InjectMocks
	RequestInvitationService requestInvitationService = new RequestInvitationServiceImpl(
			jwtUtils,
			requestRepository,
			mentorRepository,
			requestAnnouncementRepository,
			requestFollowingRepository,
			mentorRequestRepository,
			userDetailRepository
	);

	@Test
	public void inviteMentor() throws MentorNotFoundException, RequestNotFoundException {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(1L);
		rentMentorRequest.setMentorId(1L);
		Request request = new Request();
		request.setId(1L);

		UserDetail userDetail = new UserDetail();
		userDetail.setFullName("kafka tamura");

		User user = new User();
		user.setUsername("kafka");
		user.setUserDetail(userDetail);

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(request);
		when(mentorRepository.findMentorByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);
		when(userDetailRepository.findByUserId(1L)).thenReturn(userDetail);
		when(requestAnnouncementRepository.
				findByRequestIdAndMentorIdAndStatus(1L, 1L, Status.INVITE)).thenReturn(null);
		requestInvitationService.inviteMentor(rentMentorRequest);
	}

	@Test
	public void findInvitationRequests() {
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL,15,12,00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL,15,12,00));

		User user = new User();
		user.setUsername("");
		user.setEmail("");
		user.setId(1L);
		user.setCreatedDate(LocalDateTime.of(2018, Month.APRIL,15,12,00));
		user.setUserDetail(userDetail);

		Request request = new Request();
		request.setId(1L);
		request.setDeadline(LocalDateTime.of(2018, Month.APRIL,15,12,00));
		request.setCreatedDate(LocalDateTime.of(2018, Month.APRIL,15,12,00));
		request.setTitle("LocalDateTime.MIN");
		request.setContent("LocalDateTime.MIN");
		request.setStatus(Status.DELETE);
		request.setSkills(new HashSet<>());
		request.setUser(user);

		Page page = new PageImpl(Arrays.asList(request));

		RequestAnnouncement announcement = new RequestAnnouncement();
		announcement.setRequest(request);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestAnnouncementRepository.findAllByMentorIdAndStatus(1L, Status.INVITE)).thenReturn(Arrays.asList(announcement));
		when(requestRepository.findAllByIdInAndStatus(anyList(), any(), any())).thenReturn(page);
		requestInvitationService.findInvitationRequests(1,10);

	}

	@Test
	public void deleteInvitation() throws NotFoundException {
		RequestAnnouncement requestAnnouncement = new RequestAnnouncement();
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(requestAnnouncement);
		requestInvitationService.deleteInvitation(1L);
		verify(requestAnnouncementRepository, times(1)).delete(requestAnnouncement);
	}

	@Test(expected = NotFoundException.class)
	public void deleteInvitation_ThrowNotFoundException() throws NotFoundException {
		RequestAnnouncement requestAnnouncement = new RequestAnnouncement();
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(null);
		requestInvitationService.deleteInvitation(1L);
		verify(requestAnnouncementRepository, times(0)).delete(requestAnnouncement);
	}
}