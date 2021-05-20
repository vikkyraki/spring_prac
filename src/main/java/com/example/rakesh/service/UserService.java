package com.example.rakesh.service;

import com.example.rakesh.dto.UserDTO;
import com.example.rakesh.mysql.builder.UserPOBuilder;
import com.example.rakesh.mysql.dao.TokenRepository;
import com.example.rakesh.mysql.dao.UserRepository;
import com.example.rakesh.mysql.model.UserEntity;
import com.example.rakesh.mysql.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private ApplicationContext applicationContext;

	@Transactional
	public UserEntity registerNewUserAccount(UserDTO userDto)
			throws Exception {

		if (emailExist(userDto.getEmail())) {
			throw new Exception(
					"There is an account with that email address: "
							+  userDto.getEmail());
		}

		return userRepository.save(applicationContext.getBean(UserPOBuilder.class).build(userDto));
		// the rest of the registration operation
	}

	public UserEntity saveUser(UserEntity userEntity) {
		return userRepository.save(userEntity);
	}

	private boolean emailExist(String email) {
		return userRepository.findByEmail(email) != null;
	}


	public VerificationToken getVerificationToken(final String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}

	public void createVerificationToken(UserEntity user, String token) {
		VerificationToken verificationToken = new VerificationToken(token, user);
		tokenRepository.save(verificationToken);
	}

	public void deleteVerificationToken(VerificationToken token) {
		tokenRepository.delete(token);
	}

	public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
		VerificationToken token = tokenRepository.findByToken(existingVerificationToken);
		token.updateToken(UUID.randomUUID().toString());
		token = tokenRepository.save(token);
		return token;
	}

	public UserEntity getUser(final String verificationToken) {
		final VerificationToken token = tokenRepository.findByToken(verificationToken);
		if (token != null) {
			return token.getUser();
		}
		return null;
	}

}
