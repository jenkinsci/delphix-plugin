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
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import io.jenkins.plugins.delphix.objects.Action;
import io.jenkins.plugins.delphix.objects.Job;
import io.jenkins.plugins.delphix.repos.ActionRepository;
import io.jenkins.plugins.delphix.repos.JobRepository;

import java.io.IOException;

/* Shared Methods for Delphix Builders */
public abstract class DelphixBuilder extends Builder {

  protected Boolean checkActionIsFinished(
      TaskListener listener, DelphixEngine engine, JsonNode action) {
    Boolean status = false;
    try {
      engine.login();
      ActionRepository actionRepo = new ActionRepository(engine);
      Action actionStatus = actionRepo.get(action.get("action").asText());
      if (actionStatus.getState().equals("COMPLETED")) {
        String message = actionStatus.getTitle() + ": " + actionStatus.getState();
        listener.getLogger().println(message);
        status = true;
      }
    } catch (DelphixEngineException e) {
      listener.getLogger().println(e.getMessage());
    } catch (IOException e) {
      listener
          .getLogger()
          .println(
              Messages.getMessage(
                  Messages.UNABLE_TO_CONNECT, new String[] {engine.getEngineAddress()}));
    }
    return status;
  }

  protected void checkJobStatus(
      Run<?, ?> run,
      TaskListener listener,
      DelphixEngine loadedEngine,
      String job,
      String engine,
      String action) {
    // Make job state available to clean up after run completes
    run.addAction(new PublishEnvVarAction(action, engine));
    run.addAction(new PublishEnvVarAction(job, engine));

    Job status =
        new Job(
            Job.StatusEnum.RUNNING,
            "type",
            "reference",
            "namespace",
            "name",
            "actionType",
            "target",
            "targetObjectType",
            "jobState",
            "startTime",
            "updateTime",
            false,
            false,
            false,
            "user",
            "emailAddresses",
            "title",
            0,
            "targetName",
            "parentActionState",
            "parentAction");
    Job lastStatus = status;

    // Display status of job
    while (status.getStatus().equals(Job.StatusEnum.RUNNING)) {
      try {
        loadedEngine.login();
        JobRepository jobRepo = new JobRepository(loadedEngine);
        status = jobRepo.get(job);
      } catch (DelphixEngineException e) {
        listener.getLogger().println(e.getMessage());
        return;
      } catch (IOException e) {
        listener
            .getLogger()
            .println(
                Messages.getMessage(
                    Messages.UNABLE_TO_CONNECT, new String[] {loadedEngine.getEngineAddress()}));
        return;
      }

      // Update status if it has changed on Engine
      if (!status.getPercentComplete().equals(lastStatus.getPercentComplete())) {
        listener
            .getLogger()
            .println(
                status.getActionType()
                    + " "
                    + status.getReference()
                    + ": "
                    + status.getPercentComplete()
                    + "% COMPLETED.");
      }
      lastStatus = status;

      // Sleep for one second before checking again
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        return;
      }
    }
  }
}
