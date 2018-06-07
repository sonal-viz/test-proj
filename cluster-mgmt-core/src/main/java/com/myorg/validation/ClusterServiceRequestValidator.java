package com.myorg.validation;

import java.io.IOException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.myorg.dto.ClusterInfoDTO;
import com.myorg.error.ErrorMessage;
import com.myorg.util.JsonUtil;

@Component
public class ClusterServiceRequestValidator extends ServiceRequestValidator {
  private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private static Validator validator = factory.getValidator();

  public void validateCreateClusterRequest(ClusterInfoDTO clusterInfoDTO) throws IOException {
    ErrorMessage errorMessage = new ErrorMessage();

    final Set<ConstraintViolation<ClusterInfoDTO>> violations = validator.validate(clusterInfoDTO);

    for (final ConstraintViolation<ClusterInfoDTO> violation : violations) {
      errorMessage.addMessage(violation.getPropertyPath().toString() + " : "
          + violation.getInvalidValue().toString() + " : " + violation.getMessage());
    }
    if (errorMessage.hasErrors()) {
      errorMessage.setHttpStatus(HttpStatus.BAD_REQUEST.value());
      throw new IllegalArgumentException(JsonUtil.createJsonFromObject(errorMessage));
    }
  }
}
