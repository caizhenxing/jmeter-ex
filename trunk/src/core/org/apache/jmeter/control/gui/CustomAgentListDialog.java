/**
 * Project: Jmeter-Ex
 * 
 * File Created at 2010-11-19
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package org.apache.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

/**
 * TODO Comment of CustomAgentListDialog
 * @author chenchao.yecc
 *
 */
public class CustomAgentListDialog extends JDialog implements ActionListener{

    private static final long serialVersionUID = 1L;
    private JButton okBtn = new JButton("确定");
    private List<String> agentList = new ArrayList<String>();
    private JTextArea area = new JTextArea();
    
    public List<String> getCustomizedAgentList(){
        return agentList;
    }
    
    public CustomAgentListDialog(){
        initGui();
        init();
    }
    
    private void initGui(){
        JPanel textPanel = new JPanel(new BorderLayout());
        area.setLineWrap(true);
        JScrollPane scrollPanel=new JScrollPane(area);
        textPanel.add(new JLabel("添加Agent的IP地址，每行一个"),BorderLayout.NORTH);
        textPanel.add(scrollPanel,BorderLayout.CENTER);
        textPanel.add(okBtn,BorderLayout.SOUTH);
        okBtn.addActionListener(this);
        ActionListener actionListener＿Esc = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CustomAgentListDialog.this.setVisible(false);
            }
        };
        this.getRootPane().registerKeyboardAction(actionListener＿Esc,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.getContentPane().add(textPanel, BorderLayout.CENTER);
        this.setModal(true);
        this.setResizable(false);
        this.setTitle("添加自定义显示的Agent");
        this.setSize(400, 300);
        this.setLocation(400, 220);
    }

    private void init(){
        
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String text = area.getText();
        String[] tmp = text.split("\n");
        for (int i = 0; i < tmp.length; i++) {
            System.out.println(tmp[i]);
            if (!StringUtils.isEmpty(tmp[i])) {
                agentList.add(tmp[i]);
            }
        }
        this.setVisible(false);
    }
}
