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
 * Converts all the lines to commented.
 *
 * @author Derek Van Kooten.
 */
public class RefactorComment extends Refactor {

    public void process(SourceLine line) {
        char c;
        StringBuffer text;

        if (line.getLength() < 7) {
            return;
        }
        c = line.charAt(6);
        if (c == '*') {
            return;
        }
        text = new StringBuffer(line.getText());
        text.replace(6, 7, "*");
        line.setText(text.toString());
    }
}
