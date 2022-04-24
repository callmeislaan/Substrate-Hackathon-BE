package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.AuthRequest;
import com.backend.apiserver.bean.request.RegisterUserRequest;
import com.backend.apiserver.bean.response.TokenInfoResponse;
import com.backend.apiserver.configuration.CommonProperties;
import com.backend.apiserver.entity.Role;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.EmailDuplicatedException;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.UsernameDuplicatedException;
import com.backend.apiserver.repository.RoleRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.AuthenticateServiceImpl;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.JwtUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuthException;
import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class  AuthenticateServiceTest {

	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private EmailSenderService emailSenderService;
	@Mock
	private CommonProperties commonProperties;


	@InjectMocks
	AuthenticateService authenticateService = new AuthenticateServiceImpl(
			jwtUtils,
			authenticationManager,
			userRepository,
			roleRepository,
			passwordEncoder,
			emailSenderService,
			commonProperties
			);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void performAuthentication() throws Exception {

		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername("");
		authRequest.setPassword("");

		UserDetails userDetails =  new UserDetails() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
			}

			@Override
			public String getPassword() {
				return null;
			}

			@Override
			public String getUsername() {
				return "kafka2405";
			}

			@Override
			public boolean isAccountNonExpired() {
				return false;
			}

			@Override
			public boolean isAccountNonLocked() {
				return false;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return false;
			}

			@Override
			public boolean isEnabled() {
				return false;
			}
		};

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtUtils.generateToken(authentication)).thenReturn("abc");

		TokenInfoResponse tokenInfoResponse = authenticateService.performAuthentication(authRequest, false);
		assertEquals("kafka2405", tokenInfoResponse.getUsername());
	}

	@Test(expected = RoleNotFoundException.class)
	public void performAuthentication_RoleNotFoundException() throws Exception {

		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername("");
		authRequest.setPassword("");

		UserDetails userDetails =  new UserDetails() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Collections.EMPTY_LIST;
			}

			@Override
			public String getPassword() {
				return null;
			}

			@Override
			public String getUsername() {
				return "kafka2405";
			}

			@Override
			public boolean isAccountNonExpired() {
				return false;
			}

			@Override
			public boolean isAccountNonLocked() {
				return false;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return false;
			}

			@Override
			public boolean isEnabled() {
				return false;
			}
		};

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);

		TokenInfoResponse tokenInfoResponse = authenticateService.performAuthentication(authRequest, false);
		assertEquals("kafka2405", tokenInfoResponse.getUsername());
	}

	@Test(expected = ForbiddenException.class)
	public void performAuthentication_ForbiddenException() throws Exception {

		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername("");
		authRequest.setPassword("");

		UserDetails userDetails =  new UserDetails() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}

			@Override
			public String getPassword() {
				return null;
			}

			@Override
			public String getUsername() {
				return "kafka2405";
			}

			@Override
			public boolean isAccountNonExpired() {
				return false;
			}

			@Override
			public boolean isAccountNonLocked() {
				return false;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return false;
			}

			@Override
			public boolean isEnabled() {
				return false;
			}
		};

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

		when(authenticationManager.authenticate(any())).thenReturn(authentication);

		TokenInfoResponse tokenInfoResponse = authenticateService.performAuthentication(authRequest, false);
		assertEquals("kafka2405", tokenInfoResponse.getUsername());
	}

	@Test
	public void registerUser() throws Exception {

		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setDateOfBirth(6646546546L);
		registerUserRequest.setEmail("kafka@gmail.com");
		registerUserRequest.setFullName("dang dinh quyen");
		registerUserRequest.setGender(true);
		registerUserRequest.setPassword("123456");
		registerUserRequest.setPhone("09695663145");
		registerUserRequest.setUsername("kafka2405");

		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(false);
		when(roleRepository.findByName(Constants.ROLE_MENTEE)).thenReturn(new Role());
		when(commonProperties.getBackendURL()).thenReturn("localhost");
		authenticateService.registerUser(registerUserRequest);
	}

	@Test(expected = UsernameDuplicatedException.class)
	public void registerUser_UsernameDuplicatedException() throws Exception {

		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setDateOfBirth(6646546546L);
		registerUserRequest.setEmail("kafka@gmail.com");
		registerUserRequest.setFullName("dang dinh quyen");
		registerUserRequest.setGender(true);
		registerUserRequest.setPassword("123456");
		registerUserRequest.setPhone("09695663145");
		registerUserRequest.setUsername("kafka2405");

		when(userRepository.existsByUsername(any())).thenReturn(true);
		authenticateService.registerUser(registerUserRequest);
	}

	@Test(expected = EmailDuplicatedException.class)
	public void registerUser_EmailDuplicatedException() throws Exception {

		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setDateOfBirth(6646546546L);
		registerUserRequest.setEmail("kafka@gmail.com");
		registerUserRequest.setFullName("dang dinh quyen");
		registerUserRequest.setGender(true);
		registerUserRequest.setPassword("123456");
		registerUserRequest.setPhone("09695663145");
		registerUserRequest.setUsername("kafka2405");

		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(true);
		authenticateService.registerUser(registerUserRequest);
	}

	@Test(expected = NotFoundException.class)
	public void registerUser_NotFoundException() throws Exception {

		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setDateOfBirth(6646546546L);
		registerUserRequest.setEmail("kafka@gmail.com");
		registerUserRequest.setFullName("dang dinh quyen");
		registerUserRequest.setGender(true);
		registerUserRequest.setPassword("123456");
		registerUserRequest.setPhone("09695663145");
		registerUserRequest.setUsername("kafka2405");

		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(false);
		when(roleRepository.findByName(Constants.ROLE_MENTEE)).thenReturn(null);
		authenticateService.registerUser(registerUserRequest);
	}

	@Test
	public void performAuthenticationFirebase() throws FirebaseAuthException {
		when(jwtUtils.getUsername()).thenReturn("kafka");
		authenticateService.performAuthenticationFirebase();
	}

	@Test
	public void activeUser() throws DataDuplicatedException, NotFoundException, ForbiddenException {
		when(jwtUtils.validateToken(any())).thenReturn(true);
		when(jwtUtils.getUsername(any())).thenReturn("kafka");
		when(userRepository.findUserByUsername(any())).thenReturn(generateUser(Status.PENDING));
		authenticateService.activeUser("kafka");
	}

	@Test(expected = DataDuplicatedException.class)
	public void activeUser_DataDuplicatedException() throws DataDuplicatedException, NotFoundException, ForbiddenException {
		when(jwtUtils.validateToken(any())).thenReturn(true);
		when(jwtUtils.getUsername(any())).thenReturn("kafka");
		when(userRepository.findUserByUsername(any())).thenReturn(generateUser(Status.ACTIVE));
		authenticateService.activeUser("kafka");
	}

	@Test(expected = NotFoundException.class)
	public void activeUser_NotFoundException() throws DataDuplicatedException, NotFoundException, ForbiddenException {
		when(jwtUtils.validateToken(any())).thenReturn(true);
		when(jwtUtils.getUsername(any())).thenReturn("kafka");
		when(userRepository.findUserByUsername(any())).thenReturn(null);
		authenticateService.activeUser("kafka");
	}

	@Test(expected = ForbiddenException.class)
	public void activeUser_ForbiddenException() throws DataDuplicatedException, NotFoundException, ForbiddenException {
		when(jwtUtils.validateToken(any())).thenReturn(false);
		authenticateService.activeUser("kafka");
	}

	@Test
	public void getCommonProperties() throws DataDuplicatedException, NotFoundException, ForbiddenException {
		authenticateService.getCommonProperties();
	}

	private UserDetail generateUserDetail(){
		UserDetail userDetail = new UserDetail();
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		userDetail.setAvatar("LocalDateTime.MAX");
		userDetail.setPhone("LocalDateTime.MAX");
		userDetail.setFullName("LocalDateTime.MAX");
		userDetail.setDateOfBirth(LocalDateTime.of(2018, Month.APRIL, 15, 12, 00));
		return userDetail;
	}

	private User generateUser(Status status){
		User user = new User();
		user.setId(1L);
		user.setUsername("kafka2405");
		user.setUserDetail(generateUserDetail());
		user.setStatus(status);
		return user;
	}
}