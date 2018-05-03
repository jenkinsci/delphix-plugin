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

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.tasks.Builder;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;

/**
 * Describes a build step for managing a Delphix Self Service Container
 * These build steps can be added in the job configuration page in Jenkins.
 */
public class SelfServiceBuilder extends Builder implements SimpleBuildStep {

    /**
     * Delphix Engine that hosts the Container
     */
    public final String delphixEngine;

    /**
     * Container to be Updated
     */
    public final String delphixEnvironment;

    /**
     * Operation used to update Container
     */
    public final String delphixOperation;

    /**
     * [SelfServiceBuilder description]
     *
     * @param delphixEngine         String
     * @param delphixEnvironment    String
     * @param delphixOperation      String
     */
    @DataBoundConstructor
    public SelfServiceBuilder(String delphixEngine, String delphixEnvironment, String delphixOperation) {
        this.delphixEngine = delphixEngine;
        this.delphixEnvironment = delphixEnvironment;
        this.delphixOperation = delphixOperation;
    }

    @Extension
    public static final class RefreshDescriptor extends SelfServiceDescriptor {

        /**
         * Add containers to drop down for Refresh action
         */
        public ListBoxModel doFillDelphixEngineItems() {
            return super.doFillDelphixEngineItems();
        }

        /**
         * Add containers to drop down for Refresh action
         *
         * @param  delphixEngine String
         * @return               ListBoxModel
         */
        public ListBoxModel doFillDelphixEnvironmentItems(@QueryParameter String delphixEngine) {
            return super.doFillDelphixSelfServiceItems(delphixEngine);
        }

        public ListBoxModel doFillDelphixOperationItems() {
            ListBoxModel operations = new ListBoxModel();
            operations.add("Refresh","Refresh");
            operations.add("Restore","Restore");
            operations.add("Reset","Reset");
            operations.add("Activate","Activate");

            //operations.add("Branch","Branch");
            //operations.add("Bookmark","Bookmark");
            //operations.add("Share","Share");
            return operations;
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return Messages.getMessage(Messages.SELFSERVICE_OPERATION);
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
        if (delphixEnvironment.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
        }

        // Get the engine, environment and operation on the engine on which to operate
        String engine = delphixEngine;
        String environment = delphixEnvironment;
        String operationType = delphixOperation;

        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
        }
        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        String job = "";
        try {
            //Log-in to engine
            delphixEngine.login();

            switch (operationType) {
                case "Refresh": job = delphixEngine.refreshSelfServiceContainer(environment);
                    break;
                case "Reset": job = delphixEngine.resetSelfServiceContainer(environment);
                    break;
                default: throw new DelphixEngineException("Undefined Self Service Operation");
            }

        } catch (DelphixEngineException e) {
            // Print error from engine if job fails and abort Jenkins job
            listener.getLogger().println(e.getMessage());
        } catch (IOException e) {
            // Print error if unable to connect to engine and abort Jenkins job
            listener.getLogger().println(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { delphixEngine.getEngineAddress() }));
        }

        // Make job state available to clean up after run completes
        run.addAction(new PublishEnvVarAction(environment, engine));
        run.addAction(new PublishEnvVarAction(job, engine));

        JobStatus status = new JobStatus();
        JobStatus lastStatus = new JobStatus();

        // Display status of job
        while (status.getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            // Get current job status and abort the Jenkins job if getting the
            // status fails
            try {
                status = delphixEngine.getJobStatus(job);
            } catch (DelphixEngineException e) {
                listener.getLogger().println(e.getMessage());
            } catch (IOException e) {
                listener.getLogger().println(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                        new String[] { delphixEngine.getEngineAddress() }));
            }

            // Update status if it has changed on Engine
            if (!status.getSummary().equals(lastStatus.getSummary())) {
                listener.getLogger().println(status.getSummary());
                lastStatus = status;
            }
            // Sleep for one second before checking again
            Thread.sleep(1000);
        }
    }
}
