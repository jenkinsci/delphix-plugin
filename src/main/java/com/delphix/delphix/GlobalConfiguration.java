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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;

/**
 * This file controls the configuration of Delphix Engines in the Jenkins
 * plugin. It is used when the user interacts with the main Jenkins
 * configuration page.
 */
public class GlobalConfiguration extends JobProperty<Job<?, ?>> {

    private static final String ENGINE_ADDRESS = "engineAddress";
    private static final String ENGINE_USERNAME = "engineUsername";
    private static final String ENGINE_PASSWORD = "enginePassword";

    public static DescriptorImpl getPluginClassDescriptor() {
        Jenkins instance = Jenkins.getInstance();
        if (instance != null) {
            return (DescriptorImpl) instance.getDescriptor(GlobalConfiguration.class);
        } else {
            throw new IllegalStateException();
        }
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {
        /**
         * Address of Delphix Engine
         */
        private ArrayList<DelphixEngine> engines = new ArrayList<DelphixEngine>();

        public DescriptorImpl() {
            super(GlobalConfiguration.class);
            load();
        }

        @DataBoundConstructor
        public DescriptorImpl(ArrayList<DelphixEngine> engines) {
            this.engines = engines;
        }

        @Override
        public String getDisplayName() {
            return "Delphix";
        }

        /**
         * Handle persisting global configuration data
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData)
                throws FormException {
            // set private members and call save to persist these values
            engines = new ArrayList<DelphixEngine>();
            Object engine = formData.get("engine");

            // Handle the one engine case of a JSON Object otherwise it is an array of multiple engines
            if (engine instanceof JSONObject) {
                addEngine((JSONObject) engine);
            } else if (engine instanceof JSONArray) {
                JSONArray engines = (JSONArray) engine;
                for (int i = 0; i < engines.size(); i++) {
                    addEngine(engines.getJSONObject(i));
                }
            } else {
                // No engines in JSON
                engines = new ArrayList<DelphixEngine>();
            }
            save();
            return super.configure(req, formData);
        }

        /**
         * Test connection to Delphix Engine and display user status
         */
        public FormValidation doTestConnection(
                @QueryParameter(ENGINE_ADDRESS) final String engineAddress,
                @QueryParameter(ENGINE_USERNAME) final String engineUsername,
                @QueryParameter(ENGINE_PASSWORD) final String enginePassword) {
            DelphixEngine engine = new DelphixEngine(engineAddress,
                    engineUsername, enginePassword);
            try {
                try {
                    engine.login();
                    return FormValidation.ok(Messages.getMessage(Messages.TEST_LOGIN_SUCCESS));
                } catch (DelphixEngineException e) {
                    return FormValidation.error(Messages.getMessage(Messages.TEST_LOGIN_FAILURE));
                }
            } catch (IOException e) {
                return FormValidation.error(Messages.getMessage(Messages.TEST_LOGIN_CONNECT));
            }
        }

        /**
         * Add an engine to the list of available engines
         */
        private void addEngine(JSONObject engineJSON) {
            String address = engineJSON.getString(ENGINE_ADDRESS);
            String user = engineJSON.getString(ENGINE_USERNAME);
            String password = engineJSON.getString(ENGINE_PASSWORD);
            engines.add(new DelphixEngine(address, user, password));
        }

        /**
         * Get the engines that were added with this plugin
         */
        public ArrayList<DelphixEngine> getEngines() {
            return engines;
        }

        /**
         * Get an engine based upon its address
         */
        public DelphixEngine getEngine(String address) {
            for (DelphixEngine engine : engines) {
                if (engine.getEngineAddress().equals(address)) {
                    return engine;
                }
            }
            return null;
        }
    }
}
