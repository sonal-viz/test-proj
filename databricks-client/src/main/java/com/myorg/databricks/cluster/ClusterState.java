package com.myorg.databricks.cluster;

public enum ClusterState {
    PENDING, RUNNING, RESTARTING, RESIZING, TERMINATING, TERMINATED, ERROR, UNKNOWN
}