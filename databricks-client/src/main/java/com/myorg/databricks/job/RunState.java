package com.myorg.databricks.job;

import com.myorg.databricks.client.entities.jobs.RunStateDTO;

public class RunState {
    public final RunLifeCycleState LifeCycleState;
    public final RunResultState ResultState;
    public final String StateMessage;

    public RunState(RunStateDTO runStateDTO) {
        LifeCycleState = RunLifeCycleState.valueOf(runStateDTO.LifeCycleState);
        if(runStateDTO.ResultState != null) {
            ResultState = RunResultState.valueOf(runStateDTO.ResultState);
        } else {
            ResultState = null;
        }
        StateMessage = runStateDTO.StateMessage;
    }
}
