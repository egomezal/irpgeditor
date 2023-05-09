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

/**
 * a tree node
 * 
 * @author Derek Van Kooten.
 */
public interface Node {
  /**
   * returns the child for the object specified.
   */
  public Object getChild(int index);

  /**
   * return the child count for a given parent.
     * @return 
   */
  public int getChildCount();

  /**
   * return the index of the child.
     * @param child
     * @return 
   */
  public int getIndexOfChild(Object child);

  /**
   * returns true if the object has no children.
     * @return 
   */
  public boolean isLeaf();
  
  public Icon getIcon();
  
  public String getText();
  
  public String getToolTipText();
  
  public void selected();
  
  public void rightClick(Component invoker, int x, int y);
  
  public boolean isSelected();
  
  public boolean isCheckBox();
  
  public Node getParent();
}
