package io.jenkins.plugins.delphix;

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
import io.jenkins.plugins.util.ProvisionParameterUtil;
import io.jenkins.plugins.util.ValidationUtil;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import com.delphix.dct.models.ProvisionVDBBySnapshotParameters;
import com.delphix.dct.models.ProvisionVDBResponse;
import com.google.gson.JsonSyntaxException;
import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;

public class ProvisionVDBFromSnapshot extends ProvisonVDB implements SimpleBuildStep {

    private final String snapshotId;

    @DataBoundConstructor
    public ProvisionVDBFromSnapshot(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    @Symbol("delphix-provisionVdbFromSnapshot")
    @Extension
    public static final class ProvisionDescriptor extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Delphix: Provison VDB From Snapshot";
        }

        public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item item,
                @QueryParameter String credentialId) {
            return getAllCredentialsListBoxModel(item, credentialId);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public FormValidation doCheckCredentialId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please Select a Credential ID");
            return FormValidation.ok();
        }

        public FormValidation doCheckSnapshotId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please Set a Snapshot ID");
            return FormValidation.ok();
        }

        public FormValidation doCheckJsonParam(@QueryParameter String value)
                throws IOException, ServletException {
            if (!value.isEmpty()) {
                ValidationUtil validationUtil = new ValidationUtil();
                try {
                    validationUtil.validateJsonFormat(value);
                } catch (JsonSyntaxException e) {
                    return FormValidation.error("Invalid Json Format");
                } catch (Exception e) {
                    return FormValidation.error(e.getMessage());
                }

                try {
                    String invalidKey = validationUtil.validateJsonWithBookmarkProvisionParameters();
                    if (invalidKey != null) {
                        return FormValidation.error("Invalid Provision Parameter " + invalidKey);
                    }
                } catch (Exception e) {
                    return FormValidation.error(e.getMessage());
                }
            }
            return FormValidation.ok();
        }
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher,
            TaskListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        String buildId = run.getId();
        ProvisionParameterUtil provisionParameterUtil = new ProvisionParameterUtil();
        DctSdkUtil dctSdkUtil = new DctSdkUtil();
        logger.println("Provison VDB From Snapshot with BuildID: " + buildId + " ,SnapshotID: "
                + snapshotId);
        try {
            ApiClient defaultClient = dctSdkUtil.createApiClient(run, credentialId, logger);
            if (defaultClient != null) {

                ProvisionVDBBySnapshotParameters provisionFromSnapshotParameter = provisionParameterUtil
                        .provisionFromSnapshotParameter(snapshotId,
                                autoSelectRepository, tagList, name, environmentId, jsonParam,
                                sourceDataId, environmentUserId, repositoryId, engineId,
                                targetGroupId, databaseName, vdbRestart, snapshotPolicyId,
                                retentionPolicyId);

                ProvisionVDBResponse rs = dctSdkUtil.provisionVdbBySnapshot(defaultClient,
                        provisionFromSnapshotParameter);

                logger.println("Provison VDB From Snapshot Job Started for Target VDBID: "
                        + rs.getVdbId() + " with JobID: " + rs.getJob().getId());

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

}
