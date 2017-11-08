// (c) Copyright 2016 Hewlett Packard Enterprise Development LP
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.run;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.ArrayList;

import com.hpe.application.automation.tools.model.SvDeployModel;
import com.hp.sv.jsvconfigurator.core.IDataModel;
import com.hp.sv.jsvconfigurator.core.IPerfModel;
import com.hp.sv.jsvconfigurator.core.IProject;
import com.hp.sv.jsvconfigurator.core.IService;
import com.hp.sv.jsvconfigurator.processor.DeployProcessor;
import com.hp.sv.jsvconfigurator.processor.DeployProcessorInput;
import com.hp.sv.jsvconfigurator.processor.IDeployProcessor;
import com.hp.sv.jsvconfigurator.serverclient.ICommandExecutor;
import com.hp.sv.jsvconfigurator.service.ServiceAmendingServiceImpl;
import com.hp.sv.jsvconfigurator.util.ProjectUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class SvDeployBuilder extends AbstractSvRunBuilder<SvDeployModel> {

    @DataBoundConstructor
    public SvDeployBuilder(String serverName, boolean force, String service, String projectPath, String projectPassword,
                           boolean firstAgentFallback) {
        super(new SvDeployModel(serverName, force, service, projectPath, projectPassword, firstAgentFallback));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public void performImpl(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, Launcher launcher, TaskListener listener) throws Exception {
        PrintStream logger = listener.getLogger();

        IProject project = loadProject(workspace);
        printProjectContent(project, logger);
        deployServiceFromProject(project, logger);
    }

    private Iterable<IService> getServiceList(IProject project) {
        if (model.getService() == null) {
            return project.getServices();
        } else {
            ArrayList<IService> list = new ArrayList<>();
            list.add(ProjectUtils.findProjElem(project.getServices(), model.getService()));
            return list;
        }
    }

    private void deployServiceFromProject(IProject project, PrintStream logger) throws Exception {
        IDeployProcessor processor = new DeployProcessor(null, new ServiceAmendingServiceImpl());
        ICommandExecutor commandExecutor = createCommandExecutor();

        for (IService service : getServiceList(project)) {
            logger.printf("  Deploying service '%s' [%s] %n", service.getName(), service.getId());
            DeployProcessorInput deployInput = new DeployProcessorInput(model.isForce(), false, project, model.getService(), null);
            deployInput.setFirstAgentFailover(model.isFirstAgentFallback());
            processor.process(deployInput, commandExecutor);
        }
    }

    private void printProjectContent(IProject project, PrintStream logger) {
        logger.println("  Project content:");
        for (IService service : project.getServices()) {
            logger.println("    Service: " + service.getName() + " [" + service.getId() + "]");
            for (IDataModel dataModel : service.getDataModels()) {
                logger.println("      DM: " + dataModel.getName() + " [" + dataModel.getId() + "]");
            }
            for (IPerfModel perfModel : service.getPerfModels()) {
                logger.println("      PM: " + perfModel.getName() + " [" + perfModel.getId() + "]");
            }
        }
    }

    @Override
    protected void logConfig(PrintStream logger, String prefix) {
        super.logConfig(logger, prefix);
        logger.println(prefix + "First agent fallback: " + model.isFirstAgentFallback());
    }

    @Extension
    public static final class DescriptorImpl extends AbstractSvRunDescriptor {

        public DescriptorImpl() {
            super("SV: Deploy Virtual Service");
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckProjectPath(@QueryParameter String projectPath) {
            if (StringUtils.isBlank(projectPath)) {
                return FormValidation.error("Project path cannot be empty");
            }
            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckService(@QueryParameter String service) {
            if (StringUtils.isBlank(service)) {
                return FormValidation.ok("All services from project will be deployed if no service is specified");
            }
            return FormValidation.ok();
        }

    }
}
