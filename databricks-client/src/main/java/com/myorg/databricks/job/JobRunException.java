package com.myorg.databricks.job;

public class JobRunException extends Exception {
    public JobRunException() {
        super();
    }

    public JobRunException(String message) {
        super(message);
    }

    public JobRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobRunException(Throwable cause) {
        super(cause);
    }
}
