package com.example.rakesh.events;

import com.example.rakesh.mysql.model.UserEntity;
import com.example.rakesh.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


import java.util.UUID;


@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	final static Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

	@Qualifier("messageSource")
	@Autowired
	private MessageSource messages;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private UserService userService;

	@Autowired
	private Environment env;

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event){
		this.confirmRegistration(event);
	}

	public void confirmRegistration(OnRegistrationCompleteEvent event) {
		UserEntity user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.createVerificationToken(user, token);

		String recipientAddress = user.getEmail();
		String subject = "Registration Confirmation";
		String confirmationUrl
				= event.getAppUrl() + "/registrationConfirm?token=" + token;
		String message = messages.getMessage("message.regSucc", null, event.getLocale());

		logger.info("sending mail");
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
		email.setFrom(env.getProperty("spring.mail.username"));
		javaMailSender.send(email);
	}
}
