// (c) Copyright 2012 Hewlett-Packard Development Company, L.P.
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import com.hpe.application.automation.tools.mc.JobConfigurationProxy;
import hudson.EnvVars;
import hudson.util.Secret;
import hudson.util.VariableResolver;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Holds the data for RunFromFile build type.
 */
public class RunFromFileSystemModel {

    public static final String MOBILE_PROXY_SETTING_PASSWORD_FIELD = "MobileProxySetting_Password";
    public static final String MOBILE_PROXY_SETTING_USER_NAME = "MobileProxySetting_UserName";
    public static final String MOBILE_PROXY_SETTING_AUTHENTICATION = "MobileProxySetting_Authentication";
    public static final String MOBILE_USE_SSL = "MobileUseSSL";

    public final static EnumDescription FAST_RUN_MODE = new EnumDescription("Fast", "Fast");
    public final static EnumDescription NORMAL_RUN_MODE = new EnumDescription("Normal", "Normal");
    public final static List<EnumDescription> fsUftRunModes = Arrays.asList(FAST_RUN_MODE, NORMAL_RUN_MODE);

    private String fsTests;
    private String fsTimeout;
    private String fsUftRunMode;
    private String controllerPollingInterval;
    private String perScenarioTimeOut;
    private String ignoreErrorStrings;
    private String mcServerName;
    private String fsUserName;
    private Secret fsPassword;

    private String fsDeviceId;
    private String fsOs;
    private String fsManufacturerAndModel;
    private String fsTargetLab;
    private String fsAutActions;
    private String fsLaunchAppName;
    private String fsInstrumented;
    private String fsDevicesMetrics;
    private String fsExtraApps;
    private String fsJobId;
    private ProxySettings proxySettings;
    private boolean useSSL;

    /**
     * Instantiates a new Run from file system model.
     *
     * @param fsTests                   the fs tests path
     * @param fsTimeout                 the fs timeout in minutes for tests in seconds
     * @param controllerPollingInterval the controller polling interval in minutes
     * @param perScenarioTimeOut        the per scenario time out in minutes
     * @param ignoreErrorStrings        the ignore error strings
     * @param mcServerName              the mc server name
     * @param fsUserName                the fs user name
     * @param fsPassword                the fs password
     * @param fsDeviceId                the fs device id
     * @param fsTargetLab               the fs target lab
     * @param fsManufacturerAndModel    the fs manufacturer and model
     * @param fsOs                      the fs os
     * @param fsAutActions              the fs aut actions
     * @param fsLaunchAppName           the fs launch app name
     * @param fsDevicesMetrics          the fs devices metrics
     * @param fsInstrumented            the fs instrumented
     * @param fsExtraApps               the fs extra apps
     * @param fsJobId                   the fs job id
     * @param proxySettings             the proxy settings
     * @param useSSL                    the use ssl
     */
    @SuppressWarnings("squid:S00107")
    public RunFromFileSystemModel(String fsTests, String fsTimeout, String fsUftRunMode, String controllerPollingInterval,String perScenarioTimeOut,
                                  String ignoreErrorStrings, String mcServerName, String fsUserName, String fsPassword,
                                  String fsDeviceId, String fsTargetLab, String fsManufacturerAndModel, String fsOs,
                                  String fsAutActions, String fsLaunchAppName, String fsDevicesMetrics, String fsInstrumented,
                                  String fsExtraApps, String fsJobId, ProxySettings proxySettings, boolean useSSL) {

        this.setFsTests(fsTests);

        this.fsTimeout = fsTimeout;
        this.fsUftRunMode = fsUftRunMode;

        this.perScenarioTimeOut = perScenarioTimeOut;
        this.controllerPollingInterval = controllerPollingInterval;
        this.ignoreErrorStrings = ignoreErrorStrings;

        this.mcServerName = mcServerName;
        this.fsUserName = fsUserName;
        this.fsPassword = Secret.fromString(fsPassword);


        this.fsDeviceId = fsDeviceId;
        this.fsOs = fsOs;
        this.fsManufacturerAndModel = fsManufacturerAndModel;
        this.fsTargetLab = fsTargetLab;
        this.fsAutActions = fsAutActions;
        this.fsLaunchAppName = fsLaunchAppName;
        this.fsAutActions = fsAutActions;
        this.fsDevicesMetrics = fsDevicesMetrics;
        this.fsInstrumented = fsInstrumented;
        this.fsExtraApps = fsExtraApps;
        this.fsJobId = fsJobId;
        this.proxySettings = proxySettings;
        this.useSSL = useSSL;

    }


