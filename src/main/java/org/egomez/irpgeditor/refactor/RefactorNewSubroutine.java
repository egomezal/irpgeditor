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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;

/**
 * Converts the fixed format code to free form.
 *
 * @author Derek Van Kooten.
 */
public class RefactorNewSubroutine extends Refactor {

    StringBuffer buffer = new StringBuffer();
    Logger logger = LoggerFactory.getLogger(RefactorNewSubroutine.class);

    @Override
    public void start() {
        buffer = new StringBuffer();
    }

    /**
     * convert the line if it needs to be.
     *
     * @param line
     */
    @Override
    public void process(SourceLine line) {
        buffer.append(line.getText());
    }

    @Override
    public void end() {
        SourceLine sourceLine, nextLine;
        String subName;
        Document doc;

        // find the last C line and append text to it.
        sourceLine = sourceParser.getFirst();
        if (sourceLine == null) {
            return;
        }
        nextLine = sourceLine.getNext();
        if (nextLine == null) {
            return;
        }
        while (nextLine != null && nextLine.getSpec() != 'o' && nextLine.getSpec() != 'O') {
            sourceLine = nextLine;
            nextLine = nextLine.getNext();
        }
        subName = JOptionPane.showInputDialog(null, "New Subroutine Name?");
        if (subName == null) {
            return;
        }
        buffer.insert(0, "     C                   begsr\n");
        SourceLine.formatText(buffer, LinePosition.C_FACTOR_1, subName);
        buffer.insert(0, "     C*******************************************************\n");
        buffer.insert(0, "     C* " + subName + "\n");
        buffer.insert(0, "     C*******************************************************\n");
        buffer.append("     C                   endsr\n");
        doc = sourceParser.getDocument();
        try {
            doc.insertString(sourceLine.getStart() + sourceLine.getLength(), buffer.toString(), null);
            doc.remove(lineStart.getStart(), (lineEnd.getStart() + lineEnd.getLength()) - lineStart.getStart());
            doc.insertString(lineStart.getStart(), "     C                   exsr      " + subName + "\n", null);
        } catch (BadLocationException e) {
            logger.error(e.getMessage());
            
        }
    }
}
