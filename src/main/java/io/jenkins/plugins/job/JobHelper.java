package io.jenkins.plugins.job;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import com.delphix.dct.api.JobsApi;
import com.delphix.dct.models.Job;
import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.delphix.Messages;
import io.jenkins.plugins.logger.Logger;

public class JobHelper {

    private String jobId;

    public JobHelper(String jobId) {
        this.jobId = jobId;
    }

    public boolean processJob(boolean skipPolling, ApiClient defaultClient, Run<?, ?> run)
            throws ApiException {
        if (!skipPolling) {
            return waitForPolling(defaultClient, run);
        }
        else {
            return false;
        }
    }

    public boolean waitForPolling(ApiClient defaultClient, Run<?, ?> run) throws ApiException {
        Logger.println(Messages.Poll_Wait());
        boolean fail = waitForPolling(defaultClient);
        if (fail) {
            run.setResult(Result.FAILURE);
        }
        return fail;
    }

    public boolean waitForPolling(ApiClient defaultClient) throws ApiException {
        final long WAIT_TIME = 20000;
        boolean completed = false;
        boolean fail = false;
        JobsApi apiInstance = new JobsApi(defaultClient);
        while (!completed) {
            Job result = apiInstance.getJobById(jobId);

            Logger.println("Current Job Status: " + result.getStatus());
            if (!result.getStatus().toString().equals("STARTED")) {
                completed = true;
                if (!result.getStatus().toString().equals("COMPLETED")) {
                    fail = true;
                    Logger.println("Error Details: " + result.getErrorDetails());
                }
            }

            if (completed) {
                break;
            }
            try {
                Thread.sleep(WAIT_TIME);
            }
            catch (InterruptedException ex) {
                Logger.println("Wait interrupted!");
                Logger.println(ex.getMessage());
                completed = true; // bail out of wait loop
            }
        }
        return fail;
    }
}
