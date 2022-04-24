package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.FinishRequestRequest;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.RequestAnnouncementNotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.RequestConfirmationService;
import com.backend.apiserver.controller.RequestConfirmationController;
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
public class RequestConfirmationControllerTest {

	@InjectMocks
	RequestConfirmationController requestConfirmationController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private RequestConfirmationService requestConfirmationService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(requestConfirmationController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void confirmFinishRequest() throws Exception {
		mockMvc.perform(post("/api/request/user/confirm-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("140")))
				.andExpect(status().isOk());
	}

	@Test
	public void confirmFinishRequest_ErrorThrowMentorRequestNotFoundException() throws Exception {
		doThrow(new MentorRequestNotFoundException()).when(requestConfirmationService).confirmFinishRequest(any());
		mockMvc.perform(post("/api/request/user/confirm-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("134")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void confirmFinishRequest_ErrorThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestConfirmationService).confirmFinishRequest(any());
		mockMvc.perform(post("/api/request/user/confirm-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	private FinishRequestRequest generateFinishRequest() {
		FinishRequestRequest finishRequestRequest = new FinishRequestRequest();
		finishRequestRequest.setRequestId(5L);
		finishRequestRequest.setComment("Could not detect default resource locations for test class");
		finishRequestRequest.setRating(4);
		return finishRequestRequest;
	}

	@Test
	public void confirmNotFinishRequest() throws Exception {
		mockMvc.perform(post("/api/request/user/confirm-not-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("141")))
				.andExpect(status().isOk());
	}

	@Test
	public void confirmNotFinishRequest_ErrorThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestConfirmationService).confirmNotFinishRequest(any());
		mockMvc.perform(post("/api/request/user/confirm-not-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void confirmNotFinishRequest_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new MentorNotFoundException()).when(requestConfirmationService).confirmNotFinishRequest(any());
		mockMvc.perform(post("/api/request/user/confirm-not-finish-request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFinishRequest())))
				.andExpect(jsonPath("$.code", is("130")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void mentorConfirmNotFinishRequest() throws Exception {
		mockMvc.perform(post("/api/request/mentor/confirm-not-finish-request/125"))
				.andExpect(jsonPath("$.code", is("142")))
				.andExpect(status().isOk());
	}

	@Test
	public void mentorConfirmNotFinishRequest_ErrorThrowRequestAnnouncementNotFoundException() throws Exception {
		doThrow(new RequestAnnouncementNotFoundException()).when(requestConfirmationService).mentorConfirmNotFinishRequest(any());
		mockMvc.perform(post("/api/request/mentor/confirm-not-finish-request/15"))
				.andExpect(jsonPath("$.code", is("152")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void mentorConfirmNotFinishRequest_ErrorThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestConfirmationService).mentorConfirmNotFinishRequest(any());
		mockMvc.perform(post("/api/request/mentor/confirm-not-finish-request/15"))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void mentorConfirmNotFinishRequest_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new MentorNotFoundException()).when(requestConfirmationService).mentorConfirmNotFinishRequest(any());
		mockMvc.perform(post("/api/request/mentor/confirm-not-finish-request/15"))
				.andExpect(jsonPath("$.code", is("130")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void mentorConfirmFinishRequest() throws Exception {
		mockMvc.perform(post("/api/request/mentor/confirm-finish-request/125"))
				.andExpect(jsonPath("$.code", is("143")))
				.andExpect(status().isOk());
	}
}