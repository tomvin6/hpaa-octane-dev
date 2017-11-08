/*
 * MIT License
 *
 * Copyright (c) 2016 Hewlett-Packard Development Company, L.P.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.hpe.application.automation.tools.pc;

import com.thoughtworks.xstream.XStream;

/**
 *
 */
public class TrendReportRequest {

    @SuppressWarnings("unused")
    private String xmlns = PcRestProxy.PC_API_XMLNS;

    private String project;
    private int runId;
    private TrendedRange trandedRange;

    public TrendReportRequest(String project, int runId, TrendedRange trandedRange) {
        this.project = project;
        this.runId = runId;
        this.trandedRange = trandedRange;
    }

    public String objectToXML() {
        XStream xstream = new XStream();
        xstream.useAttributeFor(TrendReportRequest.class, "xmlns");
        xstream.alias("TrendReport", TrendReportRequest.class);
        xstream.aliasField("Project", TrendReportRequest.class, "project");
        xstream.aliasField("RunId", TrendReportRequest.class, "runId");
        xstream.aliasField("TrendedRange", TrendReportRequest.class, "trendedRange");
        return xstream.toXML(this);
    }
}
