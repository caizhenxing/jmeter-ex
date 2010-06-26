package org.apache.jmeter.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程数据获取接口
 * 
 * @author shenghua
 * @version jex002A
 */
public interface RemoteDataService {

    /**
     * 获取所有的项目
     * 
     * @return
     */
    public List<String> getProjects();

    /**
     * 获取指定项目的所有agent
     * 
     * @param projectName 项目的名字
     * @return
     */
    public Map<String, ArrayList<HashMap<String, String>>> getProjectAgents(String projectName);

    /**
     * 获取指定agent的监控数据
     * 
     * @param agent
     * @param startPosition
     * @return
     */
    public MonitorData getMonitorData(Map<String, String> agent, long startPosition);

    /**
     * 取得从现在往前的100条记录
     * 
     * @param agent
     * @return
     */
    public MonitorData getStartMonitorData(Map<String, String> agent);

    /**
     * 取得从现在往前的length条记录
     * 
     * @param agent
     * @param length
     * @return
     */
    public MonitorData getStartMonitorData(Map<String, String> agent, long length);

    /**
     * 取得指定时间段内的监控数据
     * 
     * @param agent
     * @param startTime
     * @param stopTime
     * @return
     */
    public MonitorData getMonitorDataByDuration(Map<String, String> agent, long startTime,
                                                long stopTime);

    /**
     * 取得指定时间开始的第length条记录
     * 
     * @param agent
     * @param startTime
     * @param length
     * @return
     */
    public MonitorData getMonitorDataByStartTimeAndLength(Map<String, String> agent,
                                                          long startTime, long length);

}
