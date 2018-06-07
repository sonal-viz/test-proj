package com.myorg.auth;

import org.apache.commons.lang.StringUtils;

public class AuthToken {

  private static char[] illegalChars = {' ', '/'};

  private Integer exp;
  private String userName;
  private String clientId;
  private String[] scope;
  private String[] authorities;

  public Integer getExp() {
    return exp;
  }

  public void setExp(Integer exp) {
    this.exp = exp;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) throws IllegalArgumentException {
    if (StringUtils.containsAny(userName, illegalChars))
      throw new IllegalArgumentException("Username cannot contain spaces or slashes");
    else
      this.userName = userName;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String[] getScopes() {
    return scope;
  }

  public void setScope(String[] scope) {
    this.scope = scope;
  }

  public String[] getAuthorities() {
    return authorities;
  }

  public void setAuthorities(String[] authorities) {
    this.authorities = authorities;
  }

}

