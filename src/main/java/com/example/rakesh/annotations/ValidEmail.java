package com.example.rakesh.annotations;

import com.example.rakesh.common.EmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
	String message() default "invalid email";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

