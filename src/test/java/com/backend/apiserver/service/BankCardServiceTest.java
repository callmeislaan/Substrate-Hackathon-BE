package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.BankCardRequest;
import com.backend.apiserver.bean.request.EWalletRequest;
import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.BankCard;
import com.backend.apiserver.entity.EWallet;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.BankCardRepository;
import com.backend.apiserver.repository.EWalletRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.service.impl.BankCardServiceImpl;
import com.backend.apiserver.service.impl.EWalletServiceImpl;
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
public class BankCardServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private BankCardRepository bankCardRepository;
	@Mock
	private MentorRepository mentorRepository;

	@InjectMocks
	BankCardService bankCardService = new BankCardServiceImpl(
			jwtUtils,
			bankCardRepository,
			mentorRepository
	);

	@Test
	public void getBankCards() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		bankCardService.getBankCards();
	}


	private BankCard generateBankCard() {
		BankCard bankCard = new BankCard();
		bankCard.setId(1L);
		bankCard.setHolderName("kafka");
		bankCard.setAccountNumber("02928683402");
		bankCard.setBank("TP");
		bankCard.setBranch("HN");
		bankCard.setMentor(generateMentor());
		return bankCard;
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
	public void createBankCard() {
		BankCardRequest request = new BankCardRequest();
		request.setBank("TPBank");
		request.setAccountNumber("02928683402");
		request.setBranch("HN");
		request.setHolderName("kafka");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(bankCardRepository.findByMentorIdAndAccountNumberAndStatus(any(), any(), any())).thenReturn(generateBankCard());
		bankCardService.createBankCard(request);
	}

	@Test
	public void createNewBankCard() {
		BankCardRequest request = new BankCardRequest();
		request.setBank("TPBank");
		request.setAccountNumber("02928683402");
		request.setBranch("HN");
		request.setHolderName("kafka");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(bankCardRepository.findByMentorIdAndAccountNumberAndStatus(any(), any(), any())).thenReturn(null);
		when(bankCardRepository.saveAndFlush(any())).thenReturn(generateBankCard());
		bankCardService.createBankCard(request);
	}

	@Test
	public void deleteBankCard() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(generateBankCard());
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		bankCardService.deleteBankCard(1L);
	}

	@Test(expected = NotFoundException.class)
	public void deleteBankCard_NotFoundException() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(null);
		bankCardService.deleteBankCard(1L);
	}

	@Test
	public void updateBankCard() throws NotFoundException {
		BankCardRequest request = new BankCardRequest();
		request.setBank("TPBank");
		request.setAccountNumber("02928683402");
		request.setBranch("HN");
		request.setHolderName("kafka");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(generateBankCard());
		bankCardService.updateBankCard(1L, request);
	}

	@Test(expected = NotFoundException.class)
	public void updateBankCard_NotFoundException() throws NotFoundException {
		BankCardRequest request = new BankCardRequest();
		request.setBank("TPBank");
		request.setAccountNumber("02928683402");
		request.setBranch("HN");
		request.setHolderName("kafka");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(null);
		bankCardService.updateBankCard(1L, request);
	}

	@Test
	public void findBankCard() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(),any())).thenReturn(generateBankCard());
		bankCardService.findBankCard(1L);
	}

	@Test(expected = NotFoundException.class)
	public void findBankCard_NotFoundException() throws NotFoundException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(bankCardRepository.findByIdAndMentorIdAndStatus(any(), any(),any())).thenReturn(null);
		bankCardService.findBankCard(1L);
	}
}