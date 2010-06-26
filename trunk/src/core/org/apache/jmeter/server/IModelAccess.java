package org.apache.jmeter.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import jxl.read.biff.BiffException;

public interface IModelAccess {
	void refresh();
	void setProject(String project);
	String getProject();
	void setServiceUrl(String serviceUrl);
	String[] getChartSpecfics();
	List<String> getProjects(String url) throws MalformedURLException;
	void restart();
	void connect(String project) throws MalformedURLException;
	String getServiceURL();
//	void save(File selectedFile, HashMap<String, ChartStore> charts) throws  Exception;
	void importFile(File selectedFile) throws BiffException, IOException;
}
