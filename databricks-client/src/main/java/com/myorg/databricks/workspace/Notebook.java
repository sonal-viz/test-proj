package com.myorg.databricks.workspace;

import com.myorg.databricks.job.builder.AutomatedNotebookJobBuilder;

public class Notebook {
    public final String Path;

    public Notebook(String workspacePath) {
        Path = workspacePath;
    }

}
