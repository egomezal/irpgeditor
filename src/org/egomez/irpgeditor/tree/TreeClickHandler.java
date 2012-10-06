package org.egomez.irpgeditor.tree;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class TreeClickHandler extends MouseAdapter {
  JTree tree;
  ListenerRightClick listenerRightClick;
  
  public TreeClickHandler(JTree tree) {
    this.tree = tree;
    tree.addMouseListener(this);
  }
  
  public TreeClickHandler(JTree tree, ListenerRightClick listenerRightClick) {
    this.tree = tree;
    this.listenerRightClick = listenerRightClick;
    tree.addMouseListener(this);
  }
  
  public void setListenerRightClick(ListenerRightClick listenerRightClick) {
    this.listenerRightClick = listenerRightClick;
  }
  
  public void mouseClicked(MouseEvent evt) {
    TreePath path;
    Object object;

    if ( evt.getClickCount() >= 1 ) {
      if ( evt.getButton() == MouseEvent.BUTTON3 ) {
        // if there is no selection at all then make a selection.
        if ( tree.getSelectionCount() == 0 ) {
          path = tree.getClosestPathForLocation(evt.getX(), evt.getY());
          if ( path == null ) {
            return;
          }
          object = path.getLastPathComponent();
          if ( object == null ) {
            return;
          }
          tree.setSelectionPath(path);
        }
        
        // if there is a right click handler, and at least 1 item is selected
        // see if the handler wants to handle it first before changing the selection
        // to one item.
        if ( listenerRightClick != null ) {
          if ( listenerRightClick.rightClick(evt.getComponent(), evt.getX(), evt.getY()) ) {
            // if a true is returned, then the handler processed the click.
            return;
          }
        }
        
        // if this point was reached, then override what selection there is
        // with the node right clicked on.
        path = tree.getClosestPathForLocation(evt.getX(), evt.getY());
        if ( path == null ) {
          return;
        }
        object = path.getLastPathComponent();
        if ( object == null ) {
          return;
        }
        tree.setSelectionPath(path);
        ((Node)object).rightClick(tree, evt.getX(), evt.getY());
      }
      path = tree.getSelectionPath();
      if ( path == null ) {
        return;
      }
      object = path.getLastPathComponent();
      if ( object instanceof NodeAbstract ) {
        ((NodeAbstract)object).click(evt.getClickCount());
      }
    }
  }
}
