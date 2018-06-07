package com.myorg.databricks.cluster;

import com.myorg.databricks.client.entities.clusters.SparkNodeAwsAttributesDTO;

public class SparkNodeAwsAttributes {
    public final Boolean IsSpot;

    public SparkNodeAwsAttributes(SparkNodeAwsAttributesDTO awsAttribInfo) {
        IsSpot = awsAttribInfo.IsSpot;
    }
}
