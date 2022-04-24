package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.HireAnestMentorRequest;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.MentorHiringService;
import com.backend.apiserver.controller.MentorHiringController;
import com.backend.apiserver.utils.FormatUtils;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class MentorHiringControllerTest {

	@InjectMocks
	MentorHiringController mentorHiringController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private MentorHiringService mentorHiringService;

	private HireAnestMentorRequest request;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.standaloneSetup(mentorHiringController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
		request = new HireAnestMentorRequest();
	}

	@Spy
	static ResourceLoader resourceLoader = new DefaultResourceLoader();

	static {
		Resource resource = resourceLoader.getResource("classpath:serviceAccount.json");
		try {
			InputStream serviceAccount = resource.getInputStream();

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://testchat-36274.firebaseio.com")
					.build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void requestHireAnestMentor_Success() throws Exception {
		mockMvc.perform(post("/api/hiring/hire-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isOk());
	}

	@Test
	public void requestHireAnestMentor_ThrowForbiddenException() throws Exception {
		doThrow(new ForbiddenException("") {})
				.when(mentorHiringService)
				.requestHireAnestMentor(any());

		mockMvc.perform(post("/api/hiring/hire-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestHireAnestMentor_ThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException("") {})
				.when(mentorHiringService)
				.requestHireAnestMentor(any());

		mockMvc.perform(post("/api/hiring/hire-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestHireAnestMentor_ThrowInvalidDataException() throws Exception {
		doThrow(new InvalidDataException("") {})
				.when(mentorHiringService)
				.requestHireAnestMentor(any());

		mockMvc.perform(post("/api/hiring/hire-mentor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
	}
}