/**
 * Project: Jmeter-Ex
 * 
 * File Created at 2010-12-29
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package org.apache.jmeter.util;

/**
 * TODO Comment of CustomLoopControler
 * 
 * @author chenchao.yecc
 */
public class CustomLoopControler {
    private int startNum = 0;
    private int endNum   = 0;
    private int stepNum  = 0;

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

}
