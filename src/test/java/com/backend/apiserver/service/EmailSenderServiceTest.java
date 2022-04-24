package com.backend.apiserver.service;

import ch.qos.logback.core.net.LoginAuthenticator;
import com.backend.apiserver.bean.request.MailRequest;
import com.backend.apiserver.service.impl.BankCardServiceImpl;
import com.backend.apiserver.service.impl.EmailSenderServiceImpl;
import com.backend.apiserver.utils.FormatUtils;
import com.sun.istack.internal.tools.DefaultAuthenticator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.naming.factory.MailSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailSenderServiceTest {

	@Mock
	private JavaMailSender mailSender;
	@Mock
	private Configuration freemarkerConfiguration;

	@InjectMocks
	EmailSenderService emailSenderService = new EmailSenderServiceImpl(
			mailSender,
			freemarkerConfiguration
	);

	@Test
	public void sendEmailTemplate() throws Exception {
		MailRequest mailRequest = new MailRequest(
				FormatUtils.makeArray("kafka@gmail.com"),
				new HashMap<>(),
				MailRequest.TEMPLATE_HIRE_MENTOR,
				MailRequest.TITLE_HIRE_MENTOR
		);
		Session customSession = Session.getInstance(new Properties());
		MimeMessage mimeMessage = new MimeMessage(customSession);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		Template template = Template.getPlainTextTemplate("","", new Configuration());
		when(freemarkerConfiguration.getTemplate(any())).thenReturn(template);

		emailSenderService.sendEmailTemplate(mailRequest);
	}
}