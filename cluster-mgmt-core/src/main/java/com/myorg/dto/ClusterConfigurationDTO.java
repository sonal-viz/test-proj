package com.myorg.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.myorg.util.ClusterPlatform;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Deprecated
public class ClusterConfigurationDTO {


  private String clusterName;
  private String sparkVersion;
  private String nodeTypeId;
  private AutoscaleParametersDTO autoscale;
  private ClusterPlatform platform;

  public ClusterPlatform getPlatform() {
    return platform;
  }

  public void setPlatform(ClusterPlatform platform) {
    this.platform = platform;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getSparkVersion() {
    return sparkVersion;
  }

  public void setSparkVersion(String sparkVersion) {
    this.sparkVersion = sparkVersion;
  }

  public String getNodeTypeId() {
    return nodeTypeId;
  }

  public void setNodeTypeId(String nodeTypeId) {
    this.nodeTypeId = nodeTypeId;
  }

  public AutoscaleParametersDTO getAutoscale() {
    return autoscale;
  }

  public void setAutoscale(AutoscaleParametersDTO autoscale) {
    this.autoscale = autoscale;
  }

  @Override
  public String toString() {
    return "ClusterConfigurationDTO [clusterName=" + clusterName + ", sparkVersion=" + sparkVersion
        + ", nodeTypeId=" + nodeTypeId + ", autoscale=" + autoscale + ", platform=" + platform
        + "]";
  }

}
