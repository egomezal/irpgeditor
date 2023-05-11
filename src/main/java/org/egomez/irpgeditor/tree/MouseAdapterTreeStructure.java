package org.egomez.irpgeditor.tree;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * listens for mouse double clicks on a structure node.
 */
public class MouseAdapterTreeStructure extends MouseAdapter {

    JTree tree;

    public MouseAdapterTreeStructure(JTree tree) {
        this.tree = tree;
        tree.addMouseListener(this);
    }

    
    @Override
    public void mouseClicked(MouseEvent evt) {
        TreePath path;
        Object selected;

        if (evt.getClickCount() == 1) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                path = tree.getClosestPathForLocation(evt.getX(), evt.getY());
                if (path == null) {
                    return;
                }
                selected = path.getLastPathComponent();
                if (selected == null) {
                    return;
                }
                tree.setSelectionPath(path);
                ((Node) selected).rightClick(tree, evt.getX(), evt.getY());
            }
        } else if (evt.getClickCount() > 1) {
            path = tree.getSelectionPath();
            if (path == null) {
                return;
            }
            selected = path.getLastPathComponent();
            if (selected == null) {
                return;
            }
            ((Node) selected).selected();
        }
    }
}
