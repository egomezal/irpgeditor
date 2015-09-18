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

/**
 * 
 * @author Derek Van Kooten.
 */
public class NodeDefault extends NodeAbstract {
  protected Node parent = null;
  protected String text = "";
  protected Icon icon = null;
  @SuppressWarnings("rawtypes")
protected ArrayList list = new ArrayList();
  
  public NodeDefault() {
  }
  
  public NodeDefault(String text) {
    this.text = text;
  }
  
  public NodeDefault(String text, Icon icon) {
    this.text = text;
    this.icon = icon;
  }
  
  @SuppressWarnings("rawtypes")
public NodeDefault(ArrayList list) {
    this.list = list;
  }
  
  public NodeDefault(Node parent) {
    this.parent = parent;
  }
  
  public NodeDefault(Node parent, Icon icon) {
    this.parent = parent;
    this.icon = icon;
  }
  
  public NodeDefault(Node parent, String text) {
    this.parent = parent;
    this.text = text;
  }
  
  public NodeDefault(Node parent, String text, Icon icon) {
    this.parent = parent;
    this.text = text;
    this.icon = icon;
  }
  
  @SuppressWarnings("rawtypes")
public NodeDefault(Node parent, ArrayList list) {
    this.parent = parent;
    this.list = list;
  }
  
  @SuppressWarnings("unchecked")
public void add(Node node) {
    list.add(node);
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
  
  public Icon getIcon() {
    return icon;
  }
  
  public void setIcon(Icon icon) {
    this.icon = icon;
  }
  
  /**
   * returns the child for the object specified.
   */
  public Object getChild(int index) {
    return list.get(index);
  }

  /**
   * return the child count for a given parent.
   */
  public int getChildCount() {
    return list.size();
  }

  public Node getParent() {
    return parent;
  }

  /**
   * return the index of the child.
   */
  public int getIndexOfChild(Object child) {
    return list.indexOf(child);
  }

  /**
   * returns true if the object has no children.
   */
  public boolean isLeaf() {
    if ( list.size() == 0 ) {
      return true;
    }
    return false;
  }
}
