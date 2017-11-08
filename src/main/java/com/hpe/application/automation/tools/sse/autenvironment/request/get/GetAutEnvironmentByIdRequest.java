package com.hpe.application.automation.tools.sse.autenvironment.request.get;

import com.hpe.application.automation.tools.sse.autenvironment.request.AUTEnvironmentResources;
import com.hpe.application.automation.tools.sse.sdk.Client;
import com.hpe.application.automation.tools.sse.sdk.request.GeneralGetRequest;

/**
 * Created by barush on 02/11/2014.
 */
public class GetAutEnvironmentByIdRequest extends GeneralGetRequest {
    
    private String autEnvironmentId;
    
    public GetAutEnvironmentByIdRequest(Client client, String autEnvironmentId) {
        
        super(client);
        this.autEnvironmentId = autEnvironmentId;
    }
    
    @Override
    protected String getSuffix() {
        
        return AUTEnvironmentResources.AUT_ENVIRONMENTS;
        
    }
    
    @Override
    protected String getQueryString() {
        
        return String.format("query={id[%s]}", autEnvironmentId);
    }
}
