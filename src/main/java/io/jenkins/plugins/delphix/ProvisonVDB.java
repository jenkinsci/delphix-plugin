package io.jenkins.plugins.delphix;

import java.util.List;

import org.kohsuke.stapler.DataBoundSetter;

import hudson.tasks.Builder;

public abstract class ProvisonVDB extends Builder {
    public Boolean autoSelectRepository;
    public List<Tags> tagList;
    public Boolean waitForPolling;
    public String credentialId;
    public String name;
    public String environmentId;
    public String jsonParam;
    public String sourceDataId;
    public String environmentUserId;
    public String repositoryId;
    public String engineId;
    public String targetGroupId;
    public String databaseName;
    public boolean vdbRestart;
    public String snapshotPolicyId;
    public String retentionPolicyId;

    public String getName() {
        return name;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public List<Tags> getTagList() {
        return tagList;
    }

    public Boolean getAutoSelectRepository() {
        return autoSelectRepository;
    }

    public Boolean getWaitForPolling() {
        return waitForPolling;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public String getSourceDataId() {
        return sourceDataId;
    }

    public String getEnvironmentUserId() {
        return environmentUserId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getEngineId() {
        return engineId;
    }

    public String getTargetGroupId() {
        return targetGroupId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isVdbRestart() {
        return vdbRestart;
    }

    public String getSnapshotPolicyId() {
        return snapshotPolicyId;
    }

    public String getRetentionPolicyId() {
        return retentionPolicyId;
    }

    @DataBoundSetter
    public void setJsonParam(String jsonParam) {
        this.jsonParam = !jsonParam.isEmpty() ? jsonParam : null;
    }

    @DataBoundSetter
    public void setName(String name) {
        this.name = !name.isEmpty() ? name : null;
    }

    @DataBoundSetter
    public void setEnvironmentId(String environmentId) {
        this.environmentId = !environmentId.isEmpty() ? environmentId : null;
    }

    @DataBoundSetter
    public void setAutoSelectRepository(Boolean auto_select_repository) {
        this.autoSelectRepository = auto_select_repository;
    }

    @DataBoundSetter
    public void setTagList(List<Tags> tagList) {
        this.tagList = tagList;
    }

    @DataBoundSetter
    public void setWaitForPolling(Boolean waitForPolling) {
        this.waitForPolling = waitForPolling;
    }

    @DataBoundSetter
    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    @DataBoundSetter
    public void setSourceDataId(String sourceDataId) {
        this.sourceDataId = !sourceDataId.isEmpty() ? sourceDataId : null;
    }

    @DataBoundSetter
    public void setEnvironmentUserId(String environmentUserId) {
        this.environmentUserId = !environmentUserId.isEmpty() ? environmentUserId : null;
    }

    @DataBoundSetter
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = !repositoryId.isEmpty() ? repositoryId : null;
    }

    @DataBoundSetter
    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    @DataBoundSetter
    public void setTargetGroupId(String targetGroupId) {
        this.targetGroupId = targetGroupId;
    }

    @DataBoundSetter
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @DataBoundSetter
    public void setVdbRestart(boolean vdbRestart) {
        this.vdbRestart = vdbRestart;
    }

    @DataBoundSetter
    public void setSnapshotPolicyId(String snapshotPolicyId) {
        this.snapshotPolicyId = snapshotPolicyId;
    }

    @DataBoundSetter
    public void setRetentionPolicyId(String retentionPolicyId) {
        this.retentionPolicyId = retentionPolicyId;
    }

}
