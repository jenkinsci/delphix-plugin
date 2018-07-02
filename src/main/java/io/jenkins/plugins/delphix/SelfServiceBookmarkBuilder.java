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

import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import io.jenkins.plugins.delphix.repos.SelfServiceBookmarkRepository;
import io.jenkins.plugins.delphix.repos.SelfServiceRepository;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;

/*
 * Describes a build step for managing a Delphix Self Service Container These build steps can be
 * added in the job configuration page in Jenkins.
 */
public class SelfServiceBookmarkBuilder extends DelphixBuilder implements SimpleBuildStep {

  public final String delphixEngine;
  public final String delphixBookmark;
  public final String delphixOperation;
  public final String delphixContainer;

  private boolean saveToProps;
  private boolean loadFromProps;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * SelfServiceBookmarkBuilder description.
   *
   * @param delphixEngine String
   * @param delphixBookmark String
   * @param delphixOperation String
   * @param delphixContainer String
   */
  @DataBoundConstructor
  public SelfServiceBookmarkBuilder(
      String delphixEngine,
      String delphixBookmark,
      String delphixOperation,
      String delphixContainer) {
    this.delphixEngine = delphixEngine;
    this.delphixOperation = delphixOperation;
    this.delphixBookmark = delphixBookmark;
    this.delphixContainer = delphixContainer;
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

    /* Add Engines to drop down */
    public ListBoxModel doFillDelphixEngineItems() {
      return super.doFillDelphixEngineItems();
    }

    public ListBoxModel doFillDelphixBookmarkItems(@QueryParameter String delphixEngine) {
      return super.doFillDelphixBookmarkItems(delphixEngine);
    }

    public ListBoxModel doFillDelphixContainerItems(@QueryParameter String delphixEngine) {
      return super.doFillDelphixSelfServiceItems(delphixEngine);
    }

    /**
     * Create Dropodown for Operations.
     *
     * @return ListBoxModel
     */
    public ListBoxModel doFillDelphixOperationItems() {
      ListBoxModel operations = new ListBoxModel();
      operations.add("Create", "Create");
      operations.add("Delete", "Delete");
      operations.add("Share", "Share");
      operations.add("Unshare", "Unshare");
      return operations;
    }

    /* Name to display for build step */
    @Override
    public String getDisplayName() {
      return "Delphix - Self Service Bookmark";
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    // Check if the input engine is not valid
    if (delphixBookmark.equals("NULL")) {
      listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
    }

    String engine = delphixEngine;
    String operationType = delphixOperation;
    String container = delphixContainer;
    String bookmark = delphixBookmark;

    if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
      listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
    }

    // Overwrite values from Delphix Properties
    File file = new File(workspace + "/delphix.properties");
    DelphixProperties delphixProps = new DelphixProperties(file, listener);
    if (this.loadFromProps) {
      try {
        // engine = delphixProps.getEngine();
        container = delphixProps.getContainerReference();
        // operationType = delphixProps.getContainerOperation();
        // bookmark = delphixProps.getBookmarkReference();
      } catch (Throwable t) {
        listener.getLogger().println(t.getMessage());
      }
    }
    if (this.saveToProps) {
      // delphixProps.setEngine(engine);
      delphixProps.setBookmarkReference(bookmark);
      // delphixProps.setBookmarkOperation(operationType);
    }

    DelphixEngine loadedEngine = GlobalConfiguration.getPluginClassDescriptor().getEngine(engine);


    JsonNode action = MAPPER.createObjectNode();
    try {
      loadedEngine.login();
      SelfServiceBookmarkRepository bookmarkRepo = new SelfServiceBookmarkRepository(loadedEngine);
      SelfServiceRepository containerRepo = new SelfServiceRepository(loadedEngine);

      switch (operationType) {
        case "Create":
          SelfServiceContainer containerObj = containerRepo.get(container);
          String buildName = "Created by Jenkins: Job #" + run.number;
          action =
              bookmarkRepo.create(
                  buildName, containerObj.getActiveBranch(), containerObj.getReference());
          delphixProps.setBookmarkReference(action.get("result").toString());
          break;
        case "Share":
          action = bookmarkRepo.share(bookmark);
          break;
        case "Unshare":
          action = bookmarkRepo.unshare(bookmark);
          break;
        case "Delete":
          action = bookmarkRepo.delete(bookmark);
          break;
        default:
          throw new DelphixEngineException("Undefined Self Service Bookmark Operation");
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
                  Messages.UNABLE_TO_CONNECT, new String[] {loadedEngine.getEngineAddress()}));
    }

    // Check for Action with a Completed State
    if (this.checkActionIsFinished(listener, loadedEngine, action)) {
      return;
    }

    // Check Job Status and update Listener
    String job = action.get("job").asText();
    this.checkJobStatus(run, listener, loadedEngine, job, engine, bookmark);
  }
}
