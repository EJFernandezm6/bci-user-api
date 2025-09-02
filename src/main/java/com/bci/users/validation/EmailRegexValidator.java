package com.bci.users.validation;

import com.bci.users.config.ValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailRegexValidator implements ConstraintValidator<EmailRegex, String> {

    private final Pattern pattern;

    public EmailRegexValidator(ValidationProperties props) {
        this.pattern = Pattern.compile(props.getEmailRegex());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return pattern.matcher(value).matches();
    }
}
