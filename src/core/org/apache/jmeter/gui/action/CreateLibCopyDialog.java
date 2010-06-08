package org.apache.jmeter.gui.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import org.apache.jmeter.libcopy.gui.LibraryCopyDialog;

public class CreateLibCopyDialog   extends AbstractAction {
    private LibraryCopyDialog copyer = null;

    private static Set<String> commands;
    static {
        commands = new HashSet<String>();
        commands.add(ActionNames.LIBCOPY);
    }

    public CreateLibCopyDialog() {
    	copyer = new LibraryCopyDialog();
    }

    /**
     * Provide the list of Action names that are available in this command.
     */
    public Set<String> getActionNames() {
        return commands;
    }

    @SuppressWarnings("deprecation")
	public void doAction(ActionEvent arg0) {
    	copyer.show();
    }
}