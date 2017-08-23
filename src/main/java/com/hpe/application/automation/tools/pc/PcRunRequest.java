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
import com.hpe.application.automation.tools.model.TimeslotDuration;

public class PcRunRequest {

	@SuppressWarnings("unused")
    private String xmlns = PcRestProxy.PC_API_XMLNS;

	private int TestID;

	private int TestInstanceID;

	private int TimeslotID;

	private TimeslotDuration TimeslotDuration;

	private String PostRunAction;

	private boolean VudsMode;
		

	public PcRunRequest(
			int testID,
			int testInstanceID,
			int timeslotID,
			TimeslotDuration timeslotDuration,
			String postRunAction,
			boolean vudsMode) {
		
		TestID = testID;
		TestInstanceID = testInstanceID;
		TimeslotID = timeslotID;
		TimeslotDuration = timeslotDuration;
		PostRunAction = postRunAction;
		VudsMode = vudsMode;
	}
	
	public PcRunRequest() {}

	public String objectToXML() {
		XStream obj = new XStream();
		obj.alias("Run", PcRunRequest.class);
		obj.alias("TimeslotDuration", TimeslotDuration.class);
		obj.useAttributeFor(PcRunRequest.class, "xmlns");
		return obj.toXML(this);
	}
	
	public int getTestID() {
		return TestID;
	}
	
	public int getTestInstanceID() {
		return TestInstanceID;
	}
	
	public int getTimeslotID() {
		return TimeslotID;
	}
	
	public TimeslotDuration getTimeslotDuration() {
		return TimeslotDuration;
	}
	
	public String getPostRunAction() {
		return PostRunAction;
	}
	
	public boolean isVudsMode() {
		return VudsMode;
	}
}
