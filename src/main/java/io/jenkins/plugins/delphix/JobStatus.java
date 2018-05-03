/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;

/**
 * Tracks information about the status of a job.
 */
public class JobStatus {

    public enum StatusEnum {
        RUNNING, ABORTED, CANCELED, COMPLETED, FAILED
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

    /**
     * English name of the target
     */
    private String targetName;

    /**
     * Action type
     */
    private String actionType;

    public JobStatus() {
        this.status = StatusEnum.RUNNING;
        this.summary = "";
        this.target = "";
        this.actionType = "";
    }

    public JobStatus(StatusEnum status, String summary, String target, String targetName, String actionType) {
        this.status = status;
        this.summary = summary;
        this.target = target;
        this.targetName = targetName;
        this.actionType = actionType;
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

    public String getTargetName() {
        return targetName;
    }

    public String getActionType() {
        return actionType;
    }
}
