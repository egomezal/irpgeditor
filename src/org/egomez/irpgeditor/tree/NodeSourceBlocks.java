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
import javax.swing.event.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;

/**
 * 
 * @author Derek Van Kooten.
 */
abstract public class NodeSourceBlocks extends NodeAbstract implements ListenerParser {
  protected SourceParser parser;
  protected SourceBlock first;
  protected ArrayList listListeners = new ArrayList();
  protected boolean changed;
  protected TreeModelSourceStructure treeModel;

  public NodeSourceBlocks(TreeModelSourceStructure treeModel) {
    this.treeModel = treeModel;
  }

  public void setSourceParser(SourceParser parser) {
    this.parser = parser;
    parser.addListener(this);
  }

  public void addListener(ListenerLineParser l) {
    listListeners.add(l);
  }

  protected void fireStructureChanged() {
    ArrayList temp;

    if ( listListeners.size() == 0 ) {
      return;
    }
    temp = (ArrayList)listListeners.clone();
    for ( int x = 0; x < temp.size(); x++ ) {
      ((ListenerLineParser)temp.get(x)).structureChanged();
    }
  }

  /**
   * handles events that occur in the source.
   */
  public void parserEvents(ArrayList listEvents) {
    SourceParserEvent event;
    SourceBlock blockClone;

    changed = false;
    blockClone = clone(first, null);
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
    shuffle();
    if ( changed ) {
      // compare differences and fire change events.
      //fireStructureChanged();
      compareChanged(blockClone, first, null);
    }
  }

  abstract public void added(SourceLine line);
  abstract public void removed(SourceLine line);
  abstract public void changed(SourceLine line);

  protected void compareChanged(SourceBlock blockBeforeFirst, SourceBlock blockAfter, SourceBlock blockAfterParent) {
    SourceBlock blockFound, blockSearch, block, blockAdd, blockBefore;
    int index = 0;

    blockBefore = blockBeforeFirst;
    blockSearch = blockAfter;
    while ( blockBefore != null ) {
      // try to find this block in the new tree.
      blockFound = null;
      block = blockSearch;
      searchloop:
      while ( block != null ) {
        if ( blockBefore.original == block ) {
          blockFound = block;
          // block was found, are there any blocks added inbetween
          // this block and the previous found block?
          blockAdd = blockSearch;
          while ( blockAdd != blockFound ) {
            treeModel.fireInserted(getTreeModelEvent(buildPath(blockAdd, false), index, blockAdd));
            index++;
            blockAdd = blockAdd.sibling;
          }
          blockSearch = blockFound.sibling;
          // since blockBefore and block have not changed, its safe to check if any of their
          // children have changed.
          compareChanged(blockBefore.child, block.child, block);
          break searchloop;
        }
        block = block.sibling;
      }
      // was a match not found?
      if ( blockFound == null ) {
        // then this block was removed.
        treeModel.fireRemoved(getTreeModelEvent(buildPath(blockAfterParent, true), index, blockBefore));
        index--;
      }
      blockBefore = blockBefore.sibling;
      index++;
    }
    // check for more in blockAfter after the end of blockBefore 
    while ( blockSearch != null ) {
      // each one of these blocks is new and added.
      treeModel.fireInserted(getTreeModelEvent(buildPath(blockSearch, false), index, blockSearch));
      index++;
      blockSearch = blockSearch.sibling;
    }
  }

  protected TreeModelEvent getTreeModelEvent(Object[] path, int index, SourceBlock block) {
    return new TreeModelEvent(treeModel, path, new int[] { index }, new Object[] { block });
  }

  protected Object[] buildPath(SourceBlock block, boolean include) {
    ArrayList list;
    SourceBlock b;

    b = block;
    list = new ArrayList();
    list.add(treeModel.getRoot());
    list.add(this);
    if ( block != null ) {
      if ( include ) {
        list.add(2, block);
      }
      block = block.parent;
    }
    while ( block != null ) {
      list.add(2, block);
      block = block.parent;
    }
    return list.toArray();
  }

  protected int indexOfParent(SourceBlock first, SourceBlock block) {
    SourceBlock b;
    int index;

    if ( block.parent == null ) {
      b = first;
    }
    else {
      b = block.parent.child;
    }
    index = 0;
    while ( b != null ) {
      if ( b == block ) {
        return index;
      }
      b = b.sibling;
      index++;
    }
    return -1;
  }

