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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.queue.QueueTaskFuture;

/**
 * This class tests the refresh build step which can be added to jobs.
 */
public class RefreshBuilderTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test a successful refresh job
     */
    @Test
    public void successfulBuildTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList()
                .add(new RefreshBuilder(TestConsts.oracleEngine, TestConsts.oracleEngine + "|" + TestConsts.oracleGroup,
                        TestConsts.oracleEngine + "|" + TestConsts.oracleGroup + "|" + TestConsts.oracleVDB1, "1"));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        while (!future.isDone()) {
            // wait for cancel to finish (needs to send cancel to Delphix Engine)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Swallow spurious interrupt exception
            }
        }
        FreeStyleBuild build = p.getLastBuild();
        Assert.assertNotNull(build);
        Assert.assertTrue(build.getResult().equals(Result.SUCCESS));
        Assert.assertTrue(
                IOUtils.toString(build.getLogReader()).contains("completed successfully"));
    }

    /**
     * Test an aborted refresh job and make sure that it is cancelled and cleaned up properly.
     */
    @Test
    public void abortedBuildTest() throws Exception {
        DelphixEngine engine =
                configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();

        engine.login();
        p.getBuildersList()
                .add(new RefreshBuilder(TestConsts.oracleEngine, TestConsts.oracleEngine + "|" + TestConsts.oracleGroup,
                        TestConsts.oracleEngine + "|" + TestConsts.oracleGroup + "|" + TestConsts.oracleVDB1, "1"));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        future.waitForStart();
        while (!future.isDone()) {
            Thread.sleep(1000);
            try {
                FreeStyleBuild build = p.getLastBuild();
                Assert.assertNotNull(build);
                for (String line : build.getLog(100)) {
                    if (line.contains("Exporting storage")) {
                        build.doStop();
                        break;
                    }
                }
            } catch (IOException e) {
                // Logs not available yet so swallow the exception
            }
        }
        while (!future.isDone()) {
            // wait for cancel to finish (needs to send cancel to Delphix Engine)
        }
        FreeStyleBuild build = p.getLastBuild();
        Assert.assertNotNull(build);
        Assert.assertTrue(build.getResult().equals(Result.ABORTED));
        Assert.assertTrue(
                IOUtils.toString(build.getLogReader()).contains("Cancelled job " + TestConsts.oracleEngine));
    }

    /**
     * Test running a refresh job with an engine that shows as a bad option in the drop down list.
     */
    @Test
    public void nullInputTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new RefreshBuilder("NULL", "NULL|NULL", "NULL|NULL|NULL", "1"));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        FreeStyleBuild b = future.get();
        Assert.assertEquals(Result.FAILURE, b.getResult());
        Assert.assertTrue(
                IOUtils.toString(b.getLogReader()).contains(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER)));
    }

    /**
     * Test running a job with an engine that has a bad address.
     */
    @Test
    public void badAddressTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList()
                .add(new RefreshBuilder("badengine", "badengine" + "|" + TestConsts.oracleGroup,
                        "badengine" + "|" + TestConsts.oracleGroup + "|" + TestConsts.oracleVDB2, "1"));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        while (!future.isDone()) {
            // wait for cancel to finish (needs to send cancel to Delphix Engine)
            Thread.sleep(1000);
        }
        FreeStyleBuild b = future.get();
        Assert.assertEquals(Result.FAILURE, b.getResult());
        Assert.assertTrue(
                IOUtils.toString(b.getLogReader()).contains(Messages.getMessage(Messages.INVALID_ENGINE_CONTAINER)));
    }

    /**
     * Test running a job with an engine that doesn't have the specified container.
     */
    @Test
    public void badContainerTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList()
                .add(new RefreshBuilder(TestConsts.oracleEngine, TestConsts.oracleEngine + "|" + TestConsts.oracleGroup,
                        TestConsts.oracleEngine + "|" + TestConsts.oracleGroup + "|" + "badcontainer", "1"));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        while (!future.isDone()) {
            // wait for cancel to finish (needs to send cancel to Delphix Engine)
        }
        FreeStyleBuild build = p.getLastBuild();
        Assert.assertNotNull(build);
        Assert.assertEquals(Result.FAILURE, build.getResult());
        Assert.assertTrue(
                IOUtils.toString(build.getLogReader())
                        .contains("The reference \"badcontainer\" is invalid"));
    }

    private DelphixEngine configureEngine(String address, String user, String password) throws Exception {
        DelphixEngine engine = new DelphixEngine(address, user, password);
        HtmlPage page = j.createWebClient().goTo("configure");
        HtmlForm form = page.getFormByName("config");
        form.getButtonByCaption("Add Engine").click();
        form.getInputByName("_.engineAddress").setValueAttribute(address);
        form.getInputByName("_.engineUsername").setValueAttribute(user);
        form.getInputByName("_.enginePassword").setValueAttribute(password);
        j.submit(form);
        return engine;
    }
}
