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

package org.apache.jmeter.report.gui.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;

import org.apache.jmeter.gui.ReportGuiPackage;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.report.gui.tree.ReportTreeNode;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class ReportAddToTree implements Command {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private Map allJMeterComponentCommands;

    public ReportAddToTree() {
        allJMeterComponentCommands = new HashMap();
        allJMeterComponentCommands.put("Add", "Add");
    }

    /**
     * Gets the Set of actions this Command class responds to.
     *
     * @return the ActionNames value
     */
    public Set getActionNames() {
        return allJMeterComponentCommands.keySet();
    }

    /**
     * Adds the specified class to the current node of the tree.
     */
    public void doAction(ActionEvent e) {
        try {
            TestElement node = ReportGuiPackage.getInstance()
                    .createTestElement(((JComponent) e.getSource()).getName());
            addObjectToTree(node);
        } catch (Exception err) {
            log.error("", err);
        }
    }

    protected void addObjectToTree(TestElement el) {
        ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
        ReportTreeNode node = new ReportTreeNode(el, guiPackage.getTreeModel());
        guiPackage.getTreeModel().insertNodeInto(node,
                guiPackage.getTreeListener().getCurrentNode(),
                guiPackage.getTreeListener().getCurrentNode().getChildCount());
        TestElement curNode =
            (TestElement)guiPackage.getTreeListener().getCurrentNode().getUserObject();
        if (curNode != null) {
            curNode.addTestElement(el);
            guiPackage.getMainFrame().getTree().setSelectionPath(
                    new TreePath(node.getPath()));
        }
    }
}
