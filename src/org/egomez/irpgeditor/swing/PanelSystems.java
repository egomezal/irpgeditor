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
import org.egomez.irpgeditor.icons.Icons;
import org.egomez.irpgeditor.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for managing systems.
 * 
 * @author Derek Van Kooten.
 */
public class PanelSystems extends PanelTool {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1025879222924257373L;

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
	Logger logger = LoggerFactory.getLogger(PanelSystems.class);

	public PanelSystems() {
		setName("Systems");
		try {
			super.actions = new Action[] { actionSessionOpen, actionFocus };
			Environment.actions.addActions(actions);
			jbInit();
			buttonSystemsOpen.addActionListener(actionSessionOpen);
			buttonRemove.addActionListener(actionSystemRemove);
			tableSystems.getColumn("ID").setPreferredWidth(0);
			tableSystems.getColumn("ID").setMinWidth(0);
			tableSystems.getColumn("ID").setWidth(0);
			tableSystems.getColumn("ID").setMaxWidth(0);

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 5318656694048713377L;

		public ActionSystemRemove() {
			super("Remove");
			setEnabled(true);
			putValue("MENU", "Systems");
		}

		public void actionPerformed(ActionEvent evt) {
			AS400System system;
			int row;

			row = tableSystems.getSelectedRow();
			if (row == -1) {
				return;
			}
			system = (AS400System) tableModelSystems.getSystem(row);
			if (system == null) {
				return;
			}
			Environment.systems.removeSystem(system);
		}
	}

	/**
	 * opens a session to the as400.
	 */
	class ActionSessionOpen extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8220067555407779059L;

		public ActionSessionOpen() {
			super("Open", Icons.iconScreen);
			setEnabled(true);
			putValue("MENU", "Session");
			// F6
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
		}

		public void actionPerformed(ActionEvent evt) {
			final AS400System system;
			int row;

			row = tableSystems.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(null, "You need to select AS400 Server into System Panel.", "Server", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			system = (AS400System) tableModelSystems.getSystem(row);
			if (system == null) {
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
					} catch (Exception e) {
						// e.printStackTrace();
						logger.error(e.getMessage());
					}

				}
			}.start();
		}
	}

	/**
	 * starts a green screen and runs it in debug.
	 */
	class ActionFocus extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5003297214347996160L;

		public ActionFocus() {
			super("Systems", Icons.iconServer);
			setEnabled(true);
			putValue("MENU", "Tools");
			// F6 + CTRL
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, KeyEvent.CTRL_MASK, false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
		}

		public void actionPerformed(ActionEvent evt) {
			focus();
		}
	}
}
