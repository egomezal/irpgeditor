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

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * basic tree model that stores a list of nodes and returns them as children.
 * 
 * @author Derek Van Kooten.
 */
public class TreeModelNode implements TreeModel {
  Node nodeRoot;
  @SuppressWarnings("rawtypes")
ArrayList listListeners = new ArrayList();

  public TreeModelNode() {
    nodeRoot = new NodeDefault();
  }
  
  public TreeModelNode(Node nodeRoot) {
    this.nodeRoot = nodeRoot;
  }
  
  public void setRoot(Node nodeRoot) {
    this.nodeRoot = nodeRoot;
    structureChanged();
  }
  
  public void createRoot() {
    nodeRoot = new NodeDefault();
    structureChanged();
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void structureChanged(Node node) {
    fireEvent(new TreeModelEvent(this, getPath(node)));
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void structureChanged(Object[] path) {
    fireEvent(new TreeModelEvent(this, path));
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void structureChanged() {
    fireEvent(new TreeModelEvent(this, new Object[] {nodeRoot}));
  }
  
  @SuppressWarnings("rawtypes")
public void fireEvent(final TreeModelEvent evt) {
    final ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }

    temp = (ArrayList)listListeners.clone();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for ( int x = 0; x < temp.size(); x++ ) {
          ((TreeModelListener)temp.get(x)).treeStructureChanged(evt);
        }
      }
    });
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void nodeChanged(Node node) {
    fireEvent(new TreeModelEvent(this, getPath(node)));
  }

  /**
   * gets called when the source line data changes.
   */
  public void nodeChanged(Object[] path) {
    fireEvent(new TreeModelEvent(this, path));
  }

  /**
   * gets called when the source line data changes.
   */
  public void nodeChanged() {
    fireEvent(new TreeModelEvent(this, new Object[] {nodeRoot}));
  }
  
  @SuppressWarnings("rawtypes")
public void fireEventChanged(final TreeModelEvent evt) {
    final ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }

    temp = (ArrayList)listListeners.clone();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for ( int x = 0; x < temp.size(); x++ ) {
          ((TreeModelListener)temp.get(x)).treeNodesChanged(evt);
        }
      }
    });
  }

  /**
   * listens for events to this model.
   */
  @SuppressWarnings("unchecked")
public void addTreeModelListener(TreeModelListener l) {
    listListeners.add(l);
  }

  /**
   * returns the child for the object specified.
   */
  public Object getChild(Object parent, int index) {
    return ((Node)parent).getChild(index);
  }

  /**
   * return the child count for a given parent.
   */
  public int getChildCount(Object parent) {
    return ((Node)parent).getChildCount();
  }

  /**
   * return the index of the child.
   */
  public int getIndexOfChild(Object parent, Object child) {
    return ((Node)parent).getIndexOfChild(child);
  }

  /**
   * returns the root.
   */
  public Object getRoot() {
    return nodeRoot;
  }

  /**
   * returns true if the object has no children.
   */
  public boolean isLeaf(Object node) {
    return ((Node)node).isLeaf();
  }

  /**
   * listens to events in this model.
   */
  public void removeTreeModelListener(TreeModelListener l) {
    listListeners.remove(l);
  }

  /**
   * dont use.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
public Object[] getPath(Node node) {
    ArrayList list;
    
    list = new ArrayList();
    list.add(node);
    node = node.getParent();
    while ( node != null ) {
      list.add(0, node);
      node = node.getParent();
    }
    // if the last node is not the root, then the node doesnt know about
    // the root and it needs to be added in.
    if ( list.get(0) != nodeRoot ) {
      list.add(0, nodeRoot);
    }
    return list.toArray();
  }
}
