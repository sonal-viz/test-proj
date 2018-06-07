package com.myorg.apptest;

import org.junit.Test;

import com.myorg.auth.AuthUtils;

public class AuthUtilsTest {
  @Test
  public void validTokenStringMustBeValidated() {
    String tokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM";

    assert AuthUtils.isTokenAuthenticated(tokenString);
  }

  @Test
  public void invalidTokenStringMustNotBeValidated() {
    String tokenString = "this.is.invalid!!!!";

    assert !AuthUtils.isTokenAuthenticated(tokenString);
  }

}
