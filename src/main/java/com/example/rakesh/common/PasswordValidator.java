package com.example.rakesh.common;

import com.example.rakesh.annotations.ValidPassword;
import com.example.rakesh.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, Object> {

	@Override
	public void initialize(ValidPassword constraintAnnotation) {

	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
		UserDTO user = (UserDTO) obj;
		return user.getPassword().equals(user.getMatchingPassword());
	}
}
