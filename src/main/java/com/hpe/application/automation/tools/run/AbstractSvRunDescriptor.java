// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.run;

import javax.annotation.Nonnull;

import com.hpe.application.automation.tools.model.SvServerSettingsModel;
import com.hpe.application.automation.tools.settings.SvServerSettingsBuilder;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractSvRunDescriptor extends BuildStepDescriptor<Builder> {
    private final String displayName;

    protected AbstractSvRunDescriptor(String displayName) {
        this.displayName = displayName;
        load();
    }

    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType) {
        return true;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return displayName;
    }

    public SvServerSettingsModel[] getServers() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Cannot get Jenkins instance, probably not running inside Jenkins");
        }
        return jenkins.getDescriptorByType(SvServerSettingsBuilder.DescriptorImpl.class).getServers();
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillServerNameItems() {
        ListBoxModel items = new ListBoxModel();
        for (SvServerSettingsModel server : getServers()) {
            if (StringUtils.isNotBlank(server.getName())) {
                items.add(server.getName(), server.getName());
            }
        }

        return items;
    }
}
