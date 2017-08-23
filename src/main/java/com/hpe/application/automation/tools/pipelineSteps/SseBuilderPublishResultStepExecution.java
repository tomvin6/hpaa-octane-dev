package com.hpe.application.automation.tools.pipelineSteps;

import com.hpe.application.automation.tools.results.RunResultRecorder;
import com.hpe.application.automation.tools.run.RunFromFileBuilder;
import com.hpe.application.automation.tools.run.SseBuilder;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.apache.commons.lang.StringUtils;
import javax.inject.Inject;
import java.util.HashMap;

/**
 * Copyright (c) 2012 Hewlett-Packard Development Company, L.P.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Execution for SseBuildAndPublish
 * Created by Roy Lu on 10/20/2016.
 */
public class SseBuilderPublishResultStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient SseBuildAndPublishStep step;

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient FilePath ws;

    @StepContextParameter
    private transient Run build;

    @StepContextParameter
    private transient Launcher launcher;

    @Override
    protected Void run() throws Exception {
        listener.getLogger().println("Execute HPE tests using HPE ALM Lab Management");

        SseBuilder sseBuilder = step.getSseBuilder();
        RunResultRecorder runResultRecorder = step.getRunResultRecorder();

        String archiveTestResultsMode = runResultRecorder.getResultsPublisherModel().getArchiveTestResultsMode();

        sseBuilder.perform(build, ws, launcher, listener);

        if (StringUtils.isNotBlank(archiveTestResultsMode)) {
            listener.getLogger().println("Publish HPE tests result");

            HashMap<String, String> resultFilename = new HashMap<String, String>(0);
            resultFilename.put(RunFromFileBuilder.class.getName(), sseBuilder.getRunResultsFileName());

            runResultRecorder.pipelinePerform(build, ws, launcher, listener, resultFilename);
        }
        return null;
    }
}
