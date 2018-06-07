package com.myorg.databricks.cluster;

import java.math.BigInteger;
import com.myorg.databricks.client.entities.clusters.SparkNodeDTO;

public class SparkNode {
    public final NodeType NodeType;
    public final String PrivateIP;
    public final String PublicDNS;
    public final String NodeId;
    public final String InstanceId;
    public final BigInteger StartTimestamp;
    public final SparkNodeAwsAttributes NodeAwsAttributes;
    public final String HostPrivateIP;

    public SparkNode(SparkNodeDTO sparkNodeDTOInfo, NodeType nodeType) {
        PrivateIP = sparkNodeDTOInfo.PrivateIP;
        PublicDNS = sparkNodeDTOInfo.PublicDNS;
        NodeId = sparkNodeDTOInfo.NodeId;
        InstanceId = sparkNodeDTOInfo.InstanceId;
        StartTimestamp = sparkNodeDTOInfo.StartTimestamp;
        NodeAwsAttributes = sparkNodeDTOInfo.NodeAwsAttributes == null ? null
                : new SparkNodeAwsAttributes(sparkNodeDTOInfo.NodeAwsAttributes);
        HostPrivateIP = sparkNodeDTOInfo.HostPrivateIP;
        NodeType = nodeType;
    }
}
