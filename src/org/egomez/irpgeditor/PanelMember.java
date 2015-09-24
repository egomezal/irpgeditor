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
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.flowchart.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.refactor.*;
import org.egomez.irpgeditor.swing.*;
import org.egomez.irpgeditor.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.borland.jbcl.layout.*;

/**
 * 
 * @author Derek Van Kooten.
 */
public class PanelMember extends PanelTool implements SourceLoader, ListenerSave, Runnable, ListenerPanelDspf,
		ClosableTab, ListenerParserSelection, ListenerStructure, ListenerMember, ListenerParserFlowChart {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2464649540392523987L;
	ProjectMember projectMember;
	SourceParser sourceParser = new SourceParser();
	NodeSourceBlocks nodeSourceBlocks;
	RPGSourceHighlighter sourceHighlighter;
	SourceBlock selectedBlock;
	@SuppressWarnings("rawtypes")
	Enumeration expands;
	// used to keep a running total of source lines loaded as lines are loaded.
	int count = 0;
	boolean closeAfterSave = false;
	boolean currentlySaving = false;
	boolean currentlyLoading = false;
	JToolTip toolTip = new JToolTip();

	TableModelKeywords tableModelKeywords = new TableModelKeywords();

	NodeFiles nodeFiles;
	NodeDuplicateCode nodeDuplicateCode;
	TreeModelSourceStructure treeModel = new TreeModelSourceStructure();
	@SuppressWarnings("rawtypes")
	DefaultListModel listModelOptions = new DefaultListModel();
	@SuppressWarnings("rawtypes")
	DefaultListModel listModelFields = new DefaultListModel();
	@SuppressWarnings("rawtypes")
	DefaultListModel listModelAttributes = new DefaultListModel();

	HandlerMouse handlerMouse = new HandlerMouse();
	HandlerCaret handlerCaret = new HandlerCaret();
	HandlerMouseDspf handlerMouseDspf = new HandlerMouseDspf();
	HandlerRightClickSource handlerRightClickSource = new HandlerRightClickSource();

	ActionCutMenu actionCutM = new ActionCutMenu();
	ActionCopyMenu actionCopyM = new ActionCopyMenu();
	ActionPasteMenu actionPasteM = new ActionPasteMenu();

	ActionSelectAll actionSelectAll = new ActionSelectAll();
	ActionCut actionCut = new ActionCut();

	ActionCopy actionCopy = new ActionCopy();
	ActionPaste actionPaste = new ActionPaste();
	ActionMemberRemove actionMemberRemove = new ActionMemberRemove();
	ActionSetSourceType actionSetSourceType = new ActionSetSourceType();
	ActionReference actionReference = new ActionReference();
	ActionMemberCompile actionMemberCompile = new ActionMemberCompile();
	ActionMemberSave actionMemberSave = new ActionMemberSave();
	ActionMemberSaveLocal actionMemberSaveLocal = new ActionMemberSaveLocal();
	ActionRefactor actionRefactorUncomment = new ActionRefactor("Uncomment", new RefactorUncomment(),
			new Integer(KeyEvent.VK_U));
	ActionRefactor actionRefactorComment = new ActionRefactor("Comment", new RefactorComment(),
			new Integer(KeyEvent.VK_M));
	ActionRefactor actionRefactorFreeForm = new ActionRefactor("Free Form", new RefactorFreeForm(),
			new Integer(KeyEvent.VK_F));
	ActionRefactor actionRefactorComparisons = new ActionRefactor("Comparisons", new RefactorComparisons(),
			new Integer(KeyEvent.VK_C));
	ActionRefactor actionRefactorNewSubroutine = new ActionRefactor("New Subroutine", new RefactorNewSubroutine(),
			new Integer(KeyEvent.VK_N));
	ActionRefactor actionRefactorCallSubroutine = new ActionRefactor("Call Subroutine", new RefactorCallSubroutine(),
			new Integer(KeyEvent.VK_S));

	ActionPrint actionPrint = new ActionPrint();
	ActionFocus actionFocus;
	ActionMemberClose actionMemberClose = new ActionMemberClose();
	ActionInputTypeSelected actionInputTypeSelected = new ActionInputTypeSelected();
	ActionUpdate actionUpdate = new ActionUpdate();
	ActionDelete actionDelete = new ActionDelete();
	ActionItemSelected actionItemSelected = new ActionItemSelected();
	ActionOptionRemove actionOptionRemove = new ActionOptionRemove();
	ActionOptionAdd actionOptionAdd = new ActionOptionAdd();
	ActionCompileType actionCompileType = new ActionCompileType();
	ActionLibrarySelected actionLibrarySelected = new ActionLibrarySelected();

	BorderLayout borderLayout1 = new BorderLayout();
	JTabbedPane jTabbedPane1 = new JTabbedPane();
	JPanel panelSource = new JPanel();
	JPanel panelSettings = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	JScrollPane scrollpaneText = new JScrollPane();
	JPanel panelDocs = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JScrollPane jScrollPane2 = new JScrollPane();
	JPanel jPanel1 = new JPanel();
	VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	JPanel jPanel2 = new JPanel();
	Border border1;
	TitledBorder titledBorder1;
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	JTextField textfieldMemberLibrary = new JTextField();
	JLabel jLabel1 = new JLabel();
	JPanel jPanel5 = new JPanel();
	JTextField textfieldSystemUser = new JTextField();
	BorderLayout borderLayout5 = new BorderLayout();
	JLabel jLabel2 = new JLabel();
	VerticalFlowLayout verticalFlowLayout3 = new VerticalFlowLayout();
	JPanel jPanel6 = new JPanel();
	JTextField textfieldMemberFile = new JTextField();
	BorderLayout borderLayout6 = new BorderLayout();
	JLabel jLabel3 = new JLabel();
	JPanel jPanel7 = new JPanel();
	JTextField textfieldMemberName = new JTextField();
	BorderLayout borderLayout7 = new BorderLayout();
	JLabel jLabel4 = new JLabel();
	JPanel jPanel8 = new JPanel();
	JTextField textfieldSystemAddress = new JTextField();
	BorderLayout borderLayout8 = new BorderLayout();
	JLabel jLabel5 = new JLabel();
	JPanel jPanel9 = new JPanel();
	JTextField textfieldSystemName = new JTextField();
	BorderLayout borderLayout9 = new BorderLayout();
	JLabel jLabel6 = new JLabel();
	JPanel jPanel13 = new JPanel();
	Border border2;
	TitledBorder titledBorder2;
	Border border3;
	TitledBorder titledBorder3;
	JPanel jPanel11 = new JPanel();
	BorderLayout borderLayout11 = new BorderLayout();
	JLabel labelDescription = new JLabel();
	JLabel labelPosition = new JLabel();
	JPanel jPanel14 = new JPanel();
	BorderLayout borderLayout12 = new BorderLayout();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxOptions = new JComboBox(new Object[] { "", "OBJTYPE(*MODULE)", "COMMIT(*NONE)",
			"DBGVIEW(*SOURCE)", "OPTION(*SOURCE)", "ALWNULL(*YES)" });
	JLabel jLabel10 = new JLabel();
	JButton buttonAdd = new JButton();
	JPanel jPanel12 = new JPanel();
	JScrollPane jScrollPane3 = new JScrollPane();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	JList listOptions = new JList(listModelOptions);
	JButton buttonRemove = new JButton();
	BorderLayout borderLayout13 = new BorderLayout();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxCompileType = new JComboBox(new Object[] { "", "CRTBNDRPG", "CRTRPGMOD", "CRTSQLRPGI", "CRTDSPF",
			"CRTCLPGM", "CRTRPGPGM", "CRTPRTF", "CRTPF", "CRTLF" });
	JLabel jLabel7 = new JLabel();
	GridLayout gridLayout1 = new GridLayout();
	JPanel jPanel10 = new JPanel();
	JTextField textfieldMemberType = new JTextField();
	BorderLayout borderLayout10 = new BorderLayout();
	JLabel jLabel8 = new JLabel();
	JPanel panelDesign = new JPanel();
	BorderLayout borderLayout14 = new BorderLayout();
	JSplitPane jSplitPane2 = new JSplitPane();
	PanelDspf panelDspf = new PanelDspf();
	JPanel jPanel15 = new JPanel();
	JPanel jPanel17 = new JPanel();
	JTextPanePrintable editorPaneSource = new JTextPanePrintable();
	BorderLayout borderLayout15 = new BorderLayout();
	PanelBreakPoints panelBreakPoints = new PanelBreakPoints();
	BorderLayout borderLayout16 = new BorderLayout();
	JSplitPane jSplitPane1 = new JSplitPane();
	JPanel jPanel16 = new JPanel();
	JPanel jPanel18 = new JPanel();
	BorderLayout borderLayout17 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	JList listboxFields = new JList(listModelFields);
	JLabel jLabel9 = new JLabel();
	TitledBorder titledBorder4;
	JPanel jPanel19 = new JPanel();
	JTextField textfieldName = new JTextField();
	BorderLayout borderLayout18 = new BorderLayout();
	JPanel jPanel20 = new JPanel();
	JLabel labelRow = new JLabel();
	JSpinner textfieldRow = new JSpinner();
	JLabel labelCol = new JLabel();
	JSpinner textfieldCol = new JSpinner();
	JLabel labelWidth = new JLabel();
	JTextField textfieldWidth = new JTextField();
	JPanel panelStatus = new JPanel();
	JLabel panelLoading = new JLabel();
	BorderLayout borderLayout19 = new BorderLayout();
	Border border4;
	TitledBorder titledBorder5;
	JPanel jPanel24 = new JPanel();
	VerticalFlowLayout verticalFlowLayout6 = new VerticalFlowLayout();
	BorderLayout borderLayout21 = new BorderLayout();
	JLabel labelIndicators = new JLabel();
	JTextField textfieldIndicators1 = new JTextField();
	JTextField textfieldIndicators2 = new JTextField();
	JTextField textfieldIndicators3 = new JTextField();
	PanelChangedDate panelDate = new PanelChangedDate();
	JPanel jPanel21 = new JPanel();
	BorderLayout borderLayout22 = new BorderLayout();
	JToolBar jToolBar1 = new JToolBar();
	JPanel jPanel26 = new JPanel();
	JLabel labelMouseCoordinates = new JLabel();
	FlowLayout flowLayout2 = new FlowLayout();
	JPanel jPanel27 = new JPanel();
	BorderLayout borderLayout23 = new BorderLayout();
	JScrollPane jScrollPane5 = new JScrollPane();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	JComboBox comboboxType = new JComboBox(new Object[] { "CONSTANT", "INPUT", "OUTPUT", "INPUT/OUTPUT", "HIDDEN" });
	JPanel panelKeywords = new JPanel();
	JLabel labelKeywords = new JLabel();
	Border border5;
	JScrollPane jScrollPane6 = new JScrollPane();
	JTable tableKeywords = new JTable(tableModelKeywords);
	@SuppressWarnings("rawtypes")
	JComboBox comboboxKeyword = new JComboBox();
	JPanel jPanel22 = new JPanel();
	JButton buttonDelete = new JButton();
	JButton buttonUpdate = new JButton();
	FlowLayout flowLayout1 = new FlowLayout();
	JPanel jPanel23 = new JPanel();
	JLabel labelDataType = new JLabel();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	JComboBox comboboxDataType = new JComboBox(new Object[] { "A", "Y" });
	JLabel labelLength = new JLabel();
	JSpinner spinnerLength = new JSpinner();
	JLabel labelDecimalPlaces = new JLabel();
	JSpinner spinnerDecimalPlaces = new JSpinner();
	FlowLayout flowLayout3 = new FlowLayout();
	FlowLayout flowLayout4 = new FlowLayout();
	FlowLayout flowLayout5 = new FlowLayout();
	Component component1;
	JLabel jLabel11 = new JLabel();
	@SuppressWarnings("rawtypes")
	JComboBox textfieldDestinationLibrary = new JComboBox();
	Component component2;
	JCheckBox checkboxReference = new JCheckBox();
	JButton buttonSetSourceType = new JButton();
	JPanel panelFlowChartTab = new JPanel();
	BorderLayout borderLayout20 = new BorderLayout();
	JScrollPane scrollpaneFlowChart = new JScrollPane();
	JPanel panelFlowChartContainer = new JPanel();
	PanelLines panelLines = new PanelLines();
	OverlayLayout2 overlayLayout21 = new OverlayLayout2();
	JTextPane jTextPane1 = new JTextPane();
	PanelFlowChart panelFlowChart = new PanelFlowChart();
	FlowLayout flowLayout6 = new FlowLayout();
	Logger logger = LoggerFactory.getLogger(PanelMember.class);

	// private Document editorPaneDocument;

	// protected UndoHandler undoHandler = new UndoHandler();
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();

	public PanelMember(ProjectMember projectMember) throws SQLException, Exception {
		super();
		this.projectMember = projectMember;
		try {
			jbInit();
			init();
			addActions();
		} catch (Exception e) {
			// e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			logger.error(e.getMessage());
			Environment.members.close(projectMember, false);
			throw new Exception(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));
		titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140)),
				"Compile Settings");
		border2 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));
		titledBorder2 = new TitledBorder(border2, "Member Settings");
		border3 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));
		titledBorder3 = new TitledBorder(border3, "System Settings");
		titledBorder4 = new TitledBorder("");
		border4 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));
		titledBorder5 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140)),
				"");
		border5 = BorderFactory.createCompoundBorder(titledBorder5, BorderFactory.createEmptyBorder(5, 5, 5, 5));
		component1 = Box.createHorizontalStrut(8);
		component2 = Box.createHorizontalStrut(8);
		this.setLayout(borderLayout1);
		jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
		jTabbedPane1.setEnabled(false);
		panelSource.setLayout(borderLayout2);
		panelSettings.setLayout(borderLayout3);
		jPanel1.setLayout(verticalFlowLayout1);
		jPanel2.setLayout(verticalFlowLayout2);
		jPanel3.setLayout(verticalFlowLayout3);
		jPanel4.setLayout(borderLayout4);
		textfieldMemberLibrary.setEditable(false);
		jLabel1.setText("Library: ");
		jPanel5.setLayout(borderLayout5);
		textfieldSystemUser.setEditable(false);
		jLabel2.setText("User: ");
		jPanel6.setLayout(borderLayout6);
		textfieldMemberFile.setEditable(false);
		jLabel3.setText("File: ");
		jPanel7.setLayout(borderLayout7);
		textfieldMemberName.setEditable(false);
		jLabel4.setText("Name: ");
		jPanel8.setLayout(borderLayout8);
		textfieldSystemAddress.setEditable(false);
		jLabel5.setText("Address: ");
		jPanel9.setLayout(borderLayout9);
		textfieldSystemName.setEditable(false);
		jLabel6.setText("Name: ");
		jPanel13.setBorder(titledBorder1);
		jPanel13.setPreferredSize(new Dimension(237, 237));
		jPanel13.setLayout(borderLayout13);
		jPanel2.setBorder(titledBorder2);
		jPanel3.setBorder(titledBorder3);
		verticalFlowLayout1.setHgap(2);
		verticalFlowLayout1.setVgap(2);
		verticalFlowLayout2.setHgap(2);
		verticalFlowLayout2.setVgap(2);
		verticalFlowLayout3.setHgap(2);
		verticalFlowLayout3.setVgap(2);
		jPanel11.setLayout(borderLayout11);
		labelDescription.setFont(new java.awt.Font("DialogInput", 0, 14));
		labelDescription.setBorder(BorderFactory.createLoweredBevelBorder());
		labelDescription.setText(" ");
		labelPosition.setFont(new java.awt.Font("DialogInput", 0, 14));
		labelPosition.setBorder(BorderFactory.createLoweredBevelBorder());
		jPanel14.setLayout(borderLayout12);
		comboboxOptions.setEditable(true);
		jLabel10.setPreferredSize(new Dimension(17, 17));
		jLabel10.setText("Destination Library");
		buttonAdd.setMargin(new Insets(0, 0, 0, 0));
		buttonAdd.setText("Add");
		jPanel12.setLayout(gridLayout1);
		buttonRemove.setMargin(new Insets(0, 0, 0, 0));
		buttonRemove.setText("Remove");
		jLabel7.setText("Type: ");
		jScrollPane3.setPreferredSize(new Dimension(200, 132));
		jPanel10.setLayout(borderLayout10);
		textfieldMemberType.setEditable(false);
		jLabel8.setText("Type: ");
		panelDesign.setLayout(borderLayout14);
		jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jSplitPane2.setLastDividerLocation(500);
		editorPaneSource.setFont(new java.awt.Font("DialogInput", 0, 14));
		editorPaneSource.setOpaque(false);
		jPanel17.setLayout(borderLayout15);
		panelBreakPoints.setMinimumSize(new Dimension(40, 40));
		panelBreakPoints.setPreferredSize(new Dimension(40, 40));
		panelDate.setMinimumSize(new Dimension(50, 50));
		panelDate.setPreferredSize(new Dimension(50, 50));
		jPanel15.setLayout(borderLayout16);
		jPanel16.setLayout(borderLayout17);
		jLabel9.setBorder(BorderFactory.createRaisedBevelBorder());
		jLabel9.setText("Fields");
		jPanel18.setLayout(borderLayout21);
		jPanel19.setLayout(borderLayout18);
		textfieldName.setEnabled(false);
		labelRow.setEnabled(false);
		labelRow.setToolTipText("");
		labelRow.setText(" Row: ");
		labelCol.setEnabled(false);
		labelCol.setText("Column: ");
		labelWidth.setEnabled(false);
		labelWidth.setText(" Width: ");
		jPanel20.setLayout(flowLayout3);
		textfieldRow.setEnabled(false);
		textfieldRow.setMinimumSize(new Dimension(57, 20));
		textfieldRow.setPreferredSize(new Dimension(57, 20));
		textfieldCol.setEnabled(false);
		textfieldCol.setMinimumSize(new Dimension(57, 20));
		textfieldCol.setPreferredSize(new Dimension(57, 20));
		textfieldWidth.setEnabled(false);
		textfieldWidth.setMinimumSize(new Dimension(46, 24));
		textfieldWidth.setPreferredSize(new Dimension(46, 24));
		textfieldWidth.setEditable(false);
		textfieldWidth.setText(" ");
		panelLoading.setFont(new java.awt.Font("Dialog", 0, 20));
		panelLoading.setHorizontalAlignment(SwingConstants.CENTER);
		panelLoading.setHorizontalTextPosition(SwingConstants.CENTER);
		panelLoading.setText("Loading Source......");
		panelStatus.setLayout(borderLayout19);
		jPanel24.setLayout(verticalFlowLayout6);
		labelIndicators.setEnabled(false);
		labelIndicators.setText(" Indicators: ");
		textfieldIndicators1.setEnabled(false);
		textfieldIndicators1.setMaximumSize(new Dimension(30, 21));
		textfieldIndicators1.setMinimumSize(new Dimension(30, 21));
		textfieldIndicators1.setPreferredSize(new Dimension(30, 21));
		textfieldIndicators2.setEnabled(false);
		textfieldIndicators2.setMaximumSize(new Dimension(30, 21));
		textfieldIndicators2.setMinimumSize(new Dimension(30, 21));
		textfieldIndicators2.setPreferredSize(new Dimension(30, 21));
		textfieldIndicators3.setEnabled(false);
		textfieldIndicators3.setMaximumSize(new Dimension(30, 21));
		textfieldIndicators3.setMinimumSize(new Dimension(30, 21));
		textfieldIndicators3.setPreferredSize(new Dimension(30, 21));
		jPanel21.setLayout(borderLayout22);
		labelMouseCoordinates.setText("Row: 0 Col: 0");
		jPanel26.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.LEFT);
		flowLayout2.setHgap(0);
		flowLayout2.setVgap(0);
		jPanel27.setLayout(borderLayout23);
		jPanel26.setBorder(BorderFactory.createLoweredBevelBorder());
		labelKeywords.setEnabled(false);
		labelKeywords.setToolTipText("");
		labelKeywords.setVerifyInputWhenFocusTarget(true);
		labelKeywords.setText("Keyword: ");
		panelKeywords.setLayout(flowLayout5);
		tableKeywords.setEnabled(false);
		comboboxKeyword.setEnabled(false);
		comboboxKeyword.setEditable(true);
		buttonDelete.setEnabled(false);
		buttonDelete.setMargin(new Insets(0, 0, 0, 0));
		buttonDelete.setText("delete");
		buttonUpdate.setEnabled(false);
		buttonUpdate.setMargin(new Insets(0, 0, 0, 0));
		buttonUpdate.setText("update");
		jPanel22.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		flowLayout1.setHgap(0);
		flowLayout1.setVgap(0);
		verticalFlowLayout6.setHgap(0);
		verticalFlowLayout6.setVgap(0);
		jPanel23.setLayout(flowLayout4);
		labelDataType.setEnabled(false);
		labelDataType.setText("Data Type: ");
		labelLength.setEnabled(false);
		labelLength.setText(" Length: ");
		labelDecimalPlaces.setEnabled(false);
		labelDecimalPlaces.setText(" Decimal Places: ");
		flowLayout3.setAlignment(FlowLayout.LEFT);
		flowLayout3.setHgap(0);
		flowLayout3.setVgap(0);
		comboboxDataType.setEnabled(false);
		comboboxDataType.setEditable(true);
		spinnerLength.setEnabled(false);
		spinnerLength.setMaximumSize(new Dimension(32767, 32767));
		spinnerLength.setMinimumSize(new Dimension(57, 20));
		spinnerLength.setPreferredSize(new Dimension(57, 20));
		spinnerDecimalPlaces.setEnabled(false);
		spinnerDecimalPlaces.setMinimumSize(new Dimension(57, 20));
		spinnerDecimalPlaces.setPreferredSize(new Dimension(57, 20));
		flowLayout4.setAlignment(FlowLayout.LEFT);
		flowLayout4.setHgap(0);
		flowLayout4.setVgap(0);
		flowLayout5.setAlignment(FlowLayout.LEFT);
		flowLayout5.setHgap(0);
		flowLayout5.setVgap(0);
		gridLayout1.setColumns(3);
		gridLayout1.setRows(3);
		jLabel11.setText("Options");
		checkboxReference.setEnabled(false);
		checkboxReference.setText("Reference");
		buttonSetSourceType.setFont(new java.awt.Font("DialogInput", 1, 12));
		buttonSetSourceType.setMargin(new Insets(0, 0, 0, 0));
		buttonSetSourceType.setText("...");
		panelFlowChartTab.setLayout(borderLayout20);
		jPanel17.setBackground(Color.lightGray);
		panelLines.setLayout(overlayLayout21);
		jTextPane1.setText("jTextPane1");
		panelLines.setBackground(UIManager.getDefaults().getColor("TextField.background"));
		panelFlowChartContainer.setLayout(flowLayout6);
		flowLayout6.setAlignment(FlowLayout.LEFT);
		flowLayout6.setHgap(0);
		flowLayout6.setVgap(0);
		textfieldDestinationLibrary.setEditable(true);
		this.add(jTabbedPane1, BorderLayout.CENTER);
		jTabbedPane1.add(panelStatus, "Status");
		panelStatus.add(panelLoading, BorderLayout.CENTER);
		jTabbedPane1.add(panelSource, "Source");
		panelSource.add(scrollpaneText, BorderLayout.CENTER);
		scrollpaneText.getViewport().add(jPanel17, null);
		jPanel17.add(panelBreakPoints, BorderLayout.WEST);
		jPanel17.add(panelDate, BorderLayout.EAST);
		jPanel17.add(panelLines, BorderLayout.CENTER);
		panelLines.add(editorPaneSource, null);
		panelSource.add(jPanel11, BorderLayout.NORTH);
		jPanel11.add(labelDescription, BorderLayout.CENTER);
		jPanel11.add(labelPosition, BorderLayout.EAST);
		panelSettings.add(jScrollPane2, BorderLayout.CENTER);
		jScrollPane2.getViewport().add(jPanel1, null);
		jPanel1.add(jPanel2, null);
		jPanel2.add(jPanel7, null);
		jPanel7.add(textfieldMemberName, BorderLayout.CENTER);
		jPanel7.add(jLabel4, BorderLayout.WEST);
		jPanel2.add(jPanel6, null);
		jPanel6.add(textfieldMemberFile, BorderLayout.CENTER);
		jPanel6.add(jLabel3, BorderLayout.WEST);
		jPanel2.add(jPanel4, null);
		jPanel4.add(textfieldMemberLibrary, BorderLayout.CENTER);
		jPanel4.add(jLabel1, BorderLayout.WEST);
		jPanel2.add(jPanel10, null);
		jPanel10.add(textfieldMemberType, BorderLayout.CENTER);
		jPanel10.add(jLabel8, BorderLayout.WEST);
		jPanel10.add(buttonSetSourceType, BorderLayout.EAST);
		jPanel1.add(jPanel3, null);
		jPanel3.add(jPanel9, null);
		jPanel9.add(textfieldSystemName, BorderLayout.CENTER);
		jPanel9.add(jLabel6, BorderLayout.WEST);
		jPanel3.add(jPanel8, null);
		jPanel8.add(textfieldSystemAddress, BorderLayout.CENTER);
		jPanel8.add(jLabel5, BorderLayout.WEST);
		jPanel3.add(jPanel5, null);
		jPanel5.add(textfieldSystemUser, BorderLayout.CENTER);
		jPanel5.add(jLabel2, BorderLayout.WEST);
		jPanel1.add(jPanel13, null);
		jPanel13.add(jPanel14, BorderLayout.CENTER);
		jPanel14.add(jPanel12, BorderLayout.NORTH);
		jPanel14.add(jScrollPane3, BorderLayout.CENTER);
		jPanel14.add(buttonRemove, BorderLayout.SOUTH);
		jScrollPane3.getViewport().add(listOptions, null);
		jTabbedPane1.add(panelDesign, "Design");
		jSplitPane2.add(jPanel15, JSplitPane.RIGHT);
		jPanel15.add(jSplitPane1, BorderLayout.CENTER);
		jSplitPane1.add(jPanel16, JSplitPane.LEFT);
		jPanel16.add(jScrollPane1, BorderLayout.CENTER);
		jPanel16.add(jLabel9, BorderLayout.NORTH);
		jScrollPane1.getViewport().add(listboxFields, null);
		jSplitPane1.add(jPanel18, JSplitPane.RIGHT);
		jPanel18.add(jPanel24, BorderLayout.NORTH);
		jPanel24.add(jPanel19, null);
		jPanel19.add(textfieldName, BorderLayout.CENTER);
		jPanel19.add(comboboxType, BorderLayout.WEST);
		jPanel24.add(jPanel20, null);
		jPanel20.add(labelCol, null);
		jPanel20.add(textfieldCol, null);
		jPanel20.add(labelRow, null);
		jPanel20.add(textfieldRow, null);
		jPanel20.add(labelWidth, null);
		jPanel20.add(textfieldWidth, null);
		jPanel20.add(labelIndicators, null);
		jPanel20.add(textfieldIndicators1, null);
		jPanel20.add(textfieldIndicators2, null);
		jPanel20.add(textfieldIndicators3, null);
		jPanel24.add(jPanel23, null);
		jPanel24.add(panelKeywords, null);
		panelKeywords.add(labelKeywords, null);
		jPanel18.add(jScrollPane6, BorderLayout.CENTER);
		jScrollPane6.getViewport().add(tableKeywords, null);
		jScrollPane5.getViewport().add(panelDspf, null);
		jSplitPane2.add(jPanel21, JSplitPane.LEFT);
		panelDesign.add(jSplitPane2, BorderLayout.CENTER);
		jPanel27.add(jToolBar1, BorderLayout.NORTH);
		jPanel27.add(jScrollPane5, BorderLayout.CENTER);
		jPanel21.add(jPanel26, BorderLayout.SOUTH);
		jPanel26.add(labelMouseCoordinates, null);
		jPanel21.add(jPanel27, BorderLayout.CENTER);
		jTabbedPane1.add(panelSettings, "Settings");
		jTabbedPane1.add(panelFlowChartTab, "FlowChart");
		jTabbedPane1.add(panelDocs, "Docs");
		jPanel12.add(jLabel7, null);
		jPanel12.add(comboboxCompileType, null);
		jPanel12.add(component1, null);
		jPanel12.add(jLabel10, null);
		jPanel12.add(textfieldDestinationLibrary, null);
		jPanel12.add(component2, null);
		jPanel12.add(jLabel11, null);
		jPanel12.add(comboboxOptions, null);
		jPanel12.add(buttonAdd, null);
		panelKeywords.add(comboboxKeyword, null);
		panelKeywords.add(checkboxReference, null);
		jPanel23.add(labelDataType, null);
		jPanel23.add(comboboxDataType, null);
		jPanel23.add(labelLength, null);
		jPanel23.add(spinnerLength, null);
		jPanel23.add(labelDecimalPlaces, null);
		jPanel23.add(spinnerDecimalPlaces, null);
		jPanel24.add(jPanel22, null);
		jPanel22.add(buttonDelete, null);
		jPanel22.add(buttonUpdate, null);
		panelFlowChartTab.add(scrollpaneFlowChart, BorderLayout.CENTER);
		scrollpaneFlowChart.getViewport().add(panelFlowChartContainer, null);
		panelFlowChartContainer.add(panelFlowChart, null);
		panelBreakPoints.setSourceFont(new java.awt.Font("DialogInput", 0, 14));
		panelLines.setSourceFont(new java.awt.Font("DialogInput", 0, 14));
		panelDate.setSourceFont(new java.awt.Font("DialogInput", 0, 14));
		panelBreakPoints.setFont(new java.awt.Font("DialogInput", 0, 12));
		panelDate.setFont(new java.awt.Font("DialogInput", 0, 12));
		jSplitPane1.setDividerLocation(150);
		jTabbedPane1.setSelectedIndex(0);
		jSplitPane2.setDividerLocation(530);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() throws SQLException {
		editorPaneSource.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		// get the height of the chars in the editor.
		FontMetrics fm = getFontMetrics(new java.awt.Font("DialogInput", 0, 14));
		handlerCaret.height = fm.getHeight();

		panelBreakPoints.setMember(projectMember);
		panelDate.setRPGSourceParser(sourceParser);
		editorPaneSource.addMouseListener(handlerRightClickSource);
		new HandlerKeyPressed(editorPaneSource);

		treeModel.setProjectMember(projectMember);
		if (projectMember.member.sourceType.equals(Member.SOURCE_TYPE_RPGLE)
				|| projectMember.member.sourceType.equals(Member.SOURCE_TYPE_SQLRPGLE)) {
			// node files.
			nodeFiles = new NodeFiles(treeModel);
			nodeFiles.setSourceParser(sourceParser);
			treeModel.addParser(nodeFiles);
			// node structure.
			nodeSourceBlocks = new NodeSourceStructure(treeModel);
			nodeSourceBlocks.setSourceParser(sourceParser);
			nodeSourceBlocks.addListener(treeModel);
			treeModel.addParser(nodeSourceBlocks);
			jTabbedPane1.remove(panelDesign);
		} else if (projectMember.member.sourceType.equals("DSPF")) {
			nodeSourceBlocks = new NodeDspf(treeModel);
			nodeSourceBlocks.setSourceParser(sourceParser);
			nodeSourceBlocks.addListener(treeModel);
			treeModel.addParser(nodeSourceBlocks);
			jTabbedPane1.remove(panelFlowChartTab);
		} else if (projectMember.member.sourceType.equals("PRTF")) {
			nodeSourceBlocks = new NodeDspf(treeModel);
			nodeSourceBlocks.setSourceParser(sourceParser);
			nodeSourceBlocks.addListener(treeModel);
			treeModel.addParser(nodeSourceBlocks);
			jTabbedPane1.remove(panelDesign);
			jTabbedPane1.remove(panelFlowChartTab);
		} else {
			jTabbedPane1.remove(panelDesign);
			jTabbedPane1.remove(panelFlowChartTab);
		}

		nodeDuplicateCode = new NodeDuplicateCode(projectMember, sourceParser, treeModel);
		treeModel.addParser(nodeDuplicateCode);

		textfieldMemberName.setText(projectMember.member.member);
		textfieldMemberFile.setText(projectMember.member.file);
		textfieldMemberLibrary.setText(projectMember.member.library);
		textfieldMemberType.setText(projectMember.member.sourceType);
		textfieldSystemName.setText(projectMember.member.as400system.name);
		textfieldSystemAddress.setText(projectMember.member.as400system.address);
		textfieldSystemUser.setText(projectMember.member.as400system.user);
		comboboxCompileType.setSelectedItem(projectMember.compileType);
		for (int x = 0; x < projectMember.compileOptionCount(); x++) {
			listModelOptions.addElement(projectMember.getCompileOption(x));
		}
		panelDate.setScrollPane(scrollpaneText);
		panelBreakPoints.setScrollPane(scrollpaneText);
		textfieldDestinationLibrary
				.setModel(new DefaultComboBoxModel(projectMember.member.as400system.getLibraries().toArray()));
		textfieldDestinationLibrary.setSelectedItem(projectMember.destinationLibrary);
		sourceParser.addListenerFlowChart(this);
		synchronized (this) {
			currentlyLoading = true;
		}
		new Thread(this).start();
	}

	/**
	 * loads the source text.
	 */
	public void run() {
		try {
			// Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			projectMember.member.getSource(PanelMember.this);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			synchronized (this) {
				currentlyLoading = false;
			}
			Environment.members.close(projectMember, false);
			Project project = Environment.projects.getSelected();
			project.removeMember(projectMember);
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doneLoading();
			}
		});
	}

	protected void doneLoading() {
		editorPaneSource.setText(sourceParser.getText().toString());
		editorPaneSource.setSelectionStart(0);
		editorPaneSource.setSelectionEnd(0);
		sourceParser.watch(editorPaneSource.getDocument());
		projectMember.getMember().addListener(PanelMember.this);
		if (projectMember.member.sourceType.equals(Member.SOURCE_TYPE_RPGLE)
				|| projectMember.member.sourceType.equals(Member.SOURCE_TYPE_SQLRPGLE)
				|| projectMember.member.sourceType.equals(Member.SOURCE_TYPE_RPG)) {
			panelLines.setParser(sourceParser);
			editorPaneSource.getDocument().addDocumentListener(panelLines);
			panelLines.startParse();
		}
		// dont highlight cl programs.
		if (projectMember.member.sourceType.equals("CLP") == false
				&& projectMember.member.getSourceType().equals("CLLE") == false
				&& projectMember.member.getSourceType().equals("C") == false) {
			sourceHighlighter = new RPGSourceHighlighter();
			sourceParser.addListener(sourceHighlighter);
			sourceParser.addListener(panelDspf);
			sourceHighlighter.setTextPane(editorPaneSource);
			sourceHighlighter.addStyle(sourceParser);
		}
		sourceParser.addListenerDirty(actionMemberSave);
		editorPaneSource.addCaretListener(handlerCaret);
		editorPaneSource.addFocusListener(handlerCaret);
		editorPaneSource.getDocument().addDocumentListener(handlerCaret);
		editorPaneSource.getDocument().addDocumentListener(nodeDuplicateCode);

		nodeDuplicateCode.startScan();
		scrollpaneText.getViewport().addChangeListener(handlerCaret);
		jTabbedPane1.remove(panelStatus);
		jTabbedPane1.setEnabled(true);
		Environment.members.select(projectMember);
		Toolkit.getDefaultToolkit().beep();
		// editorPaneSource.getDocument().addUndoableEditListener(undoManager);

		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);

		// undoAction = new UndoAction();
		editorPaneSource.getInputMap().put(undoKeystroke, "undoKeystroke");
		editorPaneSource.getActionMap().put("undoKeystroke", undoAction);

		// redoAction = new RedoAction();
		editorPaneSource.getInputMap().put(redoKeystroke, "redoKeystroke");
		editorPaneSource.getActionMap().put("redoKeystroke", redoAction);
		editorPaneSource.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				if (!e.getEdit().getUndoPresentationName().equals("Undo style change")) {
					UndoableEdit edit = e.getEdit();
					undoManager.addEdit(edit);
					undoAction.update();
					redoAction.update();
				}

			}

		});

		editorPaneSource.registerKeyboardAction(undoAction, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);
		editorPaneSource.registerKeyboardAction(redoAction, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);
		synchronized (this) {
			currentlyLoading = false;
		}
	}

	@SuppressWarnings("static-access")
	public void waitForLoading() {
		try {
			while (true) {
				synchronized (this) {
					if (!currentlyLoading) {
						return;
					}
				}
				Thread.currentThread().sleep(1000);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void dispose() {
		// projectMember = null;
		sourceParser = null;
		nodeSourceBlocks = null;
		sourceHighlighter = null;
		selectedBlock = null;
		handlerMouse = null;
		handlerCaret = null;
		handlerMouseDspf = null;
	}

	public void memberChanged(Member member) {
	}

	public boolean isOkToClose(Member member) {
		if (currentlySaving) {
			return false;
		}
		if (sourceParser.isDirty()) {
			int option = JOptionPane.showConfirmDialog(null,
					projectMember.getMember().getName() + " has been changed. Would you like to save the Changes?",
					"Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}
			if (option == JOptionPane.YES_OPTION) {
				// save the member first here.
				closeAfterSave = true;
				startSave();
				return false;
			}
		}
		return true;
	}

	/**
	 * gets called when then user clicks on the x icon. so, clicking on the x
	 * doesnt remove it from the tabbedpane.
	 */
	public void closeTab() {
		Environment.members.close(projectMember, false);
	}

	public void close() {
		Environment.structure.removeStructure(treeModel);
		projectMember.getMember().removeListener(this);
		super.close();
	}

	private void addActions() {
		buttonSetSourceType.addActionListener(actionSetSourceType);
		buttonDelete.addActionListener(actionDelete);
		buttonUpdate.addActionListener(actionUpdate);
		buttonRemove.addActionListener(actionOptionRemove);
		buttonAdd.addActionListener(actionOptionAdd);
		comboboxCompileType.addActionListener(actionCompileType);
		panelBreakPoints.addMouseListener(handlerMouse);
		listboxFields.addListSelectionListener(actionItemSelected);
		panelDspf.setListener(this);
		panelDspf.addMouseMotionListener(handlerMouseDspf);
		tableKeywords.getColumnModel().getColumn(4).setCellRenderer(new ButtonCellRenderer());
		tableKeywords.getColumnModel().getColumn(4).setCellEditor(new ButtonCellEditor());
		tableKeywords.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableKeywords.getColumnModel().getColumn(4).setWidth(50);
		tableKeywords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		comboboxType.addActionListener(actionInputTypeSelected);
		textfieldDestinationLibrary.addActionListener(actionLibrarySelected);
		checkboxReference.addItemListener(actionReference);
		actionFocus = new ActionFocus(projectMember);
		super.actions = new Action[] { actionFocus, actionMemberSave, actionMemberSaveLocal, actionMemberClose, actionMemberRemove, 
				actionPrint, actionRefactorUncomment, actionRefactorComment, actionRefactorFreeForm,
				actionRefactorComparisons, actionRefactorNewSubroutine, actionRefactorCallSubroutine,
				actionMemberCompile, undoAction, redoAction, actionCutM, actionCopyM,
				actionPasteM };
		Environment.actions.addActions(actions);
		sourceParser.addListenerSelection(this);
	}

	public ProjectMember getProjectMember() {
		return projectMember;
	}

	public void setText(String text) {
		new SetText(text).start();
	}

	class SetText extends Thread {
		String text;

		public SetText(String text) {
			this.text = text;
		}

		public void run() {
			waitForLoading();
			editorPaneSource.setText(text);
		}
	}

	/**
	 * gets called when the panel is selected in the panltoolcontainer.
	 */
	public void selected() {
		Environment.structure.setStructure(treeModel, expands, this);
	}

	@SuppressWarnings("rawtypes")
	public void saveState(Enumeration expands) {
		this.expands = expands;
	}

	/*
	 * public void select(int rowStart, int colStart, int rowEnd, int colEnd) {
	 * SourceLine lineStart, lineEnd;
	 * 
	 * lineStart = sourceParser.getLineForRow(rowStart); if ( lineStart == null
	 * ) { return; } editorPaneSource.setSelectionStart(lineStart.start +
	 * colStart - 1); lineEnd = sourceParser.getLineForRow(rowEnd);
	 * editorPaneSource.setSelectionEnd(lineEnd.start + colEnd);
	 * jTabbedPane1.setSelectedIndex(0); editorPaneSource.grabFocus(); }
	 */

	public void requestingFocus(SourceLine sourceLine) {
		editorPaneSource.setSelectionStart(editorPaneSource.getText().length() - 1);
		editorPaneSource.setSelectionEnd(editorPaneSource.getText().length() - 1);
		editorPaneSource.setSelectionStart(sourceLine.start);
		editorPaneSource.setSelectionEnd(sourceLine.start);
		editorPaneSource.requestFocus();
		focus();
	}

	@SuppressWarnings("unchecked")
	public void requestingFocus(SourceBlock sourceBlock) {
		DspfLine line;

		selectedBlock = sourceBlock;
		if (sourceBlock.lineStart != null) {
			sourceBlock.lineStart.requestFocus();
		} else if (sourceBlock.lineEnd != null) {
			sourceBlock.lineEnd.requestFocus();
		}
		if (projectMember.member.sourceType.equals("DSPF") == true) {
			line = panelDspf.setScreen(selectedBlock);
			listModelFields.clear();
			while (line != null) {
				if (line.type == DspfLine.TYPE_CONSTANT || line.type == DspfLine.TYPE_EDIT
						|| line.type == DspfLine.TYPE_HIDDEN) {
					listModelFields.addElement(line);
				}
				line = line.next;
			}
		}
		actionItemSelected.valueChanged(null);
		focus();
	}

	public void requestingFlowChart(SourceBlock sourceBlock) {
		FCShape shape;

		shape = FCShape.process(editorPaneSource.getText(), sourceBlock.lineStart.getStart(),
				sourceBlock.lineEnd.getStart() + sourceBlock.lineEnd.getLength());
		panelFlowChart.setFCShape(shape);
		jTabbedPane1.setSelectedComponent(panelFlowChartTab);
	}

	/**
	 * gets called when a line is loaded from the as400. the source file is
	 * being loaded one line at a time.
	 */
	public void lineLoaded(float number, int date, String line) {
		final int count;

		this.count++;
		count = this.count;
		// source parser will be null if the user closed the panel before
		// finished loading.
		if (sourceParser == null) {
			return;
		}
		sourceParser.lineLoaded(number, date, line);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panelLoading.setText("Loading Source. " + count + " lines loaded.");
			}
		});
	}

	/**
	 * gets called when a line is saved.
	 * 
	 * @param count
	 *            int
	 */
	public void lineSaved(final int count) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panelLoading.setText("Saving Source. " + count + " lines saved.");
			}
		});
	}

	/**
	 * gets called when the save is complete.
	 * 
	 * @param count
	 *            int
	 */
	public void saveComplete(final int count, final boolean status, final String errorMessage) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Toolkit.getDefaultToolkit().beep();
				if (status) {
					panelLoading.setText("Saved Source. " + count + " lines saved.");
					// JOptionPane.showMessageDialog(null, errorMessage);
				} else {
					JOptionPane.showMessageDialog(null, "Save failed. Error: " + errorMessage);
				}
				jTabbedPane1.removeTabAt(0);
				jTabbedPane1.setEnabled(true);
				// set the enabled state of the save action depending on the
				// current state of
				// the source.
				// if the save failed, then the state is still dirty.
				actionMemberSave.setEnabled(sourceParser.isDirty());
			}
		});
	}

	public TreeModel getTreeModel() {
		return treeModel;
	}

	public void startSave() {
		// the save is a background job, dont let the user click save twice,
		// set the button to disabled.
		Thread t = new Thread() {
			public void run() {
				setPriority(Thread.MIN_PRIORITY);
				try {
					save();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					// e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		};
		t.start();
	}

	public void startSaveLocal() {
		// the save is a background job, dont let the user click save twice,
		// set the button to disabled.
		Thread t = new Thread() {
			public void run() {
				setPriority(Thread.MIN_PRIORITY);
				try {
					saveLocal();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					// e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		};
		t.start();
	}

	protected void save() throws SQLException {
		if (currentlySaving) {
			return;
		}
		currentlySaving = true;
		actionMemberSave.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panelLoading.setText("Saving Source.");
				jTabbedPane1.insertTab("Status", null, panelStatus, "Status", 0);
				jTabbedPane1.setSelectedIndex(0);
				jTabbedPane1.setEnabled(false);
			}
		});
		try {
			projectMember.member.save(sourceParser, this);
			currentlySaving = false;
			if (closeAfterSave) {
				Environment.members.close(projectMember, false);
			}
		} catch (Exception e) {
			Environment.members.close(projectMember, false);
		}
	}

	protected void saveLocal() throws SQLException {
		if (currentlySaving) {
			return;
		}
		currentlySaving = true;
		try {
			JFileChooser fc;
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(System.getProperty("user.home")));
			if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				projectMember.member.saveBackupLocal(sourceParser, fc.getSelectedFile().getAbsolutePath());
				currentlySaving = false;
				if (closeAfterSave) {
					Environment.members.close(projectMember, false);
				}
				JOptionPane.showMessageDialog(null, "File created successfully", "Save",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			Environment.members.close(projectMember, false);
		}
	}
	/*
	 * public String getSelectedText() { return
	 * editorPaneSource.getSelectedText(); }
	 */

	/**
	 * move this to projectmember class.
	 */
	/*
	 * public void compile() { projectMember.compile(editorPaneSource); }
	 */

	/**
	 * handles when someone clicks on the breakpoint panel.
	 */
	class HandlerMouse extends MouseAdapter {
		public void mousePressed(MouseEvent evt) {
			Point p;
			int position;

			p = evt.getPoint();
			position = editorPaneSource.viewToModel(p);
			position = sourceParser.getRow(position);
			projectMember.toggleBreakPoint(position);
			panelBreakPoints.repaint();
		}
	}

	class HandlerRightClickSource extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu popupMenu = new JPopupMenu();
				JMenuItem menuCut = new JMenuItem();
				JMenuItem menuCopy = new JMenuItem();
				JMenuItem menuPaste = new JMenuItem();
				JMenuItem menuDelete = new JMenuItem();
				JMenuItem menuSelectAll = new JMenuItem();
				JMenuItem menuComparisons = new JMenuItem();
				JMenuItem menuFreeForm = new JMenuItem();
				JMenuItem menuNewSubroutine = new JMenuItem();
				JMenuItem menuCallSubroutine = new JMenuItem();
				JMenuItem menuComment = new JMenuItem();
				JMenuItem menuUncomment = new JMenuItem();

				menuCut.setText("Cut");
				menuCut.setIcon(Icons.iconCut);
				menuCopy.setText("Copy");
				menuCopy.setIcon(Icons.iconCopy);
				menuPaste.setText("Paste");
				menuPaste.setIcon(Icons.iconPaste);
				menuDelete.setText("Delete");
				menuSelectAll.setText("Select All");
				menuComparisons.setText("Comparisons");
				menuFreeForm.setText("Free Form");
				menuNewSubroutine.setText("New Subroutine");
				menuCallSubroutine.setText("Call Subroutine");
				menuComment.setText("Comment");
				menuUncomment.setText("Uncomment");

				String text = editorPaneSource.getSelectedText();
				if (text == null || text.length() == 0) {
					menuCut.setEnabled(false);
					menuCopy.setEnabled(false);
					menuDelete.setEnabled(false);
					menuComparisons.setEnabled(false);
					menuFreeForm.setEnabled(false);
					menuNewSubroutine.setEnabled(false);
					menuCallSubroutine.setEnabled(false);
					menuComment.setEnabled(false);
					menuUncomment.setEnabled(false);
				}

				popupMenu.add(menuCut);
				popupMenu.add(menuCopy);
				popupMenu.add(menuPaste);
				popupMenu.add(menuDelete);
				popupMenu.add(menuSelectAll);
				popupMenu.add(menuComparisons);
				popupMenu.add(menuFreeForm);
				popupMenu.add(menuNewSubroutine);
				popupMenu.add(menuCallSubroutine);
				popupMenu.add(menuComment);
				popupMenu.add(menuUncomment);

				menuCut.addActionListener(actionCut);
				menuCopy.addActionListener(actionCopy);
				menuPaste.addActionListener(actionPaste);
				menuSelectAll.addActionListener(actionSelectAll);
				menuDelete.addActionListener(actionDelete);
				menuComparisons.addActionListener(actionRefactorComparisons);
				menuFreeForm.addActionListener(actionRefactorFreeForm);
				menuNewSubroutine.addActionListener(actionRefactorNewSubroutine);
				menuCallSubroutine.addActionListener(actionRefactorCallSubroutine);
				menuComment.addActionListener(actionRefactorComment);
				menuUncomment.addActionListener(actionRefactorUncomment);

				popupMenu.show((Component) evt.getSource(), evt.getX(), evt.getY());
			}
		}
	}

	class ActionCut implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.cut();
		}
	}

	class ActionCopy implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.copy();
		}
	}

	class ActionPaste implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.paste();
		}
	}

	/**
	 * handles when someone clicks on the dspf design view.
	 */
	class HandlerMouseDspf implements MouseMotionListener {
		public void mouseEntered(MouseEvent evt) {
			Point p;
			int row, col;

			p = evt.getPoint();
			row = (p.y / panelDspf.textHeight) + 1;
			col = (p.x / panelDspf.textWidth) + 1;
			if (row > 24 || col > 80) {
				labelMouseCoordinates.setText(" ");
				return;
			}
			labelMouseCoordinates.setText("Row: " + row + ", Col: " + col);
		}

		public void mouseExited(MouseEvent evt) {
			labelMouseCoordinates.setText(" ");
		}

		public void mouseDragged(MouseEvent e) {
			mouseEntered(e);
		}

		public void mouseMoved(MouseEvent e) {
			mouseEntered(e);
		}
	}

	/**
	 * when a user clicks a item in the list box. highlights the item in the
	 * panel.
	 */
	class ActionItemSelected implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e != null && e.getValueIsAdjusting()) {
				return;
			}
			panelDspf.setSelected((DspfLine) listboxFields.getSelectedValue());
		}
	}

	/**
	 * gets called when the caret moves.
	 */
	class HandlerCaret implements CaretListener, DocumentListener, FocusListener, ChangeListener {
		Popup popup;
		int height;
		LinePosition positionLast;
		SourceLine lineLast;
		int rowLast = -1;
		int dotLast = -1;
		int dotEndLast = -1;
		SimpleAttributeSet attributesGray = new SimpleAttributeSet();

		public HandlerCaret() {
			StyleConstants.setBackground(attributesGray, new Color(235, 235, 235));
		}

		/**
		 * only passes in the document event if a insert or remove was done.
		 * 
		 * @param evt
		 *            DocumentEvent
		 */
		public void changed(boolean modified) {
			Point point;
			int dot;
			LinePosition position;
			Caret caret;
			Rectangle rectangle;
			SourceLine line;

			// hide previous.
			caret = editorPaneSource.getCaret();
			dot = caret.getDot();
			point = sourceParser.getPoint(dot);
			labelPosition.setText("Row: " + point.y + ", Col: " + point.x + ", Position: " + dot);
			// if no source highlighter, then this type of document doesnt get
			// any other stuff.
			if (sourceHighlighter == null) {
				return;
			}
			line = sourceParser.getLine(dot);
			position = RPGLineSpec.getLinePosition(line, point.x);
			// if same row and same position, dont do anything
			// if the content was modified, then the highlighter redid the
			// highlighting,
			// so the grey must be applied again.
			if (modified == false && position != null && position.equals(positionLast) && rowLast == point.y) {
				return;
			}
			positionLast = position;
			white();
			lineLast = line;
			if (position == null) {
				labelDescription.setText("");
				if (popup != null) {
					popup.hide();
					popup = null;
				}
				return;
			}
			// figure out where to put the tool tip.
			// find the position in the source for the start of the line
			// position returned.
			// see if the column is past the first of the line position.
			dot -= (point.x - position.start);
			dotLast = dot;
			dotEndLast = (position.end - position.start) + 1;
			if (dot + dotEndLast > line.start + line.length) {
				dotEndLast = (line.start + line.length) - dot;
			}
			gray(dotLast, dotEndLast);
			if (labelDescription.getText().equals(position.description) && rowLast == point.y) {
				return;
			}
			rowLast = point.y;
			labelDescription.setText(position.description);
			try {
				rectangle = editorPaneSource.modelToView(dot);
				if (rectangle != null) {
					toolTip.setTipText(position.description);
					point = new Point(rectangle.x, rectangle.y);
					SwingUtilities.convertPointToScreen(point, editorPaneSource);
					// hide old popup.
					if (popup != null) {
						popup.hide();
						popup = null;
					}
					popup = PopupFactory.getSharedInstance().getPopup(editorPaneSource, toolTip, point.x,
							point.y - height);
					popup.show();
				}
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		private void gray(final int start, final int end) {
			((StyledDocument) editorPaneSource.getDocument()).setCharacterAttributes(start, end, attributesGray, false);
		}

		private void white() {
			if (lineLast == null) {
				return;
			}
			sourceHighlighter.addStyle(lineLast);
		}

		public void stateChanged(ChangeEvent e) {
			if (popup != null) {
				popup.hide();
				popup = null;
			}
		}

		public void focusGained(FocusEvent evt) {
		}

		public void focusLost(FocusEvent evt) {
			if (popup != null) {
				popup.hide();
				popup = null;
			}
			white();
		}

		public void caretUpdate(CaretEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					changed(false);
				}
			});
		}

		/**
		 * gets called when the style is changed in the document.
		 */
		public void changedUpdate(DocumentEvent evt) {
			// changed();
		}

		/**
		 * gets called when the document has text inserted.
		 */
		public void insertUpdate(final DocumentEvent evt) {
			// this can not be in background thread, otherwise,
			// when content is added the line.start and line.length positions
			// wont
			// be correct as soon as the content is added, which causes bugs.
			sourceParser.insertUpdate(evt);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					changed(true);
					panelDate.repaint();
				}
			});
		}

		/**
		 * gets called when the document has text removed.
		 */
		public void removeUpdate(final DocumentEvent evt) {
			sourceParser.removeUpdate(evt);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					changed(true);
					panelDate.repaint();
				}
			});
		}
	}

	/**
	 * gets called when the selection on the panel dspf changes.
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged() {
		DspfLine line;

		line = panelDspf.lineSelected;
		if (line == null) {
			textfieldName.setEnabled(false);
			textfieldName.setText("");
			labelRow.setEnabled(false);
			textfieldRow.setEnabled(false);
			textfieldRow.setValue(new Integer(0));
			labelCol.setEnabled(false);
			textfieldCol.setEnabled(false);
			textfieldCol.setValue(new Integer(0));
			labelWidth.setEnabled(false);
			textfieldWidth.setEnabled(false);
			textfieldWidth.setText("");
			listModelAttributes.clear();
			comboboxType.setSelectedIndex(-1);
			comboboxType.setEnabled(false);
			labelIndicators.setEnabled(false);
			textfieldIndicators1.setEnabled(false);
			textfieldIndicators1.setText("");
			textfieldIndicators2.setEnabled(false);
			textfieldIndicators2.setText("");
			textfieldIndicators3.setEnabled(false);
			textfieldIndicators3.setText("");
			comboboxKeyword.setSelectedItem("");
			tableKeywords.setEnabled(false);
			tableModelKeywords.enabled = false;
			buttonUpdate.setEnabled(false);
			buttonDelete.setEnabled(false);
			spinnerLength.setValue(new Integer(0));
			spinnerDecimalPlaces.setValue(new Integer(0));
			comboboxDataType.setEnabled(false);
			comboboxDataType.setSelectedItem("");
			listboxFields.setSelectedIndex(-1);
			checkboxReference.setEnabled(false);
			checkboxReference.setSelected(false);
			return;
		}
		listboxFields.setSelectedValue(line, true);
		buttonUpdate.setEnabled(true);
		buttonDelete.setEnabled(true);
		tableModelKeywords.enabled = true;
		textfieldName.setEnabled(true);
		if (line.inputType.equalsIgnoreCase("B")) {
			comboboxType.setSelectedIndex(3);
			spinnerLength.setValue(new Integer(line.length));
			spinnerDecimalPlaces.setValue(new Integer(line.precision));
			comboboxDataType.setSelectedItem(line.dataType);
		} else if (line.inputType.equalsIgnoreCase("I")) {
			comboboxType.setSelectedIndex(1);
			spinnerLength.setValue(new Integer(line.length));
			spinnerDecimalPlaces.setValue(new Integer(line.precision));
			comboboxDataType.setSelectedItem(line.dataType);
		} else if (line.inputType.equalsIgnoreCase("O")) {
			comboboxType.setSelectedIndex(2);
			spinnerLength.setValue(new Integer(line.length));
			spinnerDecimalPlaces.setValue(new Integer(line.precision));
			comboboxDataType.setSelectedItem(line.dataType);
		} else {
			comboboxType.setSelectedIndex(0);
			spinnerLength.setValue(new Integer(0));
			spinnerDecimalPlaces.setValue(new Integer(0));
			comboboxDataType.setSelectedItem("");
		}
		tableKeywords.setEnabled(true);
		labelIndicators.setEnabled(true);
		textfieldIndicators1.setEnabled(true);
		textfieldIndicators2.setEnabled(true);
		textfieldIndicators3.setEnabled(true);
		textfieldIndicators1.setText(line.n01);
		textfieldIndicators2.setText(line.n02);
		textfieldIndicators3.setText(line.n03);
		comboboxKeyword.setSelectedItem(line.keyword);
		comboboxType.setEnabled(true);
		textfieldName.setText(line.getText());
		labelRow.setEnabled(true);
		textfieldRow.setEnabled(true);
		textfieldRow.setValue(new Integer(line.row));
		labelCol.setEnabled(true);
		textfieldCol.setEnabled(true);
		textfieldCol.setValue(new Integer(line.col));
		labelWidth.setEnabled(true);
		textfieldWidth.setEnabled(true);
		textfieldWidth.setText(Integer.toString(line.width));
		checkboxReference.setEnabled(true);
		// force a state change event by setting it to the oppisite of what it
		// will be.
		checkboxReference.setSelected(!line.reference);
		checkboxReference.setSelected(line.reference);
		tableModelKeywords.listLines.clear();
		DspfLine l = line.next;
		while (l != null) {
			if (l.type == DspfLine.TYPE_ATTRIBUTE) {
				tableModelKeywords.listLines.add(l);
			} else if (l.type != DspfLine.TYPE_CONTINUATION) {
				break;
			}
			l = l.next;
		}
		tableModelKeywords.dataChanged();
	}

	public void lineDeleted(DspfLine l) {
		listModelFields.removeElement(l);
	}

	@SuppressWarnings("unchecked")
	public void lineAdded(DspfLine l) {
		if (l.type == DspfLine.TYPE_CONSTANT || l.type == DspfLine.TYPE_EDIT || l.type == DspfLine.TYPE_HIDDEN) {
			listModelFields.addElement(l);
		}
	}

	public void lineChanged(DspfLine l) {
	}

	/**
	 * gets called when a user deletes an item from the dspf.
	 */
	class ActionDelete implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			DspfLine line;

			line = panelDspf.lineSelected;
			if (line == null) {
				return;
			}
			if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete Item", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			line.delete();
		}
	}

	/**
	 */
	class ActionSelectAll implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.select(0, editorPaneSource.getText().length());
		}
	}

	class ActionUpdate implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			DspfLine line;

			line = panelDspf.lineSelected;
			if (line == null) {
				return;
			}
			if (comboboxType.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(null, "Invalid Type");
				return;
			} else if (comboboxType.getSelectedIndex() == 0) {
				line.inputType = " ";
				line.keyword = "";
			} else if (comboboxType.getSelectedIndex() == 1) {
				line.inputType = "I";
				line.keyword = comboboxKeyword.getSelectedItem().toString();
			} else if (comboboxType.getSelectedIndex() == 2) {
				line.inputType = "O";
				line.keyword = comboboxKeyword.getSelectedItem().toString();
			} else if (comboboxType.getSelectedIndex() == 3) {
				line.inputType = "B";
				line.keyword = comboboxKeyword.getSelectedItem().toString();
			} else if (comboboxType.getSelectedIndex() == 4) {
				line.inputType = "H";
				line.keyword = comboboxKeyword.getSelectedItem().toString();
			}
			line.name = textfieldName.getText();
			line.n01 = textfieldIndicators1.getText();
			line.n02 = textfieldIndicators2.getText();
			line.n03 = textfieldIndicators3.getText();
			line.dataType = comboboxDataType.getSelectedItem().toString();
			try {
				line.length = Integer.parseInt(spinnerLength.getValue().toString());
			} catch (NumberFormatException e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(null, "Invalid Length");
				return;
			}
			try {
				line.precision = Integer.parseInt(spinnerDecimalPlaces.getValue().toString());
			} catch (NumberFormatException e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(null, "Invalid Decimal Places");
				return;
			}
			try {
				line.row = Integer.parseInt(textfieldRow.getValue().toString());
				line.col = Integer.parseInt(textfieldCol.getValue().toString());
			} catch (NumberFormatException e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
				JOptionPane.showMessageDialog(null, "Invalid Row/Column");
				return;
			}
			line.update();
		}
	}

	class ActionSetSourceType implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String sourceType;

			sourceType = JOptionPane.showInputDialog(null, "New source type for member?", "Change Source Type",
					JOptionPane.QUESTION_MESSAGE);
			if (sourceType == null || sourceType.trim().length() == 0) {
				return;
			}
			try {
				projectMember.getMember().setSourceType(sourceType);
				textfieldMemberType.setText(projectMember.member.sourceType);
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * gets called when the input type gets selected. disables and enables other
	 * controls.
	 */
	class ActionInputTypeSelected implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			if (comboboxType.getSelectedIndex() > 0) {
				labelKeywords.setEnabled(true);
				comboboxKeyword.setEnabled(true);
				labelDataType.setEnabled(true);
				comboboxDataType.setEnabled(true);
				labelLength.setEnabled(true);
				spinnerLength.setEnabled(true);
				labelDecimalPlaces.setEnabled(true);
				spinnerDecimalPlaces.setEnabled(true);
				checkboxReference.setEnabled(true);
			} else {
				labelKeywords.setEnabled(false);
				comboboxKeyword.setEnabled(false);
				labelDataType.setEnabled(false);
				comboboxDataType.setEnabled(false);
				labelLength.setEnabled(false);
				spinnerLength.setEnabled(false);
				labelDecimalPlaces.setEnabled(false);
				spinnerDecimalPlaces.setEnabled(false);
				checkboxReference.setEnabled(false);
			}
		}
	}

	/**
	 * gets calles when an item in the dspf is a reference item.
	 */
	class ActionReference implements ItemListener {
		public void itemStateChanged(ItemEvent evt) {
			if (checkboxReference.isSelected()) {
				labelDataType.setEnabled(false);
				comboboxDataType.setEnabled(false);
				labelLength.setEnabled(false);
				spinnerLength.setEnabled(false);
				labelDecimalPlaces.setEnabled(false);
				spinnerDecimalPlaces.setEnabled(false);
			} else {
				labelDataType.setEnabled(true);
				comboboxDataType.setEnabled(true);
				labelLength.setEnabled(true);
				spinnerLength.setEnabled(true);
				labelDecimalPlaces.setEnabled(true);
				spinnerDecimalPlaces.setEnabled(true);
			}
		}
	}

	/**
	 * gets called when the user selects a compile type.
	 */
	class ActionCompileType implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			projectMember.compileType = (String) comboboxCompileType.getSelectedItem();
		}
	}

	/**
	 * adds a compile option to this project member.
	 */
	class ActionOptionAdd implements ActionListener {
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent evt) {
			String value;

			value = (String) comboboxOptions.getSelectedItem();
			projectMember.addCompileOption(value);
			listModelOptions.addElement(value);
		}
	}

	/**
	 * removes a compile option from this project member.
	 */
	class ActionOptionRemove implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String value;

			value = (String) listOptions.getSelectedValue();
			if (value == null) {
				return;
			}
			projectMember.removeCompileOption(value);
			listModelOptions.removeElement(value);
		}
	}

	/**
	 * gets called when the user selects the library that the member should be
	 * compiled into.
	 */
	class ActionLibrarySelected implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			projectMember.destinationLibrary = (String) textfieldDestinationLibrary.getSelectedItem();
		}
	}

	/**
	 * gets called by the user to refactor the selected code.
	 */
	class ActionRefactor extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3233693652665950746L;
		Refactor refactor;

		public ActionRefactor(String name, Refactor refactor, Integer mnemonic) {
			super(name);
			this.refactor = refactor;
			setEnabled(true);
			putValue("MENU", "Refactor");
			// F10 + CTRL
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(121,
			// KeyEvent.CTRL_MASK, false));
			if (mnemonic != null) {
				putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			}
		}

		public void actionPerformed(ActionEvent evt) {
			int start, end;

			start = editorPaneSource.getSelectionStart();
			if (start == -1) {
				start = 0;
			}
			end = editorPaneSource.getSelectionEnd();
			if (end == -1) {
				end = editorPaneSource.getText().length() - 1;
			}
			refactor.refactor(sourceParser, start, end);
		}
	}

	class ActionCutMenu extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3233693652665950746L;

		public ActionCutMenu() {
			super("Cut", Icons.iconCut);
			setEnabled(true);
			putValue("MENU", "Edit");
			putValue("SEPARATOR", "true");
			putValue(Action.ACCELERATOR_KEY,
					javax.swing.KeyStroke.getKeyStroke(88, java.awt.event.KeyEvent.CTRL_MASK, false));
		}

		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.cut();
		}
	}

	class ActionCopyMenu extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3233693652665950746L;

		public ActionCopyMenu() {
			super("Copy", Icons.iconCut);
			setEnabled(true);
			putValue("MENU", "Edit");
			putValue(Action.ACCELERATOR_KEY,
					javax.swing.KeyStroke.getKeyStroke(67, java.awt.event.KeyEvent.CTRL_MASK, false));
		}

		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.copy();
		}
	}

	class ActionPasteMenu extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3233693652665950746L;

		public ActionPasteMenu() {
			super("Paste", Icons.iconPaste);
			setEnabled(true);
			putValue("MENU", "Edit");
			putValue(Action.ACCELERATOR_KEY,
					javax.swing.KeyStroke.getKeyStroke(86, java.awt.event.KeyEvent.CTRL_MASK, false));
		}

		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.paste();
		}
	}

	class ActionSelectAllMenu extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3233693652665950746L;

		public ActionSelectAllMenu() {
			super("Select All");
			setEnabled(true);
			putValue("MENU", "Edit");
		}

		public void actionPerformed(ActionEvent evt) {
			editorPaneSource.select(0, editorPaneSource.getText().length());
		}
	}

	/**
	 * gets called when the user clicks the print button. prints the current
	 * document.
	 */
	class ActionPrint extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 529646136687853791L;

		public ActionPrint() {
			super("Print Member", Icons.iconPrint);
			setEnabled(true);
			putValue("MENU", "File");
			putValue("SEPARATOR", "true");
			// F10 + CTRL
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}

		public void actionPerformed(ActionEvent evt) {
			JPrinter.doPrintActions(panelLines);
			// editorPaneSource.doPrintActions();
		}
	}

	class ActionFocus extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2267334141189929860L;

		public ActionFocus(ProjectMember projectMember) {
			super(projectMember.getMember().getName(), projectMember.getIcon());
			setEnabled(true);
			putValue("MENU", "Window");
			// F10 + CTRL
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(121,
			// KeyEvent.CTRL_MASK, false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
		}

		public void actionPerformed(ActionEvent evt) {
			focus();
		}
	}

	/**
	 * gets called when the user wants to compile a member.
	 */
	class ActionMemberCompile extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8825618717059472888L;

		public ActionMemberCompile() {
			super("Compile Member", Icons.iconCompile);
			setEnabled(true);
			putValue("MENU", "Build");
			// F10 + CTRL
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(120, 0, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}

		public void actionPerformed(ActionEvent evt) {
			Environment.compilerResults.focus();
			try {
				save();
				projectMember.compile(editorPaneSource);
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
			Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 * gets called when the user wants to save a member.
	 */
	class ActionMemberSave extends AbstractAction implements ListenerParserDirty {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7332631874571668105L;

		public ActionMemberSave() {
			super("Save Member", Icons.iconMemberSave);
			setEnabled(false);
			putValue("MENU", "File");
			putValue("SEPARATOR", "true");
			// F10 + CTRL
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
		}

		public void actionPerformed(ActionEvent evt) {
			startSave();
		}

		public void parserDirty(SourceParser parser, boolean dirty) {
			setEnabled(dirty);
		}
	}

	class ActionMemberSaveLocal extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7332631874571668105L;

		public ActionMemberSaveLocal() {
			super("Save Member on Disk", Icons.iconMemberSaveLocal);
			putValue("MENU", "File");
			// F10 + CTRL
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}

		public void actionPerformed(ActionEvent evt) {
			startSaveLocal();
		}
	}

	/**
	 * gets called when the user wants to close a member.
	 */
	class ActionMemberClose extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7352777697662648270L;

		public ActionMemberClose() {
			super("Close Member", Icons.iconMemberClose);
			setEnabled(true);
			putValue("MENU", "File");
			putValue("Separator", "true");
			// F10 + CTRL
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(121,
			// KeyEvent.CTRL_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		}

		public void actionPerformed(ActionEvent evt) {
			// dont cache the panel since it was closed explicitly.
			Environment.members.close(projectMember, false);
		}
	}

	/**
	 * gets called when the user wants to remove a member from the project.
	 */
	class ActionMemberRemove extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5221846149933033586L;

		public ActionMemberRemove() {
			super("Remove Member", Icons.iconMemberRemove);
			setEnabled(true);
			putValue("MENU", "File");
			// F10 + CTRL
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(121,
			// KeyEvent.CTRL_MASK, false));
			// putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		}

		public void actionPerformed(ActionEvent evt) {
			// dont cache the panel since it was closed explicitly.
			if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Remove Member from Project?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			projectMember.getProject().removeMember(projectMember);
		}
	}
	/*
	 * class UndoHandler implements UndoableEditListener {
	 * 
	 * /** Messaged when the Document has created an edit, the edit is added to
	 * <code>undoManager</code>, an instance of UndoManager.
	 *
	 * public void undoableEditHappened(UndoableEditEvent e) {
	 * undoManager.addEdit(e.getEdit()); undoAction.update();
	 * redoAction.update(); } }
	 */

	class UndoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 807563726057660102L;

		public UndoAction() {
			super("Undo", Icons.iconUndo);
			setEnabled(false);
			putValue("MENU", "Edit");
			putValue("SEPARATOR", "true");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.undo();
			} catch (CannotUndoException ex) {
				// TODO deal with this
				// ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if (undoManager.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}

	}

	class RedoAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 563043380935256332L;

		public RedoAction() {
			super("Redo", Icons.iconRedo);
			setEnabled(false);
			putValue("MENU", "Edit");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo();
			} catch (CannotRedoException ex) {
				// TODO deal with this
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update() {
			if (undoManager.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}

	}

	public int hashCode() {
		return projectMember.hashCode();
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof PanelMember) {
			return ((PanelMember) object).projectMember.equals(projectMember);
		}
		return false;
	}
}

/**
 * parses the lines in a block and builds the screen.
 */
class PanelDspf extends JPanel implements ListenerParser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1071738252107348094L;
	SourceBlock selectedBlock;
	DspfLine lineFirst = null;
	DspfLine lineSelected = null;
	int textHeight, textWidth, descent;
	Font font;
	boolean changed;
	ListenerPanelDspf listener;

	public PanelDspf() {
		FontMetrics fm;

		font = new java.awt.Font("Monospaced", 0, 14);
		setFont(font);
		fm = getFontMetrics(font);
		descent = fm.getDescent();
		textHeight = fm.getMaxAscent() + descent;
		textWidth = fm.charWidth('W');
		addMouseListener(new HandlerMouse());
	}

	public void setListener(ListenerPanelDspf listener) {
		this.listener = listener;
	}

	public Dimension getPreferredSize() {
		return new Dimension((80 * textWidth) + descent, (24 * textHeight) + descent);
	}

	public Dimension getMinimumSize() {
		return new Dimension((80 * textWidth) + descent, (24 * textHeight) + descent);
	}

	public void setSelected(DspfLine line) {
		this.lineSelected = line;
		repaint();
		if (listener != null) {
			listener.selectionChanged();
		}
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(new Color(85, 85, 85));
		graphics.fillRect(0, 0, (80 * textWidth) + descent, (24 * textHeight) + descent);
		graphics.setFont(font);
		if (lineFirst != null) {
			lineFirst.paint(graphics, font, textWidth, textHeight, descent);
		}
		if (lineSelected != null) {
			graphics.setColor(Color.yellow);
			lineSelected.select(graphics, textWidth, textHeight, descent);
		}
	}

	public DspfLine setScreen(SourceBlock selectedBlock) {
		SourceLine line;
		DspfLine lineDspf, lineLast;

		this.selectedBlock = selectedBlock;
		lineFirst = null;
		lineSelected = null;
		if (selectedBlock.lineStart == null) {
			repaint();
			return null;
		}
		lineLast = null;
		line = selectedBlock.lineStart.getNext();
		while (line != null) {
			lineDspf = new DspfLine(lineLast, line);
			if (lineLast != null) {
				lineLast.next = lineDspf;
			}
			lineLast = lineDspf;
			if (lineFirst == null) {
				lineFirst = lineDspf;
			}
			if (line == selectedBlock.lineEnd) {
				break;
			}
			line = line.getNext();
		}
		repaint();
		return lineFirst;
	}

	@SuppressWarnings("rawtypes")
	public void parserEvents(ArrayList listEvents) {
		SourceParserEvent event;

		if (selectedBlock == null) {
			return;
		}
		changed = false;
		for (int x = 0; x < listEvents.size(); x++) {
			event = (SourceParserEvent) listEvents.get(x);
			if (event.type == SourceParserEvent.CHANGED) {
				changed(event.line);
			} else if (event.type == SourceParserEvent.ADDED) {
				added(event.line);
			} else if (event.type == SourceParserEvent.REMOVED) {
				removed(event.line);
			}
		}
		if (changed) {
			repaint();
		}
	}

	/**
	 * gets called when a line is added.
	 * 
	 * @param lineAdded
	 *            RPGSourceLine
	 */
	protected void added(SourceLine lineAdded) {
		DspfLine line, lineNew;
		SourceLine lineSource;

		// if this line is a member of the block then it gets inserted into
		// the lines.
		line = lineFirst;
		while (line != null) {
			lineSource = line.line;
			if (lineSource.getNext().equals(lineAdded)) {
				lineNew = new DspfLine(line, lineAdded);
				lineNew.next = line.next;
				line.next = lineNew;
				if (lineNew.next != null) {
					lineNew.next.previous = lineNew;
				}
				changed = true;
				if (listener != null) {
					listener.lineAdded(lineNew);
				}
				return;
			}
			line = line.next;
		}
		// if this section is reached, then the line added is the first or not a
		// part of this block.
		if (selectedBlock.lineStart.equals(lineAdded)) {
			lineNew = new DspfLine(line, lineAdded);
			lineNew.next = lineFirst;
			if (lineFirst != null) {
				lineFirst.previous = lineNew;
			}
			lineFirst = lineNew;
			if (listener != null) {
				listener.lineAdded(lineNew);
			}
			changed = true;
		}
	}

	/**
	 * gets called when a line is changed in the file.
	 * 
	 * @param lineChanged
	 *            RPGSourceLine
	 */
	protected void changed(SourceLine lineChanged) {
		DspfLine line;

		line = lineFirst;
		while (line != null) {
			if (line.line.equals(lineChanged)) {
				changed = true;
				line.parse();
				return;
			}
			line = line.next;
		}
	}

	/**
	 * gets called when a line is removed from the file.
	 * 
	 * @param line
	 *            RPGSourceLine
	 */
	protected void removed(SourceLine lineRemoved) {
		DspfLine line;

		line = lineFirst;
		while (line != null) {
			if (line.line.equals(lineRemoved)) {
				if (listener != null) {
					listener.lineDeleted(line);
				}
				if (line.equals(lineSelected)) {
					lineSelected = null;
					if (listener != null) {
						listener.selectionChanged();
					}
				}
				changed = true;
				if (line.previous != null) {
					line.previous.next = line.next;
				} else {
					// no previous means this is first line.
					lineFirst = line.next;
				}
				if (line.next != null) {
					line.next.previous = line.previous;
				}

				return;
			}
			line = line.next;
		}
	}

	/**
	 * handles when someone clicks on the dspf design view.
	 */
	class HandlerMouse extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			DspfLine line;

			line = lineFirst;
			while (line != null) {
				if (line.hitTest(evt.getX(), evt.getY(), textWidth, textHeight)) {
					lineSelected = line;
					if (listener != null) {
						listener.selectionChanged();
					}
					return;
				}
				line = line.next;
			}
		}
	}
}

