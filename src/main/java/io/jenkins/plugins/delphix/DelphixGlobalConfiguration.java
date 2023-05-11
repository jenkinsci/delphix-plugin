package io.jenkins.plugins.delphix;

import javax.annotation.CheckForNull;
import org.kohsuke.stapler.StaplerRequest;
import hudson.Extension;
import hudson.model.Descriptor;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalPluginConfiguration;
import net.sf.json.JSONObject;

public class DelphixGlobalConfiguration extends GlobalPluginConfiguration {

    @Extension
    public static final class DescriptorImpl extends Descriptor<GlobalConfiguration> {
        @CheckForNull
        private String dctUrl;

        public DescriptorImpl() {
            load();
            dctUrl = this.getDctUrl();
        }

        @Override
        public String getDisplayName() {
            return "Delphix Plugin Configuration";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            dctUrl = formData.getString("dctUrl");
            save();
            return super.configure(req, formData);
        }

        public String getDctUrl() {
            return dctUrl != null && !dctUrl.isEmpty() ? dctUrl : null;
        }
    }
}
