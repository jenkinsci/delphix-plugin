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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import hudson.util.FormValidation;

/**
 * This class tests the global Jenkins configuration form which is used to add engines to the Jenkins instance.
 */
public class GlobalConfigurationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test the Test Connection button with a successful login.
     */
    @Test
    public void loginSuccessTest() {
        FormValidation validation =
                GlobalConfiguration.getPluginClassDescriptor().doTestConnection(TestConsts.oracleEngine,
                        TestConsts.oracleUser,
                        TestConsts.oraclePassword);
        Assert.assertEquals(validation.getMessage(), Messages.getMessage(Messages.TEST_LOGIN_SUCCESS));
    }

    /**
     * Test the Test Connection button with a failed login.
     */
    @Test
    public void loginFailureTest() {
        FormValidation validation =
                GlobalConfiguration.getPluginClassDescriptor().doTestConnection(TestConsts.oracleEngine,
                        TestConsts.oracleUser,
                        "badpassword");
        Assert.assertEquals(validation.getMessage(), Messages.getMessage(Messages.TEST_LOGIN_FAILURE));
    }

    /**
     * Test the Test Connection button with a login to an engine that doesn't exist.
     */
    @Test
    public void loginConnectFailureTest() throws Exception {
        FormValidation validation =
                GlobalConfiguration.getPluginClassDescriptor().doTestConnection("badaddress", TestConsts.oracleUser,
                        TestConsts.oraclePassword);
        Assert.assertEquals(validation.getMessage(), Messages.getMessage(Messages.TEST_LOGIN_CONNECT));
    }

    /**
     * Test adding a single engine to the Jenkins instance.
     */
    @Test
    public void globalConifgurationSingleEngineTest() throws Exception {
        HtmlPage page = j.createWebClient().goTo("configure");
        HtmlForm form = page.getFormByName("config");
        form.getButtonByCaption("Add Engine").click();
        form.getInputByName("_.engineAddress").setValueAttribute(TestConsts.oracleEngine);
        form.getInputByName("_.engineUsername").setValueAttribute(TestConsts.oracleUser);
        form.getInputByName("_.enginePassword").setValueAttribute(TestConsts.oraclePassword);
        j.submit(form);
        DelphixEngine engine1 = GlobalConfiguration.getPluginClassDescriptor().getEngine(TestConsts.oracleEngine);
        Assert.assertEquals(TestConsts.oracleEngine, engine1.getEngineAddress());
        Assert.assertEquals(TestConsts.oracleUser, engine1.getEngineUsername());
        Assert.assertEquals(TestConsts.oraclePassword, engine1.getEnginePassword());

        // Try getting an engine that doesn't exist
        Assert.assertNull(GlobalConfiguration.getPluginClassDescriptor().getEngine("badengine"));
    }

    /**
     * Test adding multiple engines to the Jenkins instance.
     */
    @Test
    public void globalConifgurationMultiEngineTest() throws Exception {
        HtmlPage page = j.createWebClient().goTo("configure");
        HtmlForm form = page.getFormByName("config");
        form.getButtonByCaption("Add Engine").click();
        form.getButtonByCaption("Add Engine").click();
        form.getInputsByName("_.engineAddress").get(0).setValueAttribute(TestConsts.oracleEngine);
        form.getInputsByName("_.engineUsername").get(0).setValueAttribute(TestConsts.oracleUser);
        form.getInputsByName("_.enginePassword").get(0).setValueAttribute(TestConsts.oraclePassword);
        form.getInputsByName("_.engineAddress").get(1).setValueAttribute(TestConsts.mssqlEngine);
        form.getInputsByName("_.engineUsername").get(1).setValueAttribute(TestConsts.mssqlUser);
        form.getInputsByName("_.enginePassword").get(1).setValueAttribute(TestConsts.mssqlPassword);
        j.submit(form);
        DelphixEngine engine1 = GlobalConfiguration.getPluginClassDescriptor().getEngine(TestConsts.oracleEngine);
        Assert.assertEquals(TestConsts.oracleEngine, engine1.getEngineAddress());
        Assert.assertEquals(TestConsts.oracleUser, engine1.getEngineUsername());
        Assert.assertEquals(TestConsts.oraclePassword, engine1.getEnginePassword());
        DelphixEngine engine2 = GlobalConfiguration.getPluginClassDescriptor().getEngine(TestConsts.mssqlEngine);
        Assert.assertEquals(TestConsts.mssqlEngine, engine2.getEngineAddress());
        Assert.assertEquals(TestConsts.mssqlUser, engine2.getEngineUsername());
        Assert.assertEquals(TestConsts.mssqlPassword, engine2.getEnginePassword());
    }
}
