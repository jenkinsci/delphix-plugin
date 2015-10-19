/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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
public class DelphixBuilder extends Builder {

    /**
     * The container to operate on for the build step
     */
    public final String delphixContainer;

    public DelphixBuilder(String delphixContainer) {
        this.delphixContainer = delphixContainer;
    }

    /**
     * Run the refresh job
     */
    public boolean perform(final AbstractBuild<?, ?> build, final BuildListener listener, boolean refresh)
            throws InterruptedException {
        // Check if the input engine does not have any containers or is not available
        if (delphixContainer.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER));
            return false;
        }

        // Get the engine and the container on the engine on which to operate
        String engine = delphixContainer.split("\\|")[0];
        String container = delphixContainer.split("\\|")[1];

        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER));
            return false;
        }
        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        // Login to Delphix Engine and run either a refresh or sync job
        String job;
        try {
            delphixEngine.login();
            if (refresh) {
                job = delphixEngine.refresh(container);
            } else {
                job = delphixEngine.sync(container);
            }
        } catch (DelphixEngineException e) {
            // Print error from engine if job fails and abort Jenkins job
            listener.getLogger().println(e.getMessage());
            return false;
        } catch (IOException e) {
            // Print error if unable to connect to engine and abort Jenkins job
            listener.getLogger().println(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                    new String[] { delphixEngine.getEngineAddress() }));
            return false;
        }

        // Make job state available to clean up after run completes
        build.addAction(new PublishEnvVarAction(container, engine));
        build.addAction(new PublishEnvVarAction(job, engine));
        JobStatus status = new JobStatus();
        JobStatus lastStatus = new JobStatus();

        // Display status of job
        while (status.getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            // Get current job status and abort the Jenkins job if getting the status fails
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
        // Job completed successfully
        return true;
    }
}
