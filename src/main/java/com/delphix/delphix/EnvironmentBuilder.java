/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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

package com.delphix.delphix;

import java.io.IOException;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;

/**
 * Describes a build step for the Delphix plugin. The refresh and sync build
 * steps inherit from this class. These build steps can be added in the job
 * configuration page in Jenkins.
 */
public class EnvironmentBuilder extends Builder {

    /**
     * The environment to operate on for the build step
     */
    public final String delphixEngine;
    public final String delphixEnvironment;

    public EnvironmentBuilder(String delphixEngine, String delphixEnvironment) {
        this.delphixEngine = delphixEngine;
        this.delphixEnvironment = delphixEnvironment;
    }

    /**
     * Run the refresh job
     */
    public boolean perform(final AbstractBuild<?, ?> build, final BuildListener listener,
            DelphixEngine.EnvironmentOperationType operationType)
                    throws InterruptedException {
        // Check if the input engine is not valid
        if (delphixEnvironment.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
            return false;
        }

        // Get the engine and the environment on the engine on which to operate
        String engine = delphixEnvironment.split("\\|")[0];
        String environment = delphixEnvironment.split("\\|")[1];

        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
            return false;
        }
        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        // Login to Delphix Engine and run either a refresh or sync job
        String job = "";
        try {
            delphixEngine.login();
            if (operationType.equals(DelphixEngine.EnvironmentOperationType.REFRESH)) {
                job = delphixEngine.refreshEnvironment(environment);
            } else if (operationType.equals(DelphixEngine.EnvironmentOperationType.DELETE)) {
                job = delphixEngine.deleteEnvironment(environment);
            }
        } catch (DelphixEngineException e) {
            // Print error from engine if job fails and abort Jenkins job
            listener.getLogger().println(e.getMessage());
            return false;
        } catch (IOException e) {
            // Print error if unable to connect to engine and abort Jenkins job
            listener.getLogger().println(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { delphixEngine.getEngineAddress() }));
            return false;
        }

        // Make job state available to clean up after run completes
        build.addAction(new PublishEnvVarAction(environment, engine));
        build.addAction(new PublishEnvVarAction(job, engine));
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
                return false;
            } catch (IOException e) {
                listener.getLogger().println(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                        new String[] { delphixEngine.getEngineAddress() }));
                return false;
            }

            // Update status if it has changed on Engine
            if (!status.getSummary().equals(lastStatus.getSummary())) {
                listener.getLogger().println(status.getSummary());
                lastStatus = status;
            }
            // Sleep for one second before checking again
            Thread.sleep(1000);
        }

        // Job completed
        return !status.getStatus().equals(JobStatus.StatusEnum.FAILED);
    }
}
