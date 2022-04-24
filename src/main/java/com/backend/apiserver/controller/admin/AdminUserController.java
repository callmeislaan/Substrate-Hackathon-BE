package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.MentorService;
import com.backend.apiserver.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("api/admin/")
public class AdminUserController {

	private UserService userService;

	private MentorService mentorService;

	@GetMapping("users")
	public PagingWrapperResponse viewUsers(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(required = false) String keyword
	) {
		return userService.viewUsers(page, size, keyword);
	}

	@GetMapping("mentors")
	public PagingWrapperResponse viewMentors(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(required = false) String keyword
	) {
		return mentorService.viewMentors(page, size, keyword);
	}

	@PostMapping("mentor/{mentorId}")
	public Response setAnestMentor(@PathVariable final Long mentorId) throws BadRequestException {
		try {
			mentorService.setAnestMentor(mentorId);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		}
	}
}
