package com.example.oauthjwt.global.validator.validator;

import com.example.oauthjwt.global.validator.customvalid.EmailValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailValid,String> {

    private static final String EMAIL_REGEX =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    private String message;

    @Override
    public void initialize(EmailValid constraintAnnotation) {
        pattern = Pattern.compile(EMAIL_REGEX);
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email==null)
            return false;

        boolean isValid = pattern.matcher(email).matches();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
