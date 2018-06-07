package com.myorg.auth;

public class InvalidAuthenticationToken extends Exception {

  public InvalidAuthenticationToken() {
    super();
  }

  public InvalidAuthenticationToken(String message) {
    super(message);
  }

  public InvalidAuthenticationToken(Throwable cause) {
    super(cause);
  }

  public InvalidAuthenticationToken(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAuthenticationToken(String message, Throwable cause, boolean enableSuppression,
      boolean writeableStackTrace) {
    super(message, cause, enableSuppression, writeableStackTrace);
  }

}
