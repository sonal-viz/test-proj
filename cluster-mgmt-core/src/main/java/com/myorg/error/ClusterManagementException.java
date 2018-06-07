package com.myorg.error;

public class ClusterManagementException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private ErrorMessage errorMessage;

  public ClusterManagementException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClusterManagementException(ErrorMessage errorMessage, Throwable cause) {
    super(errorMessage.toString(), cause);
    this.setErrorMessage(errorMessage);
  }

  public ErrorMessage getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(ErrorMessage errorMessage) {
    this.errorMessage = errorMessage;
  }

  public void addMessage(String infoMessage) {
    this.errorMessage.addMessage(infoMessage);
  }
}
