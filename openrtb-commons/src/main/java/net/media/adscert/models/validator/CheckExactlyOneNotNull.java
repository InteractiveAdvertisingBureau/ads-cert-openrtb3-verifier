package net.media.adscert.models.validator;

import org.apache.commons.beanutils.PropertyUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = CheckExactlyOneNotNull.CheckExactlyOneNotNullValidator.class)
@Documented
public @interface CheckExactlyOneNotNull {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] fieldNames();

  public class CheckExactlyOneNotNullValidator implements ConstraintValidator<CheckExactlyOneNotNull, Object> {
    private String[] fieldNames;
    public void initialize(CheckExactlyOneNotNull constraint) {
      this.fieldNames = constraint.fieldNames();
    }

    public boolean isValid(Object object, ConstraintValidatorContext context) {
      if (object == null) {
        return true;
      }
      boolean firstField = false;
      try {
        for (String fieldName:fieldNames){
          Object property = PropertyUtils.getProperty(object, fieldName);
          if(property!=null) {
            if(firstField) {
              ValidatorErrorHandler.setErrorMessage(context, "exactly one of the following should be present: " + Arrays.toString(fieldNames));
              return false;
            }
            firstField = true;
          }
        }
        if(firstField) {
          return true;
        }
        ValidatorErrorHandler.setErrorMessage(context, "Exactly one of the following should be present: " + fieldNames);
        return false;
      } catch (Exception e) {
        ValidatorErrorHandler.setErrorMessage(context, "Exactly one of the following should be present: " + fieldNames);
        return false;
      }
    }
  }
}