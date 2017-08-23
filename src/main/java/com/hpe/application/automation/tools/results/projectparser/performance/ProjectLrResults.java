/*
 * MIT License
 *
 * Copyright (c) 2016 Hewlett-Packard Development Company, L.P.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
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

package com.hpe.application.automation.tools.results.projectparser.performance;

import java.util.SortedMap;
import java.util.TreeMap;


public class ProjectLrResults extends LrRunResults {
    private SortedMap<String, LrProjectScenarioResults> _scenarioResults;

    public ProjectLrResults() {
        _scenarioResults = new TreeMap<String, LrProjectScenarioResults>();
    }

    public SortedMap<String, LrProjectScenarioResults> getScenarioResults() {
        return _scenarioResults;
    }

    public LrProjectScenarioResults addScenario(LrProjectScenarioResults scenario) {
        LrProjectScenarioResults jobLrScenarioResult = _scenarioResults.put(scenario.getScenarioName(), scenario);
        if (jobLrScenarioResult != null) {
            _totalErrors += scenario.getTotalErrors();
            _totalFailures += scenario.getTotalFailures();
            _time += getTime();
        }
        return jobLrScenarioResult;
    }
}
