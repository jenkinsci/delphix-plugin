package io.jenkins.plugins.util;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import com.delphix.dct.Configuration;
import com.delphix.dct.api.JobsApi;
import com.delphix.dct.api.VdbsApi;
import com.delphix.dct.models.DeleteVDBParameters;
import com.delphix.dct.models.DeleteVDBResponse;
import com.delphix.dct.models.Job;
import com.delphix.dct.models.PermissionEnum;
import com.delphix.dct.models.ProvisionVDBBySnapshotParameters;
import com.delphix.dct.models.ProvisionVDBFromBookmarkParameters;
import com.delphix.dct.models.ProvisionVDBResponse;
import com.delphix.dct.models.SearchBody;
import com.delphix.dct.models.SearchVDBsResponse;
import com.delphix.dct.models.VDB;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.constant.Constant;
import io.jenkins.plugins.delphix.DelphixGlobalConfiguration;
import static io.jenkins.plugins.util.CredentialUtil.getApiKey;
import io.jenkins.plugins.delphix.Messages;

public class DctSdkUtil {

    private ApiClient defaultClient;

    public DctSdkUtil(Run<?, ?> run, TaskListener listener, String credId) {
        String url = DelphixGlobalConfiguration.get().getDctUrl();
        if (url == null) {
            listener.getLogger().println(Messages.DctSDkUtil_Error1());
            return;
        }
        String apiKey = getApiKey(credId, run);
        if (apiKey == null) {
            listener.getLogger().println(Messages.DctSDkUtil_Error2(credId));
            return;
        }
        this.defaultClient = Configuration.getDefaultApiClient();
        this.defaultClient.setVerifyingSsl(!DelphixGlobalConfiguration.get().getDisableSsl()); 
        this.defaultClient.setConnectTimeout(Constant.TIMEOUT);
        this.defaultClient.setReadTimeout(Constant.TIMEOUT);
        this.defaultClient.setWriteTimeout(Constant.TIMEOUT);
        this.defaultClient.setUserAgent(Constant.USER_AGENT);
        this.defaultClient.addDefaultHeader(Constant.CLIENT_NAME_HEADER, Constant.CLIENT_NAME);
        this.defaultClient.setApiKey(apiKey);
        this.defaultClient.setBasePath(url + Constant.API_VERSION);
    }

    public ApiClient getDefaultClient() {
        return defaultClient;
    }

    /**
     * 
     * @param provisionVDBFromBookmarkParameters
     * @return
     * @throws ApiException
     */
    public ProvisionVDBResponse provisionVdbFromBookmark(
            ProvisionVDBFromBookmarkParameters provisionVDBFromBookmarkParameters)
            throws ApiException {
        ProvisionVDBResponse result = null;
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        result = apiInstance.provisionVdbFromBookmark(provisionVDBFromBookmarkParameters);
        return result;
    }

    /**
     * 
     * @param provisionVDBBySnapshotParameters
     * @return
     * @throws ApiException
     */
    public ProvisionVDBResponse provisionVdbBySnapshot(
            ProvisionVDBBySnapshotParameters provisionVDBBySnapshotParameters) throws ApiException {
        ProvisionVDBResponse result = null;
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        result = apiInstance.provisionVdbBySnapshot(provisionVDBBySnapshotParameters);
        return result;
    }

    /**
     * 
     * @param vdbId
     * @param force
     * @return
     * @throws ApiException
     */
    public DeleteVDBResponse deleteVdb(String vdbId, Boolean force) throws ApiException {
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        DeleteVDBParameters deleteVDBParameters = new DeleteVDBParameters();
        deleteVDBParameters.setForce(force);
        DeleteVDBResponse result = apiInstance.deleteVdb(vdbId, deleteVDBParameters);
        return result;

    }

    public VDB getVDBDetails(String vdbId) throws ApiException {
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        VDB result = apiInstance.getVdbById(vdbId);
        return result;
    }

    public Job getJobStatus(String jobId) throws ApiException {
        JobsApi apiInstance = new JobsApi(defaultClient);
        Job result = apiInstance.getJobById(jobId);
        return result;
    }

    public SearchVDBsResponse searchVDB(String name) throws ApiException {
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        Integer limit = 100;
        String cursor = null;
        String sort = "id";
        PermissionEnum permission = PermissionEnum.fromValue("READ");
        SearchBody searchBody = new SearchBody();
        String filterExpr = "name EQ \'" + name + "\'";
        searchBody.setFilterExpression(filterExpr);
        SearchVDBsResponse result = apiInstance.searchVdbs(limit, cursor, sort, permission, searchBody);
        return result;
    }
}
