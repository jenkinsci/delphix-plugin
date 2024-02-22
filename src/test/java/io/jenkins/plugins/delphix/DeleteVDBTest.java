package io.jenkins.plugins.delphix;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Before;
import org.junit.Rule;

import org.jvnet.hudson.test.JenkinsRule;


public class DeleteVDBTest {

        @Rule
        public JenkinsRule jenkins = new JenkinsRule();

        @Before
        public void init() throws Exception {
                DelphixGlobalConfiguration globalConfig1 =
                                GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
                globalConfig1.setDctUrl("HOST");


                globalConfig1.save();

                StringCredentialsImpl c = new StringCredentialsImpl(CredentialsScope.USER,
                                "test123", "description", Secret.fromString(
                                                "KEY"));
                CredentialsProvider.lookupStores(jenkins).iterator().next()
                                .addCredentials(Domain.global(), c);
        }
}
