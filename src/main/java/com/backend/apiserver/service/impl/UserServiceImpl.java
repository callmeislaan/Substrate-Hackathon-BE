package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.bean.request.MentorSkillRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.request.UpdatePasswordRequest;
import com.backend.apiserver.bean.request.UseAnestCardRequest;
import com.backend.apiserver.bean.request.UserProfileRequest;
import com.backend.apiserver.bean.response.BriefUserResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.UserFinanceOverviewResponse;
import com.backend.apiserver.bean.response.UserOverviewResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.entity.Achievement;
import com.backend.apiserver.entity.AnestCard;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorSkill;
import com.backend.apiserver.entity.MoneyInHistory;
import com.backend.apiserver.entity.PaymentMethod;
import com.backend.apiserver.entity.Role;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.PasswordNotMatchException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.repository.AnestCardRepository;
import com.backend.apiserver.repository.MentorHireRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MoneyInHistoryRepository;
import com.backend.apiserver.repository.RoleRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.service.UserService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private JwtUtils jwtUtils;

	private UserRepository userRepository;

	private UserDetailRepository userDetailRepository;

	private PasswordEncoder passwordEncoder;

	private RoleRepository roleRepository;

	private MentorRepository mentorRepository;

	private SkillRepository skillRepository;

	private MentorHireRepository mentorHireRepository;

	private AnestCardRepository anestCardRepository;

	private MoneyInHistoryRepository moneyInHistoryRepository;

	private EmailSenderService emailSenderService;

	@Override
	public UserProfileResponse getProfile() {
		Long userId = jwtUtils.getUserId();
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);

		UserProfileResponse userProfileResponse = new UserProfileResponse();

		userProfileResponse.setUsername(user.getUsername());
		userProfileResponse.setEmail(user.getEmail());
		userProfileResponse.setMentor(user.getRole().getName().equals(Constants.ROLE_MENTOR));
		userProfileResponse.setAvatar(user.getUserDetail().getAvatar());
		userProfileResponse.setFullName(user.getUserDetail().getFullName());
		userProfileResponse.setDateOfBirth(DateTimeUtils.toCurrentTimeMillis(user.getUserDetail().getDateOfBirth()));
		userProfileResponse.setPhone(user.getUserDetail().getPhone());
		userProfileResponse.setGender(user.getUserDetail().isGender());
		userProfileResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(user.getUserDetail().getCreatedDate()));

		return userProfileResponse;
	}

	@Override
	public UserOverviewResponse getUserOverview() {
		Long userId = jwtUtils.getUserId();
		UserDetail userDetail = userDetailRepository.findByUserId(userId);

		UserOverviewResponse userOverviewResponse = new UserOverviewResponse();
		userOverviewResponse.setTotalHoursHiredMentor(DateTimeUtils.minuteToHour(userDetail.getTotalHoursHiredMentor()));
		userOverviewResponse.setTotalPeopleHired(userDetail.getTotalPeopleHired());
		userOverviewResponse.setTotalRequestCreate(userDetail.getTotalRequestCreate());

		return userOverviewResponse;
	}

	@Override
	public UserFinanceOverviewResponse getUserFinanceOverview() {
		Long userId = jwtUtils.getUserId();
		UserDetail userDetail = userDetailRepository.findByUserId(userId);
		UserFinanceOverviewResponse userFinanceOverviewResponse = new UserFinanceOverviewResponse();
		userFinanceOverviewResponse.setTotalBudgetCurrent(userDetail.getTotalBudgetCurrent());
		userFinanceOverviewResponse.setTotalBudgetIn(userDetail.getTotalBudgetIn());
		Mentor mentor = mentorRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
		if (Objects.nonNull(mentor)) {
			userFinanceOverviewResponse.setTotalMoneyCurrent(mentor.getTotalMoneyCurrent());
		}
		return userFinanceOverviewResponse;
	}

	@Override
	@AnestTransactional
	public void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws PasswordNotMatchException {
		Long userId = jwtUtils.getUserId();
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new PasswordNotMatchException("Password provided doesn't match with current password");
		}
		user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
		userRepository.saveAndFlush(user);
	}

	@Override
	@AnestTransactional
	public void registerMentor(RegisterMentorRequest registerMentorRequest) throws UserNotFoundException, RoleNotFoundException, SkillNotFoundException {
		Long userId = jwtUtils.getUserId();
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		if (Objects.isNull(user)) {
			throw new UserNotFoundException("User does not exist");
		}
		Role role = roleRepository.findByName(Constants.ROLE_MENTOR);
		if (Objects.isNull(role)) {
			throw new RoleNotFoundException("Role MENTOR haven't been inserted to system");
		}
		List<Long> skillIds = registerMentorRequest
				.getMentorSkills()
				.stream()
				.map(MentorSkillRequest::getId)
				.collect(Collectors.toList());
		if (!skillRepository.existsAllByIdInAndStatus(skillIds, Status.ACTIVE)) {
			throw new SkillNotFoundException("Register mentor with deleted skill or undefined skills" + skillIds);
		}
		Mentor mentor;
		boolean existedMentorInformation = mentorRepository.existsByUserIdAndStatus(userId, Status.DELETE);
		if (existedMentorInformation) {
			mentor = mentorRepository.findByUserIdAndStatus(userId, Status.DELETE);
		} else {
			mentor = new Mentor();
		}
		mentor.setAnestMentor(false);
		mentor.setPrice(registerMentorRequest.getPrice());
		mentor.setJob(registerMentorRequest.getJob());
		mentor.setIntroduction(registerMentorRequest.getIntroduction());
		mentor.setSkillDescription(registerMentorRequest.getSkillDescription());
		mentor.setService(registerMentorRequest.getService());
		mentor.setStatus(Status.ACTIVE);
		mentor.getMentorSkills().addAll(
				registerMentorRequest.getMentorSkills()
						.stream()
						.map(
								requestSkill -> {
									Skill skill = skillRepository.findByIdAndStatus(requestSkill.getId(), Status.ACTIVE);
									MentorSkill mentorSkill = new MentorSkill();
									mentorSkill.setStatus(Status.ACTIVE);
									mentorSkill.setMentor(mentor);
									mentorSkill.setSkill(skill);
									mentorSkill.setValue(requestSkill.getValue());
									return mentorSkill;
								}
						)
						.collect(Collectors.toList())
		);
		if (Objects.nonNull(registerMentorRequest.getAchievements())) {
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
		}
		if (!existedMentorInformation) {
			mentor.setUser(user);
			user.setMentor(mentor);
		}
		user.setRole(role);
		mentorRepository.saveAndFlush(mentor);
	}

	@Override
	@AnestTransactional
	public void updateProfile(UserProfileRequest userProfileRequest) {
		Long userId = jwtUtils.getUserId();
		UserDetail userDetail = userDetailRepository.findUserDetailByUserId(userId);
		userDetail.setFullName(userProfileRequest.getFullName());
		userDetail.setPhone(userProfileRequest.getPhone());
		userDetail.setGender(userProfileRequest.isGender());
		userDetail.setDateOfBirth(DateTimeUtils.fromCurrentTimeMillis(userProfileRequest.getDateOfBirth()));
		userDetailRepository.saveAndFlush(userDetail);
	}

	@Override
	public PagingWrapperResponse viewUsers(Integer page, Integer size, String keyword) {
		Page<User> pageUsers = userRepository.findAllByEmailOrUsernameOrFullName(keyword, PageRequest.of(page - 1, size, Sort.by("last_modified_date").descending()));
		return new PagingWrapperResponse(
				pageUsers.get()
						.map(this::convertToBriefUserResponse)
						.collect(Collectors.toList()),
				pageUsers.getTotalElements()
		);
	}

	private BriefUserResponse convertToBriefUserResponse(User user) {
		BriefUserResponse briefUserResponse = new BriefUserResponse();
		UserDetail userDetail = user.getUserDetail();
		briefUserResponse.setId(user.getId());
		briefUserResponse.setFullName(userDetail.getFullName());
		briefUserResponse.setUsername(user.getUsername());
		briefUserResponse.setEmail(user.getEmail());
		briefUserResponse.setDateOfBirth(DateTimeUtils.toCurrentTimeMillis(userDetail.getDateOfBirth()));
		briefUserResponse.setPhone(userDetail.getPhone());
		briefUserResponse.setStatus(user.getStatus().toString());
		briefUserResponse.setGender(userDetail.isGender());
		return briefUserResponse;
	}

	@Override
	@AnestTransactional
	public void requestUsingAnestCard(UseAnestCardRequest useAnestCardRequest) throws NotFoundException, DataDuplicatedException, InvalidDataException, MessagingException, IOException, TemplateException {
		Optional<AnestCard> anestCardOptional = anestCardRepository.findById(idFromSerial(useAnestCardRequest.getSerial()));
		if (!anestCardOptional.isPresent()) throw new NotFoundException("Serial does not exist");
		AnestCard anestCard = anestCardOptional.get();
		if (!anestCard.getStatus().equals(Status.ACTIVE)) throw new DataDuplicatedException("Anest Card is used");
		if (!anestCard.getCode().toString().equals(useAnestCardRequest.getCode()))
			throw new InvalidDataException("Anest Card code is not correct");
		Long userId = jwtUtils.getUserId();
		UserDetail userDetail = userDetailRepository.findUserDetailByUserId(userId);
		userDetail.setTotalBudgetIn(userDetail.getTotalBudgetIn() + anestCard.getValue());
		userDetail.setTotalBudgetCurrent(userDetail.getTotalBudgetCurrent() + anestCard.getValue());
		userDetailRepository.saveAndFlush(userDetail);
		MoneyInHistory moneyInHistory = new MoneyInHistory();
		moneyInHistory.setAmount(anestCard.getValue());
		moneyInHistory.setPaymentMethod(PaymentMethod.ANEST_CARD);
		moneyInHistory.setStatus(Status.ACTIVE);
		moneyInHistory.setUser(userDetail.getUser());
		moneyInHistoryRepository.saveAndFlush(moneyInHistory);
		anestCard.setStatus(Status.USED);
		anestCard.setMoneyInHistory(moneyInHistory);
		anestCardRepository.saveAndFlush(anestCard);

		Map<String, Object> params = new HashMap<>();
		params.put("amount", anestCard.getValue());
		params.put("fullName", userDetail.getFullName());

		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(userDetail.getUser().getEmail()),
				params,
				MailRequest.TEMPLATE_MONEY_IN,
				MailRequest.TITLE_MONEY_IN
		);

		emailSenderService.sendEmailTemplate(mailRequest);
	}

	public Long idFromSerial(String serial) throws InvalidDataException {
		try {
			return Long.parseLong(serial.split(Constants.ANEST)[1]);
		} catch (NumberFormatException e) {
			throw new InvalidDataException("Serial must be correct!");
		}
	}
}

