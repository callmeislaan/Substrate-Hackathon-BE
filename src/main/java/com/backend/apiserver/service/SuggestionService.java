package com.backend.apiserver.service;

import com.backend.apiserver.bean.response.WrapperResponse;

import java.util.List;

public interface SuggestionService {
    /**
     * Get all anest mentor suggestion
     *
     * @param skillIds
     * @return ResponseWrapper
     */
    WrapperResponse getAllAnestMentorSuggestion(List<Long> skillIds);

    /**
     * Get all following mentor suggestion
     *
     * @param skillIds
     * @return ResponseWrapper
     */
    WrapperResponse getAllFollowingMentorSuggestion(List<Long> skillIds);

    /**
     * Get all hired mentor suggestion
     *
     * @param skillIds
     * @return ResponseWrapper
     */
    WrapperResponse getAllHiredMentorSuggestion(List<Long> skillIds);

    /**
     * Get all best mentor suggestion
     *
     * @param skillIds
     * @return ResponseWrapper
     */
    WrapperResponse getAllBestMentorSuggestion(List<Long> skillIds);
}
