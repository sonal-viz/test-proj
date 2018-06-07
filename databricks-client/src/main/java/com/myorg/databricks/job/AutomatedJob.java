package com.myorg.databricks.job;

import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.jobs.JobSettingsDTO;
import com.myorg.databricks.cluster.ClusterSpec;
import com.myorg.databricks.library.LibraryConfigException;
import java.net.URISyntaxException;

public abstract class AutomatedJob extends Job {
    public final ClusterSpec ClusterSpec;

    protected AutomatedJob(JobsClient client, long jobId, JobSettingsDTO jobSettingsDTO) throws URISyntaxException, LibraryConfigException {
        super(client, jobId, jobSettingsDTO);
        ClusterSpec = new ClusterSpec(jobSettingsDTO.NewCluster);
    }


}
