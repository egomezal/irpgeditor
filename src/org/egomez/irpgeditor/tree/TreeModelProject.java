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
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

/**
 * 
 * @author Derek Van Kooten.
 */
public class TreeModelProject implements TreeModel, ListenerProjects, ListenerProject {
  Project project;
  ArrayList listListeners = new ArrayList();

  public TreeModelProject() {
    Environment.projects.addListener(this);
    selected(Environment.projects.getSelected());
  }
  
  /**
   * gets called when the source line data changes.
   */
  public void structureChanged() {
    final ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }

    temp = (ArrayList)listListeners.clone();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for ( int x = 0; x < temp.size(); x++ ) {
          ((TreeModelListener)temp.get(x)).treeStructureChanged(new TreeModelEvent(this, new Object[] {getRoot()}));
        }
      }
    });
  }
  
  public void selected(Project project) {
    if ( this.project != null ) {
      this.project.removeListener(this);
    }
    this.project = project;
    structureChanged();
    if ( project != null ) {
      project.addListener(this);
    }
  }
  
  public void added(Project project, int index) {
  }
  
  public void removed(Project project, int index) {
  }
  
  /**
   * gets called when a member is added to the project.
   * 
   * @param project Project
   * @param member ProjectMember
   */
  public void memberAdded(Project project, ProjectMember member) {
    structureChanged();
  }
  
  /**
   * gets called when a member is removed from the project.
   * 
   * @param project Project
   * @param member ProjectMember
   */
  public void memberRemoved(Project project, ProjectMember member) {
    structureChanged();
  }
  
  /**
   * listens for events to this model.
   */
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
    Object object;
    
    object = Environment.projects.getSelected();
    if ( object == null ) {
      return new NodeDefault();
    }
    return object;
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
}
