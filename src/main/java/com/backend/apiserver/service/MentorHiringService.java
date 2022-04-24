package com.backend.apiserver.service;

import com.backend.apiserver.bean.request.HireAnestMentorRequest;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MentorHiringService {
	void requestHireAnestMentor(HireAnestMentorRequest hireAnestMentorRequest) throws NotFoundException, MessagingException, IOException, TemplateException, InvalidDataException, ForbiddenException;
}
