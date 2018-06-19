/**
 * Copyright (c) 2018 by Delphix. All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import io.jenkins.plugins.delphix.objects.User;
import io.jenkins.plugins.delphix.repos.SelfServiceRepository;
import io.jenkins.plugins.delphix.repos.UserRepository;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;

/*
 * Describes a build step for managing a Delphix Self Service Container These build steps can be
 * added in the job configuration page in Jenkins.
 */
public class SelfServiceContainerBuilder extends DelphixBuilder implements SimpleBuildStep {

  private final String delphixEngine;
  private final String delphixEnvironment;
  private final String delphixOperation;
  private final String delphixBookmark;

  private boolean saveToProps;
  private boolean loadFromProps;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * SelfServiceBuilder description.
   *
   * @param delphixEngine String
   * @param delphixEnvironment String
   * @param delphixOperation String
   * @param delphixBookmark String
   */
  @DataBoundConstructor
  public SelfServiceContainerBuilder(
      String delphixEngine,
      String delphixEnvironment,
      String delphixOperation,
      String delphixBookmark) {
    this.delphixEngine = delphixEngine;
    this.delphixEnvironment = delphixEnvironment;
    this.delphixOperation = delphixOperation;
    this.delphixBookmark = delphixBookmark;
  }

  public String getDelphixEngine() {
    return this.delphixEngine;
  }

  public String getDelphixEnvironment() {
    return this.delphixEnvironment;
  }

  public String getDelphixOperation() {
    return this.delphixOperation;
  }

  public String getDelphixBookmark() {
    return this.delphixBookmark;
  }

  public boolean getSaveToProps() {
    return this.saveToProps;
  }

  public boolean getLoadFromProps() {
    return this.loadFromProps;
  }

  @DataBoundSetter
  public void setSaveToProps(boolean saveToProps) {
    this.saveToProps = saveToProps;
  }

  @DataBoundSetter
  public void setLoadFromProps(boolean loadFromProps) {
    this.loadFromProps = loadFromProps;
  }

  @Extension
  public static final class RefreshDescriptor extends SelfServiceDescriptor {

    /* Add containers to drop down for Refresh action */
    public ListBoxModel doFillDelphixEngineItems() {
      return super.doFillDelphixEngineItems();
    }

    /**
     * Add containers to drop down for Refresh action.
     *
     * @param delphixEngine String
     * @return ListBoxModel
     */
    public ListBoxModel doFillDelphixEnvironmentItems(@QueryParameter String delphixEngine) {
      return super.doFillDelphixSelfServiceItems(delphixEngine);
    }

    public ListBoxModel doFillDelphixBookmarkItems(@QueryParameter String delphixEngine) {
      return super.doFillDelphixBookmarkItems(delphixEngine);
    }

    /**
     * Create dropdown of Container Operations.
     *
     * @return ListBoxModel
     */
    public ListBoxModel doFillDelphixOperationItems() {
      ListBoxModel operations = new ListBoxModel();
      operations.add("Refresh", "Refresh");
      operations.add("Restore", "Restore");
      operations.add("Enable", "Enable");
      operations.add("Disable", "Disable");
      operations.add("Recover", "Recover");
      operations.add("Reset", "Reset");
      operations.add("Undo", "Undo");
      operations.add("Lock", "Lock");
      operations.add("Unlock", "Unlock");
      return operations;
    }

    /* Name to display for build step */
    @Override
    public String getDisplayName() {
      return Messages.getMessage(Messages.SELFSERVICE_OPERATION);
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    // Check if the input engine is not valid
    if (delphixEnvironment.equals("NULL")) {
      listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
    }

    String engine = delphixEngine;
    String selfServiceContainer = delphixEnvironment;
    String operationType = delphixOperation;
    String bookmark = delphixBookmark;

    // Overwrite values from Delphix Properties
    DelphixProperties delphixProps = new DelphixProperties(workspace, listener);
    if (this.loadFromProps) {
      try {
        // engine = delphixProps.getEngine();
        // selfServiceContainer = delphixProps.getContainerReference();
        // operationType = delphixProps.getContainerOperation();
        bookmark = delphixProps.getBookmarkReference();
      } catch (Throwable t) {
        listener.getLogger().println(t.getMessage());
      }
    }
    if (this.saveToProps) {
      // delphixProps.setEngine(engine);
      delphixProps.setContainerReference(selfServiceContainer);
      // delphixProps.setContainerOperation(operationType);
    }

    if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
      listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
    }

    DelphixEngine loadedEngine = GlobalConfiguration.getPluginClassDescriptor().getEngine(engine);
    SelfServiceRepository delphixEngine = new SelfServiceRepository(loadedEngine);

    // Run main operation as defined by build settings
    JsonNode action = MAPPER.createObjectNode();
    try {
      delphixEngine.login();
      switch (operationType) {
        case "Refresh":
          action = delphixEngine.refresh(selfServiceContainer);
          break;
        case "Reset":
          action = delphixEngine.reset(selfServiceContainer);
          break;
        case "Restore":
          action = delphixEngine.restore(selfServiceContainer, bookmark);
          break;
        case "Enable":
          action = delphixEngine.enable(selfServiceContainer);
          break;
        case "Disable":
          action = delphixEngine.disable(selfServiceContainer);
          break;
        case "Recover":
          action = delphixEngine.recover(selfServiceContainer);
          break;
        case "Undo":
          SelfServiceContainer container = delphixEngine.get(selfServiceContainer);
          action = delphixEngine.undo(selfServiceContainer, container.getLastOperation());
          break;
        case "Lock":
          delphixEngine.login();
          UserRepository userRepo = new UserRepository(delphixEngine);
          User user = userRepo.getCurrent();
          action = delphixEngine.lock(selfServiceContainer, user.getReference());
          break;
        case "Unlock":
          action = delphixEngine.unlock(selfServiceContainer);
          break;
        default:
          throw new DelphixEngineException("Undefined Self Service Operation");
      }
    } catch (DelphixEngineException e) {
      // Print error from engine if job fails and abort Jenkins job
      listener.getLogger().println(e.getMessage());
    } catch (IOException e) {
      // Print error if unable to connect to engine and abort Jenkins job
      listener
          .getLogger()
          .println(
              Messages.getMessage(
                  Messages.UNABLE_TO_CONNECT, new String[] {delphixEngine.getEngineAddress()}));
    }

    // Check for Action with a Completed State
    if (this.checkActionIsFinished(listener, loadedEngine, action)) {
      return;
    }

    // Check Job Status and update Listener
    String job = action.get("job").asText();
    this.checkJobStatus(run, listener, loadedEngine, job, engine, selfServiceContainer);
  }
}
