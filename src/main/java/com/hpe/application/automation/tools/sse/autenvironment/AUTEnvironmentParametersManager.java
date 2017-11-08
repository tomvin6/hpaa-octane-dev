package com.hpe.application.automation.tools.sse.autenvironment;

import com.hpe.application.automation.tools.common.SSEException;
import com.hpe.application.automation.tools.model.AutEnvironmentParameterModel;
import com.hpe.application.automation.tools.sse.autenvironment.request.get.GetAutEnvFoldersByIdRequest;
import com.hpe.application.automation.tools.sse.autenvironment.request.get.GetParametersByAutEnvConfIdRequest;
import com.hpe.application.automation.tools.sse.autenvironment.request.put.PutAutEnvironmentParametersBulkRequest;
import com.hpe.application.automation.tools.sse.common.JsonHandler;
import com.hpe.application.automation.tools.sse.common.StringUtils;
import com.hpe.application.automation.tools.sse.common.XPathUtils;
import com.hpe.application.automation.tools.sse.sdk.Client;
import com.hpe.application.automation.tools.sse.sdk.Logger;
import com.hpe.application.automation.tools.sse.sdk.Response;
import hudson.util.VariableResolver;

import java.util.*;

/**
 * Created by barush on 29/10/2014.
 */
public class AUTEnvironmentParametersManager {
    
    public final static String PARAMETER_PATH_DELIMITER = "/";
    
    private Logger logger;
    private Client client;
    private List<AutEnvironmentParameterModel> parametersToAssign;
    private String parametersRootFolderId;
    private String autEnvironmentConfigurationId;
    private Map<String, AUTEnvironmnentParameter> parameters;
    
    private VariableResolver<String> buildVariableResolver;
    private String pathToJsonFile;
    
    public AUTEnvironmentParametersManager(
            Client client,
            List<AutEnvironmentParameterModel> parametersToAssign,
            String parametersRootFolderId,
            String autEnvironmentConfigurationId,
            VariableResolver<String> buildVariableResolver,
            String pathToJsonFile,
            Logger logger) {
        
        this.logger = logger;
        this.client = client;
        this.parametersToAssign = parametersToAssign;
        this.parametersRootFolderId = parametersRootFolderId;
        this.autEnvironmentConfigurationId = autEnvironmentConfigurationId;
        this.buildVariableResolver = buildVariableResolver;
        this.pathToJsonFile = pathToJsonFile;
    }
    
    public Collection<AUTEnvironmnentParameter> getParametersToUpdate() {
        
        parameters = getAllParametersByAutEnvConfId();
        Map<String, AUTEnvironmentFolder> parametersFolders = getAllRelevantParametersFolders();
        
        for (AUTEnvironmnentParameter parameter : parameters.values()) {
            parameter.setFullPath(parametersFolders.get(parameter.getParentId()).getPath()
                                  + PARAMETER_PATH_DELIMITER
                                  + parameter.getName());
            
        }
        
        resolveValuesOfParameters();
        return getResolvedParametersWithAssignedValues();
    }
    
    public void updateParametersValues(Collection<AUTEnvironmnentParameter> parametersToUpdate) {
        
        Response response =
                new PutAutEnvironmentParametersBulkRequest(client, parametersToUpdate).execute();
        if (!response.isOk()) {
            throw new SSEException(
                    String.format(
                            "Failed to update the parameters of AUT Environment Configuration with ID: [%s]",
                            autEnvironmentConfigurationId),
                    response.getFailure());
        }
        logger.log("Submitted all parameters to ALM");
    }
    
    private Map<String, AUTEnvironmnentParameter> getAllParametersByAutEnvConfId() {
        
        Map<String, AUTEnvironmnentParameter> parametersMap =
                new HashMap<String, AUTEnvironmnentParameter>();
        Response response =
                new GetParametersByAutEnvConfIdRequest(client, autEnvironmentConfigurationId).execute();
        if (!response.isOk()) {
            throw new SSEException(
                    String.format(
                            "Failed to retrieve the parameters of AUT Environment Configuration with ID: [%s]",
                            autEnvironmentConfigurationId),
                    response.getFailure());
        }
        
        List<Map<String, String>> parameters = XPathUtils.toEntities(response.toString());
        
        for (Map<String, String> parameter : parameters) {
            
            String id = parameter.get(AUTEnvironmnentParameter.ALM_PARAMETER_ID_FIELD);
            AUTEnvironmnentParameter param =
                    new AUTEnvironmnentParameter(
                            id,
                            parameter.get(AUTEnvironmnentParameter.ALM_PARAMETER_PARENT_ID_FIELD),
                            parameter.get(AUTEnvironmnentParameter.ALM_PARAMETER_NAME_FIELD));
            parametersMap.put(id, param);
            
        }
        
        return parametersMap;
        
    }
    
