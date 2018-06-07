package com.myorg.util;

public enum ClusterPlatform {

  DATABRICKS("databricks"), EMR("emr");

  private String value;

  ClusterPlatform(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
