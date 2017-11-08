// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.hpe.application.automation.tools.model.SvServerSettingsModel;
import com.hp.sv.jsvconfigurator.core.impl.jaxb.ServerInfo;
import com.hp.sv.jsvconfigurator.core.impl.processor.Credentials;
import com.hp.sv.jsvconfigurator.serverclient.ICommandExecutor;
import com.hp.sv.jsvconfigurator.serverclient.impl.CommandExecutorFactory;
import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class SvServerSettingsBuilder extends Builder {

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link SvServerSettingsBuilder}. Used as a singleton. The class is marked as
     * public so that it can be accessed from views.
     * <p>
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt> for the
     * actual HTML fragment for the configuration screen.
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @CopyOnWrite
        private SvServerSettingsModel[] servers;

        public DescriptorImpl() {
            load();
        }

        private static boolean isHttpsSchema(String url) {
            try {
                return "https".equals(new URL(url).getProtocol());
            } catch (MalformedURLException e) {
                return false;
            }
        }

        @Override
        public boolean isApplicable(
                @SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project
            // types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            List<SvServerSettingsModel> list = req.bindJSONToList(SvServerSettingsModel.class, formData.get("srv"));

            validateMandatoryFields(list);

            servers = list.toArray(new SvServerSettingsModel[list.size()]);

            save();

            return super.configure(req, formData);
        }

        private void validateMandatoryFields(List<SvServerSettingsModel> servers) throws FormException {
            for (SvServerSettingsModel server : servers) {
                validateConfiguration(doCheckName(server.getName()), "name");
                validateConfiguration(doCheckUrl(server.getUrl()), "url");
                validateConfiguration(doCheckUsername(server.getUsername(), server.getUrl()), "username");
                validateConfiguration(doCheckPassword(server.getPassword(), server.getUrl()), "password");
            }
        }

        private void validateConfiguration(FormValidation result, String formField) throws FormException {
            if (!result.equals(FormValidation.ok())) {
                throw new FormException("Validation of property in Service Virtualization server configuration failed: " + result.getMessage(), formField);
            }
        }

        public FormValidation doCheckUrl(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("Management Endpoint URL cannot be empty");
            }
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return FormValidation.error("'" + value + "' is not valid URL");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUsername(@QueryParameter String value, @QueryParameter("url") final String url) {
            if (isHttpsSchema(url) && StringUtils.isBlank(value)) {
                return FormValidation.error("Username is required for secured server");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckPassword(@QueryParameter String value, @QueryParameter("url") final String url) {
            if (isHttpsSchema(url) && StringUtils.isBlank(value)) {
                return FormValidation.error("Password is required for secured server");
            }
            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doTestConnection(@QueryParameter("url") final String url, @QueryParameter("username") final String username,
                                               @QueryParameter("password") final String password) {
            try {
                Credentials credentials = (!StringUtils.isBlank(username)) ? new Credentials(username, password) : null;
                ICommandExecutor commandExecutor = new CommandExecutorFactory().createCommandExecutor(new URL(url), credentials);
                ServerInfo serverInfo = commandExecutor.getClient().getServerInfo();
                return FormValidation.ok("Validation passed. Connected to %s server of version: %s", serverInfo.getServerType(), serverInfo.getProductVersion());
            } catch (Exception e) {
                return FormValidation.error("Validation failed: " + e.getMessage());
            }
        }

        public SvServerSettingsModel[] getServers() {
            return servers;
        }

        public void setServers(SvServerSettingsModel[] servers) {
            this.servers = servers;
        }

        public FormValidation doCheckName(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("Name cannot be empty");
            }

            return FormValidation.ok();
        }
    }
}
