/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

import com.delphix.delphix.DelphixContainer.ContainerType;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.util.ListBoxModel;

/**
 * Describes a VDB Refresh build step for the Delphix plugin
 */
public class RefreshBuilder extends DelphixBuilder {

    @DataBoundConstructor
    public RefreshBuilder(String delphixContainer) {
        super(delphixContainer);
    }

    /**
     * Run the refresh job
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener)
            throws IOException, InterruptedException {
        return super.perform(build, listener, true);
    }

    @Extension
    public static final class RefreshDescriptor extends DelphixDescriptor {

        /**
         * Add containers to drop down for Refresh action
         */
        public ListBoxModel doFillDelphixContainerItems() {
            return super.doFillDelphixContainerItems(ContainerType.VDB);
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
