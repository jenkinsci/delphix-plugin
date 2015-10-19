/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

import java.io.IOException;
import java.util.ArrayList;

import com.delphix.delphix.DelphixContainer.ContainerType;

/**
 * This class controls the drop down that is presented to the user when
 * configuring a Delphix build step in Jenkins job configuration.
 */
public abstract class DelphixDescriptor extends BuildStepDescriptor<Builder> {

    public DelphixDescriptor() {
        load();
    }

    /**
     * Add containers to drop down for build action
     */
    public ListBoxModel doFillDelphixContainerItems(ContainerType containerType) {
        ArrayList<Option> options = new ArrayList<Option>();
        // Loop through all engines added to Jenkins
        for (DelphixEngine engine : GlobalConfiguration.getPluginClassDescriptor().getEngines()) {
            DelphixEngine delphixEngine = new DelphixEngine(engine);
            try {
                // login to engine
                try {
                    delphixEngine.login();

                    // List containers on engine
                    ArrayList<DelphixContainer> containers = delphixEngine.listContainers();

                    // Add containers to engine
                    for (DelphixContainer container : containers) {
                        if (container.getType().equals(containerType)) {
                            options.add(new Option(container.getEngineAddress() + " - " + container.getName(),
                                    container.getEngineAddress() + "|" + container.getReference()));
                        }
                    }
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
     * Applicable for all jobs
     */
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }
}
