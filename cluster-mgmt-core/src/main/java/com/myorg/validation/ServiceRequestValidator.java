package com.myorg.validation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.myorg.error.ErrorMessage;

public class ServiceRequestValidator {

  public void validateNotNullorEmpty(final String fieldValue, final String fieldName) throws JsonGenerationException, JsonMappingException, IOException {
    ErrorMessage errorMessage = new ErrorMessage();
    validateNotNullorEmpty(fieldValue, fieldName, errorMessage);
  }

  public void validateNotNullorEmpty(final String fieldValue, final String fieldName,
      final ErrorMessage errorMessage) throws JsonGenerationException, JsonMappingException, IOException {
    if (StringUtils.isBlank(fieldValue)) {
      errorMessage.addMessage(fieldName + " should not be empty.");
      throw new IllegalArgumentException(errorMessage.errorMessage());
    }
  }

  public ErrorMessage validation() throws JsonGenerationException, JsonMappingException, IOException {
    ErrorMessage errorMessage = new ErrorMessage();
    validateDate("Date", errorMessage);
    return errorMessage;
  }

  public void check() throws JsonGenerationException, JsonMappingException, IOException {
    if (validation().hasErrors())
      throw new IllegalArgumentException(validation().errorMessage());
  }

  public void validateDate(final String date, ErrorMessage errorMessage) throws JsonGenerationException, JsonMappingException, IOException {
    if (date == null) {
      errorMessage.addMessage("Date is missing.");
      return;
    }
    LocalDate parsedDate;
    try {
      parsedDate = LocalDate.parse(date);
    } catch (final DateTimeParseException e) {
      errorMessage.addMessage("Invalid format for date. Reason: " + e.getMessage());
      return;
    }
    if (parsedDate.isBefore(LocalDate.now()))
      errorMessage.addMessage("Date cannot be before today.");
    throw new IllegalArgumentException(errorMessage.errorMessage());
  }

  public void checkIfErrorOccured(ErrorMessage errorMessage) throws JsonGenerationException, JsonMappingException, IOException {
    if (errorMessage.hasErrors())
      throw new IllegalArgumentException(errorMessage.errorMessage());
  }

}
