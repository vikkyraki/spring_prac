package com.example.rakesh.annotations;

import com.example.rakesh.common.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
	String message() default "Passwords don't match";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
