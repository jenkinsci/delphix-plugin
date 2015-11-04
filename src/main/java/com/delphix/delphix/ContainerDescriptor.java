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
import java.util.LinkedHashMap;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.delphix.delphix.DelphixContainer.ContainerType;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

/**
 * This class controls the drop down that is presented to the user when
 * configuring a Delphix build step in Jenkins job configuration.
 */
public abstract class ContainerDescriptor extends BuildStepDescriptor<Builder> {

    @DataBoundConstructor
    public ContainerDescriptor() {
        load();
    }

    /**
     * Add engines to drop down for build action
     */
    public ListBoxModel doFillDelphixEngineItems() {
        ArrayList<Option> options = new ArrayList<Option>();
        // Loop through all engines added to Jenkins
        for (DelphixEngine engine : GlobalConfiguration.getPluginClassDescriptor().getEngines()) {
            DelphixEngine delphixEngine = new DelphixEngine(engine);
            try {
                // login to engine
                try {
                    delphixEngine.login();

                    options.add(new Option(delphixEngine.getEngineAddress(), delphixEngine.getEngineAddress()));
                } catch (DelphixEngineException e) {
                    // Add message to drop down if unable to login to engine
                    options.add(new Option(Messages.getMessage(Messages.UNABLE_TO_LOGIN,
                            new String[] { delphixEngine.getEngineAddress() }), "NULL"));
                    continue;
                }
            } catch (IOException e) {
                // Add message to drop down if unable to connect to engine
                options.add(new Option(Messages.getMessage(Messages.UNABLE_TO_CONNECT,
                        new String[] { delphixEngine.getEngineAddress() }), "NULL"));
            }
        }

        // If there are no engines state that in the drop down
        if (GlobalConfiguration.getPluginClassDescriptor().getEngines().size() == 0) {
            // Add message to drop down if no engines in Jenkins
            options.add(new Option(Messages.getMessage(Messages.NO_ENGINES), "NULL"));
        }
        return new ListBoxModel(options);
    }

