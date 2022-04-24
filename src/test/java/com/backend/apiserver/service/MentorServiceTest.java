package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.AchievementRequest;
import com.backend.apiserver.bean.request.FilterMentorRequest;
import com.backend.apiserver.bean.request.FilterMentorWrapperRequest;
import com.backend.apiserver.bean.request.FilterRequestRequest;
import com.backend.apiserver.bean.request.MentorSkillRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.response.CommentResponse;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.Comment;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorFollowing;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.Role;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.mapper.SkillMapper;
import com.backend.apiserver.repository.AchievementRepository;
import com.backend.apiserver.repository.CommentRepository;
import com.backend.apiserver.repository.MentorFollowingRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.MentorSkillRepository;
import com.backend.apiserver.repository.MoneyExchangeHistoryRepository;
import com.backend.apiserver.repository.RoleRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.MentorServiceImpl;
import com.backend.apiserver.service.impl.RequestConfirmationServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.JwtUtils;
import org.aspectj.weaver.ast.Not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MentorServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private MentorRepository mentorRepository;
	@Mock
	private MentorFollowingRepository mentorFollowingRepository;
	@Mock
	private MentorSkillRepository mentorSkillRepository;
	@Mock
	private SkillRepository skillRepository;
	@Mock
	private AchievementRepository achievementRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private MentorRequestRepository mentorRequestRepository;

	@InjectMocks
	MentorService mentorService = new MentorServiceImpl(
			jwtUtils,
			moneyExchangeHistoryRepository,
			userRepository,
			roleRepository,
			mentorRepository,
			mentorFollowingRepository,
			mentorSkillRepository,
			skillRepository,
			achievementRepository,
			commentRepository,
			mentorRequestRepository
	);

	@Test
	public void getAllHomeMentor() {
		when(skillRepository.findAllByMentorIdAndStatus(any(), any())).thenReturn(Arrays.asList(generateSkill()));
		when(mentorRepository.getListRandomMentor(true, Status.ACTIVE.toString())).thenReturn(Arrays.asList(generateMentor()));
		mentorService.getAllHomeMentor(true);
	}


	private Skill generateSkill(){
		Skill skill = new Skill();
		skill.setName("java");
		skill.setId(1L);
		return skill;
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

	@Test(expected = ForbiddenException.class)
	public void unregisterMentor_ThrowForbiddenException() throws ForbiddenException {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRequestRepository.existsByMentorIdAndStatus(any(), any())).thenReturn(true);
		mentorService.unregisterMentor();
	}

	@Test
	public void unregisterMentor_Success() throws ForbiddenException {
		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		Role role = new Role();
		role.setName("ROLE_USER");
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRequestRepository.existsByMentorIdAndStatus(any(), any())).thenReturn(false);
		when(mentorRequestRepository.findAllByMentorIdAndStatus(any(), any())).thenReturn(Arrays.asList(new MentorRequest()));
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(user);
		when(roleRepository.findByName(Constants.ROLE_MENTEE)).thenReturn(role);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		mentorService.unregisterMentor();
	}

	@Test
	public void getMentorOverview() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		mentorService.getMentorOverview();
	}

	@Test
	public void getIncomeReport() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		mentorService.getIncomeReport();
	}

	@Test(expected = NotFoundException.class)
	public void getMentorComments() throws NotFoundException {
		Page page = new PageImpl(Arrays.asList(generateComment()));
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(null);
		mentorService.getMentorComments(1L,1,10);
	}

	@Test
	public void getMentorComments_Exception() throws NotFoundException {
		Page page = new PageImpl(Arrays.asList(generateComment()));
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(commentRepository.findAllByMentorId(any(), any())).thenReturn(page);
		mentorService.getMentorComments(1L,1,10);
	}

	private Comment generateComment() {
		Comment comment = new Comment();
		comment.setContent("acb");
		comment.setId(1L);
		comment.setCreatedDate(LocalDateTime.now());
		comment.setUser(generateUser());
		return comment;
	}

	@Test
	public void getMentorResume() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(jwtUtils.hasJsonWebToken()).thenReturn(true);
		when(mentorFollowingRepository.existsByMentorIdAndUserIdAndStatus(any(), any(), any())).thenReturn(true);
		mentorService.getMentorResume(1L);
	}

	@Test(expected = NotFoundException.class)
	public void getMentorResume_NotFoundException() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(null);
		mentorService.getMentorResume(1L);
	}

	@Test(expected = NotFoundException.class)
	public void getMentorAchievements() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(null);
		mentorService.getMentorAchievements(1L);
	}

	@Test
	public void getMentorAchievements_NotFoundException() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		mentorService.getMentorAchievements(1L);
	}

	@Test
	public void followOrUnfollowMentor() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorFollowingRepository.findByMentorIdAndUserId(any(), any())).thenReturn(null);
		mentorService.followOrUnfollowMentor(1L);
	}

	@Test
	public void followOrUnfollowMentorReturnValueActive() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorFollowingRepository.findByMentorIdAndUserId(any(), any())).thenReturn(generateMentorFollowing(Status.ACTIVE));
		mentorService.followOrUnfollowMentor(1L);
	}

	@Test
	public void followOrUnfollowMentorReturnValueDelete() throws NotFoundException {
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(userRepository.findByIdAndStatus(any(), any())).thenReturn(generateUser());
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorFollowingRepository.findByMentorIdAndUserId(any(), any())).thenReturn(generateMentorFollowing(Status.DELETE));
		mentorService.followOrUnfollowMentor(1L);
	}

	private MentorFollowing generateMentorFollowing(Status status) {
		MentorFollowing mentorFollowing = new MentorFollowing();
		mentorFollowing.setMentor(generateMentor());
		mentorFollowing.setUser(generateUser());
		mentorFollowing.setStatus(status);
		return mentorFollowing;
	}

	@Test
	public void unfollowAllMentor() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		mentorService.unfollowAllMentor();
	}

	@Test
	public void getFollowingMentors() {
		when(jwtUtils.getUserId()).thenReturn(1L);
		Page page = new PageImpl(Arrays.asList(generateMentorFollowing(Status.ACTIVE)));
		when(mentorFollowingRepository.findAllByUserIdAndStatusOrderByLastModifiedDateDesc(
				any(),any(), any())).thenReturn(page);
;		mentorService.getFollowingMentors(1,10);
	}

	@Test
	public void filterMentor() {
		Page page = new PageImpl(Arrays.asList(generateMentor()));
		when(mentorRepository.findAll((Specification<Mentor>) any(), (Pageable) any())).thenReturn(page);
		FilterMentorWrapperRequest wrapperRequest = new FilterMentorWrapperRequest();
		wrapperRequest.setPage(1);
		wrapperRequest.setSize(6);
		wrapperRequest.setSort("rating");
		wrapperRequest.setOrder("desc");
		FilterMentorRequest filter = new FilterMentorRequest();
		filter.setSkillIds(Arrays.asList(1L,2L));
		filter.setMinPrice(10000);
		filter.setMaxPrice(200000);
		filter.setAnestMentor(true);
		wrapperRequest.setFilter(filter);
		mentorService.filterMentor(wrapperRequest);
	}

	@Test
	public void updateMentorInfo() throws Exception {
		RegisterMentorRequest request = new RegisterMentorRequest();
		request.setPrice(100000);
		request.setService("service");
		MentorSkillRequest mentorSkill = new MentorSkillRequest();
		mentorSkill.setId(1L);
		mentorSkill.setValue(4);
		request.setMentorSkills(Arrays.asList(mentorSkill));
		request.setSkillDescription("description");
		request.setJob("java");
		AchievementRequest achievementRequest = new AchievementRequest();
		achievementRequest.setTitle("love");
		achievementRequest.setContent("love");
		request.setAchievements(Arrays.asList(achievementRequest));
		request.setIntroduction("intro");

		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		when(skillRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(generateSkill());
		mentorService.updateMentorInfo(request);
	}

	@Test(expected = UserNotFoundException.class)
	public void updateMentorInfo_UserNotFoundException() throws Exception {
		when(jwtUtils.getUserId()).thenReturn(1L);
		when(mentorRepository.findByUserIdAndStatus(any(), any())).thenReturn(null);
		mentorService.updateMentorInfo(new RegisterMentorRequest());
	}

	@Test(expected = MentorNotFoundException.class)
	public void setAnestMentor_MentorNotFoundException() throws MentorNotFoundException {
		mentorService.setAnestMentor(1L);
	}

	@Test
	public void setAnestMentor() throws MentorNotFoundException {
		when(mentorRepository.findMentorByUserIdAndStatus(any(), any())).thenReturn(generateMentor());
		mentorService.setAnestMentor(1L);
	}

	@Test
	public void viewMentors() {
		Page page = new PageImpl(Arrays.asList(generateMentor()));
		when(mentorRepository.findAllByEmailOrUsernameOrFullName(any(),any())).thenReturn(page);
		mentorService.viewMentors(1,10, "any");
	}
}