package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.RequestAdminResponse;
import com.backend.apiserver.controller.admin.AdminRequestController;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.RequestConfirmationService;
import com.backend.apiserver.service.RequestService;
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
public class AdminRequestControllerTest {

	@InjectMocks
	AdminRequestController adminRequestController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private RequestService requestService;
	@Mock
	private RequestConfirmationService requestConfirmationService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminRequestController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void viewAllRequests() throws Exception {
		List<RequestAdminResponse> adminResponses = new ArrayList<>();
		PagingWrapperResponse pagingWrapperResponse = new PagingWrapperResponse(adminResponses, 1);
		when(requestService.getAllAdminRequest(true, 1, 10, null)).thenReturn(pagingWrapperResponse);
		mockMvc.perform(get("/api/admin/requests"))
				.andExpect(status().isOk());
	}

	@Test
	public void resolveConflict() throws Exception {
		mockMvc.perform(post("/api/admin/resolve-conflict/15")
				.param("forUser", "true"))
				.andExpect(status().isOk());
	}

	@Test
	public void resolveConflict_throwNotFoundException() throws Exception {
		doThrow(new RequestNotFoundException("")).when(requestConfirmationService).resolveConflict(15L, true);
		mockMvc.perform(post("/api/admin/resolve-conflict/15")
				.param("forUser", "true"))
				.andExpect(status().isBadRequest());
	}
}