package com.myorg.databricks;

public class AutoscaleParameters {

  private String min_workers;
  private String max_workers;

  public String getMin_workers() {
    return min_workers;
  }

  public void setMin_workers(String min_workers) {
    this.min_workers = min_workers;
  }

  public String getMax_workers() {
    return max_workers;
  }

  public void setMax_workers(String max_workers) {
    this.max_workers = max_workers;
  }

  @Override
  public String toString() {
    return "AutoscaleRequest [min_workers=" + min_workers + ", max_workers=" + max_workers + "]";
  }

}
