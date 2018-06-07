package com.myorg.databricks.job;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.client.entities.jobs.RunDTO;
import com.myorg.databricks.cluster.ClusterConfigException;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.library.LibraryConfigException;
import java.net.URISyntaxException;

abstract public class InteractiveJobRun extends JobRun {
    private InteractiveCluster _cluster;
    private JobsClient _client;
    private boolean _clusterCreated = false;

    protected InteractiveJobRun(JobsClient client, RunDTO runDTO) throws JobRunException, LibraryConfigException, URISyntaxException {
        super(client, runDTO);
        _client = client;

        validateJobRun(runDTO);
    }

    public InteractiveCluster getCluster() throws HttpException, ClusterConfigException, JobRunException {
        if (_cluster == null) {
            RunDTO run = _client.getRun(this.RunId);
            validateJobRun(run);
            ClustersClient clusterClient = new ClustersClient(_client.Session);
            ClusterInfoDTO clusterInfo = clusterClient.getCluster(run.ClusterInstance.ClusterId);
            _cluster = new InteractiveCluster(clusterClient, clusterInfo);
        }
        return _cluster;
    }

    private void validateJobRun(RunDTO runDTO) throws JobRunException {
        if (runDTO.ClusterInstance == null) {
            _clusterCreated = false;
            return;
        } else if(runDTO.ClusterInstance.ClusterId == null) {
            _clusterCreated = false;
            return;
        } else {
            _clusterCreated = true;
            if(runDTO.ClusterSpec.ExistingClusterId == null) {
                throw new JobRunException("JobRun is not associated with an interactive cluster");
            } else {
                return;
            }
        }
    }
}
