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

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;

/**
 * listens for when source blocks change.
 * listens for when lines are added and removed.
 * lines to lines so that files and such can be displayed.
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class TreeModelSourceStructure implements TreeModel, ListenerLineParser {
  private ProjectMember projectMember;
  Node nodeRoot = new NodeRoot();
  @SuppressWarnings("rawtypes")
ArrayList listListeners = new ArrayList();
  @SuppressWarnings("rawtypes")
ArrayList listParsers = new ArrayList();
  
  public TreeModelSourceStructure() {
  }
  
  public void setProjectMember(ProjectMember projectMember) {
    this.projectMember = projectMember;
  }
  
  public ProjectMember getProjectMember() {
    return projectMember;
  }
  
  /**
   * adds a node that parses source lines and displays some nodes based on
   * source code.
   * 
   * @param node Node
   */
  @SuppressWarnings("unchecked")
public void addParser(Node node) {
    listParsers.add(node);
  }
  
  /**
   * gets called when a file line is added to the source code.
   * 
   * @param line RPGSourceLine
   */
  public void added(SourceLine line) {
  }
  
  /**
   * gets called when a file line is removed from the source code.
   * 
   * @param line RPGSourceLine
   */
  public void removed(SourceLine line) {
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void structureChanged() {
    fireStructureChanged(new TreeModelEvent(this, new Object[] {nodeRoot}));
  }

  @SuppressWarnings("rawtypes")
public void fireStructureChanged(final TreeModelEvent evt) {
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

  @SuppressWarnings("rawtypes")
public void fireRemoved(final TreeModelEvent evt) {
    final ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }

    temp = (ArrayList)listListeners.clone();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for ( int x = 0; x < temp.size(); x++ ) {
          ((TreeModelListener)temp.get(x)).treeNodesRemoved(evt);
        }
      }
    });
  }

  @SuppressWarnings("rawtypes")
public void fireInserted(final TreeModelEvent evt) {
    final ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }

    temp = (ArrayList)listListeners.clone();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for ( int x = 0; x < temp.size(); x++ ) {
          ((TreeModelListener)temp.get(x)).treeNodesInserted(evt);
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
  
  /**
   * root node.
   */
  class NodeRoot extends NodeAbstract {
    /**
     * returns the child for the object specified.
     */
    public Object getChild(int index) {
      return listParsers.get(index);
    }
  
    /**
     * return the child count for a given parent.
     */
    public int getChildCount() {
      return listParsers.size();
    }
  
    /**
     * return the index of the child.
     */
    public int getIndexOfChild(Object child) {
      return listParsers.indexOf(child);
    }
    
    public Node getParent() {
      return null;
    }
  
    /**
     * returns true if the object has no children.
     */
    public boolean isLeaf() {
      if ( listParsers.size() == 0 ) {
        return true;
      }
      return false;
    }
  
    public Icon getIcon() {
      if ( projectMember == null ) {
        return null;
      }
      return projectMember.getIcon();
    }
  
    public String getText() {
      if ( projectMember == null ) {
        return "";
      }
      return projectMember.getMember().getName();
    }
  
    public String getToolTipText() {
      if ( projectMember == null ) {
        return null;
      }
      Member member = projectMember.getMember();
      AS400System system = member.getSystem();
      return system.getName() + " - " + member.getLibrary() + " - " + member.getFile() + " - " + member.getName();
    }
  }
}


