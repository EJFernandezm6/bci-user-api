package com.bci.users.validation;

import com.bci.users.config.ValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordRegexValidator implements ConstraintValidator<PasswordRegex, String> {

    private final Pattern pattern;

    public PasswordRegexValidator(ValidationProperties props) {
        this.pattern = Pattern.compile(props.getPasswordRegex());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return pattern.matcher(value).matches();
    }
}
