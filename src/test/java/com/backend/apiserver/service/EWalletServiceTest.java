package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.bean.response.EWalletResponse;
import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.service.impl.EWalletServiceImpl;
import com.backend.apiserver.service.impl.MentorHiringServiceImpl;
import com.backend.apiserver.utils.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
public class EWalletServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private EWalletRepository eWalletRepository;
	@Mock
	private MentorRepository mentorRepository;

	@InjectMocks
	EWalletService eWalletService = new EWalletServiceImpl(
			jwtUtils,
			eWalletRepository,
			mentorRepository
	);

	@Test
	public void getEWallets() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		eWalletService.getEWallets();
	}


	private EWallet generateEWallet() {
		EWallet eWallet = new EWallet();
		eWallet.setId(1L);
		eWallet.setHolderName("kafka");
		eWallet.setEWalletName("momo");
		eWallet.setPhone("0969563145");
		eWallet.setMentor(generateMentor());
		return eWallet;
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

	@Test
	public void createEWallet() {
		EWalletRequest request = new EWalletRequest();
		request.setPhone("0969563145");
		request.setEWalletName("kafka");
		request.setEWalletName("momo");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(eWalletRepository.findByMentorIdAndHolderNameAndPhoneAndStatus(any(), any(), any(), any())).thenReturn(generateEWallet());
		eWalletService.createEWallet(request);
	}

	@Test
	public void createNewEWallet() {
		EWalletRequest request = new EWalletRequest();
		request.setPhone("0969563145");
		request.setEWalletName("kafka");
		request.setEWalletName("momo");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(eWalletRepository.findByMentorIdAndHolderNameAndPhoneAndStatus(any(), any(), any(), any())).thenReturn(null);
		when(eWalletRepository.saveAndFlush(any())).thenReturn(generateEWallet());
		eWalletService.createEWallet(request);
	}

	@Test
	public void deleteEWallet() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(generateEWallet());
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		eWalletService.deleteEWallet(1L);
	}

	@Test(expected = NotFoundException.class)
	public void deleteEWallet_NotFoundException() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(null);
		eWalletService.deleteEWallet(1L);
	}

	@Test
	public void updateEWallet() throws NotFoundException {
		EWalletRequest request = new EWalletRequest();
		request.setPhone("0969563145");
		request.setEWalletName("kafka");
		request.setEWalletName("momo");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(generateEWallet());
		eWalletService.updateEWallet(1L, request);
	}

	@Test(expected = NotFoundException.class)
	public void updateEWallet_NotFoundException() throws NotFoundException {
		EWalletRequest request = new EWalletRequest();
		request.setPhone("0969563145");
		request.setEWalletName("kafka");
		request.setEWalletName("momo");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(null);
		eWalletService.updateEWallet(1L, request);
	}

	@Test
	public void findEWallet() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(),any())).thenReturn(generateEWallet());
		eWalletService.findEWallet(1L);
	}

	@Test(expected = NotFoundException.class)
	public void findEWallet_NotFoundException() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(eWalletRepository.findByIdAndMentorIdAndStatus(any(), any(),any())).thenReturn(null);
		eWalletService.findEWallet(1L);
	}
}