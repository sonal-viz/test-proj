package com.myorg.util;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class MonitoringUtil {

  // Create Cluster
  public static final Counter createClusterCounter =
      Counter.build().name("create_cluster_counter").help("Number of clusters created").register();

  public static final Histogram createClusterLatency = Histogram.build()
      .name("create_cluster_latency").help("create cluster requests in seconds").register();
  
  // Get Cluster
  public static final Counter getClusterCounter =
      Counter.build().name("get_cluster_counter").help("Number of times getCluster called").register();

  public static final Histogram getClusterLatency = Histogram.build()
      .name("get_cluster_latency").help("Get cluster requests in seconds").register();


  // start cluster
  public static final Counter startClusterCounter = Counter.build().name("start_cluster_counter")
      .help("Number of Times clusters are started").register();

  public static final Histogram startClusterLatency = Histogram.build()
      .name("start_cluster_latency").help("start cluster requests in seconds").register();


  // restart cluster
  public static final Counter restartClusterCounter = Counter.build().name("restart_cluster_counter")
      .help("Number of times clusters are re-started").register();

  public static final Histogram restartedClusterLatency = Histogram.build()
      .name("restart_cluster_latency").help("restart cluster requests in seconds").register();

  
  // resize cluster
  public static final Counter resizeClusterCounter = Counter.build().name("resize_cluster_counter")
      .help("Number of times clusters are resized").register();

  public static final Histogram resizeClusterLatency = Histogram.build()
      .name("resize_cluster_latency").help("resize cluster requests in seconds").register();


  // terminate cluster
  public static final Counter terminateClusterCounter = Counter.build()
      .name("terminate_cluster_counter").help("Number of clusters got terminated").register();

  public static final Histogram terminateClusterLatency = Histogram.build()
      .name("terminate_cluster_latency").help("terminate cluster requests in seconds").register();

  private MonitoringUtil() {
    // To defeat object instantiation
  }

}
