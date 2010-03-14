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
package org.apache.jmeter.report.engine;

import java.io.Serializable;

import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jorphan.collections.HashTree;

public class StandardReportEngine implements Runnable, Serializable,
        ReportEngine {

    /**
     *
     */
    public StandardReportEngine() {
        super();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.report.engine.ReportEngine#configure(org.apache.jorphan.collections.HashTree)
     */
    public void configure(HashTree testPlan) {
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.report.engine.ReportEngine#runReport()
     */
    public void runReport() throws JMeterEngineException {
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.report.engine.ReportEngine#stopReport()
     */
    public void stopReport() {
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.report.engine.ReportEngine#reset()
     */
    public void reset() {
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.report.engine.ReportEngine#exit()
     */
    public void exit() {
    }

}
