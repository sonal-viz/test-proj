package com.myorg.validation;

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class CustomTagsValidatorImpl
    implements ConstraintValidator<CustomTagsValidator, Map<String, String>> {

  @Override
  public boolean isValid(final Map<String, String> customTags,
      final ConstraintValidatorContext constraintCtx) {
    if (customTags.containsKey("TEAM") && StringUtils.isNotBlank(customTags.get("TEAM"))
        && customTags.containsKey("PRODUCT") && StringUtils.isNotBlank(customTags.get("PRODUCT"))
        && customTags.containsKey("OWNER") && StringUtils.isNotBlank(customTags.get("OWNER"))
        && customTags.containsKey("FUNCTION") && StringUtils.isNotBlank(customTags.get("FUNCTION"))
        && customTags.containsKey("ENVIRONMENT")
        && StringUtils.isNotBlank(customTags.get("ENVIRONMENT"))) {
      return true;
    }
    return false;
  }

}
