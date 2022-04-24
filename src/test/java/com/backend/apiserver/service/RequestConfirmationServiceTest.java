package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.FinishRequestRequest;
import com.backend.apiserver.configuration.CommonProperties;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.repository.CommentRepository;
import com.backend.apiserver.repository.MentorRejectionRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.RequestConfirmationServiceImpl;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestConfirmationServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private RequestRepository requestRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private MentorRequestRepository mentorRequestRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private RequestAnnouncementRepository requestAnnouncementRepository;
	@Mock
	private MentorRejectionRepository mentorRejectionRepository;
	@Mock
	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;
	@Mock
	private UserDetailRepository userDetailRepository;
	@Mock
	private EmailSenderService emailSenderService;
	@Mock
	private CommonProperties commonProperties;

	@InjectMocks
	RequestConfirmationService requestConfirmationService = new RequestConfirmationServiceImpl(
			jwtUtils,
			requestRepository,
			mentorRepository,
			mentorRequestRepository,
			userRepository,
			commentRepository,
			requestAnnouncementRepository,
			mentorRejectionRepository,
			moneyExchangeHistoryRepository,
			userDetailRepository,
			emailSenderService,
			commonProperties
	);

	@Test
	public void confirmFinishRequest() throws Exception {
		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(userDetail);

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		Request request = new Request();
		request.setId(1L);
		request.setStartDoingTime(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		MentorRequest mentorRequest = new MentorRequest();
		mentorRequest.setRequest(request);
		mentorRequest.setMentor(mentor);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(request);
		when(mentorRequestRepository.findMentorRequestByRequestIdAndStatus(any(), any())).thenReturn(mentorRequest);
		when(userDetailRepository.findByUserId(any())).thenReturn(userDetail);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(user);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(mentor);
		when(commonProperties.getFrontendURL()).thenReturn("http://localhost:8080");

		requestConfirmationService.confirmFinishRequest(finishRequestRequest);
	}

	@Test(expected = RequestNotFoundException.class)
	public void confirmFinishRequest_RequestNotFoundException() throws Exception {
		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(null);
		requestConfirmationService.confirmFinishRequest(finishRequestRequest);
	}

	@Test(expected = MentorRequestNotFoundException.class)
	public void confirmFinishRequest_MentorRequestNotFoundException() throws Exception {
		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		Request request = new Request();
		request.setId(1L);
		request.setStartDoingTime(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		MentorRequest mentorRequest = new MentorRequest();
		mentorRequest.setRequest(request);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(request);
		when(mentorRequestRepository.findMentorRequestByRequestIdAndStatus(any(), any())).thenReturn(null);

		requestConfirmationService.confirmFinishRequest(finishRequestRequest);
	}


	@Test
	public void confirmNotFinishRequest() throws Exception {

		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(userDetail);

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		Request request = new Request();
		request.setId(1L);
		request.setStartDoingTime(LocalDateTime.now());
		request.setUser(user);

		MentorRequest mentorRequest = new MentorRequest();
		mentorRequest.setRequest(request);
		mentorRequest.setMentor(mentor);


		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(request);
		when(mentorRequestRepository.findMentorRequestByRequestIdAndStatus(any(), any())).thenReturn(mentorRequest);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(mentor);

		when(userDetailRepository.findUserDetailByUserId(any())).thenReturn(userDetail);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(user);

		requestConfirmationService.confirmNotFinishRequest(finishRequestRequest);
	}

	@Test(expected = RequestNotFoundException.class)
	public void confirmNotFinishRequest_RequestNotFoundException() throws Exception {

		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(null);

		requestConfirmationService.confirmNotFinishRequest(finishRequestRequest);
	}

	@Test(expected = MentorNotFoundException.class)
	public void confirmNotFinishRequest_MentorNotFoundException() throws Exception {

		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(userDetail);

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		Request request = new Request();
		request.setId(1L);
		request.setStartDoingTime(LocalDateTime.now());
		request.setUser(user);

		MentorRequest mentorRequest = new MentorRequest();
		mentorRequest.setRequest(request);
		mentorRequest.setMentor(mentor);

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(request);
		when(mentorRequestRepository.findMentorRequestByRequestIdAndStatus(any(), any())).thenReturn(mentorRequest);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(null);

		requestConfirmationService.confirmNotFinishRequest(finishRequestRequest);
	}

	@Test
	public void mentorConfirmNotFinishRequest() throws Exception {
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		User user = new User();
		user.setUsername("");
		user.setEmail("");
		user.setId(1L);
		user.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		user.setUserDetail(userDetail);
		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);
		Request request = new Request();
		request.setId(1L);
		request.setDeadline(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setTitle("LocalDateTime.MIN");
		request.setContent("LocalDateTime.MIN");
		request.setStatus(Status.DELETE);
		request.setSkills(new HashSet<>());
		request.setUser(user);
		RequestAnnouncement announcement = new RequestAnnouncement();
		announcement.setRequest(request);
		announcement.setMentor(mentor);
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(announcement);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(mentor);
		when(userDetailRepository.findUserDetailByUserId(any())).thenReturn(userDetail);
		requestConfirmationService.mentorConfirmNotFinishRequest(1L);
	}

	@Test
	public void mentorConfirmFinishRequest() throws Exception {
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		User user = new User();
		user.setUsername("");
		user.setEmail("");
		user.setId(1L);
		user.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		user.setUserDetail(userDetail);
		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);
		Request request = new Request();
		request.setId(1L);
		request.setDeadline(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setTitle("LocalDateTime.MIN");
		request.setContent("LocalDateTime.MIN");
		request.setStatus(Status.DELETE);
		request.setSkills(new HashSet<>());
		request.setUser(user);
		RequestAnnouncement announcement = new RequestAnnouncement();
		announcement.setRequest(request);
		announcement.setMentor(mentor);
		when(requestAnnouncementRepository.findByRequestIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(announcement);
		when(commonProperties.getFrontendURL()).thenReturn("http://localhost:8080");
		requestConfirmationService.mentorConfirmFinishRequest(1L);
	}

	@Test
	public void resolveConflictForUser() throws Exception {
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		User user = new User();
		user.setUsername("");
		user.setEmail("");
		user.setId(1L);
		user.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		user.setUserDetail(userDetail);
		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);
		Request request = new Request();
		request.setId(1L);
		request.setDeadline(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setCreatedDate(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setTitle("LocalDateTime.MIN");
		request.setContent("LocalDateTime.MIN");
		request.setStatus(Status.DELETE);
		request.setSkills(new HashSet<>());
		request.setUser(user);
		RequestAnnouncement announcement = new RequestAnnouncement();
		announcement.setRequest(request);
		announcement.setMentor(mentor);
		when(requestRepository.findByIdAndStatus(any(), any())).thenReturn(request);
		when(requestAnnouncementRepository.findByRequestIdAndStatus(any(), any())).thenReturn(announcement);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(mentor);
		when(userDetailRepository.findUserDetailByUserId(any())).thenReturn(userDetail);
		requestConfirmationService.resolveConflict(1L, true);
	}

	@Test
	public void resolveConflictForMentor() throws Exception {
		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setComment("as");
		finishRequestRequest.setRating(1);
		finishRequestRequest.setRequestId(1L);

		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));

		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(userDetail);

		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setUser(user);

		Request request = new Request();
		request.setId(1L);
		request.setStartDoingTime(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		request.setUser(user);

		MentorRequest mentorRequest = new MentorRequest();
		mentorRequest.setRequest(request);
		mentorRequest.setMentor(mentor);

		RequestAnnouncement announcement = new RequestAnnouncement();
		announcement.setRequest(request);
		announcement.setMentor(mentor);

		when(requestRepository.findByIdAndStatus(any(), any())).thenReturn(request);
		when(requestRepository.findRequestByIdAndUserIdAndStatus(any(), any(), any())).thenReturn(request);
		when(mentorRequestRepository.findMentorRequestByRequestIdAndStatus(any(), any())).thenReturn(mentorRequest);
		when(requestAnnouncementRepository.findByRequestIdAndStatus(any(), any())).thenReturn(announcement);
		when(userDetailRepository.findByUserId(any())).thenReturn(userDetail);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(user);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(mentor);
		when(commonProperties.getFrontendURL()).thenReturn("http://localhost:8080");

		requestConfirmationService.resolveConflict(1L, false);
	}
}