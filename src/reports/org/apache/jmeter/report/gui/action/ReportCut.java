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
import java.util.HashSet;
import java.util.Set;

import org.apache.jmeter.gui.ReportGuiPackage;
import org.apache.jmeter.report.gui.action.AbstractAction;
import org.apache.jmeter.report.gui.tree.ReportTreeNode;

public class ReportCut extends AbstractAction {
    public static final String CUT = "Cut";//$NON-NLS-1$

    private static final Set commands = new HashSet();
    static {
        commands.add(CUT);
    }

    /**
     * @see org.apache.jmeter.gui.action.Command#getActionNames()
     */
    public Set getActionNames() {
        return commands;
    }

    /**
     * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
     */
    public void doAction(ActionEvent e) {
        ReportGuiPackage guiPack = ReportGuiPackage.getInstance();
        ReportTreeNode[] currentNodes = guiPack.getTreeListener().getSelectedNodes();

        ReportCopy.setCopiedNodes(currentNodes);
        for (int i = 0; i < currentNodes.length; i++) {
            guiPack.getTreeModel().removeNodeFromParent(currentNodes[i]);
        }
        guiPack.getMainFrame().repaint();
    }
}