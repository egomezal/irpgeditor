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
import org.egomez.irpgeditor.event.ListenerMemberCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.borland.jbcl.layout.*;
import com.ibm.as400.access.*;

/**
 * 
 * @author Derek Van Kooten.
 */

public class DialogMemberOpen extends JDialog {
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

	JLabel labelFile = new JLabel();

	JLabel labelLibrary = new JLabel();

	JLabel labelMember = new JLabel();

	JLabel labelSystem = new JLabel();

	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel listModelFiles = new DefaultComboBoxModel();

	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel listModelLibraries = new DefaultComboBoxModel();

	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel listModelSystems = new DefaultComboBoxModel();
	ListenerMemberCreated listener;
	Member member;
	JPanel panel1 = new JPanel();

	JTextField textfieldMember = new JTextField();

	VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	JComboBox comboboxFile = new JComboBox(this.listModelFiles);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxLibrary = new JComboBox(this.listModelLibraries);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxSystem = new JComboBox(this.listModelSystems);
	Logger logger = LoggerFactory.getLogger(DialogMemberOpen.class);
	
	public DialogMemberOpen(Frame frame) {
		super(frame, "Open Member", true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(DialogMemberOpen.class.getResource("/org/egomez/irpgeditor/icons/MemberOpen.gif")));
		setResizable(false);
		setSize(301, 182);
		enableEvents(64L);
		try {
			jbInit();
			// pack();
			addActions();
		} catch (Exception ex) {
			//ex.printStackTrace();
			logger.error(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected void addActions() {
		ArrayList<AS400System> listSystems = Environment.systems.getSystems();
		for (int x = 0; x < listSystems.size(); x++) {
			this.listModelSystems.addElement(listSystems.get(x));
		}
		this.comboboxSystem.setSelectedIndex(-1);

		this.comboboxSystem.addActionListener(this.actionSelectSystem);
		this.comboboxLibrary.addActionListener(this.actionSelectLibrary);
		this.comboboxFile.addActionListener(this.actionSelectFile);
		this.buttonOk.addActionListener(this.actionOk);
		this.buttonCancel.addActionListener(this.actionCancel);
	}

	void jbInit() throws Exception {
		setTitle("Open Member from Library");
		this.buttonCancel.setMnemonic('C');
		this.buttonCancel.setText("Cancel");
		this.buttonOk.setMnemonic('O');
		this.buttonOk.setText("Ok");
		jPanel15.setBounds(0, 126, 234, 27);
		this.jPanel15.setLayout(this.flowLayout1);
		this.flowLayout1.setAlignment(2);
		this.flowLayout1.setHgap(2);
		this.flowLayout1.setVgap(2);
		this.jPanel4.setLayout(this.borderLayout4);
		this.jPanel3.setLayout(this.borderLayout3);
		jPanel12.setBounds(75, 0, 210, 115);
		this.jPanel12.setLayout(this.verticalFlowLayout4);
		this.labelFile.setEnabled(false);
		this.labelFile.setText("File: ");
		this.comboboxLibrary.setEnabled(false);
		this.labelLibrary.setEnabled(false);
		this.labelLibrary.setText("Library: ");
		this.verticalFlowLayout4.setHgap(2);
		this.verticalFlowLayout4.setVgap(2);
		this.comboboxFile.setEnabled(false);
		this.jPanel5.setLayout(this.borderLayout5);
		this.labelSystem.setText("System: ");
		this.labelMember.setText("Member: ");
		this.labelMember.setEnabled(false);
		this.jPanel6.setLayout(this.borderLayout6);
		this.textfieldMember.setEnabled(false);
		this.jPanel7.setLayout(this.borderLayout7);
		getContentPane().add(this.panel1, "Center");
		panel1.setLayout(null);
		this.panel1.add(this.jPanel15);
		this.jPanel15.add(this.buttonOk, null);
		this.jPanel15.add(this.buttonCancel, null);
		this.panel1.add(this.jPanel12);
		this.jPanel12.add(this.jPanel5, null);
		this.jPanel5.add(this.labelSystem, "West");
		this.jPanel5.add(this.comboboxSystem, "Center");
		this.jPanel12.add(this.jPanel4, null);
		this.jPanel4.add(this.labelLibrary, "West");
		this.jPanel4.add(this.comboboxLibrary, "Center");
		this.jPanel12.add(this.jPanel3, null);
		this.jPanel3.add(this.labelFile, "West");
		this.jPanel3.add(this.comboboxFile, "Center");
		this.jPanel12.add(this.jPanel6, null);
		this.jPanel6.add(this.labelMember, "West");
		this.jPanel6.add(this.textfieldMember, "Center");
		this.jPanel12.add(this.jPanel7, null);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(
				new ImageIcon(DialogMemberOpen.class.getResource("/org/egomez/irpgeditor/icons/folder-open-5.png")));
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
				if (file != null) {
					this.comboboxFile.setSelectedItem(file);
				}
			}
		}
		if (memberName != null) {
			this.textfieldMember.setText(memberName);
		}
		
	}

	public static void showDialog(Frame frame, AS400System as400, String library, String file, String memberName,
			String type, ListenerMemberCreated listener) {
		DialogMemberOpen dialog = new DialogMemberOpen(frame);
		dialog.listener = listener;

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
		dialog.dispose();
	}

	public static void showDialog(Frame frame, ListenerMemberCreated listener) {
		showDialog(frame, null, null, null, null, null, listener);
	}

	class ActionOk implements ActionListener {
		ActionOk() {
		}

		public void actionPerformed(ActionEvent evt) {
			if (DialogMemberOpen.this.textfieldMember.getText().trim().length() > 10) {
				JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
						"Name must be no more than 10 characters.");
				return;
			}
			if (DialogMemberOpen.this.textfieldMember.getText().trim().indexOf(" ") > -1) {
				JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
						"Name must not contain spaces.");
				return;
			}

			if ((DialogMemberOpen.this.comboboxSystem.getSelectedIndex() == -1)
					|| (DialogMemberOpen.this.comboboxLibrary.getSelectedIndex() == -1)
					|| (DialogMemberOpen.this.comboboxFile.getSelectedIndex() == -1)
					|| (DialogMemberOpen.this.textfieldMember.getText().trim().length() == 0)) {
				return;
			}
			AS400System system = (AS400System) DialogMemberOpen.this.comboboxSystem.getSelectedItem();
			String library = (String) DialogMemberOpen.this.comboboxLibrary.getSelectedItem();
			String file = (String) DialogMemberOpen.this.comboboxFile.getSelectedItem();
			String member = DialogMemberOpen.this.textfieldMember.getText();
			//String type = (String) DialogMemberOpen.this.comboboxType.getSelectedItem();
			Member member1 = null;
			try {
				member1 = new Member(system, library, file, member);
				Project project = Environment.projects.getSelected();
				if (project == null) {
					return;
				}

				ProjectMember projectMember = project.addMember(member1);
				Environment.members.open(projectMember);
				Environment.members.select(projectMember);

			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
			DialogMemberOpen.this.setVisible(false);
			// DialogMemberNew.this.hide();
		}
	}

