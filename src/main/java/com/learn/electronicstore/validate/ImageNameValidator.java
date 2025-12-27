package com.learn.electronicstore.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageNameValidator implements ConstraintValidator<ImageNameValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("isValid", value);
        if (value.isBlank())
            return false;
        else
            return true;
    }
}
