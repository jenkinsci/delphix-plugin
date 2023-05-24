package io.jenkins.plugins.delphix;

import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
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
import io.jenkins.plugins.util.Constant;
import io.jenkins.plugins.util.DctSdkUtil;
import io.jenkins.plugins.util.DelphixProperties;
import io.jenkins.plugins.util.Helper;
import jenkins.tasks.SimpleBuildStep;

public class DeleteVDB extends Builder implements SimpleBuildStep {

    private String credentialId;
    private String vdbId;
    private Boolean skipPolling;
    private Boolean force;

    private Boolean loadFromProperties;

    private final DctSdkUtil dctSdkUtil = new DctSdkUtil();

    @DataBoundConstructor
    public DeleteVDB() {

    }


    @Symbol("deleteVDB")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public Boolean load;

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.Delete_DisplayName();
        }

        public ListBoxModel doFillCredentialIdItems(@AncestorInPath Item item,
                @QueryParameter String credentialId) {
            return getAllCredentialsListBoxModel(item, credentialId);
        }

        public FormValidation doCheckLoadFromProperties(@QueryParameter Boolean value,
                @AncestorInPath AbstractProject project) throws IOException, ServletException {
            load = value;
            return FormValidation.ok();
        }

        public FormValidation doCheckVdbId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0 && load == false)
                return FormValidation.error(Messages.VDBId_Empty());
            return FormValidation.ok();
        }

        public FormValidation doCheckCredentialId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0 && load == false)
                return FormValidation.error(Messages.Credential_Empty());
            return FormValidation.ok();
        }

    }


    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher,
            TaskListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        String buildId = run.getId();
        logger.println(Messages.Delete_Start(buildId));
        try {
            ApiClient defaultClient = dctSdkUtil.createApiClient(run, credentialId, logger);
            if (defaultClient != null) {
                if (loadFromProperties) {
                    logger.println(Messages.Delete_Message1());
                    Helper helper = new Helper(listener);
                    List<String> fileList =
                            helper.getFileList(Paths.get(workspace.toURI()), Constant.FILE_PATTERN);

                    for (String file : fileList) {
                        DelphixProperties x = new DelphixProperties(workspace, file, listener);
                        String vdbId = x.getVDB();
                        logger.println(Messages.Delete_Message2(vdbId, file));
                        deleteVDB(run, vdbId, defaultClient, listener);
                    }
                }
                else {
                    List<String> vdbList = Arrays.asList(vdbId.split(","));
                    for (String vdb : vdbList) {
                        logger.println(Messages.Delete_Message3(vdb));
                        deleteVDB(run, vdb, defaultClient, listener);
                    }
                }
            }
            else {
                logger.println(Messages.Apiclient_Fail());
                run.setResult(Result.FAILURE);
            }
        }
        catch (Exception e) {
            logger.println("Response : " + e.getMessage());
            run.setResult(Result.FAILURE);
        }
    }

    private void deleteVDB(Run<?, ?> run, String vdbId, ApiClient defaultClient,
            TaskListener listener) {
        try {
            Helper helper = new Helper(listener);
            DeleteVDBResponse rs = dctSdkUtil.deleteVdb(defaultClient, vdbId, force);
            listener.getLogger().println(Messages.Delete_Message4(rs.getJob().getId()));
            if (!skipPolling) {
                if (helper.waitForPolling(dctSdkUtil, defaultClient, listener, run,
                        rs.getJob().getId())) {
                    listener.getLogger().println(Messages.Delete_Fail());
                }
            }
            listener.getLogger().println(Messages.Delete_Complete());
        }
        catch (ApiException e) {
            listener.getLogger().println("Response : " + e.getResponseBody());
            run.setResult(Result.FAILURE);
        }
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

    public Boolean getSkipPolling() {
        return skipPolling;
    }

    public Boolean getLoadFromProperties() {
        return loadFromProperties;
    }

    @DataBoundSetter
    public void setLoadFromProperties(Boolean loadFromProperties) {
        this.loadFromProperties = loadFromProperties;
    }

    @DataBoundSetter
    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    @DataBoundSetter
    public void setVdbId(String vdbId) {
        this.vdbId = !vdbId.isEmpty() ? vdbId : null;

    }

    @DataBoundSetter
    public void setSkipPolling(Boolean waitForPolling) {
        this.skipPolling = waitForPolling;
    }

    @DataBoundSetter
    public void setForce(Boolean force) {
        this.force = force;
    }

}
