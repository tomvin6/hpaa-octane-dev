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

/**
 * Holds Trending Types Enum
 */
public class TrendReportTypes {

    public enum DataType {
        Transaction, Monitors, Regular
    }

    public enum PctType {
        TRT, TPS, TRS, UDP, VU, WEB
    }

    public enum Measurement{
        getPCT_TYPE,
        getPCT_NAME,
        PCT_MINIMUM,
        PCT_MAXIMUM,
        PCT_AVERAGE,
        PCT_MEDIAN,
        PCT_STDDEVIATION,
        PCT_COUNT1,
        PCT_SUM1,
        PCT_MACHINE,
        PCT_PERCENTILE_25,
        PCT_PERCENTILE_75,
        PCT_PERCENTILE_90,
        PCT_PERCENTILE_91,
        PCT_PERCENTILE_92,
        PCT_PERCENTILE_93,
        PCT_PERCENTILE_94,
        PCT_PERCENTILE_95,
        PCT_PERCENTILE_96,
        PCT_PERCENTILE_97,
        PCT_PERCENTILE_98,
        PCT_PERCENTILE_99
    }
}
