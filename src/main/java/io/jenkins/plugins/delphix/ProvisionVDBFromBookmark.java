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
import io.jenkins.plugins.util.Helper;
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
import com.delphix.dct.ApiException;
import com.delphix.dct.models.Job;
import com.delphix.dct.models.ProvisionVDBFromBookmarkParameters;
import com.delphix.dct.models.ProvisionVDBResponse;
import com.delphix.dct.models.VDB;
import com.google.gson.JsonSyntaxException;

import static io.jenkins.plugins.util.CredentialUtil.getAllCredentialsListBoxModel;

public class ProvisionVDBFromBookmark extends ProvisonVDB implements SimpleBuildStep {

  private final String bookmarkId;

  @DataBoundConstructor
  public ProvisionVDBFromBookmark(String bookmarkId) {
    this.bookmarkId = bookmarkId;
  }

  public String getBookmarkId() {
    return bookmarkId;
  }

  @Symbol("provisionVDBFromBookmark")
  @Extension
  public static final class ProvisionDescriptor extends BuildStepDescriptor<Builder> {

    @Override
    public String getDisplayName() {
      return Messages.ProvisionVDBBookmark_DisplayName();
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
          String invalidKey = validationUtil.validateJsonWithBookmarkProvisionParameters();
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

    public FormValidation doCheckBookmarkId(@QueryParameter String value)
        throws IOException, ServletException {
      if (value.length() == 0)
        return FormValidation.error(Messages.BookmarkId_Empty());
      return FormValidation.ok();
    }

  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher,
      TaskListener listener) throws InterruptedException, IOException {
    PrintStream logger = listener.getLogger();
    ProvisionParameterUtil provisionParameterUtil = new ProvisionParameterUtil();
    Helper helper = new Helper(logger);
    logger.println(Messages.ProvisionVDBBookmark_Info(run.getId()));
    try {
      DctSdkUtil dctSdkUtil = new DctSdkUtil(run, credentialId, logger);
      if (dctSdkUtil.getDefaultClient() != null) {

        ProvisionVDBFromBookmarkParameters provisionFromBookmarkParameter =
            provisionParameterUtil.provisionFromBookmarkParameter(bookmarkId, autoSelectRepository,
                tagList, name, environmentId, jsonParam, environmentUserId, repositoryId,
                targetGroupId, databaseName, vdbRestart, snapshotPolicyId, retentionPolicyId);

        ProvisionVDBResponse provisionResponse =
            dctSdkUtil.provisionVdbFromBookmark(provisionFromBookmarkParameter);
        Job job = provisionResponse.getJob();
        if (job != null) {
          logger.println(Messages.ProvisionVDB_Start(provisionResponse.getVdbId(), job.getId()));

          if (!skipPolling) {
            if (helper.waitForPolling(dctSdkUtil, run, job.getId())) {
              listener.getLogger().println(Messages.ProvisionVDB_Fail());
            }
            else {
              helper.displayAndSave(dctSdkUtil, provisionResponse.getVdbId(), workspace, listener,
                  fileNameSuffix);
              listener.getLogger().println(Messages.ProvisionVDB_Complete());
            }
          }
          else {
            helper.displayAndSave(dctSdkUtil, provisionResponse.getVdbId(), workspace, listener,
                fileNameSuffix);
          }
          // VDB vdbDetails =
          // helper.displayVDBDetails(dctSdkUtil, provisionResponse.getVdbId());
          // helper.saveToProperties(vdbDetails, workspace, listener, fileNameSuffix);
        }
        else {
          logger.println("Job Creation Failed");
        }
      }
      else {
        logger.println(Messages.Apiclient_Fail());
        run.setResult(Result.FAILURE);
      }
    }
    catch (ApiException e) {
      logger.println("ApiException : " + e.getResponseBody());
      run.setResult(Result.FAILURE);
    }
    catch (Exception e) {
      logger.println("Exception : " + e.getMessage());
      run.setResult(Result.FAILURE);
    }

  }

}
