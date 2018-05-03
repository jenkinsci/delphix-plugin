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
 * Describes a VDB Rollback build step for the Delphix plugin
 */
public class RollbackBuilder extends ContainerBuilder {

    public final ArrayList<HookOperation> preRollbackHooks;
    public final ArrayList<HookOperation> postRollbackHooks;

    @DataBoundConstructor
    public RollbackBuilder(String delphixEngine, String delphixGroup, String delphixContainer, String retryCount,
            String delphixSnapshot, ArrayList<HookOperation> preRollbackHooks,
            ArrayList<HookOperation> postRollbackHooks) {
        super(delphixEngine, delphixGroup, delphixContainer, retryCount, "", delphixSnapshot, "", "");

        // Set the rollback hooks to be empty if there is no input
        if (preRollbackHooks != null) {
            this.preRollbackHooks = preRollbackHooks;
        } else {
            this.preRollbackHooks = new ArrayList<HookOperation>();
        }
        if (postRollbackHooks != null) {
            this.postRollbackHooks = postRollbackHooks;
        } else {
            this.postRollbackHooks = new ArrayList<HookOperation>();
        }
    }

    /**
     * Run the rollback job
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener)
            throws IOException, InterruptedException {
        return super.perform(build, listener, DelphixEngine.ContainerOperationType.ROLLBACK, preRollbackHooks,
                postRollbackHooks);
    }

    /**
     * Used for data binding on Jelly UI elements
     */
    public ArrayList<HookOperation> getPreRollbackHooks() {
        return preRollbackHooks;
    }

    /**
     * Used for data binding on Jelly UI elements
     */
    public ArrayList<HookOperation> getPostRollbackHooks() {
        return postRollbackHooks;
    }

    @Extension
    public static final class RollbackDescriptor extends ContainerDescriptor {

        /**
         * Add containers to drop down for Rollback action
         */
        public ListBoxModel doFillDelphixContainerItems(@QueryParameter String delphixEngine,
                @QueryParameter String delphixGroup) {
            return super.doFillDelphixContainerItems(delphixEngine, delphixGroup, ContainerType.VDB);
        }

        /**
         * Add snapshots to drop down for Rollback action
         */
        public ListBoxModel doFillDelphixSnapshotItems(@QueryParameter String delphixEngine,
                @QueryParameter String delphixGroup, @QueryParameter String delphixContainer) {
            return super.doFillDelphixSnapshotItems(delphixEngine, delphixGroup, delphixContainer,
                    DelphixEngine.ContainerOperationType.ROLLBACK);
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return Messages.getMessage(Messages.ROLLBACK_OPERATION);
        }
    }
}
