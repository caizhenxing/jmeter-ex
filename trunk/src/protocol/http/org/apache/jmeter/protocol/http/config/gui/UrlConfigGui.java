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

package org.apache.jmeter.protocol.http.config.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;

/**
 * Basic URL / HTTP Request configuration:
 * - host and port
 * - connect and response timeouts
 * - path, method, encoding, parameters
 * - redirects & keepalive
 */
public class UrlConfigGui extends JPanel implements ChangeListener {
    private HTTPArgumentsPanel argsPanel;

    private JTextField domain;

    private JTextField port;

    private JTextField connectTimeOut;
    
    private JTextField responseTimeOut;
    
    private JTextField protocol;

    private JTextField contentEncoding;

    private JTextField path;

    private JCheckBox followRedirects;

    private JCheckBox autoRedirects;

    private JCheckBox useKeepAlive;

    private JCheckBox useMultipartForPost;

    private JLabeledChoice method;

    private final boolean notConfigOnly;
    // set this true to suppress some items for use in HTTP Request defaults

    public UrlConfigGui() {
        notConfigOnly=true;
        init();
    }

    public UrlConfigGui(boolean value) {
        notConfigOnly=value;
        init();
    }

    public void clear() {
        domain.setText(""); // $NON-NLS-1$
        if (notConfigOnly){
            followRedirects.setSelected(false);
            autoRedirects.setSelected(true);
            method.setText(HTTPSamplerBase.DEFAULT_METHOD);
            useKeepAlive.setSelected(true);
            useMultipartForPost.setSelected(false);
        }
        path.setText(""); // $NON-NLS-1$
        port.setText(""); // $NON-NLS-1$
        connectTimeOut.setText(""); // $NON-NLS-1$
        responseTimeOut.setText(""); // $NON-NLS-1$
        protocol.setText(""); // $NON-NLS-1$
        contentEncoding.setText(""); // $NON-NLS-1$
        argsPanel.clear();
    }

    public TestElement createTestElement() {
        ConfigTestElement element = new ConfigTestElement();

        element.setName(this.getName());
        element.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
        element.setProperty(TestElement.TEST_CLASS, element.getClass().getName());
        modifyTestElement(element);
        return element;
    }

    /**
     * Save the GUI values in the sampler.
     *
     * @param element
     */
    public void modifyTestElement(TestElement element) {
        Arguments args = (Arguments) argsPanel.createTestElement();

        HTTPArgument.convertArgumentsToHTTP(args);
        element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
        element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
        element.setProperty(HTTPSamplerBase.PORT, port.getText());
        element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
        element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
        element.setProperty(HTTPSamplerBase.PROTOCOL, protocol.getText());
        element.setProperty(HTTPSamplerBase.CONTENT_ENCODING, contentEncoding.getText());
        element.setProperty(HTTPSamplerBase.PATH, path.getText());
        if (notConfigOnly){
            element.setProperty(HTTPSamplerBase.METHOD, method.getText());
            element.setProperty(new BooleanProperty(HTTPSamplerBase.FOLLOW_REDIRECTS, followRedirects.isSelected()));
            element.setProperty(new BooleanProperty(HTTPSamplerBase.AUTO_REDIRECTS, autoRedirects.isSelected()));
            element.setProperty(new BooleanProperty(HTTPSamplerBase.USE_KEEPALIVE, useKeepAlive.isSelected()));
            element.setProperty(new BooleanProperty(HTTPSamplerBase.DO_MULTIPART_POST, useMultipartForPost.isSelected()));
        }
    }

