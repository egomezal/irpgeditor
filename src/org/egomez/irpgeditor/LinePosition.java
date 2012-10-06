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
 * 
 * @author Derek Van Kooten.
 */
public class LinePosition {
  public RPGLineSpec spec;
  public int start, end, length;
  public int align = 0;
  public String description;
  
  public static int ALIGN_RIGHT = 0;
  public static int ALIGN_LEFT = 1;
  
  public static LinePosition FORM_TYPE = new LinePosition(6, 6, "FORM TYPE");
  public static LinePosition COMMENT = new LinePosition(7, 7, "COMMENT");
  public static LinePosition DIRECTIVE = new LinePosition(7, 7, "DIRECTIVE");
  
  public static LinePosition C_CONTROL_LEVEL = new LinePosition(8, 8, "CONTROL LEVEL");
  public static LinePosition C_INDICATORS = new LinePosition(9, 11, "INDICATORS");
  public static LinePosition C_FACTOR_1 = new LinePosition(12, 25, ALIGN_LEFT, "FACTOR 1");
  public static LinePosition C_OPERATION = new LinePosition(26, 35, ALIGN_LEFT, "OPERATION");
  public static LinePosition C_FACTOR_2 = new LinePosition(36, 49, ALIGN_LEFT, "FACTOR 2");
  public static LinePosition C_FACTOR_2_EXTENDED = new LinePosition(36, 80, ALIGN_LEFT, "FACTOR 2 EXTENDED");
  public static LinePosition C_RESULT = new LinePosition(50, 63, ALIGN_LEFT, "RESULT");
  public static LinePosition C_FIELD_LENGTH = new LinePosition(64, 68, ALIGN_LEFT, "FIELD LENGTH");
  public static LinePosition C_DECIMAL_POSITIONS = new LinePosition(69, 70, ALIGN_LEFT, "DECIMAL POSITIONS");
  public static LinePosition C_RESULT_INDICATORS = new LinePosition(71, 76, "RESULT INDICATORS");
  
  public static LinePosition D_NAME = new LinePosition(7, 21, "NAME");
  public static LinePosition D_EXTERNAL = new LinePosition(22, 22, "EXTERNAL");
  public static LinePosition D_DATA_STRUCTURE_TYPE = new LinePosition(23, 23, "DATA STRUCTURE TYPE");
  public static LinePosition D_DECLARATION_TYPE = new LinePosition(24, 25, "DECLARATION TYPE");
  public static LinePosition D_FROM = new LinePosition(26, 32, "FROM");
  public static LinePosition D_TO = new LinePosition(33, 39, "TO/LENGTH");
  public static LinePosition D_INTERNAL_DATA_TYPE = new LinePosition(40, 40, "INTERNAL DATA TYPE");
  public static LinePosition D_DECIMAL_POSITIONS = new LinePosition(41, 42, "DECIMAL POSITIONS");
  public static LinePosition D_KEYWORDS = new LinePosition(43, 79, "KEYWORDS");
  public static LinePosition D_COMMENTS = new LinePosition(79, 120, "COMMENTS");
  
  public static LinePosition F_NAME = new LinePosition(7, 16, "FILE NAME");
  public static LinePosition F_TYPE = new LinePosition(17, 17, "FILE TYPE");
  public static LinePosition F_DESIGNATION = new LinePosition(18, 18, "FILE DESIGNATION");
  public static LinePosition F_EOF = new LinePosition(19, 19, "END OF FILE");
  public static LinePosition F_ADDITION = new LinePosition(20, 20, "FILE ADDITION");
  public static LinePosition F_SEQUENCE = new LinePosition(21, 21, "SEQUENCE");
  public static LinePosition F_FORMAT = new LinePosition(22, 22, "FILE FORMAT");
  public static LinePosition F_RECORD_LENGTH = new LinePosition(23, 27, "RECORD LENGTH");
  public static LinePosition F_LIMITS_PROCESSING = new LinePosition(28, 28, "LIMITS PROCESSING");
  public static LinePosition F_KEY = new LinePosition(29, 33, "LENGTH OF KEY/RECORD ADDRESS");
  public static LinePosition F_ADDRESS_TYPE = new LinePosition(34, 34, "RECORD ADDRESS TYPE");
  public static LinePosition F_ORGANIZATION = new LinePosition(35, 35, "FILE ORGANIZATION");
  public static LinePosition F_DEVICE = new LinePosition(36, 42, "DEVICE");
  public static LinePosition F_RESERVED = new LinePosition(43, 43, "RESERVED");
  public static LinePosition F_KEYWORDS = new LinePosition(44, 80, "KEYWORDS");
  
  public static LinePosition A_INDICATOR1 = new LinePosition(8, 10, "INDICATOR1");
  public static LinePosition A_INDICATOR2 = new LinePosition(11, 13, "INDICATOR2");
  public static LinePosition A_INDICATOR3 = new LinePosition(14, 16, "INDICATOR3");
  public static LinePosition A_TYPE = new LinePosition(17, 17, "TYPE");
  public static LinePosition A_NAME = new LinePosition(19, 28, ALIGN_LEFT, "NAME");
  public static LinePosition A_REF = new LinePosition(29, 29, "REF");
  public static LinePosition A_LENGTH = new LinePosition(30, 34, "LENGTH");
  public static LinePosition A_DATA_TYPE = new LinePosition(35, 35, "DATA TYPE");
  public static LinePosition A_DECIMAL_POSITIONS = new LinePosition(36, 37, "DECIMAL POSITIONS");
  public static LinePosition A_USE = new LinePosition(38, 38, "USE");
  public static LinePosition A_LINE = new LinePosition(39, 41, "LINE");
  public static LinePosition A_POSITIONS = new LinePosition(42, 44, "POSITION");
  public static LinePosition A_FUNCTIONS = new LinePosition(45, 80, "FUNCTIONS");
  
  protected LinePosition(int start, int end, String description) {
    this.start = start;
    this.end = end;
    this.description = description;
    length = (end - start) + 1;
  }
  
  protected LinePosition(int start, int end, int align, String description) {
    this(start, end, description);
    this.align = align;
  }
}







