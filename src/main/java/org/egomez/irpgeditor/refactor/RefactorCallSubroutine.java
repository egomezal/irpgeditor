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

import javax.swing.*;
import javax.swing.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the fixed format code to free form.
 *  
 * @author Derek Van Kooten.
 */
public class RefactorCallSubroutine extends Refactor {
  public void start() {
    String subName;
    Document doc;
    final  Logger logger = LoggerFactory.getLogger(RefactorCallSubroutine.class);
    subName = JOptionPane.showInputDialog(null, "Call Subroutine Name?");
    if ( subName == null ) {
      return;
    }
    doc = sourceParser.getDocument();
    try {
      doc.remove(lineStart.getStart(), (lineEnd.getStart() + lineEnd.getLength()) - lineStart.getStart());
      doc.insertString(lineStart.getStart(), "     C                   exsr      " + subName + "\n", null);
    }
    catch (Exception e) {
      //e.printStackTrace();
    	logger.error(e.getMessage());
    }
  }
}

