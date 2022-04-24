package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.FinishRequestRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.RequestAnnouncementNotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.service.RequestConfirmationService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

@RequestMapping("api/request/")
@RestController
@AllArgsConstructor
public class RequestConfirmationController {

	private RequestConfirmationService requestConfirmationService;

	@PostMapping("user/confirm-finish-request")
	public Response confirmFinishRequest(@Valid @RequestBody final FinishRequestRequest finishRequestRequest) throws BadRequestException, MessagingException, IOException, TemplateException, URISyntaxException {
		try {
			requestConfirmationService.confirmFinishRequest(finishRequestRequest);
			return new Response(ResponseMessage.ConfirmMentorFinishRequestSuccess);
		} catch (MentorRequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorRequestNotFound);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, finishRequestRequest.getRequestId());
		}
	}

	@PostMapping("user/confirm-not-finish-request")
	public Response confirmNotFinishRequest(@Valid @RequestBody final FinishRequestRequest finishRequestRequest) throws BadRequestException {
		try {
			requestConfirmationService.confirmNotFinishRequest(finishRequestRequest);
			return new Response(ResponseMessage.UserRejectRequestSuccess);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound);
		} catch (MentorNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		}
	}


	@Secured("ROLE_MENTOR")
	@PostMapping("mentor/confirm-not-finish-request/{requestId}")
	public Response mentorConfirmNotFinishRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			requestConfirmationService.mentorConfirmNotFinishRequest(requestId);
			return new Response(ResponseMessage.MentorConfirmRejectRequestSuccess);
		} catch (RequestAnnouncementNotFoundException e) {
			throw new BadRequestException(ResponseMessage.InvitationNotFound);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound);
		} catch (MentorNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		}
	}

	@Secured("ROLE_MENTOR")
	@PostMapping("mentor/confirm-finish-request/{requestId}")
	public Response mentorConfirmFinishRequest(@PathVariable final Long requestId) throws MessagingException, IOException, TemplateException, URISyntaxException {
		requestConfirmationService.mentorConfirmFinishRequest(requestId);
		return new Response(ResponseMessage.MentorDenyRejectRequestSuccess);
	}
}
