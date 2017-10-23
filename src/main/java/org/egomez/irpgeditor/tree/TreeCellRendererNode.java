package org.egomez.irpgeditor.tree;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * 
 * @author Derek Van Kooten.
 */
public class TreeCellRendererNode extends DefaultTreeCellRenderer {
  /**
	 * 
	 */
	private static final long serialVersionUID = -4723707844569600842L;
JPanel panel = new JPanel(new BorderLayout(0, 0));
  JCheckBox checkBox = new JCheckBox();
  JLabel label = new JLabel();
  Color textBackground = UIManager.getColor("Tree.textBackground");
  Color textForeground;
  Color selectionBackground = UIManager.getColor("Tree.selectionBackground");

  public TreeCellRendererNode() {
    checkBox.setBackground(textBackground);
    label.setBackground(textBackground);
    textForeground = label.getForeground();
    panel.setBackground(textBackground);
    panel.add(checkBox, BorderLayout.WEST);
    panel.add(label, BorderLayout.EAST);
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    if ( value == null || value instanceof Node == false) {
      return super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
    }
    Node node = (Node)value;
    if ( node.isCheckBox() ) {
      panel.setEnabled(tree.isEnabled());
      if ( isSelected ) {
        panel.setBackground(selectionBackground);
        label.setForeground(textBackground);
      }
      else {
        panel.setBackground(textBackground);
        label.setForeground(textForeground);
      }
      checkBox.setSelected(node.isSelected());
      label.setText(node.getText());
      label.setIcon(node.getIcon());
      return panel;
    }
    JLabel label2 = (JLabel)super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
    label2.setText(((Node)value).getText());
    label2.setIcon(((Node)value).getIcon());
    String tooltip = ((Node)value).getToolTipText();
    if ( tooltip == null ) {
      label2.setToolTipText("");
    }
    else {
      label2.setToolTipText(tooltip);
    }
    return label2;
  }
}
