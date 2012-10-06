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

/**
 * @author not attributable
 */
public class SQL {
  static OutputSQL output;
  
  public static void setOutput(OutputSQL output) {
    SQL.output = output;
  }
  
  public static void executeSQL(String sql) {
    output.executeSQL(sql);
  }
  
  public static void setSQL(String sql) {
    output.setSQL(sql);
  }
  
  public static void appendSQL(String sql) {
    output.appendSQL(sql);
  }
  
  public static void clear() {
    output.clear();
  }
  
  public static void focus() {
    output.focus();
  }
  
  public static void saveSettings() {
    output.saveSettings();
  }
}
