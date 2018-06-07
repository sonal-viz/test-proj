package com.myorg.client;

import java.io.IOException;

import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.ResizeClusterDTO;
import com.myorg.entity.ClusterInfo;

public interface ClusterClient {
  public ClusterInfo createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException;

  public ClusterInfo getCluster(ClusterInfo clusterInfo) throws IOException;

  public String startCluster(String clusterId) throws IOException;

  public String restartCluster(String clusterId) throws IOException;

  public String resizeCluster(ResizeClusterDTO resizeClusterDTO) throws IOException;

  public String terminateCluster(ClusterInfo cluster) throws IOException;

}
