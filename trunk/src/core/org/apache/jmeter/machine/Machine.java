package org.apache.jmeter.machine;

import java.io.Serializable;

import org.apache.jmeter.testelement.AbstractTestElement;

import sun.tools.jconsole.VMInternalFrame;
import sun.tools.jconsole.VMPanel;

/**
 * jvm client of jconsole
 * 
 * @since jex001A
 * @author chenchao.yecc
 */
public class Machine extends AbstractTestElement implements Serializable {

	private static final long serialVersionUID = -7087997027675418139L;
	private VMInternalFrame interFrame = null;
	// start button state
	private boolean start = true;
	// lebal info
	private String info = "";

	/**
	 * get label info
	 * 
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * set label info
	 * 
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * get start button state
	 * 
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * set start button state
	 * 
	 */
	public void setStart(boolean state) {
		this.start = state;
	}

	/**
	 * set jconsole's frame
	 * 
	 */
	public void setFrame(VMInternalFrame interFrame) {
		this.interFrame = interFrame;
	}

	/**
	 * get jconsole's panel
	 * 
	 */
	public VMPanel getPanel() {
		if (interFrame == null) {
			return null;
		} else {
			return interFrame.getVMPanel();
		}
	}
}
