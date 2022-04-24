package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.HireAnestMentorRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.service.MentorHiringService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;


@RestController
@AllArgsConstructor
@RequestMapping("api/hiring/")
public class MentorHiringController {

	private MentorHiringService mentorHiringService;

	@PostMapping("hire-mentor")
	public Response requestHireAnestMentor(@RequestBody final HireAnestMentorRequest hireAnestMentorRequest) throws BadRequestException, MessagingException, IOException, TemplateException {
		try {
			mentorHiringService.requestHireAnestMentor(hireAnestMentorRequest);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (ForbiddenException e) {
			throw new BadRequestException(ResponseMessage.PerformOperationYourSelf);
		} catch (NotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		} catch (InvalidDataException e) {
			throw new BadRequestException(ResponseMessage.NotAnestMentorException);
		}
	}
}
