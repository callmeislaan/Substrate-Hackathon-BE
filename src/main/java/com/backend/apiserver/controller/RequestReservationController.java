package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.service.RequestReservationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("api/request/")
public class RequestReservationController {

	private RequestReservationService requestReservationService;

	@Secured("ROLE_MENTOR")
	@PostMapping("reserve-request/{requestId}")
	public Response reserveRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			requestReservationService.reserveRequest(requestId);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (DataDuplicatedException e) {
			throw new BadRequestException(ResponseMessage.ReserveWrongRequestException);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, requestId);
		} catch (MentorNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		}
	}

	@Secured("ROLE_MENTOR")
	@DeleteMapping("undo-reserve-request/{requestId}")
	public Response undoReserveRequest(@PathVariable final Long requestId) throws BadRequestException {
		try {
			requestReservationService.undoReserveRequest(requestId);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (MentorRequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorRequestNotFound);
		}
	}

	@GetMapping("pending-mentors/{requestId}")
	public WrapperResponse findPendingMentors(@PathVariable final Long requestId) throws BadRequestException {
		try {
			return requestReservationService.findPendingMentors(requestId);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorRequestNotFound);
		}
	}

	@PostMapping("rent-mentor")
	public Response rentMentor(@Valid @RequestBody final RentMentorRequest rentMentorRequest) throws BadRequestException {
		try {
			requestReservationService.rentMentor(rentMentorRequest);
			return new Response(ResponseMessage.ConfirmChooseMentorSuccess);
		} catch (MentorRequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorRequestNotFound);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound, rentMentorRequest.getRequestId());
		} catch (MoneyRelatedException e) {
			throw new BadRequestException(ResponseMessage.RequestMoneyWithdrawExceedBalance);
		} catch (MentorNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorNotFound);
		}
	}

	@DeleteMapping("{requestId}/mentor/{mentorId}")
	public Response deleteRequestReservation(@PathVariable final Long requestId, @PathVariable final Long mentorId) throws BadRequestException {
		try {
			requestReservationService.deleteRequestReservation(requestId, mentorId);
			return new Response(ResponseMessage.PerformOperationSuccess);
		} catch (RequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.RequestNotFound);
		} catch (MentorRequestNotFoundException e) {
			throw new BadRequestException(ResponseMessage.MentorRequestNotFound);
		}
	}
}
