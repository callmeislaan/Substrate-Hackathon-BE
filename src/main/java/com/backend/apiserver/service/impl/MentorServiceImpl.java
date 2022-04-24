package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.FilterMentorWrapperRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.response.BriefMentorResponse;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.IncomeReportResponse;
import com.backend.apiserver.bean.response.MentorOverviewResponse;
import com.backend.apiserver.bean.response.MentorResumeResponse;
import com.backend.apiserver.bean.response.MentorSkillResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
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
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.mapper.AchievementMapper;
import com.backend.apiserver.mapper.CommentMapper;
import com.backend.apiserver.mapper.MentorMapper;
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
import com.backend.apiserver.service.MentorService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MentorServiceImpl implements MentorService {

	private JwtUtils jwtUtils;

	private MoneyExchangeHistoryRepository moneyExchangeHistoryRepository;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private MentorRepository mentorRepository;

	private MentorFollowingRepository mentorFollowingRepository;

	private MentorSkillRepository mentorSkillRepository;

	private SkillRepository skillRepository;

	private AchievementRepository achievementRepository;

	private CommentRepository commentRepository;

	private MentorRequestRepository mentorRequestRepository;

	@Override
	public WrapperResponse getAllHomeMentor(boolean isAnestMentor) {
		List<Mentor> mentors = mentorRepository.getListRandomMentor(isAnestMentor, Status.ACTIVE.toString());
		return new WrapperResponse(
				mentors.stream()
						.map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString())))
						.collect(Collectors.toList())
		);
	}

	@Override
	@AnestTransactional
	public void unregisterMentor() throws ForbiddenException {
		Long userId = jwtUtils.getUserId();
		if (mentorRequestRepository.existsByMentorIdAndStatus(userId, Status.DOING)) {
			throw new ForbiddenException("Cannot unregister mentor have doing request");
		}

		List<MentorRequest> mentorRequests = mentorRequestRepository.findAllByMentorIdAndStatus(userId, Status.PENDING);
		mentorRequestRepository.deleteAll(mentorRequests);

		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		Role role = roleRepository.findByName(Constants.ROLE_MENTEE);
		user.setRole(role);
		userRepository.saveAndFlush(user);

		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		mentor.getAchievements().forEach(achievement -> {
			achievement.setMentor(null);
		});
		mentor.getAchievements().clear();

		mentor.getMentorSkills().forEach(mentorSkill -> {
			mentorSkill.setMentor(null);
			mentorSkill.setSkill(null);
		});
		mentor.getMentorSkills().clear();
		mentor.setStatus(Status.DELETE);

		mentorRepository.saveAndFlush(mentor);
	}

	@Override
	public MentorOverviewResponse getMentorOverview() {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);

		MentorOverviewResponse mentorOverviewResponse = new MentorOverviewResponse();
		mentorOverviewResponse.setTotalHoursBeHired(DateTimeUtils.minuteToHour(mentor.getTotalHoursBeHired()));
		mentorOverviewResponse.setAverageRating(mentor.getAverageRating());
		mentorOverviewResponse.setTotalRequestDeny(mentor.getTotalRequestDeny());
		mentorOverviewResponse.setTotalRequestReceive(mentor.getTotalRequestReceive());

		return mentorOverviewResponse;
	}

	@Override
	public IncomeReportResponse getIncomeReport() {
		Long userId = jwtUtils.getUserId();

		LocalDateTime dailyDateTime = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
		LocalDateTime weeklyDateTime = LocalDate.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY).atStartOfDay();
		LocalDateTime monthlyDateTime = LocalDate.now(ZoneOffset.UTC).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();

		int dailyIncome = moneyExchangeHistoryRepository.getIncomeStatistic(dailyDateTime, userId);
		int weeklyIncome = moneyExchangeHistoryRepository.getIncomeStatistic(weeklyDateTime, userId);
		int monthlyIncome = moneyExchangeHistoryRepository.getIncomeStatistic(monthlyDateTime, userId);

		IncomeReportResponse incomeReportResponse = new IncomeReportResponse();
		incomeReportResponse.setDailyIncome(dailyIncome);
		incomeReportResponse.setWeeklyIncome(weeklyIncome);
		incomeReportResponse.setMonthlyIncome(monthlyIncome);

		return incomeReportResponse;
	}

	@Override
	public PagingWrapperResponse getMentorComments(Long userId, Integer page, Integer size) throws NotFoundException {
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(mentor)) {
			throw new NotFoundException("This userId is not a mentor: " + userId);
		}
		Page<Comment> comments = commentRepository.findAllByMentorId(mentor.getId(), PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		return new PagingWrapperResponse(
				comments.stream().map(CommentMapper::convertToResponse).collect(Collectors.toList()), comments.getTotalElements()
		);
	}

	@Override
	public MentorResumeResponse getMentorResume(Long mentorId) throws NotFoundException {
		Mentor mentor = mentorRepository.findByUserIdAndStatus(mentorId, Status.ACTIVE);
		if (Objects.isNull(mentor)) {
			throw new NotFoundException("This userId is not a mentor: " + mentorId);
		}
		MentorResumeResponse mentorResumeResponse = new MentorResumeResponse();
		User user = mentor.getUser();
		UserDetail userDetail = user.getUserDetail();
		mentorResumeResponse.setGender(userDetail.isGender());
		mentorResumeResponse.setUsername(user.getUsername());
		mentorResumeResponse.setFullName(userDetail.getFullName());
		mentorResumeResponse.setAvatar(userDetail.getAvatar());
		mentorResumeResponse.setJob(mentor.getJob());
		mentorResumeResponse.setPrice(mentor.getPrice());
		mentorResumeResponse.setAnestMentor(mentor.isAnestMentor());
		mentorResumeResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(mentor.getCreatedDate()));
		mentorResumeResponse.setAverageRating(mentor.getAverageRating());
		mentorResumeResponse.setTotalRating1(mentor.getTotalRating1());
		mentorResumeResponse.setTotalRating2(mentor.getTotalRating2());
		mentorResumeResponse.setTotalRating3(mentor.getTotalRating3());
		mentorResumeResponse.setTotalRating4(mentor.getTotalRating4());
		mentorResumeResponse.setTotalRating5(mentor.getTotalRating5());
		mentorResumeResponse.setTotalRequestReceive(mentor.getTotalRequestReceive());
		mentorResumeResponse.setTotalRequestFinish(mentor.getTotalRequestFinish());
		mentorResumeResponse.setTotalHoursBeHired(DateTimeUtils.minuteToHour(mentor.getTotalHoursBeHired()));
		mentorResumeResponse.setIntroduction(mentor.getIntroduction());
		mentorResumeResponse.setSkillDescription(mentor.getSkillDescription());
		mentorResumeResponse.setMentorSkillResponses(
				mentor.getMentorSkills()
						.stream()
						.filter(mentorSkill -> mentorSkill.getSkill().getStatus() == Status.ACTIVE)
						.map(mentorSkill -> MentorSkillResponse
								.builder()
								.id(mentorSkill.getSkill().getId())
								.name(mentorSkill.getSkill().getName())
								.value(mentorSkill.getValue())
								.build()
						).collect(Collectors.toList())
		);
		mentorResumeResponse.setService(mentor.getService());
		if (jwtUtils.hasJsonWebToken()) {
			Long userId = jwtUtils.getUserId();
			if (mentorFollowingRepository.existsByMentorIdAndUserIdAndStatus(mentorId, userId, Status.ACTIVE))
				mentorResumeResponse.setFollowing(true);
		}
		return mentorResumeResponse;
	}

	@Override
	public WrapperResponse getMentorAchievements(Long userId) throws NotFoundException {
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(mentor)) {
			throw new NotFoundException("This userId is not a mentor: " + userId);
		}
		List<Achievement> achievements = achievementRepository.findAllByMentorId(userId);
		//throw to response wrapper
		return new WrapperResponse(
				achievements.stream()
						.map(AchievementMapper::convertToResponse)
						.collect(Collectors.toList())
		);
	}

	@Override
	@AnestTransactional
	public FollowingResponse followOrUnfollowMentor(Long mentorId) throws NotFoundException {
		//validate mentor exist
		Mentor mentor = mentorRepository.findByUserIdAndStatus(mentorId, Status.ACTIVE);
		if (Objects.isNull(mentor)) throw new NotFoundException("Not found any mentor with id: " + mentorId);

		//validate user exist
		Long userId = jwtUtils.getUserId();
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(user)) throw new NotFoundException("Not found any user with id: " + userId);

		//get mentor following by mentorId and userId
		MentorFollowing mentorFollowing = mentorFollowingRepository.findByMentorIdAndUserId(mentorId, userId);
		if (Objects.isNull(mentorFollowing)) {
			mentorFollowing = new MentorFollowing();
			mentorFollowing.setMentor(mentor);
			mentorFollowing.setUser(user);
			mentorFollowing.setStatus(Status.ACTIVE);
		} else if (Status.ACTIVE.toString().equals(mentorFollowing.getStatus().toString())) {
			mentorFollowing.setStatus(Status.DELETE);
		} else {
			mentorFollowing.setStatus(Status.ACTIVE);
		}
		mentorFollowingRepository.saveAndFlush(mentorFollowing);

		FollowingResponse followingResponse = new FollowingResponse();
		followingResponse.setFollowingStatus(mentorFollowing.getStatus().toString());
		return followingResponse;
	}

	@Override
	@AnestTransactional
	public void unfollowAllMentor() {
		Long userId = jwtUtils.getUserId();
		mentorFollowingRepository.unfollowAllMentor(Status.DELETE, Status.ACTIVE, userId);
	}

	@Override
	public PagingWrapperResponse getFollowingMentors(Integer page, Integer size) {
		Long userId = jwtUtils.getUserId();
		Page<MentorFollowing> mentorFollowings = mentorFollowingRepository.findAllByUserIdAndStatusOrderByLastModifiedDateDesc(userId, Status.ACTIVE, PageRequest.of(page - 1, size, Constants.DEFAULT_ORDER));
		if (mentorFollowings.isEmpty()) return new PagingWrapperResponse(Collections.emptyList(), 0);
		return new PagingWrapperResponse(
				mentorFollowings.stream()
						.map(mentorFollowing -> MentorMapper.mentorEntityToShortDesResponse(mentorFollowing.getMentor(), skillRepository.findAllByMentorIdAndStatus(mentorFollowing.getMentor().getId(), Status.ACTIVE.toString())))
						.map(mentorShortDescriptionResponse -> {
							mentorShortDescriptionResponse.setFollowed(true);
							return mentorShortDescriptionResponse;
						})
						.collect(Collectors.toList()),
				mentorFollowings.getTotalElements()
		);
	}

	@Override
	public PagingWrapperResponse filterMentor(FilterMentorWrapperRequest filterMentorWrapperRequest) {
		Page<Mentor> mentors = mentorRepository.findAll((Specification<Mentor>) (root, criteriaQuery, criteriaBuilder) -> {
			//create default predicate
			Predicate p = criteriaBuilder.conjunction();

			//keyword search
			if (!StringUtils.isEmpty(filterMentorWrapperRequest.getKeyWord())) {
				p = criteriaBuilder.and(
						p, criteriaBuilder.or(
								criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), "%" + filterMentorWrapperRequest.getKeyWord().toLowerCase() + "%"),
								criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("userDetail").get("fullName")), "%" + filterMentorWrapperRequest.getKeyWord().toLowerCase() + "%")
						)
				);
			}

			//list skill id search
			if (!CollectionUtils.isEmpty(filterMentorWrapperRequest.getFilter().getSkillIds())) {
				List<Long> mentorIds = mentorSkillRepository.getListMentorIdByListSkillId(filterMentorWrapperRequest.getFilter().getSkillIds());
				if (!CollectionUtils.isEmpty(mentorIds)) {
					p = criteriaBuilder.and(p, root.get("user").get("id").in(mentorIds));
				}
			}

			//price search
			p = criteriaBuilder.and(p, criteriaBuilder.between(root.get("price"), filterMentorWrapperRequest.getFilter().getMinPrice(), filterMentorWrapperRequest.getFilter().getMaxPrice()));

			//Anest mentor search
			p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("isAnestMentor"), filterMentorWrapperRequest.getFilter().isAnestMentor()));

			//Active mentor
			p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("status"), Status.ACTIVE));

			//sorting by
			Expression<Number> expression;
			switch (filterMentorWrapperRequest.getSort().toLowerCase()) {
				case "price":
					expression = root.get("price");
					break;
				case "total_request_finish":
					expression = root.get("totalRequestFinish");
					break;
				default:
					expression = root.get("averageRating");
					break;
			}

			//order
			switch (filterMentorWrapperRequest.getOrder().toLowerCase()) {
				case "asc":
					criteriaQuery.orderBy(criteriaBuilder.asc(expression));
					break;
				default:
					criteriaQuery.orderBy(criteriaBuilder.desc(expression));
					break;
			}

			return p;
		}, PageRequest.of(filterMentorWrapperRequest.getPage() - 1, filterMentorWrapperRequest.getSize()));

		return new PagingWrapperResponse(mentors.getContent().stream().map(mentor -> MentorMapper.mentorEntityToShortDesResponse(mentor, skillRepository.findAllByMentorIdAndStatus(mentor.getId(), Status.ACTIVE.toString()))).collect(Collectors.toList()), mentors.getTotalElements());
	}

	@Override
	@AnestTransactional
	public void updateMentorInfo(RegisterMentorRequest registerMentorRequest) throws UserNotFoundException {
		Long userId = jwtUtils.getUserId();
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(mentor)) {
			throw new UserNotFoundException("User does not exist");
		}
		mentor.setPrice(registerMentorRequest.getPrice());
		mentor.setJob(registerMentorRequest.getJob());
		mentor.setIntroduction(registerMentorRequest.getIntroduction());
		mentor.setSkillDescription(registerMentorRequest.getSkillDescription());
		mentor.setService(registerMentorRequest.getService());
		mentor.getMentorSkills().stream().forEach(mentorSkill -> {
			mentorSkill.setSkill(null);
			mentorSkill.setMentor(null);
		});
		mentor.getMentorSkills().clear();
		List<MentorSkill> mentorSkills = registerMentorRequest.getMentorSkills()
				.stream()
				.filter(requestSkill -> Objects.nonNull(skillRepository.findByIdAndStatus(requestSkill.getId(), Status.ACTIVE)))
				.map(
						requestSkill -> {
							Skill skill = skillRepository.findByIdAndStatus(requestSkill.getId(), Status.ACTIVE);
							MentorSkill mentorSkill = new MentorSkill();
							mentorSkill.setMentor(mentor);
							mentorSkill.setSkill(skill);
							mentorSkill.setValue(requestSkill.getValue());
							mentorSkill.setStatus(Status.ACTIVE);
							return mentorSkill;
						}
				)
				.collect(Collectors.toList());
		mentor.getMentorSkills().addAll(mentorSkills);
		mentor.getAchievements().stream().forEach(achievement -> achievement.setMentor(null));
		mentor.getAchievements().clear();
		mentor.getAchievements().addAll(
				registerMentorRequest.getAchievements()
						.stream()
						.map(
								requestAchievement -> {
									Achievement achievement = new Achievement();
									achievement.setMentor(mentor);
									achievement.setContent(requestAchievement.getContent());
									achievement.setTitle(requestAchievement.getTitle());
									return achievement;
								}
						)
						.collect(Collectors.toSet())
		);
		mentorRepository.saveAndFlush(mentor);
	}

	@Override
	@AnestTransactional
	public void setAnestMentor(Long mentorId) throws MentorNotFoundException {
		Mentor mentor = mentorRepository.findMentorByUserIdAndStatus(mentorId, Status.ACTIVE);
		if (Objects.isNull(mentor)) throw new MentorNotFoundException("Mentor not found with given id" + mentorId);
		mentor.setAnestMentor(true);
		mentorRepository.saveAndFlush(mentor);
	}

	@Override
	public PagingWrapperResponse viewMentors(Integer page, Integer size, String keyword) {
		Page<Mentor> pageUsers = mentorRepository.findAllByEmailOrUsernameOrFullName(keyword, PageRequest.of(page - 1, size, Sort.by("last_modified_date").descending()));
		return new PagingWrapperResponse(
				pageUsers.get()
						.map(this::convertToBriefMentorResponse)
						.collect(Collectors.toList()),
				pageUsers.getTotalElements()
		);
	}

	private BriefMentorResponse convertToBriefMentorResponse(Mentor mentor) {
		BriefMentorResponse briefMentorResponse = new BriefMentorResponse();
		User user = mentor.getUser();
		UserDetail userDetail = user.getUserDetail();
		briefMentorResponse.setAnestMentor(mentor.isAnestMentor());
		briefMentorResponse.setEmail(user.getEmail());
		briefMentorResponse.setId(mentor.getId());
		briefMentorResponse.setJob(mentor.getJob());
		briefMentorResponse.setName(userDetail.getFullName());
		briefMentorResponse.setPrice(mentor.getPrice());
		briefMentorResponse.setRating(mentor.getAverageRating());
		briefMentorResponse.setTotalRequestFinish(mentor.getTotalRequestFinish());
		briefMentorResponse.setTotalRequestReceive(mentor.getTotalRequestReceive());
		briefMentorResponse.setTotalTime(DateTimeUtils.minuteToHour(mentor.getTotalHoursBeHired()));
		briefMentorResponse.setUsername(user.getUsername());
		briefMentorResponse.setGender(userDetail.isGender());
		return briefMentorResponse;
	}
}
