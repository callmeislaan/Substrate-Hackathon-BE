package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.service.impl.RequestReservationServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestReservationServiceTest {
	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private RequestRepository requestRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private MentorRequestRepository mentorRequestRepository;
	@Mock
	private RequestAnnouncementRepository requestAnnouncementRepository;
	@Mock
	private UserDetailRepository userDetailRepository;
	@Mock
	private SkillRepository skillRepository;

	@InjectMocks
	RequestReservationService requestReservationService = new RequestReservationServiceImpl(
			jwtUtils,
			requestRepository,
			mentorRepository,
			mentorRequestRepository,
			requestAnnouncementRepository,
			userDetailRepository,
			skillRepository
	);

	@Test
	public void reserveRequest() throws Exception {
		User user = new User();
		user.setUsername("Dung");
		Request request = new Request();
		request.setUser(user);
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.existsByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(false);
		when(requestRepository.findByIdAndStatus(1L, Status.OPEN)).thenReturn(request);
		when(mentorRepository.findMentorByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(new Mentor());
		when(userDetailRepository.findByUserId(1L)).thenReturn(new UserDetail());
		requestReservationService.reserveRequest(1L);
		verify(mentorRequestRepository, times(1)).saveAndFlush(any());
	}

	@Test
	public void undoReserveRequest() throws MentorRequestNotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(1L, 1L, Status.PENDING)).thenReturn(new MentorRequest());
		requestReservationService.undoReserveRequest(1L);
		verify(mentorRequestRepository, times(1)).delete(any());
	}

	@Test
	public void findPendingMentors() throws RequestNotFoundException {
		UserDetail userDetail = new UserDetail();
		userDetail.setGender(false);
		userDetail.setFullName("Kafka Tamura");
		userDetail.setAvatar("avatar");
		User user = new User();
		user.setUsername("kafka2405");
		user.setId(1L);
		user.setUserDetail(userDetail);
		Mentor mentorr = new Mentor();
		mentorr.setJob("job");
		mentorr.setUser(user);
		Skill skill = new Skill();
		skill.setId(1L);
		skill.setName("JAVA");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(new Request());
		requestReservationService.findPendingMentors(1L);
	}

	@Test(expected = NotFoundException.class)
	public void findPendingMentors_ThrowNotFoundException() throws RequestNotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(null);
		requestReservationService.findPendingMentors(1L);
	}

	@Test
	public void rentMentor() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setMentorId(1L);
		rentMentorRequest.setRequestId(1L);

		User user = new User();
		user.setUsername("kafka2405");

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		Request request = new Request();
		request.setId(1L);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(jwtUtils.getUsername()).thenReturn("kafka2405");
		when(requestRepository.findRequestByIdAndUserIdAndStatus(rentMentorRequest.getRequestId(), 1L, Status.OPEN)).thenReturn(request);
		when(mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(
				1L,
				1L,
				Status.PENDING
		)).thenReturn(new MentorRequest());
		when(mentorRepository.findByUserIdAndStatus(rentMentorRequest.getMentorId(), Status.ACTIVE)).thenReturn(mentor);
		when(userDetailRepository.findUserDetailByUserId(1L)).thenReturn(new UserDetail());

		requestReservationService.rentMentor(rentMentorRequest);

	}

	@Test
	public void deleteRequestReservation() throws MentorRequestNotFoundException, RequestNotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		Request request = new Request();
		request.setId(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(request);
		when(mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(request.getId(), 1L, Status.PENDING)).thenReturn(new MentorRequest());
		requestReservationService.deleteRequestReservation(1L, 1L);
	}

	@Test(expected = RequestNotFoundException.class)
	public void deleteRequestReservation_ThrowRequestNotFoundException() throws MentorRequestNotFoundException, RequestNotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		Request request = new Request();
		request.setId(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(null);
		requestReservationService.deleteRequestReservation(1L, 1L);
	}

	@Test(expected = MentorRequestNotFoundException.class)
	public void deleteRequestReservation_ThrowMentorRequestNotFoundException() throws MentorRequestNotFoundException, RequestNotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		Request request = new Request();
		request.setId(1L);
		when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(request);
		when(mentorRequestRepository.findByRequestIdAndMentorIdAndStatus(request.getId(), 1L, Status.PENDING)).thenReturn(null);
		requestReservationService.deleteRequestReservation(1L, 1L);
	}
}