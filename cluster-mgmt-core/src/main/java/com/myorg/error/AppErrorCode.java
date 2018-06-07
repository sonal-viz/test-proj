package com.myorg.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Errors managed by Cluster Service. This class has been introduced so that slowly it will
 * contain all the Cluster Service managed errors.
 *
 */
public class AppErrorCode {

  private static Map<String, String> errorCodeMap;

  public static final String REQUEST_VALIDATION_ERROR = "REQUEST_VALIDATION_ERROR";
  public static final String DATABASE_ERROR = "DATABASE_ERROR";
  public static final String DATABASE_DUPLICATE_ENTRY = "DATABASE_DUPLICATE_ENTRY";

  public static final String DATABRICKS_SERVER_ERROR = "DATABRICKS_SERVER_ERROR";
  public static final String DATABRICKS_CLIENT_ERROR = "DATABRICKS_CLIENT_ERROR";

  public static final String CLUSTER_SERVICE_ERROR = "CLUSTER_SERVICE_ERROR";

  public static final String EMR_SERVICE_ERROR = "EMR_SERVICE_ERROR";
  public static final String EMR_CLIENT_ERROR = "EMR_CLIENT_ERROR";

  static {
    errorCodeMap = new HashMap<>();
    errorCodeMap.put(DATABASE_ERROR, "An error occurred in the database");
    errorCodeMap.put(DATABASE_DUPLICATE_ENTRY, "The entry already exists");
    errorCodeMap.put(DATABRICKS_SERVER_ERROR, "Error occured in databricks cloud server.");
    errorCodeMap.put(DATABRICKS_CLIENT_ERROR, "Error occured in databricks client.");
    errorCodeMap.put(CLUSTER_SERVICE_ERROR, "Error occured in cluster service app.");

  }

  public static String getErrorMessage(String code) {
    return errorCodeMap.get(code);
  }
}
