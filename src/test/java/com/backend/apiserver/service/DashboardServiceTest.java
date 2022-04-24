package com.backend.apiserver.service;

import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.MoneyOutHistoryRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.BankCardServiceImpl;
import com.backend.apiserver.service.impl.DashboardServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private RequestRepository requestRepository;
	@Mock
	private MoneyInHistoryRepository moneyInHistoryRepository;
	@Mock
	private MoneyOutHistoryRepository moneyOutHistoryRepository;
	@Mock
	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;
	@Mock
	private MentorRequestRepository mentorRequestRepository;
	@Mock
	private UserDetailRepository userDetailRepository;

	@InjectMocks
	DashboardService dashboardService = new DashboardServiceImpl(
			userRepository,
			mentorRepository,
			requestRepository,
			moneyInHistoryRepository,
			moneyOutHistoryRepository,
			moneyExchangeHistoryRepository,
			mentorRequestRepository,
			userDetailRepository
	);

	@Test
	public void getDashboardInfo() {
		when(mentorRepository.countMentorByStatus(Status.ACTIVE)).thenReturn(10);
		when(userRepository.countUserByStatus(Status.ACTIVE)).thenReturn(10);
		when(requestRepository.countAllRequests()).thenReturn(10);
		when(requestRepository.countRequestsByStatus(Status.COMPLETE)).thenReturn(10);
		when(moneyInHistoryRepository.countAllMoneyInHistories()).thenReturn(10);
		when(moneyOutHistoryRepository.countAllMoneyOutHistories()).thenReturn(10);
		when(moneyExchangeHistoryRepository.countAllMoneyExchangeHistories()).thenReturn(10);
		when(requestRepository.countRequestsByStartDateAndEndDate(any(), any())).thenReturn(10);
		when(mentorRequestRepository.countMentorRequestByStartDateAndEndDate(any(), any(), any())).thenReturn(10);
		when(moneyInHistoryRepository.countMoneyInByStartDateAndEndDate(any(), any())).thenReturn(10);
		when(moneyOutHistoryRepository.countMoneyOutByStartDateAndEndDate(any(), any())).thenReturn(10);
		when(moneyExchangeHistoryRepository.countMoneyExchangeByStartDateAndEndDate(any(), any())).thenReturn(10);
		when(mentorRepository.findTop5ExcellentMentor()).thenReturn(Arrays.asList(generateMentor()));
		when(userDetailRepository.findByUserId(any())).thenReturn(generateUserDetail());
		when(requestRepository.countRequestsByStatus(Status.OPEN)).thenReturn(10);
		when(requestRepository.countRequestsByStatus(Status.DOING)).thenReturn(10);
		when(requestRepository.countRequestsByStatus(Status.COMPLETE)).thenReturn(10);
		when(requestRepository.countRequestsByStatus(Status.DELETE)).thenReturn(10);
		dashboardService.getDashboardInfo();
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
		return mentor;
	}
}