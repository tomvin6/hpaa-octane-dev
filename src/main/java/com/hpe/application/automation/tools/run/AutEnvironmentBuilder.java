package com.hpe.application.automation.tools.run;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hpe.application.automation.tools.model.AUTEnvironmentResolvedModel;
import com.hpe.application.automation.tools.model.AlmServerSettingsModel;
import com.hpe.application.automation.tools.model.AutEnvironmentModel;
import com.hpe.application.automation.tools.settings.AlmServerSettingsBuilder;
import com.hpe.application.automation.tools.sse.autenvironment.AUTEnvironmentBuilderPerformer;
import com.hpe.application.automation.tools.sse.common.StringUtils;
import com.hpe.application.automation.tools.sse.sdk.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

import com.hpe.application.automation.tools.model.AUTEnvironmentModelResolver;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.VariableResolver;

/**
 * Created by barush on 21/10/2014.
 */
public class AutEnvironmentBuilder extends Builder {
    
    private final AutEnvironmentModel autEnvironmentModel;
    
    @DataBoundConstructor
    public AutEnvironmentBuilder(AutEnvironmentModel autEnvironmentModel) {
        
        this.autEnvironmentModel = autEnvironmentModel;
        
    }
    
    public AutEnvironmentModel getAutEnvironmentModel() {
        return autEnvironmentModel;
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        
        return (DescriptorImpl) super.getDescriptor();
    }
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        
        autEnvironmentModel.setAlmServerUrl(getServerUrl(autEnvironmentModel.getAlmServerName()));
        PrintStream logger = listener.getLogger();
        EnvVars envVars = build.getEnvironment(listener);
        execute(build, envVars, autEnvironmentModel, logger);
        
        return true;
    }
    
    public String getServerUrl(String almServerName) {
        
        String ret = "";
        AlmServerSettingsModel[] almServers = getDescriptor().getAlmServers();
        if (almServers != null && almServers.length > 0) {
            for (AlmServerSettingsModel almServer : almServers) {
                if (almServerName.equals(almServer.getAlmServerName())) {
                    ret = almServer.getAlmServerUrl();
                    break;
                }
            }
        }
        
        return ret;
    }
    
    private void execute(
            AbstractBuild<?, ?> build,
            EnvVars envVars,
            AutEnvironmentModel autEnvironmentModel,
            final PrintStream printStreamLogger) throws InterruptedException {
        
        AUTEnvironmentBuilderPerformer performer;
        try {
            Logger logger = new Logger() {
                
                public void log(String message) {
                    printStreamLogger.println(message);
                }
            };
            VariableResolver.ByMap<String> variableResolver =
                    new VariableResolver.ByMap<String>(envVars);
            
            AUTEnvironmentResolvedModel autEnvModel =
                    AUTEnvironmentModelResolver.resolveModel(autEnvironmentModel, variableResolver);
            performer = new AUTEnvironmentBuilderPerformer(autEnvModel, variableResolver, logger);
            performer.start();
            assignOutputValue(build, performer, autEnvModel.getOutputParameter(), logger);
        } catch (InterruptedException e) {
            build.setResult(Result.ABORTED);
            throw e;
        } catch (Throwable cause) {
            build.setResult(Result.FAILURE);
        }
        
    }
    
    private void assignOutputValue(
            AbstractBuild<?, ?> build,
            AUTEnvironmentBuilderPerformer performer,
            String outputParameterName,
            Logger logger) {
        
        if (StringUtils.isNullOrEmpty(outputParameterName)) {
            logger.log("No environment variable was specified for getting the AUT Environment Configuration ID");
            return;
        }
        
        ParametersAction oldParametersAction = build.getAction(ParametersAction.class);
        if (oldParametersAction != null
            && oldParametersAction.getParameter(outputParameterName) != null) {
            
            List<ParameterValue> parametersList =
                    new ArrayList<ParameterValue>(oldParametersAction.getParameters());
            Iterator<ParameterValue> iterator = parametersList.iterator();
            while (iterator.hasNext()) {
                ParameterValue nextValue = iterator.next();
                if (nextValue.getName().equals(outputParameterName)) {
                    if (!(nextValue instanceof StringParameterValue)) {
                        logger.log(String.format(
                                "Can't assign value to %s because it's type is not 'String Parameter'",
                                outputParameterName));
                        return;
                    }
                    parametersList.remove(nextValue);
                    parametersList.add(new StringParameterValue(
                            nextValue.getName(),
                            performer.getAutEnvironmentConfigurationIdToReturn(),
                            nextValue.getDescription()));
                    break;
                }
            }
            
            build.getActions().remove(oldParametersAction);
            build.addAction(new ParametersAction(parametersList));
            
        } else {
            logger.log(String.format(
                    "Can't assign created AUT Environment Configuration ID to: [%s] because there's no such parameter for this build",
                    outputParameterName));
        }
        
    }
    
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        
        public DescriptorImpl() {
            
            load();
        }
        
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }
        
        @Override
        public String getDisplayName() {
            
            return "Execute AUT Environment preparation using HPE ALM Lab Management";
        }
        
        public AlmServerSettingsModel[] getAlmServers() {
            
            return Hudson.getInstance().getDescriptorByType(
                    AlmServerSettingsBuilder.DescriptorImpl.class).getInstallations();
        }
        
    }
}
