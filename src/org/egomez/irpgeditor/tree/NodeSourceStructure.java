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

//import java.util.*;
import javax.swing.*;
//import javax.swing.event.*;
import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;

/**
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class NodeSourceStructure extends NodeSourceBlocks implements ListenerParser {
  public NodeSourceStructure(TreeModelSourceStructure treeModel) {
    super(treeModel);
  }
  
  public void added(SourceLine line) {
    // get type and position.
    blockType(line);
    // if is not block
    if ( line.type == 0 ) {
      return;
    }
    processLine(line);
  }
  
  public void removed(SourceLine line) {
    SourceBlock block;
    
    block = line.block;
    // was the line part of a block?
    if ( block == null ) {
      return;
    }
    line.block = null;
    // if is start
    if ( line.position == SourceLine.POSITION_START ) {
      block.lineStart = null;
      if ( block.lineEnd != null ) {
        processEndLine(block.lineEnd);
      }
    }
    // else if is end
    else {
      block.lineEnd = null;
      if ( block.lineStart != null ) {
        processStartLine(block.lineStart);
      }
    }
    changed = true;
  }
  
  public void changed(SourceLine line) {
    int type;
    int position;
    SourceLine other = null;
    SourceBlock block;
    
    // save the type to see if it changed.
    type = line.type;
    position = line.position;
    // get the block type and see if it changed?
    blockType(line);
    
    // did it become a block?
    if ( line.block == null && line.type != 0 ) {
      processLine(line);
    }
    // did it stop being a block?
    else if ( type != 0 && line.type == 0 ) {
      block = line.block;
      line.block = null;
      changed = true;
      // if is start
      if ( position == SourceLine.POSITION_START ) {
        block.lineStart = null;
        if ( block.lineEnd != null && block.lineEnd != line ) {
          processEndLine(block.lineEnd);
        }
      }
      // else if is end
      else {
        block.lineEnd = null;
        if ( block.lineStart != null && block.lineStart != line ) {
          processStartLine(block.lineStart);
        }
      }
    }
    // did it change block type?
    else if ( type != line.type ) {
      // if is start
      if ( line.position == SourceLine.POSITION_START ) {
        // save a reference, because the next method overwrites it.
        other = line.block.lineEnd;
        processStartLine(line);
        if ( other != null ) {
          processEndLine(other);
        }
      }
      // else if is end
      else {
        // save a reference, because the next method overwrites it.
        other = line.block.lineStart;
        processEndLine(line);
        if ( other != null ) {
          processStartLine(other);
        }
      }
    }
    // did it change position?
    else if ( position != line.position ) {
      // if is start
      if ( line.position == SourceLine.POSITION_START ) {
        // save a reference, because the next method overwrites it.
        if ( line.block != null ) {
          other = line.block.lineStart;
        }
        processStartLine(line);
        if ( other != null ) {
          processStartLine(other);
        }
      }
      // else if is end
      else {
        // save a reference, because the next method overwrites it.
        other = line.block.lineEnd;
        processEndLine(line);
        if ( other != null ) {
          processEndLine(other);
        }
      }
    }
  }
  
  protected void processLine(SourceLine line) {
    if ( line.position == SourceLine.POSITION_START ) {
      processStartLine(line);
    }
    // else if is end
    else {
      processEndLine(line);
    }
  }
  
  /**
   * looks for the line end.
   */
  protected void processStartLine(SourceLine lineStart) {
    SourceLine lineEnd, lineOld;
    SourceBlock block;
    
    // does the line no longer exist?
    if ( lineStart.isInvalid() ) {
      return;
    }
    lineEnd = findEnd(lineStart);
    if ( lineEnd == null ) {
      block = new SourceBlock(lineStart, null);
      changed = true;
    }
    else {
      block = lineEnd.block;
      if ( block == null ) {
        block = new SourceBlock(lineStart, lineEnd);
        changed = true;
      }
      else {
        // this line is already in the correct block.
        if ( block.lineStart == lineStart ) {
          return;
        }
        lineOld = block.lineStart;
        block.lineStart = lineStart;
        lineStart.block = block;
        // line end will always have a block, but it might not be closed.
        if ( lineOld != null ) {
          processStartLine(lineOld);
        }
        changed = true;
      }
    }
  }
  
  /**
   * looks for the start end.
   */
  protected void processEndLine(SourceLine lineEnd) {
    SourceLine lineStart, lineOld;
    SourceBlock block;
    
    // does the line no longer exist?
    if ( lineEnd.isInvalid() ) {
      return;
    }
    lineStart = findStart(lineEnd);
    if ( lineStart == null ) {
      block = new SourceBlock(null, lineEnd);
      changed = true;
    }
    else {
      block = lineStart.block;
      if ( block == null ) {
        block = new SourceBlock(lineStart, lineEnd);
        changed = true;
      }
      else {
        // this line is already in the correct block.
        if ( block.lineEnd == lineEnd ) {
          return;
        }
        lineOld = block.lineEnd;
        block.lineEnd = lineEnd;
        lineEnd.block = block;
        // line start will always have a block, but it might not be closed.
        if ( lineOld != null ) {
          processEndLine(lineOld);
        }
        changed = true;
      }
    }
  }
  
  /**
   * finds the start for the end.
   */
  public SourceLine findStart(SourceLine lineEnd) {
    SourceLine line;
    
    line = lineEnd.getParent();
    while ( line != null ) {
      // if is same type? 
      if ( line.type == lineEnd.type ) {
        // is this line a start for the same type?
        if ( line.position == SourceLine.POSITION_START ) {
          return line;
        }
        else {
          // does it have an end? if so goto it?
          if ( line.block == null || line.block.lineStart == null ) {
            // the other one couldnt find an end. so neither will this one.
            return null;
          }
          else {
            line = line.block.lineStart;
          }
        }
      }
      line = line.getParent();
    }
    return null;
  }
  
  /**
   * finds the end for the start.
   */
  public SourceLine findEnd(SourceLine lineStart) {
    SourceLine line;
    
    line = lineStart.getNext();
    while ( line != null ) {
      // if is same type of start 
      if ( line.type == lineStart.type ) {
        // is this line an end for the same type?
        if ( line.position == SourceLine.POSITION_END ) {
          return line;
        }
        else {
          // does it have an end? if so goto it.
          if ( line.block == null || line.block.lineEnd == null ) {
            // the other one couldnt find an end. so neither will this one.
            return null;
          }
          else {
            // skip past this one.
            line = line.block.lineEnd;
          }
        }
      }
      line = line.getNext();
    }
    return null;
  }
  
  /**
   * returns true if the block type changed.
   * otherwise false.
   */
  public void blockType(SourceLine line) {
    StringBuffer source;
    String buffer;
    char c;
    int index;
    
    source = line.getSourceParser().getText();
    
    if ( line.getStart() + 6 >= source.length() ) {
      line.type = SourceLine.TYPE_NONE;
      return;
    }
    if ( source.charAt(line.getStart()) == '*' &&
         source.charAt(line.getStart() + 1) == '*' ) {
      line.type = SourceLine.TYPE_NONE;
      return;
    }
    c = source.charAt(line.getStart() + 6);
    // is comment, directive, sql?
    if ( c == '/' || c == '+' ) {
      line.type = SourceLine.TYPE_NONE;
      return;
    }
    if ( c == '*' ) {
      c = source.charAt(line.getStart() + 5);
      if ( c == 'A' || c == 'a' ) {
        SourceLine next = line.getNext();
        if ( next != null ) {
          c = source.charAt(next.getStart() + 16);
          if ( c != 'R' && c != 'r' ) {
            line.type = SourceLine.TYPE_NONE;
            return;
          }
        }
        // there has to be at least one block start before this for it to qualify.
        next = line.getParent();
        while ( next != null ) {
          if ( next.type == SourceLine.TYPE_SCREEN && next.position == SourceLine.POSITION_START ) {
            line.type = SourceLine.TYPE_SCREEN;
            line.position = SourceLine.POSITION_END;
            return;
          }
          next = next.getParent();
        }
      }
      line.type = SourceLine.TYPE_NONE;
      return;
    }
    
    c = source.charAt(line.getStart() + 5);
    if ( c == 'C' || c == 'c' ) {
      buffer = line.get(LinePosition.C_OPERATION).toLowerCase();
      if ( buffer.equals("begsr") ) {
        line.type = SourceLine.TYPE_SUBROUTINE;
        line.position = SourceLine.POSITION_START;
        return;
      }
      if ( buffer.equals("endsr") ) {
        line.type = SourceLine.TYPE_SUBROUTINE;
        line.position = SourceLine.POSITION_END;
        return;
      }
      line.type = 0;
      line.position = 0;
      return;
    }
    if ( c == 'P' || c == 'p' ) {
      if ( line.getLength() < 25 ) {
        line.type = 0;
        line.position = 0;
        return;
      }
      c = source.charAt(line.getStart() + 23);
      if ( c == 'B' || c == 'b' ) {
        line.type = SourceLine.TYPE_PROCEDURE;
        line.position = SourceLine.POSITION_START;
        return;
      }
      else if ( c == 'E' || c == 'e' ) {
        line.type = SourceLine.TYPE_PROCEDURE;
        line.position = SourceLine.POSITION_END;
        return;
      }
    }
    if ( c == 'A' || c == 'a' ) {
      if ( source.length() <= line.getStart() + 16 ) {
        line.type = 0;
        line.position = 0;
        return;
      }
      c = source.charAt(line.getStart() + 16);
      if ( c == 'R' || c == 'r' ) {
        line.type = SourceLine.TYPE_SCREEN;
        line.position = SourceLine.POSITION_START;
        return;
      }
      SourceLine next = line.getNext();
      if ( next != null ) {
        if ( next.isComment() || next.getLength() < 16 ) {
          line.type = 0;
          line.position = 0;
          return;
        }
        c = source.charAt(next.getStart() + 16);
        if ( c != 'R' && c != 'r' ) {
          line.type = 0;
          line.position = 0;
          return;
        }
      }
      // there has to be at least one block start before this for it to qualify.
      next = line.getParent();
      while ( next != null ) {
        if ( next.type == SourceLine.TYPE_SCREEN && next.position == SourceLine.POSITION_START ) {
          line.type = SourceLine.TYPE_SCREEN;
          line.position = SourceLine.POSITION_END;
          return;
        }
        next = next.getParent();
      }
    }
    // free form?
    if ( c == ' ' ) {
      buffer = source.substring(line.getStart(), line.getStart() + line.getLength()).trim();
      index = buffer.indexOf(' ');
      if ( index > -1 ) {
        buffer = buffer.substring(0, index);
      }
      if ( buffer.equalsIgnoreCase("begsr") ) {
        line.type = SourceLine.TYPE_SUBROUTINE;
        line.position = SourceLine.POSITION_START;
        return;
      }
      if ( buffer.equalsIgnoreCase("endsr;") ) {
        line.type = SourceLine.TYPE_SUBROUTINE;
        line.position = SourceLine.POSITION_END;
        return;
      }
    }
    
    line.type = 0;
    line.position = 0;
  }
  
  public Icon getIcon() {
    return Icons.iconStructure;
  }

  public String getText() {
    return "Structure";
  }
}
