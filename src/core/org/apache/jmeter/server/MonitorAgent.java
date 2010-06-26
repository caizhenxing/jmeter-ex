package org.apache.jmeter.server;

import java.io.Serializable;

/**
 * 监控的机器
 * 
 * @author shenghua
 * @version jex002A
 */
public class MonitorAgent implements Serializable {

    private static final long serialVersionUID = -2337392151149978774L;
    private String            projectName;
    private String            reportTime;
    private String            machineIp;
    private String            name;                                     // 监控项的名字

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
