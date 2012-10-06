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
import org.egomez.irpgeditor.table.*;

/**
 * Panel for managing systems.
 * 
 * @author Derek Van Kooten.
 */
public class PanelSystems extends PanelTool {
  TableModelSystems tableModelSystems = new TableModelSystems();
  
  ActionSessionOpen actionSessionOpen = new ActionSessionOpen();
  ActionSystemRemove actionSystemRemove = new ActionSystemRemove();
  ActionFocus actionFocus = new ActionFocus();
  
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel panelSystemsButtons = new JPanel();
  JTable tableSystems = new JTable(tableModelSystems);
  JButton buttonSystemsOpen = new JButton();
  FlowLayout flowlayoutSystemsButtons = new FlowLayout();
  JScrollPane scrollpaneSystems = new JScrollPane();
  BorderLayout borderLayoutSystems = new BorderLayout();
  JButton buttonRemove = new JButton();
  
  public PanelSystems() {
    setName("Systems");
    try {
      super.actions = new Action[] { actionSessionOpen, actionFocus };
      Environment.actions.addActions(actions);
      jbInit();
      buttonSystemsOpen.addActionListener(actionSessionOpen);
      buttonRemove.addActionListener(actionSystemRemove);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    panelSystemsButtons.setLayout(flowlayoutSystemsButtons);
    buttonSystemsOpen.setMargin(new Insets(0, 0, 0, 0));
    buttonSystemsOpen.setText("Open");
    flowlayoutSystemsButtons.setAlignment(FlowLayout.LEFT);
    flowlayoutSystemsButtons.setHgap(2);
    flowlayoutSystemsButtons.setVgap(2);
    buttonRemove.setMargin(new Insets(0, 0, 0, 0));
    buttonRemove.setText("Remove");
    add(scrollpaneSystems, BorderLayout.CENTER);
    add(panelSystemsButtons, BorderLayout.NORTH);
    panelSystemsButtons.add(buttonSystemsOpen, null);
    panelSystemsButtons.add(buttonRemove, null);
    scrollpaneSystems.getViewport().add(tableSystems, null);
  }
  
  class ActionSystemRemove extends AbstractAction {
    public ActionSystemRemove() {
      super("Remove");
      setEnabled(true);
      putValue("MENU", "Systems");
    }
    
    public void actionPerformed(ActionEvent evt) {
      AS400System system;
      int row;

      row = tableSystems.getSelectedRow();
      if ( row == -1 ) {
        return;
      }
      system = (AS400System)tableModelSystems.getSystem(row);
      if ( system == null ) {
        return;
      }
      Environment.systems.removeSystem(system);
    }
  }
  
  /**
   * opens a session to the as400.
   */
  class ActionSessionOpen extends AbstractAction {
    public ActionSessionOpen() {
      super("Open");
      setEnabled(true);
      putValue("MENU", "Session");
      // F6
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
//      putValue(Action.MNEMONIC_KEY, new Character('S'));
    }
    
    public void actionPerformed(ActionEvent evt) {
      final AS400System system;
      int row;
      
      row = tableSystems.getSelectedRow();
      if ( row == -1 ) {
        return;
      }
      system = (AS400System)tableModelSystems.getSystem(row);
      if ( system == null ) {
        return;
      }
      new Thread() {
        public void run() {
          FrameTN5250J frame;

          frame = new FrameTN5250J();
          frame.setSystem(system.getAddress());
          frame.determineSize();
          frame.center();
          frame.setVisible(true);
          try {
            frame.connect();
          }
          catch (Exception e) {
            e.printStackTrace();
          }

        }
      }.start();
    }
  }
  
  /**
   * starts a green screen and runs it in debug.
   */
  class ActionFocus extends AbstractAction {
    public ActionFocus() {
      super("Systems");
      setEnabled(true);
      putValue("MENU", "Tools");
      // F6 + CTRL
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, KeyEvent.CTRL_MASK, false));
//      putValue(Action.MNEMONIC_KEY, new Character('S'));
    }

    public void actionPerformed(ActionEvent evt) {
      focus();
    }
  }
}
