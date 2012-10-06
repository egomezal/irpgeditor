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

import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.env.*;

import com.borland.jbcl.layout.*;
import com.ibm.as400.access.*;

/**
 * 
 * @author Derek Van Kooten.
 */
public class DialogMemberNew extends JDialog {
  AS400JDBCDriver driver = new AS400JDBCDriver();
  AS400System as400system;
  Member member;
  
  DefaultComboBoxModel listModelSystems = new DefaultComboBoxModel();
  DefaultComboBoxModel listModelLibraries = new DefaultComboBoxModel();
  DefaultComboBoxModel listModelFiles = new DefaultComboBoxModel();
  
  ActionOk actionOk = new ActionOk();
  ActionCancel actionCancel = new ActionCancel();
  ActionSelectSystem actionSelectSystem = new ActionSelectSystem();
  ActionSelectLibrary actionSelectLibrary = new ActionSelectLibrary();
  ActionSelectFile actionSelectFile = new ActionSelectFile();
  
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton buttonCancel = new JButton();
  JButton buttonOk = new JButton();
  JPanel jPanel15 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel12 = new JPanel();
  JLabel labelFile = new JLabel();
  JComboBox comboboxLibrary = new JComboBox(listModelLibraries);
  JLabel labelLibrary = new JLabel();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();
  JComboBox comboboxFile = new JComboBox(listModelFiles);
  JPanel jPanel5 = new JPanel();
  JComboBox comboboxSystem = new JComboBox(listModelSystems);
  JLabel labelSystem = new JLabel();
  BorderLayout borderLayout5 = new BorderLayout();
  JLabel labelMember = new JLabel();
  JPanel jPanel6 = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  JTextField textfieldMember = new JTextField();
  JComboBox comboboxType = new JComboBox(new Object[] {"RPGLE", "RPG", "SQLRPGLE", "PRTF", "DSPF", "PF", "LF", "CLP", "CLLE" });
  JLabel labelType = new JLabel();
  BorderLayout borderLayout7 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  
  public static Member showDialog(Frame frame) {
    return showDialog(frame, null, null, null, null, null);
  }
  
  public static Member showDialog(Frame frame, AS400System as400, String library, String file, String memberName, String type) {
    Member member;
    DialogMemberNew dialog;
    
    dialog = new DialogMemberNew(frame);
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = dialog.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    dialog.set(as400, library, file, memberName, type);
    dialog.setVisible(true);
    member = dialog.member;
    dialog.dispose();
    
    return member;
  }
  
  public DialogMemberNew(Frame frame) {
    super(frame, "New Member", true);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      pack();
      addActions();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.setTitle("New Member");
    buttonCancel.setMnemonic('C');
    buttonCancel.setText("Cancel");
    buttonOk.setMnemonic('O');
    buttonOk.setText("Ok");
    jPanel15.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    flowLayout1.setHgap(2);
    flowLayout1.setVgap(2);
    jPanel4.setLayout(borderLayout4);
    jPanel3.setLayout(borderLayout3);
    jPanel12.setLayout(verticalFlowLayout4);
    labelFile.setEnabled(false);
    labelFile.setText("File: ");
    comboboxLibrary.setEnabled(false);
    labelLibrary.setEnabled(false);
    labelLibrary.setText("Library: ");
    verticalFlowLayout4.setHgap(2);
    verticalFlowLayout4.setVgap(2);
    comboboxFile.setEnabled(false);
    jPanel5.setLayout(borderLayout5);
    labelSystem.setText("System: ");
    labelMember.setText("Member: ");
    labelMember.setEnabled(false);
    jPanel6.setLayout(borderLayout6);
    textfieldMember.setEnabled(false);
    comboboxType.setEnabled(false);
    comboboxType.setEditable(true);
    labelType.setText("Type: ");
    labelType.setEnabled(false);
    jPanel7.setLayout(borderLayout7);
    this.getContentPane().add(panel1, BorderLayout.CENTER);
    panel1.add(jPanel15, BorderLayout.SOUTH);
    jPanel15.add(buttonOk, null);
    jPanel15.add(buttonCancel, null);
    panel1.add(jPanel12, BorderLayout.CENTER);
    jPanel7.add(labelType, BorderLayout.WEST);
    jPanel7.add(comboboxType, BorderLayout.CENTER);
    jPanel12.add(jPanel5, null);
    jPanel5.add(labelSystem, BorderLayout.WEST);
    jPanel5.add(comboboxSystem, BorderLayout.CENTER);
    jPanel12.add(jPanel4, null);
    jPanel4.add(labelLibrary, BorderLayout.WEST);
    jPanel4.add(comboboxLibrary, BorderLayout.CENTER);
    jPanel12.add(jPanel3, null);
    jPanel3.add(labelFile, BorderLayout.WEST);
    jPanel3.add(comboboxFile, BorderLayout.CENTER);
    jPanel12.add(jPanel6, null);
    jPanel6.add(labelMember, BorderLayout.WEST);
    jPanel6.add(textfieldMember, BorderLayout.CENTER);
    jPanel12.add(jPanel7, null);
  }
  
