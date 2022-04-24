package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.HireAnestMentorRequest;
import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.MentorHireRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.MentorHiringServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MentorHiringServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private MentorHireRepository mentorHireRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private EmailSenderService emailSenderService;

	@InjectMocks
	MentorHiringService mentorHiringService = new MentorHiringServiceImpl(
			jwtUtils,
			mentorHireRepository,
			userRepository,
			mentorRepository,
			emailSenderService
	);

	@Test
	public void requestHireAnestMentor() throws TemplateException, ForbiddenException, IOException, InvalidDataException, NotFoundException, MessagingException {
		HireAnestMentorRequest hireAnestMentorRequest = new HireAnestMentorRequest();
		hireAnestMentorRequest.setMentorId(2L);
		hireAnestMentorRequest.setNote("note");
		hireAnestMentorRequest.setTitle("title");

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		mentorHiringService.requestHireAnestMentor(hireAnestMentorRequest);
	}

	@Test(expected = ForbiddenException.class)
	public void requestHireAnestMentor_ForbiddenException() throws TemplateException, ForbiddenException, IOException, InvalidDataException, NotFoundException, MessagingException {
		HireAnestMentorRequest hireAnestMentorRequest = new HireAnestMentorRequest();
		hireAnestMentorRequest.setMentorId(1L);
		hireAnestMentorRequest.setNote("note");
		hireAnestMentorRequest.setTitle("title");

		when(jwtUtils.getUserId()).thenReturn(1L);
		mentorHiringService.requestHireAnestMentor(hireAnestMentorRequest);
	}

	@Test(expected = NotFoundException.class)
	public void requestHireAnestMentor_NotFoundException() throws TemplateException, ForbiddenException, IOException, InvalidDataException, NotFoundException, MessagingException {
		HireAnestMentorRequest hireAnestMentorRequest = new HireAnestMentorRequest();
		hireAnestMentorRequest.setMentorId(2L);
		hireAnestMentorRequest.setNote("note");
		hireAnestMentorRequest.setTitle("title");

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(null);
		mentorHiringService.requestHireAnestMentor(hireAnestMentorRequest);
	}

	@Test(expected = InvalidDataException.class)
	public void requestHireAnestMentor_InvalidDataException() throws TemplateException, ForbiddenException, IOException, InvalidDataException, NotFoundException, MessagingException {
		HireAnestMentorRequest hireAnestMentorRequest = new HireAnestMentorRequest();
		hireAnestMentorRequest.setMentorId(2L);
		hireAnestMentorRequest.setNote("note");
		hireAnestMentorRequest.setTitle("title");

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		Mentor mentor = generateMentor();
		mentor.setAnestMentor(false);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(mentor);
		mentorHiringService.requestHireAnestMentor(hireAnestMentorRequest);
	}

	private UserDetail generateUserDetail(){
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		return userDetail;
	}

	private User generateUser(){
		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(generateUserDetail());
		return user;
	}

	private Mentor generateMentor(){
		Mentor mentor = new Mentor();
		mentor.setId(1L);
		mentor.setJob("java");
		mentor.setAchievements(new HashSet<>(Arrays.asList(new Achievement())));
		Skill skill = new Skill();
		skill.setName("JAVA");
		skill.setId(1L);
		skill.setStatus(Status.ACTIVE);
		MentorSkill mentorSkill = new MentorSkill();
		mentorSkill.setSkill(skill);
		mentorSkill.setMentor(mentor);
		List<MentorSkill> mentorSkillList = new ArrayList<>();
		mentorSkillList.add(mentorSkill);
		mentor.setMentorSkills(mentorSkillList);
		mentor.setCreatedDate(LocalDateTime.now());
		mentor.setUser(generateUser());
		mentor.setAnestMentor(true);
		return mentor;
	}
}