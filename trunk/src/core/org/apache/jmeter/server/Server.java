package org.apache.jmeter.server;

import java.io.Serializable;

import org.apache.jmeter.testelement.AbstractTestElement;

import com.alibaba.b2b.qa.monitor.RemoteAgent;

public class Server extends AbstractTestElement implements Serializable {

	private static final long serialVersionUID = 1L;
	RemoteAgent remoteAgent=null;
	
	public RemoteAgent getRemoteAgent() {
		return remoteAgent;
	}
	public void setRemoteAgent(RemoteAgent remoteAgent) {
		this.remoteAgent = remoteAgent;
	}

}
