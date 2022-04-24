package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.RegisterMentorRequest;
import com.backend.apiserver.bean.request.UpdatePasswordRequest;
import com.backend.apiserver.bean.request.UseAnestCardRequest;
import com.backend.apiserver.bean.request.UserProfileRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.UserFinanceOverviewResponse;
import com.backend.apiserver.bean.response.UserOverviewResponse;
import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.PasswordNotMatchException;
import com.backend.apiserver.exception.RoleNotFoundException;
import com.backend.apiserver.exception.SkillNotFoundException;
import com.backend.apiserver.exception.UserNotFoundException;
import com.backend.apiserver.service.UserService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("api/user/")
public class UserController {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	/**
	 * UserService
	 */
	private UserService userService;

	@GetMapping("profile")
	public UserProfileResponse getUserProfile() {
		LOG.info("Start to get user profiles information");
		UserProfileResponse userProfileResponse = userService.getProfile();
		LOG.info("End to get user profiles information");
		return userProfileResponse;
	}

	@GetMapping("overview")
	public UserOverviewResponse getUserOverview() {
		LOG.info("Start to get user overview");
		UserOverviewResponse userOverviewResponse = userService.getUserOverview();
		LOG.info("End to get user overview");
		return userOverviewResponse;
	}

	@GetMapping("finance-overview")
	public UserFinanceOverviewResponse getUserFinanceOverview() {
		LOG.info("Start to get user finance overview");
		UserFinanceOverviewResponse userFinanceOverviewResponse = userService.getUserFinanceOverview();
		LOG.info("End to get user finance overview");
		return userFinanceOverviewResponse;
	}

	@Secured("ROLE_MENTEE")
	@PostMapping("register-mentor")
	public Response registerMentor(@Valid @RequestBody final RegisterMentorRequest registerMentorRequest) throws BadRequestException {
		LOG.info("Start to register mentor member for user");
		try {
			userService.registerMentor(registerMentorRequest);
			LOG.info("End to register mentor member for user");
			return new Response(ResponseMessage.RegisterMentorSuccess);
		} catch (UserNotFoundException e) {
			throw new BadRequestException(ResponseMessage.UserIdNotFound);
		} catch (RoleNotFoundException e) {
			throw new BadRequestException(ResponseMessage.UserRoleNotFound);
		} catch (SkillNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestHasInvalidSkill);
		}
	}

	@PutMapping("update-password")
	public Response updatePassword(@Valid @RequestBody final UpdatePasswordRequest updatePasswordRequest) throws BadRequestException {
		try {
			LOG.info("Start to update user password");
			userService.updatePassword(updatePasswordRequest);
			LOG.info("End to update user password");
			return new Response(ResponseMessage.UpdatePasswordSuccess);
		} catch (PasswordNotMatchException e) {
			throw new BadRequestException(ResponseMessage.UpdatePasswordNotMatch);
		}
	}

	@PutMapping("profile")
	public Response updateProfile(@Valid @RequestBody UserProfileRequest userProfileRequest) {
		userService.updateProfile(userProfileRequest);
		return new Response(ResponseMessage.PerformOperationSuccess);
	}

	@PostMapping("anest-card")
	public Response requestUsingAnestCard(@Valid @RequestBody UseAnestCardRequest useAnestCardRequest) throws BadRequestException, MessagingException, IOException, TemplateException {
		try {
			userService.requestUsingAnestCard(useAnestCardRequest);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.TopUpNotFoundException);
		} catch (DataDuplicatedException e) {
			throw new BadRequestException(ResponseMessage.TopUpAlreadyUsedException);
		} catch (InvalidDataException e) {
			throw new BadRequestException(ResponseMessage.TopUpCodeIsWrongException);
		}
	}
}
