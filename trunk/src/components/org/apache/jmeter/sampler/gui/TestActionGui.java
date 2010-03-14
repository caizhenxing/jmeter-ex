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

package org.apache.jmeter.sampler.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.layout.VerticalLayout;

public class TestActionGui extends AbstractSamplerGui {
    // Gui components
    private JComboBox targetBox;

    // private ButtonGroup actionButtons;
    private JRadioButton pauseButton;

    private JRadioButton stopButton;

    private JRadioButton stopNowButton;

    private JTextField durationField;

    // State variables
    private int target;

    private int action;

    private String durationString;

    // String in the panel
    // Do not make these static, otherwise language changes don't work
    private final String targetLabel = JMeterUtils.getResString("test_action_target"); // $NON-NLS-1$

    private final String threadTarget = JMeterUtils.getResString("test_action_target_thread"); // $NON-NLS-1$

    private final String testTarget = JMeterUtils.getResString("test_action_target_test"); // $NON-NLS-1$

    private final String actionLabel = JMeterUtils.getResString("test_action_action"); // $NON-NLS-1$

    private final String pauseAction = JMeterUtils.getResString("test_action_pause"); // $NON-NLS-1$

    private final String stopAction = JMeterUtils.getResString("test_action_stop"); // $NON-NLS-1$

    private final String stopNowAction = JMeterUtils.getResString("test_action_stop_now"); // $NON-NLS-1$

    private final String durationLabel = JMeterUtils.getResString("test_action_duration"); // $NON-NLS-1$

    public TestActionGui() {
        super();
        target = TestAction.THREAD;
        action = TestAction.PAUSE;
        durationString = ""; // $NON-NLS-1$
        init();
    }

    public String getLabelResource() {
        return "test_action_title"; // $NON-NLS-1$
    }

    public void configure(TestElement element) {
        super.configure(element);
        TestAction ta = (TestAction) element;

        target = ta.getTarget();
        if (target == TestAction.THREAD) {
            targetBox.setSelectedItem(threadTarget);
        } else {
            targetBox.setSelectedItem(testTarget);
        }
        action = ta.getAction();
        if (action == TestAction.PAUSE) {
            pauseButton.setSelected(true);
        } else if (action == TestAction.STOP_NOW) {
            stopNowButton.setSelected(true);
        } else {
            stopButton.setSelected(true);
        }

        durationString = ta.getDurationAsString();
        durationField.setText(durationString);
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement() {
        TestAction ta = new TestAction();
        modifyTestElement(ta);
        return ta;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        TestAction ta = (TestAction) element;
        ta.setAction(action);
        ta.setTarget(target);
        ta.setDuration(durationString);
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();

        targetBox.setSelectedIndex(0);
        durationString = ""; //$NON-NLS-1$
        durationField.setText(""); //$NON-NLS-1$
        pauseButton.setSelected(true);
        action = TestAction.PAUSE;
        target = TestAction.THREAD;

    }

    private void init() {
        setLayout(new VerticalLayout(5, VerticalLayout.BOTH, VerticalLayout.TOP));
        setBorder(makeBorder());
        add(makeTitlePanel());

        // Target
        HorizontalPanel targetPanel = new HorizontalPanel();
        targetPanel.add(new JLabel(targetLabel));
        DefaultComboBoxModel targetModel = new DefaultComboBoxModel();
        targetModel.addElement(threadTarget);
        targetModel.addElement(testTarget);
        targetBox = new JComboBox(targetModel);
        targetBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((String) targetBox.getSelectedItem()).equals(threadTarget)) {
                    target = TestAction.THREAD;
                } else {
                    target = TestAction.TEST;
                }
            }
        });
        targetPanel.add(targetBox);
        add(targetPanel);

        // Action
        HorizontalPanel actionPanel = new HorizontalPanel();
        ButtonGroup actionButtons = new ButtonGroup();
        pauseButton = new JRadioButton(pauseAction, true);
        pauseButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (pauseButton.isSelected()) {
                    action = TestAction.PAUSE;
                    durationField.setEnabled(true);
                }

            }
        });
        stopButton = new JRadioButton(stopAction, false);
        stopButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (stopButton.isSelected()) {
                    action = TestAction.STOP;
                    durationField.setEnabled(false);
                }
            }
        });
        stopNowButton = new JRadioButton(stopNowAction, false);
        stopNowButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (stopNowButton.isSelected()) {
                    action = TestAction.STOP_NOW;
                    durationField.setEnabled(false);
                }
            }
        });
        actionButtons.add(pauseButton);
        actionButtons.add(stopButton);
        actionButtons.add(stopNowButton);
        actionPanel.add(new JLabel(actionLabel));
        actionPanel.add(pauseButton);
        actionPanel.add(stopButton);
        actionPanel.add(stopNowButton);
        add(actionPanel);

        // Duration
        HorizontalPanel durationPanel = new HorizontalPanel();
        durationField = new JTextField(15);
        durationField.setText("");
        durationField.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                durationString = durationField.getText();
            }

            public void focusGained(FocusEvent e) {
            }
        });
        durationPanel.add(new JLabel(durationLabel));
        durationPanel.add(durationField);
        add(durationPanel);
    }

}
