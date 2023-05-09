package org.egomez.irpgeditor.tree;

import javax.swing.event.*;

/**
 * @author Derek Van Kooten.
 */
public class TreeExpansionListenerNode implements TreeExpansionListener {

    @Override
    public void treeCollapsed(TreeExpansionEvent evt) {
        Object object;

        object = evt.getPath().getLastPathComponent();
        if (object instanceof NodeAbstract) {
            ((NodeAbstract) object).collapse();
        }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent evt) {
        Object object;

        object = evt.getPath().getLastPathComponent();
        if (object instanceof NodeAbstract) {
            ((NodeAbstract) object).expand();
        }
    }
}