    /**
     * Add groups to drop down for build action
     */
    public ListBoxModel doFillDelphixGroupItems(@QueryParameter String delphixEngine) {
        ArrayList<Option> options = new ArrayList<Option>();

        // Mark as N/A if engine is invalid
        if (delphixEngine.equals("NULL")) {
            options.add(new Option("N/A", "NULL"));
            return new ListBoxModel(options);
        }

        if (delphixEngine.isEmpty()) {
            return new ListBoxModel(options);
        }
        // Loop through all engines added to Jenkins
        DelphixEngine engine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(delphixEngine));
        try {
            // login to engine
            try {
                engine.login();

                // Get list of groups on engine
                ArrayList<DelphixGroup> groups = engine.listGroups();

                // Add groups to list
                for (DelphixGroup group : groups) {
                    options.add(new Option(group.getName(), delphixEngine + "|" + group.getReference()));
                }
            } catch (DelphixEngineException e) {
                // Add message to drop down if unable to login to engine
                options.add(new Option(
                        Messages.getMessage(Messages.UNABLE_TO_LOGIN, new String[] { engine.getEngineAddress() }),
                        "NULL"));
            }
        } catch (IOException e) {
            // Add message to drop down if unable to connect to engine
            options.add(new Option(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { engine.getEngineAddress() }),
                    "NULL"));
        }

        return new ListBoxModel(options);
    }

    /**
     * Add containers to drop down for build action
     */
    public ListBoxModel doFillDelphixContainerItems(@QueryParameter String delphixGroup, ContainerType containerType) {
        ArrayList<Option> options = new ArrayList<Option>();

        if (delphixGroup.equals("NULL")) {
            options.add(new Option("N/A", "NULL"));
            return new ListBoxModel(options);
        }

        if (delphixGroup.isEmpty()) {
            return new ListBoxModel(options);
        }

        // Get the engine and group
        String engine = delphixGroup.split("\\|")[0];
        String group = delphixGroup.split("\\|")[1];

        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        // Add refresh and sync all options
        if (containerType.equals(ContainerType.VDB)) {
            options.add(new Option("Refresh all", delphixEngine.getEngineAddress() + "|" + group + "|" + "ALL"));

        } else if (containerType.equals(ContainerType.SOURCE)) {
            options.add(new Option("Sync all", delphixEngine.getEngineAddress() + "|" + group + "|" + "ALL"));
        }

        try {
            // login to engine
            try {
                delphixEngine.login();

                // List containers on engine
                LinkedHashMap<String, DelphixContainer> containers = delphixEngine.listContainers();

                // Add containers to list
                for (DelphixContainer container : containers.values()) {
                    if ((container.getType().equals(containerType) || containerType.equals(ContainerType.ALL)) &&
                            container.getGroup().equals(group)) {
                        options.add(new Option(container.getName(),
                                container.getEngineAddress() + "|" + group + "|" + container.getReference()));
                    }
                }
            } catch (DelphixEngineException e) {
                // Add message to drop down if unable to login to engine
                options.add(new Option(Messages.getMessage(Messages.UNABLE_TO_LOGIN,
                        new String[] { delphixEngine.getEngineAddress() }), "NULL"));
            }
        } catch (IOException e) {
            // Add message to drop down if unable to connect to engine
            options.add(new Option(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { delphixEngine.getEngineAddress() }),
                    "NULL"));
        }
        return new ListBoxModel(options);
    }

    /**
     * Add snapshot to drop down for build action
     */
    public ListBoxModel doFillDelphixSnapshotItems(@QueryParameter String delphixContainer,
            DelphixEngine.ContainerOperationType operationType) {
        ArrayList<Option> options = new ArrayList<Option>();

        if (delphixContainer.equals("NULL")) {
            options.add(new Option("N/A", "NULL"));
            return new ListBoxModel(options);
        }

        if (delphixContainer.isEmpty()) {
            return new ListBoxModel(options);
        }

        // Get the engine and group
        String engine = delphixContainer.split("\\|")[0];
        String group = delphixContainer.split("\\|")[1];
        String container = delphixContainer.split("\\|")[2];

        DelphixEngine delphixEngine = new DelphixEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(engine));

        // Add semantic options for latest snapshot and latest point
        options.add(new Option("Latest Point",
                delphixEngine.getEngineAddress() + "|" + group + "|" + container + "|" +
                        DelphixEngine.CONTENT_LATEST_POINT));
        options.add(new Option("Latest Snapshot",
                delphixEngine.getEngineAddress() + "|" + group + "|" + container + "|" +
                        DelphixEngine.CONTENT_LATEST_SNAPSHOT));

        // If all containers in group are targeted then just make semantic options available
        if (container.equals("ALL")) {
            return new ListBoxModel(options);
        }

        try {
            // login to engine
            try {
                delphixEngine.login();

                // List snapshots on engine by parent if refresh operation or current container otherwise
                ArrayList<DelphixSnapshot> snapshots = delphixEngine.listSnapshots();
                String containerRef;
                if (operationType.equals(DelphixEngine.ContainerOperationType.REFRESH)) {
                    containerRef = delphixEngine.getParentContainer(container);
                } else {
                    containerRef = container;

                }

                // Add snapshots to drop down and filter list by container selected above
                for (DelphixSnapshot snapshot : snapshots) {
                    if (snapshot.getContainerRef().equals(containerRef)) {
                        options.add(new Option(snapshot.getName(),
                                delphixEngine.getEngineAddress() + "|" + group + "|" + container + "|" +
                                        snapshot.getReference()));
                    }
                }
            } catch (DelphixEngineException e) {
                // Add message to drop down if unable to login to engine
                options.add(new Option(Messages.getMessage(Messages.UNABLE_TO_LOGIN,
                        new String[] { delphixEngine.getEngineAddress() }), "NULL"));
            }
        } catch (IOException e) {
            // Add message to drop down if unable to connect to engine
            options.add(new Option(
                    Messages.getMessage(Messages.UNABLE_TO_CONNECT, new String[] { delphixEngine.getEngineAddress() }),
                    "NULL"));
        }
        return new ListBoxModel(options);
    }

    /**
     * Applicable for all jobs
     */
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }
}
