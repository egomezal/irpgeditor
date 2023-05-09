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

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.icons.*;

/**
 *
 * @author Derek Van Kooten.
 */
public class NodeDspf extends NodeSourceBlocks {

    public NodeDspf(TreeModelSourceStructure treeModel) {
        super(treeModel);
    }

    /**
     * handles events that occur in the source.
     *
     * @param line
     */
    /*  public void parserEvents(ArrayList listEvents) {
    SourceParserEvent event;

    changed = false;
    for ( int x = 0; x < listEvents.size(); x++ ) {
      event = (SourceParserEvent)listEvents.get(x);
      if ( event.type == SourceParserEvent.CHANGED ) {
        changed(event.line);
      }
      else if ( event.type == SourceParserEvent.ADDED ) {
        added(event.line);
      }
      else if ( event.type == SourceParserEvent.REMOVED ) {
        removed(event.line);
      }
    }
    if ( changed ) {
      shuffle();
      fireStructureChanged();
    }
  }*/
    @Override
    public void added(SourceLine line) {
        SourceLine next, end, temp;
        SourceBlock block;

        // was a record added?
        if (isRecord(line)) {
            line.type = SourceLine.TYPE_SCREEN;
            line.position = SourceLine.POSITION_START;
            changed = true;
            // find the end for this start.
            // the end will either be the last line because there isnt an end yet.
            // or it will be the line right before a start, because there wasnt a start before that line yet.
            // or it will be an existing end, which means, the start of that block will take
            // the line right before this line as its end.
            end = line.getNext();
            if (end != null) {
                next = end.getNext();
                while (next != null && isStart(next) == false) {
                    end = next;
                    next = next.getNext();
                }
                end.type = SourceLine.TYPE_SCREEN;
                end.position = SourceLine.POSITION_END;
                // keep track of the block so that if one exists, then it can get a new end line.
                // is the end line found is part of an existing block?
                if (end.block != null && end.block.lineStart != null) {
                    // is an endline for an existing block.
                    // the existing block must find a new endline.
                    temp = end.block.lineStart;
                    next = temp.getNext();
                    while (next != null && isStart(next) == false) {
                        temp = next;
                        next = next.getNext();
                    }
                    // temp will be the new end block for the existing block that is
                    // having its end block taken away.
                    temp.block = end.block;
                    temp.block.lineEnd = temp;
                    temp.type = SourceLine.TYPE_SCREEN;
                    temp.position = SourceLine.POSITION_END;
                }
            }
            new SourceBlock(line, end);
        } else {
            next = line.getNext();
            // is it an end of record?
            if (next == null || isStart(next)) {
                // this is an end record only if there is a start record before it.
                SourceLine parent = line.getParent();
                while (parent != null) {
                    if (isRecord(parent)) {
                        // ok, an end was added, for an existing start.
                        // remove the previous end for that block and
                        // use this one.
                        block = parent.block;
                        // the other line end cant refer the block anymore.
                        if (block.lineEnd != null) {
                            block.lineEnd.block = null;
                            block.lineEnd.type = SourceLine.TYPE_NONE;
                        }
                        block.lineEnd = line;
                        line.block = block;
                        line.type = SourceLine.TYPE_SCREEN;
                        line.position = SourceLine.POSITION_END;
                        changed = true;
                        return;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    @Override
    public void removed(SourceLine line) {
        if (line.type != SourceLine.TYPE_SCREEN) {
            return;
        }
        line.type = SourceLine.TYPE_NONE;
        SourceLine parent = line.getParent();
        if (line.position == SourceLine.POSITION_START) {
            // if there is a start before this one, then
            // it gets this line's end.
            // and its end goes away.
            // other wise, the block and lines both go away.
            while (parent != null && isStart(parent) == false) {
                parent = parent.getParent();
            }
            if (parent == null) {
                // set the lineend to nothing.
                line.block.lineEnd.type = SourceLine.TYPE_NONE;
                line.block.lineEnd.block = null;
                line.block.lineEnd = null;
                line.block = null;
                changed = true;
                return;
            }
            // the lineend for the parent is no longer the lineend.
            parent.block.lineEnd.type = SourceLine.TYPE_NONE;
            parent.block.lineEnd.block = null;
            parent.block.lineEnd = line.block.lineEnd;
            parent.block.lineEnd.block = parent.block;
        } else {
            // the line above this one becomes the new end line unless the one
            // above is a line start.
            if (isStart(parent)) {
                line.block.lineEnd = null;
                line.block = null;
            } else {
                line.block.lineEnd = parent;
                parent.block = line.block;
                parent.type = SourceLine.TYPE_SCREEN;
                parent.position = SourceLine.POSITION_END;
            }
        }
        changed = true;
    }

    @Override
    public void changed(SourceLine line) {
        if (isRecord(line)) {
            // if it wasnt a record before.
            if (line.type == SourceLine.TYPE_NONE) {
                added(line);
            }
        } else {
            // if it was a record before.
            if (line.type == SourceLine.TYPE_SCREEN && line.position == SourceLine.POSITION_START) {
                removed(line);
            }
        }
    }

    /**
     * returns true if this is a dspf record.
     *
     * @param line
     * @return
     */
    public boolean isRecord(SourceLine line) {
        StringBuffer source;
        char c;

        source = line.getSourceParser().getText();

        if (line.getStart() + 6 >= source.length()) {
            return false;
        }
        if (source.charAt(line.getStart()) == '*'
                && source.charAt(line.getStart() + 1) == '*') {
            return false;
        }
        c = source.charAt(line.getStart() + 6);
        // is comment, directive, sql?
        if (c == '/' || c == '+') {
            return false;
        }

        c = source.charAt(line.getStart() + 5);
        if (c == 'A' || c == 'a') {
            if (source.length() <= line.getStart() + 16) {
                return false;
            }
            c = source.charAt(line.getStart() + 16);
            if (c == 'R' || c == 'r') {
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if this is a dspf record.
     *
     * @param line
     * @return
     */
    public boolean isStart(SourceLine line) {
        return line.position == SourceLine.POSITION_START && line.type == SourceLine.TYPE_SCREEN;
    }

    @Override
    public Icon getIcon() {
        return Icons.iconStructure;
    }

    @Override
    public String getText() {
        return "Structure";
    }
}
