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

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;

/**
 * Describes a build step to create an environment
 * Currently only Unix single host environments are supported.
 */
public class EnvironmentCreateBuilder extends Builder {

    /**
     * The environment to operate on for the build step
     */
    public final String engine;
    public final String address;
    public final String user;
    public final String password;
    public final String toolkit;

    @DataBoundConstructor
    public EnvironmentCreateBuilder(String engine, String address, String user, String password, String toolkit) {
        this.engine = engine;
        this.address = address;
        this.user = user;
        this.password = password;
        this.toolkit = toolkit;
    }

    /**
     * Run the environment creation job
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener)
            throws IOException, InterruptedException {
        // Check if the input engine is not valid
        if (engine.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
            return false;
        }

        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_ENVIRONMENT));
            return false;
        }
        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        // Login to Delphix Engine and run either a refresh or sync job
        String job;
        try {
            delphixEngine.login();
            job = delphixEngine.createEnvironment(address, user, password, toolkit);
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

    @Extension
    public static final class RefreshDescriptor extends EnvironmentDescriptor {

        /**
         * Add engines to list of places where the environment can be created
         */
        public ListBoxModel doFillEngineItems() {
            return super.doFillDelphixEngineItems();
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return Messages.getMessage(Messages.ENVIRONMENT_CREATE_OPERATION);
        }
    }
}
