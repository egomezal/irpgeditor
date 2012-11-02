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
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import com.ibm.as400.access.*;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.swing.*;
import org.egomez.irpgeditor.tree.*;

/**
 * 
 * @author Derek Van Kooten.
 */
public class FrameiRPGEditor extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4983045774526568372L;
	static WindowSplash splash;
	// tree files.
	TreeCellRendererNode treeCellRendererNode = new TreeCellRendererNode();
	DefaultMutableTreeNode nodeFilesRoot = new DefaultMutableTreeNode("");
	TreeModelProject treeModelFiles = new TreeModelProject();

	// tree structure.
	TreeModelSourceStructure treeModelStructure = new TreeModelSourceStructure();

	HandlerStructure handlerStructure = new HandlerStructure();

	ActionRpgReference actionRpgReference = new ActionRpgReference();
	ActionLayout actionLayout = new ActionLayout();
	ActionExit actionExit = new ActionExit();

	JSplitPane splitpaneMain = new JSplitPane();
	JSplitPane splitpaneTop = new JSplitPane();
	JSplitPane splitpaneLeft = new JSplitPane();
	JScrollPane scrollpaneStructure = new JScrollPane();
	JTree treeStructure = new JTree(treeModelStructure);
	JMenuBar jMenuBar1 = new JMenuBar();
	JToolBar toolbar = new JToolBar();
	JPanel panelProject = new JPanel();
	BorderLayout borderLayoutProject = new BorderLayout();
	JMenu menuFile = new JMenu();
	PanelToolContainer tabbedPaneTools = new PanelToolContainer();
	PanelQcmdexec panelQcmdexec = new PanelQcmdexec();
	PanelCompilerResults panelCompileResults = new PanelCompilerResults();
	JSplitPane splitpaneRight = new JSplitPane();
	PanelToolContainer tabbedpaneFiles = new PanelToolContainer();
	PanelToolContainer tabbedpaneLayout = new PanelToolContainer();
	PanelSQL panelSql = new PanelSQL();
	PanelSQLPlus panelSqlPlus = new PanelSQLPlus();
	PanelJobs panelJobs = new PanelJobs();
	JMenuItem menuAddFileView = new JMenuItem();
	JMenu menuEdit = new JMenu();
	JMenuItem jMenuItem5 = new JMenuItem();
	JMenuItem jMenuItem6 = new JMenuItem();
	JMenuItem jMenuItem8 = new JMenuItem();
	JMenuItem jMenuItem9 = new JMenuItem();
	JMenuItem jMenuItem10 = new JMenuItem();
	JMenuItem jMenuItem11 = new JMenuItem();
	JMenuItem jMenuItem12 = new JMenuItem();
	JMenuItem jMenuItem13 = new JMenuItem();
	JMenu menuBuild = new JMenu();
	Border border1;
	TitledBorder titledBorder1;
	Border border2;
	TitledBorder titledBorder2;
	PanelSearchResults panelSearchResults = new PanelSearchResults();
	JMenu menuHelp = new JMenu();
	JMenuItem menuRpgReference = new JMenuItem();
	JMenuItem jMenuItem1 = new JMenuItem();
	JPanel panelHelp = new JPanel();
	BorderLayout borderLayout11 = new BorderLayout();
	JScrollPane jScrollPane8 = new JScrollPane();
	JEditorPane editorpaneHelp = new JEditorPane();
	PanelSpoolFiles panelSpoolFiles = new PanelSpoolFiles();
	JMenu menuDatabase = new JMenu();
	JMenu menuRefactor = new JMenu();
	PanelRunConfigurations panelRunConfigurations = new PanelRunConfigurations();
	PanelScan panelScan = new PanelScan();
	PanelSystems panelSystems = new PanelSystems();
	JTabbedPane tabbedpaneLeft = new JTabbedPane();
	JPanel panelProjectBrowserTab = new JPanel();
	JPanel panelSystemBrowserTab = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	PanelSystemBrowser panelSystemBrowser = new PanelSystemBrowser();
	PanelProjects panelProjects = new PanelProjects();
	PanelProjectMembers panelProjectMembers = new PanelProjectMembers();
	JPanel panelProjectBrowser = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel panelModules = new JPanel();
	PanelModuleBrowser scrollpaneModules = new PanelModuleBrowser();
	BorderLayout borderLayout4 = new BorderLayout();
	JMenu menuWindow = new JMenu();
	JMenu menuTools = new JMenu();
	JMenu menuSession = new JMenu();
	JMenu menuSpool = new JMenu();

	public static void main(String[] args) throws Exception {
		FrameiRPGEditor frame;

		splash = new WindowSplash(new JFrame());

		Environment.loadSettings();
		try {
			String SO = System.getProperty("os.name");
			if (SO.toUpperCase().indexOf("WINDOWS") == -1) {
				PlasticXPLookAndFeel.setPlasticTheme(new ExperienceBlue());
				UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			} else {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Center the window
		frame = new FrameiRPGEditor();
		frame.setVisible(true);
	}

	public FrameiRPGEditor() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			addActions();
			loadSettings();
			Environment.actions.addActions(new Action[] { actionExit });
		} catch (Exception e) {
			e.printStackTrace();
		}
		splash.setVisible(false);
		splash.dispose();
		splash = null;
	}

	private void jbInit() throws Exception {
		border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(
				148, 145, 140));
		titledBorder1 = new TitledBorder(border1, "Run");
		border2 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(
				148, 145, 140));
		titledBorder2 = new TitledBorder(border2, "Debug");
		splitpaneMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitpaneMain.setLastDividerLocation(600);
		splitpaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setIconImage(Icons.iconFrame.getImage());
		this.setJMenuBar(jMenuBar1);
		this.setTitle("iRPGEditor");
		setSize(700, 800);
		panelProject.setLayout(borderLayoutProject);
		menuFile.setMnemonic('F');
		menuFile.setText("File");
		treeStructure.setFont(new java.awt.Font("DialogInput", 0, 14));
		menuDatabase.setMnemonic('D');
		menuDatabase.setText("Database");
		menuAddFileView.setIcon(Icons.iconFileAdd);
		menuAddFileView.setMnemonic('A');
		menuAddFileView.setText("Add File View");
		menuAddFileView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(116,
				0, false));
		tabbedpaneLayout.setFont(new java.awt.Font("DialogInput", 0, 14));
		menuEdit.setText("Edit");
		jMenuItem5.setIcon(Icons.iconUndo);
		jMenuItem5.setText("Undo");
		jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(90,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem6.setIcon(Icons.iconRedo);
		jMenuItem6.setText("Redo");
		jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(89,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem8.setIcon(Icons.iconCut);
		jMenuItem8.setText("Cut");
		jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(88,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem9.setIcon(Icons.iconCopy);
		jMenuItem9.setText("Copy");
		jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(67,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem10.setIcon(Icons.iconPaste);
		jMenuItem10.setText("Paste");
		jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(86,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem11.setIcon(Icons.iconDelete);
		jMenuItem11.setText("Delete");
		jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(127, 0,
				false));
		jMenuItem12.setText("Select All");
		jMenuItem12.setAccelerator(javax.swing.KeyStroke.getKeyStroke(65,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		jMenuItem13.setText("Unselect");
		jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(65,
				java.awt.event.KeyEvent.CTRL_MASK
						| java.awt.event.KeyEvent.SHIFT_MASK, false));
		menuBuild.setText("Build");
		menuHelp.setMnemonic('H');
		menuHelp.setText("Help");
		menuRpgReference.setMnemonic('R');
		menuRpgReference.setText("Rpg Reference");
		menuRpgReference.setAccelerator(javax.swing.KeyStroke.getKeyStroke(112,
				0, false));
		jMenuItem1.setMnemonic('A');
		jMenuItem1.setText("About");
		jMenuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new WindowAbout().setVisible(true);
			}
		});
		panelHelp.setLayout(borderLayout11);
		menuRefactor.setText("Refactor");
		panelProjectBrowserTab.setLayout(borderLayout2);
		panelSystemBrowserTab.setLayout(borderLayout3);
		tabbedpaneLeft.setTabPlacement(JTabbedPane.BOTTOM);
		panelProjectBrowser.setLayout(borderLayout1);
		panelModules.setLayout(borderLayout4);
		menuWindow.setMnemonic('W');
		menuWindow.setText("Window");
		menuTools.setMnemonic('T');
		menuTools.setText("Tools");
		menuSession.setMnemonic('S');
		menuSession.setText("Session");
		menuSpool.setMnemonic('O');
		menuSpool.setText("Spool");
		this.getContentPane().add(splitpaneMain, BorderLayout.CENTER);
		splitpaneMain.add(splitpaneTop, JSplitPane.TOP);
		splitpaneTop.add(tabbedpaneLeft, JSplitPane.LEFT);
		panelProjectBrowserTab.add(splitpaneLeft, BorderLayout.CENTER);

		splitpaneLeft.add(scrollpaneStructure, JSplitPane.BOTTOM);
		splitpaneLeft.add(panelProjectBrowser, JSplitPane.TOP);
		panelProjectBrowser.add(panelProjectMembers, BorderLayout.CENTER);
		panelProjectBrowser.add(panelProjects, BorderLayout.NORTH);
		splitpaneTop.add(splitpaneRight, JSplitPane.RIGHT);
		splitpaneRight.add(tabbedpaneFiles, JSplitPane.LEFT);
		splitpaneRight.add(tabbedpaneLayout, JSplitPane.RIGHT);
		panelHelp.add(jScrollPane8, BorderLayout.CENTER);
		jScrollPane8.getViewport().add(editorpaneHelp, null);
		scrollpaneStructure.getViewport().add(treeStructure, null);
		splitpaneMain.add(tabbedPaneTools, JSplitPane.BOTTOM);
		tabbedPaneTools.setTabPlacement(JTabbedPane.LEFT);
		tabbedPaneTools.add(panelSystems);
		tabbedPaneTools.add(panelSpoolFiles);
		tabbedPaneTools.add(panelQcmdexec);
		tabbedPaneTools.add(panelScan);
		tabbedPaneTools.add(panelRunConfigurations);
		tabbedPaneTools.add(panelCompileResults);
		tabbedPaneTools.add(panelSearchResults);
		tabbedPaneTools.add(panelSql);
		tabbedPaneTools.add(panelSqlPlus);
		tabbedPaneTools.add(panelJobs);
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		jMenuBar1.add(menuFile);
		jMenuBar1.add(menuEdit);
		jMenuBar1.add(menuTools);
		jMenuBar1.add(menuSession);
		jMenuBar1.add(menuRefactor);
		jMenuBar1.add(menuBuild);
		jMenuBar1.add(menuSpool);
		jMenuBar1.add(menuDatabase);
		jMenuBar1.add(menuWindow);
		jMenuBar1.add(menuHelp);
		jMenuBar1.add(menuHelp);
		menuDatabase.add(menuAddFileView);
		menuEdit.add(jMenuItem5);
		menuEdit.add(jMenuItem6);
		menuEdit.addSeparator();
		menuEdit.add(jMenuItem8);
		menuEdit.add(jMenuItem9);
		menuEdit.add(jMenuItem10);
		menuEdit.add(jMenuItem11);
		menuEdit.addSeparator();
		menuEdit.add(jMenuItem12);
		menuEdit.add(jMenuItem13);
		menuHelp.add(menuRpgReference);
		menuHelp.addSeparator();
		menuHelp.add(jMenuItem1);

		tabbedpaneLeft.add(panelProjectBrowserTab, "Projects");
		tabbedpaneLeft.add(panelSystemBrowserTab, "Files");
		panelSystemBrowserTab.add(panelSystemBrowser, BorderLayout.CENTER);
		tabbedpaneLeft.add(panelModules, "Modules");
		panelModules.add(scrollpaneModules, BorderLayout.CENTER);
		splitpaneMain.setDividerLocation(550);
		splitpaneLeft.setDividerLocation(350);
		splitpaneTop.setDividerLocation(200);
		splitpaneRight.setDividerLocation(350);
	}

	/**
	 * Overridden so we can exit when window is closed
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			actionExit.actionPerformed(null);
		}
	}

	/**
	 * associates actions with gui controls.
	 */
	@SuppressWarnings("static-access")
	protected void addActions() {
		menuAddFileView.addActionListener(actionLayout);
		menuRpgReference.addActionListener(actionRpgReference);
		new MouseAdapterTreeStructure(treeStructure);

		panelQcmdexec.setJFrame(this);

		Environment.qcmdexec.setOutput(panelQcmdexec);
		Environment.searchResults.setOutput(panelSearchResults);
		Environment.compilerResults.setOutput(panelCompileResults);
		Environment.sql.setOutput(panelSql);
		Environment.toolManager.setFactory(ProjectMember.class,
				new FactoryMembers());
		Environment.toolManager.setPanelContainer(ProjectMember.class,
				tabbedpaneFiles);
		Environment.toolManager.setFactory(SpooledFile.class,
				new FactorySpoolFiles());
		Environment.toolManager.setPanelContainer(SpooledFile.class,
				tabbedpaneLayout);
		Environment.toolManager.setFactory(LayoutRequest.class,
				new FactoryLayout());
		Environment.toolManager.setPanelContainer(LayoutRequest.class,
				tabbedpaneLayout);
		Environment.toolManager
				.setFactory(HelpRequest.class, new FactoryHelp());
		Environment.toolManager.setPanelContainer(HelpRequest.class,
				tabbedpaneLayout);
		Environment.structure.setOutput(handlerStructure);

		new HandlerActions(toolbar, getJMenuBar());

		treeStructure.setCellRenderer(treeCellRendererNode);
		ToolTipManager.sharedInstance().registerComponent(treeStructure);
	}

	/**
	 * centers the frame.
	 */
	public void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	/**
	 * loads the settings.
	 */
	public void loadSettings() {
		Properties settings;

		center();
		settings = Environment.settings;
		setBounds(Integer.parseInt(settings.getProperty("x")),
				Integer.parseInt(settings.getProperty("y")),
				Integer.parseInt(settings.getProperty("width")),
				Integer.parseInt(settings.getProperty("height")));
		splitpaneMain.setDividerLocation(Integer.parseInt(settings
				.getProperty("jSplitPane1.dividerLocation")));
		splitpaneTop.setDividerLocation(Integer.parseInt(settings
				.getProperty("jSplitPane2.dividerLocation")));
		splitpaneLeft.setDividerLocation(Integer.parseInt(settings
				.getProperty("jSplitPane3.dividerLocation")));
		splitpaneRight.setDividerLocation(Integer.parseInt(settings
				.getProperty("jSplitPane4.dividerLocation")));
	}

	/**
	 * saves the settings.
	 */
	public void saveSettings() {
		Properties settings;

		settings = Environment.settings;
		settings.setProperty("x", Integer.toString(getX()));
		settings.setProperty("y", Integer.toString(getY()));
		settings.setProperty("width", Integer.toString(getWidth()));
		settings.setProperty("height", Integer.toString(getHeight()));
		settings.setProperty("jSplitPane1.dividerLocation",
				Integer.toString(splitpaneMain.getDividerLocation()));
		settings.setProperty("jSplitPane2.dividerLocation",
				Integer.toString(splitpaneTop.getDividerLocation()));
		settings.setProperty("jSplitPane3.dividerLocation",
				Integer.toString(splitpaneLeft.getDividerLocation()));
		settings.setProperty("jSplitPane4.dividerLocation",
				Integer.toString(splitpaneRight.getDividerLocation()));
	}

	/**
	 * gets called when the application exits.
	 */
	class ActionExit extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9220129392250898861L;

		public ActionExit() {
			super("Exit");
			setEnabled(true);
			putValue("MENU", "File");
			// F9 + SHIFT
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(120,
			// KeyEvent.SHIFT_MASK, false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
		}

		public void actionPerformed(ActionEvent evt) {
			saveSettings();
			Environment.saveSettings();
			System.exit(0);
		}
	}

	/**
	 * displays a layout for a given file on a given as400.
	 */
	class ActionLayout implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Component component;
			String buffer;

			// is a name highlighted?
			component = KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.getFocusOwner();
			if (component == null
					|| component instanceof JTextComponent == false) {
				buffer = JOptionPane.showInputDialog(null, "File Name",
						"Layout", JOptionPane.QUESTION_MESSAGE);
			} else {
				buffer = ((JTextComponent) component).getSelectedText();
				if (buffer == null) {
					buffer = ((JTextComponent) component).getText();
				}
			}
			if (buffer == null) {
				return;
			}
			buffer = buffer.trim();
			if (buffer.length() == 0) {
				return;
			}
			Environment.layout.open(new LayoutRequest(buffer));
		}
	}

	/**
	 * gets called to display the rpg reference information for the command
	 * selected.
	 */
	class ActionRpgReference implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Component component;
			String buffer;
			int index, start, end;

			component = KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.getFocusOwner();
			if (component == null) {
				return;
			}
			if (component instanceof JTextComponent == false) {
				return;
			}
			buffer = ((JTextComponent) component).getSelectedText();
			if (buffer == null) {
				buffer = ((JTextComponent) component).getText();
				if (buffer == null) {
					return;
				}
				index = ((JTextComponent) component).getSelectionStart();
				if (index == -1) {
					return;
				}
				end = buffer.indexOf(" ", index);
				if (end == -1) {
					return;
				}
				start = buffer.lastIndexOf(" ", index);
				if (start == -1) {
					start = 0;
				}
				buffer = buffer.substring(start + 1, end);
			}
			Environment.toolManager.open(new HelpRequest(buffer));
		}
	}

	class HandlerStructure implements OutputStructure {
		ListenerStructure listener;

		@SuppressWarnings("rawtypes")
		public void setStructure(TreeModel treeModel, Enumeration expands,
				ListenerStructure listener) {
			TreePath path;

			if (this.listener != null) {
				this.listener.saveState(treeStructure
						.getExpandedDescendants(new TreePath(treeStructure
								.getModel().getRoot())));
			}
			treeStructure.setModel(treeModel);
			this.listener = listener;
			if (expands == null) {
				return;
			}
			while (expands.hasMoreElements()) {
				path = (TreePath) expands.nextElement();
				treeStructure.expandPath(path);
			}
		}

		public void removeStructure(TreeModel treeModel) {
			if (treeStructure.getModel() == treeModel) {
				if (listener != null) {
					listener.saveState(treeStructure
							.getExpandedDescendants(new TreePath(treeStructure
									.getModel().getRoot())));
				}
				treeStructure.setModel(treeModelStructure);
				listener = null;
			}
		}
	}
}
