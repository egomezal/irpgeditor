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
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.ListenerAS400Systems;
import org.egomez.irpgeditor.event.ListenerSubmitJob;
import org.egomez.irpgeditor.icons.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.ui.util.CommandPrompter;

import org.egomez.irpgeditor.SubmitJob;

//import com.ibm.as400.ui.util.*;

/**
 * @author Derek Van Kooten.
 */
public class PanelQcmdexec extends PanelTool
		implements QcmdexecOutput, ListenerAS400Systems, KeyListener, ClosableTab, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1585840902513976999L;

	ActionCommandPrompter actionCommandPrompter = new ActionCommandPrompter();

	ActionQcmdexec actionQcmdexec = new ActionQcmdexec();

	BorderLayout borderLayout1 = new BorderLayout();
	Logger logger = LoggerFactory.getLogger(PanelQcmdexec.class);
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();

	BorderLayout borderLayoutQcmdExecPrompt = new BorderLayout();

	BorderLayout borderLayoutQcmdexec = new BorderLayout();

	JButton buttonPrompt = new JButton();
	JCheckBox checkboxSql = new JCheckBox();
	JFrame frame;
	int index = 0;

	JPanel jPanel1 = new JPanel();

	JScrollPane jScrollPane1 = new JScrollPane();

	JSplitPane jSplitPane1 = new JSplitPane();

	JLabel labelCurrentSystem = new JLabel();

	@SuppressWarnings("rawtypes")
	DefaultListModel listModel = new DefaultListModel();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	JList jList1 = new JList(this.listModel);

	JPanel panelQcmdexecPrompt = new JPanel();

	JPanel panelQueue = new JPanel();
	JPanel panelResults = new JPanel();

	JScrollPane scrollpaneQcmdexec = new JScrollPane();

	JTextPane textareaQcmdexecMessages = new JTextPane();
	JTextField textfieldQcmdexec = new JTextField();

	public PanelQcmdexec() {
		setName("QCMDEXEC");
		try {
			jbInit();
			this.textfieldQcmdexec.addActionListener(this.actionQcmdexec);
			this.textfieldQcmdexec.addKeyListener(this);
			this.buttonPrompt.addActionListener(this.actionCommandPrompter);
			new HandlerKeyPressed(this.textareaQcmdexecMessages);
			Environment.systems.addListener(this);

			defaultSytem(Environment.systems.getDefault());
			new Thread(this).start();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void addedSytem(AS400System system) {
	}

	public void append(String text, Color color) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, color);
		try {
			this.textareaQcmdexecMessages.getDocument()
					.insertString(this.textareaQcmdexecMessages.getDocument().getLength(), text, attributes);
			int length = this.textareaQcmdexecMessages.getText().length();
			this.textareaQcmdexecMessages.setSelectionStart(length);
			this.textareaQcmdexecMessages.setSelectionEnd(length);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	protected void callBuffer(int direction) {
		AS400System system = Environment.systems.getDefault();
		if (system == null) {
			return;
		}
		ArrayList<String> listCallBuffer = system.getCallBuffer();
		if (listCallBuffer.size() == 0) {
			return;
		}
		if (this.index >= listCallBuffer.size()) {
			this.index = 0;
		}
		if (this.index < 0) {
			this.index = 0;
			this.textfieldQcmdexec.setText("");
		} else {
			this.textfieldQcmdexec.setText((String) listCallBuffer.get(listCallBuffer.size() - this.index - 1));
		}
		this.index += direction;
	}

	public void clear() {
		this.textareaQcmdexecMessages.setText("");
	}

	public void closeTab() {
		setContainer(null);
		close();
	}

	public void defaultSytem(AS400System system) {
		if (system == null) {
			this.labelCurrentSystem.setText("NONE:>");
			this.buttonPrompt.setEnabled(false);
			this.textfieldQcmdexec.setEnabled(false);
			this.checkboxSql.setEnabled(false);
		} else {
			this.labelCurrentSystem.setText(system.getName() + ":>");
			this.buttonPrompt.setEnabled(true);
			this.textfieldQcmdexec.setEnabled(true);
			this.checkboxSql.setEnabled(true);
		}
	}

	public void focus() {
		fireRequestingFocus();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PanelQcmdexec.this.textfieldQcmdexec.requestFocus();
			}
		});
	}

	public Icon getIcon() {
		return Icons.iconQcmdexc;
	}

	private void jbInit() throws Exception {
		setLayout(this.borderLayoutQcmdexec);
		this.borderLayoutQcmdExecPrompt.setVgap(2);
		this.borderLayoutQcmdExecPrompt.setHgap(2);
		this.panelQcmdexecPrompt.setLayout(this.borderLayoutQcmdExecPrompt);
		this.textfieldQcmdexec.setEnabled(false);
		this.jPanel1.setLayout(this.borderLayout1);
		this.buttonPrompt.setEnabled(false);
		this.buttonPrompt.setMargin(new Insets(0, 0, 0, 0));
		this.buttonPrompt.setText("prompt");
		this.checkboxSql.setEnabled(false);
		this.checkboxSql.setMargin(new Insets(0, 0, 0, 0));
		this.checkboxSql.setText("SQL");
		this.labelCurrentSystem.setText("NONE:");
		this.jSplitPane1.setOrientation(0);
		this.panelResults.setLayout(this.borderLayout2);
		this.panelQueue.setLayout(this.borderLayout3);
		this.panelResults.add(this.scrollpaneQcmdexec, "Center");
		this.jSplitPane1.add(this.jScrollPane1, "top");
		this.jScrollPane1.setViewportView(this.jList1);
		add(this.panelQcmdexecPrompt, "North");
		this.scrollpaneQcmdexec.setViewportView(this.textareaQcmdexecMessages);
		this.panelQcmdexecPrompt.add(this.textfieldQcmdexec, "Center");
		this.panelQcmdexecPrompt.add(this.jPanel1, "East");
		this.jPanel1.add(this.buttonPrompt, "West");
		this.jPanel1.add(this.checkboxSql, "Center");
		this.panelQcmdexecPrompt.add(this.labelCurrentSystem, "West");
		add(this.jSplitPane1, "Center");
		this.jSplitPane1.add(this.panelResults, "bottom");
	}

	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == 38) {
			callBuffer(1);
		} else if (evt.getKeyCode() == 40) {
			callBuffer(-1);
		} else if (evt.getKeyCode() == 27) {
			this.index = 0;
			this.textfieldQcmdexec.setText("");
		} else {
			if (evt.getKeyCode() == KeyEvent.VK_F4) {
				String cmd = PanelQcmdexec.this.textfieldQcmdexec.getText().trim();
				if (cmd.length() == 0) {
					return;
				}

				AS400System as400system = Environment.systems.getDefault();
				CommandPrompter cp = new CommandPrompter(PanelQcmdexec.this.frame, as400system.getAS400(), cmd);
				if (cp.showDialog() != 0) {
					return;
				}
				PanelQcmdexec.this.textfieldQcmdexec.setText(cp.getCommandString());
				PanelQcmdexec.this.actionQcmdexec.actionPerformed(null);
			}
		}
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
	}

	public void removedSytem(AS400System system) {
	}

	public void run() {
		while (true)
			try {
				Thread.currentThread();
				Thread.sleep(500L);
				SubmitJob job = null;
				// continue;
				if (PanelQcmdexec.this.listModel.size() > 0) {
					job = (SubmitJob) this.listModel.get(0);

					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							if (PanelQcmdexec.this.listModel.size() > 0)
								PanelQcmdexec.this.listModel.remove(0);
						}
					});
					job.execute();
				}
				if (this.listModel.size() > 0)
					;

				continue;

			} catch (Exception e) {
				logger.error(e.getMessage());
				// e.printStackTrace();
			}
	}

	public void setJFrame(JFrame frame) {
		this.frame = frame;
	}

	public void submitJob(final AS400System system, final String command, final ListenerSubmitJob listener) {
		SwingUtilities.invokeLater(new Runnable() {
			/*
			 * private final String val$command; private final ListenerSubmitJob
			 * val$listener; private final AS400System val$system;
			 */

			@SuppressWarnings("unchecked")
			public void run() {
				PanelQcmdexec.this.listModel.addElement(new SubmitJob(system, command, listener));
			}

		});
	}

	class ActionQcmdexec implements ActionListener {
		ActionQcmdexec() {
		}

		public void actionPerformed(ActionEvent evt) {
			String cmd = PanelQcmdexec.this.textfieldQcmdexec.getText().trim();
			if (cmd.length() == 0) {
				return;
			}
			PanelQcmdexec.this.textfieldQcmdexec.setText("");

			AS400System as400system = Environment.systems.getDefault();

			if (as400system == null)
				return;
			try {
				if (PanelQcmdexec.this.checkboxSql.isSelected()) {
					as400system.sqlCall(cmd);
				} else
					Environment.qcmdexec.submitJob(as400system, cmd);
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	class ActionCommandPrompter implements ActionListener {
		ActionCommandPrompter() {
		}

		public void actionPerformed(ActionEvent evt) {
			String cmd = PanelQcmdexec.this.textfieldQcmdexec.getText().trim();
			if (cmd.length() == 0) {
				return;
			}

			AS400System as400system = Environment.systems.getDefault();
			CommandPrompter cp = new CommandPrompter(PanelQcmdexec.this.frame, as400system.getAS400(), cmd);
			if (cp.showDialog() != 0) {
				return;
			}
			PanelQcmdexec.this.textfieldQcmdexec.setText(cp.getCommandString());
			PanelQcmdexec.this.actionQcmdexec.actionPerformed(null);
		}
	}
}