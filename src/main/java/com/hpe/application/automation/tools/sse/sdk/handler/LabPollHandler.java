package com.hpe.application.automation.tools.sse.sdk.handler;

import com.hpe.application.automation.tools.sse.common.StringUtils;
import com.hpe.application.automation.tools.sse.common.XPathUtils;
import com.hpe.application.automation.tools.sse.sdk.Client;
import com.hpe.application.automation.tools.sse.sdk.Logger;
import com.hpe.application.automation.tools.sse.sdk.Response;
import com.hpe.application.automation.tools.sse.sdk.request.GetLabRunEntityDataRequest;
import com.hpe.application.automation.tools.sse.sdk.request.PollSSERunRequest;

/***
 * 
 * @author Effi Bar-She'an
 * @author Dani Schreiber
 * 
 */
public class LabPollHandler extends PollHandler {
    
    private EventLogHandler _eventLogHandler;
    
    public LabPollHandler(Client client, String entityId) {
        
        super(client, entityId);
    }
    
    public LabPollHandler(Client client, String entityId, int interval) {
        
        super(client, entityId, interval);
    }
    
    @Override
    protected boolean doPoll(Logger logger) throws InterruptedException {
        
        boolean ret = false;
        
        Response runEntityResponse = getRunEntityData();
        if (isOk(runEntityResponse, logger)) {
            setTimeslotId(runEntityResponse, logger);
            _eventLogHandler = new EventLogHandler(_client, _timeslotId);
            if (!StringUtils.isNullOrEmpty(_timeslotId)) {
                ret = super.doPoll(logger);
            }
        }
        return ret;
        
    }
    
    @Override
    protected Response getResponse() {
        
        return new PollSSERunRequest(_client, _runId).execute();
    }
    
    @Override
    protected void log(Logger logger) {
        
        _eventLogHandler.log(logger);
    }
    
    @Override
    protected boolean isFinished(Response response, Logger logger) {
        
        boolean ret = false;
        try {
            String xml = response.toString();
            String endTime = XPathUtils.getAttributeValue(xml, "end-time");
            if (!StringUtils.isNullOrEmpty(endTime)) {
                String startTime = XPathUtils.getAttributeValue(xml, "start-time");
                String currentRunState = XPathUtils.getAttributeValue(xml, "state");
                logger.log(String.format(
                        "Timeslot %s is %s.\nRun start time: %s, Run end time: %s",
                        _timeslotId,
                        currentRunState,
                        startTime,
                        endTime));
                ret = true;
            }
        } catch (Throwable cause) {
            logger.log(String.format("Failed to parse response: %s", response));
            ret = true;
        }
        
        return ret;
    }
    
    @Override
    protected boolean logRunEntityResults(Response response, Logger logger) {
        
        boolean ret = false;
        try {
            String xml = response.toString();
            String state = XPathUtils.getAttributeValue(xml, "state");
            String completedSuccessfully =
                    XPathUtils.getAttributeValue(xml, "completed-successfully");
            logger.log(String.format(
                    "Run state of %s: %s, Completed successfully: %s",
                    _runId,
                    state,
                    completedSuccessfully));
            ret = true;
            
        } catch (Throwable cause) {
            logger.log(String.format("Failed to parse response: %s", response));
        }
        
        return ret;
    }
    
    private void setTimeslotId(Response runEntityResponse, Logger logger) {
        
        _timeslotId = getTimeslotId(runEntityResponse, logger);
        if (!StringUtils.isNullOrEmpty(_timeslotId)) {
            logger.log(String.format("Timeslot id: %s", _timeslotId));
        }
    }
    
    private Response getRunEntityData() {
        
        return new GetLabRunEntityDataRequest(_client, _runId).execute();
    }
    
    private String getTimeslotId(Response response, Logger logger) {
        
        String ret = StringUtils.EMPTY_STRING;
        try {
            String xml = response.toString();
            ret = XPathUtils.getAttributeValue(xml, "reservation-id");
        } catch (Throwable cause) {
            logger.log(String.format("Failed to parse response for timeslot ID: %s", response));
        }
        
        return ret;
    }
    
    @Override
    protected Response getRunEntityResultsResponse() {
        return new GetLabRunEntityDataRequest(_client, _runId).execute();
    }
}
