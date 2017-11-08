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

import java.util.ArrayList;
import java.util.List;

/**
 * Holds Trending Root data
 */
public  class TrendReportTransactionDataRoot{

    private List trendReportRoot;

    public TrendReportTransactionDataRoot(){
        trendReportRoot = new ArrayList();
    }

    public static TrendReportTransactionDataRoot xmlToObject(String xml)
    {
        XStream xstream = new XStream();
        xstream.alias("Root" , TrendReportTransactionDataRoot.class);
        xstream.alias("TransactionsData" , TrendReportTransactionDataRows.class);
        xstream.alias("TransactionsDataRow" , TrendReportTransactionDataRow.class);
        xstream.alias("MonitorsData" , TrendReportMonitorsDataRows.class);
        xstream.alias("MonitorsDataRow" , TrendReportMonitorsDataRow.class);
        xstream.alias("RegularData" , TrendReportRegularDataRows.class);
        xstream.alias("RegularDataRow" , TrendReportRegularDataRow.class);
        xstream.addImplicitCollection(TrendReportTransactionDataRoot.class, "trendReportRoot");
        xstream.addImplicitCollection(TrendReportTransactionDataRows.class, "trendReportTransactionDataRowList");
        xstream.addImplicitCollection(TrendReportMonitorsDataRows.class, "trendReportMonitorsDataRowList");
        xstream.addImplicitCollection(TrendReportRegularDataRows.class, "trendReportRegularDataRowList");

        xstream.setClassLoader(TrendReportTransactionDataRoot.class.getClassLoader());
        return (TrendReportTransactionDataRoot)xstream.fromXML(xml);
    }

    public List getTrendReportRoot() {
        return trendReportRoot;
    }


}
