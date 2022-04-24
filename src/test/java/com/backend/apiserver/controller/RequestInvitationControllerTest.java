package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.RequestInvitationService;
import com.backend.apiserver.controller.RequestInvitationController;
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

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
public class RequestInvitationControllerTest {

	@InjectMocks
	RequestInvitationController requestInvitationController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private RequestInvitationService requestInvitationService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(requestInvitationController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void inviteMentor() throws Exception {
		mockMvc.perform(post("/api/request/invite-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRentMentor())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	private RentMentorRequest generateRentMentor() {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setMentorId(15L);
		rentMentorRequest.setRequestId(12L);
		return rentMentorRequest;
	}

	@Test
	public void inviteMentor_ErrorThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestInvitationService).inviteMentor(any());
		mockMvc.perform(post("/api/request/invite-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRentMentor())))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void inviteMentor_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new MentorNotFoundException()).when(requestInvitationService).inviteMentor(any());
		mockMvc.perform(post("/api/request/invite-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRentMentor())))
				.andExpect(jsonPath("$.code", is("130")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void findInvitationRequests() throws Exception {
		when(requestInvitationService.findInvitationRequests(1, 10)).thenReturn((PagingWrapperResponse) generateHomeRequests(true));
		mockMvc.perform(get("/api/request/invitation-requests"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].deadline", is(235465466456L)))
				.andExpect(jsonPath("$.data[0].content", is("Cần nhờ người làm hộ shopping cart java web")))
				.andExpect(jsonPath("$.data[0].price", is(200000)))
				.andExpect(jsonPath("$.data[0].title", is("Cần người làm shopping cart")))
				.andExpect(jsonPath("$.data[0].status", is("DOING")))
				.andExpect(jsonPath("$.data[0].createdDate", is(65646546646L)))
				.andExpect(jsonPath("$.data[0].bookmarked", is(true)))
				.andExpect(jsonPath("$.data[0].reserved", is(true)))
				.andExpect(jsonPath("$.data[0].skills[0].id", is(1)))
				.andExpect(jsonPath("$.data[0].skills[0].name", is("JAVA1")))
				.andExpect(jsonPath("$.data[0].skills[1].id", is(2)))
				.andExpect(jsonPath("$.data[0].skills[1].name", is("JAVA2")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.id", is(5)))
				.andExpect(jsonPath("$.data[0].userInfoResponse.username", is("kafka2405")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.email", is("kafka@gmail.com")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.avatar", is("")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.createdDate", is(5246545645656L)))
				.andExpect(jsonPath("$.data[0].userInfoResponse.dateOfBirth", is(6356512613654L)))
				.andExpect(jsonPath("$.data[0].userInfoResponse.phone", is("0969563145")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.fullName", is("ĐẶNG ĐÌNH QUYỀN")))
				.andExpect(jsonPath("$.data[0].userInfoResponse.gender", is(true)))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.id", is(5)))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.username", is("kafka2405")))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.email", is("kafka@gmail.com")))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.avatar", is("")))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.createdDate", is(5246545645656L)))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.dateOfBirth", is(6356512613654L)))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.phone", is("0969563145")))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.fullName", is("ĐẶNG ĐÌNH QUYỀN")))
				.andExpect(jsonPath("$.data[0].mentorInfoResponse.gender", is(true)));
	}

	@Test
	public void deleteInvitation() throws Exception {
		mockMvc.perform(delete("/api/request/delete-invitation/111"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	@Test
	public void deleteInvitation_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(requestInvitationService).deleteInvitation(any());
		mockMvc.perform(delete("/api/request/delete-invitation/111"))
				.andExpect(jsonPath("$.code", is("152")))
				.andExpect(status().isBadRequest());
	}

	private Object generateHomeRequests(boolean isPagination) {
		RequestResponse requestResponse = new RequestResponse();
		requestResponse.setId(5L);
		requestResponse.setTitle("Cần người làm shopping cart");
		requestResponse.setContent("Cần nhờ người làm hộ shopping cart java web");
		requestResponse.setDeadline(235465466456L);
		requestResponse.setStatus("DOING");
		requestResponse.setPrice(200000);
		requestResponse.setCreatedDate(65646546646L);
		requestResponse.setBookmarked(true);
		requestResponse.setReserved(true);
		requestResponse.setSkills(
				LongStream.rangeClosed(1, 3)
						.mapToObj(skill -> {
							SkillResponse skillResponse = new SkillResponse();
							skillResponse.setId(skill);
							skillResponse.setName("JAVA" + skill);
							return skillResponse;
						})
						.collect(Collectors.toList())
		);
		requestResponse.setUserInfoResponse(generateUserProfileResponse());
		requestResponse.setMentorInfoResponse(generateUserProfileResponse());
		if (isPagination) {
			return new PagingWrapperResponse(Arrays.asList(requestResponse), 1);
		} else {
			return new WrapperResponse(Arrays.asList(requestResponse));
		}
	}

	private UserProfileResponse generateUserProfileResponse() {
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		userProfileResponse.setId(5L);
		userProfileResponse.setUsername("kafka2405");
		userProfileResponse.setEmail("kafka@gmail.com");
		userProfileResponse.setAvatar("");
		userProfileResponse.setCreatedDate(5246545645656L);
		userProfileResponse.setDateOfBirth(6356512613654L);
		userProfileResponse.setPhone("0969563145");
		userProfileResponse.setFullName("ĐẶNG ĐÌNH QUYỀN");
		userProfileResponse.setGender(true);
		return userProfileResponse;
	}
}