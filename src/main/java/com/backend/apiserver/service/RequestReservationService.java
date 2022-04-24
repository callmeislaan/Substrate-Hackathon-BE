package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.RentMentorRequest;
import com.backend.apiserver.bean.response.WrapperResponse;
import com.backend.apiserver.exception.DataDuplicatedException;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.MoneyRelatedException;
import com.backend.apiserver.exception.RequestNotFoundException;

public interface RequestReservationService {
	void reserveRequest(Long requestId) throws RequestNotFoundException, DataDuplicatedException, MentorNotFoundException;

	void undoReserveRequest(Long requestId) throws MentorRequestNotFoundException;

	WrapperResponse findPendingMentors(Long requestId) throws RequestNotFoundException;

	void rentMentor(RentMentorRequest rentMentorRequest) throws MentorRequestNotFoundException, RequestNotFoundException, MoneyRelatedException, MentorNotFoundException;

	void deleteRequestReservation(Long requestId, Long mentorId) throws MentorRequestNotFoundException, RequestNotFoundException;
}
