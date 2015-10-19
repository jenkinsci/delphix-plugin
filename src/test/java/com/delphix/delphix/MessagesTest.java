/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test retrieving localized messages for english for the application.
 */
public class MessagesTest {

    /**
     * Test getting a basic message.
     */
    @Test
    public void getMessageTest() {
        Assert.assertEquals(Messages.getMessage(Messages.NO_ENGINES), "Add Delphix Engines in Jenkins configuration");
    }

    /**
     * Test getting a message that has arguments substituted into it.
     */
    @Test
    public void getMessageSubstituteTest() {
        Assert.assertEquals(Messages.getMessage(Messages.UNABLE_TO_LOGIN, new String[] { "engineName" }),
                "Unable to login to Delphix Engine: engineName");
    }
}
