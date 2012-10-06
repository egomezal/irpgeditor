package org.egomez.irpgeditor.swing;

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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;

import com.ibm.as400.ui.util.*;

/**
 * @author Derek Van Kooten.
 */
public class PanelQcmdexec extends PanelTool implements QcmdexecOutput {
  JFrame frame;
  ActionQcmdexec actionQcmdexec = new ActionQcmdexec();
  ActionCommandPrompter actionCommandPrompter = new ActionCommandPrompter();
  ActionFocus actionFocus = new ActionFocus();
  
  JTextArea textareaQcmdexecMessages = new JTextArea();
  JTextField textfieldQcmdexec = new JTextField();
  BorderLayout borderLayoutQcmdExecPrompt = new BorderLayout();
  JPanel panelQcmdexecPrompt = new JPanel();
  JScrollPane scrollpaneQcmdexec = new JScrollPane();
  JButton buttonPrompt = new JButton();
  BorderLayout borderLayoutQcmdexec = new BorderLayout();
  JCheckBox checkboxSql = new JCheckBox();
  
  public PanelQcmdexec() {
    setName("QCMDEXEC");
    try {
      super.actions = new Action[] { actionFocus };
      Environment.actions.addActions(actions);
      jbInit();
      textfieldQcmdexec.addActionListener(actionQcmdexec);
      buttonPrompt.addActionListener(actionCommandPrompter);
      new HandlerKeyPressed(textareaQcmdexecMessages);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception {
    setLayout(borderLayoutQcmdexec);
    borderLayoutQcmdExecPrompt.setVgap(2);
    borderLayoutQcmdExecPrompt.setHgap(2);
    panelQcmdexecPrompt.setLayout(borderLayoutQcmdExecPrompt);
    buttonPrompt.setMargin(new Insets(0, 0, 0, 0));
    buttonPrompt.setText("prompt");
    checkboxSql.setMargin(new Insets(0, 0, 0, 0));
    checkboxSql.setText("SQL");
    add(scrollpaneQcmdexec, BorderLayout.CENTER);
    add(panelQcmdexecPrompt, BorderLayout.NORTH);
    scrollpaneQcmdexec.getViewport().add(textareaQcmdexecMessages, null);
    panelQcmdexecPrompt.add(textfieldQcmdexec, BorderLayout.CENTER);
    panelQcmdexecPrompt.add(buttonPrompt, BorderLayout.WEST);
    panelQcmdexecPrompt.add(checkboxSql,  BorderLayout.EAST);
  }
  
  public void clear() {
    textareaQcmdexecMessages.setText("");
  }
  
  public void setJFrame(JFrame frame) {
    this.frame = frame;
  }
  
  public void append(String text) {
    int length;
    
    textareaQcmdexecMessages.append(text);
    length = textareaQcmdexecMessages.getText().length();
    textareaQcmdexecMessages.setSelectionStart(length);
    textareaQcmdexecMessages.setSelectionEnd(length);
  }
  
  public void focus() {
    // throw an event here, so that what ever container this panel is placed
    // into, it should show this panel as the focus.
    fireRequestingFocus();
  }
  
  /**
   * prompts the user for the command the user has typed in.
   */
  class ActionCommandPrompter implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      AS400System as400system;
      CommandPrompter cp;
      String cmd;

      cmd = textfieldQcmdexec.getText().trim();
      if ( cmd.length() == 0 ) {
        return;
      }
      // choose system.
      as400system = Environment.systems.getDefault();
      cp = new CommandPrompter(frame, as400system.getAS400(), cmd);
      if ( cp.showDialog() != CommandPrompter.OK ) {
        return;
      }
      textfieldQcmdexec.setText(cp.getCommandString());
      actionQcmdexec.actionPerformed(null);
    }
  }

  /**
   * executes some command and displays the results.
   */
  class ActionQcmdexec implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      String cmd;
      AS400System as400system;

      cmd = textfieldQcmdexec.getText().trim();
      if ( cmd.length() == 0 ) {
        return;
      }
      textfieldQcmdexec.setText("");
      // choose system.
      as400system = Environment.systems.getDefault();
      try {
        if ( checkboxSql.isSelected() ) {
          as400system.sqlCall(cmd);
        }
        else {
          as400system.call(cmd);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   */
  class ActionFocus extends AbstractAction {
    public ActionFocus() {
      super("Qcmdexec");
      setEnabled(true);
      putValue("MENU", "Tools");
      // F5 + CTRL
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(116, KeyEvent.CTRL_MASK, false));
//      putValue(Action.MNEMONIC_KEY, new Character('S'));
    }

    public void actionPerformed(ActionEvent evt) {
      focus();
    }
  }
}