    /**
     * Instantiates a new file system model.
     *
     * @param fsTests the fs tests
     */
    @DataBoundConstructor
    public RunFromFileSystemModel(String fsTests) {
        this.setFsTests(fsTests);

        //Init default vals
        this.fsTimeout = "";
        this.fsUftRunMode = "Fast";
        this.controllerPollingInterval = "30";
        this.perScenarioTimeOut = "10";
        this.ignoreErrorStrings = "";
    }


    /**
     * Sets fs tests.
     *
     * @param fsTests the fs tests
     */
    public void setFsTests(String fsTests) {
        this.fsTests = fsTests.trim();

        if (!this.fsTests.contains("\n")) {
            this.fsTests += "\n";
        }
    }

    /**
     * Sets fs timeout.
     *
     * @param fsTimeout the fs timeout
     */
    public void setFsTimeout(String fsTimeout) {
        this.fsTimeout = fsTimeout;
    }

    /**
     * Sets fs runMode.
     *
     * @param fsUftRunMode the fs runMode
     */
    public void setFsUftRunMode(String fsUftRunMode) {
        this.fsUftRunMode = fsUftRunMode;
    }

    /**
     * Sets mc server name.
     *
     * @param mcServerName the mc server name
     */
    public void setMcServerName(String mcServerName) {
        this.mcServerName = mcServerName;
    }

    /**
     * Sets fs user name.
     *
     * @param fsUserName the fs user name
     */
    public void setFsUserName(String fsUserName) {
        this.fsUserName = fsUserName;
    }

    /**
     * Sets fs password.
     *
     * @param fsPassword the fs password
     */
    public void setFsPassword(String fsPassword) {
        this.fsPassword = Secret.fromString(fsPassword);
    }

    /**
     * Sets fs device id.
     *
     * @param fsDeviceId the fs device id
     */
    public void setFsDeviceId(String fsDeviceId) {
        this.fsDeviceId = fsDeviceId;
    }

    /**
     * Sets fs os.
     *
     * @param fsOs the fs os
     */
    public void setFsOs(String fsOs) {
        this.fsOs = fsOs;
    }

    /**
     * Sets fs manufacturer and model.
     *
     * @param fsManufacturerAndModel the fs manufacturer and model
     */
    public void setFsManufacturerAndModel(String fsManufacturerAndModel) {
        this.fsManufacturerAndModel = fsManufacturerAndModel;
    }

    /**
     * Sets fs target lab.
     *
     * @param fsTargetLab the fs target lab
     */
    public void setFsTargetLab(String fsTargetLab) {
        this.fsTargetLab = fsTargetLab;
    }

    /**
     * Sets fs aut actions.
     *
     * @param fsAutActions the fs aut actions
     */
    public void setFsAutActions(String fsAutActions) {
        this.fsAutActions = fsAutActions;
    }

    /**
     * Sets fs launch app name.
     *
     * @param fsLaunchAppName the fs launch app name
     */
    public void setFsLaunchAppName(String fsLaunchAppName) {
        this.fsLaunchAppName = fsLaunchAppName;
    }

    /**
     * Sets fs instrumented.
     *
     * @param fsInstrumented the fs instrumented
     */
    public void setFsInstrumented(String fsInstrumented) {
        this.fsInstrumented = fsInstrumented;
    }

    /**
     * Sets fs devices metrics.
     *
     * @param fsDevicesMetrics the fs devices metrics
     */
    public void setFsDevicesMetrics(String fsDevicesMetrics) {
        this.fsDevicesMetrics = fsDevicesMetrics;
    }

    /**
     * Sets fs extra apps.
     *
     * @param fsExtraApps the fs extra apps
     */
    public void setFsExtraApps(String fsExtraApps) {
        this.fsExtraApps = fsExtraApps;
    }

    /**
     * Sets fs job id.
     *
     * @param fsJobId the fs job id
     */
    public void setFsJobId(String fsJobId) {
        this.fsJobId = fsJobId;
    }