class DspfLine {
	DspfLine previous, next;
	SourceLine line;
	String name;
	String inputType; // I/O/B
	String dataType; // A/Y/....
	String n01, n02, n03;
	String keyword;
	int row, col, width;
	int length, precision;
	boolean reference = false; // gets its properties from a referenced field.
	int type; // CONSTANT/EDIT/ATTRIBUTE/INVALID/CONTINUATION/INDICATORS
	boolean underline = false;
	Logger logger = LoggerFactory.getLogger(DspfLine.class);

	public static int TYPE_CONSTANT = 0;
	public static int TYPE_EDIT = 1;
	public static int TYPE_HIDDEN = 2;
	public static int TYPE_ATTRIBUTE = 3;
	public static int TYPE_INVALID = 4;
	public static int TYPE_CONTINUATION = 5;
	public static int TYPE_INDICATORS = 6;

	public DspfLine(DspfLine previous, SourceLine line) {
		this.previous = previous;
		this.line = line;
		parse();
	}

	public void parse() {
		StringBuffer source;

		if (line.isComment() || line.length <= 44) {
			type = TYPE_INVALID;
			return;
		}
		source = line.parser.getText();
		inputType = source.substring(line.start + 37, line.start + 38).trim();
		n01 = source.substring(line.start + 7, line.start + 10).trim();
		n02 = source.substring(line.start + 10, line.start + 13).trim();
		n03 = source.substring(line.start + 13, line.start + 16).trim();
		try {
			if (inputType.trim().length() > 0) {
				// EDIT
				// get the name of the edit.
				this.name = source.substring(line.start + 18, line.start + 28).trim();
				dataType = source.substring(line.start + 34, line.start + 35);
				if (source.substring(line.start + 28, line.start + 29).equalsIgnoreCase("R")) {
					reference = true;
					// get the length;
					length = 0;
				} else {
					// get the length;

					length = Integer.parseInt(line.get(LinePosition.A_LENGTH).trim());
				}
				width = length;
				String buffer = source.substring(line.start + 35, line.start + 37).trim();
				if (buffer.length() > 0) {
					precision = Integer.parseInt(buffer);
				} else {
					precision = 0;
				}
				if (inputType.equalsIgnoreCase("H")) {
					col = -1;
					row = -1;
					type = TYPE_HIDDEN;
				} else {
					type = TYPE_EDIT;
					row = Integer.parseInt(source.substring(line.start + 38, line.start + 41).trim());
					col = Integer.parseInt(source.substring(line.start + 41, line.start + 44).trim());
				}
				if (line.length > 44) {
					keyword = source.substring(line.start + 44, (line.start + line.length) - 1).trim();
				} else {
					keyword = "";
				}
			}
			// row == blanks
			else if (line.length > 44 && source.substring(line.start + 39, line.start + 41).trim().length() == 0) {
				String buffer = source.substring(line.start + 44, (line.start + line.length) - 1).trim();
				keyword = "";
				if (buffer.length() > 0) {
					// could be a continuation.
					// if previous line is a constant and ends with a "-"
					// character then this is a continuation.
					if (previous != null
							&& (previous.type == DspfLine.TYPE_CONSTANT || previous.type == DspfLine.TYPE_CONTINUATION)
							&& source.charAt((previous.line.start + previous.line.length) - 2) == '-'
							&& (source.charAt((line.start + line.length) - 2) == '-'
									|| source.charAt((line.start + line.length) - 2) == '\'')) {
						type = TYPE_CONTINUATION;
						name = buffer.substring(0, buffer.length() - 1);
						width = name.length();
					} else {
						// ATTRIBUTE
						type = TYPE_ATTRIBUTE;
						keyword = buffer;
						name = "";
					}
				} else {
					// just indicators.
					this.name = "";
					type = TYPE_INDICATORS;
				}
			} else {
				// CONSTANT
				keyword = "";
				type = TYPE_CONSTANT;
				source = line.parser.getText();
				row = Integer.parseInt(source.substring(line.start + 38, line.start + 41).trim());
				col = Integer.parseInt(source.substring(line.start + 41, line.start + 44).trim());
				// could be DATE or TIME constant
				if (source.charAt(line.start + 44) == '\'') {
					try {
						this.name = source.substring(line.start + 45, (line.start + line.length) - 2);
					} catch (Exception e) {
						// System.out.println(line.start + ", " + line.length +
						// ", text: " + line.getText() + ")");
						logger.info(line.start + ", " + line.length + ", text: " + line.getText() + ")");
						// e.printStackTrace();
						logger.error(e.getMessage());
					}
				} else {
					this.name = source.substring(line.start + 44, (line.start + line.length) - 1);
				}
				width = name.length();
			}
		} catch (NumberFormatException e) {
			type = TYPE_INVALID;
		}
	}