    /**
     * Set the text, etc. in the UI.
     *
     * @param el
     *            contains the data to be displayed
     */
    public void configure(TestElement el) {
        setName(el.getName());
        argsPanel.configure((TestElement) el.getProperty(HTTPSamplerBase.ARGUMENTS).getObjectValue());
        domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));

        String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);

        // Only display the port number if it is meaningfully specified
        if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
            port.setText(""); // $NON-NLS-1$
        } else {
            port.setText(portString);
        }
        connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
        responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
        protocol.setText(el.getPropertyAsString(HTTPSamplerBase.PROTOCOL));
        contentEncoding.setText(el.getPropertyAsString(HTTPSamplerBase.CONTENT_ENCODING));
        path.setText(el.getPropertyAsString(HTTPSamplerBase.PATH));
        if (notConfigOnly){
            method.setText(el.getPropertyAsString(HTTPSamplerBase.METHOD));
            followRedirects.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
            autoRedirects.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
            useKeepAlive.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
            useMultipartForPost.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
        }
    }

    private void init() {// called from ctor, so must not be overridable
        this.setLayout(new BorderLayout());

        // WEB REQUEST PANEL
        JPanel webRequestPanel = new JPanel();
        webRequestPanel.setLayout(new BorderLayout());
        webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("web_request"))); // $NON-NLS-1$

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(getProtocolAndMethodPanel());
        northPanel.add(getPathPanel());

        webRequestPanel.add(northPanel, BorderLayout.NORTH);
        webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);

        this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
        this.add(webRequestPanel, BorderLayout.CENTER);
    }

    /**
     * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
     * 
     * @return the panel
     */
    protected final JPanel getWebServerTimeoutPanel() {
        // WEB SERVER PANEL
        JPanel webServerPanel = new HorizontalPanel();
        webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
        final JPanel domainPanel = getDomainPanel();
        final JPanel portPanel = getPortPanel();
        webServerPanel.add(domainPanel, BorderLayout.CENTER);
        webServerPanel.add(portPanel, BorderLayout.EAST);
        
        JPanel timeOut = new HorizontalPanel();
        timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
        final JPanel connPanel = getConnectTimeOutPanel();
        final JPanel reqPanel = getResponseTimeOutPanel();
        timeOut.add(connPanel);
        timeOut.add(reqPanel);
        
        
        JPanel webServerTimeoutPanel = new VerticalPanel();
        webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
        webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
        return webServerTimeoutPanel;
    }

    private JPanel getPortPanel() {
        port = new JTextField(4);

        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
        label.setLabelFor(port);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(port, BorderLayout.CENTER);

        return panel;
    }

    private JPanel getConnectTimeOutPanel() {
        connectTimeOut = new JTextField(4);

        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
        label.setLabelFor(connectTimeOut);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(connectTimeOut, BorderLayout.CENTER);

        return panel;
    }

    private JPanel getResponseTimeOutPanel() {
        responseTimeOut = new JTextField(4);

        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
        label.setLabelFor(responseTimeOut);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(responseTimeOut, BorderLayout.CENTER);

        return panel;
    }

    private JPanel getDomainPanel() {
        domain = new JTextField(20);

        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
        label.setLabelFor(domain);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(domain, BorderLayout.CENTER);
        return panel;
    }

    /**
     * This method defines the Panel for the HTTP path, 'Follow Redirects'
     * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
     *
     * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
     *         KeepAlive' elements.
     */
    protected Component getPathPanel() {
        path = new JTextField(15);

        JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
        label.setLabelFor(path);

        if (notConfigOnly){
            followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
            followRedirects.setSelected(false);

            autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
            autoRedirects.addChangeListener(this);
            autoRedirects.setSelected(true);// Default changed in 2.3

            useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
            useKeepAlive.setSelected(true);

            useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
            useMultipartForPost.setSelected(false);
        }

        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.add(label, BorderLayout.WEST);
        pathPanel.add(path, BorderLayout.CENTER);
        pathPanel.setMinimumSize(pathPanel.getPreferredSize());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pathPanel);
        if (notConfigOnly){
            JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            optionPanel.add(autoRedirects);
            optionPanel.add(followRedirects);
            optionPanel.add(useKeepAlive);
             optionPanel.add(useMultipartForPost);
            optionPanel.setMinimumSize(optionPanel.getPreferredSize());
            panel.add(optionPanel);
        }

        return panel;
    }

    protected JPanel getProtocolAndMethodPanel() {
        // PROTOCOL
        protocol = new JTextField(10);
        // CONTENT_ENCODING
        contentEncoding = new JTextField(10);

        JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
        protocolLabel.setLabelFor(protocol);
        JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
        protocolLabel.setLabelFor(contentEncoding);
        if (notConfigOnly){
            method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                    HTTPSamplerBase.getValidMethodsAsArray());
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(protocolLabel);
        panel.add(protocol);
        panel.add(Box.createHorizontalStrut(5));

        if (notConfigOnly){
            panel.add(method);
        }
        panel.setMinimumSize(panel.getPreferredSize());
        panel.add(Box.createHorizontalStrut(5));

        panel.add(contentEncodingLabel);
        panel.add(contentEncoding);
        return panel;
    }

    protected JPanel getParameterPanel() {
        argsPanel = new HTTPArgumentsPanel();

        return argsPanel;
    }

    // Disable follow redirects if Autoredirect is selected
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == autoRedirects){
            if (autoRedirects.isSelected()) {
                followRedirects.setEnabled(false);
            } else {
                followRedirects.setEnabled(true);
            }
        }
    }
}
