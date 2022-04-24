package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.request.UpdatePasswordRequest;
import com.backend.apiserver.bean.request.UseAnestCardRequest;
import com.backend.apiserver.bean.request.UserProfileRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.UserFinanceOverviewResponse;
import com.backend.apiserver.bean.response.UserOverviewResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.PasswordNotMatchException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface UserService {

	/**
	 * Get user profile information
	 *
	 * @return UserProfileResponse
	 */
	UserProfileResponse getProfile();

	/**
	 * Get user overview
	 *
	 * @return UserProfileResponse
	 */
	UserOverviewResponse getUserOverview();

	/**
	 * Get user finance information
	 *
	 * @return UserFinanceOverviewResponse
	 */
	UserFinanceOverviewResponse getUserFinanceOverview();

	/**
	 * Update password for user general
	 *
	 * @param updatePasswordRequest
	 * @throws PasswordNotMatchException
	 */
	void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws PasswordNotMatchException;

	/**
	 * Using to register mentor
	 *
	 * @param registerMentorRequest
	 * @throws NotFoundException
	 */
	void registerMentor(RegisterMentorRequest registerMentorRequest) throws UserNotFoundException, RoleNotFoundException, SkillNotFoundException;

	void updateProfile(UserProfileRequest userProfileRequest);

	PagingWrapperResponse viewUsers(Integer page, Integer size, String keyword);

	void requestUsingAnestCard(UseAnestCardRequest useAnestCardRequest) throws NotFoundException, DataDuplicatedException, InvalidDataException, MessagingException, IOException, TemplateException;
}
