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

package io.jenkins.plugins.delphix.objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Tracks information about the status of a job.
 */
public class Job {

    public enum StatusEnum {
        RUNNING, ABORTED, CANCELED, COMPLETED, FAILED
    }

    private StatusEnum status;
    private final String type;
    private final String reference;
    private final String namespace;
    private final String name;
    private final String actionType;
    private final String target;
    private final String targetObjectType;
    private final String jobState;
    private final String startTime;
    private final String updateTime;
    private final Boolean suspendable;
    private final Boolean cancelable;
    private final Boolean queued;
    private final String user;
    private final String emailAddresses;
    private final String title;
    private final String cancelReason;
    private final Integer percentComplete;
    private final String targetName;
    private final String parentActionState;
    private final String parentAction;

    public Job(
        StatusEnum status,
        String type,
        String reference,
        String namespace,
        String name,
        String actionType,
        String target,
        String targetObjectType,
        String jobState,
        String startTime,
        String updateTime,
        Boolean suspendable,
        Boolean cancelable,
        Boolean queued,
        String user,
        String emailAddresses,
        String title,
        String cancelReason,
        Integer percentComplete,
        String targetName,
        String parentActionState,
        String parentAction
    ) {
        this.status = StatusEnum.RUNNING;
        this.type = type;
        this.reference = reference;
        this.namespace = namespace;
        this.name = name;
        this.actionType = actionType;
        this.target = target;
        this.targetObjectType = targetObjectType;
        this.jobState = jobState;
        this.startTime = startTime;
        this.updateTime = updateTime;
        this.suspendable = suspendable;
        this.cancelable = cancelable;
        this.queued = queued;
        this.user = user;
        this.emailAddresses = emailAddresses;
        this.title = title;
        this.cancelReason = cancelReason;
        this.percentComplete = percentComplete;
        this.targetName = targetName;
        this.parentActionState = parentActionState;
        this.parentAction = parentAction;
        this.status = status;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public String getType() {
        return this.type;
    }

    public String getReference() {
        return this.reference;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public String getActionType() {
        return this.actionType;
    }

    public String getTarget() {
        return this.target;
    }

    public String getTargetObjectType() {
        return this.targetObjectType;
    }

    public String getJobState() {
        return this.jobState;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public Boolean getSuspendable() {
        return this.suspendable;
    }

    public Boolean getCancelable() {
        return this.cancelable;
    }

    public Boolean getQueued() {
        return this.queued;
    }

    public String getUser() {
        return this.user;
    }

    public String getEmailAddresses() {
        return this.emailAddresses;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCancelReason() {
        return this.cancelReason;
    }

    public Integer getPercentComplete() {
        return this.percentComplete;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public String getParentActionState() {
        return this.parentActionState;
    }

    public String getParentAction() {
        return this.parentAction;
    }

    public static Job fromJson(JsonNode json) {
        Job.StatusEnum statusEnum = Job.StatusEnum.valueOf(json.get("jobState").asText());
        Job status = new Job(
            statusEnum,
            json.get("type").asText(),
            json.get("reference").asText(),
            json.get("namespace").asText(),
            json.get("name").asText(),
            json.get("actionType").asText(),
            json.get("target").asText(),
            json.get("targetObjectType").asText(),
            json.get("jobState").asText(),
            json.get("startTime").asText(),
            json.get("updateTime").asText(),
            json.get("suspendable").asBoolean(),
            json.get("cancelable").asBoolean(),
            json.get("queued").asBoolean(),
            json.get("user").asText(),
            json.get("emailAddresses").asText(),
            json.get("title").asText(),
            json.get("cancelReason").asText(),
            json.get("percentComplete").asInt(),
            json.get("targetName").asText(),
            json.get("parentActionState").asText(),
            json.get("parentAction").asText()
        );
        return status;
    }
}
