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
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import io.jenkins.plugins.delphix.objects.SelfServiceBookmark;

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
        return DelphixEngine.fillEnginesForDropdown();
    }


    /**
     * Fill Item ListBoxModels
     *
     * @param  delphixEngine [description]
     * @param  itemType      [description]
     *
     * @return               [description]
     */
    private ListBoxModel fillItems(String delphixEngine, String itemType) {
        ArrayList<Option> options = new ArrayList<Option>();

        if (delphixEngine.equals("NULL") || delphixEngine.equals(" ")) {
            options.add(new Option("N/A", "NULL"));
            return new ListBoxModel(options);
        }
        if (delphixEngine.isEmpty()) {
            return new ListBoxModel(options);
        }

        SelfServiceEngine engine = new SelfServiceEngine(
                GlobalConfiguration.getPluginClassDescriptor().getEngine(delphixEngine));

        try {
            try {
                engine.login();
                switch (itemType) {
                    case "SelfService":
                        LinkedHashMap<String, SelfServiceContainer> environments = engine.listSelfServices();
                        for (SelfServiceContainer environment : environments.values()) {
                            options.add(new Option(environment.getName(), environment.getReference()));
                        }
                        break;
                    case "Bookmark":
                        LinkedHashMap<String, SelfServiceBookmark> bookmarks = engine.listBookmarks();
                        for (SelfServiceBookmark bookmark : bookmarks.values()) {
                            options.add(new Option(bookmark.getName(), bookmark.getReference()));
                        }
                        break;
                    default: throw new DelphixEngineException("Invalid Self Service Item Type");
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
     * Add groups to drop down for build action
     *
     * @param   delphixEngine   String
     *
     * @return  ListBoxModel
     */
    public ListBoxModel doFillDelphixSelfServiceItems(@QueryParameter String delphixEngine) {
        return fillItems(delphixEngine, "SelfService");
    }

    /**
     *
     * [doFillDelphixBookmarkItems description]
     *
     * @param  delphixEngine [description]
     *
     * @return               [description]
     */
    public ListBoxModel doFillDelphixBookmarkItems(@QueryParameter String delphixEngine) {
        return fillItems(delphixEngine, "Bookmark");
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
