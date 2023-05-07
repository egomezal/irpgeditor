package org.egomez.irpgeditor;

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
/**
 * Events that occur as text is removed, added or changed in the editor.
 * Multiple events are usually grouped together. For example, a user selects and
 * highlights multiple lines in the editor, then pastes in text over the
 * selected area and the pasted text contains multiple lines. This will cause
 * multiple REMOVED events and ADDED events, and possibly CHANGED event to
 * occur. All the events will be grouped together in an array list and passed to
 * a handler.
 *
 * @author Derek Van Kooten.
 */
public class SourceParserEvent {

    /**
     * a line was added from the editor.
     */
    public static int ADDED = 0;
    /**
     * a line was removed from the editor.
     */
    public static int REMOVED = 1;
    /**
     * a line was changed in the editor.
     */
    public static int CHANGED = 2;

    int type;
    SourceLine line;

    public SourceParserEvent(int type, SourceLine line) {
        this.type = type;
        this.line = line;
    }

    public int getType() {
        return type;
    }

    public SourceLine getLine() {
        return line;
    }
}
