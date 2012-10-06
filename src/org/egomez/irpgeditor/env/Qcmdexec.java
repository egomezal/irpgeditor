package org.egomez.irpgeditor.env;

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

import com.ibm.as400.access.*;

/**
 * All commands issued on a system should show the commands issued here.
 * Another class will handle displaying the messages. 
 *  
 * @author not attributable
 */
public class Qcmdexec {
  QcmdexecOutput output;
  
  public void setOutput(QcmdexecOutput o) {
    output = o;
  }
  
  public void clear() {
    output.clear();
    focus();
  }
  
  public void append(String text) {
    output.append(text);
    focus();
  }
  
  public void appendLine(String text) {
    append(text);
    append("\n");
  }
  
  public void focus() {
    output.focus();
  }
  
  public void append(AS400Message[] messages) {
    for ( int m = 0; m < messages.length; m++ ) {
      appendLine(messages[m].getText());
    }
  }
}
