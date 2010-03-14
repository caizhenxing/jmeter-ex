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
package org.apache.jmeter.extractor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.extractor.XPathExtractor;
import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;
/**
 * GUI for XPathExtractor class.
 */
 /* This file is inspired by RegexExtractor.
 * author <a href="mailto:hpaluch@gitus.cz">Henryk Paluch</a>
 *            of <a href="http://www.gitus.com">Gitus a.s.</a>
 * See Bugzilla: 37183
 */
public class XPathExtractorGui extends AbstractPostProcessorGui {

    private JLabeledTextField defaultField;

    private JLabeledTextField xpathQueryField;

    private JLabeledTextField refNameField;

    private JCheckBox tolerant; // Should Tidy be run?

    private JCheckBox quiet; // Should Tidy be quiet?

    private JCheckBox reportErrors; // Report Tidy errors as Assertion failure?

    private JCheckBox showWarnings; // Show Tidy warnings ?

    private JCheckBox nameSpace; // Should parser be namespace aware?

    // We could perhaps add validate/whitespace options, but they're probably not necessary for
    // the XPathExtractor

    public String getLabelResource() {
        return "xpath_extractor_title"; //$NON-NLS-1$
    }

    public XPathExtractorGui(){
        super();
        init();
    }

    public void configure(TestElement el) {
        super.configure(el);
        XPathExtractor xpe = (XPathExtractor) el;
        xpathQueryField.setText(xpe.getXPathQuery());
        defaultField.setText(xpe.getDefaultValue());
        refNameField.setText(xpe.getRefName());
        tolerant.setSelected(xpe.isTolerant());
        quiet.setSelected(xpe.isQuiet());
        showWarnings.setSelected(xpe.showWarnings());
        reportErrors.setSelected(xpe.reportErrors());
        nameSpace.setSelected(xpe.useNameSpace());
        setTidyOptions(tolerant.isSelected());
    }


    public TestElement createTestElement() {
        XPathExtractor extractor = new XPathExtractor();
        modifyTestElement(extractor);
        return extractor;
    }

    /* (non-Javadoc)
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(org.apache.jmeter.testelement.TestElement)
     */
    public void modifyTestElement(TestElement extractor) {
        super.configureTestElement(extractor);
        if ( extractor instanceof XPathExtractor){
            XPathExtractor xpath = (XPathExtractor)extractor;
            xpath.setDefaultValue(defaultField.getText());
            xpath.setRefName(refNameField.getText());
            xpath.setXPathQuery(xpathQueryField.getText());
            xpath.setTolerant(tolerant.isSelected());
            xpath.setNameSpace(nameSpace.isSelected());
            xpath.setShowWarnings(showWarnings.isSelected());
            xpath.setReportErrors(reportErrors.isSelected());
            xpath.setQuiet(quiet.isSelected());
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();

        xpathQueryField.setText(""); // $NON-NLS-1$
        defaultField.setText(""); // $NON-NLS-1$
        refNameField.setText(""); // $NON-NLS-1$
        tolerant.setSelected(false);
        nameSpace.setSelected(true);
        quiet.setSelected(true);
        reportErrors.setSelected(false);
        showWarnings.setSelected(false);
    }

    private void setTidyOptions(boolean tidySelected){
        quiet.setEnabled(tidySelected);
        reportErrors.setEnabled(tidySelected);
        showWarnings.setEnabled(tidySelected);
        nameSpace.setEnabled(!tidySelected);
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        Box options = Box.createHorizontalBox();
        tolerant = new JCheckBox(JMeterUtils.getResString("xpath_extractor_tolerant"));//$NON-NLS-1$
        quiet = new JCheckBox(JMeterUtils.getResString("xpath_tidy_quiet"),true);//$NON-NLS-1$
        reportErrors = new JCheckBox(JMeterUtils.getResString("xpath_tidy_report_errors"),true);//$NON-NLS-1$
        showWarnings = new JCheckBox(JMeterUtils.getResString("xpath_tidy_show_warnings"),true);//$NON-NLS-1$
        nameSpace = new JCheckBox(JMeterUtils.getResString("xpath_extractor_namespace"),true);//$NON-NLS-1$

        tolerant.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    setTidyOptions(tolerant.isSelected());
        }});

        setTidyOptions(tolerant.isSelected());

        Box tidyOptions = Box.createHorizontalBox();
        tidyOptions.add(tolerant);
        tidyOptions.add(quiet);
        tidyOptions.add(reportErrors);
        tidyOptions.add(showWarnings);
        tidyOptions.setBorder(BorderFactory.createEtchedBorder());
        options.add(tidyOptions);
        options.add(nameSpace);
        box.add(options);
        add(box, BorderLayout.NORTH);
        add(makeParameterPanel(), BorderLayout.CENTER);
    }


    private JPanel makeParameterPanel() {
        xpathQueryField = new JLabeledTextField(JMeterUtils.getResString("xpath_extractor_query"));//$NON-NLS-1$
        defaultField = new JLabeledTextField(JMeterUtils.getResString("default_value_field"));//$NON-NLS-1$
        refNameField = new JLabeledTextField(JMeterUtils.getResString("ref_name_field"));//$NON-NLS-1$

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        initConstraints(gbc);
        addField(panel, refNameField, gbc);
        resetContraints(gbc);
        addField(panel, xpathQueryField, gbc);
        resetContraints(gbc);
        gbc.weighty = 1;
        addField(panel, defaultField, gbc);
        return panel;
    }

    private void addField(JPanel panel, JLabeledTextField field, GridBagConstraints gbc) {
        List item = field.getComponentList();
        panel.add((Component) item.get(0), gbc.clone());
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add((Component) item.get(1), gbc.clone());
    }

    private void resetContraints(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill=GridBagConstraints.NONE;
    }

    private void initConstraints(GridBagConstraints gbc) {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
    }
}