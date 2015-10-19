/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import org.junit.Assert;
import org.junit.Test;

import hudson.EnvVars;

/**
 * This test class is to cover a function that must exist but isn't used at all.
 */
public class PublishEnvVarActionTest {

    @Test
    public void buildTest() {
        PublishEnvVarAction action = new PublishEnvVarAction("key1", "value1");
        EnvVars vars = new EnvVars();
        action.buildEnvVars(null, vars);
        Assert.assertEquals("value1", vars.get("key1"));
    }

}
