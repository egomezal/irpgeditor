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
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;

/**
 * keeps track of file lines in the source code.
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class NodeFiles extends NodeAbstract implements ListenerParser {
  SourceParser parser;
  FileLine first = null;
  int count = 0;
  TreeModelSourceStructure treeModel;

  public NodeFiles(TreeModelSourceStructure treeModel) {
    this.treeModel = treeModel;
  }

  public void setSourceParser(SourceParser parser) {
    this.parser = parser;
    parser.addListener(this);
  }
  
  /**
   * handles events that occur in the source.
   */
  @SuppressWarnings("rawtypes")
public void parserEvents(ArrayList listEvents) {
    SourceParserEvent event;
    
    for ( int x = 0; x < listEvents.size(); x++ ) {
      event = (SourceParserEvent)listEvents.get(x);
      if ( event.getType() == SourceParserEvent.CHANGED ) {
        changed(event.getLine());
      }
      else if ( event.getType() == SourceParserEvent.ADDED ) {
        added(event.getLine());
      }
      else if ( event.getType() == SourceParserEvent.REMOVED ) {
        removed(event.getLine());
      }
    }
  }
  
  public void added(SourceLine line) {
    if ( isFileLine(line) == false ) {
      return;
    }
    // create a file line and add it to the list.
    if ( first == null ) {
      first = new FileLine(this, line);
      count++;
      // fire event here.
      treeModel.fireInserted(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }, new int[] { count - 1 }, new Object[] { first }));
      return;
    }
    FileLine fileLine = first;
    while ( fileLine.next != null ) {
      fileLine = fileLine.next;
    }
    fileLine.next = new FileLine(this, line);
    count++;
    fileLine.next.previous = fileLine;
    // fire event here.
    treeModel.fireInserted(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }, new int[] { count - 1 }, new Object[] { fileLine.next }));
  }
  
  public void removed(SourceLine line) {
    FileLine fileLine;
    
    fileLine = first;
    while ( fileLine != null ) {
      if ( fileLine.line == line ) {
        // remove this line.
        if ( fileLine.previous == null ) {
          // this is the first item in the list.
          if ( fileLine.next == null ) {
            first = null;
          }
          else {
            first = fileLine.next;
            first.previous = null;
          }
          count--;
          treeModel.fireRemoved(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }, new int[] { count }, new Object[] { fileLine }));
          // fire event here.
          return;
        }
        fileLine.previous.next = fileLine.next;
        if ( fileLine.next != null ) {
          fileLine.next.previous = fileLine.previous;
        }
        count--;
        treeModel.fireRemoved(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }, new int[] { count }, new Object[] { fileLine }));
        return;
      }
      fileLine = fileLine.next;
    }
  }
  
  public void changed(SourceLine line) {
    // changed from not file to file or
    // changed from file to not file?
    FileLine fileLine;
    
    fileLine = getFileLine(line);
    if ( fileLine == null ) {
      // this line is not already a file line.
      // see if it is now a file line.
      added(line);
    }
    else {
      // this line is aready a file line
      // if it is no longer a file line then it needs to be removed.
      if ( isFileLine(line) == false ) {
        removed(line);
      }
      else {
        treeModel.fireStructureChanged(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }, new int[] { getIndex(fileLine) }, new Object[] { fileLine }));
      }
    }
  }
  
  protected int getIndex(FileLine fileLine) {
    int index = 0;
    FileLine line = first;
    while ( line != null ) {
      if ( line == fileLine ) {
        return index;
      }
      line = line.next;
      index++;
    }
    return index;
  }
  
  protected FileLine getFileLine(SourceLine line) {
    FileLine fileLine = first;
    while ( fileLine != null ) {
      if ( fileLine.line == line ) {
        return fileLine;
      }
      fileLine = fileLine.next;
    }
    return null;
  }
  
  /**
   * returns true if the block type changed.
   * otherwise false.
   */
  protected boolean isFileLine(SourceLine line) {
    StringBuffer source;
    //String buffer;
    char c;
    int start;
    
    source = line.getSourceParser().getText();
    start = line.getStart();
    if ( start + 6 >= source.length() ) {
      return false;
    }
    if ( source.charAt(start) == '*' &&
         source.charAt(start + 1) == '*' ) {
      return false;
    }
    c = source.charAt(start + 6);
    // is comment
    if ( c == '*' ) {
      return false;
    }
    
    c = source.charAt(start + 5);
    if ( c == 'F' || c == 'f' ) {
      if ( line.get(LinePosition.D_NAME).length() > 0 ) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * returns the child for the object specified.
   */
  public Object getChild(int index) {
    FileLine fileLine;

    fileLine = first;
    while ( index > 0 && fileLine != null ) {
      fileLine = fileLine.next;
      index--;
    }
    return fileLine;
  }

  /**
   * return the child count for a given parent.
   */
  public int getChildCount() {
    FileLine fileLine;
    int count;

    fileLine = first;
    count = 0;
    while ( fileLine != null ) {
      count++;
      fileLine = fileLine.next;
    }
    return count;
  }

  /**
   * return the index of the child.
   */
  public int getIndexOfChild(Object child) {
    FileLine fileLine;
    int index;

    fileLine = first;
    if ( fileLine == null ) {
      return -1;
    }
    index = 0;
    while ( fileLine != child ) {
      fileLine = fileLine.next;
      if ( fileLine == null ) {
        return -1;
      }
      index++;
    }
    return index;
  }

  /**
   * returns true if the object has no children.
   */
  public boolean isLeaf() {
    if ( first == null ) {
      return true;
    }
    return false;
  }

  public Icon getIcon() {
    return Icons.iconFiles;
  }

  public String getText() {
    return "Files";
  }
  
  public Node getParent() {
    return null;
  }
}

/**
 * represents a single line in the source code that is a file.
 */
class FileLine extends NodeAbstract {
  NodeFiles nodeFiles;
  SourceLine line;
  FileLine previous, next;
  
  public FileLine(NodeFiles nodeFiles, SourceLine line) {
    this.nodeFiles = nodeFiles;
    this.line = line;
  }

  public Icon getIcon() {
    return Icons.iconMember;
  }

  public String getText() {
    return line.get(LinePosition.F_NAME);
  }

  public void selected() {
    line.requestFocus();
  }
  
  public void rightClick(Component invoker, int x, int y) {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem menuFileRemove = new JMenuItem();
    JMenuItem menuFileLayout = new JMenuItem();
    JMenuItem menuFileFind = new JMenuItem();
    
    menuFileRemove.setText("Remove");
    menuFileLayout.setText("Layout");
    menuFileFind.setText("Find");
    popupMenu.add(menuFileRemove);
    popupMenu.add(menuFileLayout);
    popupMenu.add(menuFileFind);
    
    menuFileLayout.addActionListener(new ActionLayout());
    menuFileFind.addActionListener(new ActionFind());
    menuFileRemove.addActionListener(new ActionRemove());
    
    popupMenu.show(invoker, x, y);
  }
  
  public Node getParent() {
    return nodeFiles;
  }
  
  /**
   * shows a layout of the file selected in the tree structure.
   */
  class ActionLayout implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      Environment.layout.open(new LayoutRequest(line.get(LinePosition.F_NAME)));
    }
  }
  
  /**
   * executes sql that displays what libraries the file exist in.
   */
  class ActionFind implements ActionListener {
    @SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent evt) {
      Environment.sql.executeSQL("select distinct table_schema from syscolumns where table_name = '" + line.get(LinePosition.D_NAME).toUpperCase() + "'");
    }
  }
  
  /**
   * removes the selected file line from the source member.
   */
  class ActionRemove implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      if ( JOptionPane.showConfirmDialog(null, "Are You Sure?", "Remove File Reference", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION ) {
        return;
      }
      line.delete();
    }
  }
}

