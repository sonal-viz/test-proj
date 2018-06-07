package com.myorg.databricks;

public class CreateClusterRequest {
  private String cluster_name;
  private String spark_version;
  private String node_type_id;
  private AutoscaleParameters autoscale;


  public String getCluster_name() {
    return cluster_name;
  }

  public void setCluster_name(String cluster_name) {
    this.cluster_name = cluster_name;
  }

  public String getSpark_version() {
    return spark_version;
  }

  public void setSpark_version(String spark_version) {
    this.spark_version = spark_version;
  }

  public String getNode_type_id() {
    return node_type_id;
  }

  public void setNode_type_id(String node_type_id) {
    this.node_type_id = node_type_id;
  }

  public AutoscaleParameters getAutoscale() {
    return autoscale;
  }

  public void setAutoscale(AutoscaleParameters autoscale) {
    this.autoscale = autoscale;
  }

  @Override
  public String toString() {
    return "CreateClusterRequest [cluster_name=" + cluster_name + ", spark_version=" + spark_version
        + ", node_type_id=" + node_type_id + ", autoscale=" + autoscale + "]";
  }
}