    /**
     * Sets proxy settings.
     *
     * @param proxySettings the proxy settings
     */
    public void setProxySettings(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    /**
     * Sets use ssl.
     *
     * @param useSSL the use ssl
     */
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    /**
     * Gets fs tests.
     *
     * @return the fs tests
     */
    public String getFsTests() {
        return fsTests;
    }

    /**
     * Gets fs timeout.
     *
     * @return the fs timeout
     */
    public String getFsTimeout() {
        return fsTimeout;
    }

    /**
     * Gets fs runMode.
     *
     * @return the fs runMode
     */
    public String getFsUftRunMode() {
        return fsUftRunMode;
    }

    /**
     * Gets fs runModes
     *
     * @return the fs runModes
     */
    public List<EnumDescription> getFsUftRunModes() { return fsUftRunModes; }

    /**
     * Gets mc server name.
     *
     * @return the mc server name
     */
    public String getMcServerName() {
        return mcServerName;
    }

    /**
     * Gets fs user name.
     *
     * @return the fs user name
     */
    public String getFsUserName() {
        return fsUserName;
    }

    /**
     * Gets fs password.
     *
     * @return the fs password
     */
    public String getFsPassword() {
        //Temp fix till supported in pipeline module in LR
        if(fsPassword == null)
        {
            return null;
        }
        return fsPassword.getPlainText();
    }
    /**
     * Gets fs device id.
     *
     * @return the fs device id
     */
    public String getFsDeviceId() {
        return fsDeviceId;
    }

    /**
     * Gets fs os.
     *
     * @return the fs os
     */
    public String getFsOs() {
        return fsOs;
    }

    /**
     * Gets fs manufacturer and model.
     *
     * @return the fs manufacturer and model
     */
    public String getFsManufacturerAndModel() {
        return fsManufacturerAndModel;
    }

    /**
     * Gets fs target lab.
     *
     * @return the fs target lab
     */
    public String getFsTargetLab() {
        return fsTargetLab;
    }

    /**
     * Gets fs aut actions.
     *
     * @return the fs aut actions
     */
    public String getFsAutActions() {
        return fsAutActions;
    }

    /**
     * Gets fs launch app name.
     *
     * @return the fs launch app name
     */
    public String getFsLaunchAppName() {
        return fsLaunchAppName;
    }

    /**
     * Gets fs instrumented.
     *
     * @return the fs instrumented
     */
    public String getFsInstrumented() {
        return fsInstrumented;
    }

    /**
     * Gets fs devices metrics.
     *
     * @return the fs devices metrics
     */
    public String getFsDevicesMetrics() {
        return fsDevicesMetrics;
    }

    /**
     * Gets fs extra apps.
     *
     * @return the fs extra apps
     */
    public String getFsExtraApps() {
        return fsExtraApps;
    }

    /**
     * Gets fs job id.
     *
     * @return the fs job id
     */
    public String getFsJobId() {
        return fsJobId;
    }

    /**
     * Is use proxy boolean.
     *
     * @return the boolean
     */
    public boolean isUseProxy() {
        return proxySettings != null;
    }

    /**
     * Is use authentication boolean.
     *
     * @return the boolean
     */
    public boolean isUseAuthentication() {
        return proxySettings != null && StringUtils.isNotBlank(proxySettings.getFsProxyUserName());
    }

    /**
     * Gets proxy settings.
     *
     * @return the proxy settings
     */
    public ProxySettings getProxySettings() {
        return proxySettings;
    }

    /**
     * Is use ssl boolean.
     *
     * @return the boolean
     */
    public boolean isUseSSL() {
        return useSSL;
    }


    /**
     * Gets controller polling interval.
     *
     * @return the controllerPollingInterval
     */
    public String getControllerPollingInterval() {
        return controllerPollingInterval;
    }

    /**
     * Sets controller polling interval.
     *
     * @param controllerPollingInterval the controllerPollingInterval to set
     */
    public void setControllerPollingInterval(String controllerPollingInterval) {
        this.controllerPollingInterval = controllerPollingInterval;
    }

    /**
     * Gets ignore error strings.
     *
     * @return the ignoreErrorStrings
     */
    public String getIgnoreErrorStrings() {
        return ignoreErrorStrings;
    }


    /**
     * Sets ignore error strings.
     *
     * @param ignoreErrorStrings the ignoreErrorStrings to set
     */
    public void setIgnoreErrorStrings(String ignoreErrorStrings) {
        this.ignoreErrorStrings = ignoreErrorStrings;
    }


    /**
     * Gets per scenario time out.
     *
     * @return the perScenarioTimeOut
     */
    public String getPerScenarioTimeOut() {
        return perScenarioTimeOut;
    }

    /**
     * Sets per scenario time out.
     *
     * @param perScenarioTimeOut the perScenarioTimeOut to set
     */
    public void setPerScenarioTimeOut(String perScenarioTimeOut) {
        this.perScenarioTimeOut = perScenarioTimeOut;
    }


	/**
	 * Gets properties.
	 *
	 * @param envVars     the env vars
	 * @return the properties
	 */
	@Nullable
	public Properties getProperties(EnvVars envVars,
									VariableResolver<String> varResolver) {
		return createProperties(envVars, varResolver);
	}

	private Properties createProperties(EnvVars envVars,
										VariableResolver<String> varResolver) {

		return createProperties(envVars);
	}

    /**
     * Gets properties.
     *
     * @param envVars     the env vars
     * @return the properties
     */
	@Nullable
	public Properties getProperties(EnvVars envVars) {
        return createProperties(envVars);
    }

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return createProperties(null);
    }

