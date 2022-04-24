package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.FinishRequestRequest;
import com.backend.apiserver.exception.MentorNotFoundException;
import com.backend.apiserver.exception.MentorRequestNotFoundException;
import com.backend.apiserver.exception.RequestAnnouncementNotFoundException;
import com.backend.apiserver.exception.RequestNotFoundException;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface RequestConfirmationService {

	void confirmFinishRequest(FinishRequestRequest finishRequestRequest) throws RequestNotFoundException, MentorRequestNotFoundException, MessagingException, IOException, TemplateException, URISyntaxException;

	void confirmNotFinishRequest(FinishRequestRequest finishRequestRequest) throws MentorNotFoundException, RequestNotFoundException;

	void mentorConfirmNotFinishRequest(Long requestId) throws RequestAnnouncementNotFoundException, RequestNotFoundException, MentorNotFoundException;

	void mentorConfirmFinishRequest(Long requestId) throws MessagingException, IOException, TemplateException, URISyntaxException;

	void resolveConflict(Long requestId, boolean forUser) throws RequestNotFoundException, MentorRequestNotFoundException, RequestAnnouncementNotFoundException, MentorNotFoundException, MessagingException, IOException, TemplateException, URISyntaxException;

	void removeExpiredRejectRequest(Long requestId) throws MentorNotFoundException, RequestAnnouncementNotFoundException;
}