    private Map<String, AUTEnvironmentFolder> getAllRelevantParametersFolders() {
        
        Map<String, AUTEnvironmentFolder> parametersFolders =
                new HashMap<String, AUTEnvironmentFolder>();
        StringBuilder foldersToGet = new StringBuilder(parametersRootFolderId);
        
        for (AUTEnvironmnentParameter parameter : parameters.values()) {
            foldersToGet.append("%20OR%20" + parameter.getParentId());
        }
        
        Response response =
                new GetAutEnvFoldersByIdRequest(client, foldersToGet.toString()).execute();
        if (!response.isOk()) {
            throw new SSEException(
                    String.format(
                            "Failed to retrieve parameters folders of AUT Environment Configuration with ID: [%s]",
                            autEnvironmentConfigurationId),
                    response.getFailure());
        }
        
        List<Map<String, String>> folders = XPathUtils.toEntities(response.toString());
        
        for (Map<String, String> folder : folders) {
            
            String folderId = folder.get(AUTEnvironmentFolder.ALM_PARAMETER_FOLDER_ID_FIELD);
            if (!parametersFolders.containsKey(folderId)) {
                AUTEnvironmentFolder autEnvironmentFolder =
                        new AUTEnvironmentFolder(
                                folderId,
                                folder.get(AUTEnvironmentFolder.ALM_PARAMETER_FOLDER_PARENT_ID_FIELD),
                                folder.get(AUTEnvironmentFolder.ALM_PARAMETER_FOLDER_NAME_FIELD));
                parametersFolders.put(folderId, autEnvironmentFolder);
                
            }
        }
        
        for (AUTEnvironmentFolder folder : parametersFolders.values()) {
            calculatePaths(folder, parametersFolders);
        }
        return parametersFolders;
    }
    
    private String calculatePaths(
            AUTEnvironmentFolder folder,
            Map<String, AUTEnvironmentFolder> parametersFolders) {
        
        String calculatedPath;
        if (folder.getId().equals(parametersRootFolderId)) {
            calculatedPath = folder.getName();
        } else {
            calculatedPath =
                    StringUtils.isNullOrEmpty(folder.getPath())
                            ? calculatePaths(
                                    parametersFolders.get(folder.getParentId()),
                                    parametersFolders)
                              + PARAMETER_PATH_DELIMITER
                              + folder.getName() : folder.getPath();
        }
        
        folder.setPath(calculatedPath);
        return calculatedPath;
    }
    
    private void resolveValuesOfParameters() {
        
        boolean shouldLoadJsonObject = true;
        Object jsonObject = null;
        JsonHandler jsonHandler = new JsonHandler(logger);
        
        for (AutEnvironmentParameterModel parameter : parametersToAssign) {
            String resolvedValue = "";
            switch (AutEnvironmentParameterModel.AutEnvironmentParameterType.get(parameter.getParamType())) {
            
                case ENVIRONMENT:
                    resolvedValue = buildVariableResolver.resolve(parameter.getValue());
                    break;
                case EXTERNAL:
                    if (shouldLoadJsonObject) {
                        jsonObject = jsonHandler.load(pathToJsonFile);
                        shouldLoadJsonObject = false;
                    }
                    resolvedValue =
                            jsonHandler.getValueFromJsonAsString(
                                    jsonObject,
                                    parameter.getValue(),
                                    parameter.isShouldGetOnlyFirstValueFromJson());
                    break;
                case USER_DEFINED:
                    resolvedValue = parameter.getValue();
                    break;
                case UNDEFINED:
                    resolvedValue = "";
                    break;
            }
            
            parameter.setResolvedValue(resolvedValue);
        }
        
    }
    
    private Collection<AUTEnvironmnentParameter> getResolvedParametersWithAssignedValues() {
        
        Collection<AUTEnvironmnentParameter> valuesToReturn =
                new ArrayList<AUTEnvironmnentParameter>();
        for (AutEnvironmentParameterModel parameterByModel : parametersToAssign) {
            String parameterPathByModel = parameterByModel.getName();
            for (AUTEnvironmnentParameter parameter : parameters.values()) {
                if (parameterPathByModel.equalsIgnoreCase(parameter.getFullPath())) {
                    String resolvedValue = parameterByModel.getResolvedValue();
                    parameter.setValue(resolvedValue);
                    logger.log(String.format(
                            "Parameter: [%s] of type: [%s] will get the value: [%s] ",
                            parameter.getFullPath(),
                            parameterByModel.getParamType(),
                            resolvedValue));
                    valuesToReturn.add(parameter);
                    break;
                }
            }
            
        }
        logger.log(parametersToAssign.size() > 0
                ? "Finished assignment of values for all parameters"
                : "There was no parameters to assign");
        
        return valuesToReturn;
    }
}
