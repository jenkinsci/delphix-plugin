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

package io.jenkins.plugins.delphix;

import java.io.IOException;
import java.util.ArrayList;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import io.jenkins.plugins.delphix.DelphixContainer.ContainerType;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.util.ListBoxModel;

/**
 * Describes a VDB Refresh build step for the Delphix plugin
 */
public class RefreshBuilder extends ContainerBuilder {

    public final ArrayList<HookOperation> preRefreshHooks;
    public final ArrayList<HookOperation> postRefreshHooks;

    @DataBoundConstructor
    public RefreshBuilder(String delphixEngine, String delphixGroup, String delphixContainer, String retryCount,
            String delphixSnapshot, ArrayList<HookOperation> preRefreshHooks,
            ArrayList<HookOperation> postRefreshHooks) {
        super(delphixEngine, delphixGroup, delphixContainer, retryCount, "", delphixSnapshot, "", "");

        // Set the refresh hooks to be empty if there is no input
        if (preRefreshHooks != null) {
            this.preRefreshHooks = preRefreshHooks;
        } else {
            this.preRefreshHooks = new ArrayList<HookOperation>();
        }
        if (postRefreshHooks != null) {
            this.postRefreshHooks = postRefreshHooks;
        } else {
            this.postRefreshHooks = new ArrayList<HookOperation>();
        }
    }

    /**
     * Run the refresh job
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener)
            throws IOException, InterruptedException {
        return super.perform(build, listener, DelphixEngine.ContainerOperationType.REFRESH, preRefreshHooks,
                postRefreshHooks);
    }

    /**
     * Used for data binding on Jelly UI elements
     */
    public ArrayList<HookOperation> getPreRefreshHooks() {
        return preRefreshHooks;
    }

    /**
     * Used for data binding on Jelly UI elements
     */
    public ArrayList<HookOperation> getPostRefreshHooks() {
        return postRefreshHooks;
    }

    @Extension
    public static final class RefreshDescriptor extends ContainerDescriptor {

        /**
         * Add containers to drop down for Refresh action
         */
        public ListBoxModel doFillDelphixContainerItems(@QueryParameter String delphixEngine,
                @QueryParameter String delphixGroup) {
            return super.doFillDelphixContainerItems(delphixEngine, delphixGroup, ContainerType.VDB);
        }

        /**
         * Add snapshots to drop down for Refresh action
         */
        public ListBoxModel doFillDelphixSnapshotItems(@QueryParameter String delphixEngine,
                @QueryParameter String delphixGroup, @QueryParameter String delphixContainer) {
            return super.doFillDelphixSnapshotItems(delphixEngine, delphixGroup, delphixContainer,
                    DelphixEngine.ContainerOperationType.REFRESH);
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return Messages.getMessage(Messages.REFRESH_OPERATION);
        }
    }
}
