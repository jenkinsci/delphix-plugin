package io.jenkins.plugins.delphix;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import com.delphix.dct.models.DeleteVDBResponse;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.util.DctSdkUtil;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;

public class DeleteVDB extends Builder implements SimpleBuildStep {

    public String credentialId;
    public String vdbId;
    public Boolean waitForPolling;
    public Boolean force;

    @DataBoundConstructor
    public DeleteVDB(String credentialId, String vdbId, Boolean waitForPolling, Boolean force) {
        this.credentialId = credentialId;
        this.vdbId = vdbId;
        this.waitForPolling = waitForPolling;
        this.force = force;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public Boolean getForce() {
        return force;
    }

    public String getVdbId() {
        return vdbId;
    }

    public Boolean getWaitForPolling() {
        return waitForPolling;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher,
            TaskListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        String buildId = run.getId();
        DctSdkUtil dctSdkUtil = new DctSdkUtil();
        logger.println("Delete VDB with BuildID: " + buildId);
        try {
            ApiClient defaultClient = dctSdkUtil.createApiClient(run, credentialId, logger);
            if (defaultClient != null) {
                DeleteVDBResponse rs = dctSdkUtil.deleteVdb(defaultClient, vdbId, force);
                logger.println("Delete VDB Job Started with JobID: " + rs.getJob().getId());
                if (waitForPolling) {
                    logger.println("Waiting For Job to complete...");
                    String status = dctSdkUtil.waitForPolling(defaultClient, rs.getJob().getId(), logger);
                    if (status != null) {
                        logger.println("Provision Job completed.");
                    }
                }
            } else {
                run.setResult(Result.FAILURE);
            }
        } catch (ApiException e) {
            logger.println("Response : " + e.getResponseBody());
            run.setResult(Result.FAILURE);
        }

    }

    @Symbol("delphix-deleteVdb")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Delphix: Delete VDB";
        }

        public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item item,
                @QueryParameter String credentialId) {
            return getAllCredentialsListBoxModel(item, credentialId);
        }

        public FormValidation doCheckVdbId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please Set a Vdb ID");
            return FormValidation.ok();
        }

        public FormValidation doCheckCredentialId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please Select a Credential ID");
            return FormValidation.ok();
        }

    }

}
