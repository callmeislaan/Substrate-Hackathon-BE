package com.backend.apiserver.service;

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
import com.google.firebase.auth.FirebaseAuthException;
import freemarker.template.TemplateException;
import org.springframework.security.core.AuthenticationException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface AuthenticateService {

	/**
	 * User login
	 *
	 * @param authRequest
	 * @param adminAuth
	 * @return
	 * @throws AuthenticationException
	 */
	TokenInfoResponse performAuthentication(AuthRequest authRequest, boolean adminAuth) throws RoleNotFoundException, ForbiddenException;

	/**
	 * Register new user with given information
	 *
	 * @param registerUserRequest
	 * @throws UsernameDuplicatedException
	 * @throws EmailDuplicatedException
	 * @throws NotFoundException
	 */
	void registerUser(RegisterUserRequest registerUserRequest) throws UsernameDuplicatedException, EmailDuplicatedException, NotFoundException, MessagingException, IOException, TemplateException, URISyntaxException;

	/**
	 * Authen firebase
	 *
	 * @return TokenFirebaseResponse
	 * @throws FirebaseAuthException
	 */
	TokenFirebaseResponse performAuthenticationFirebase() throws FirebaseAuthException;

	void activeUser(String username) throws NotFoundException, DataDuplicatedException, ForbiddenException;

	CommonProperties getCommonProperties();
}
