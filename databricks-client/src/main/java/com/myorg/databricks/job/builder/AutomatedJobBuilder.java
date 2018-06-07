package com.myorg.databricks.job.builder;

import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.client.entities.jobs.JobSettingsDTO;

public abstract class AutomatedJobBuilder extends JobBuilder {
    private ClusterInfoDTO _clusterInfo;
    public AutomatedJobBuilder() {
        super();
    }

    public AutomatedJobBuilder withClusterInfo(ClusterInfoDTO clusterInfoDTO) {
        _clusterInfo = clusterInfoDTO;
        return this;
    }

    @Override
    protected JobSettingsDTO applySettings(JobSettingsDTO jobSettingsDTO) {
        jobSettingsDTO = super.applySettings(jobSettingsDTO);
        jobSettingsDTO.NewCluster = _clusterInfo;

        //TODO parse cron schedule expression
        //https://stackoverflow.com/questions/3641575/how-to-get-cron-expression-given-job-name-and-group-name
        //jobSettingsDTO.Schedule = ;

        //TODO add libraries to DTO

        return jobSettingsDTO;
    }

}
