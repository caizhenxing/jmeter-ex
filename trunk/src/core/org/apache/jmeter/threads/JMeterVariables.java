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

package org.apache.jmeter.threads;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jmeter.util.JMeterUtils;

/**
 * Class which defines JMeter variables.
 * These are similar to properties, but they are local to a single thread.
 */
public class JMeterVariables {
    private Map variables = new HashMap();

    private int iteration = 0;

    // Property names to preload into JMeter variables:
    private static final String [] PRE_LOAD = {
      "START.MS",     // $NON-NLS-1$
      "START.YMD",    // $NON-NLS-1$
      "START.HMS",    //$NON-NLS-1$
      "TESTSTART.MS", // $NON-NLS-1$
    };

    public JMeterVariables() {
        preloadVariables();
    }

    private void preloadVariables(){
        for (int i = 0; i < PRE_LOAD.length; i++){
            String property=PRE_LOAD[i];
            String value=JMeterUtils.getProperty(property);
            if (value != null){
                variables.put(property,value);
            }
        }
    }

    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    public int getIteration() {
        return iteration;
    }

    public void incIteration() {
        iteration++;
    }

    // Does not appear to be used
    public void initialize() {
        variables.clear();
        preloadVariables();
    }

    public Object remove(String key) {
        return variables.remove(key);
    }

    public void put(String key, String value) {
        variables.put(key, value);
    }

    public void putObject(String key, Object value) {
        variables.put(key, value);
    }

    public void putAll(Map vars) {
        variables.putAll(vars);
    }

    public void putAll(JMeterVariables vars) {
        putAll(vars.variables);
    }

    /**
     * Returns null values if variable doesn't exist. Users of this must check
     * for null.
     */
    public String get(String key) {
        return (String) variables.get(key);
    }

    public Object getObject(String key) {
        return variables.get(key);
    }

    public Iterator getIterator(){
        return Collections.unmodifiableMap(variables).entrySet().iterator() ;
    }

    // Used by DebugSampler
    public Set entrySet(){
        return Collections.unmodifiableMap(variables).entrySet();
    }
}