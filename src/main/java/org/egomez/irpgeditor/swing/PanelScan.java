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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.borland.jbcl.layout.*;
import java.sql.SQLException;

/**
 * Scan systems for some text.
 * 
 * @author Derek Van Kooten
 */
public class PanelScan extends PanelTool {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6282222667558157250L;
	boolean stop = false;
	TreeModelNode treeModelScan = new TreeModelNode();
	TreeCellRendererNode treeCellRendererNode = new TreeCellRendererNode();

	ActionScanStart actionScanStart = new ActionScanStart();
	ActionScanStop actionScanStop = new ActionScanStop();

	BorderLayout borderLayout1 = new BorderLayout();
	JSplitPane splitpaneScan = new JSplitPane();
	BorderLayout borderLayout15 = new BorderLayout();
	JTextArea textareaScanResults = new JTextArea();
	JPanel panelScanResults = new JPanel();
	JScrollPane scrollpaneScanResults = new JScrollPane();
	JPanel panelScan = new JPanel();
	JPanel panelScanStop = new JPanel();
	JLabel label1 = new JLabel();
	JLabel label2 = new JLabel();
	JLabel label3 = new JLabel();
	JLabel label4 = new JLabel();
	VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	JPanel panelScanStart = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel labelScanTerm = new JLabel();
	JButton buttonScanStart = new JButton();
	JPanel panelScanTerm = new JPanel();
	BorderLayout borderLayout14 = new BorderLayout();
	JTextField textfieldScanTerm = new JTextField();
	JScrollPane scrollpaneScanTree = new JScrollPane();
	JTree treeScan = new JTree();
	CardLayout cardlayout = new CardLayout();
	JButton buttonStop = new JButton();
	Logger logger = LoggerFactory.getLogger(PanelScan.class);

	public PanelScan() {
		setName("Scan");
		try {
			jbInit();
			buttonScanStart.addActionListener(actionScanStart);
			buttonStop.addActionListener(actionScanStop);
			treeScan.setModel(treeModelScan);
			treeScan.setCellRenderer(treeCellRendererNode);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		labelScanTerm.setText("Term: ");
		textfieldScanTerm.setText("");
		buttonScanStart.setText("Start");
		buttonScanStart.setMargin(new Insets(0, 0, 0, 0));
		panelScanTerm.setLayout(borderLayout14);
		panelScanTerm.add(labelScanTerm, BorderLayout.WEST);
		panelScanTerm.add(textfieldScanTerm, BorderLayout.CENTER);
		panelScanTerm.add(buttonScanStart, BorderLayout.EAST);

		treeScan.setRootVisible(false);
		scrollpaneScanTree.getViewport().add(treeScan, null);
		panelScanStart.setLayout(borderLayout2);
		panelScanStart.add(scrollpaneScanTree, BorderLayout.CENTER);
		panelScanStart.add(panelScanTerm, BorderLayout.NORTH);

		buttonStop.setText("Stop");
		label1.setText("");
		label2.setText("");
		label3.setText("");
		panelScanStop.setLayout(verticalFlowLayout1);
		panelScanStop.add(buttonStop, null);
		panelScanStop.add(label1, null);
		panelScanStop.add(label2, null);
		panelScanStop.add(label3, null);
		panelScanStop.add(label4, null);

		panelScan.setLayout(cardlayout);
		panelScan.add("start", panelScanStart);
		panelScan.add("stop", panelScanStop);

		scrollpaneScanResults.getViewport().add(textareaScanResults, null);
		panelScanResults.setLayout(borderLayout15);
		panelScanResults.add(scrollpaneScanResults, BorderLayout.CENTER);

		splitpaneScan.add(panelScan, JSplitPane.LEFT);
		splitpaneScan.add(panelScanResults, JSplitPane.RIGHT);
		splitpaneScan.setDividerLocation(200);

		this.setLayout(borderLayout1);
		this.add(splitpaneScan, BorderLayout.CENTER);
	}

	class ActionScanStop implements ActionListener {
                @Override
		public void actionPerformed(ActionEvent evt) {
			stop = true;
			cardlayout.first(panelScan);
		}
	}

	/**
	 * scans the selected areas in the scan tree for the term specified.
	 * displays the results in the scan results pane.
	 */
	class ActionScanStart implements ActionListener {
                @Override
		public void actionPerformed(ActionEvent evt) {
			cardlayout.last(panelScan);
			// scan should be a background task.
			new Thread() {
				@SuppressWarnings("rawtypes")
                                @Override
				public void run() {
					AS400System system;
					ArrayList listLibs, listFiles, listMembers, listResults;
					String lib, file;
					Member member;

					setPriority(Thread.MIN_PRIORITY);
					system = Environment.systems.getDefault();
					try {
						setText(label1, "System: " + system.getName());
						setText(label2, "");
						setText(label3, "");
						setText(label4, "");
						listLibs = system.getSourceLibraries();
						if (stop)
							return;
						for (int l = 0; l < listLibs.size(); l++) {
							lib = (String) listLibs.get(l);
							setText(label2, "Library: " + lib + " (" + (l + 1) + " of " + listLibs.size() + ")");
							setText(label3, "");
							setText(label4, "");
							listFiles = system.getSourceFiles(lib);
							if (stop)
								return;
							for (int f = 0; f < listFiles.size(); f++) {
								file = (String) listFiles.get(f);
								setText(label3, "File: " + file + " (" + (f + 1) + " of " + listFiles.size() + ")");
								setText(label4, "");
								try {
									listMembers = system.listMembers(lib, file);
									if (stop)
										return;
									for (int m = 0; m < listMembers.size(); m++) {
										member = (Member) listMembers.get(m);
										setText(label4, "Member: " + member.getName() + " (" + (m + 1) + " of "
												+ listMembers.size() + ")");
										try {
											listResults = system.search(lib, file, member.getName(),
													textfieldScanTerm.getText(), false);
											if (stop)
												return;
											for (int x = 0; x < listResults.size(); x++) {
												append("FOUND: " + system.getName() + ", " + lib + ", " + file + ", "
														+ member.getName() + ", " + listResults.get(x) + "\n");
											}
										} catch (Exception e) {
											// e.printStackTrace();
											logger.error(e.getMessage());
											// JOptionPane.showMessageDialog(FrameRPGEditor.frameRPGEditor,
											// e.getMessage());
										}
									}
								} catch (Exception e) {
									// e.printStackTrace();
									logger.error(e.getMessage());
									// JOptionPane.showMessageDialog(FrameRPGEditor.frameRPGEditor,
									// e.getMessage());
								}
							}
						}
					} catch (SQLException e) {
						// e.printStackTrace();
						logger.error(e.getMessage());
						// JOptionPane.showMessageDialog(FrameRPGEditor.frameRPGEditor,
						// e.getMessage());
					}
					setText(label1, "Scan completed " + system.getName());
					setText(label2, "");
					setText(label3, "");
					setText(label4, "");
				}
			}.start();
		}

		public void setText(final JLabel label, final String text) {
			SwingUtilities.invokeLater(() -> {
                            label.setText(text);
                        });
		}

		public void append(final String text) {
			SwingUtilities.invokeLater(() -> {
                            textareaScanResults.append(text);
                        });
		}
	}
}
