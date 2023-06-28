package io.jenkins.plugins.delphix;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.ExtensionList;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BatchFile;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.Secret;
import io.jenkins.plugins.constant.Constant;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalPluginConfiguration;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;
import org.jvnet.hudson.test.JenkinsRule;


public class ProvisionVDBFromSnapshotTest {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Before
    public void init() throws Exception {
        DelphixGlobalConfiguration globalConfig1 =
                GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
        globalConfig1.setDctUrl("https://dct6.dlpxdc.co" + Constant.API_VERSION);


        globalConfig1.save();

        StringCredentialsImpl c = new StringCredentialsImpl(CredentialsScope.USER, "test123",
                "description", Secret.fromString(
                        "apk 1.YKhbbGsoA2LUoaIpZ8nxPQsOQbQ5BBWAdB7AhWZISkGjeB6JsyiImpRP0EtKG86y"));
        CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(),
                c);
    }

    @Test
    // @Timeout(value = 10, unit = TimeUnit.MINUTES, threadMode = SEPARATE_THREAD)
    public void testProvisionWithPolling() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        ProvisionVDBFromSnapshot builder = new ProvisionVDBFromSnapshot();
        builder.setSourceDataId("4-ORACLE_DB_CONTAINER-6");
        builder.setCredentialId("test123");
        builder.setAutoSelectRepository(true);
        // builder.setSkipPolling(true);
        project.getBuildersList().add(builder);
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Current Job Status: COMPLETED", build);
        // String x = JenkinsRule.getLog(build);
        // FilePath fp = build.getWorkspace();
        // System.out.println(x);
        DeleteVDB builder1 = new DeleteVDB();
        // builder.set
        builder1.setLoadFromProperties(true);
        builder1.setCredentialId("test123");
        // builder.setAutoSelectRepository(true);
        // builder.setSkipPolling(true);
        project.getBuildersList().add(builder1);
        FreeStyleBuild build1 = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Current Job Status: COMPLETED", build1);

    }

    @Test
    // @Timeout(value = 10, unit = TimeUnit.MINUTES, threadMode = SEPARATE_THREAD)
    public void testProvisionWithoutPolling() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        ProvisionVDBFromSnapshot builder = new ProvisionVDBFromSnapshot();
        builder.setName("test123");
        builder.setSourceDataId("4-ORACLE_DB_CONTAINER-6");
        builder.setCredentialId("test123");
        builder.setAutoSelectRepository(true);
        builder.setSkipPolling(true);
        project.getBuildersList().add(builder);
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("VDB status: UNKNOWN", build);
    }


    @After
    public void destroy() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        DeleteVDB builder1 = new DeleteVDB();
        // builder1.setLoadFromProperties(true);
        builder1.setName("test123");
        builder1.setCredentialId("test123");
        project.getBuildersList().add(builder1);
        FreeStyleBuild build1 = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Current Job Status: COMPLETED", build1);

    }
}
