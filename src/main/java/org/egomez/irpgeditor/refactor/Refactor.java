package org.egomez.irpgeditor.refactor;

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
import org.egomez.irpgeditor.*;

/**
 * Base class for refactoring code.
 *
 * @author Derek Van Kooten.
 */
public class Refactor {

    SourceLine lineStart, lineEnd;
    SourceParser sourceParser;

    public void refactor(SourceParser sourceParser, int start, int end) {

        this.sourceParser = sourceParser;
        lineStart = sourceParser.getLine(start);
        if (lineStart == null) {
            return;
        }
        lineEnd = sourceParser.getLine(end);
        if (lineEnd == null) {
            return;
        }
        refactor();
    }

    public void refactor() {
        SourceLine line;

        start();
        line = lineStart;
        while (line != null && line.equals(lineEnd) == false) {
            process(line);
            line = line.getNext();
        }
        // the last line wont have been processed, check to see if it needs to
        // be processed.
        if (lineStart.equals(lineEnd) == false && line != null) {
            process(line);
        }
        end();
    }

    public void process(SourceLine line) {
    }

    public void start() {
    }

    public void end() {
    }
}
