package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.ListSkillIdRequest;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.service.SuggestionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class SuggestionController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private SuggestionService suggestionService;

    @PostMapping("api/anest-mentor-suggestion")
    public WrapperResponse getAllAnestMentorSuggestion(@Valid @RequestBody ListSkillIdRequest listSkillIdRequest) {
        LOG.info("Start to find all anest mentor suggestion");
        WrapperResponse wrapperResponse = suggestionService.getAllAnestMentorSuggestion(listSkillIdRequest.getSkillIds());
        LOG.info("End to find all anest mentor suggestion");
        return wrapperResponse;
    }

    @PostMapping("api/following-mentor-suggestion")
    public WrapperResponse getAllFollowingMentorSuggestion(@Valid @RequestBody ListSkillIdRequest listSkillIdRequest) {
        LOG.info("Start to find all following mentor suggestion");
        WrapperResponse wrapperResponse = suggestionService.getAllFollowingMentorSuggestion(listSkillIdRequest.getSkillIds());
        LOG.info("End to find all following mentor suggestion");
        return wrapperResponse;
    }

    @PostMapping("api/hired-mentor-suggestion")
    public WrapperResponse getAllHiredMentorSuggestion(@Valid @RequestBody ListSkillIdRequest listSkillIdRequest) {
        LOG.info("Start to find all hired mentor suggestion");
        WrapperResponse wrapperResponse = suggestionService.getAllHiredMentorSuggestion(listSkillIdRequest.getSkillIds());
        LOG.info("End to find all hired mentor suggestion");
        return wrapperResponse;
    }

    @PostMapping("api/best-mentor-suggestion")
    public WrapperResponse getAllBestMentorSuggestion(@Valid @RequestBody ListSkillIdRequest listSkillIdRequest) {
        LOG.info("Start to find all best mentor suggestion");
        WrapperResponse wrapperResponse = suggestionService.getAllBestMentorSuggestion(listSkillIdRequest.getSkillIds());
        LOG.info("End to find all best mentor suggestion");
        return wrapperResponse;
    }
}
