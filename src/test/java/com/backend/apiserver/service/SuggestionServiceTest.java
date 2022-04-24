package com.backend.apiserver.service;

import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.RatingBean;
import com.backend.apiserver.entity.Role;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.repository.CommentRepository;
import com.backend.apiserver.repository.MentorFollowingRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.service.impl.SuggestionServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceTest {
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private MentorFollowingRepository mentorFollowingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    SuggestionService suggestionService = new SuggestionServiceImpl(
            jwtUtils,
            mentorRepository,
            mentorFollowingRepository,
            commentRepository,
            skillRepository
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllAnestMentorSuggestion() {
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
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        List<Mentor> mentors = Arrays.asList(mentor);

        when(mentorRepository.getListAnestMentorSuggestionByListSkillId(Arrays.asList(1L))).thenReturn(mentors);
        WrapperResponse wrapperResponse = suggestionService.getAllAnestMentorSuggestion(Arrays.asList(1L));

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllFollowingMentorSuggestion() {
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
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        List<Mentor> mentors = Arrays.asList(mentor);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(mentorFollowingRepository.getListMentorIdByUserId(1L)).thenReturn(new HashSet<>(Arrays.asList(1L)));
        when(mentorRepository.getListFollowingMentorSuggestionByListSkillIdAndListMentorId(Arrays.asList(1L), new HashSet<>(Arrays.asList(1L)))).thenReturn(mentors);
        WrapperResponse wrapperResponse = suggestionService.getAllFollowingMentorSuggestion(Arrays.asList(1L));

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllHiredMentorSuggestion() {
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
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        List<Mentor> mentors = Arrays.asList(mentor);

        RatingBean ratingBean = new RatingBean() {
            @Override
            public Long getUserId() {
                return 1L;
            }

            @Override
            public Long getMentorId() {
                return 1L;
            }

            @Override
            public float getRating() {
                return 5;
            }
        };
        List<RatingBean> ratingBeans = Arrays.asList(ratingBean);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(commentRepository.getListRatingByListSkillIdAndUserId(Arrays.asList(1L), 1L)).thenReturn(ratingBeans);
        when(mentorRepository.findByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);


        WrapperResponse wrapperResponse = suggestionService.getAllHiredMentorSuggestion(Arrays.asList(1L));

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllBestMentorSuggestion() {
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
        Mentor mentor = new Mentor();
        mentor.setUser(user);
        List<Mentor> mentors = Arrays.asList(mentor);

        RatingBean ratingBean = new RatingBean() {
            @Override
            public Long getUserId() {
                return 1L;
            }

            @Override
            public Long getMentorId() {
                return 1L;
            }

            @Override
            public float getRating() {
                return 5;
            }
        };
        List<RatingBean> ratingBeans = Arrays.asList(ratingBean);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(commentRepository.getListRatingBean()).thenReturn(ratingBeans);
        when(mentorRepository.findByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(mentor);


        WrapperResponse wrapperResponse = suggestionService.getAllBestMentorSuggestion(Arrays.asList(1L));

        assertEquals(1, wrapperResponse.getData().size());
    }
}