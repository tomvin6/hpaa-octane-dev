// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import hudson.util.Secret;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Date;

public class OctaneServerSettingsModel {

    private String identity;
    private Long identityFrom;

    private String uiLocation;
    private String username;
    private Secret password;
    private String impersonatedUser;

    // inferred from uiLocation
    private String location;
    private String sharedSpace;


    public OctaneServerSettingsModel() {

    }

    @DataBoundConstructor
    public OctaneServerSettingsModel(String uiLocation, String username, Secret password, String impersonatedUser) {
        this.uiLocation = StringUtils.trim(uiLocation);
        this.username = username;
        this.password = password;
        this.impersonatedUser = impersonatedUser;
    }


    public String getUiLocation() {
        return uiLocation;
    }

    public String getUsername() {
        return username;
    }

    public Secret getPassword() {
        return password;
    }

    public String getImpersonatedUser() {
        return impersonatedUser;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        if (StringUtils.isEmpty(identity)) {
            throw new IllegalArgumentException("Empty identity is not allowed");
        }
        this.identity = identity;
        this.setIdentityFrom(new Date().getTime());
    }

    public Long getIdentityFrom() {
        return identityFrom;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSharedSpace() {
        return sharedSpace;
    }

    public void setSharedSpace(String sharedSpace) {
        this.sharedSpace = sharedSpace;
    }

    public void setIdentityFrom(Long identityFrom) {
        this.identityFrom = identityFrom;
    }
}