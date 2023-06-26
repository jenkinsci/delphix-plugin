package io.jenkins.plugins.job;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import com.delphix.dct.api.JobsApi;
import com.delphix.dct.models.Job;
import com.delphix.dct.models.VDB;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.constant.Constant;
import io.jenkins.plugins.delphix.Messages;
import io.jenkins.plugins.util.DctSdkUtil;

public class JobHelper {

    private String jobId;
    private DctSdkUtil dctSdkUtil;
    private TaskListener listener;

    public JobHelper(DctSdkUtil dctSdkUtil, TaskListener listener, String jobId) {
        this.jobId = jobId;
        this.listener = listener;
        this.dctSdkUtil = dctSdkUtil;
    }

    public boolean waitForPolling(ApiClient defaultClient, Run<?, ?> run)
            throws ApiException, Exception {
        this.listener.getLogger().println(Messages.Poll_Wait());
        boolean completed = false;
        boolean fail = false;
        while (!completed) {
            try {
                Job result = dctSdkUtil.getJobStatus(jobId);
                if (result != null && result.getStatus() != null) {
                    this.listener.getLogger().println("Current Job Status: " + result.getStatus());
                    if (!result.getStatus().toString().equals("STARTED")) {
                        completed = true;
                        if (!result.getStatus().toString().equals("COMPLETED")) {
                            fail = true;
                            this.listener.getLogger()
                                    .println("Error Details: " + result.getErrorDetails());
                        }
                    }
                    if (completed) {
                        break;
                    }
                    Thread.sleep(Constant.WAIT_TIME);
                }
            }
            catch (InterruptedException ex) {
                this.listener.getLogger().println("Wait interrupted!");
                this.listener.getLogger().println(ex.getMessage());
                completed = true;
                fail = true;
            }
            catch (Exception e) {
                this.listener.getLogger().println(e.getMessage());
                completed = true;
                fail = true;
            }
        }
        if (fail) {
            run.setResult(Result.FAILURE);
        }
        return fail;
    }

    public boolean waitForGetVDB(ApiClient defaultClient, Run<?, ?> run, String vdbId)
            throws InterruptedException {
        this.listener.getLogger().println(Messages.Vdb_Get());
        boolean completed = false;
        boolean fail = false;
        while (!completed) {
            try {
                Thread.sleep(Constant.WAIT_TIME);
                VDB vdbDetails = dctSdkUtil.getVDBDetails(vdbId);
                if (vdbDetails != null)
                    completed = true;

                if (completed) {
                    break;
                }
            }
            catch (InterruptedException ex) {
                this.listener.getLogger().println("Wait interrupted!");
                this.listener.getLogger().println(ex.getMessage());
                completed = true;
                fail = true;
            }
            catch (ApiException e) {
                if (!e.getMessage().isEmpty()) {
                    this.listener.getLogger().println(e.getMessage());
                    completed = true;
                    fail = true;
                }
            }
        }
        if (fail) {
            run.setResult(Result.FAILURE);
        }
        return fail;
    }
}
