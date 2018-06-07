package com.myorg.databricks.cluster;

import com.myorg.databricks.client.entities.clusters.NodeTypeDTO;

public class NodeType {
    public final String Id;
    public final Integer MemoryMB;
    public final Float NumCores;
    public final String Description;
    public final String InstanceTypeId;
    public final Boolean IsDeprecated;

    public NodeType(NodeTypeDTO nodeTypeDTOInfo) {
        Id = nodeTypeDTOInfo.Id;
        MemoryMB = nodeTypeDTOInfo.MemoryMB;
        NumCores = nodeTypeDTOInfo.NumCores;
        Description = nodeTypeDTOInfo.Description;
        InstanceTypeId = nodeTypeDTOInfo.InstanceTypeId;
        IsDeprecated = nodeTypeDTOInfo.IsDeprecated;
    }
}
