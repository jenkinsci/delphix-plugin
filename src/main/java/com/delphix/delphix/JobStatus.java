/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

/**
 * Tracks information about the status of a job.
 */
public class JobStatus {

    public enum StatusEnum {
        RUNNING, ABORTED, CANCELED, COMPLETED
    }

    /**
     * Current status of the job (RUNNING, ABORTED, etc)
     */
    private StatusEnum status;

    /**
     * Summary of what is being done currently for the job
     */
    private String summary;

    /**
     * Target object for the job
     */
    private String target;

    public JobStatus() {
        this.status = StatusEnum.RUNNING;
        this.summary = "";
        this.target = "";
    }

    public JobStatus(StatusEnum status, String summary, String target) {
        this.status = status;
        this.summary = summary;
        this.target = target;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }

    public String getTarget() {
        return target;
    }
}
