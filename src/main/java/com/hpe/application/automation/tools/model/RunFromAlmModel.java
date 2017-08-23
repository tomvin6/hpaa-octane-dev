// (c) Copyright 2012 Hewlett-Packard Development Company, L.P. 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import hudson.EnvVars;
import hudson.Util;
import hudson.util.VariableResolver;
import java.util.Arrays;
import java.util.List;
import hudson.util.Secret;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class RunFromAlmModel {

	public final static EnumDescription runModeLocal = new EnumDescription(
			"RUN_LOCAL", "Run locally");
	public final static EnumDescription runModePlannedHost = new EnumDescription(
			"RUN_PLANNED_HOST", "Run on planned host");
	public final static EnumDescription runModeRemote = new EnumDescription(
			"RUN_REMOTE", "Run remotely");
	public final static List<EnumDescription> runModes = Arrays.asList(
			runModeLocal, runModePlannedHost, runModeRemote);
	public final static int DEFAULT_TIMEOUT = 36000; // 10 hrs
	public final static String ALM_PASSWORD_KEY = "almPassword";

	private String almServerName;
	private String almUserName;
	private Secret almPassword;
	private String almDomain;
	private String almProject;
	private String almTestSets;
	private String almRunResultsMode;
	private String almTimeout;
	private String almRunMode;
	private String almRunHost;

	@DataBoundConstructor
	public RunFromAlmModel(String almServerName, String almUserName,
			String almPassword, String almDomain, String almProject,
			String almTestSets, String almRunResultsMode, String almTimeout,
			String almRunMode, String almRunHost) {

		this.almServerName = almServerName;
		this.almUserName = almUserName;
		this.almPassword = Secret.fromString(almPassword);
		this.almDomain = almDomain;
		this.almProject = almProject;
		this.almTestSets = almTestSets;

		if (!this.almTestSets.contains("\n")) {
			this.almTestSets += "\n";
		}

		this.almRunResultsMode = almRunResultsMode;
		
		this.almTimeout = almTimeout;
		this.almRunMode = almRunMode;

		if (this.almRunMode.equals(runModeRemote.getValue())) {
			this.almRunHost = almRunHost;
		} else {
			this.almRunHost = "";
		}

		if (almRunHost == null) {
			this.almRunHost = "";
		}
	}

	public String getAlmUserName() {
		return almUserName;
	}

	public String getAlmDomain() {
		return almDomain;
	}

	public String getAlmPassword() {
		return almPassword.getPlainText();
	}

	public String getAlmProject() {
		return almProject;
	}

	public String getAlmTestSets() {
		return almTestSets;
	}

	public String getAlmRunResultsMode() {
		return almRunResultsMode;
	}

	public String getAlmTimeout() {
		return almTimeout;
	}

	public String getAlmRunHost() {
		return almRunHost;
	}

	public String getAlmRunMode() {
		return almRunMode;
	}

	public String getAlmServerName() {
		return almServerName;
	}

	public Properties getProperties(EnvVars envVars,
			VariableResolver<String> varResolver) {
		return CreateProperties(envVars, varResolver);
	}

	public Properties getProperties() {
		return CreateProperties(null, null);
	}

	private Properties CreateProperties(EnvVars envVars,
			VariableResolver<String> varResolver) {
		Properties props = new Properties();

		if (envVars == null) {
			props.put("almUserName", almUserName);
			props.put(ALM_PASSWORD_KEY, almPassword);
			props.put("almDomain", almDomain);
			props.put("almProject", almProject);
		} else {
			props.put("almUserName",
					Util.replaceMacro(envVars.expand(almUserName), varResolver));
			props.put(ALM_PASSWORD_KEY, almPassword);
			props.put("almDomain",
					Util.replaceMacro(envVars.expand(almDomain), varResolver));
			props.put("almProject",
					Util.replaceMacro(envVars.expand(almProject), varResolver));
		}

		if (!StringUtils.isEmpty(this.almTestSets)) {

			String[] testSetsArr = this.almTestSets.replaceAll("\r", "").split(
					"\n");

			int i = 1;

			for (String testSet : testSetsArr) {
				if (!StringUtils.isBlank(testSet)) {
					props.put("TestSet" + i,
						Util.replaceMacro(envVars.expand(testSet), varResolver));
					i++;
				}
			}
		} else {
			props.put("almTestSets", "");
		}

		if (StringUtils.isEmpty(almTimeout)) {
			props.put("almTimeout", "-1");
		} else {
			props.put("almTimeout", almTimeout);
		}

		props.put("almRunMode", almRunMode);
		props.put("almRunHost", almRunHost);

		return props;
	}
}
