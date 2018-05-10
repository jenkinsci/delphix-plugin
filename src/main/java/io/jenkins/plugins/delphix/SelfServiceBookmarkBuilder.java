/**
 * Copyright (c) 2018 by Delphix. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Describes a build step for managing a Delphix Self Service Container
 * These build steps can be added in the job configuration page in Jenkins.
 */
public class SelfServiceBookmarkBuilder extends DelphixBuilder implements SimpleBuildStep {

    public final String delphixEngine;
    public final String delphixBookmark;
    public final String delphixOperation;
    public final String delphixContainer;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * [SelfServiceBookmarkBuilder description]
     *
     * @param delphixEngine         String
     * @param delphixBookmark       String
     * @param delphixOperation      String
     */
    @DataBoundConstructor
    public SelfServiceBookmarkBuilder(
        String delphixEngine,
        String delphixBookmark,
        String delphixOperation,
        String delphixContainer
    ) {
        this.delphixEngine = delphixEngine;
        this.delphixOperation = delphixOperation;
        this.delphixBookmark = delphixBookmark;
        this.delphixContainer = delphixContainer;
    }

    @Extension
    public static final class RefreshDescriptor extends SelfServiceDescriptor {

        /**
         * Add Engines to drop down
         */
        public ListBoxModel doFillDelphixEngineItems() {
            return super.doFillDelphixEngineItems();
        }

        public ListBoxModel doFillDelphixBookmarkItems(@QueryParameter String delphixEngine) {
            return super.doFillDelphixBookmarkItems(delphixEngine);
        }

        public ListBoxModel doFillDelphixContainerItems(@QueryParameter String delphixEngine) {
            return super.doFillDelphixSelfServiceItems(delphixEngine);
        }

        public ListBoxModel doFillDelphixOperationItems() {
            ListBoxModel operations = new ListBoxModel();
            operations.add("Create","Create");
            operations.add("Delete","Delete");
            operations.add("Share","Share");
            operations.add("Unshare","Unshare");
            return operations;
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return "Delphix - Self Service Bookmark";
        }
    }

    @Override
    public void perform(
        Run<?, ?> run,
        FilePath workspace,
        Launcher launcher,
        TaskListener listener
    ) throws InterruptedException, IOException {
        // Check if the input engine is not valid
        if (delphixBookmark.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
        }

        String engine = delphixEngine;
        String operationType = delphixOperation;
        String bookmark = delphixBookmark;

        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
        }

        DelphixEngine loadedEngine = GlobalConfiguration.getPluginClassDescriptor().getEngine(engine);
        SelfServiceBookmarkRepository bookmarkRepo = new SelfServiceBookmarkRepository(loadedEngine);
        SelfServiceRepository containerRepo = new SelfServiceRepository(loadedEngine);

        JsonNode action = MAPPER.createObjectNode();
        try {
            bookmarkRepo.login();
            switch (operationType) {
                case "Create":
                    containerRepo.login();
                    SelfServiceContainer container = containerRepo.get(delphixContainer);
                    action = bookmarkRepo.create("Created By Jenkins", container.getActiveBranch(), container.getReference());
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
                default: throw new DelphixEngineException("Undefined Self Service Bookmark Operation");
            }
        } catch (DelphixEngineException e) {
            // Print error from engine if job fails and abort Jenkins job
            listener.getLogger().println(e.getMessage());
        } catch (IOException e) {
            // Print error if unable to connect to engine and abort Jenkins job
            listener.getLogger().println(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { bookmarkRepo.getEngineAddress() }));
        }

        //Check for Action with a Completed State
        if (this.checkActionIsFinished(listener, loadedEngine, action)) {
            return;
        }

        //Check Job Status and update Listener
        String job = action.get("job").asText();
        this.checkJobStatus(run, listener, loadedEngine, job, engine, bookmark);
    }
}
