// (c) Copyright 2012 Hewlett-Packard Development Company, L.P. 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

@SuppressWarnings("squid:S1160")
public class AlmToolsUtils {


	private AlmToolsUtils() {
	}

	public static void runOnBuildEnv(
            Run<?, ?> build,
            Launcher launcher,
            TaskListener listener,
            FilePath file,
            String paramFileName) throws IOException, InterruptedException {
        
        ArgumentListBuilder args = new ArgumentListBuilder();
        PrintStream out = listener.getLogger();
        
        // Use script to run the cmdLine and get the console output
        args.add(file);
        args.add("-paramfile");
        args.add(paramFileName);
        
        // Run the script on node
        // Execution result should be 0
        int returnCode = launcher.launch().cmds(args).stdout(out).pwd(file.getParent()).join();
        
        if (returnCode != 0) {
            if (returnCode == -1) {
                build.setResult(Result.FAILURE);
            } else if (returnCode == -2) {
                build.setResult(Result.UNSTABLE);
            } else if (returnCode == -3) {
                build.setResult(Result.ABORTED);
            }
        }
    }

    public static void runHpToolsAborterOnBuildEnv(
            AbstractBuild<?, ?> build,
            Launcher launcher,
            BuildListener listener,
            String paramFileName) throws IOException, InterruptedException {

            runHpToolsAborterOnBuildEnv(build, launcher, listener, paramFileName, build.getWorkspace());
    }

	@SuppressWarnings("squid:S2259")
	public static void runHpToolsAborterOnBuildEnv(
            Run<?, ?> build,
            Launcher launcher,
            TaskListener listener,
            String paramFileName, FilePath runWorkspace) throws IOException, InterruptedException {

        ArgumentListBuilder args = new ArgumentListBuilder();
        PrintStream out = listener.getLogger();

        String hpToolsAborter_exe = "HpToolsAborter.exe";

		URL hpToolsAborterUrl = Jenkins.getInstance().pluginManager.uberClassLoader.getResource("HpToolsAborter.exe");
        FilePath hpToolsAborterFile = runWorkspace.child(hpToolsAborter_exe);
        
        args.add(hpToolsAborterFile);
        args.add(paramFileName);
        
        hpToolsAborterFile.copyFrom(hpToolsAborterUrl);
        
        int returnCode = launcher.launch().cmds(args).stdout(out).pwd(hpToolsAborterFile.getParent()).join();
        
        try {
        	hpToolsAborterFile.delete();
		} catch (Exception e) {
			 listener.error("failed copying HpToolsAborter" + e);
		}
        
        
        if (returnCode != 0) {
            if (returnCode == 1) {
                build.setResult(Result.FAILURE);
            } else if (returnCode == 2) {
                build.setResult(Result.UNSTABLE);
            } else if (returnCode == 3) {
                build.setResult(Result.ABORTED);
            }
        }
    }
    
    
}
