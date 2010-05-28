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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;

/**
 * <p>Function which escapes the characters in a <code>String</code> using HTML entities.</p>
 *
 * <p>
 * For example:
 * </p> 
 * <p><code>"bread" & "butter"</code></p>
 * becomes:
 * <p>
 * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
 * </p>
 *
 * <p>Supports all known HTML 4.0 entities.
 * Note that the commonly used apostrophe escape character (&amp;apos;)
 * is not a legal entity and so is not supported). </p>
 * 
 * @see StringEscapeUtils#escapeHtml(String) (Commons Lang)
 */
public class EscapeHtml extends AbstractFunction {

    private static final List desc = new LinkedList();

    private static final String KEY = "__escapeHtml"; //$NON-NLS-1$

    static {
        desc.add(JMeterUtils.getResString("escape_html_string")); //$NON-NLS-1$
    }

    private Object[] values;

    public EscapeHtml() {
    }

    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

        String rawString = ((CompoundVariable) values[0]).execute();
        return StringEscapeUtils.escapeHtml(rawString);

    }

    public synchronized void setParameters(Collection parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 1);
        values = parameters.toArray();
    }

    public String getReferenceKey() {
        return KEY;
    }

    public List getArgumentDesc() {
        return desc;
    }
}