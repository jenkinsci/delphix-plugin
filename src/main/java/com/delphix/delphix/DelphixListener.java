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
import java.util.Map;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

/**
 * Performs cleanup on the Delphix Engine if a run is aborted.
 */
@Extension
public class DelphixListener extends RunListener<AbstractBuild<?, ?>> {

    public DelphixListener() {
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
