package com.bci.users.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordRegexValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordRegex {
    String message() default "Password inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
