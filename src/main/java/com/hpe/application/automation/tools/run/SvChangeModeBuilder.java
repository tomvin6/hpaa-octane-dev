// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.run;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hpe.application.automation.tools.model.SvChangeModeModel;
import com.hpe.application.automation.tools.model.SvDataModelSelection;
import com.hpe.application.automation.tools.model.SvPerformanceModelSelection;
import com.hpe.application.automation.tools.model.SvServiceSelectionModel;
import com.hp.sv.jsvconfigurator.core.IProjectElement;
import com.hp.sv.jsvconfigurator.core.IService;
import com.hp.sv.jsvconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.hp.sv.jsvconfigurator.processor.ChmodeProcessor;
import com.hp.sv.jsvconfigurator.processor.ChmodeProcessorInput;
import com.hp.sv.jsvconfigurator.processor.IChmodeProcessor;
import com.hp.sv.jsvconfigurator.serverclient.ICommandExecutor;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Performs change mode of virtual service
 */
public class SvChangeModeBuilder extends AbstractSvRunBuilder<SvChangeModeModel> {
    private static final Logger LOG = Logger.getLogger(SvChangeModeBuilder.class.getName());

    @DataBoundConstructor
    public SvChangeModeBuilder(String serverName, boolean force, ServiceRuntimeConfiguration.RuntimeMode mode,
                               SvDataModelSelection dataModel, SvPerformanceModelSelection performanceModel, SvServiceSelectionModel serviceSelection) {
        super(new SvChangeModeModel(serverName, force, mode, dataModel, performanceModel, serviceSelection));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @SuppressWarnings("unused")
    public SvDataModelSelection getDataModel() {
        return model.getDataModel();
    }

    @SuppressWarnings("unused")
    public SvPerformanceModelSelection getPerformanceModel() {
        return model.getPerformanceModel();
    }

    @Override
    protected void performImpl(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, Launcher launcher, TaskListener listener) throws Exception {
        PrintStream logger = listener.getLogger();

        ICommandExecutor exec = createCommandExecutor();
        for (ServiceInfo service : getServiceList(false, logger, workspace)) {
            changeServiceMode(service, logger, exec);
        }
    }

    private void changeServiceMode(ServiceInfo serviceInfo, PrintStream logger, ICommandExecutor commandExecutor) throws Exception {

        String dataModel = model.getDataModel().getSelectedModelName();
        String performanceModel = model.getPerformanceModel().getSelectedModelName();
        boolean useDefaultDataModel = model.getDataModel().isDefaultSelected();
        boolean useDefaultPerformanceModel = model.getPerformanceModel().isDefaultSelected();
        ServiceRuntimeConfiguration.RuntimeMode targetMode = getTargetMode();

        ChmodeProcessorInput chmodeInput = new ChmodeProcessorInput(model.isForce(), null, serviceInfo.getId(),
                dataModel, performanceModel, targetMode, useDefaultDataModel, useDefaultPerformanceModel);

        logger.printf("    Changing mode of service '%s' [%s] to %s mode%n", serviceInfo.getName(), serviceInfo.getId(), model.getMode());

        IChmodeProcessor processor = new ChmodeProcessor(null);

        try {
            processor.process(chmodeInput, commandExecutor);
        } finally {
            printServiceStatus(logger, serviceInfo, commandExecutor);
        }
    }

    private ServiceRuntimeConfiguration.RuntimeMode getTargetMode() {
        // Set STAND_BY with PM in case of simulation without data model to be in accord with designer & SVM
        if (model.getMode() == ServiceRuntimeConfiguration.RuntimeMode.SIMULATING
                && !model.getPerformanceModel().isNoneSelected()
                && model.getDataModel().isNoneSelected()) {
            return ServiceRuntimeConfiguration.RuntimeMode.STAND_BY;
        }

        return model.getMode();
    }

    private void printServiceStatus(PrintStream logger, ServiceInfo serviceInfo, ICommandExecutor commandExecutor) {
        try {
            IService service = commandExecutor.findService(serviceInfo.getId(), null);
            ServiceRuntimeConfiguration info = commandExecutor.getServiceRuntimeInfo(service);
            ServiceRuntimeConfiguration.RuntimeMode mode = getDisplayRuntimeMode(info);

            logger.printf("    Service '%s' [%s] is in %s mode%n", service.getName(), service.getId(), mode);
            if (mode == ServiceRuntimeConfiguration.RuntimeMode.LEARNING || mode == ServiceRuntimeConfiguration.RuntimeMode.SIMULATING) {
                logger.println("      Data model: " + getModelName(service.getDataModels(), info.getDataModelId()));
                logger.println("      Performance model: " + getModelName(service.getPerfModels(), info.getPerfModelId()));
            }

            if (info.getDeploymentErrorMessage() != null) {
                logger.println("      Error message: " + info.getDeploymentErrorMessage());
            }
        } catch (Exception e) {
            String msg = String.format("Failed to get detail of service '%s' [%s]", serviceInfo.getName(), serviceInfo.getId());
            logger.printf("      %s: %s%n", msg, e.getMessage());
            LOG.log(Level.SEVERE, msg, e);
        }
    }

    private ServiceRuntimeConfiguration.RuntimeMode getDisplayRuntimeMode(ServiceRuntimeConfiguration info) {
        // display SIMULATING in case of STAND_BY mode with PM set (as it is done in designer and SVM)
        return (info.getRuntimeMode() == ServiceRuntimeConfiguration.RuntimeMode.STAND_BY && info.getPerfModelId() != null)
                ? ServiceRuntimeConfiguration.RuntimeMode.SIMULATING
                : info.getRuntimeMode();
    }

    private String getModelName(Collection<? extends IProjectElement> models, String modelId) {
        for (IProjectElement model : models) {
            if (model.getId().equals(modelId)) {
                return String.format("'%s' [%s]", model.getName(), modelId);
            }
        }
        return null;
    }

    @Override
    protected void logConfig(PrintStream logger, String prefix) {
        super.logConfig(logger, prefix);
        logger.println(prefix + "Mode: " + model.getMode().toString());
        logger.println(prefix + "Data model: " + model.getDataModel().toString());
        logger.println(prefix + "Performance model: " + model.getPerformanceModel().toString());
    }

    @Extension
    public static final class DescriptorImpl extends AbstractSvRunDescriptor {

        public DescriptorImpl() {
            super("SV: Change Mode of Virtual Service");
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckDataModel(@QueryParameter String value, @QueryParameter("mode") String mode, @QueryParameter("serviceSelectionKind") String kind) {
            if (StringUtils.isNotBlank(mode)) {
                ServiceRuntimeConfiguration.RuntimeMode runtimeMode = ServiceRuntimeConfiguration.RuntimeMode.valueOf(mode);
                if ((ServiceRuntimeConfiguration.RuntimeMode.SIMULATING == runtimeMode
                        || ServiceRuntimeConfiguration.RuntimeMode.LEARNING == runtimeMode)
                        && StringUtils.isBlank(value)) {
                    return FormValidation.ok("First data model will be used if not specified");
                }
                if (ServiceRuntimeConfiguration.RuntimeMode.STAND_BY == runtimeMode && StringUtils.isNotBlank(value)) {
                    return FormValidation.warning("Data model will not be used in Stand-By mode");
                }
            }
            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillModeItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Stand-By", ServiceRuntimeConfiguration.RuntimeMode.STAND_BY.toString());
            items.add("Simulate", ServiceRuntimeConfiguration.RuntimeMode.SIMULATING.toString());
            items.add("Learn", ServiceRuntimeConfiguration.RuntimeMode.LEARNING.toString());
            return items;
        }
    }
}
