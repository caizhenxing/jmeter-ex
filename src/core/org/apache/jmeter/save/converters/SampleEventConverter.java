/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.save.converters;

import org.apache.jmeter.samplers.SampleEvent;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream Converter for the SampleResult class
 */
public class SampleEventConverter implements Converter {
    /**
     * Returns the converter version; used to check for possible
     * incompatibilities
     */
    public static String getVersion() {
        return "$Revision: 674365 $"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
     */
    public boolean canConvert(Class arg0) {
        return SampleEvent.class.equals(arg0);
    }

    // TODO save hostname; save sample type (plain or http)
    public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        SampleEvent evt = (SampleEvent) source;
        Object res = evt.getResult();
        context.convertAnother(res);
    }

    // TODO does not work yet; need to determine the sample type
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        SampleEvent evt = new SampleEvent();
        return evt;
    }
}