	protected void getPosition() {
		// get the column that this is drawn on.
		int diff = 0;
		DspfLine l = previous;
		while (l != null && l.type == TYPE_CONTINUATION) {
			diff += l.width;
			l = l.previous;
		}
		if (l != null) {
			col = l.col + l.width + diff;
			row = l.row;
		} else {
			col = -1;
			row = -1;
		}
	}

	public void paint(Graphics graphics, Font font, int textWidth, int textHeight, int descent) {
		// constant?
		if (type == TYPE_CONTINUATION) {
			getPosition();
			graphics.setColor(Color.green);
			graphics.setFont(font);
			doAttributes(graphics);
			if (underline) {
				// do underline.
				graphics.drawLine((col - 1) * textWidth, (row * textHeight), ((col - 1) + width) * textWidth,
						(row * textHeight));
			}
			graphics.drawString(name, (col - 1) * textWidth, (row * textHeight) - descent);
		} else if (type == TYPE_CONSTANT) {
			graphics.setColor(Color.green);
			graphics.setFont(font);
			doAttributes(graphics);
			if (underline) {
				// do underline.
				graphics.drawLine((col - 1) * textWidth, (row * textHeight), ((col - 1) + width) * textWidth,
						(row * textHeight));
			}
			graphics.drawString(name, (col - 1) * textWidth, (row * textHeight) - descent);
		} else if (type == TYPE_EDIT) {
			// some type of edit.
			graphics.setColor(Color.green);
			graphics.setFont(font);
			doAttributes(graphics);
			if (underline) {
				// do underline.
				graphics.drawLine((col - 1) * textWidth, (row * textHeight), ((col - 1) + width) * textWidth,
						(row * textHeight));
			}
			for (int x = 0; x < length; x++) {
				graphics.drawString(inputType, ((col - 1) + x) * textWidth, (row * textHeight) - descent);
			}
		}
		if (next != null) {
			next.paint(graphics, font, textWidth, textHeight, descent);
		}
	}

