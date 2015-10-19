/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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
 * This class tests the sync build step which can be added to jobs.
 */
public class SyncBuilderTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test a successful sync job
     */
    @Test
    public void successfulBuildTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new SyncBuilder(TestConsts.oracleEngine + "|" + TestConsts.oracleSource));
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
     * Test an aborted sync job and make sure that it is cancelled and cleaned up properly.
     */
    @Test
    public void abortedBuildTest() throws Exception {
        DelphixEngine engine =
                configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();

        engine.login();
        p.getBuildersList().add(new SyncBuilder(engine.getEngineAddress() + "|" + TestConsts.oracleSource));
        QueueTaskFuture<FreeStyleBuild> future = p.scheduleBuild2(0);
        future.waitForStart();
        while (!future.isDone()) {
            Thread.sleep(1000);
            FreeStyleBuild build = p.getLastBuild();
            Assert.assertNotNull(build);
            try {
                for (String line : build.getLog(100)) {
                    if (line.contains("SnapSync of database")) {
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
                IOUtils.toString(build.getLogReader()).contains(Messages.getMessage(Messages.CANCELED_JOB,
                        new String[] { TestConsts.oracleEngine })));
    }

    /**
     * Test loading the job configuration form with no engines added in the global configuration.
     */
    @Test
    public void formNoEnginesTest() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new SyncBuilder("NULL"));

        HtmlForm form = j.createWebClient().getPage(p, "configure").getFormByName("config");
        HtmlSelect select = form.getSelectByName("_.delphixContainer");
        Assert.assertEquals(1, select.getOptions().size());
    }

    /**
     * Test loading the job configuration form with one engine added in the global configuration.
     */
    @Test
    public void formOneEngineTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new SyncBuilder("NULL"));

        HtmlForm form = j.createWebClient().getPage(p, "configure").getFormByName("config");
        HtmlSelect select = form.getSelectByName("_.delphixContainer");
        Assert.assertEquals(1, select.getOptions().size());
    }

    /**
     * Test running a sync job with a bad engine specified.
     */
    @Test
    public void formBadEngineTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);
        configureEngine("badengine", TestConsts.oracleUser, TestConsts.oraclePassword);
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, "badpassword");
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new SyncBuilder("NULL"));

        HtmlForm form = j.createWebClient().getPage(p, "configure").getFormByName("config");
        HtmlSelect select = form.getSelectByName("_.delphixContainer");
        Assert.assertEquals(Messages.getMessage(Messages.UNABLE_TO_LOGIN, new String[] { TestConsts.oracleEngine }),
                select.getOptions().get(0).getText());
    }

    /**
     * Test running a sync job with an engine that shows as a bad option in the drop down list.
     */
    @Test
    public void nullInputTest() throws Exception {
        configureEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new SyncBuilder("NULL"));
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
        p.getBuildersList().add(new SyncBuilder("badengine" + "|" + TestConsts.oracleSource));
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
        p.getBuildersList().add(new SyncBuilder(TestConsts.oracleEngine + "|" + "badcontainer"));
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