  /**
   * sets up the parent, child, sibling links in the blocks.
   */
  protected void shuffle() {
    SourceLine line;
    SourceBlock current, previous, block;
    boolean foundEnd;

    current = null;
    previous = null;
    first = null;
    line = parser.getFirst();
    while ( line != null && first == null ) {
      if ( line.block != null ) {
        line.block.parent = null;
        line.block.child = null;
        line.block.sibling = null;
        first = line.block;
        // is this the end or the start of the block?
        if ( line.position == SourceLine.POSITION_START ) { 
          current = first;
        }
        else {
          previous = first;
        }
      }
      line = line.getNext();
    }
    while ( line != null ) {
      if ( line.block != null ) {
        // is this the close of the current block or a parent block?
        foundEnd = false;
        block = current;
        while ( block != null ) {
          if ( line.block == block ) {
            // this is the end of the current block
            previous = block;
            current = block.parent;
            foundEnd = true;
            break;
          }
          block = block.parent;
        }
        if ( foundEnd == false ) {
          // this is the start of a new block.
          line.block.parent = current;
          line.block.child = null;
          line.block.sibling = null;

          // siblings?
          if ( previous == null ) {
            // this is the first child.
            if ( current != null ) {
              current.child = line.block;
            }
          }
          else {
            previous.sibling = line.block;
          }

          // is line the first or last in the block?
          // it could be the last if the block has no first defined.
          if ( line.position == SourceLine.POSITION_START ) {
            current = line.block;
            previous = null;
          }
          else {
            previous = line.block;
          }
        }
      }
      line = line.getNext();
    }
  }

  protected SourceBlock clone(SourceBlock blockFirst, SourceBlock blockParent) {
    SourceBlock blockCloneFirst, blockClone, block;
    int count = 1;

    if ( blockFirst == null ) {
      return null;
    }
    blockClone = (SourceBlock)blockFirst.clone();
    blockClone.parent = blockParent;
    blockClone.child = clone(blockFirst.child, blockClone);
    blockCloneFirst = blockClone;
    block = blockFirst.sibling;
    while ( block != null ) {
      blockClone.sibling = (SourceBlock)block.clone();
      blockClone = blockClone.sibling;
      blockClone.parent = blockParent;
      blockClone.child = clone(block.child, blockClone);
      block = block.sibling;
      count++;
    }
    return blockCloneFirst;
  }

  protected TreeModelEvent buildEvent(SourceBlock block) {
    return new TreeModelEvent(treeModel, getPath(block), new int[] { getIndexOfChild(block) }, new Object[] { block });
  }

  protected Object[] getPath(SourceBlock block) {
    SourceBlock b;
    int size;
    Object[] path;

    size = 0;
    b = block;
    while ( b.parent != null ){
      size++;
      b = b.parent;
    }
    path = new Object[size + 2];
    path[0] = treeModel.getRoot();
    path[1] = this;
    while ( size > 0 ) {
      path[size + 1] = block.parent;
      size--;
      block = block.parent;
    }
    return path;
  }

  /**
   * returns the child for the object specified.
   */
  public Object getChild(int index) {
    SourceBlock block;

    block = first;
    while ( index > 0 && block != null ) {
      block = block.sibling;
      index--;
    }
    return block;
  }

  /**
   * return the child count for a given parent.
   */
  public int getChildCount() {
    SourceBlock block;
    int count;

    block = first;
    count = 0;
    while ( block != null ) {
      count++;
      block = block.sibling;
    }
    return count;
  }

  /**
   * return the index of the child.
   */
  public int getIndexOfChild(Object child) {
    SourceBlock block;
    int index = 0;

    block = (SourceBlock)child;
    if ( block.parent != null ) {
      return ((SourceBlock)child).indexOfParent();
    }
    block = first;
    while ( block != null ) {
      if ( block == child ) {
        return index;
      }
      index++;
      block = block.sibling;
    }
    return -1;
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
    return Icons.iconStructure;
  }

  public String getText() {
    return "Structure";
  }

  public Node getParent() {
    return null;
  }
}
