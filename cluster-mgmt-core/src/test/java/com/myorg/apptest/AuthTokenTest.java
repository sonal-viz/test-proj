package com.myorg.apptest;

import org.junit.Test;

import com.myorg.auth.AuthToken;

public class AuthTokenTest {

  @Test(expected = IllegalArgumentException.class)
  public void userNameShouldNotContainSpaces() {
    AuthToken authToken = new AuthToken();
    authToken.setUserName("miq user");
  }

}
