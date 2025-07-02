
package io.jenkins.plugins.delphix;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class Tags extends AbstractDescribableImpl<Tags> {

    private final String key;
    private final String value;

    @DataBoundConstructor
    public Tags(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Implementation of descriptor
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<Tags> {
        @NonNull
        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
