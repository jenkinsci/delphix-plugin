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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.delphix.delphix.DelphixContainer.ContainerType;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;

/**
 * Describes a build step for the Delphix plugin. The refresh, sync, and
 * provision build steps inherit from this class. These build steps can be added
 * in the job configuration page in Jenkins.
 */
public class ContainerBuilder extends Builder {

    /**
     * The container to operate on for the build step
     */
    public final String delphixEngine;
    public final String delphixGroup;
    public final String delphixContainer;
    public final String retryCount;
    public final String containerName;
    public final String delphixSnapshot;

    public ContainerBuilder(String delphixEngine, String delphixGroup, String delphixContainer, String retryCount,
            String containerName, String delphixSnapshot) {
        this.delphixEngine = delphixEngine;
        this.delphixGroup = delphixGroup;
        this.delphixContainer = delphixContainer;
        this.retryCount = retryCount;
        this.containerName = containerName;
        this.delphixSnapshot = delphixSnapshot;
    }

    /**
     * Run the job
     */
    public boolean perform(final AbstractBuild<?, ?> build, final BuildListener listener,
            DelphixEngine.ContainerOperationType operationType) throws InterruptedException {
        // Check if the input is not a valid target
        if (delphixSnapshot.equals("NULL")) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER));
            return false;
        }

        // Get the engine, the group, and the container on which to operate
        String engine = delphixSnapshot.split("\\|")[0];
        String group = delphixSnapshot.split("\\|")[1];
        String container = delphixSnapshot.split("\\|")[2];
        String location = delphixSnapshot.split("\\|")[3];

        // Targets tracks the containers on which to operate
        CopyOnWriteArrayList<DelphixContainer> targets = new CopyOnWriteArrayList<DelphixContainer>();

        // Retries tracks how many times an operation has failed and been retried for a container
        LinkedHashMap<String, Integer> retries = new LinkedHashMap<String, Integer>();

        // Containers is a list of containers on the Delphix Engine
        LinkedHashMap<String, DelphixContainer> containers;

        // Jobs is the list of jobs related to this operation
        ArrayList<String> jobs = new ArrayList<String>();

        // Check if the engine is a valid engine and instantiate it if it is
        if (GlobalConfiguration.getPluginClassDescriptor().getEngine(engine) == null) {
            listener.getLogger().println(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER));
            return false;
        }
        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));
        try {
            // Get containers from engine
            delphixEngine.login();
            containers = delphixEngine.listContainers();

            // Set the container targets of this operation which will be all containers if ALL was selected
            if (container.equals("ALL")) {
                targets = new CopyOnWriteArrayList<DelphixContainer>(containers.values());
                for (DelphixContainer target : targets) {
                    retries.put(target.getReference(), 0);
                }
            } else {
                DelphixContainer target = delphixEngine.listContainers().get(container);
                if (target != null) {
                    targets.add(target);
                } else {
                    throw new DelphixEngineException("The reference \"" + container + "\" is invalid");
                }
                retries.put(container, 0);
            }
        } catch (DelphixEngineException e) {
            // Print error from engine if login or list operations fail and abort job since engine is not functional
            listener.getLogger().println(e.getMessage());
            return false;
        } catch (IOException e) {
            // Print error if unable to connect to engine and abort Jenkins job
            listener.getLogger().println(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { delphixEngine.getEngineAddress() }));
            return false;
        }

        // The current statuses with the keys being the containers
        HashMap<String, JobStatus> status = new HashMap<String, JobStatus>();

        // The statuses last seen with keys being the containers
        HashMap<String, JobStatus> lastStatus = new HashMap<String, JobStatus>();

        // Tracks the state of the jobs as a group
        boolean running = true;

        // Login to Delphix Engine and run the job on the target(s)
        while (running) {
            // Set running to false and set it back to running if checks pass at the end of this loop
            running = false;

            // Loop through all target containers and run operations against them
            for (DelphixContainer target : targets) {
                targets.remove(target);
                try {
                    String job = "";
                    // Refresh operation
                    if (operationType.equals(DelphixEngine.ContainerOperationType.REFRESH) &&
                            target.getType() == ContainerType.VDB && target.getGroup().equals(group)) {
                        build.addAction(new PublishEnvVarAction(target.getReference(), engine));
                        job = delphixEngine.refreshContainer(target.getReference(), location);
                    } else if (operationType.equals(DelphixEngine.ContainerOperationType.SYNC) &&
                            target.getType() == ContainerType.SOURCE && target.getGroup().equals(group)) {
                        // Sync operation
                        build.addAction(new PublishEnvVarAction(target.getReference(), engine));
                        job = delphixEngine.sync(target.getReference());
                    } else if (operationType.equals(DelphixEngine.ContainerOperationType.PROVISIONVDB) &&
                            target.getGroup().equals(group)) {
                        // Provision operation
                        build.addAction(new PublishEnvVarAction(target.getReference(), engine));
                        job = delphixEngine.provisionVDB(target.getReference(), location, containerName);
                    } else if (operationType.equals(DelphixEngine.ContainerOperationType.DELETECONTAINER) &&
                            target.getGroup().equals(group)) {
                        // Delete operation
                        build.addAction(new PublishEnvVarAction(target.getReference(), engine));
                        job = delphixEngine.deleteContainer(target.getReference());
                    }
                    // Add job reference to environment variables so that it can be used by run listener
                    if (!job.isEmpty()) {
                        build.addAction(new PublishEnvVarAction(job, engine));
                        jobs.add(job);
                    }
                } catch (DelphixEngineException e) {
                    // Print error from engine if job fails and add container back to targets to retry the operation
                    listener.getLogger().println(
                            delphixEngine.getEngineAddress() + " - " + target.getName() + " - " + e.getMessage());
                    if (retries.get(target.getReference()).compareTo(Integer.decode(retryCount)) < 0) {
                        listener.getLogger()
                                .println(Messages.getMessage(Messages.RETRY, new String[] { target.getName() }));
                        targets.add(containers.get(target.getReference()));
                        retries.put(target.getReference(), retries.get(target.getReference()) + 1);
                    }
                } catch (IOException e) {
                    // Print error if unable to connect to engine and add container back to targets to retry operation
                    listener.getLogger().println(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                            new String[] { delphixEngine.getEngineAddress() }));
                    if (retries.get(target.getReference()).compareTo(Integer.decode(retryCount)) < 0) {
                        listener.getLogger()
                                .println(Messages.getMessage(Messages.RETRY, new String[] { target.getName() }));
                        targets.add(containers.get(target.getReference()));
                        retries.put(target.getReference(), retries.get(target.getReference()) + 1);
                    }
                }
            }

            // Add status trackers if they haven't been added before
            for (String job : jobs) {
                if (!status.containsKey(job)) {
                    status.put(job, new JobStatus());
                }
                if (!lastStatus.containsKey(job)) {
                    lastStatus.put(job, new JobStatus());
                }
            }

            // Display status of job
            for (String job : jobs) {
                // Get current job status
                try {
                    status.put(job, delphixEngine.getJobStatus(job));
                } catch (DelphixEngineException e) {
                    listener.getLogger().println(e.getMessage());
                } catch (IOException e) {
                    listener.getLogger().println(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                            new String[] { delphixEngine.getEngineAddress() }));
                }

                // Update status if it has changed on engine
                if (!status.get(job).getSummary().equals(lastStatus.get(job).getSummary())) {
                    listener.getLogger().println(delphixEngine.getEngineAddress() + " - " +
                            status.get(job).getTargetName() + status.get(job).getSummary());
                    lastStatus.put(job, status.get(job));
                }

                // If job is running on engine then mark this Jenkins job as still running
                if (status.get(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
                    running = true;
                } else if (!status.get(job).getStatus().equals(JobStatus.StatusEnum.COMPLETED)) {
                    // If job finished but did not complete successfully then restart it
                    running = true;
                    jobs.remove(job);
                    String target = status.get(job).getTarget();
                    if (retries.get(target).compareTo(Integer.decode(retryCount)) < 0) {
                        listener.getLogger().println(Messages.getMessage(Messages.RETRY, new String[] { target }));
                        targets.add(containers.get(target));
                        retries.put(target, retries.get(target) + 1);
                    }
                }
            }
            // Sleep for one second before checking again
            Thread.sleep(1000);
        }
        // Job completed successfully
        return true;
    }
}
