package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.FilterMentorWrapperRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.IncomeReportResponse;
import com.backend.apiserver.bean.response.MentorOverviewResponse;
import com.backend.apiserver.bean.response.MentorResumeResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.service.MentorService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class MentorController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private MentorService mentorService;

    @GetMapping("api/public/home/anest-mentor")
    public WrapperResponse getAllAnestMentor() {
        LOG.info("Start to find all anest mentor");
        WrapperResponse wrapperResponse = mentorService.getAllHomeMentor(true);
        LOG.info("End to find all anest mentor");
        return wrapperResponse;
    }

    @GetMapping("api/public/home/other-mentor")
    public WrapperResponse getAllOtherMentor() {
        LOG.info("Start to find all other mentor");
        WrapperResponse wrapperResponse = mentorService.getAllHomeMentor(false);
        LOG.info("End to find all other mentor");
        return wrapperResponse;
    }

    @Secured("ROLE_MENTOR")
    @PutMapping("api/mentor/unregister-mentor")
    public Response unregisterMentorMember() throws BadRequestException {
        try {
            LOG.info("Start to unregister mentor member for members");
            mentorService.unregisterMentor();
            LOG.info("End to unregister mentor member for members");
        } catch (ForbiddenException e) {
            throw new BadRequestException(ResponseMessage.MentorHaveDoingRequestException);
        }
        return new Response(ResponseMessage.RemoveMentorSuccess);
    }

    @Secured("ROLE_MENTOR")
    @GetMapping("api/mentor/overview")
    public MentorOverviewResponse getMentorOverview() {
        LOG.info("Start to get mentor overview information");
        MentorOverviewResponse mentorOverviewResponse = mentorService.getMentorOverview();
        LOG.info("End to get mentor overview information");
        return mentorOverviewResponse;
    }

    @Secured("ROLE_MENTOR")
    @GetMapping("api/mentor/income-report")
    public IncomeReportResponse getIncomeReport() {
        LOG.info("Start to get mentor overview information");
        IncomeReportResponse incomeReportResponse = mentorService.getIncomeReport();
        LOG.info("End to get mentor overview information");
        return incomeReportResponse;
    }

    @GetMapping("api/public/mentor/resume/{mentorId}")
    public MentorResumeResponse getMentorResume(@PathVariable final Long mentorId) throws BadRequestException {
        try {
            LOG.info("Start to get mentor profile resume information");
            MentorResumeResponse mentorResumeResponse = mentorService.getMentorResume(mentorId);
            LOG.info("End to get mentor profile resume information");
            return mentorResumeResponse;
        } catch (NotFoundException e) {
            throw new BadRequestException(ResponseMessage.MentorRoleNotFound);
        }
    }

    @GetMapping("api/public/mentor/comments/{mentorId}")
    public PagingWrapperResponse getMentorComments(@PathVariable final Long mentorId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) throws BadRequestException {
        try {
            LOG.info("Start to get mentor comments information");
            PagingWrapperResponse wrapperResponse = mentorService.getMentorComments(mentorId, page, size);
            LOG.info("End to get mentor comments  information");
            return wrapperResponse;
        } catch (NotFoundException e) {
            throw new BadRequestException(ResponseMessage.MentorNotFound);
        }
    }

    @GetMapping("api/public/mentor/achievements/{mentorId}")
    public WrapperResponse getMentorAchievements(@PathVariable final Long mentorId) throws BadRequestException {
        try {
            LOG.info("Start to get mentor achievements information");
            WrapperResponse wrapperResponse = mentorService.getMentorAchievements(mentorId);
            LOG.info("End to get mentor achievements information");
            return wrapperResponse;
        } catch (NotFoundException e) {
            throw new BadRequestException(ResponseMessage.MentorRoleNotFound);
        }
    }

    @PostMapping("api/mentor/following-or-unfollowing/{mentorId}")
    public FollowingResponse followOrUnfollowMentor(@PathVariable final Long mentorId) throws NotFoundException {
        LOG.info("Start to follow or unfollow mentor");
        FollowingResponse followingResponse = mentorService.followOrUnfollowMentor(mentorId);
        LOG.info("End to follow or unfollow mentor");
        return followingResponse;
    }

    @PostMapping("api/mentor/unfollowing-all")
    public Response unfollowAllMentor() {
        LOG.info("Start to unfollow all mentor");
        mentorService.unfollowAllMentor();
        LOG.info("End to unfollow all mentor");
        return new Response(ResponseMessage.FollowOrUnfollowSuccess);
    }

    @GetMapping("api/mentor/following-mentors")
    public PagingWrapperResponse getFollowingMentors(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        LOG.info("Start to get following mentor");
        PagingWrapperResponse responseWrapper = mentorService.getFollowingMentors(page, size);
        LOG.info("End to get following mentor");
        return responseWrapper;
    }

    @PostMapping("api/public/mentors/filter")
    public PagingWrapperResponse filterMentor(@Valid @RequestBody FilterMentorWrapperRequest filterMentorWrapperRequest) {
        LOG.info("Start to filter mentor");
        PagingWrapperResponse pagingWrapperResponse = mentorService.filterMentor(filterMentorWrapperRequest);
        LOG.info("End to filter mentor");
        return pagingWrapperResponse;
    }

    @Secured("ROLE_MENTOR")
    @PutMapping("api/mentor")
    public Response updateMentorInfo(@Valid @RequestBody RegisterMentorRequest registerMentorRequest) throws BadRequestException {
        try {
            LOG.info("Start to update mentor information");
            mentorService.updateMentorInfo(registerMentorRequest);
            LOG.info("End to update mentor information");
            return new Response(ResponseMessage.PerformOperationSuccess);
        } catch (UserNotFoundException e) {
            throw new BadRequestException(ResponseMessage.UserRoleNotFound);
        } catch (SkillNotFoundException e) {
            throw new BadRequestException(ResponseMessage.RequestHasInvalidSkill);
        }
    }
}
