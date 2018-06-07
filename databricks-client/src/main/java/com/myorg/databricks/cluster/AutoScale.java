package com.myorg.databricks.cluster;

import com.myorg.databricks.client.entities.clusters.AutoScaleDTO;

public class AutoScale {
    public final Integer MinWorkers;
    public final Integer MaxWorkers;

    public AutoScale(Integer minWorkers, Integer maxWorkers) {
        MinWorkers = minWorkers;
        MaxWorkers = maxWorkers;
    }

    public AutoScale(AutoScaleDTO autoScaleDTOInfo) {
        MinWorkers = autoScaleDTOInfo.MinWorkers;
        MaxWorkers = autoScaleDTOInfo.MaxWorkers;
    }
}
