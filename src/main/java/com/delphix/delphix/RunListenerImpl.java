/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import java.io.IOException;
import java.util.Map;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

/**
 * Performs actions at certain events during a job run
 */
@Extension
public class RunListenerImpl extends RunListener<AbstractBuild<?, ?>> {

    public RunListenerImpl() {
        super();
    }

    /**
     * Runs after job completes
     */
    @Override
    public void onCompleted(AbstractBuild<?, ?> build, TaskListener listener) {
        try {
            // Cancel the job on the engine if it was aborted in Jenkins
            if (build.getResult() == Result.ABORTED) {
                // Check all environment variables to find all running Delphix jobs
                for (Map.Entry<String, String> jobEngine : build.getEnvironment(listener).entrySet()) {
                    if (jobEngine.getKey().contains("JOB-")) {
                        // Login and cancel job
                        DelphixEngine delphixEngine = new DelphixEngine(
                                GlobalConfiguration.getPluginClassDescriptor().getEngine(jobEngine.getValue()));
                        delphixEngine.login();
                        delphixEngine.cancelJob(jobEngine.getKey());
                        listener.getLogger().println(Messages.getMessage(Messages.CANCELED_JOB,
                                new String[] { jobEngine.getValue() }));
                    }
                }
            }
        } catch (IOException | InterruptedException | DelphixEngineException e) {
            listener.getLogger().println(Messages.getMessage(Messages.CANCEL_JOB_FAIL));
        }
        super.onCompleted(build, listener);
    }
}
