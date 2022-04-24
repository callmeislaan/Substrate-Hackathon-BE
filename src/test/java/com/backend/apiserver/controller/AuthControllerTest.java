package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.AuthRequest;
import com.backend.apiserver.bean.request.RegisterUserRequest;
import com.backend.apiserver.bean.response.TokenFirebaseResponse;
import com.backend.apiserver.bean.response.TokenInfoResponse;
import com.backend.apiserver.configuration.CommonProperties;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.EmailDuplicatedException;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.UsernameDuplicatedException;
import com.backend.apiserver.exception.handler.ApiExceptionHandler;
import com.backend.apiserver.service.AuthenticateService;
import com.backend.apiserver.utils.FormatUtils;
import com.google.firebase.auth.FirebaseAuthException;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuthControllerTest {

	@InjectMocks
	AuthController authController;

	@InjectMocks
	ApiExceptionHandler apiExceptionHandler;

	private MockMvc mockMvc;

	@Mock
	private AuthenticateService authenticateService;

	private TokenInfoResponse tokenInfoResponse;

	private AuthRequest authRequest;

	private AuthRequest adminAuthRequest;

	private RegisterUserRequest registerUserRequest;

	private TokenFirebaseResponse tokenFirebaseResponse;

	private String role = "ROLE_USER";
	private String username = "kafka2405";
	private String token = "eyJhbGciOiJIUzUxMiJ9." +
			"eyJzdWIiOiJhY2NvdW50MTI2IiwiaWF0IjoxNjA1MTkzODUzLCJleHAiOjE2MDUyODAyNTMsImlkIjoxMjZ9." +
			"YQEVeBYPLl6WyUo3LKKf_2teQCk2lfYyqq-mGuvOsy98Zj9ZANi8C2rj7LSpGk2z2JljyrX3txn4CAvj2kr8oA";

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.standaloneSetup(authController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
		tokenInfoResponse = TokenInfoResponse
				.builder()
				.token(token)
				.role(role)
				.username(username)
				.build();

		authRequest = new AuthRequest();
		authRequest.setUsername("kafka2405");
		authRequest.setPassword("1234656");

		adminAuthRequest = new AuthRequest();
		adminAuthRequest.setUsername("admin");
		adminAuthRequest.setPassword("123456");

		registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setFullName("ĐẶNG ĐÌNH QUYỀN");
		registerUserRequest.setDateOfBirth(1602647114507L);
		registerUserRequest.setEmail("kafka@gmail.com");
		registerUserRequest.setGender(true);
		registerUserRequest.setPassword("123456");
		registerUserRequest.setPhone("0969563145");
		registerUserRequest.setUsername("kafka2405");

		tokenFirebaseResponse = TokenFirebaseResponse.builder().firebaseToken(token).build();
	}

	@Test
	public void authenticate_Success() throws Exception {
		when(authenticateService.performAuthentication(any(), eq(false))).thenReturn(tokenInfoResponse);
		mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(authRequest)))
				.andExpect(jsonPath("$.role", is(role)))
				.andExpect(jsonPath("$.username", is(username)))
				.andExpect(jsonPath("$.token", is(token)))
				.andExpect(status().isOk());
	}

	@Test
	public void authenticate_ErrorThrowAuthenticationException() throws Exception {
		doThrow(new AuthenticationException("") {
		})
				.when(authenticateService)
				.performAuthentication(any(), eq(false));

		mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(authRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void authenticate_ErrorThrowRoleNotFoundException() throws Exception {
		when(authenticateService.performAuthentication(any(), eq(false)))
				.thenThrow(RoleNotFoundException.class);

		mockMvc.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(authRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_Success() throws Exception {
		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(jsonPath("$.code", is("108")))
				.andExpect(status().isOk());
	}

	@Test
	public void register_ErrorThrowUsernameDuplicatedException() throws Exception {
		doThrow(new UsernameDuplicatedException())
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_ErrorThrowEmailDuplicatedException() throws Exception {
		doThrow(new EmailDuplicatedException(""))
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_ErrorThrowNotFoundException() throws Exception {
		doThrow(new NotFoundException(""))
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_ErrorThrowIOException() throws Exception {
		doThrow(new IOException(""))
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_ErrorTemplateException() throws Exception {
		doThrow(new TemplateException((Environment.getCurrentEnvironment())))
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void register_ErrorThrowMessagingException() throws Exception {
		doThrow(new MessagingException(""))
				.when(authenticateService)
				.registerUser(any());

		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(registerUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void authenticateFirebase_Success() throws Exception {
		when(authenticateService.performAuthenticationFirebase())
				.thenReturn(tokenFirebaseResponse);

		mockMvc.perform(post("/api/firebase/auth"))
				.andExpect(jsonPath("$.firebaseToken", is(token)))
				.andExpect(status().isOk());
	}

	@Test
	public void authenticateFirebase_ErrorThrowFirebaseAuthException() throws Exception {
		doThrow(new FirebaseAuthException("Firebase error", "Firebase error"))
				.when(authenticateService)
				.performAuthenticationFirebase();

		mockMvc.perform(post("/api/firebase/auth"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void adminAuthenticate_Success() throws Exception {
		when(authenticateService.performAuthentication(any(), eq(true))).thenReturn(tokenInfoResponse);
		mockMvc.perform(post("/api/admin/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(adminAuthRequest)))
				.andExpect(jsonPath("$.role", is(role)))
				.andExpect(jsonPath("$.username", is(username)))
				.andExpect(jsonPath("$.token", is(token)))
				.andExpect(status().isOk());
	}

	@Test
	public void adminAuthenticate_ErrorThrowAuthenticationException() throws Exception {
		doThrow(new AuthenticationException("") {
		})
				.when(authenticateService)
				.performAuthentication(any(), eq(true));

		mockMvc.perform(post("/api/admin/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(adminAuthRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void adminAuthenticate_ErrorThrowRoleNotFoundException() throws Exception {
		when(authenticateService.performAuthentication(any(), eq(true)))
				.thenThrow(RoleNotFoundException.class);

		mockMvc.perform(post("/api/admin/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(FormatUtils.convertObjectToJsonBytes(adminAuthRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void activeUser_Success() throws Exception {
		CommonProperties properties = new CommonProperties();
		when(authenticateService.getCommonProperties()).thenReturn(properties);
		mockMvc.perform(get("/api/activation")
				.param("token", "abcdg3egrgerggdrgd"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	public void activeUser_ThrowNotFoundException() throws Exception {
		CommonProperties properties = new CommonProperties();
		when(authenticateService.getCommonProperties()).thenReturn(properties);
		doThrow(new NotFoundException("error"))
				.when(authenticateService)
				.activeUser("abcdg3egrgerggdrgd");
		mockMvc.perform(get("/api/activation")
				.param("token", "abcdg3egrgerggdrgd"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	public void activeUser_ThrowDataDuplicatedException() throws Exception {
		CommonProperties properties = new CommonProperties();
		doThrow(new DataDuplicatedException("error"))
				.when(authenticateService)
				.activeUser("abcdg3egrgerggdrgd");
		when(authenticateService.getCommonProperties()).thenReturn(properties);
		mockMvc.perform(get("/api/activation")
				.param("token", "abcdg3egrgerggdrgd"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	public void activeUser_ThrowForbiddenException() throws Exception {
		CommonProperties properties = new CommonProperties();
		doThrow(new ForbiddenException("error"))
				.when(authenticateService)
				.activeUser("abcdg3egrgerggdrgd");
		when(authenticateService.getCommonProperties()).thenReturn(properties);
		mockMvc.perform(get("/api/activation")
				.param("token", "abcdg3egrgerggdrgd"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	public void healthCHeck() throws Exception {
		mockMvc.perform(get("/api/public/health"))
				.andExpect(status().isOk());
	}
}