    private Properties createProperties(EnvVars envVars) {
        Properties props = new Properties();

        if (!StringUtils.isEmpty(this.fsTests)) {
            String expandedFsTests = envVars.expand(fsTests);
            String[] testsArr;
            if (isMtbxContent(expandedFsTests)) {
                testsArr = new String[]{expandedFsTests};
            } else {
                testsArr = expandedFsTests.replaceAll("\r", "").split("\n");
            }

            int i = 1;

            for (String test : testsArr) {
                test = test.trim();
                props.put("Test" + i, test);
                i++;
            }
        } else {
            props.put("fsTests", "");
        }


        if (StringUtils.isEmpty(fsTimeout)){
            props.put("fsTimeout", "-1");
        }
        else{
            props.put("fsTimeout", "" + fsTimeout);
        }

        if (StringUtils.isEmpty(fsUftRunMode)){
            props.put("fsUftRunMode", "Fast");
        }
        else{
            props.put("fsUftRunMode", "" + fsUftRunMode);
        }


        if (StringUtils.isEmpty(controllerPollingInterval)){
            props.put("controllerPollingInterval", "30");
        }
        else{
            props.put("controllerPollingInterval", "" + controllerPollingInterval);
        }

        if (StringUtils.isEmpty(perScenarioTimeOut)){
            props.put("PerScenarioTimeOut", "10");
        }
        else{
            props.put("PerScenarioTimeOut", ""+ perScenarioTimeOut);
        }

        if (!StringUtils.isEmpty(ignoreErrorStrings.replaceAll("\\r|\\n", ""))){
            props.put("ignoreErrorStrings", ""+ignoreErrorStrings.replaceAll("\r", ""));
        }

        if (StringUtils.isNotBlank(fsUserName)){
            props.put("MobileUserName", fsUserName);
        }

        if(isUseProxy()){
            props.put("MobileUseProxy", "1");
            props.put("MobileProxyType","2");
            props.put("MobileProxySetting_Address", proxySettings.getFsProxyAddress());
            if(isUseAuthentication()){
                props.put(MOBILE_PROXY_SETTING_AUTHENTICATION,"1");
                props.put(MOBILE_PROXY_SETTING_USER_NAME,proxySettings.getFsProxyUserName());
                props.put(MOBILE_PROXY_SETTING_PASSWORD_FIELD, proxySettings.getFsProxyPassword());
            }else{
                props.put(MOBILE_PROXY_SETTING_AUTHENTICATION,"0");
                props.put(MOBILE_PROXY_SETTING_USER_NAME,"");
                props.put(MOBILE_PROXY_SETTING_PASSWORD_FIELD,"");
            }
        }else{
            props.put("MobileUseProxy", "0");
            props.put("MobileProxyType","0");
            props.put(MOBILE_PROXY_SETTING_AUTHENTICATION,"0");
            props.put("MobileProxySetting_Address", "");
            props.put(MOBILE_PROXY_SETTING_USER_NAME,"");
            props.put(MOBILE_PROXY_SETTING_PASSWORD_FIELD,"");
        }

        if(useSSL){
            props.put(MOBILE_USE_SSL,"1");
        }else{
            props.put(MOBILE_USE_SSL,"0");
        }

        return props;
    }

    public static boolean isMtbxContent(String testContent) {
        return testContent.toLowerCase().contains("<mtbx>");
    }

    /**
     * Get proxy details json object.
     *
     * @param mcUrl         the mc url
     * @param proxyAddress  the proxy address
     * @param proxyUserName the proxy user name
     * @param proxyPassword the proxy password
     * @return the json object
     */
    public JSONObject getJobDetails(String mcUrl, String proxyAddress, String proxyUserName, String proxyPassword){
        if(StringUtils.isBlank(fsUserName) || StringUtils.isBlank(fsPassword.getPlainText())){
            return null;
        }
        return JobConfigurationProxy
                .getInstance().getJobById(mcUrl, fsUserName, fsPassword.getPlainText(), proxyAddress, proxyUserName, proxyPassword, fsJobId);
    }
}
