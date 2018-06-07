package com.myorg.util;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.elasticmapreduce.model.Application;

public class Constants {

  public static class VertxEventBusTopics {

    public static final String GET_ALL_ARTICLES = "get-articles-all";
    public static final String CREATE_CLUSTER = "create-cluster";
    public static final String GET_CLUSTER = "get-cluster";
    public static final String START_CLUSTER = "start-cluster";
    public static final String RESTART_CLUSTER = "restart-cluster";
    public static final String RESIZE_CLUSTER = "resize-cluster";
    public static final String TERMINATE_CLUSTER = "terminate-cluster";

  }

  public static class Emr {
    public static List<Application> applications = getApplications();

    public static class StepType {
      public static final String HIVE = "HIVE";
      public static final String PIG = "PIG";
      public static final String SPARK = "SPARK";
    }

  // Hardcoded here, until we figure out how to pass creds
  public static final String RELEASE_LABEL = "emr-5.2.0";
  public static final String SERVICE_ROLE = "EMR_DefaultRole";
  public static final String JOB_FLOW_ROLE = "EMR_EC2_DefaultRole";
  public static final String EC2_KEY = "myorg-emr";
  private static final String MASTER_INSTANCE_TYPE = "m3.xlarge";
  private static final String SLAVE_INSTANCE_TYPE = "m3.xlarge";
  public static final String MASTER_BID_PRICE = "1.25";
  public static final String SLAVE_BID_PRICE = "1";

  public static final String ACCESS_KEY = "aws.emr.accesskey";
  public static final String SECRET_KEY = "aws.emr.secretkey";

  public static final String DEFAULT_TAG_KEY = "created_by_service";
  public static final String DEFAULT_TAG_VALUE = "Cluster Service";



  public static List<Application> getApplications() {
    List<Application> applications = new ArrayList<Application>();
    applications.add(new Application().withName("Hadoop"));
    applications.add(new Application().withName("Spark"));
    applications.add(new Application().withName("Hive"));
    applications.add(new Application().withName("Tez"));

    return applications;
  }


  }

  public static final String APPLICATION_PROPERTIES = "application.properties";
  
  
  // Prometheus Specific Constants
  public static final String METRICS_REGISTRY_NAME = "myregistry";
  public static final String METRICS_REGISTRY_CM = "metricsRegistryQS";
  public static final String METRICS_EVENT_BUS_ADDRESS = "myaddress";

}
