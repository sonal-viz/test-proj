package com.myorg.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = CustomTagsValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTagsValidator {

  String message() default "Missing mandatory tags. Please add following tags with values: PRODUCT, TEAM, FUNCTION, OWNER, ENVIRONMENT (Make sure tags are all UpperCase)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
