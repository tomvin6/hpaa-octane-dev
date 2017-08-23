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


public class TimeInterval {

    @SuppressWarnings("unused")
    private String xmlns = PcRestProxy.PC_API_XMLNS;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean daysSpecified;
    private boolean hoursSpecified;
    private boolean minutesSpecified;
    private boolean secondsSpecified;

    public TimeInterval(int days, int hours, int minutes, int seconds, boolean daysSpecified, boolean hoursSpecified, boolean minutesSpecified, boolean secondsSpecified) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.daysSpecified = daysSpecified;
        this.hoursSpecified = hoursSpecified;
        this.minutesSpecified = minutesSpecified;
        this.secondsSpecified = secondsSpecified;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isDaysSpecified() {
        return daysSpecified;
    }

    public boolean isHoursSpecified() {
        return hoursSpecified;
    }

    public boolean isMinutesSpecified() {
        return minutesSpecified;
    }

    public boolean isSecondsSpecified() {
        return secondsSpecified;
    }
}
