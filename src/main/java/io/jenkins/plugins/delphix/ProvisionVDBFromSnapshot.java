package io.jenkins.plugins.delphix;

import edu.umd.cs.findbugs.annotations.NonNull;
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
import io.jenkins.plugins.job.JobHelper;
import io.jenkins.plugins.util.DctSdkUtil;
import io.jenkins.plugins.util.Helper;
import io.jenkins.plugins.util.ValidationUtil;
import io.jenkins.plugins.vdb.VDBRequestBuilder;
import java.io.IOException;
import jakarta.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import com.delphix.dct.ApiException;
import com.delphix.dct.models.Job;
import com.delphix.dct.models.ProvisionVDBBySnapshotParameters;
import com.delphix.dct.models.ProvisionVDBResponse;
import com.google.gson.JsonSyntaxException;
import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;

public class ProvisionVDBFromSnapshot extends ProvisonVDB implements SimpleBuildStep {

    private String snapshotId;

    @DataBoundConstructor
    public ProvisionVDBFromSnapshot() {

    }

    public String getSnapshotId() {
        return snapshotId;
    }

    @DataBoundSetter
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = !snapshotId.isEmpty() ? snapshotId : null;
    }

    @Symbol("provisionVDBFromSnapshot")
    @Extension
    public static final class ProvisionDescriptor extends BuildStepDescriptor<Builder> {

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.ProvisionVDBSnapshot_DisplayName();
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
            if (value.isEmpty())
                return FormValidation.error(Messages.Credential_Empty());
            return FormValidation.ok();
        }

        public FormValidation doCheckJsonParam(@QueryParameter String value)
                throws IOException, ServletException {
            if (!value.isEmpty()) {
                ValidationUtil validationUtil = new ValidationUtil();
                try {
                    validationUtil.validateJsonFormat(value);
                }
                catch (JsonSyntaxException e) {
                    return FormValidation.error(Messages.Json_Invalid());
                }
                catch (Exception e) {
                    return FormValidation.error(e.getMessage());
                }

                try {
                    String invalidKey =
                            validationUtil.validateJsonWithSnapshotProvisionParameters();
                    if (invalidKey != null) {
                        return FormValidation.error(Messages.Json_IncorrectKey(invalidKey));
                    }
                }
                catch (Exception e) {
                    return FormValidation.error(e.getMessage());
                }
            }
            return FormValidation.ok();
        }
    }

    @Override
    public void perform(Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher,
                        @NonNull TaskListener listener) throws InterruptedException, IOException {
        VDBRequestBuilder vdbRequestBuilder = new VDBRequestBuilder();
        Helper helper = new Helper(listener);
        listener.getLogger().println(Messages._ProvisionVDBSnapshot_Info(run.getId()));
        try {
            DctSdkUtil dctSdkUtil = new DctSdkUtil(run, listener, credentialId);
            if (dctSdkUtil.getDefaultClient() != null) {
                ProvisionVDBBySnapshotParameters provisionFromSnapshotParameter = vdbRequestBuilder
                        .provisionFromSnapshotParameter(snapshotId, autoSelectRepository, tagList,
                                name, environmentId, jsonParam, sourceDataId, environmentUserId,
                                repositoryId, engineId, targetGroupId, databaseName, vdbRestart,
                                snapshotPolicyId, retentionPolicyId);

                ProvisionVDBResponse provisionResponse =
                        dctSdkUtil.provisionVdbBySnapshot(provisionFromSnapshotParameter);

                Job job = provisionResponse.getJob();
                if (job != null) {
                    listener.getLogger().println(
                            Messages.ProvisionVDB_Start(provisionResponse.getVdbId(), job.getId()));
                    JobHelper jobHelper = new JobHelper(dctSdkUtil, listener, job.getId());
                    boolean status;
                    if (skipPolling) {
                        status = jobHelper.waitForGetVDB( run,
                                provisionResponse.getVdbId());
                    }
                    else {
                        status = jobHelper.waitForPolling( run);
                    }
                    if (status) {
                        listener.getLogger().println(Messages.ProvisionVDB_Fail());
                    }
                    else {
                        helper.displayAndSave(dctSdkUtil, provisionResponse.getVdbId(), workspace,
                                listener, fileNameSuffix);
                    }
                }
                else {
                    listener.getLogger().println("Job Creation Failed");
                    run.setResult(Result.FAILURE);
                }
            }
            else {
                listener.getLogger().println(Messages.Apiclient_Fail());
                run.setResult(Result.FAILURE);
            }
        }
        catch (ApiException e) {
            listener.getLogger().println("ApiException : " + e.getResponseBody());
            listener.getLogger().println("ApiException : " + e.getMessage());
            run.setResult(Result.FAILURE);
        }
        catch (Exception e) {
            listener.getLogger().println("Exception : " + e.getMessage());
            run.setResult(Result.FAILURE);
        }
    }
}
