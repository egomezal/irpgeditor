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
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.ListenerMemberCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.borland.jbcl.layout.*;
import com.ibm.as400.access.*;

/**
 * 
 * @author Derek Van Kooten.
 */

public class DialogAddLibrary extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8750883042348276901L;

	ActionCancel actionCancel = new ActionCancel();

	ActionOk actionOk = new ActionOk();

	ActionSelectFile actionSelectFile = new ActionSelectFile();

	ActionSelectLibrary actionSelectLibrary = new ActionSelectLibrary();

	ActionSelectSystem actionSelectSystem = new ActionSelectSystem();
	AS400System as400system;

	BorderLayout borderLayout3 = new BorderLayout();

	BorderLayout borderLayout4 = new BorderLayout();

	BorderLayout borderLayout5 = new BorderLayout();

	BorderLayout borderLayout6 = new BorderLayout();

	BorderLayout borderLayout7 = new BorderLayout();

	JButton buttonCancel = new JButton();
	JButton buttonOk = new JButton();

	AS400JDBCDriver driver = new AS400JDBCDriver();

	FlowLayout flowLayout1 = new FlowLayout();

	JPanel jPanel12 = new JPanel();

	JPanel jPanel15 = new JPanel();

	JPanel jPanel3 = new JPanel();

	JPanel jPanel4 = new JPanel();

	JPanel jPanel5 = new JPanel();

	JPanel jPanel6 = new JPanel();

	JPanel jPanel7 = new JPanel();

	JLabel labelLibrary = new JLabel();

	JLabel labelSystem = new JLabel();

	JLabel labelType = new JLabel();

	DefaultComboBoxModel<String> listModelFiles = new DefaultComboBoxModel<String>();

	DefaultComboBoxModel<String> listModelLibraries = new DefaultComboBoxModel<String>();

	DefaultComboBoxModel<AS400System> listModelSystems = new DefaultComboBoxModel<AS400System>();
	ListenerMemberCreated listener;
	Member member;
	JPanel panel1 = new JPanel();

	VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();

	JComboBox<String> comboboxLibrary = new JComboBox<String>(this.listModelLibraries);

	JComboBox<AS400System> comboboxSystem = new JComboBox<AS400System>(this.listModelSystems);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxType = new JComboBox(new Object[] { "*FIRST", "*LAST", "*AFTER", "*BEFORE" });
	private final JPanel panel = new JPanel();
	private final JLabel lblDescription = new JLabel();
	Logger logger = LoggerFactory.getLogger(DialogAddLibrary.class);
	private final JComboBox<String> comboBox = new JComboBox<String>(this.listModelLibraries);

	public DialogAddLibrary(Frame frame) {
		super(frame, "Add to Library List", true);
		setResizable(false);
		setSize(301, 180);
		enableEvents(64L);
		try {
			jbInit();
			// pack();
			addActions();
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error(ex.getMessage());
		}
	}

	protected void addActions() {
		ArrayList<AS400System> listSystems = Environment.systems.getSystems();
		for (int x = 0; x < listSystems.size(); x++) {
			this.listModelSystems.addElement(listSystems.get(x));
		}
		this.comboboxSystem.setSelectedIndex(-1);

		this.comboboxSystem.addActionListener(this.actionSelectSystem);
		this.comboboxLibrary.addActionListener(this.actionSelectLibrary);
		this.buttonOk.addActionListener(this.actionOk);
		this.buttonCancel.addActionListener(this.actionCancel);
	}

	void jbInit() throws Exception {
		setTitle("Add to Library List");
		this.buttonCancel.setMnemonic('C');
		this.buttonCancel.setText("Cancel");
		this.buttonOk.setMnemonic('O');
		this.buttonOk.setText("Ok");
		jPanel15.setBounds(10, 113, 234, 27);
		this.jPanel15.setLayout(this.flowLayout1);
		this.flowLayout1.setAlignment(2);
		this.flowLayout1.setHgap(2);
		this.flowLayout1.setVgap(2);
		this.jPanel4.setLayout(this.borderLayout4);
		this.jPanel3.setLayout(this.borderLayout3);
		jPanel12.setBounds(75, 0, 210, 102);
		this.jPanel12.setLayout(this.verticalFlowLayout4);
		this.comboboxLibrary.setEnabled(false);
		this.labelLibrary.setEnabled(false);
		this.labelLibrary.setText("Library: ");
		this.verticalFlowLayout4.setHgap(2);
		this.verticalFlowLayout4.setVgap(2);
		this.jPanel5.setLayout(this.borderLayout5);
		this.labelSystem.setText("System: ");
		this.jPanel6.setLayout(this.borderLayout6);
		comboboxType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboboxType.getSelectedIndex()>1){
					comboBox.setEnabled(true);
				}
				else{
					comboBox.setEnabled(false);
				}
			}
		});
		this.comboboxType.setEnabled(false);
		this.comboboxType.setEditable(true);
		this.labelType.setText("Position: ");
		this.labelType.setEnabled(false);
		this.jPanel7.setLayout(this.borderLayout7);
		getContentPane().add(this.panel1, "Center");
		panel1.setLayout(null);
		this.panel1.add(this.jPanel15);
		this.jPanel15.add(this.buttonOk, null);
		this.jPanel15.add(this.buttonCancel, null);
		this.panel1.add(this.jPanel12);
		this.jPanel7.add(this.labelType, "West");
		this.jPanel7.add(this.comboboxType, "Center");
		this.jPanel12.add(this.jPanel5, null);
		this.jPanel5.add(this.labelSystem, "West");
		this.jPanel5.add(this.comboboxSystem, "Center");
		this.jPanel12.add(this.jPanel4, null);
		this.jPanel4.add(this.labelLibrary, "West");
		this.jPanel4.add(this.comboboxLibrary, "Center");
		this.jPanel12.add(this.jPanel3, null);
		this.jPanel12.add(this.jPanel6, null);
		this.jPanel12.add(this.jPanel7, null);
		this.jPanel12.add(this.panel, null);
		jPanel12.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		lblDescription.setText("Library:");
		lblDescription.setEnabled(false);

		panel.add(lblDescription, "West");

		panel.add(comboBox, BorderLayout.CENTER);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(
				new ImageIcon(DialogAddLibrary.class.getResource("/org/egomez/irpgeditor/icons/go-last-view-page.png")));
		lblNewLabel.setBounds(19, 11, 46, 79);
		panel1.add(lblNewLabel);
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == 201)
			this.actionCancel.actionPerformed(null);
	}

	public void set(AS400System system, String library, String file, String memberName, String type) {
		if (system != null) {
			this.comboboxSystem.setSelectedItem(system);
			if (library != null) {
				this.comboboxLibrary.setSelectedItem(library);

			}
		}
		if (type != null)
			this.comboboxType.setSelectedItem(type);
	}

	public static void showDialog(Frame frame) {
		DialogAddLibrary dialog = new DialogAddLibrary(frame);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		dialog.setVisible(true);
		dialog.dispose();
	}

	class ActionOk implements ActionListener {
		ActionOk() {
		}

		public void actionPerformed(ActionEvent evt) {

			if ((DialogAddLibrary.this.comboboxSystem.getSelectedIndex() == -1)
					|| (DialogAddLibrary.this.comboboxLibrary.getSelectedIndex() == -1)
					|| (DialogAddLibrary.this.comboboxType.getSelectedItem() == null)) {
				return;
			}
			AS400System system = (AS400System) DialogAddLibrary.this.comboboxSystem.getSelectedItem();
			String library = (String) DialogAddLibrary.this.comboboxLibrary.getSelectedItem();
			String type = (String) DialogAddLibrary.this.comboboxType.getSelectedItem();
			String library1 = (String) DialogAddLibrary.this.comboBox.getSelectedItem();

			try {
				ArrayList<String> libraries = system.getLibrariesList();
				if (libraries == null)
					libraries = new ArrayList<String>();

				if (!type.equals("*FIRST") && !type.equals("*LAST")) {
					system.call("ADDLIBLE " + library + " POSITION(" + type + " " + library1 + ")");
				} else {
					system.call("ADDLIBLE " + library + " POSITION(" + type + ")");
				}
				libraries.add(library);
				system.setLibrariesList(libraries);
				Environment.systems.removeSystem(system);
				Environment.systems.addSystem(system);
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
			DialogAddLibrary.this.setVisible(false);
			// DialogMemberNew.this.hide();
		}
	}

	class ActionSelectFile implements ActionListener {
		ActionSelectFile() {
		}

		public void actionPerformed(ActionEvent evt) {
			DialogAddLibrary.this.labelType.setEnabled(true);
			DialogAddLibrary.this.comboboxType.setEnabled(true);

		}
	}

	class ActionSelectLibrary implements ActionListener {
		ActionSelectLibrary() {
		}

		public void actionPerformed(ActionEvent evt) {
			if (DialogAddLibrary.this.comboboxLibrary.getSelectedIndex() == -1) {
				return;
			}
			AS400System system = (AS400System) DialogAddLibrary.this.listModelSystems.getSelectedItem();
			if (system == null)
				return;
				DialogAddLibrary.this.listModelFiles.removeAllElements();
				DialogAddLibrary.this.lblDescription.setEnabled(true);
				DialogAddLibrary.this.comboboxType.setEnabled(true);
				DialogAddLibrary.this.labelType.setEnabled(true);
				DialogAddLibrary.this.comboBox.setEnabled(false);
		}
	}

	class ActionSelectSystem implements ActionListener {
		ActionSelectSystem() {
		}

		public void actionPerformed(ActionEvent evt) {
			AS400System system = (AS400System) DialogAddLibrary.this.listModelSystems.getSelectedItem();
			if (system == null)
				return;
			try {
				ArrayList<String> list = system.getSourceLibraries();
				while (list.size() > 0) {
					DialogAddLibrary.this.listModelLibraries.addElement(list.remove(0));
				}
				DialogAddLibrary.this.comboboxLibrary.setSelectedIndex(-1);
				DialogAddLibrary.this.comboboxLibrary.addActionListener(DialogAddLibrary.this.actionSelectLibrary);
				DialogAddLibrary.this.labelLibrary.setEnabled(true);
				DialogAddLibrary.this.comboboxLibrary.setEnabled(true);
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
						e.getMessage());
			}
			DialogAddLibrary.this.listModelFiles.removeAllElements();
		}
	}

	class ActionCancel implements ActionListener {
		ActionCancel() {
		}

		public void actionPerformed(ActionEvent evt) {
			DialogAddLibrary.this.setVisible(false);
			// DialogMemberNew.this.hide();
		}
	}
}
