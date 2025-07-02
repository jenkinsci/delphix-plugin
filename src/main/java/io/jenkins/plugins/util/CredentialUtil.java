package io.jenkins.plugins.util;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.Item;
import hudson.model.Run;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.Secret;

import java.util.Collections;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

public class CredentialUtil {

    private CredentialUtil() {}

    public static ListBoxModel getAllCredentialsListBoxModel(@Nullable final Item item,
            final String credentialId) {
        StandardListBoxModel result = new StandardListBoxModel();
        if (item == null) {
            return result;
        }
        else {
            if (!item.hasPermission(Item.EXTENDED_READ)
                    && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                return result.includeCurrentValue(credentialId);
            }
        }
        return result.includeEmptyValue()
                .includeMatchingAs(ACL.SYSTEM2, item, StringCredentials.class,
                        Collections.emptyList(), CredentialsMatchers.always())
                .includeCurrentValue(credentialId);
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
