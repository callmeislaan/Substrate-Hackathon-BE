package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.MentorSkillRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.request.UpdatePasswordRequest;
import com.backend.apiserver.bean.request.UseAnestCardRequest;
import com.backend.apiserver.bean.request.UserProfileRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.entity.AnestCard;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.Role;
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
import com.backend.apiserver.service.impl.UserServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailRepository userDetailRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private MentorHireRepository mentorHireRepository;
    @Mock
    private AnestCardRepository anestCardRepository;
    @Mock
    private MoneyInHistoryRepository moneyInHistoryRepository;
    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    UserService userService = new UserServiceImpl(
            jwtUtils,
            userRepository,
            userDetailRepository,
            passwordEncoder,
            roleRepository,
            mentorRepository,
            skillRepository,
            mentorHireRepository,
            anestCardRepository,
            moneyInHistoryRepository,
            emailSenderService
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getProfile() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        Role role = new Role();
        role.setName(Constants.ROLE_MENTOR);
        user.setRole(role);
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(user);
        userService.getProfile();
    }

    @Test
    public void getUserOverview() {
        UserDetail userDetail = new UserDetail();
        userDetail.setTotalHoursHiredMentor(10);
        userDetail.setTotalHoursHiredMentor(20);
        userDetail.setTotalRequestCreate(30);
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userDetailRepository.findByUserId(1L)).thenReturn(userDetail);
        userService.getUserOverview();
    }

    @Test
    public void getUserFinanceOverview() {
        UserDetail userDetail = new UserDetail();
        userDetail.setTotalBudgetCurrent(10);
        userDetail.setTotalBudgetIn(20);

        Mentor mentor = new Mentor();
        mentor.setTotalMoneyCurrent(30);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userDetailRepository.findByUserId(1L)).thenReturn(userDetail);
        when(mentorRepository.findByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);
        userService.getUserFinanceOverview();
    }

    @Test
    public void updatePassword() throws PasswordNotMatchException {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setNewPassword("123");
        updatePasswordRequest.setOldPassword("123456");

        User user = new User();
        String currentPass = passwordEncoder.encode("123456");
        user.setPassword(currentPass);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(user);
        when(passwordEncoder.matches(updatePasswordRequest.getOldPassword(), currentPass)).thenReturn(true);
        userService.updatePassword(updatePasswordRequest);
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void registerMentor() throws UserNotFoundException, SkillNotFoundException, RoleNotFoundException {
        RegisterMentorRequest registerMentorRequest = new RegisterMentorRequest();
        registerMentorRequest.setIntroduction("intro");
        MentorSkillRequest mentorSkillRequest = new MentorSkillRequest();
        mentorSkillRequest.setId(1);
        mentorSkillRequest.setValue(1);
        List<MentorSkillRequest> mentorSkills = Arrays.asList(mentorSkillRequest);
        registerMentorRequest.setMentorSkills(mentorSkills);
        List<Long> skillIds = Arrays.asList(1L);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userRepository.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(new User());
        when(roleRepository.findByName(Constants.ROLE_MENTOR)).thenReturn(new Role());
        when(skillRepository.existsAllByIdInAndStatus(skillIds, Status.ACTIVE)).thenReturn(true);

        userService.registerMentor(registerMentorRequest);
    }

    @Test
    public void updateProfile() {
        UserProfileRequest userProfileRequest = new UserProfileRequest();
        userProfileRequest.setFullName("Dung Do");
        userProfileRequest.setPhone("012345678");
        userProfileRequest.setGender(true);

        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(userDetailRepository.findUserDetailByUserId(1L)).thenReturn(userDetail);

        userService.updateProfile(userProfileRequest);
    }

    @Test
    public void viewUsers() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setStatus(Status.ACTIVE);
        Role role = new Role();
        role.setName(Constants.ROLE_MENTOR);
        user.setRole(role);
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Page<User> users = new PageImpl(Arrays.asList(user));

        when(userRepository.findAllByEmailOrUsernameOrFullName("key", PageRequest.of(0, 10, Sort.by("last_modified_date").descending()))).thenReturn(users);
        PagingWrapperResponse wrapperResponse = userService.viewUsers(1, 10, "key");
        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void requestUsingAnestCard() throws TemplateException, DataDuplicatedException, IOException, InvalidDataException, NotFoundException, MessagingException {
        UseAnestCardRequest useAnestCardRequest = new UseAnestCardRequest();
        useAnestCardRequest.setCode("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        useAnestCardRequest.setSerial("ANEST1");

        AnestCard anestCard = new AnestCard();
        anestCard.setId(1L);
        anestCard.setStatus(Status.ACTIVE);
        anestCard.setCode(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        anestCard.setValue(10);
        Optional<AnestCard> anestCardOptional = Optional.of(anestCard);

        User user = new User();
        user.setId(1L);
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.setTotalBudgetCurrent(10);
        userDetail.setTotalBudgetIn(20);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(anestCardRepository.findById(1L)).thenReturn(anestCardOptional);
        when(userDetailRepository.findUserDetailByUserId(1L)).thenReturn(userDetail);

        userService.requestUsingAnestCard(useAnestCardRequest);
    }
}