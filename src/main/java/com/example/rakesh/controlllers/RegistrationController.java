package com.example.rakesh.controlllers;

import com.example.rakesh.dto.UserDTO;
import com.example.rakesh.events.OnRegistrationCompleteEvent;
import com.example.rakesh.mysql.model.UserEntity;
import com.example.rakesh.mysql.model.VerificationToken;
import com.example.rakesh.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.logging.log4j.util.EnglishEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Controller
@Validated
public class RegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private UserService userService;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	Environment environment;

	@Qualifier("messageSource")
	@Autowired
	private MessageSource messages;

	@GetMapping("/login")
	public String showLoginPage(WebRequest request, Model model) {
		logger.info("login");
		return "login";
	}

	@GetMapping("/badUser")
	public String showBadUser(WebRequest request, Model model) {
		logger.info("badUser");
		return "badUser";
	}

	@GetMapping("/user/registration")
	public String showRegistrationForm(WebRequest request, Model model) {
		UserDTO userDTO = new UserDTO();

		model.addAttribute("user", userDTO);
		return "registration";
	}

	@PostMapping("/user/registration")
	public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDTO userDTO,
								   HttpServletRequest request, Errors errors) {

		logger.info("Registering user account with information: {}", userDTO);

		try {
			UserEntity registered = userService.registerNewUserAccount(userDTO);
//			new ModelAndView("login", "user", userDTO);

			String appUrl = request.getContextPath();

			applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ModelAndView mav = new ModelAndView("registration", "user", userDTO);
			String errMessage = messages.getMessage("message.regError", null, request.getLocale());
			mav.addObject("message", errMessage);
			return mav;
		}

		return new ModelAndView("successRegister", "user", userDTO);
	}

	@GetMapping("/registrationConfirm")
	public String confirmRegistration(HttpServletRequest request, Model model, @RequestParam("token") String token) {

		VerificationToken verificationToken = userService.getVerificationToken(token);

		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, Locale.ENGLISH);
			model.addAttribute("message", message);
			return "redirect:/badUser";
		}

		UserEntity user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			String messageValue = messages.getMessage("auth.message.expired", null, Locale.ENGLISH);
			model.addAttribute("message", messageValue);
			model.addAttribute("expired", true);
			model.addAttribute("token", token);
			return "redirect:/badUser";
		}

		user.setEnabled(true);
		userService.saveUser(user);
		userService.deleteVerificationToken(verificationToken);
		return "redirect:/login";
	}

	@GetMapping("/user/resendRegistrationToken")
	public String resendRegistrationToken(final HttpServletRequest request, final Model model, @RequestParam("token") final String existingToken) {
		final Locale locale = request.getLocale();
		final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
		final UserEntity user = userService.getUser(newToken.getToken());
		try {
			final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			final SimpleMailMessage email = constructResetVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
			javaMailSender.send(email);
		} catch (final MailAuthenticationException e) {
			logger.debug("MailAuthenticationException", e);
			return "redirect:/emailError.html";
		} catch (final Exception e) {
			logger.debug(e.getLocalizedMessage(), e);
			model.addAttribute("message", e.getLocalizedMessage());
			return "redirect:/login.html";
		}
		model.addAttribute("message", messages.getMessage("message.resendToken", null, locale));
		return "redirect:/login.html";
	}

	private final SimpleMailMessage constructResetVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final UserEntity user) {
		final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
		final String message = messages.getMessage("message.resendToken", null, locale);
		final SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Resend Registration Token");
		email.setText(message + " \r\n" + confirmationUrl);
		email.setTo(user.getEmail());
		email.setFrom(environment.getProperty("support.email"));
		return email;
	}
}
