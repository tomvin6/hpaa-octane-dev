// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import java.net.MalformedURLException;
import java.net.URL;

import com.hp.sv.jsvconfigurator.core.impl.processor.Credentials;
import hudson.util.Secret;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class SvServerSettingsModel {

    private final String name;
    private final String url;
    private final String username;
    private final Secret password;

    @DataBoundConstructor
    public SvServerSettingsModel(String name, String url, String username, Secret password) {
        this.name = StringUtils.trim(name);
        this.url = StringUtils.trim(url);
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public URL getUrlObject() throws MalformedURLException {
        return new URL(url);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password.getPlainText();
    }

    public Credentials getCredentials() {
        if (StringUtils.isBlank(username) || password == null) {
            return null;
        }
        return new Credentials(username, password.getPlainText());
    }
}