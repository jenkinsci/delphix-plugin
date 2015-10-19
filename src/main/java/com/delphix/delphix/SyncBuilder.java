/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.util.ListBoxModel;
import hudson.model.AbstractBuild;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

import com.delphix.delphix.DelphixContainer.ContainerType;

/**
 * Describes a source Sync build step for the Delphix plugin
 */
public class SyncBuilder extends DelphixBuilder {

    @DataBoundConstructor
    public SyncBuilder(String delphixContainer) {
        super(delphixContainer);
    }

    /**
     * Run the sync job
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener)
            throws IOException, InterruptedException {
        return super.perform(build, listener, false);
    }

    @Extension
    public static final class DescriptorImpl extends DelphixDescriptor {

        /**
         * Add containers to drop down for Sync action
         */
        public ListBoxModel doFillDelphixContainerItems() {
            return super.doFillDelphixContainerItems(ContainerType.SOURCE);
        }

        /**
         * Name to display for build step
         */
        @Override
        public String getDisplayName() {
            return Messages.getMessage(Messages.SYNC_OPERATION);
        }
    }
}