	/**
	 * scan for attributes that affect how this will be painted.
	 */
	private void doAttributes(Graphics g) {
		DspfLine l;

		// if this is an edit, then there is a keyword on this line too.
		if (inputType.trim().length() > 0) {
			processKeyword(keyword, g);
		}

		l = next;
		while (l != null) {
			if (l.type == TYPE_ATTRIBUTE) {
				processKeyword(l.keyword, g);
			} else if (l.type != TYPE_CONTINUATION) {
				return;
			}
			l = l.next;
		}
	}

	protected void processKeyword(String keyword, Graphics g) {
		if (keyword.equalsIgnoreCase("COLOR(BLU)")) {
			g.setColor(new Color(0, 255, 255));
		} else if (keyword.equalsIgnoreCase("COLOR(WHT)")) {
			g.setColor(Color.white);
		} else if (keyword.equalsIgnoreCase("COLOR(RED)")) {
			g.setColor(Color.red);
		} else if (keyword.equalsIgnoreCase("DSPATR(UL)")) {
			underline = true;
		} else if (keyword.equalsIgnoreCase("DSPATR(HI)")) {
			Font font = g.getFont();
			g.setFont(new Font(font.getName(), font.getStyle() + Font.BOLD, font.getSize()));
		} else if (keyword.equalsIgnoreCase("DSPATR(RI)")) {
			// reverse image?
		}
	}

