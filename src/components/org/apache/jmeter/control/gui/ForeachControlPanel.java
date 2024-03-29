/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.control.ForeachController;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

/**
 * The user interface for a foreach controller which specifies that its
 * subcomponents should be executed some number of times in a loop. This
 * component can be used standalone or embedded into some other component.
 */

public class ForeachControlPanel extends AbstractControllerGui {

    /**
     * A field allowing the user to specify the input variable the controller
     * should loop.
     */
    private JTextField inputVal;

    /**
     * A field allowing the user to specify output variable the controller
     * should return.
     */
    private JTextField returnVal;

    // Should we add the "_" separator?
    private JCheckBox useSeparator;

    /**
     * Boolean indicating whether or not this component should display its name.
     * If true, this is a standalone component. If false, this component is
     * intended to be used as a subpanel for another component.
     */
    private boolean displayName = true;

    /** The name of the infinite checkbox component. */
    private static final String INPUTVAL = "Input Field"; // $NON-NLS-1$

    /** The name of the loops field component. */
    private static final String RETURNVAL = "Return Field"; // $NON-NLS-1$

    /**
     * Create a new LoopControlPanel as a standalone component.
     */
    public ForeachControlPanel() {
        this(true);
    }

    /**
     * Create a new LoopControlPanel as either a standalone or an embedded
     * component.
     *
     * @param displayName
     *            indicates whether or not this component should display its
     *            name. If true, this is a standalone component. If false, this
     *            component is intended to be used as a subpanel for another
     *            component.
     */
    public ForeachControlPanel(boolean displayName) {
        this.displayName = displayName;
        init();
    }

    /**
     * A newly created component can be initialized with the contents of a Test
     * Element object by calling this method. The component is responsible for
     * querying the Test Element object for the relevant information to display
     * in its GUI.
     *
     * @param element
     *            the TestElement to configure
     */
    public void configure(TestElement element) {
        super.configure(element);
        inputVal.setText(((ForeachController) element).getInputValString());
        returnVal.setText(((ForeachController) element).getReturnValString());
        useSeparator.setSelected(((ForeachController) element).getUseSeparator());
    }

    /* Implements JMeterGUIComponent.createTestElement() */
    public TestElement createTestElement() {
        ForeachController lc = new ForeachController();
        modifyTestElement(lc);
        return lc;
    }

    /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
    public void modifyTestElement(TestElement lc) {
        configureTestElement(lc);
        if (lc instanceof ForeachController) {
            if (inputVal.getText().length() > 0) {
                ((ForeachController) lc).setInputVal(inputVal.getText());
            } else {
                ((ForeachController) lc).setInputVal(""); // $NON-NLS-1$
            }
            if (returnVal.getText().length() > 0) {
                ((ForeachController) lc).setReturnVal(returnVal.getText());
            } else {
                ((ForeachController) lc).setReturnVal(""); // $NON-NLS-1$
            }
            ((ForeachController) lc).setUseSeparator(useSeparator.isSelected());
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();

        inputVal.setText(""); // $NON-NLS-1$
        returnVal.setText(""); // $NON-NLS-1$
        useSeparator.setSelected(true);
    }


    public String getLabelResource() {
        return "foreach_controller_title"; // $NON-NLS-1$
    }

    /**
     * Initialize the GUI components and layout for this component.
     */
    private void init() {
        // The Loop Controller panel can be displayed standalone or inside
        // another panel. For standalone, we want to display the TITLE, NAME,
        // etc. (everything). However, if we want to display it within another
        // panel, we just display the Loop Count fields (not the TITLE and
        // NAME).

        // Standalone
        if (displayName) {
            setLayout(new BorderLayout(0, 5));
            setBorder(makeBorder());
            add(makeTitlePanel(), BorderLayout.NORTH);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(createLoopCountPanel(), BorderLayout.NORTH);
            add(mainPanel, BorderLayout.CENTER);
        } else {
            // Embedded
            setLayout(new BorderLayout());
            add(createLoopCountPanel(), BorderLayout.NORTH);
        }
    }

    /**
     * Create a GUI panel containing the components related to the number of
     * loops which should be executed.
     *
     * @return a GUI panel containing the loop count components
     */
    private JPanel createLoopCountPanel() {
        // JPanel loopPanel = new JPanel(new BorderLayout(5, 0));
        VerticalPanel loopPanel = new VerticalPanel();

        // LOOP LABEL
        JLabel inputValLabel = new JLabel(JMeterUtils.getResString("foreach_input")); // $NON-NLS-1$
        JLabel returnValLabel = new JLabel(JMeterUtils.getResString("foreach_output")); // $NON-NLS-1$

        // TEXT FIELD
        JPanel inputValSubPanel = new JPanel(new BorderLayout(5, 0));
        inputVal = new JTextField("", 5); // $NON-NLS-1$
        inputVal.setName(INPUTVAL);
        inputValLabel.setLabelFor(inputVal);
        inputValSubPanel.add(inputValLabel, BorderLayout.WEST);
        inputValSubPanel.add(inputVal, BorderLayout.CENTER);

        // TEXT FIELD
        JPanel returnValSubPanel = new JPanel(new BorderLayout(5, 0));
        returnVal = new JTextField("", 5); // $NON-NLS-1$
        returnVal.setName(RETURNVAL);
        returnValLabel.setLabelFor(returnVal);
        returnValSubPanel.add(returnValLabel, BorderLayout.WEST);
        returnValSubPanel.add(returnVal, BorderLayout.CENTER);

        // Checkbox
        useSeparator = new JCheckBox(JMeterUtils.getResString("foreach_use_separator"), true); // $NON-NLS-1$

        loopPanel.add(inputValSubPanel);
        loopPanel.add(returnValSubPanel);
        loopPanel.add(useSeparator);

        return loopPanel;
    }
}
