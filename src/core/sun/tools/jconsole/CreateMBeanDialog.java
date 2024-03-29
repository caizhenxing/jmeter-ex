/*
 * Copyright 2004-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.tools.jconsole;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.TreeSet;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import static sun.tools.jconsole.Resources.*;
import static sun.tools.jconsole.Utilities.*;

@SuppressWarnings("serial")
public class CreateMBeanDialog extends InternalDialog
                implements ActionListener {
    JConsole jConsole;
    JComboBox connections;
    JButton createMBeanButton, unregisterMBeanButton, cancelButton;

    private static final String HOTSPOT_MBEAN =
        "sun.management.HotspotInternal";
    private static final String HOTSPOT_MBEAN_OBJECTNAME =
        "sun.management:type=HotspotInternal";
    public CreateMBeanDialog(JConsole jConsole) {
        super(jConsole, "JConsole: Hotspot MBeans", true);

        this.jConsole = jConsole;
        setAccessibleDescription(this,
                                 getText("Hotspot MBeans.dialog.accessibleDescription"));
        Container cp = getContentPane();
        ((JComponent)cp).setBorder(new EmptyBorder(10, 10, 4, 10));

        JPanel centerPanel = new JPanel(new VariableGridLayout(0,
                                                        1,
                                                        4,
                                                        4,
                                                        false,
                                                        true));
        cp.add(centerPanel, BorderLayout.CENTER);
        connections = new JComboBox();
        updateConnections();

        centerPanel.add(new LabeledComponent(Resources.
                                             getText("Manage Hotspot MBeans "+
                                                     "in: "),
                                             connections));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        cp.add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.add(createMBeanButton =
                        new JButton(Resources.getText("Create")));
        buttonPanel.add(unregisterMBeanButton =
                        new JButton(Resources.getText("Unregister")));
        buttonPanel.add(cancelButton =
                        new JButton(Resources.getText("Cancel")));

        statusBar = new JLabel(" ", JLabel.CENTER);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        createMBeanButton.addActionListener(this);
        unregisterMBeanButton.addActionListener(this);
        cancelButton.addActionListener(this);

        LabeledComponent.layout(centerPanel);
        pack();
        setLocationRelativeTo(jConsole);
    }

    private void updateConnections() {
        List<VMPanel> frames = jConsole.getInternalFrames();	// jex001C
        TreeSet<ProxyClient> data =
            new TreeSet<ProxyClient>(new Comparator<ProxyClient>() {
            public int compare(ProxyClient o1, ProxyClient o2) {
                // TODO: Need to understand how this method being used?
                return o1.connectionName().compareTo(o2.connectionName());
            }
        });

        if (frames.size() == 0) {
            JComponent cp = (JComponent)jConsole.getDesktopPane();	// jex001C
            Component comp = ((BorderLayout)cp.getLayout()).
                getLayoutComponent(BorderLayout.CENTER);
            if (comp instanceof VMPanel) {
                VMPanel vmpanel = (VMPanel) comp;
                ProxyClient client = vmpanel.getProxyClient(false);
                if (client != null && client.hasPlatformMXBeans()) {
                    data.add(client);
                }
            }
        } else {
            for (VMPanel f : frames) {		// jex001C
                ProxyClient client = f.getProxyClient(false);	// jex001C
                if (client != null && client.hasPlatformMXBeans()) {
                    data.add(client);
                }
            }
        }
        connections.invalidate();
        connections.setModel(new DefaultComboBoxModel(data.toArray()));
        connections.validate();
    }

    public void actionPerformed(final ActionEvent ev) {
        setVisible(false);
        statusBar.setText("");
        if (ev.getSource() != cancelButton) {
            new Thread("CreateMBeanDialog.actionPerformed") {
                    public void run() {
                        try {
                            StringBuffer buff = null;
                            Object c = connections.getSelectedItem();
                            if(c == null) return;
                            if(ev.getSource() == createMBeanButton) {
                                MBeanServerConnection connection =
                                    ((ProxyClient) c).
                                    getMBeanServerConnection();
                                connection.createMBean(HOTSPOT_MBEAN, null);
                            } else {
                                if(ev.getSource() == unregisterMBeanButton) {
                                    MBeanServerConnection connection =
                                        ((ProxyClient) c).
                                        getMBeanServerConnection();
                                    connection.unregisterMBean(new
                                        ObjectName(HOTSPOT_MBEAN_OBJECTNAME));
                                }
                            }
                            return;
                        } catch(InstanceAlreadyExistsException e) {
                            statusBar.setText(Resources.
                                              getText("Error: MBeans already "
                                                      + "exist"));
                        } catch(InstanceNotFoundException e) {
                            statusBar.setText(Resources.
                                              getText("Error: MBeans do not "
                                                      + "exist"));
                        } catch(Exception e) {
                            statusBar.setText(e.toString());
                        }
                        setVisible(true);
                    }
                }.start();
        }
    }

    public void setVisible(boolean b) {
        boolean wasVisible = isVisible();

        if(b) {
            setLocationRelativeTo(jConsole);
            invalidate();
            updateConnections();
            validate();
            repaint();
        }

        super.setVisible(b);


        if (b && !wasVisible) {
            // Need to delay this to make focus stick
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    connections.requestFocus();
                }
            });
        }
    }
}
