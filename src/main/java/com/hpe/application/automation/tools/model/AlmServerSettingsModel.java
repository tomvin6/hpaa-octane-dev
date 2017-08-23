// (c) Copyright 2012 Hewlett-Packard Development Company, L.P. 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class AlmServerSettingsModel {
    
    private final String _almServerName;
    private final String _almServerUrl;
    
    @DataBoundConstructor
    public AlmServerSettingsModel(String almServerName, String almServerUrl) {
        
        _almServerName = almServerName;
        _almServerUrl = almServerUrl;
    }
    
    /**
     * @return the almServerName
     */
    public String getAlmServerName() {
        
        return _almServerName;
    }
    
    /**
     * @return the almServerUrl
     */
    public String getAlmServerUrl() {
        
        return _almServerUrl;
    }
    
    public Properties getProperties() {
        
        Properties prop = new Properties();
        if (!StringUtils.isEmpty(_almServerUrl)) {
            prop.put("almServerUrl", _almServerUrl);
        } else {
            prop.put("almServerUrl", "");
        }
        
        return prop;
    }
}
