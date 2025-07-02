package io.jenkins.plugins.delphix;

import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.ServletException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import com.delphix.dct.ApiException;
import com.delphix.dct.models.DeleteVDBResponse;
import com.delphix.dct.models.Job;
import com.delphix.dct.models.SearchVDBsResponse;
import com.delphix.dct.models.VDB;
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
import io.jenkins.plugins.constant.Constant;
import io.jenkins.plugins.job.JobHelper;
import io.jenkins.plugins.properties.DelphixProperties;
import io.jenkins.plugins.util.DctSdkUtil;
import io.jenkins.plugins.util.Helper;
import jenkins.tasks.SimpleBuildStep;

public class DeleteVDB extends Builder implements SimpleBuildStep {

    private String credentialId;
    private String vdbId;
    private boolean skipPolling;
    private boolean force;
    private String name;
    private boolean loadFromProperties;

    @DataBoundConstructor
    public DeleteVDB() {

    }

    @Symbol("deleteVDB")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

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

        public FormValidation doCheckCredentialId(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.Credential_Empty());
            return FormValidation.ok();
        }

    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher,
            TaskListener listener) throws InterruptedException, IOException {
        Helper helper = new Helper(listener);
        listener.getLogger().println(Messages.Delete_Start(run.getId()));
        try {
            DctSdkUtil dctSdkUtil = new DctSdkUtil(run, listener, credentialId);
            if (dctSdkUtil.getDefaultClient() != null) {
                if (loadFromProperties) {
                    listener.getLogger().println(Messages.Delete_Message1());
                    List<String> fileList =
                            helper.getFileList(Paths.get(workspace.toURI()), Constant.FILE_PATTERN);

                    for (String file : fileList) {
                        DelphixProperties delphixProperties = new DelphixProperties(workspace, file, listener);
                        String vdbIdFromFile = delphixProperties.getVDB();
                        listener.getLogger().println(Messages.Delete_Message2(vdbIdFromFile, file));
                        deleteVDB(run, vdbIdFromFile, listener, dctSdkUtil);
                    }
                }
                else if (vdbId != null) {
                    List<String> vdbList = Arrays.asList(vdbId.split(","));
                    for (String vdb : vdbList) {
                        listener.getLogger().println(Messages.Delete_Message3(vdb));
                        deleteVDB(run, vdb, listener, dctSdkUtil);
                    }
                }
                else if (name != null) {
                    List<String> nameList = Arrays.asList(name.split(","));
                    for (String vdbname : nameList) {
                        listener.getLogger().println(Messages.Delete_Message5(vdbname));
                        SearchVDBsResponse result = dctSdkUtil.searchVDB(vdbname);
                        if (result.getItems().size() == 0) {
                            listener.getLogger().println(Messages.Delete_Error3(vdbname));
                            run.setResult(Result.FAILURE);
                        }
                        else if (result.getItems().size() > 1) {
                            listener.getLogger().println(Messages.Delete_Error2(vdbname));
                            run.setResult(Result.FAILURE);
                        }
                        else {
                            VDB vdb = result.getItems().get(0);
                            deleteVDB(run, vdb.getId(), listener, dctSdkUtil);
                        }
                    }
                }
                else {
                    listener.getLogger().println(Messages.Delete_Error1());
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

    private void deleteVDB(Run<?, ?> run, String vdbId, TaskListener listener,
            DctSdkUtil dctSdkUtil) throws ApiException, Exception {
        DeleteVDBResponse rs = dctSdkUtil.deleteVdb(vdbId, force);
        Job job = rs.getJob();
        if (job != null) {
            listener.getLogger().println(Messages.Delete_Message4(job.getId()));
            if (!skipPolling) {
                JobHelper jh = new JobHelper(dctSdkUtil, listener, job.getId());
                boolean jobStatus = jh.waitForPolling( run);
                if (jobStatus) {
                    listener.getLogger().println(Messages.Delete_Fail());
                }
                else {
                    listener.getLogger().println(Messages.Delete_Complete());
                }
            }
        }
        else {
            listener.getLogger().println("Job Creation Failed");
            run.setResult(Result.FAILURE);
        }
    }

    public String getCredentialId() {
        return credentialId;
    }

    public boolean getForce() {
        return force;
    }

    public String getVdbId() {
        return vdbId;
    }

    public boolean getSkipPolling() {
        return skipPolling;
    }

    public boolean getLoadFromProperties() {
        return loadFromProperties;
    }

    public String getName() {
        return name;
    }

    @DataBoundSetter
    public void setName(String name) {
        this.name = !name.isEmpty() ? name : null;
    }

    @DataBoundSetter
    public void setLoadFromProperties(boolean loadFromProperties) {
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
    public void setSkipPolling(boolean waitForPolling) {
        this.skipPolling = waitForPolling;
    }

    @DataBoundSetter
    public void setForce(boolean force) {
        this.force = force;
    }

}
