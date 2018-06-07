package com.myorg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResizeClusterDTO {

  @JsonProperty("cluster_id")
  public String clusterId;

  @JsonProperty("num_workers")
  public Integer numWorkers;
  
  @JsonProperty("autoscale")
  public AutoScaleDTO autoscale;

}
