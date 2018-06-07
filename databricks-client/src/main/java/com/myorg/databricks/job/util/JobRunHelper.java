package com.myorg.databricks.job.util;

import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.jobs.JobRunOutputDTO;
import com.myorg.databricks.job.JobRunException;
import com.myorg.databricks.job.RunLifeCycleState;

public class JobRunHelper {

    public static String getJobRunOutput(JobsClient client, long runId) throws HttpException, JobRunException {
        //otherwise, get the notebook output, if any
        JobRunOutputDTO jobRunOutput = client.getRunOutput(runId);

        if(jobRunOutput.NotebookOutput != null) {
            if(jobRunOutput.NotebookOutput.Result != null) {
                return jobRunOutput.NotebookOutput.Result;
            }
        } else if(jobRunOutput.Error != null) {
            throw new JobRunException(jobRunOutput.Error);
        }

        //No Job Run Output was found; Nor was an error
        return null;
    }
}
