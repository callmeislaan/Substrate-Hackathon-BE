package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.DashboardResponse;
import com.backend.apiserver.controller.admin.AdminDashboardController;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.DashboardService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdminDashboardControllerTest {

	@InjectMocks
	AdminDashboardController adminDashboardController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private DashboardService dashboardService;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminDashboardController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	public void getDashboardInf() throws Exception {
		DashboardResponse dashboardResponse = new DashboardResponse();
		dashboardResponse.setNumberUsers(500);
		dashboardResponse.setTotalTransactions(250);
		dashboardResponse.setCompletedRates("90");
		dashboardResponse.setNumberMentors(85);
		dashboardResponse.setNumberCreatedRequests(850);

		when(dashboardService.getDashboardInfo()).thenReturn(dashboardResponse);
		mockMvc.perform(get("/api/admin/dashboard"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numberUsers", is(500)))
				.andExpect(jsonPath("$.totalTransactions", is(250)))
				.andExpect(jsonPath("$.completedRates", is("90")))
				.andExpect(jsonPath("$.numberMentors", is(85)))
				.andExpect(jsonPath("$.numberCreatedRequests", is(850)));
	}
}