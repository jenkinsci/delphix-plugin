// package io.jenkins.plugins.delphix;

// import com.cloudbees.plugins.credentials.Credentials;
// import com.cloudbees.plugins.credentials.CredentialsProvider;
// import com.cloudbees.plugins.credentials.CredentialsScope;
// import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
// import com.cloudbees.plugins.credentials.domains.Domain;
// import com.gargoylesoftware.htmlunit.html.HtmlPage;
// import hudson.ExtensionList;
// import hudson.Functions;
// import hudson.model.Descriptor;
// import hudson.model.FreeStyleBuild;
// import hudson.model.FreeStyleProject;
// import hudson.model.Result;
// import hudson.tasks.BatchFile;
// import hudson.tasks.Builder;
// import hudson.tasks.Shell;
// import hudson.util.Secret;
// import jenkins.model.GlobalConfiguration;
// import jenkins.model.GlobalPluginConfiguration;
// import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
// import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
// import org.jenkinsci.plugins.workflow.job.WorkflowJob;
// import org.jenkinsci.plugins.workflow.job.WorkflowRun;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Rule;
// import org.junit.Test;
// import org.junit.jupiter.api.Timeout;
// import org.jvnet.hudson.test.JenkinsRule;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.concurrent.TimeUnit;
// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.junit.jupiter.api.Timeout.ThreadMode.SEPARATE_THREAD;

// public class DeleteVDBTest {

// @Rule
// public JenkinsRule jenkins = new JenkinsRule();

// @Before
// public void init() throws Exception {
// DelphixGlobalConfiguration globalConfig1 =
// GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
// globalConfig1.setDctUrl("https://dct6.dlpxdc.co/v3");

// globalConfig1.save();

// StringCredentialsImpl c = new StringCredentialsImpl(CredentialsScope.USER,
// "test123",
// "description", Secret.fromString(
// "apk 1.YKhbbGsoA2LUoaIpZ8nxPQsOQbQ5BBWAdB7AhWZISkGjeB6JsyiImpRP0EtKG86y"));
// CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(),
// c);
// }

// // @Test
// // @Timeout(value = 10, unit = TimeUnit.MINUTES, threadMode =
// SEPARATE_THREAD)
// // public void testDeleteBuild() throws Exception {
// // FreeStyleProject project = jenkins.createFreeStyleProject();
// // DeleteVDB builder = new DeleteVDB();
// // // builder.set
// // builder.setVdbId(null);
// // builder.setCredentialId("test123");
// // // builder.setAutoSelectRepository(true);
// // // builder.setSkipPolling(true);
// // project.getBuildersList().add(builder);
// // FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
// // jenkins.assertLogContains("Current Job Status: COMPLETED", build);
// // // System.out.println(build);
// // }

// }
