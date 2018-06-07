package com.myorg.error;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.myorg.util.JsonUtil;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ErrorMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer httpStatus;

  private String errorCode;

  private List<String> messages;

  private String errorDetails;

  public boolean hasErrors() {
    return ! messages.isEmpty();
  }

  
  public ErrorMessage() {
    super();
    this.messages = new ArrayList<>();
  }


  public ErrorMessage(Integer httpStatus, String errorCode, String message,
      String errorDetails) {
    super();
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.messages = new ArrayList<>();
    if(StringUtils.isNotBlank(message)) {
      this.messages.add(message);
    }
    this.errorDetails = errorDetails;
  }

  // public String errorMessage() {
  // return messages.stream().map(e -> e.message).collect(Collectors.joining(", "));
  // }

  public ErrorMessage addErrorDetails(String errorDetails) {
    this.errorDetails = this.errorDetails + "\n" + errorDetails;
    return this;
  }

  public ErrorMessage addMessage(String message) {
    messages.add(message);
    return this;
  }

  public String errorMessage() throws JsonGenerationException, JsonMappingException, IOException {
    return JsonUtil.createJsonFromObject(this);
  }

  public Integer getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(Integer httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  public String getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
  }
  
  @Override
  public String toString() {
    try {
      return JsonUtil.createJsonFromObject(this);
    } catch (IOException e) {
      return "{}";
    }
  }

}