	public String getText() {
		StringBuffer buffer = new StringBuffer(name);
		DspfLine l = next;

		while (l != null && l.type == TYPE_CONTINUATION) {
			buffer.append(l.name);
			l = l.next;
		}
		return buffer.toString();
	}

	public void select(Graphics graphics, int textWidth, int textHeight, int descent) {
		int w = width;
		DspfLine l = next;

		while (l != null && l.type == TYPE_CONTINUATION) {
			w += l.width;
			l = l.next;
		}
		graphics.draw3DRect((col - 1) * textWidth, (row - 1) * textHeight, w * textWidth, textHeight, true);
		graphics.draw3DRect((col - 1) * textWidth - 1, (row - 1) * textHeight - 1, w * textWidth + 2, textHeight + 2,
				true);
	}

	public boolean hitTest(int x, int y, int textWidth, int textHeight) {
		int left, top;

		if (next != null && next.type == TYPE_CONTINUATION) {
			if (next.hitTest(x, y, textWidth, textHeight)) {
				return true;
			}
		}
		left = (col - 1) * textWidth;
		if (x < left) {
			return false;
		}
		top = (row - 1) * textHeight;
		if (y < top) {
			return false;
		}
		if (x > left + (width * textWidth)) {
			return false;
		}
		if (y > top + textHeight) {
			return false;
		}
		return true;
	}

