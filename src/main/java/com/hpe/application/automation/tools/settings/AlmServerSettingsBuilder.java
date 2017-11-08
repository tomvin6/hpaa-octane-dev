// (c) Copyright 2012 Hewlett-Packard Development Company, L.P. 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.settings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import hudson.XmlFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.hpe.application.automation.tools.model.AlmServerSettingsModel;

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link AlmServerSettingsBuilder} is created. The created instance is persisted to the project
 * configuration XML by using XStream, so this allows you to use instance fields (like {@link #name}
 * ) to remember the configuration.
 * 
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)} method
 * will be invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class AlmServerSettingsBuilder extends Builder {

    private static final Logger logger = LogManager.getLogger(AlmServerSettingsBuilder.class);
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    
    /**
     * Descriptor for {@link AlmServerSettingsBuilder}. Used as a singleton. The class is marked as
     * public so that it can be accessed from views.
     * 
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt> for the
     * actual HTML fragment for the configuration screen.
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        
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
        
        public DescriptorImpl() {
            load();
        }

        @Override
        protected XmlFile getConfigFile() {
            XmlFile xmlFile = super.getConfigFile();
            //Between 5.1 to 5.2 - migration hp->hpe was done.
            //Old configuration file 'com.hp.application.automation.tools.settings.AlmServerSettingsBuilder.xml'
            //is replaced by new one 'com.hpe.application.automation.tools.settings.AlmServerSettingsBuilder.xml'.
            //As well, inside the configuration, there were replaces of hp->hpe
            //if xmlFile is not exist, we will check if configuration file name exist in format of 5.1 version
            //if so, we will copy old configuration to new one with replacements of hp->hpe
            if (!xmlFile.exists()) {
                //try to get from old path
                File oldConfigurationFile = new File(xmlFile.getFile().getPath().replace("hpe", "hp"));
                if (oldConfigurationFile.exists()) {
                    try {
                        String configuration = FileUtils.readFileToString(oldConfigurationFile);
                        String newConfiguration = StringUtils.replace(configuration, ".hp.", ".hpe.");
                        FileUtils.writeStringToFile(xmlFile.getFile(), newConfiguration);
                        xmlFile = super.getConfigFile();
                    } catch (IOException e) {
                        logger.error("failed to copy ALM Server Plugin configuration 5.1 to new 5.2 format : " + e.getMessage());
                    }
                }
            }

            return xmlFile;
        }
        
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            // (easier when there are many fields; need set* methods for this,
            // like setUseFrench)
            // req.bindParameters(this, "locks.");
            
            setInstallations(req.bindParametersToList(AlmServerSettingsModel.class, "alm.").toArray(
                    new AlmServerSettingsModel[0]));
            
            save();
            
            return super.configure(req, formData);
        }
        
        public FormValidation doCheckAlmServerUrl(@QueryParameter String value) {
            return checkQcServerURL(value, false);
        }
        
        @CopyOnWrite
        private AlmServerSettingsModel[] installations = new AlmServerSettingsModel[0];
        
        public AlmServerSettingsModel[] getInstallations() {
            return installations;
        }
        
        public void setInstallations(AlmServerSettingsModel... installations) {
            this.installations = installations;
        }
        
        public FormValidation doCheckAlmServerName(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("ALM server name cannot be empty");
            }
            
            return FormValidation.ok();
        }
        
        private FormValidation checkQcServerURL(String value, Boolean acceptEmpty) {
            String url;
            // Path to the page to check if the server is alive
            String page = "servlet/tdservlet/TDAPI_GeneralWebTreatment";
            
            // Do will allow empty value?
            if (StringUtils.isBlank(value)) {
                if (!acceptEmpty) {
                    return FormValidation.error("ALM server must be defined");
                } else {
                    return FormValidation.ok();
                }
            }

            // Does the URL ends with a "/" ? if not, add it
            if (value.lastIndexOf("/") == value.length() - 1) {
                url = value + page;
            } else {
                url = value + "/" + page;
            }
            
            // Open the connection and perform a HEAD request
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                // Check the response code
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return FormValidation.error(connection.getResponseMessage());
                }
            } catch (MalformedURLException ex) {
                // This is not a valid URL
                return FormValidation.error("ALM server URL is malformed.");
            } catch (IOException ex) {
                // Cant open connection to the server
                return FormValidation.error("Error opening a connection to the ALM server");
            }
            
            return FormValidation.ok();
        }

        public Boolean hasAlmServers() {
            return installations.length > 0;
        }
    }
}
