/*
 * Copyright 2005-2006 Sun Microsystems, Inc.  All Rights Reserved.
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

import javax.swing.*;

import static javax.swing.JLayeredPane.*;
import static sun.tools.jconsole.Resources.*;

/**
 * Used instead of JDialog in a JDesktopPane/JInternalFrame environment.
 */
@SuppressWarnings("serial")
public class InternalDialog extends JInternalFrame {
    protected JLabel statusBar;

    public InternalDialog(JConsole jConsole, String title, boolean modal) {
        super(title, true, true, false, false);

        setLayer(PALETTE_LAYER);
        putClientProperty("JInternalFrame.frameType", "optionDialog");



        getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
                if (statusBar != null) {
                    statusBar.setText("");
                }
            }
        });
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
    }

    public void setLocationRelativeTo(Component c) {
        setLocation((c.getWidth()  - getWidth())  / 2,
                    (c.getHeight() - getHeight()) / 2);
    }

    protected class MastheadIcon implements Icon {
        // Important: Assume image background is white!
        private ImageIcon leftIcon =
            new ImageIcon(InternalDialog.class.getResource("resources/masthead-left.png"));
        private ImageIcon rightIcon =
            new ImageIcon(InternalDialog.class.getResource("resources/masthead-right.png"));

        private Font font = Font.decode(getText("Masthead.font"));
        private int gap = 10;
        private String title;

        public MastheadIcon(String title) {
            this.title = title;
        }

        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            // Clone the Graphics object
            g = g.create();

            // Ignore x to make sure we fill entire component width
            x = 0;
            int width = c.getWidth();
            int lWidth = leftIcon.getIconWidth();
            int rWidth = rightIcon.getIconWidth();
            int height = getIconHeight();
            int textHeight = g.getFontMetrics(font).getAscent();

            g.setColor(Color.white);
            g.fillRect(x, y, width, height);

            leftIcon.paintIcon(c, g, x, y);
            rightIcon.paintIcon(c, g, width - rWidth, y);

            g.setFont(font);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(0x35556b));
            g.drawString(title, lWidth + gap, height/2 + textHeight/2);
        }

        public int getIconWidth() {
            int textWidth = 0;
            Graphics g = getGraphics();
            if (g != null) {
                FontMetrics fm = g.getFontMetrics(font);
                if (fm != null) {
                    textWidth = fm.stringWidth(title);
                }
            }
            return (leftIcon.getIconWidth() + gap + textWidth +
                    gap + rightIcon.getIconWidth());
        }


        public int getIconHeight() {
            return leftIcon.getIconHeight();
        }
    }
}
