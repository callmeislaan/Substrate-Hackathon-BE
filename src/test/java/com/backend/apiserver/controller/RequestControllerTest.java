package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.FilterRequestRequest;
import com.backend.apiserver.bean.request.FilterRequestWrapperRequest;
import com.backend.apiserver.bean.request.RequestRequest;
import com.backend.apiserver.bean.response.FollowingResponse;
import com.backend.apiserver.bean.response.MentorShortDescriptionResponse;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestResponse;
import com.backend.apiserver.bean.response.SkillResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.RequestService;
import com.backend.apiserver.controller.RequestController;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class RequestControllerTest {

	@InjectMocks
	RequestController requestController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private RequestService requestService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(requestController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getHomeRequests() throws Exception {
		when(requestService.getHomeRequests()).thenReturn((WrapperResponse) generateHomeRequests(false));
		mockMvc.perform(get("/api/public/requests"))
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

	@Test
	public void filterRequest() throws Exception {
		when(requestService.filterRequest(any())).thenReturn((PagingWrapperResponse) generateHomeRequests(true));
		mockMvc.perform(post("/api/public/requests/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateFilterRequest())))
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

	private FilterRequestWrapperRequest generateFilterRequest() {
		FilterRequestWrapperRequest filterRequestWrapperRequest = new FilterRequestWrapperRequest();
		filterRequestWrapperRequest.setPage(1);
		filterRequestWrapperRequest.setSize(10);
		filterRequestWrapperRequest.setKeyWord("request");
		filterRequestWrapperRequest.setOrder("ascending");
		filterRequestWrapperRequest.setSort("name");
		FilterRequestRequest filterRequestRequest = new FilterRequestRequest();
		filterRequestRequest.setMaxPrice(1500000);
		filterRequestRequest.setMinPrice(200000);
		filterRequestRequest.setSkillIds(Arrays.asList(1L, 2L, 3L));
		filterRequestRequest.setStatus("OPEN");
		filterRequestWrapperRequest.setFilter(filterRequestRequest);
		return filterRequestWrapperRequest;
	}

	@Test
	public void getAllUserRequests() throws Exception {
		when(requestService.getAllCreatedRequests(1, 10)).thenReturn((PagingWrapperResponse) generateHomeRequests(true));
		mockMvc.perform(get("/api/user/requests"))
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
	public void getOtherRequest() throws Exception {
		when(requestService.getOtherRequests(5L)).thenReturn((WrapperResponse) generateHomeRequests(false));
		mockMvc.perform(get("/api/public/request/other-requests/5"))
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
	public void getRequest() throws Exception {
		when(requestService.getRequest(15L)).thenReturn(generateRequestResponse());
		mockMvc.perform(get("/api/public/request/15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.deadline", is(235465466456L)))
				.andExpect(jsonPath("$.content", is("Cần nhờ người làm hộ shopping cart java web")))
				.andExpect(jsonPath("$.price", is(200000)))
				.andExpect(jsonPath("$.title", is("Cần người làm shopping cart")))
				.andExpect(jsonPath("$.status", is("DOING")))
				.andExpect(jsonPath("$.createdDate", is(65646546646L)))
				.andExpect(jsonPath("$.bookmarked", is(true)))
				.andExpect(jsonPath("$.reserved", is(true)))
				.andExpect(jsonPath("$.skills[0].id", is(1)))
				.andExpect(jsonPath("$.skills[0].name", is("JAVA1")))
				.andExpect(jsonPath("$.skills[1].id", is(2)))
				.andExpect(jsonPath("$.skills[1].name", is("JAVA2")));
	}

	@Test
	public void getRequest_ErrorThrowNotFoundException() throws Exception {
		when(requestService.getRequest(15L)).thenThrow(NotFoundException.class);
		mockMvc.perform(get("/api/public/request/15"))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	private RequestResponse generateRequestResponse() {
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
		return requestResponse;
	}

	@Test
	public void createRequest() throws Exception {
		mockMvc.perform(post("/api/user/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(jsonPath("$.code", is("124")))
				.andExpect(status().isOk());
	}

	@Test
	public void createRequest_ErrorThrowNotFoundException() throws Exception {
		when(requestService.createRequest(any())).thenThrow(NotFoundException.class);
		mockMvc.perform(post("/api/user/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createRequest_ErrorThrowMoneyRelatedException() throws Exception {
		when(requestService.createRequest(any())).thenThrow(MoneyRelatedException.class);
		mockMvc.perform(post("/api/user/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(status().isBadRequest());
	}

	private RequestRequest generateRequestRequest() {
		RequestRequest requestRequest = new RequestRequest();
		requestRequest.setContent("Cần người dạy MAS");
		requestRequest.setDeadline(65412354135L);
		requestRequest.setPrice(150000);
		requestRequest.setContent("Cần người dạy MAS pas cuối kì");
		requestRequest.setSkillIds(Arrays.asList(1L, 2L));
		return requestRequest;
	}

	@Test
	public void updateRequest() throws Exception {
		mockMvc.perform(put("/api/user/request/120")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(jsonPath("$.code", is("125")))
				.andExpect(status().isOk());
	}

	@Test
	public void updateRequest_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(requestService).updateRequest(any(), any());
		mockMvc.perform(put("/api/user/request/123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateRequest_ErrorThrowMoneyRelatedException() throws Exception {
		doThrow(new MoneyRelatedException()).when(requestService).updateRequest(any(), any());
		mockMvc.perform(put("/api/user/request/123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(generateRequestRequest())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteRequest() throws Exception {
		mockMvc.perform(delete("/api/user/request/120"))
				.andExpect(jsonPath("$.code", is("126")))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteRequest_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(requestService).closeRequest(any());
		mockMvc.perform(delete("/api/user/request/120"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void reopenRequest() throws Exception {
		mockMvc.perform(post("/api/user/reopen-request/122"))
				.andExpect(jsonPath("$.code", is("150")))
				.andExpect(status().isOk());
	}

	@Test
	public void reopenRequest_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException()).when(requestService).reopenRequest(any());
		mockMvc.perform(post("/api/user/reopen-request/123"))
				.andExpect(jsonPath("$.code", is("123")))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void followOrUnfollowRequest() throws Exception {
		when(requestService.followOrUnfollowRequest(any())).thenReturn(generateFollowingResponse());
		mockMvc.perform(post("/api/request/following-or-unfollowing/212"))
				.andExpect(jsonPath("$.followingStatus", is("FOLLOWING")))
				.andExpect(status().isOk());
	}

	private FollowingResponse generateFollowingResponse() {
		FollowingResponse followingResponse = new FollowingResponse();
		followingResponse.setFollowingStatus("FOLLOWING");
		return followingResponse;
	}

	@Test
	public void unfollowAllRequest() throws Exception {
		mockMvc.perform(post("/api/request/unfollowing-all"))
				.andExpect(status().isOk());
	}

	@Test
	public void getFollowingRequests() throws Exception {
		when(requestService.getFollowingRequests(1, 10)).thenReturn((PagingWrapperResponse) generateHomeRequests(true));
		mockMvc.perform(get("/api/request/following-requests"))
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
	public void getReceivedRequest() throws Exception {
		when(requestService.getReceivedRequest(1, 10)).thenReturn((PagingWrapperResponse) generateHomeRequests(true));
		mockMvc.perform(get("/api/request/mentor/receive-request"))
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
	public void getListRentMentors() throws Exception {
		when(requestService.getAllRentMentors(1,10)).thenReturn((PagingWrapperResponse) generateMentors(true, true));
		mockMvc.perform(get("/api/hiring/rent-mentors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(1)))
				.andExpect(jsonPath("$.data[0].anestMentor", is(true)))
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