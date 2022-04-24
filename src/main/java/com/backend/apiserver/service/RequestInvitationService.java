package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.PagingWrapperResponse;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;

public interface RequestInvitationService {
	void inviteMentor(RentMentorRequest rentMentorRequest) throws RequestNotFoundException, MentorNotFoundException;
	PagingWrapperResponse findInvitationRequests(Integer page, Integer size);
    void deleteInvitation(Long requestId) throws NotFoundException;
}
