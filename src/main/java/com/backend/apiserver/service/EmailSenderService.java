package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.MailRequest;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailSenderService {
	void sendEmailTemplate(MailRequest mailRequest) throws MessagingException, IOException, TemplateException;
}
