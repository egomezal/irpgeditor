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
import javax.swing.*;
import javax.swing.text.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;

/**
 * Represents a Source member in a project and the members settings.
 * For example, how to compile it, and its compile options.
 * 
 * @author Derek Van Kooten.
 */
public class ProjectMember extends NodeAbstract implements ListenerMember {
  Project project;
  Member member;
  String compileType;
  String destinationLibrary;
  ArrayList options = new ArrayList();
  ArrayList breakPoints = new ArrayList();
  Icon icon;
  
  public static String COMPILE_TYPE_CRTDSPF = "CRTDSPF";
  public static String COMPILE_TYPE_CRTRPGPGM = "CRTRPGPGM";
  public static String COMPILE_TYPE_CRTBNDRPG = "CRTBNDRPG";
  public static String COMPILE_TYPE_CRTSQLRPGI = "CRTSQLRPGI";
  public static String COMPILE_TYPE_CRTCLPGM = "CRTCLPGM";
  public static String COMPILE_TYPE_CRTPRTF = "CRTPRTF";
  public static String COMPILE_TYPE_CRTPF = "CRTPF";
  public static String COMPILE_TYPE_CRTLF = "CRTLF";
  
  /**
   * Creates an instance of the ProjectMember class. 
   * Calls determineDefaultCompileType.
   * 
   * Determines the compileType
   * from the sourceType property of the Member object.
   * 
   * @param project Project The project that this Member object is part of.
   * @param member Member The member that is joining the Project.
   */
  public ProjectMember(Project project, Member member) {
    this.project = project;
    this.member = member;
    destinationLibrary = member.library;
    member.addListener(this);
    determineDefaultCompileType();
    determineIcon();
  }
  
  /**
   * Creates an instance of this ProjectMember class.
   * 
   * @param project Project The project that this member object is a part of.
   * @param member Member The member that is joining the projct.
   * @param compileType String The AS400 command used to compile the source member.
   * @param destinationLibrary String The library to place the compiled program into.
   */
  public ProjectMember(Project project, Member member, String compileType, String destinationLibrary) {
    this.project = project;
    this.member = member;
    this.compileType = compileType;
    if ( destinationLibrary == null ) {
      destinationLibrary = member.library;
    }
    this.destinationLibrary = destinationLibrary;
    member.addListener(this);
    determineIcon();
  }
  
  public void memberChanged(Member member) {
    determineIcon();
  }
  
  public void isDirty(Member member, boolean isDirty) {
  }
  
  public boolean isOkToClose(Member member) {
    return true;
  }
  
  public Project getProject() {
    return project;
  }
  
  public Member getMember() {
    return member;
  }
  
  public String getCompileType() {
    return compileType;
  }
  
  public String getDestinationLibrary() {
    return destinationLibrary;
  }
  