  /**
   * Overridden so we can exit when window is closed
   */
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      actionCancel.actionPerformed(null);
    }
  }
  
  /**
   * associates actions with gui controls.
   */
  protected void addActions() {
    ArrayList listSystems;
    
    listSystems = Environment.systems.getSystems();
    for ( int x = 0; x < listSystems.size(); x++ ) {
      listModelSystems.addElement(listSystems.get(x));
    }
    comboboxSystem.setSelectedIndex(-1);
    
    comboboxSystem.addActionListener(actionSelectSystem);
    comboboxLibrary.addActionListener(actionSelectLibrary);
    comboboxFile.addActionListener(actionSelectFile);
    buttonOk.addActionListener(actionOk);
    buttonCancel.addActionListener(actionCancel);
  }
  
  public void set(AS400System system, String library, String file, String memberName, String type) {
    if ( system != null ) {
      comboboxSystem.setSelectedItem(system);
      if ( library != null ) {
        comboboxLibrary.setSelectedItem(library);
        if ( file != null ) {
          comboboxFile.setSelectedItem(file);
        }
      }
    }
    if ( memberName != null ) {
      textfieldMember.setText(memberName);
    }
    if ( type != null ) {
      comboboxType.setSelectedItem(type);
    }
  }
  
  /**
   * gets called when the application exits.
   */
  class ActionCancel implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      hide();
    }
  }
  
  /**
   * gets called when a user selects a system.
   */
  class ActionSelectSystem implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      AS400System system;
      ArrayList list;
      
      system = (AS400System)listModelSystems.getSelectedItem();
      if ( system == null ) {
        return;
      }
      try {
        list = system.getSourceLibraries();
        while ( list.size() > 0 ) {
          listModelLibraries.addElement(list.remove(0));
        }
        comboboxLibrary.setSelectedIndex(-1);
        comboboxLibrary.addActionListener(actionSelectLibrary);
        labelLibrary.setEnabled(true);
        comboboxLibrary.setEnabled(true);
      }
      catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(DialogMemberNew.this, e.getMessage());
      }
      labelFile.setEnabled(false);
      comboboxFile.setEnabled(false);
      listModelFiles.removeAllElements();
    }
  }
  
  /**
   * gets called when a library is selected.
   */
  class ActionSelectLibrary implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      AS400System system;
      ArrayList list;
      
      if ( comboboxLibrary.getSelectedIndex() == -1 ) {
        return;
      }
      system = (AS400System)listModelSystems.getSelectedItem();
      if ( system == null ) {
        return;
      }
      try {
        comboboxFile.removeActionListener(actionSelectFile);
        // get a list of libraries.
        listModelFiles.removeAllElements();
        list = system.getSourceFiles((String)comboboxLibrary.getSelectedItem());
        while ( list.size() > 0 ) {
          listModelFiles.addElement((String)list.remove(0));
        }
        comboboxFile.setSelectedIndex(-1);
        labelFile.setEnabled(true);
        comboboxFile.setEnabled(true);
        comboboxFile.addActionListener(actionSelectFile);
      }
      catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(DialogMemberNew.this, e.getMessage());
      }
    }
  }
  
  /**
   * gets called when the user selects a file.
   */
  class ActionSelectFile implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      textfieldMember.setEnabled(true);
      labelMember.setEnabled(true);
      labelType.setEnabled(true);
      comboboxType.setEnabled(true);
    }
  }
  
  /**
   * gets called when the user clicks the ok button.
   */
  class ActionOk implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      String cmd;
      AS400System system;
      String library, file, member, type;
      
      if ( textfieldMember.getText().trim().length() > 10 ) {
        JOptionPane.showMessageDialog(null, "Name must be no more than 10 characters.");
        return;
      }
      if ( textfieldMember.getText().trim().indexOf(" ") > -1 ) {
        JOptionPane.showMessageDialog(null, "Name must not contain spaces.");
        return;
      }
      // if all the project settings have not been selected.
      if ( comboboxSystem.getSelectedIndex() == -1 ||
           comboboxLibrary.getSelectedIndex() == -1 ||
           comboboxFile.getSelectedIndex() == -1 ||
           textfieldMember.getText().trim().length() == 0 ||
           comboboxType.getSelectedItem() == null ) {
        return;
      }
      system = (AS400System)comboboxSystem.getSelectedItem();
      library = (String)comboboxLibrary.getSelectedItem();
      file = (String)comboboxFile.getSelectedItem();
      member = textfieldMember.getText();
      type = (String)comboboxType.getSelectedItem();
      try {
        DialogMemberNew.this.member = system.createMember(library, file, member, type);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      hide();
    }
  }
}




