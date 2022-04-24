package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.ListSkillIdRequest;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.SuggestionService;
import com.backend.apiserver.controller.SuggestionController;
import com.backend.apiserver.utils.FormatUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
public class SuggestionControllerTest {

	@InjectMocks
	private SuggestionController suggestionController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private SuggestionService suggestionService;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.standaloneSetup(suggestionController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getAllAnestMentorSuggestion() throws Exception {
		List<Long> skillIds = new ArrayList<>();
		ListSkillIdRequest listSkillIdRequest = new ListSkillIdRequest();
		listSkillIdRequest.setSkillIds(skillIds);

		MentorShortDescriptionResponse mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		WrapperResponse wrapperResponse = new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));

		when(suggestionService.getAllAnestMentorSuggestion(anyList())).thenReturn(wrapperResponse);

		mockMvc.perform(post("/api/anest-mentor-suggestion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(listSkillIdRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getAllFollowingMentorSuggestion() throws Exception {

		List<Long> skillIds = new ArrayList<>();
		ListSkillIdRequest listSkillIdRequest = new ListSkillIdRequest();
		listSkillIdRequest.setSkillIds(skillIds);

		MentorShortDescriptionResponse mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		WrapperResponse wrapperResponse = new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));

		when(suggestionService.getAllFollowingMentorSuggestion(anyList())).thenReturn(wrapperResponse);

		mockMvc.perform(post("/api/following-mentor-suggestion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(listSkillIdRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}

	@Test
	public void getAllHiredMentorSuggestion() throws Exception {

		List<Long> skillIds = new ArrayList<>();
		ListSkillIdRequest listSkillIdRequest = new ListSkillIdRequest();
		listSkillIdRequest.setSkillIds(skillIds);

		MentorShortDescriptionResponse mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		WrapperResponse wrapperResponse = new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));

		when(suggestionService.getAllHiredMentorSuggestion(anyList())).thenReturn(wrapperResponse);

		mockMvc.perform(post("/api/hired-mentor-suggestion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(listSkillIdRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));

	}

	@Test
	public void getAllBestMentorSuggestion() throws Exception {
		List<Long> skillIds = new ArrayList<>();
		ListSkillIdRequest listSkillIdRequest = new ListSkillIdRequest();
		listSkillIdRequest.setSkillIds(skillIds);

		MentorShortDescriptionResponse mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		WrapperResponse wrapperResponse = new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));

		when(suggestionService.getAllBestMentorSuggestion(anyList())).thenReturn(wrapperResponse);

		mockMvc.perform(post("/api/best-mentor-suggestion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(listSkillIdRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)));
	}
}