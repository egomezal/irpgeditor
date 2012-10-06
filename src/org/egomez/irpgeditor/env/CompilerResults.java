package org.egomez.irpgeditor.env;

import javax.swing.text.*;

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
 * Outputs compiler results.
 *  
 * @author not attributable
 */
public class CompilerResults {
  CompilerResultsOutput output;
      
  public void clear() {
    output.clear();
  }
  
  public void setResults(String text, JTextComponent textComponent) {
    output.setResults(text, textComponent);
  }
  
/*  public void appendLine(String text, JTextComponent textComponent) {
    append(text + "\n", textComponent);
  }*/
  
  public void focus() {
    output.focus();
  }
  
  public void setOutput(CompilerResultsOutput output) {
    this.output = output;
  }
}
