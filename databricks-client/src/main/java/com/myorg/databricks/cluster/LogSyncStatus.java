package com.myorg.databricks.cluster;

import java.math.BigInteger;
import com.myorg.databricks.client.entities.clusters.LogSyncStatusDTO;

public class LogSyncStatus {
    public final BigInteger LastAttempted;
    public final String LastException;

    public LogSyncStatus(LogSyncStatusDTO statusInfo) {
        LastAttempted = statusInfo.LastAttempted;
        LastException = statusInfo.LastException;
    }
}
