// package io.jenkins.plugins.delphix;
//
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
//
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.concurrent.TimeUnit;
// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.junit.jupiter.api.Timeout.ThreadMode.SEPARATE_THREAD;
//
// public class ProvisionVDBFromBookmarkTest {
//
// @Rule
// public JenkinsRule jenkins = new JenkinsRule();
//
// @Before
// public void init() throws Exception {
// DelphixGlobalConfiguration globalConfig1 =
// GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
// globalConfig1.setDctUrl("https://dct6.dlpxdc.co/v3");
//
//
// globalConfig1.save();
//
// StringCredentialsImpl c = new StringCredentialsImpl(CredentialsScope.USER, "test123",
// "description", Secret.fromString(
// "apk 1.YKhbbGsoA2LUoaIpZ8nxPQsOQbQ5BBWAdB7AhWZISkGjeB6JsyiImpRP0EtKG86y"));
// CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(),
// c);
// }
//
//
////  @Test
////  public void testConfigRoundtrip() throws Exception {
////  FreeStyleProject project = jenkins.createFreeStyleProject();
////
////  ProvisionVDBFromBookmark x = new ProvisionVDBFromBookmark("GLOBAL_CREDENTIALS_ID_1");
////  x.setCredentialId("test123");
////  project.getBuildersList().add(x);
////  project = jenkins.configRoundtrip(project);
////  FreeStyleBuild build =
////  jenkins.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0).get());
////  build.ge
////  jenkins.assertEqualDataBoundBeans(x, project.getBuildersList().get(0));
////  }
//
// // @Test
// // public void publishArtifactSuccessTest() throws Exception {
//
//
// // FreeStyleProject project = jenkins.createFreeStyleProject();
// // ProvisionVDBFromBookmark step = new ProvisionVDBFromBookmark("GLOBAL_CREDENTIALS_ID_1");
// // step.setCredentialId("test123");
// // project.getBuildersList().add(step);
// // FreeStyleBuild build = project.scheduleBuild2(0).get();
// // System.out.println(build.getDisplayName() + " completed");
// // // TODO: change this to use HtmlUnit
// // // String s = FileUtils.readFileToString(build.getLogFile());
// // // assertThat(s, containsString("Finished: SUCCESS"));
// // }
//
//  @Test
//  public void testBuilda() throws Exception {
//  FreeStyleProject project = jenkins.createFreeStyleProject();
//  ProvisionVDBFromBookmark builder = new ProvisionVDBFromBookmark("GLOBAL_CREDENTIALS_ID_1");
//  builder.setCredentialId("test123");
//  project.getBuildersList().add(builder);
//  FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
//  jenkins.assertLogContains("web test run " + "\n", build);
//  }
//
// // @Test
// // @Timeout(value = 10, unit = TimeUnit.MINUTES, threadMode = SEPARATE_THREAD)
// // // @Timeout(value = 10, unit = TimeUnit.MINUTES)
// // public void testBuild1() throws Exception {
// // FreeStyleProject project = jenkins.createFreeStyleProject();
// // ProvisionVDBFromBookmark builder = new ProvisionVDBFromBookmark("bkm123");
// // builder.setCredentialId("test123");
// // builder.setAutoSelectRepository(true);
// // // builder.setSkipPolling(true);
// // project.getBuildersList().add(builder);
// // FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
// // jenkins.assertLogContains("Current Job Status: COMPLETED", build);
// // // System.out.println(build);
// // }
//
// // @Test
// // public void testConfigElements() throws Exception {
// // // HtmlPage page = jenkins.createWebClient().goTo("configure");
// // // String pageText = page.asNormalizedText();
// // // Assert.assertTrue("Missing: BrowserStack Global Config",
// // // pageText.contains("BrowserStack"));
// // }
//
//
////  @Test
////  public void testBuild() throws Exception {
////  FreeStyleProject project = jr.createFreeStyleProject();
////  ProvarAutomation builder = new ProvarAutomation(provarAutomationName, buildFile, testPlan,
////  testFolder, environment, browser, secretsPassword, salesforceMetadataCacheSetting,
////  resultsPathSetting, projectName);
////  project.getBuildersList().add(builder);
////  FreeStyleBuild build =
////  jr.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(quietPeriod).get());
////  jr.assertLogContains("Running the build file: " + buildFile, build);
////  jr.assertLogContains("Executing test plan: " + testPlan, build);
////  jr.assertLogContains("Executing test folder: " + testFolder, build);
////  jr.assertLogContains("Target browser: " + browser, build);
////  jr.assertLogContains("Target environment: " + environment, build);
////  jr.assertLogContains("Salesforce Metadata Cache Setting: " + salesforceMetadataCacheSetting,
////  build);
////  jr.assertLogContains("Results Path Setting: " + resultsPathSetting, build);
////  jr.assertLogContains("Project Folder: " + projectName, build);
////  jr.assertLogContains("Project is encrypted! Thank you for being secure.", build);
////  }
//
// // @Test
// // public void testScriptedPipeline() throws Exception {
// // String agentLabel = "my-agent";
// // jr.createOnlineSlave(Label.get(agentLabel));
// // WorkflowJob job = jr.createProject(WorkflowJob.class, "test-scripted-pipeline");
// // String pipelineScript = "node {\n" + " provarAutomation provarAutomationName: '"
// // + provarAutomationName + "',\n" + " buildFile: '" + buildFile + "',\n"
// // + " testPlan: '" + testPlan + "',\n" + " testFolder: '" + testFolder + "',\n"
// // + " environment: '" + environment + "',\n" + " browser: '" + browser + "',\n"
// // + " secretsPassword: '" + secretsPassword + "',\n"
// // + " salesforceMetadataCacheSetting: '" + salesforceMetadataCacheSetting + "',\n"
// // + " resultsPathSetting: '" + resultsPathSetting + "',\n" + " projectName: '"
// // + projectName + "'\n" + "}";
// // job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
//
// // WorkflowRun completedBuild = job.scheduleBuild2(quietPeriod).get();
// // jr.assertBuildStatusSuccess(completedBuild);
// // jr.assertLogContains("Start of Pipeline", completedBuild);
// // }
// }
