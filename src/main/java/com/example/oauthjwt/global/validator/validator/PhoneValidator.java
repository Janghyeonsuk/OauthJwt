package com.example.oauthjwt.global.validator.validator;

import com.example.oauthjwt.global.validator.customvalid.PhoneValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<PhoneValid,String> {

    private static final String PHONE_REGEX =
            "^01([0|1]?)-([0-9]{4})-([0-9]{4})$";
    private Pattern pattern;
    private String message;

    @Override
    public void initialize(PhoneValid constraintAnnotation) {
        pattern = Pattern.compile(PHONE_REGEX);
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if(phone==null)
            return false;
        boolean isValid = pattern.matcher(phone).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
