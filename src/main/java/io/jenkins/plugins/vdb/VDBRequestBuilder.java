package io.jenkins.plugins.vdb;

import java.util.List;
import com.delphix.dct.models.ProvisionVDBBySnapshotParameters;
import com.delphix.dct.models.ProvisionVDBFromBookmarkParameters;
import com.delphix.dct.models.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import io.jenkins.plugins.delphix.Tags;

public class VDBRequestBuilder {

    Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    public ProvisionVDBBySnapshotParameters provisionFromSnapshotParameter(String snapshot_id,
            boolean auto_select_repository, List<Tags> tagList, String name, String environmentId,
            String jsonParameters, String sourceDataId, String environmentUserId,
            String repositoryId, String engineId, String targetGroupId, String databaseName,
            boolean vdbRestart, String snapshotPolicyId, String retentionPolicyId) {
        ProvisionVDBBySnapshotParameters provisionVDBBySnapshotParameters;
        if (jsonParameters != null) {
            provisionVDBBySnapshotParameters =
                    gson.fromJson(jsonParameters, ProvisionVDBBySnapshotParameters.class);
        }
        else {
            provisionVDBBySnapshotParameters = new ProvisionVDBBySnapshotParameters();
        }
        provisionVDBBySnapshotParameters.setAutoSelectRepository(auto_select_repository);
        provisionVDBBySnapshotParameters.setSnapshotId(snapshot_id);
        provisionVDBBySnapshotParameters.setName(name);
        provisionVDBBySnapshotParameters.setEnvironmentId(environmentId);
        provisionVDBBySnapshotParameters.setSourceDataId(sourceDataId);
        provisionVDBBySnapshotParameters.setEnvironmentUserId(environmentUserId);
        provisionVDBBySnapshotParameters.setRepositoryId(repositoryId);
        provisionVDBBySnapshotParameters.setEngineId(engineId);
        provisionVDBBySnapshotParameters.setTargetGroupId(targetGroupId);
        provisionVDBBySnapshotParameters.setDatabaseName(databaseName);
        provisionVDBBySnapshotParameters.setVdbRestart(vdbRestart);
        provisionVDBBySnapshotParameters.setSnapshotPolicyId(snapshotPolicyId);
        provisionVDBBySnapshotParameters.setRetentionPolicyId(retentionPolicyId);
        if (tagList != null) {
            for (Tags t : tagList) {
                Tag tg = new Tag();
                tg.setKey(t.getKey());
                tg.value(t.getValue());
                provisionVDBBySnapshotParameters.addTagsItem(tg);
            }
        }
        return provisionVDBBySnapshotParameters;
    }

    public ProvisionVDBFromBookmarkParameters provisionFromBookmarkParameter(String bookmark_id,
            boolean auto_select_repository, List<Tags> tagList, String name, String environmentId,
            String jsonParameters, String environmentUserId, String repositoryId,
            String targetGroupId, String databaseName, boolean vdbRestart, String snapshotPolicyId,
            String retentionPolicyId) {
        ProvisionVDBFromBookmarkParameters provisionVDBFromBookmarkParameters;
        if (jsonParameters != null) {
            provisionVDBFromBookmarkParameters =
                    gson.fromJson(jsonParameters, ProvisionVDBFromBookmarkParameters.class);
        }
        else {
            provisionVDBFromBookmarkParameters = new ProvisionVDBFromBookmarkParameters();
        }
        provisionVDBFromBookmarkParameters.setAutoSelectRepository(auto_select_repository);
        provisionVDBFromBookmarkParameters.setBookmarkId(bookmark_id);
        provisionVDBFromBookmarkParameters.setName(name);
        provisionVDBFromBookmarkParameters.setEnvironmentId(environmentId);
        provisionVDBFromBookmarkParameters.setEnvironmentUserId(environmentUserId);
        provisionVDBFromBookmarkParameters.setRepositoryId(repositoryId);
        provisionVDBFromBookmarkParameters.setTargetGroupId(targetGroupId);
        provisionVDBFromBookmarkParameters.setDatabaseName(databaseName);
        provisionVDBFromBookmarkParameters.setVdbRestart(vdbRestart);
        provisionVDBFromBookmarkParameters.setSnapshotPolicyId(snapshotPolicyId);
        provisionVDBFromBookmarkParameters.setRetentionPolicyId(retentionPolicyId);
        if (tagList != null) {
            for (Tags t : tagList) {
                Tag tg = new Tag();
                tg.setKey(t.getKey());
                tg.value(t.getValue());
                provisionVDBFromBookmarkParameters.addTagsItem(tg);
            }
        }
        return provisionVDBFromBookmarkParameters;
    }

}
