package org.apache.jmeter.control.gui;

import javax.swing.JDialog;

public class ProcessListDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public ProcessListDialog(){
			init();
			this.setResizable(false);
			this.setModal(true);
			this.setTitle("Title Dialog");
			this.setSize(800,500);
			this.setLocation(400, 200);
	}

	private void init() {
		
	}
}
