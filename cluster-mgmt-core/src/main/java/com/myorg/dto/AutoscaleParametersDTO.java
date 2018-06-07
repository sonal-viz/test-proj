package com.myorg.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Deprecated
public class AutoscaleParametersDTO {

  private String minWorkers;
  private String maxWorkers;

  public String getMinWorkers() {
    return minWorkers;
  }

  public void setMinWorkers(String minWorkers) {
    this.minWorkers = minWorkers;
  }

  public String getMaxWorkers() {
    return maxWorkers;
  }

  public void setMaxWorkers(String maxWorkers) {
    this.maxWorkers = maxWorkers;
  }

  @Override
  public String toString() {
    return "AutoscaleParametersDTO [minWorkers=" + minWorkers + ", maxWorkers=" + maxWorkers + "]";
  }

}
