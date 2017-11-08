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

public class PcRunEventLogRecord {

    private int    ID;

    private String Type;

    private String Time;

    private String Name;

    private String Description;

    private String Responsible;
    
    public static PcRunEventLogRecord xmlToObject(String xml)
    {         
      XStream xstream = new XStream();
      xstream.alias("Record" , PcRunEventLogRecord.class);
      return (PcRunEventLogRecord)xstream.fromXML(xml); 
    }

    public int getID() {
        return ID;
    }

    public String getType() {
        return Type;
    }

    public String getTime() {
        return Time;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public String getResponsible() {
        return Responsible;
    }

}
