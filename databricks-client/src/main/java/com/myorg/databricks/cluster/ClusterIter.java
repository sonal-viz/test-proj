package com.myorg.databricks.cluster;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import java.util.Iterator;

public class ClusterIter implements Iterator<InteractiveCluster> {
    private ClustersClient _client;
    private ClusterInfoDTO[] _clusterInfoDTOs;
    private int _clusterInfoIndex = 0;

    public ClusterIter(ClustersClient client, ClusterInfoDTO[] clusterInfoDTOs) {
        _client = client;
        _clusterInfoDTOs = clusterInfoDTOs;
    }

    public boolean hasNext() {
        return _clusterInfoIndex < _clusterInfoDTOs.length;
    }

    public InteractiveCluster next() throws RuntimeException {
        try {
            InteractiveCluster cluster = new InteractiveCluster(_client, _clusterInfoDTOs[_clusterInfoIndex]);
            _clusterInfoIndex++;
            return cluster;
        } catch (HttpException e) {
            throw(new RuntimeException(e));
        } catch (ClusterConfigException e) {
            throw(new RuntimeException(e));
        }
    }

    public InteractiveCluster first() throws RuntimeException {
        try {
            return new InteractiveCluster(_client, _clusterInfoDTOs[0]);
        } catch (HttpException e) {
            throw(new RuntimeException(e));
        } catch (ClusterConfigException e) {
            throw(new RuntimeException(e));
        }
    }

    public void remove() {
        //No Op
    }

}
