package com.myorg.databricks.job;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.client.entities.jobs.RunDTO;
import com.myorg.databricks.cluster.AutomatedCluster;
import com.myorg.databricks.cluster.ClusterConfigException;
import com.myorg.databricks.cluster.ClusterSpec;
import com.myorg.databricks.library.LibraryConfigException;
import java.net.URISyntaxException;

public abstract class AutomatedJobRun extends JobRun {
    private AutomatedCluster _cluster;
    private JobsClient _client;
    private boolean _clusterCreated = false;

    public final ClusterSpec NewClusterSpec;

    protected AutomatedJobRun(JobsClient client, RunDTO runDTO) throws JobRunException, LibraryConfigException, URISyntaxException {
        super(client, runDTO);
        _client = client;

        validateJobRun(runDTO);
        NewClusterSpec = new ClusterSpec(runDTO.ClusterSpec.NewCluster);
    }

    public AutomatedCluster getCluster() throws HttpException, ClusterConfigException, JobRunException {
        if (_cluster == null) {
            RunDTO run = _client.getRun(this.RunId);
            validateJobRun(run);
            if(_clusterCreated) {
                ClustersClient clusterClient = new ClustersClient(_client.Session);
                ClusterInfoDTO clusterInfo = clusterClient.getCluster(run.ClusterInstance.ClusterId);
                _cluster = new AutomatedCluster(clusterClient, clusterInfo);
            }
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
            if(runDTO.ClusterSpec.NewCluster == null) {
                throw new JobRunException("JobRun is not associated with an automated cluster");
            } else {
                return;
            }
        }
    }



}
