package io.jenkins.plugins.delphix;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BatchFile;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalPluginConfiguration;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProvisionVDBFromBookmarkTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    public static final String GLOBAL_CREDENTIALS_ID_1 = "global-1";
    public static final String GLOBAL_CREDENTIALS_ID_2 = "global-2";

    private Credentials GLOBAL_CREDENTIAL_1;
    private Credentials GLOBAL_CREDENTIAL_2;

    private FreeStyleProject project;

    @Before
    public void init() throws Exception {
        // ExtensionList<GlobalConfiguration> globalConfig = GlobalPluginConfiguration.all();
        // System.out.println(globalConfig.toString());

        // ExtensionList<GlobalConfiguration> globalConfig1 = GlobalConfiguration.all();
        // System.out.println(globalConfig1.toString());

        // for (GlobalConfiguration gc : globalConfig) {
        // System.out.println(globalConfig.toString());
        // }

        // DelphixGlobalConfiguration globalConfig1 =
        // GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
        // globalConfig1.setDctUrl(GLOBAL_CREDENTIALS_ID_1);

        // Descriptor<GlobalConfiguration> x = globalConfig1.getDescriptor();
        // System.out.println(x.getDisplayName());

        // globalConfig1.save();


        // GLOBAL_CREDENTIAL_1 = createTokenCredential(GLOBAL_CREDENTIALS_ID_1);
        // GLOBAL_CREDENTIAL_2 = createTokenCredential(GLOBAL_CREDENTIALS_ID_2);

        // SystemCredentialsProvider.getInstance().setDomainCredentialsMap(Collections.singletonMap(
        // Domain.global(), Arrays.asList(GLOBAL_CREDENTIAL_1, GLOBAL_CREDENTIAL_2)));

        // this.project = jenkins.createFreeStyleProject("test");

        // setupStubs();
    }


    @Test
    public void testConfigRoundtrip() throws Exception {
        // FreeStyleProject project = jenkins.createFreeStyleProject();
        // project.getBuildersList().add(new HelloWorldBuilder(name));
        // project = jenkins.configRoundtrip(project);
        // jenkins.assertEqualDataBoundBeans(new HelloWorldBuilder(name),
        // project.getBuildersList().get(0));
    }

    @Test
    public void testConfigElements() throws Exception {
        // HtmlPage page = jenkins.createWebClient().goTo("configure");
        // String pageText = page.asNormalizedText();
        // Assert.assertTrue("Missing: BrowserStack Global Config",
        // pageText.contains("BrowserStack"));
    }
}
