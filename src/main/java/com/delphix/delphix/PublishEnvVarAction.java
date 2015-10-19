/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import hudson.EnvVars;
import hudson.model.EnvironmentContributingAction;
import hudson.model.InvisibleAction;
import hudson.model.AbstractBuild;

/**
 * An action to publish a single environment variable.
 */
public class PublishEnvVarAction extends InvisibleAction implements
        EnvironmentContributingAction {

    private String key;

    private String value;

    public PublishEnvVarAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        env.put(key, value);
    }
}
