package com.backend.apiserver.service.impl;

import com.backend.apiserver.annotation.AnestTransactional;
import com.backend.apiserver.bean.request.HireAnestMentorRequest;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MentorHire;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.exception.ForbiddenException;
import com.backend.apiserver.exception.InvalidDataException;
import com.backend.apiserver.exception.NotFoundException;
import com.backend.apiserver.repository.MentorHireRepository;
import com.backend.apiserver.repository.MentorRepository;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.EmailSenderService;
import com.backend.apiserver.service.MentorHiringService;
import com.backend.apiserver.utils.FormatUtils;
import com.backend.apiserver.utils.JwtUtils;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MentorHiringServiceImpl implements MentorHiringService {

	private JwtUtils jwtUtils;
	private MentorHireRepository mentorHireRepository;
	private UserRepository userRepository;
	private MentorRepository mentorRepository;
	private EmailSenderService emailSenderService;

	@Override
	@AnestTransactional
	public void requestHireAnestMentor(HireAnestMentorRequest hireAnestMentorRequest) throws NotFoundException, MessagingException, IOException, TemplateException, InvalidDataException, ForbiddenException {
		Long userId = jwtUtils.getUserId();
		User user = userRepository.findByIdAndStatus(userId, Status.ACTIVE);
		Mentor mentor = mentorRepository.findByUserIdAndStatus(hireAnestMentorRequest.getMentorId(), Status.ACTIVE);
		if (userId.equals(hireAnestMentorRequest.getMentorId()))
			throw new ForbiddenException("You can rent yourself");
		if (Objects.isNull(mentor))
			throw new NotFoundException("Not found mentor with given id: " + hireAnestMentorRequest.getMentorId());
		if (!mentor.isAnestMentor())
			throw new InvalidDataException("Only anest mentor can't have this features");
		MentorHire mentorHire = new MentorHire();
		mentorHire.setMentor(mentor);
		mentorHire.setUser(user);
		mentorHire.setTitle(hireAnestMentorRequest.getTitle());
		mentorHire.setNote(hireAnestMentorRequest.getNote());
		mentorHire.setStatus(Status.ACTIVE);
		mentorHireRepository.saveAndFlush(mentorHire);
		User mentorUser = mentor.getUser();
		Map<String, Object> params = new HashMap<>();
		params.put("mentorFullName", mentorUser.getUserDetail().getFullName());
		params.put("userFullName", user.getUserDetail().getFullName());
		params.put("userPhone", user.getUserDetail().getPhone());
		params.put("title", hireAnestMentorRequest.getTitle());
		params.put("note", hireAnestMentorRequest.getNote());
		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray(mentorUser.getEmail()),
				params,
				MailRequest.TEMPLATE_HIRE_MENTOR,
				MailRequest.TITLE_HIRE_MENTOR
		);
		mailRequest.setCc(FormatUtils.makeArray(MailRequest.CC, user.getEmail()));
		emailSenderService.sendEmailTemplate(mailRequest);
	}
}
