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

package org.apache.jmeter.functions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Function to generate chars from a list of decimal or hex values
 */
public class CharFunction extends AbstractFunction {

    private static final Logger log = LoggingManager.getLoggerForClass();
    
    private static final List desc = new LinkedList();

    private static final String KEY = "__char"; //$NON-NLS-1$

    static {
        desc.add(JMeterUtils.getResString("char_value")); //$NON-NLS-1$
    }

    private Object[] values;

    public CharFunction() {
    }

    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

        StrBuilder sb = new StrBuilder(values.length);
        for (int i=0; i < values.length; i++){
            String numberString = ((CompoundVariable) values[i]).execute().trim();
            long value = 0;
            try {
                if (numberString.startsWith("0x")) {// $NON-NLS-1$
                    value=Long.parseLong(numberString.substring(2),16);
                } else {
                    value=Long.parseLong(numberString);
                }
            } catch (NumberFormatException e){
                log.warn("Could not parse "+numberString+" : "+e);
            }
            char ch = (char) value;
            sb.append(ch);
        }
        return sb.toString();

    }

    public synchronized void setParameters(Collection parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        values = parameters.toArray();
    }

    public String getReferenceKey() {
        return KEY;
    }

    public List getArgumentDesc() {
        return desc;
    }
}
