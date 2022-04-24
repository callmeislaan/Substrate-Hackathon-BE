package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.AuthRequest;
import com.backend.apiserver.bean.request.RegisterUserRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.TokenFirebaseResponse;
import com.backend.apiserver.bean.response.TokenInfoResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.EmailDuplicatedException;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.UsernameDuplicatedException;
import com.backend.apiserver.service.AuthenticateService;
import com.backend.apiserver.utils.Constants;
import com.google.firebase.auth.FirebaseAuthException;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
public class AuthController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	private AuthenticateService authenticateService;

	@PostMapping("api/auth")
	public TokenInfoResponse authenticate(@Valid @RequestBody final AuthRequest authRequest) throws BadRequestException {
		try {
			LOG.info("Start to authenticate login request with information: " + authRequest);
			TokenInfoResponse tokenInfoResponse = authenticateService.performAuthentication(authRequest, false);
			LOG.info("End to authenticate login request with information: " + authRequest);
			return tokenInfoResponse;
		} catch (AuthenticationException | RoleNotFoundException | ForbiddenException e) {
			throw new BadRequestException(ResponseMessage.AuthenticateUserFailed);
		}
	}

	@PostMapping("api/register")
	public Response register(@Valid @RequestBody final RegisterUserRequest registerUserRequest) throws BadRequestException, URISyntaxException {
		try {
			LOG.info("Start to create user with username: ", registerUserRequest.getUsername());
			authenticateService.registerUser(registerUserRequest);
			LOG.info("End to create user with username: ", registerUserRequest.getUsername());
			return new Response(ResponseMessage.RegisterUserSuccess);
		} catch (UsernameDuplicatedException e) {
			throw new BadRequestException(ResponseMessage.DuplicatedUsername);
		} catch (EmailDuplicatedException e) {
			throw new BadRequestException(ResponseMessage.DuplicatedEmail);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.UserRoleNotFound);
		} catch (MessagingException | IOException | TemplateException e) {
			throw new BadRequestException(ResponseMessage.SendMailException);
		}
	}

	@PostMapping("api/firebase/auth")
	public TokenFirebaseResponse authenticateFirebase() throws BadRequestException {
		try {
			LOG.info("Start to authenticate firebase");
			TokenFirebaseResponse tokenFirebaseResponse = authenticateService.performAuthenticationFirebase();
			LOG.info("End to authenticate firebase");
			return tokenFirebaseResponse;
		} catch (FirebaseAuthException e) {
			throw new BadRequestException(ResponseMessage.AuthenticateUserFailed);
		}
	}

	@PostMapping("api/admin/auth")
	public TokenInfoResponse adminAuthenticate(@Valid @RequestBody final AuthRequest authRequest) throws BadRequestException {
		try {
			return authenticateService.performAuthentication(authRequest, true);
		} catch (AuthenticationException | RoleNotFoundException | ForbiddenException e) {
			throw new BadRequestException(ResponseMessage.AuthenticateUserFailed);
		}
	}

	@GetMapping("api/activation")
	public RedirectView activeUser(@RequestParam final String token) {
		try {
			authenticateService.activeUser(token);
			return new RedirectView(
					authenticateService
							.getCommonProperties()
							.getFrontendURL() + Constants.ACTIVE_PAGE);
		} catch (DataDuplicatedException | ForbiddenException | NotFoundException e) {
			return new RedirectView(
					authenticateService
							.getCommonProperties()
							.getFrontendURL() + Constants.ERROR_PAGE
			);
		}
	}

	@GetMapping(value = "api/public/health")
	@ResponseStatus(HttpStatus.OK)
	public String healthCHeck() {
		return "OK";
	}
}
