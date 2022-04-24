package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestAdminResponse;
import com.backend.apiserver.controller.admin.AdminUserController;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.MentorService;
import com.backend.apiserver.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringJUnit4ClassRunner.class)
public class AdminUserControllerTest {

	@InjectMocks
	AdminUserController adminUserController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private UserService userService;
	@Mock
	private MentorService mentorService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminUserController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void viewUsers() throws Exception {
		List<RequestAdminResponse> adminResponses = new ArrayList<>();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(adminResponses, 1);
		when(userService.viewUsers(1, 10, null)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/users"))
				.andExpect(status().isOk());
	}

	@Test
	public void viewMentors() throws Exception {
		List<RequestAdminResponse> adminResponses = new ArrayList<>();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(adminResponses, 1);
		when(mentorService.viewMentors(1, 10, null)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/mentors"))
				.andExpect(status().isOk());
	}

	@Test
	public void setAnestMentor() throws Exception {
		mockMvc.perform(post("/api/admin/mentor/15"))
				.andExpect(status().isOk());
	}

	@Test
	public void setAnestMentor_ThrowNotFoundException() throws Exception {
		doThrow(new MentorNotFoundException("")).when(mentorService).setAnestMentor(15L);
		mockMvc.perform(post("/api/admin/mentor/15"))
				.andExpect(status().isBadRequest());
	}
}