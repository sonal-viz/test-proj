package com.myorg.databricks;

public class CreateClusterResponse {

  private String cluster_id;

  public String getCluster_id() {
    return cluster_id;
  }

  public void setCluster_id(String cluster_id) {
    this.cluster_id = cluster_id;
  }

  @Override
  public String toString() {
    return "CreateClusterResponse [cluster_id=" + cluster_id + "]";
  }

}
