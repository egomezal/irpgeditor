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

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import org.egomez.irpgeditor.event.*;


/**
 * highlights rpg source code according to types and keywords.
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unchecked")
public class RPGSourceHighlighter implements ListenerParser {
  JTextPane textPane;
  StyledDocument styleDocument;
  
  SimpleAttributeSet attributes = new SimpleAttributeSet();
  
  // COMMENTS
  static SimpleAttributeSet attributesComment = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesComment, new Color(0, 128, 0));
    StyleConstants.setBold(attributesComment, false);
  }
  // HEADER
  static SimpleAttributeSet attributesHeader = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesHeader, new Color(192, 128, 64));
    StyleConstants.setBold(attributesHeader, true);
  }
  // DIRECTIVES
  static SimpleAttributeSet attributesDirectives = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesDirectives, new Color(255, 128, 128));
    StyleConstants.setBold(attributesDirectives, true);
  }
  // FILES
  static SimpleAttributeSet attributesFiles = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesFiles, new Color(128, 0, 128));
    StyleConstants.setBold(attributesFiles, false);
  }
  // D SPEC
  static SimpleAttributeSet attributesD = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesD, new Color(0, 0, 255));
    StyleConstants.setBold(attributesD, false);
  }
  // P SPEC
  static SimpleAttributeSet attributesP = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesP, new Color(0, 0, 192));
    StyleConstants.setBold(attributesP, true);
  }
  // A spec
  static SimpleAttributeSet attributesA = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesA, new Color(160, 108, 0));
    StyleConstants.setBold(attributesA, false);
  }
  // A RECORD spec
  static SimpleAttributeSet attributesARecord = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesARecord, new Color(10, 151, 186));
    StyleConstants.setBold(attributesARecord, true);
  }
  // SQL
  static SimpleAttributeSet attributesSql = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesSql, new Color(128, 64, 128));
    StyleConstants.setBold(attributesSql, true);
  }
  // UNKNOWN
  static SimpleAttributeSet attributesUnknown = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesUnknown, new Color(0, 0, 0));
    StyleConstants.setBold(attributesUnknown, false);
  }
  // KEYWORD
  static SimpleAttributeSet attributesKeyword = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesKeyword, new Color(0, 0, 128));
    StyleConstants.setBold(attributesKeyword, true);
  }
  // INDICATOR
  static SimpleAttributeSet attributesIndicator = new SimpleAttributeSet();
  static {
    StyleConstants.setForeground(attributesIndicator, new Color(128, 0, 0));
    StyleConstants.setBold(attributesIndicator, true);
  }
  
  @SuppressWarnings("rawtypes")
public static HashSet setKeywords = new HashSet();
  static {
    setKeywords.add("adddur");
    setKeywords.add("callp");
    setKeywords.add("begsr");
    setKeywords.add("endsr");
    setKeywords.add("endfor");
    setKeywords.add("if");
    setKeywords.add("or");
    setKeywords.add("and");
    setKeywords.add("dou");
    setKeywords.add("dow");
    setKeywords.add("leavesr");
    setKeywords.add("endif");
    setKeywords.add("call");
    setKeywords.add("write");
    setKeywords.add("exfmt");
    setKeywords.add("read");
    setKeywords.add("readc");
    setKeywords.add("enddo");
    setKeywords.add("not");
    setKeywords.add("chain");
    setKeywords.add("exsr");
    setKeywords.add("reade");
    setKeywords.add("reade(n)");
    setKeywords.add("select");
    setKeywords.add("when");
    setKeywords.add("endsl");
    setKeywords.add("update");
    setKeywords.add("else");
    setKeywords.add("return");
    setKeywords.add("plist");
    setKeywords.add("parm");
    setKeywords.add("klist");
    setKeywords.add("kfld");
    setKeywords.add("tag");
    setKeywords.add("eval");
    setKeywords.add("evalr");
    setKeywords.add("open(e)");
    setKeywords.add("chain(e)");
    setKeywords.add("setgt(e)");
    setKeywords.add("readpe(e)");
    setKeywords.add("read(e)");
    setKeywords.add("setll(e)");
    setKeywords.add("write(e)");
    setKeywords.add("occur");
    setKeywords.add("occur(e)");
    setKeywords.add("close(e)");
    setKeywords.add("time");
    setKeywords.add("unlock(e)");
    setKeywords.add("clear");
    setKeywords.add("reade(e)");
    setKeywords.add("move");
    setKeywords.add("in(e)");
    setKeywords.add("in");
    setKeywords.add("out(e)");
    setKeywords.add("out");
    setKeywords.add("z-add");
    setKeywords.add("seton");
    setKeywords.add("setll");
    setKeywords.add("setoff");
    setKeywords.add("movel");
    setKeywords.add("sub");
    setKeywords.add("mult");
    setKeywords.add("add");
    setKeywords.add("lookup");
    setKeywords.add("adddur(e)");
    setKeywords.add("subdur(e)");
    setKeywords.add("other");
    setKeywords.add("dump");
    setKeywords.add("do");
    setKeywords.add("movea");
    setKeywords.add("setgt");
    setKeywords.add("readp");
    setKeywords.add("ifne");
    setKeywords.add("end");
    setKeywords.add("andeq");
    setKeywords.add("downe");
    setKeywords.add("oreq");
    setKeywords.add("define");
    setKeywords.add("div");
    setKeywords.add("div(h)");
    setKeywords.add("mult(h)");
    setKeywords.add("iflt");
    setKeywords.add("ifgt");
    setKeywords.add("check");
    setKeywords.add("ifeq");
    setKeywords.add("doweq");
    setKeywords.add("ifge");
    setKeywords.add("open");
    setKeywords.add("close");
    setKeywords.add("except");
    setKeywords.add("andle");
    setKeywords.add("ifle");
    setKeywords.add("z-sub");
    setKeywords.add("orne");
    setKeywords.add("doueq");
    setKeywords.add("goto");
    setKeywords.add("andne");
    setKeywords.add("andlt");
    setKeywords.add("dowlt");
    setKeywords.add("delete");
    setKeywords.add("dowle");
    setKeywords.add("comp");
    setKeywords.add("iter");
    setKeywords.add("movel(p)");
    setKeywords.add("test(de)");
    setKeywords.add("subdur");
    setKeywords.add("sorta");
    setKeywords.add("cat");
    setKeywords.add("leave");
    setKeywords.add("cabeq");
    setKeywords.add("xfoot");
    setKeywords.add("wheneq");
    setKeywords.add("chain(n)");
    setKeywords.add("andgt");
    setKeywords.add("orgt");
    setKeywords.add("scan");
    setKeywords.add("callb");
    setKeywords.add("read(n)");
    setKeywords.add("unlock");
    setKeywords.add("test(d)");
    setKeywords.add("eval(r)");
    setKeywords.add("cabne");
    setKeywords.add("mvr");
    setKeywords.add("andge");
    setKeywords.add("for");
    setKeywords.add("subst");
    setKeywords.add("testn");
    setKeywords.add("monitor");
    setKeywords.add("endmon");
    setKeywords.add("on-error");
    setKeywords.add("reset");
    setKeywords.add("add(h)");
  }
  
  public RPGSourceHighlighter() {
  }
  
  public void setTextPane(JTextPane textPane) {
    this.textPane = textPane;
    styleDocument = (StyledDocument)textPane.getDocument();
  }
  
  /**
   * handles events that occur in the source.
   */
  @SuppressWarnings("rawtypes")
