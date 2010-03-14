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

package org.apache.jmeter.visualizers;

import java.io.Serializable;
import java.text.Format;
import java.util.Date;

public class Sample implements Serializable, Comparable {
    private final long data; // = elapsed

    private final long average;

    private final long median;

    private final long distributionLine; // TODO: what is this for?

    private final long deviation;

    private final double throughput;

    private final long errorCount;

    private final boolean success;

    private final String label;

    private final String threadName;

    private final long count;

    private final long endTime;

    private final int bytes;


    // TODO - is this used?
    public Sample(long data, long average, long deviation, double throughput, long median, boolean success) {
        this(null, data, average, deviation, median, 0, throughput, 0, success, 0, 0);
    }

    // TODO - is this used?
    public Sample(long data) {
        this(null, data, 0, 0, 0, 0, 0, 0, false, 0, 0);
    }

    public Sample(String name, long data, long average, long deviation, long median, long distributionLine,
            double throughput, long errorCount, boolean success, long num, long endTime) {
        this.data = data;
        this.average = average;
        this.deviation = deviation;
        this.throughput = throughput;
        this.success = success;
        this.median = median;
        this.distributionLine = distributionLine;
        this.label = name;
        this.errorCount = errorCount;
        this.count = num;
        this.endTime = endTime;
        this.bytes = 0;
        this.threadName = "";
    }

    public Sample(String name, long data, long average, long deviation, long median, long distributionLine,
            double throughput, long errorCount, boolean success, long num, long endTime, int bytes) {
        this.data = data;
        this.average = average;
        this.deviation = deviation;
        this.throughput = throughput;
        this.success = success;
        this.median = median;
        this.distributionLine = distributionLine;
        this.label = name;
        this.errorCount = errorCount;
        this.count = num;
        this.endTime = endTime;
        this.bytes = bytes;
        this.threadName = "";
    }

    public Sample(String name, long data, long average, long deviation, long median, long distributionLine,
            double throughput, long errorCount, boolean success, long num, long endTime, int bytes, String threadName) {
        this.data = data;
        this.average = average;
        this.deviation = deviation;
        this.throughput = throughput;
        this.success = success;
        this.median = median;
        this.distributionLine = distributionLine;
        this.label = name;
        this.errorCount = errorCount;
        this.count = num;
        this.endTime = endTime;
        this.bytes = bytes;
        this.threadName = threadName;
    }

    public Sample() {
        this(null, 0, 0, 0, 0, 0, 0, 0, true, 0, 0);
    }

    // Appears not to be used - however it is invoked via the Functor class
    public int getBytes() {
        return bytes;
    }

    /**
     * @return Returns the average.
     */
    public long getAverage() {
        return average;
    }

    /**
     * @return Returns the count.
     */
    public long getCount() {
        return count;
    }

    /**
     * @return Returns the data (usually elapsed time)
     */
    public long getData() {
        return data;
    }

    /**
     * @return Returns the deviation.
     */
    public long getDeviation() {
        return deviation;
    }

    /**
     * @return Returns the distributionLine.
     */
    public long getDistributionLine() {
        return distributionLine;
    }

    /**
     * @return Returns the error.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return Returns the errorRate.
     */
    public long getErrorCount() {
        return errorCount;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return Returns the threadName.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @return Returns the median.
     */
    public long getMedian() {
        return median;
    }

    /**
     * @return Returns the throughput.
     */
    public double getThroughput() {
        return throughput;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Sample oo = (Sample) o;
        return ((count - oo.count) < 0 ? -1 : (count == oo.count ? 0 : 1));
    }

    /**
     * @return Returns the endTime.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @return Returns the (calculated) startTime, assuming Data is the elapsed time.
     */
    public long getStartTime() {
        return endTime-data;
    }

    /**
     * @return the start time using the specified format
     * Intended for use from Functors
     */
    public String getStartTimeFormatted(Format format) {
        return format.format(new Date(getStartTime()));
    }
}
