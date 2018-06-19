/**
 * Copyright (c) 2015 by Delphix. All rights reserved. Licensed under the Apache License, Version
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

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.delphix.repos.JobRepository;

import java.io.IOException;
import java.util.Map;

/* Performs actions at certain events during a job run */
@Extension
public class RunListenerImpl extends RunListener<AbstractBuild<?, ?>> {

  public RunListenerImpl() {
    super();
  }

  /* Runs after job completes */
  @Override
  public void onCompleted(AbstractBuild<?, ?> build, TaskListener listener) {
    try {
      // Cancel the job on the engine if it was aborted in Jenkins
      if (build.getResult() == Result.ABORTED) {
        // Check all environment variables to find all running Delphix jobs
        for (Map.Entry<String, String> jobEngine : build.getEnvironment(listener).entrySet()) {
          if (jobEngine.getKey().contains("JOB-")) {
            // Login and cancel job
            DelphixEngine delphixEngine = new DelphixEngine(GlobalConfiguration.getPluginClassDescriptor().getEngine(jobEngine.getValue()));
            delphixEngine.login();
            JobRepository jobRepo = new JobRepository(delphixEngine);
            jobRepo.cancel(jobEngine.getKey());
            listener
                .getLogger()
                .println(
                    Messages.getMessage(
                        Messages.CANCELED_JOB, new String[] {jobEngine.getValue()}));
          }
        }
      }
    } catch (IOException | InterruptedException | DelphixEngineException e) {
      listener.getLogger().println(Messages.getMessage(Messages.CANCEL_JOB_FAIL));
    }
    super.onCompleted(build, listener);
  }
}
