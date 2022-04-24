package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.RequestReservationService;
import com.backend.apiserver.controller.RequestReservationController;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class RequestReservationControllerTest {

	@InjectMocks
	RequestReservationController requestReservationController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private RequestReservationService requestReservationService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(requestReservationController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void reserveRequest() throws Exception {
		mockMvc.perform(post("/api/request/reserve-request/123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	@Test
	public void reserveRequest_ErrorThrowDataDuplicatedException() throws Exception {
		doThrow(new DataDuplicatedException()).when(requestReservationService).reserveRequest(any());
		mockMvc.perform(post("/api/request/reserve-request/123"))
				.andExpect(jsonPath("$.code", is("132")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void reserveRequest_ErrorThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestReservationService).reserveRequest(any());
		mockMvc.perform(post("/api/request/reserve-request/123"))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void reserveRequest_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new MentorNotFoundException()).when(requestReservationService).reserveRequest(any());
		mockMvc.perform(post("/api/request/reserve-request/123"))
				.andExpect(jsonPath("$.code", is("130")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void undoReserveRequest() throws Exception {
		mockMvc.perform(delete("/api/request/undo-reserve-request/343"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	@Test
	public void undoReserveRequest_ErrorThrowMentorNotFoundException() throws Exception {
		doThrow(new MentorRequestNotFoundException()).when(requestReservationService).undoReserveRequest(any());
		mockMvc.perform(delete("/api/request/undo-reserve-request/343"))
				.andExpect(jsonPath("$.code", is("134")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void findPendingMentors() throws Exception {
		when(requestReservationService.findPendingMentors(5L)).thenReturn((WrapperResponse) generateMentors(false, false));
		mockMvc.perform(get("/api/request/pending-mentors/5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(false)))
				.andExpect(jsonPath("$.data[0].avatar", is("")))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].gender", is(true)))
				.andExpect(jsonPath("$.data[0].fullName", is("NGUYỄN VĂN NAM")))
				.andExpect(jsonPath("$.data[0].id", is(125)))
				.andExpect(jsonPath("$.data[0].job", is("Java Developer")))
				.andExpect(jsonPath("$.data[0].price", is(250000)))
				.andExpect(jsonPath("$.data[0].rating", is(4.5)))
				.andExpect(jsonPath("$.data[0].username", is("namlm3")))
				.andExpect(jsonPath("$.data[0].totalRequestFinish", is(55)))
				.andExpect(jsonPath("$.data[0].listSkill[0].id", is(5)))
				.andExpect(jsonPath("$.data[0].listSkill[0].name", is("JAVA")))
				.andExpect(jsonPath("$.data[0].listSkill[1].id", is(6)))
				.andExpect(jsonPath("$.data[0].listSkill[1].name", is("C#")));
	}

	@Test
	public void findPendingMentors_ThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestReservationService).findPendingMentors(any());
		mockMvc.perform(get("/api/request/pending-mentors/5"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void rentMentor() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(15L);
		rentMentorRequest.setMentorId(14L);
		mockMvc.perform(post("/api/request/rent-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(rentMentorRequest)))
				.andExpect(jsonPath("$.code", is("135")))
				.andExpect(status().isOk());
	}

	@Test
	public void rentMentor_throwMentorRequestNotFoundException() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(15L);
		rentMentorRequest.setMentorId(14L);
		doThrow(new MentorRequestNotFoundException()).when(requestReservationService).rentMentor(any());
		mockMvc.perform(post("/api/request/rent-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(rentMentorRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void rentMentor_throwRequestNotFoundException() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(15L);
		rentMentorRequest.setMentorId(14L);
		doThrow(new RequestNotFoundException()).when(requestReservationService).rentMentor(any());
		mockMvc.perform(post("/api/request/rent-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(rentMentorRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void rentMentor_throwMoneyRelatedException() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(15L);
		rentMentorRequest.setMentorId(14L);
		doThrow(new MoneyRelatedException()).when(requestReservationService).rentMentor(any());
		mockMvc.perform(post("/api/request/rent-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(rentMentorRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void rentMentor_throwMentorNotFoundException() throws Exception {
		RentMentorRequest rentMentorRequest = new RentMentorRequest();
		rentMentorRequest.setRequestId(15L);
		rentMentorRequest.setMentorId(14L);
		doThrow(new MentorNotFoundException()).when(requestReservationService).rentMentor(any());
		mockMvc.perform(post("/api/request/rent-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(rentMentorRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteRequestReservation() throws Exception {
		mockMvc.perform(delete("/api/request/12/mentor/15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code", is("150")))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRequestReservation_ThrowRequestNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException()).when(requestReservationService).deleteRequestReservation(anyLong(),anyLong());
		mockMvc.perform(delete("/api/request/12/mentor/15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteRequestReservation_throwMentorRequestNotFoundException() throws Exception {
		doThrow(new MentorRequestNotFoundException()).when(requestReservationService).deleteRequestReservation(anyLong(),anyLong());
		mockMvc.perform(delete("/api/request/12/mentor/15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code", is("134")))
				.andExpect(status().isBadRequest());
	}

	private Object generateMentors(boolean isAnestMentor, boolean isPagination) {
		MentorShortDescriptionResponse mentorShortDescriptionResponse;
		mentorShortDescriptionResponse = new MentorShortDescriptionResponse();
		mentorShortDescriptionResponse.setAnestMentor(isAnestMentor);
		mentorShortDescriptionResponse.setAvatar("");
		mentorShortDescriptionResponse.setFullName("NGUYỄN VĂN NAM");
		mentorShortDescriptionResponse.setGender(true);
		mentorShortDescriptionResponse.setId(125L);
		mentorShortDescriptionResponse.setJob("Java Developer");
		mentorShortDescriptionResponse.setPrice(250000);
		mentorShortDescriptionResponse.setRating(4.5F);
		mentorShortDescriptionResponse.setUsername("namlm3");
		mentorShortDescriptionResponse.setTotalRequestFinish(55);
		SkillResponse java = new SkillResponse();
		java.setId(5L);
		java.setName("JAVA");
		SkillResponse cSharp = new SkillResponse();
		cSharp.setId(6L);
		cSharp.setName("C#");
		mentorShortDescriptionResponse.setListSkill(Arrays.asList(java, cSharp));
		if (isPagination) return new PagingWrapperResponse(Arrays.asList(mentorShortDescriptionResponse), 1);
		else return new WrapperResponse(Arrays.asList(mentorShortDescriptionResponse));
	}
}