	class ActionSelectFile implements ActionListener {
		ActionSelectFile() {
		}

		public void actionPerformed(ActionEvent evt) {
			DialogMemberOpen.this.textfieldMember.setEnabled(true);
			DialogMemberOpen.this.labelMember.setEnabled(true);
			
		}
	}

	class ActionSelectLibrary implements ActionListener {
		ActionSelectLibrary() {
		}

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent evt) {
			if (DialogMemberOpen.this.comboboxLibrary.getSelectedIndex() == -1) {
				return;
			}
			AS400System system = (AS400System) DialogMemberOpen.this.listModelSystems.getSelectedItem();
			if (system == null)
				return;
			try {
				DialogMemberOpen.this.comboboxFile.removeActionListener(DialogMemberOpen.this.actionSelectFile);

				DialogMemberOpen.this.listModelFiles.removeAllElements();
				ArrayList<String> list = system
						.getSourceFiles((String) DialogMemberOpen.this.comboboxLibrary.getSelectedItem());
				while (list.size() > 0) {
					DialogMemberOpen.this.listModelFiles.addElement((String) list.remove(0));
				}
				DialogMemberOpen.this.comboboxFile.setSelectedIndex(-1);
				DialogMemberOpen.this.labelFile.setEnabled(true);
				DialogMemberOpen.this.comboboxFile.setEnabled(true);
				DialogMemberOpen.this.comboboxFile.addActionListener(DialogMemberOpen.this.actionSelectFile);
			} catch (SQLException e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
						e.getMessage());
			}
		}
	}

	class ActionSelectSystem implements ActionListener {
		ActionSelectSystem() {
		}

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent evt) {
			AS400System system = (AS400System) DialogMemberOpen.this.listModelSystems.getSelectedItem();
			if (system == null)
				return;
			try {
				ArrayList<String> list = system.getSourceLibraries();
				while (list.size() > 0) {
					DialogMemberOpen.this.listModelLibraries.addElement(list.remove(0));
				}
				DialogMemberOpen.this.comboboxLibrary.setSelectedIndex(-1);
				DialogMemberOpen.this.comboboxLibrary.addActionListener(DialogMemberOpen.this.actionSelectLibrary);
				DialogMemberOpen.this.labelLibrary.setEnabled(true);
				DialogMemberOpen.this.comboboxLibrary.setEnabled(true);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
						e.getMessage());
			}
			DialogMemberOpen.this.labelFile.setEnabled(false);
			DialogMemberOpen.this.comboboxFile.setEnabled(false);
			DialogMemberOpen.this.listModelFiles.removeAllElements();
		}
	}

	class ActionCancel implements ActionListener {
		ActionCancel() {
		}

		public void actionPerformed(ActionEvent evt) {
			DialogMemberOpen.this.setVisible(false);
			// DialogMemberNew.this.hide();
		}
	}
}
