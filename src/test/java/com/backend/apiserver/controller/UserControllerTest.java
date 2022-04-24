package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.AchievementRequest;
import com.backend.apiserver.bean.request.MentorSkillRequest;
import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.request.UpdatePasswordRequest;
import com.backend.apiserver.bean.request.UseAnestCardRequest;
import com.backend.apiserver.bean.request.UserProfileRequest;
import com.backend.apiserver.bean.response.UserFinanceOverviewResponse;
import com.backend.apiserver.bean.response.UserOverviewResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.PasswordNotMatchException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.UserService;
import com.backend.apiserver.controller.UserController;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

	@InjectMocks
	UserController userController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(userController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getUserProfile() throws Exception {
		when(userService.getProfile()).thenReturn(generateUserProfile());
		mockMvc.perform(get("/api/user/profile"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is("kafka2405")))
				.andExpect(jsonPath("$.fullName", is("Đặng Đình Quyền")));
	}

	@Test
	public void getUserOverview() throws Exception {
		when(userService.getUserOverview()).thenReturn(generateUserOverview());
		mockMvc.perform(get("/api/user/overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalPeopleHired", is(15)));
	}

	@Test
	public void getUserFinanceOverview() throws Exception {
		UserFinanceOverviewResponse response = new UserFinanceOverviewResponse();
		response.setTotalMoneyCurrent(15);
		when(userService.getUserFinanceOverview()).thenReturn(response);
		mockMvc.perform(get("/api/user/finance-overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalMoneyCurrent", is(15)));
	}

	@Test
	public void registerMentor() throws Exception {
		mockMvc.perform(post("/api/user/register-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentorRequest())))
				.andExpect(status().isOk());
	}

	private RegisterMentorRequest generateRegisterMentorRequest(){
		RegisterMentorRequest registerMentorRequest = new RegisterMentorRequest();
		String job = "a";
		String introduction = "a";
		String skillDescription = "a";
		List<MentorSkillRequest> mentorSkills = new ArrayList<>();
		MentorSkillRequest request = new MentorSkillRequest();
		request.setId(1L);
		request.setValue(2);
		mentorSkills.add(request);
		String service = "a";
		int price = 1000000;
		List<AchievementRequest> achievements = new ArrayList<>();
		AchievementRequest achievement = new AchievementRequest();
		achievement.setTitle("title");
		achievement.setContent("content");
		achievements.add(achievement);

		registerMentorRequest.setAchievements(achievements);
		registerMentorRequest.setJob(job);
		registerMentorRequest.setIntroduction(introduction);
		registerMentorRequest.setSkillDescription(skillDescription);
		registerMentorRequest.setMentorSkills(mentorSkills);
		registerMentorRequest.setService(service);
		registerMentorRequest.setPrice(price);
		return registerMentorRequest;
	}

	@Test
	public void registerMentor_ThrowUserNotFoundException() throws Exception {
		doThrow(new UserNotFoundException()).when(userService).registerMentor(any());
		mockMvc.perform(post("/api/user/register-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentorRequest())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void registerMentor_ThrowRoleNotFoundException() throws Exception {
		doThrow(new RoleNotFoundException()).when(userService).registerMentor(any());
		mockMvc.perform(post("/api/user/register-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentorRequest())))
				.andExpect(status().isBadRequest());
	}


	@Test
	public void registerMentor_ThrowSkillNotFoundException() throws Exception {
		doThrow(new SkillNotFoundException()).when(userService).registerMentor(any());
		mockMvc.perform(post("/api/user/register-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRegisterMentorRequest())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updatePassword() throws Exception {
		UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
		updatePasswordRequest.setNewPassword("");
		updatePasswordRequest.setOldPassword("");
		mockMvc.perform(put("/api/user/update-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(updatePasswordRequest)))
				.andExpect(status().isOk());
	}

	@Test
	public void updatePassword_ThrowPasswordNotMatchException() throws Exception {
		UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
		updatePasswordRequest.setNewPassword("");
		updatePasswordRequest.setOldPassword("");
		doThrow(new PasswordNotMatchException()).when(userService).updatePassword(any());
		mockMvc.perform(put("/api/user/update-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(updatePasswordRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateProfile() throws Exception {
		UserProfileRequest userProfileRequest = new UserProfileRequest();
		mockMvc.perform(put("/api/user/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(userProfileRequest)))
				.andExpect(status().isOk());
	}

	@Test
	public void requestUsingAnestCard() throws Exception {
		mockMvc.perform(post("/api/user/anest-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateAnestCardRequest())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("150")));
	}

	@Test
	public void requestUsingAnestCard_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(userService).requestUsingAnestCard(any());
		mockMvc.perform(post("/api/user/anest-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateAnestCardRequest())))
				.andExpect(jsonPath("$.code", is("155")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestUsingAnestCard_ErrorThrowDataDuplicatedException() throws Exception {
		doThrow(new DataDuplicatedException()).when(userService).requestUsingAnestCard(any());
		mockMvc.perform(post("/api/user/anest-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateAnestCardRequest())))
				.andExpect(jsonPath("$.code", is("156")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestUsingAnestCard_ErrorThrowInvalidDataException() throws Exception {
		doThrow(new InvalidDataException()).when(userService).requestUsingAnestCard(any());
		mockMvc.perform(post("/api/user/anest-card")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateAnestCardRequest())))
				.andExpect(jsonPath("$.code", is("157")))
				.andExpect(status().isBadRequest());
	}

	private UseAnestCardRequest generateAnestCardRequest() {
		UseAnestCardRequest useAnestCardRequest = new UseAnestCardRequest();
		useAnestCardRequest.setSerial("ANEST56465");
		useAnestCardRequest.setCode("dasf342-asdasd3234-asdasdas-jsd4akjsd");
		return useAnestCardRequest;
	}

	private UserProfileResponse generateUserProfile() {
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		userProfileResponse.setUsername("kafka2405");
		userProfileResponse.setFullName("Đặng Đình Quyền");
		return userProfileResponse;
	}

	private UserOverviewResponse generateUserOverview() {
		UserOverviewResponse response = new UserOverviewResponse();
		response.setTotalPeopleHired(15);
		return response;
	}
}