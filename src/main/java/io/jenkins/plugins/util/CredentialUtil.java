package io.jenkins.plugins.util;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.google.common.collect.Lists;
import hudson.model.Item;
import hudson.model.Run;
import hudson.util.ListBoxModel;
import hudson.util.Secret;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

public class CredentialUtil {

    private CredentialUtil() {}

    public static List<StandardCredentials> getAllSystemCredentials(@Nullable final Item item) {
        List<StandardCredentials> credentials = Lists.newArrayList();
        credentials.addAll(getStandardCredentials(item));
        return Collections.unmodifiableList(credentials);
    }

    public static ListBoxModel getAllCredentialsListBoxModel(@Nullable final Item item,
            final String credentialId) {
        return getCredentialsListBoxModel(credentialId, getAllSystemCredentials(item));
    }

    private static ListBoxModel getCredentialsListBoxModel(final String credentialId,
            final List<StandardCredentials> credentials) {
        final StandardListBoxModel result = new StandardListBoxModel();

        result.includeEmptyValue();
        for (StandardCredentials credential : credentials) {
            result.with(credential);
        }

        return result.includeCurrentValue(credentialId);
    }

    private static List<StringCredentials> getStandardCredentials(@Nullable Item item) {

        List<StringCredentials> credList = CredentialsProvider
                .lookupCredentials(StringCredentials.class, item, null, Collections.emptyList());

        return credList;
    }

    public static String getApiKey(String credentialsId, Run<?, ?> run) {
        StringCredentials credentials = CredentialsProvider.findCredentialById(credentialsId,
                StringCredentials.class, run, Collections.emptyList());

        if (credentials == null) {
            return null;
        }
        String webAccessToken = Secret.toString(credentials.getSecret());
        return webAccessToken;
    }

}
