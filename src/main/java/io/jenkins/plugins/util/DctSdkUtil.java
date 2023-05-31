package io.jenkins.plugins.util;

import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
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
import io.jenkins.plugins.delphix.DelphixGlobalConfiguration;

import jenkins.model.Jenkins;
import static io.jenkins.plugins.util.CredentialUtil.getApiKey;

public class DctSdkUtil {

    private ApiClient defaultClient;
    private PrintStream logger;

    /**
     * 
     * @param run
     * @param credId
     * @param logger
     */
    public DctSdkUtil(Run<?, ?> run, String credId, PrintStream logger) {
        this.logger = logger;
        String url = getGlobalConfig().getDctUrl();
        if (getGlobalConfig().getDctUrl() == null) {
            this.logger.println("Delphix Global Configuration Missing");
            return;
        }
        String apiKey = getApiKey(credId, run);
        if (apiKey == null) {
            this.logger.println("Cannot find any credentials for " + credId);
            return;
        }
        try {
            this.defaultClient = ApiClientInit.init();
            this.defaultClient.setApiKey(apiKey);
            this.defaultClient.setBasePath(url);
        }
        catch (KeyManagementException | NoSuchAlgorithmException | ApiException e) {
            this.defaultClient = null;
            this.logger.println("ApiClient Creation Exception: " + e.getMessage());
        }
    }

    public ApiClient getDefaultClient() {
        return defaultClient;
    }

    // /**
    // *
    // * @param run
    // * @param credId
    // * @param logger
    // * @return
    // */
    // public ApiClient createApiClient(Run<?, ?> run, String credId, PrintStream logger) {
    // String url = getGlobalConfig().getDctUrl();
    // if (url == null) {
    // logger.println("Delphix Global Configuration Missing");
    // return null;
    // }
    // String apiKey = getApiKey(credId, run);
    // if (apiKey == null) {
    // logger.println("Cannot find any credentials for " + credId);
    // return null;
    // }
    // ApiClient defaultClient = null;
    // try {
    // defaultClient = ApiClientInit.init();
    // defaultClient.setApiKey(apiKey);
    // defaultClient.setBasePath(url);
    // }
    // catch (KeyManagementException | NoSuchAlgorithmException | ApiException e) {
    // logger.println("ApiClient Creation Exception: " + e.getMessage());
    // e.printStackTrace();
    // }
    // return defaultClient;
    // }

    /**
     * 
     * @param defaultClient
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
     * @param defaultClient
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
     * @param defaultClient
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
        System.out.println(result);
        return result;

    }

    /**
     * 
     * @param defaultClient
     * @param vdbId
     * @return
     * @throws ApiException
     */
    public VDB getVDBDetails(String vdbId) throws ApiException {
        VdbsApi apiInstance = new VdbsApi(this.defaultClient);
        VDB result = apiInstance.getVdbById(vdbId);
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
        SearchVDBsResponse result =
                apiInstance.searchVdbs(limit, cursor, sort, permission, searchBody);
        return result;
    }

    /**
     * 
     * @param defaultClient
     * @param jobId
     * @param logger
     * @return
     * @throws ApiException
     */
    public boolean waitForPolling(String jobId) throws ApiException {
        final long WAIT_TIME = 20000;
        boolean completed = false;
        boolean fail = false;
        // String status = null;
        JobsApi apiInstance = new JobsApi(this.defaultClient);
        while (!completed) {
            Job result = apiInstance.getJobById(jobId);

            this.logger.println("Current Job Status: " + result.getStatus());
            if (!result.getStatus().toString().equals("STARTED")) {
                completed = true;
                if (!result.getStatus().toString().equals("COMPLETED")) {
                    fail = true;
                    this.logger.println("Error Details: " + result.getErrorDetails());
                }
            }

            if (completed) {
                break;
            }
            try {
                Thread.sleep(WAIT_TIME);
            }
            catch (InterruptedException ex) {
                this.logger.println("Wait interrupted!");
                this.logger.println(ex.getMessage());
                completed = true; // bail out of wait loop
            }
        }
        return fail;
    }

    private DelphixGlobalConfiguration.DescriptorImpl getGlobalConfig() {
        DelphixGlobalConfiguration.DescriptorImpl delphixGlobalConfig =
                Jenkins.get().getDescriptorByType(DelphixGlobalConfiguration.DescriptorImpl.class);
        return delphixGlobalConfig;
    }
}
