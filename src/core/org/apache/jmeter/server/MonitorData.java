/**
 * Project: monitor.core
 * 
 * File Created at 2010-6-21
 * $Id$
 * 
 * Copyright 2008 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package org.apache.jmeter.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 监控数据的传输
 * 
 * @author shenghua
 * @version jex002A
 */
public class MonitorData implements Serializable {

    private static final long serialVersionUID = -5567905433529362028L;
    private String[]          fields;    
    private List<String[]>      values           = new LinkedList<String[]>();
    private long              dataEndPosition;

    public void setDataEndPosition(long dataEndPosition) {
        this.dataEndPosition = dataEndPosition;
    }

    public long getDataEndPosition() {
        return dataEndPosition;
    }

    public void setValues(List<String[]> values) {
        this.values = values;
    }

    public List<String[]> getValues() {
        return values;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields;
    }

}
