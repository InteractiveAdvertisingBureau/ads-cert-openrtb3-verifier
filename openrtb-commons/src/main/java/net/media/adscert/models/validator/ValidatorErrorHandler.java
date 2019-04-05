package net.media.adscert.models.validator;

import javax.validation.ConstraintValidatorContext;

public class ValidatorErrorHandler {
  public static void setErrorMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
