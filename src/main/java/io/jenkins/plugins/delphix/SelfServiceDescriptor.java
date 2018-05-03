/**
 * Copyright (c) 2018 by Delphix. All rights reserved.
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

package io.jenkins.plugins.delphix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kohsuke.stapler.QueryParameter;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

/**
 * This class controls the drop down that is presented to the user when
 * configuring a Delphix build step in Jenkins job configuration.
 */
public abstract class SelfServiceDescriptor extends BuildStepDescriptor<Builder> {

    public SelfServiceDescriptor() {
        load();
    }

    /**
     * Add engines to drop down for build action
     *
     * @return ListBoxModel
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
     *
     * @param   delphixEngine
     *
     * @return  ListBoxModel
     */
    public ListBoxModel doFillDelphixSelfServiceItems(@QueryParameter String delphixEngine) {
        ArrayList<Option> options = new ArrayList<Option>();

        // Mark as N/A if engine is invalid
        if (delphixEngine.equals("NULL") || delphixEngine.equals(" ")) {
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
                LinkedHashMap<String, DelphixSelfService> environments = engine.listSelfServices();

                // Add groups to list
                for (DelphixSelfService environment : environments.values()) {
                    options.add(new Option(environment.getName(), environment.getReference()));
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
     * isApplicable
     *
     * @param  jobType AbstractProject
     *
     * @return         boolean
     */
    @SuppressWarnings("rawtypes")
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }
}
