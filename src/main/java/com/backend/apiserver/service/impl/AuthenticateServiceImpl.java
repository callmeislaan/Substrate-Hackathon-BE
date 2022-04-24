package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.AuthRequest;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.bean.request.RegisterUserRequest;
import com.backend.apiserver.bean.response.TokenFirebaseResponse;
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
import com.backend.apiserver.service.AuthenticateService;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.utils.Constants;
import com.backend.apiserver.utils.DateTimeUtils;
import com.backend.apiserver.utils.FirebaseUtils;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import com.google.firebase.auth.FirebaseAuthException;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {

	private JwtUtils jwtUtils;

	private AuthenticationManager authenticationManager;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private PasswordEncoder passwordEncoder;

	private EmailSenderService emailSenderService;

	private CommonProperties commonProperties;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TokenInfoResponse performAuthentication(AuthRequest authRequest, boolean adminAuth) throws RoleNotFoundException, ForbiddenException {

		// authenticate username and password
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						StringUtils.lowerCase(authRequest.getUsername()),
						authRequest.getPassword()
				)
		);

		//get information from UserDetailService
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		//Get all the roles of user
		Optional<String> optionalRole = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.findFirst();

		if (!optionalRole.isPresent())
			throw new RoleNotFoundException("Not found role when login");

		if ((adminAuth && !optionalRole.get().equals(Constants.ROLE_ADMIN)) ||
				!adminAuth && optionalRole.get().equals(Constants.ROLE_ADMIN))
			throw new ForbiddenException("Can only login for normal user here");

		// if there's not exception thrown, mean that user information is valid
		// set authentication information to Security Context
		SecurityContextHolder.getContext().setAuthentication(authentication);

		//generate jwt token after user valid
		String jwt = jwtUtils.generateToken(authentication);

		return TokenInfoResponse.builder()
				.username(userDetails.getUsername())
				.token(jwt)
				.role(optionalRole.get())
				.build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AnestTransactional
	public void registerUser(RegisterUserRequest registerUserRequest) throws UsernameDuplicatedException, EmailDuplicatedException, NotFoundException, MessagingException, IOException, TemplateException, URISyntaxException {
		String username = StringUtils.lowerCase(registerUserRequest.getUsername());
		String email = StringUtils.lowerCase(registerUserRequest.getEmail());

		if (userRepository.existsByUsername(username))
			throw new UsernameDuplicatedException("User with this username already exists: " + username);

		if (userRepository.existsByEmail(email))
			throw new EmailDuplicatedException("User with this email already exists: " + email);

		Role role = roleRepository.findByName(Constants.ROLE_MENTEE);
		if (Objects.isNull(role))
			throw new NotFoundException("Not found role USER in order to insert to database");

		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
		user.setEmail(email);
		user.setRole(role);
		user.setStatus(Status.PENDING);

		UserDetail userDetail = new UserDetail();
		userDetail.setFullName(registerUserRequest.getFullName());
		userDetail.setDateOfBirth(DateTimeUtils.fromCurrentTimeMillis(registerUserRequest.getDateOfBirth()));
		userDetail.setGender(registerUserRequest.isGender());
		userDetail.setPhone(registerUserRequest.getPhone());
		userDetail.setUser(user);
		user.setUserDetail(userDetail);
		//save user to database
		userRepository.saveAndFlush(user);

		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		params.put("email", email);
		params.put("fullName", registerUserRequest.getFullName());
		params.put("phone", registerUserRequest.getPhone());
		params.put("gender", registerUserRequest.isGender() ? "Nam" : "Nữ");
		String birthDate = DateTimeUtils.fromCurrentTimeMillis(registerUserRequest.getDateOfBirth()).format(Constants.DATE_FORMAT_DDMMYYYY);
		params.put("dateOfBirth", birthDate);
		String baseURL = commonProperties.getBackendURL();
		String registerURL = new URIBuilder(new URI(baseURL))
				.setPath(Constants.ACTIVATION_PATH)
				.addParameter("token", jwtUtils.generateToken(registerUserRequest.getUsername()))
				.toString();
		params.put("url", registerURL);

		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(registerUserRequest.getEmail()),
				params,
				MailRequest.TEMPLATE_REGISTRATION,
				MailRequest.TITLE_REGISTRATION
		);
		emailSenderService.sendEmailTemplate(mailRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TokenFirebaseResponse performAuthenticationFirebase() throws FirebaseAuthException {
		String username = jwtUtils.getUsername();
		String firebaseToken = FirebaseUtils.createToken(username);
		return TokenFirebaseResponse.builder()
				.firebaseToken(firebaseToken)
				.build();
	}

	@Override
	@AnestTransactional
	public void activeUser(String token) throws NotFoundException, DataDuplicatedException, ForbiddenException {
		if (!jwtUtils.validateToken(token))
			throw new ForbiddenException("Link is invalid or expired");
		User user = userRepository.findUserByUsername(jwtUtils.getUsername(token));
		if (Objects.isNull(user))
			throw new NotFoundException("Doesn't exist any record related to this username");
		if (user.getStatus().equals(Status.ACTIVE))
			throw new DataDuplicatedException("This user is already activated");
		user.setStatus(Status.ACTIVE);
		userRepository.saveAndFlush(user);
	}

	@Override
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
}
