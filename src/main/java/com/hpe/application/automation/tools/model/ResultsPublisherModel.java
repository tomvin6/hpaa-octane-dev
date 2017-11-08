// (c) Copyright 2012 Hewlett-Packard Development Company, L.P. 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.hpe.application.automation.tools.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

public class ResultsPublisherModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final static EnumDescription dontArchiveResults = new EnumDescription("DONT_ARCHIVE_TEST_REPORT", "Do not archive HPE test reports");
	public final static EnumDescription alwaysArchiveResults = new EnumDescription("ALWAYS_ARCHIVE_TEST_REPORT", "Always archive HPE test reports");
	public final static EnumDescription ArchiveFailedTestsResults = new EnumDescription("ONLY_ARCHIVE_FAILED_TESTS_REPORT", "Archive HPE test report for failed tests ");
	public final static EnumDescription CreateHtmlReportResults = new EnumDescription("PUBLISH_HTML_REPORT", "Always archive and publish HPE test reports (LR only)");
    public final static List<EnumDescription> archiveModes =
            Arrays.asList(ArchiveFailedTestsResults, alwaysArchiveResults,
                    CreateHtmlReportResults, dontArchiveResults);

	private String archiveTestResultsMode;

	@DataBoundConstructor
	public ResultsPublisherModel(String archiveTestResultsMode) {
	
		this.archiveTestResultsMode=archiveTestResultsMode;
		
		if (this.archiveTestResultsMode.isEmpty()){
			this.archiveTestResultsMode=dontArchiveResults.getValue();
		}
	}

	public String getArchiveTestResultsMode() {
		return archiveTestResultsMode;
	}
	
}



