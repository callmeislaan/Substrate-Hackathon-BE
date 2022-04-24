package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.FilterMentorWrapperRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.IncomeReportResponse;
import com.backend.apiserver.bean.response.MentorOverviewResponse;
import com.backend.apiserver.bean.response.MentorResumeResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;

public interface MentorService {
    /**
     * Get all home mentor
     *
     * @return ResponseWrapper
     */
    WrapperResponse getAllHomeMentor(boolean isAnestMentor);

    /**
     * Using to unregister mentor member for user
     */
    void unregisterMentor() throws ForbiddenException;

    /**
     * Get mentor overview
     *
     * @return MentorOverviewResponse
     */
    MentorOverviewResponse getMentorOverview();

    /**
     * Get mentor income reports
     *
     * @return MentorOverviewResponse
     */
    IncomeReportResponse getIncomeReport();

    /**
     * Get comments for mentor CV
     *
     * @param userId
     * @return ResponseWrapper
     */
    PagingWrapperResponse getMentorComments(Long userId, Integer page, Integer size) throws NotFoundException;

    /**
     * Find CV information for mentor only
     *
     * @param userId
     * @return MentorResumeResponse
     * @throws NotFoundException
     */
    MentorResumeResponse getMentorResume(Long userId) throws NotFoundException;

    /**
     * Find mentor achievement information for mentor only
     *
     * @param userId
     * @return ResponseWrapper
     * @throws NotFoundException
     */
    WrapperResponse getMentorAchievements(Long userId) throws NotFoundException;

    /**
     * Follow or unfollow a mentor
     *
     * @param mentorId
     * @return FollowingResponse
     */
    FollowingResponse followOrUnfollowMentor(Long mentorId) throws NotFoundException;

    /**
     * Unfollow all mentor
     */
    void unfollowAllMentor();

    /**
     * get all following mentor
     */
    PagingWrapperResponse getFollowingMentors(Integer page, Integer size);

    /**
     * filter mentor
     *
     * @param filterMentorWrapperRequest
     * @return PagingWrapperResponse
     */
    PagingWrapperResponse filterMentor(FilterMentorWrapperRequest filterMentorWrapperRequest);

	void updateMentorInfo(RegisterMentorRequest registerMentorRequest) throws UserNotFoundException, SkillNotFoundException;

	void setAnestMentor(Long mentorId) throws MentorNotFoundException;

    PagingWrapperResponse viewMentors(Integer page, Integer size, String keyword);
}
