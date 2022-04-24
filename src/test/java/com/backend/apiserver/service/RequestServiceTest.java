package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RequestRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorRequest;
import com.backend.apiserver.entity.Request;
import com.backend.apiserver.entity.RequestAnnouncement;
import com.backend.apiserver.entity.RequestFollowing;
import com.backend.apiserver.entity.Skill;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.MentorFollowingRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.MentorRequestRepository;
import com.backend.apiserver.repository.RequestAnnouncementRepository;
import com.backend.apiserver.repository.RequestFollowingRepository;
import com.backend.apiserver.repository.RequestRepository;
import com.backend.apiserver.repository.SkillRepository;
import com.backend.apiserver.repository.UserDetailRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.RequestServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.Stat;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceTest {
    @Mock
    JwtUtils jwtUtils;
    @Mock
    RequestRepository requestRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    RequestAnnouncementRepository requestAnnouncementRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    RequestFollowingRepository requestFollowingRepository;
    @Mock
    MentorRequestRepository mentorRequestRepository;
    @Mock
    UserDetailRepository userDetailRepository;
    @Mock
    MentorFollowingRepository mentorFollowingRepository;
    @InjectMocks
    RequestService requestService = new RequestServiceImpl(
            jwtUtils,
            requestRepository,
            skillRepository,
            requestAnnouncementRepository,
            mentorRepository,
            userRepository,
            requestFollowingRepository,
            mentorRequestRepository,
            userDetailRepository,
            mentorFollowingRepository
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getHomeRequests() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.ACTIVE);
        request.setUser(user);
        List<Request> requests = Arrays.asList(request);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findFirst50ByStatusOrderByIdDesc(Status.OPEN)).thenReturn(requests);
        WrapperResponse wrapperResponse = requestService.getHomeRequests();

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllCreatedRequests() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.OPEN);
        request.setUser(user);
        Page<Request> requests = new PageImpl(Arrays.asList(request));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findAllByUserId(1L, PageRequest.of(0, 10, Sort.by("lastModifiedDate").descending()))).thenReturn(requests);
        PagingWrapperResponse wrapperResponse = requestService.getAllCreatedRequests(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getRequestDoing() throws NotFoundException {
        RequestAnnouncement requestAnnouncement = new RequestAnnouncement();
        requestAnnouncement.setStatus(Status.PENDING);
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findRequestById(1L)).thenReturn(generateRequest(Status.DOING));
        when(requestFollowingRepository.existsByMentorIdAndRequestIdAndStatus(any(), any(), any())).thenReturn(true);
        when(mentorRequestRepository.existsByRequestIdAndMentorIdAndStatus(any(), any(), any())).thenReturn(true);
        when(requestAnnouncementRepository.findByRequestId(1L)).thenReturn(Arrays.asList(requestAnnouncement));
        RequestResponse requestResponse = requestService.getRequest(1L);

        assertEquals(1, (long) requestResponse.getId());
    }

    private Request generateRequest(Status status) {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
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
        MentorRequest mentorRequest = new MentorRequest();
        mentorRequest.setMentor(mentor);
        mentorRequest.setStatus(Status.DOING);
        Skill skill = new Skill();
        skill.setName("Java");
        skill.setId(1L);
        Request request = new Request();
        request.setId(1L);
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStartDoingTime(LocalDateTime.now());
        request.setStatus(status);
        request.setSkills(new HashSet<>(Arrays.asList(skill)));
        request.setUser(user);
        request.setMentorRequests(new HashSet<>(Arrays.asList(mentorRequest)));
        return request;
    }

    @Test
    public void updateRequest() throws NotFoundException, MoneyRelatedException {
        RequestRequest requestRequest = new RequestRequest();
        requestRequest.setTitle("Thuê Mentor");
        Request request = new Request();
        request.setTitle("Thuê mentor");

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(request);
        when(skillRepository.existsAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE)).thenReturn(true);

        requestService.updateRequest(1L, requestRequest);
        verify(requestRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void createRequest() throws NotFoundException, MoneyRelatedException {
        RequestRequest requestRequest = new RequestRequest();
        requestRequest.setTitle("Thuê Mentor");
        Request request = new Request();
        request.setId(1L);
        request.setTitle("Thuê mentor");
        UserDetail userDetail = new UserDetail();
        userDetail.setTotalRequestCreate(1);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(skillRepository.existsAllByIdInAndStatus(requestRequest.getSkillIds(), Status.ACTIVE)).thenReturn(true);
        when(userDetailRepository.findUserDetailByUserId(1L)).thenReturn(userDetail);
        when(requestRepository.saveAndFlush(any())).thenReturn(request);

        requestService.createRequest(requestRequest);
        verify(requestRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void closeRequest() throws NotFoundException {
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findRequestByIdAndUserIdAndStatus(1L, 1L, Status.OPEN)).thenReturn(new Request());

        requestService.closeRequest(1L);
        verify(requestRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void reopenRequest() throws NotFoundException {
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findRequestByIdAndUserIdAndStatus(1L, 1L, Status.DELETE)).thenReturn(new Request());

        requestService.reopenRequest(1L);
        verify(requestRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void followOrUnfollowRequest() throws NotFoundException {
        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findByIdAndStatusNot(1L, Status.DELETE)).thenReturn(new Request());
        when(mentorRepository.findByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(new Mentor());

        requestService.followOrUnfollowRequest(1L);
        verify(requestFollowingRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void unfollowAllRequest() {
        when(jwtUtils.getUserId()).thenReturn(1L);
        requestService.unfollowAllRequest();
        verify(requestFollowingRepository, times(1)).unfollowAllRequest(Status.DELETE, Status.ACTIVE, 1L);
    }

    @Test
    public void getFollowingRequests() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.ACTIVE);
        request.setUser(user);
        RequestFollowing requestFollowing = new RequestFollowing();
        requestFollowing.setRequest(request);
        Page<RequestFollowing> requestFollowings = new PageImpl(Arrays.asList(requestFollowing));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestFollowingRepository.findAllByMentorIdAndStatusOrderByLastModifiedDateDesc(1L, Status.ACTIVE, PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(requestFollowings);
        PagingWrapperResponse wrapperResponse = requestService.getFollowingRequests(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test()
    public void filterRequest() {
//        FilterRequestWrapperRequest filterRequestWrapperRequest = new FilterRequestWrapperRequest();
//
//        User user = new User();
//        user.setUsername("account1");
//        user.setEmail("account1@gmail.com");
//        user.setCreatedDate(LocalDateTime.now());
//        UserDetail userDetail = new UserDetail();
//        userDetail.setAvatar("avatar");
//        userDetail.setFullName("Dung Do");
//        userDetail.setDateOfBirth(LocalDateTime.now());
//        userDetail.setPhone("012345678");
//        userDetail.setGender(true);
//        userDetail.setCreatedDate(LocalDateTime.now());
//        user.setUserDetail(userDetail);
//        Request request = new Request();
//        request.setDeadline(LocalDateTime.now());
//        request.setCreatedDate(LocalDateTime.now());
//        request.setStatus(Status.ACTIVE);
//        request.setUser(user);
//        Page<Request> requests = new PageImpl(Arrays.asList(request));
//
//        when(jwtUtils.getUserId()).thenReturn(1L);
//        when(requestRepository.findAll((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction(), PageRequest.of(0, 10))).thenReturn(requests);
//        PagingWrapperResponse wrapperResponse = requestService.filterRequest(filterRequestWrapperRequest);
    }

    @Test
    public void getOtherRequests() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.ACTIVE);
        request.setUser(user);
        List<Request> requests = Arrays.asList(request);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(requestRepository.findOtherRequests(2L, Status.OPEN.toString())).thenReturn(requests);
        WrapperResponse wrapperResponse = requestService.getOtherRequests(2L);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getReceivedRequest() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setId(1L);
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.ACTIVE);
        request.setUser(user);
        Page<Request> requests = new PageImpl(Arrays.asList(request));
        MentorRequest mentorRequest = new MentorRequest();
        mentorRequest.setRequest(request);
        List<MentorRequest> mentorRequests = Arrays.asList(mentorRequest);
        List<Status> acceptedStatuses = Lists.newArrayList(Status.DOING, Status.COMPLETE);

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(mentorRequestRepository.findAllByMentorIdAndStatusIn(1L, acceptedStatuses)).thenReturn(mentorRequests);
        when(requestRepository.findAllByIdIn(Arrays.asList(1L), PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(requests);

        PagingWrapperResponse wrapperResponse = requestService.getReceivedRequest(1, 10);

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllAdminRequest() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
        UserDetail userDetail = new UserDetail();
        userDetail.setAvatar("avatar");
        userDetail.setFullName("Dung Do");
        userDetail.setDateOfBirth(LocalDateTime.now());
        userDetail.setPhone("012345678");
        userDetail.setGender(true);
        userDetail.setCreatedDate(LocalDateTime.now());
        user.setUserDetail(userDetail);
        Request request = new Request();
        request.setId(1L);
        request.setDeadline(LocalDateTime.now());
        request.setCreatedDate(LocalDateTime.now());
        request.setStatus(Status.ACTIVE);
        request.setUser(user);
        Page<Request> requests = new PageImpl(Arrays.asList(request));

        when(requestRepository.findAllByTitleContaining("key", PageRequest.of(0, 10, Constants.DEFAULT_ORDER))).thenReturn(requests);
        PagingWrapperResponse wrapperResponse = requestService.getAllAdminRequest(false, 1, 10, "key");

        assertEquals(1, wrapperResponse.getData().size());
    }

    @Test
    public void getAllRentMentors() {
        User user = new User();
        user.setUsername("account1");
        user.setEmail("account1@gmail.com");
        user.setCreatedDate(LocalDateTime.now());
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
        Page<Mentor> mentors = new PageImpl(Arrays.asList(mentor));

        when(jwtUtils.getUserId()).thenReturn(1L);
        when(mentorFollowingRepository.getListMentorIdByUserId(1L)).thenReturn(new HashSet<>(Arrays.asList(1L)));
        when(mentorRepository.findAllRentMentorsByUserIdAndStatus(1L, Status.COMPLETE.toString(), PageRequest.of(0, 10, Sort.by("last_modified_date").descending()))).thenReturn(mentors);

        PagingWrapperResponse wrapperResponse = requestService.getAllRentMentors(1, 10);
        assertEquals(1, wrapperResponse.getData().size());
    }
}