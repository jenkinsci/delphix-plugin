package io.jenkins.plugins.delphix;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import hudson.model.Result;
import jenkins.model.GlobalConfiguration;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class GlobalConfigurationTest {

  @Rule
  public JenkinsRule jenkins = new JenkinsRule();

  @Test
  public void GlobalConfigSSLDisable() throws Exception {
    DelphixGlobalConfiguration globalConfig1 =
        GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
    globalConfig1.setDctUrl("https://self-signed.badssl.com");
    globalConfig1.setDisableSsl(true); //disable ssl
    globalConfig1.save();

    StringCredentialsImpl c =
        new StringCredentialsImpl(CredentialsScope.USER, "test123", "description",
            Secret.fromString("api key"));
    CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(), c);

    FreeStyleProject project = jenkins.createFreeStyleProject();
    ProvisionVDBFromSnapshot builder = new ProvisionVDBFromSnapshot();
    builder.setSourceDataId("4-ORACLE_DB_CONTAINER-6");
    builder.setCredentialId("test123");
    builder.setAutoSelectRepository(true);
    project.getBuildersList().add(builder);

    FreeStyleBuild b1 = project.scheduleBuild2(0).get();
    System.out.println(b1.toString());
    jenkins.assertLogContains("<head><title>404 Not Found</title></head>", b1);
    jenkins.assertBuildStatus(Result.FAILURE, b1);
  }


  @Test
  public void GlobalConfigDefault() throws Exception {
    DelphixGlobalConfiguration globalConfig1 =
        GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
    globalConfig1.setDctUrl("https://self-signed.badssl.com");
    globalConfig1.save();

    StringCredentialsImpl c =
        new StringCredentialsImpl(CredentialsScope.USER, "test123", "description",
            Secret.fromString("api key"));
    CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(), c);

    FreeStyleProject project = jenkins.createFreeStyleProject();
    ProvisionVDBFromSnapshot builder = new ProvisionVDBFromSnapshot();
    builder.setSourceDataId("4-ORACLE_DB_CONTAINER-6");
    builder.setCredentialId("test123");
    builder.setAutoSelectRepository(true);
    project.getBuildersList().add(builder);

    FreeStyleBuild b1 = project.scheduleBuild2(0).get();
    System.out.println(b1.toString());
    jenkins.assertLogContains("javax.net.ssl.SSLHandshakeException:", b1);
    jenkins.assertBuildStatus(Result.FAILURE, b1);
  }
}