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

package org.apache.jmeter.testelement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.jmeter.threads.JMeterVariables;

/**
 * @version $Revision: 674365 $
 */
public class VariablesCollection implements Serializable {
    private Map varMap = new HashMap();

    public void addJMeterVariables(JMeterVariables jmVars) {
        varMap.put(Thread.currentThread().getName(), jmVars);
    }

    public JMeterVariables getVariables() {
        return (JMeterVariables) varMap.get(Thread.currentThread().getName());
    }

}
