package org.apache.jmeter.gui.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import org.apache.jmeter.jtlparse.gui.JTLParserDialog;

/**
 * jtl file parser dialog
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class CreateJTLParserDialog  extends AbstractAction {
    private JTLParserDialog parser = null;

    private static Set<String> commands;
    static {
        commands = new HashSet<String>();
        commands.add(ActionNames.JTLPARSE);
    }

    public CreateJTLParserDialog() {
    	parser = new JTLParserDialog();
    }

    /**
     * Provide the list of Action names that are available in this command.
     */
    public Set<String> getActionNames() {
        return commands;
    }

    @SuppressWarnings("deprecation")
	public void doAction(ActionEvent arg0) {
    	parser.show();
    }
}