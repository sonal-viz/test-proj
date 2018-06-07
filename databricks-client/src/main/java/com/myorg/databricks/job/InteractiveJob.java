package com.myorg.databricks.job;

import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.jobs.JobSettingsDTO;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.library.Library;
import com.myorg.databricks.library.LibraryConfigException;
import java.net.URISyntaxException;
import java.util.List;

public abstract class InteractiveJob extends Job {

    public final InteractiveCluster Cluster;

    protected InteractiveJob(JobsClient client,
                             InteractiveCluster cluster,
                             long jobId,
                             JobSettingsDTO jobSettingsDTO)
            throws LibraryConfigException, URISyntaxException{
        super(client, jobId, jobSettingsDTO);
        Cluster = cluster;
    }
}