  public void determineDefaultCompileType() {
    if ( member.sourceType.equals(Member.SOURCE_TYPE_DSPF) ) {
      compileType = COMPILE_TYPE_CRTDSPF;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_RPG) ) {
      compileType = COMPILE_TYPE_CRTRPGPGM;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_RPGLE) ) {
      compileType = COMPILE_TYPE_CRTBNDRPG;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_SQLRPGLE) ) {
      compileType = COMPILE_TYPE_CRTSQLRPGI;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_CLP) ) {
      compileType = COMPILE_TYPE_CRTCLPGM;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_CLLE) ) {
      compileType = COMPILE_TYPE_CRTCLPGM;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_PRTF) ) {
      compileType = COMPILE_TYPE_CRTPRTF;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_PF) ) {
      compileType = COMPILE_TYPE_CRTPF;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_LF) ) {
      compileType = COMPILE_TYPE_CRTLF;
    }
    else {
      compileType = "";
    }
  }
  
  public void determineIcon() {
    if ( member.sourceType.equals(Member.SOURCE_TYPE_DSPF) ) {
      icon = Icons.iconScreen;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_RPGLE) ) {
      icon = Icons.iconRpgle;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_SQLRPGLE) ) {
      icon = Icons.iconSqlrpgle;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_CLP) ) {
      icon = Icons.iconCl;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_CLLE) ) {
      icon = Icons.iconCl;
    }
    else if ( member.sourceType.equals(Member.SOURCE_TYPE_PRTF) ) {
      icon = Icons.iconPrtf;
    }
    else {
      icon = Icons.iconMember;
    }
  }
  
  public void toggleBreakPoint(int line) {
    Integer i;
    
    i = new Integer(line);
    if ( breakPoints.contains(i) ) {
      breakPoints.remove(i);
    }
    else {
      breakPoints.add(i);
    }
  }
  
  public ArrayList getBreakPoints() {
    return breakPoints;
  }
  
  /**
   * adds a compile option.
   */
  public void addCompileOption(String option) {
    options.add(option);
  }
  
  public void removeCompileOption(String option) {
    options.remove(option);
  }
  
  public String getCompileOption(int index) {
    return (String)options.get(index);
  }
  
  public int compileOptionCount() {
    return options.size();
  }
  
  /**
   * move this to projectmember class.
   * the textComponent is optional, if the source is being displayed in a
   * text component, then include it, so that compile result line numbers can be
   * matched up to a text component.
   */
  public void compile(JTextComponent textComponent) {
    String cmd;
    String type, option;
    String buffer;
    
    // determine the type of object to create.
    if ( compileType.equals("CRTRPGMOD") ) {
      type = "MODULE";
      option = "OPTION(*EVENTF) ";
    }
    else if ( compileType.equals("CRTBNDRPG") ) {
      type = "PGM";
      option = "OPTION(*EVENTF) ";
    }
    else if ( compileType.equals("CRTRPGPGM") || compileType.equals("CRTCLPGM") ) {
      type = "PGM";
      option = "";
    }
    else if ( compileType.equals("CRTSQLRPGI") ) {
      type = "OBJ";
      option = "OPTION(*EVENTF *NOSEQSRC) ";
    }
    else if ( compileType.equals("CRTDSPF") ) {
      type = "FILE";
      option = "OPTION(*EVENTF) ";
    }
    else if ( compileType.equals("CRTPRTF") || compileType.equals("CRTPF") || compileType.equals("CRTLF") ) {
      type = "FILE";
      option = "";
    }
    else {
      type = "OBJ";
      option = "OPTION(*EVENTF) ";
    }
    
    Environment.compilerResults.clear();
    cmd = compileType + " " + type + "(" + destinationLibrary + "/" + member.member + ") SRCFILE(" + member.library + "/" + member.file + ") SRCMBR(" + member.member + ") " + option;
    for ( int x = 0; x < compileOptionCount(); x++ ) {
      cmd = cmd + getCompileOption(x) + " ";
    }
    try {
      member.as400system.call(cmd);
      if ( option.length() > 0 ) {
        // get the event file results.
        buffer = member.as400system.getErrorText(destinationLibrary, "EVFEVENT", member.member, member.as400system.name + " " + member.library + " " + member.file + " " + member.member);
        // build the line index if the type is sql
        if ( compileType.equals("CRTSQLRPGI") ) {
          cmd = "CPYF FROMFILE(QTEMP/QSQLTEMP1) TOFILE(" + destinationLibrary + "/QSQLTEMP1) FROMMBR(" + member.member + ") TOMBR(" + member.member + ") MBROPT(*REPLACE) CRTFILE(*YES)";
          member.as400system.call(cmd);
          buffer = member.as400system.indexLineNumbers(destinationLibrary, "QSQLTEMP1", member.member, buffer);
        }
        Environment.compilerResults.setResults(buffer, textComponent);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public String getText() {
    return member.member;
  }
  
  public String getToolTipText() {
    return member.as400system.name + " - " + member.library + " - " + member.file + " - " + member.member;
  }
  
  public Icon getIcon() {
    return icon;
  }
  
  public Node getParent() {
    return project;
  }
  
  public int hashCode() {
    return member.hashCode();
  }
  
  public boolean equals(Object object) {
    if ( object == null ) {
      return false;
    }
    if ( object instanceof ProjectMember ) {
      return ((ProjectMember)object).member.equals(member);
    }
    return false;
  }
  
  public String toString() {
    return member.member;
  }
}
