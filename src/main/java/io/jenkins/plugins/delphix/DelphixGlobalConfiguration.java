package io.jenkins.plugins.delphix;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;


@Extension
public class DelphixGlobalConfiguration extends GlobalConfiguration {

    public static DelphixGlobalConfiguration get() {
        return GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
    }

    private String dctUrl;
    private boolean sslCheck;

    public DelphixGlobalConfiguration() {
        load();
    }

    public String getDctUrl() {
        return dctUrl;
    }

    @DataBoundSetter
    public void setDctUrl(String dctUrl) {
        this.dctUrl = dctUrl;
        save();
    }

    public boolean getSslCheck() {
        return sslCheck;
    }

    @DataBoundSetter
    public void setSslCheck(boolean sslCertificate) {
        this.sslCheck = sslCertificate;
        save();
    }
}