	public void delete() {
		DspfLine l;
		int total;

		total = line.length;
		l = next;
		while (l != null && (l.type == TYPE_CONTINUATION || l.type == TYPE_ATTRIBUTE)) {
			total += l.line.length;
			l = l.next;
		}
		try {
			line.parser.getDocument().remove(line.start, total);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	public void update() {
		StringBuffer buffer;
		DspfLine l;
		// String temp;
		ArrayList list;

		list = new ArrayList();
		buffer = new StringBuffer(line.parser.getText(line.start, line.start + (line.length - 1)));
		// indicators 1
		line.formatText(buffer, LinePosition.A_INDICATOR1, n01.trim());
		// indicators 2
		line.formatText(buffer, LinePosition.A_INDICATOR2, n02.trim());
		// indicators 3
		line.formatText(buffer, LinePosition.A_INDICATOR3, n03.trim());

		// input type.
		line.formatText(buffer, LinePosition.A_USE, inputType);

		// data length
		if (inputType.trim().length() == 0) {
			// constant.
			// name.
			line.formatText(buffer, LinePosition.A_NAME, "");
			// row.
			line.formatText(buffer, LinePosition.A_LINE, row);
			// column.
			line.formatText(buffer, LinePosition.A_POSITIONS, col);
			// data type.
			line.formatText(buffer, LinePosition.A_DATA_TYPE, " ");
			// reference
			line.formatText(buffer, LinePosition.A_REF, " ");
			// data length
			line.formatText(buffer, LinePosition.A_LENGTH, "");
			// decimal places.
			line.formatText(buffer, LinePosition.A_DECIMAL_POSITIONS, "  ");
			// if length is greater than one line, then make a continuations.
			if (name.length() > 34) {
				buffer.replace(44, buffer.length(), "'" + name.substring(0, 34) + "-");
				// build continuations.
				for (int x = 34; x < name.length(); x += 35) {
					if (name.length() > x + 35) {
						list.add(name.substring(x, x + 35) + "-");
					} else {
						list.add(name.substring(x, name.length()) + "'");
					}
				}
			} else {
				if (!name.equalsIgnoreCase("DATE") && !name.equalsIgnoreCase("TIME")) {
					name = "'" + name + "'";
				}
				buffer.replace(44, buffer.length(), name);
			}
		} else {
			// edit.
			if (inputType.equalsIgnoreCase("H")) {
				// row.
				line.formatText(buffer, LinePosition.A_LINE, "");
				// column.
				line.formatText(buffer, LinePosition.A_POSITIONS, "");
			} else {
				// row.
				line.formatText(buffer, LinePosition.A_LINE, row);
				// column.
				line.formatText(buffer, LinePosition.A_POSITIONS, col);
			}
			// name.
			line.formatText(buffer, LinePosition.A_NAME, name.trim());
			if (reference) {
				// data type.
				line.formatText(buffer, LinePosition.A_DATA_TYPE, "");
				// data length
				line.formatText(buffer, LinePosition.A_LENGTH, "");
				// decimal places.
				line.formatText(buffer, LinePosition.A_DECIMAL_POSITIONS, "  ");
			} else {
				// data type.
				line.formatText(buffer, LinePosition.A_DATA_TYPE, dataType);
				// data length
				line.formatText(buffer, LinePosition.A_LENGTH, length);
				// only write decimal places for certain types.
				if (dataType.equalsIgnoreCase("Y") || dataType.equalsIgnoreCase("P")
						|| dataType.equalsIgnoreCase("S")) {
					// decimal places.
					line.formatText(buffer, LinePosition.A_DECIMAL_POSITIONS, precision);
				} else {
					// decimal places.
					line.formatText(buffer, LinePosition.A_DECIMAL_POSITIONS, "  ");
				}
			}
			// keyword
			buffer.replace(44, buffer.length(), keyword);
		}

		try {
			// delete all the continuations.
			l = next;
			while (l != null && l.type == TYPE_CONTINUATION) {
				line.parser.getDocument().remove(l.line.start, l.line.length);
				l = l.next;
			}
			// dont delete the end of line, so the line wont be gone.
			line.parser.getDocument().remove(line.start, line.length - 1);
			line.parser.getDocument().insertString(line.start, buffer.toString(), null);
			// add in new continuations.
			buffer.replace(0, buffer.length(), "     A                                      \n");
			while (list.size() > 0) {
				String text = (String) list.remove(list.size() - 1);
				buffer.replace(44, buffer.length() - 1, text);
				line.parser.getDocument().insertString(line.start + line.length, buffer.toString(), null);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public String toString() {
		if (type != TYPE_ATTRIBUTE) {
			if (name.length() <= 10) {
				return name;
			}
			return name.substring(0, 10);
		}
		return name;
	}
}

class TableModelKeywords extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1068602320440994857L;
	String[] columnNames = new String[] { "N01", "N02", "N03", "KEYWORDS", " " };
	@SuppressWarnings("rawtypes")
	ArrayList listLines = new ArrayList();
	boolean enabled = false;

	public int getRowCount() {
		if (enabled == false) {
			return 0;
		}
		return listLines.size() + 1;
	}

	public void dataChanged() {
		fireTableDataChanged();
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int index) {
		return columnNames[index];
	}

	public Object getValueAt(int row, int column) {
		DspfLine line;

		if (row >= listLines.size()) {
			if (column == 4) {
				return "+";
			}
			return "";
		}
		line = (DspfLine) listLines.get(row);
		if (column == 0) {
			return line.n01;
		} else if (column == 1) {
			return line.n02;
		} else if (column == 2) {
			return line.n03;
		} else if (column == 3) {
			return line.keyword;
		}
		return "-";
	}
}

class ButtonCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7148061632155957103L;
	JButton button = new JButton("");

	public ButtonCellRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		button.setText(value.toString());
		return button;
	}
}

class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5805292825732681717L;
	JButton button = new JButton("");

	public Object getCellEditorValue() {
		return "";
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		button.setText(value.toString());
		return button;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}
}

interface ListenerPanelDspf {
	public void selectionChanged();

	public void lineDeleted(DspfLine l);

	public void lineAdded(DspfLine l);

	public void lineChanged(DspfLine l);
}
