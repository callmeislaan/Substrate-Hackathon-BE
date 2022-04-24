package com.backend.apiserver.controller;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import com.backend.apiserver.service.RequestInvitationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("api/request/")
public class RequestInvitationController {

    private RequestInvitationService requestInvitationService;

    @PostMapping("invite-mentor")
    public Response inviteMentor(@Valid @RequestBody final RentMentorRequest rentMentorRequest) throws BadRequestException {
        try {
            requestInvitationService.inviteMentor(rentMentorRequest);
            return new Response(ResponseMessage.PerformOperationSuccess);
        } catch (RequestNotFoundException e) {
            throw new BadRequestException(ResponseMessage.RequestNotFound, rentMentorRequest.getRequestId());
        } catch (MentorNotFoundException e) {
            throw new BadRequestException(ResponseMessage.MentorNotFound, rentMentorRequest.getMentorId());
        }
    }

    @Secured("ROLE_MENTOR")
    @GetMapping("invitation-requests")
    public PagingWrapperResponse findInvitationRequests(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return requestInvitationService.findInvitationRequests(page, size);
    }

    @Secured("ROLE_MENTOR")
    @DeleteMapping("delete-invitation/{requestId}")
    public Response deleteInvitation(@PathVariable final Long requestId) throws BadRequestException {
        try {
            requestInvitationService.deleteInvitation(requestId);
            return new Response(ResponseMessage.PerformOperationSuccess);
        } catch (NotFoundException e) {
            throw new BadRequestException(ResponseMessage.InvitationNotFound);
        }
    }


}