public void parserEvents(ArrayList listEvents) {
    SourceParserEvent event;
    
    for ( int x = 0; x < listEvents.size(); x++ ) {
      event = (SourceParserEvent)listEvents.get(x);
      if ( event.type != SourceParserEvent.REMOVED ) {
        backgroundAddStyle(event.line);
      }
    }
  }
  
  public void addStyle(SourceParser sourceParser) {
    SourceLine line;
    
    line = sourceParser.first;
    while ( line != null ) {
      addStyle(line);
      line = line.getNext();
    }
  }
  
  public void backgroundAddStyle(final SourceLine line) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        addStyle(line);
      }
    });
  }
  
  public void addStyle(SourceLine line) {
    StringBuffer source;
    String  keyword; //buffer,
    char c;
    
    source = line.parser.getText();
    
    if ( line.start + 6 >= source.length() ) {
      return;
    }
    // compile time data.
    if ( source.charAt(line.start) == '*' &&
         source.charAt(line.start + 1) == '*' ) {
      return;
    }
    
    c = source.charAt(line.start + 6);
    // is comment?
    if ( c == '*' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesComment, true);
      return;
    }
    // is directive?
    if ( c == '/' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesDirectives, true);
      return;
    }
    // is sql?
    if ( c == '+' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesSql, true);
      return;
    }
    
    c = source.charAt(line.start + 5);
    // header?
    if ( c == 'H' || c == 'h' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesHeader, true);
      return;
    }
    // is files?
    if ( c == 'F' || c == 'f' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesFiles, true);
      return;
    }
    // is D?
    if ( c == 'D' || c == 'd' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesD, true);
      return;
    }
    // is P?
    if ( c == 'P' || c == 'p' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesP, true);
      return;
    }
    // is A?
    if ( c == 'A' || c == 'a' ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesA, true);
      if ( source.length() <= line.start + 16 ) {
        return;
      }
      c = source.charAt(line.start + 16);
      if ( c == 'R' || c == 'r' ) {
        styleDocument.setCharacterAttributes(line.start, line.length, attributesARecord, true);
      }
      else {
        styleDocument.setCharacterAttributes(line.start, line.length, attributesA, true);
      }
      return;
    }
    
    int begin = line.start;
    int stop = line.start + line.length;
    // free form comments?
    if ( startsWith(source, begin, stop, "//" ) ) {
      styleDocument.setCharacterAttributes(line.start, line.length, attributesComment, true);
      return;
    }
    
    // unknown
    styleDocument.setCharacterAttributes(line.start, line.length, attributesUnknown, true);
    
    // find keywords.
    // find the start of the first keyword.
    int end;
    begin = findNonBlank(source, begin, stop);
    while ( begin > -1 ) {
      // find blank.
      end = findBlank(source, begin, stop);
      // process keyword.
      if ( source.charAt(begin) == '*' ) {
        styleDocument.setCharacterAttributes(begin, end - begin, attributesIndicator, true);
      }
      else {
        if ( startsWith(source, begin, stop, "//") ) {
          styleDocument.setCharacterAttributes(begin, line.length - (begin - line.start), attributesComment, true);
          break;
        }
        keyword = source.substring(begin, end);
        keyword = keyword.trim().toLowerCase();
        if ( keyword.endsWith(";") ) {
          keyword = keyword.substring(0, keyword.length() - 1);
        }
        if ( RPGSourceHighlighter.setKeywords.contains(keyword) ) {
          styleDocument.setCharacterAttributes(begin, end - begin, attributesKeyword, true);
        }
        else {
        }
      }
      // find non blank.
      begin = findNonBlank(source, end + 1, stop);
    }
  }
  
  @SuppressWarnings("unused")
private boolean startsWith(StringBuffer string, int start, int end, String compare) {
    int length2 = 0, y;
    
    y = 0;
    length2 = compare.length();
    for ( int x = start; x < end && x < string.length(); x++ ) {
      while ( string.charAt(x) == compare.charAt(y) ) {
        y++;
        if ( y == compare.length() ) {
          return true;
        }
      }
      y = 0;
    }
    return false;
  }
  
  private int findBlank(StringBuffer line, int x, int stop) {
    if ( stop >= line.length() ) {
      stop = line.length() - 1;
    }
    for ( ; x < stop; x++ ) {
      if ( line.charAt(x) == ' ' ) {
        return x;
      }
    }
    return stop;
  }
  
  private int findNonBlank(StringBuffer line, int x, int stop) {
    if ( x >= stop ) {
      return -1;
    }
    if ( stop >= line.length() ) {
      stop = line.length() - 1;
    }
    for ( ; x < stop; x++ ) {
      if ( line.charAt(x) != ' ' ) {
        return x;
      }
    }
    return -1;
  }
}
