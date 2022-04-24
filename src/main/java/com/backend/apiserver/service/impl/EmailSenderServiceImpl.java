package com.backend.apiserver.service.impl;

import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.service.EmailSenderService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

	private JavaMailSender mailSender;

	private Configuration freemarkerConfiguration;

	@Async
	@Override
	public void sendEmailTemplate(MailRequest mailRequest) throws MessagingException, IOException, TemplateException {
		//initialize
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(
				message,
				MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name()
		);

		//load template
		Template template = freemarkerConfiguration.getTemplate(mailRequest.getTemplate());

		//add time signature to prevent mail being trim
		Map<String, Object> params = mailRequest.getParams();
		params.put("now", System.currentTimeMillis());
		//create html content with param from map
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailRequest.getParams());

		helper.setTo(mailRequest.getTo());
		helper.setCc(mailRequest.getCc());
		helper.setText(html, true);
		helper.setSubject(mailRequest.getSubject());
		helper.setFrom(mailRequest.getFrom());

		//send mail
		mailSender.send(message);
	}
}
