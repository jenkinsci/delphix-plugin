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
    private boolean disableSsl;

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

    public boolean getDisableSsl() {
        return disableSsl;
    }

    @DataBoundSetter
    public void setDisableSsl(boolean disableSsl) {
        this.disableSsl = disableSsl;
        save();
    }
}
