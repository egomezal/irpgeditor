package org.egomez.irpgeditor.swing;

import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.ListenerMemberCreated;
import org.egomez.irpgeditor.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Van Kooten.
 */
public class PanelLayout extends PanelTool implements Runnable, ClosableTab {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3723457222053794935L;
	LayoutRequest layoutRequest;
	AS400System as400;
	String tableSchema, tableName;
	String fileName;
	@SuppressWarnings("rawtypes")
	DefaultComboBoxModel comboboxModel;
	JTable table;
	TableModelLayout tableModel = new TableModelLayout();
	TableModelIndexes tableModelIndexes = new TableModelIndexes();
	TableModelFileDescription tableModelInfo = new TableModelFileDescription();
	TableModelFormats tableModelFormats = new TableModelFormats();

	ActionSqlGenerate actionSqlGenerate = new ActionSqlGenerate();
	ActionLibrarySelected actionLibrarySelected = new ActionLibrarySelected();
	ActionAlphaToggle actionAlphaToggle = new ActionAlphaToggle();
	ActionOpenSource actionOpenSource = new ActionOpenSource();
	ActionCreateLogical actionCreateLogical = new ActionCreateLogical();

	JPopupMenu popupTable = new JPopupMenu();
	JMenu jMenu11 = new JMenu();
	JMenu jMenu12 = new JMenu();
	JMenu jMenuItem3 = new JMenu();
	JMenu jMenuItem14 = new JMenu();
	JMenuItem menuSql1p = new JMenuItem();
	JMenuItem menuSql2p = new JMenuItem();
	JMenuItem menuSql3p = new JMenuItem();
	JMenuItem menuSql4p = new JMenuItem();
	JMenuItem menuSql5p = new JMenuItem();
	JMenuItem menuSql6p = new JMenuItem();
	JMenuItem menuSql7p = new JMenuItem();
	JMenuItem menuSql8p = new JMenuItem();
	JMenuItem menuSql9p = new JMenuItem();

	PopupListener popupListenerSql = new PopupListener(popupTable);

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel panelLibraryContainer = new JPanel();
	FlowLayout flowLayout1 = new FlowLayout();
	JPanel panelLibrary = new JPanel();
	@SuppressWarnings("rawtypes")
	JComboBox comboboxLibrary = new JComboBox();
	JLabel labelLibrary = new JLabel();
	BorderLayout borderLayout2 = new BorderLayout();
	JScrollPane scrollpane;
	JTabbedPane tabbedpaneLayout = new JTabbedPane();
	JPanel panelFieldsContainer = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	JPanel panelFields = new JPanel();
	JLabel labelLoading = new JLabel();
	JPanel panelFieldButtons = new JPanel();
	JToggleButton buttonAlpha = new JToggleButton();
	FlowLayout flowLayout2 = new FlowLayout();
	JPanel panelIndexes = new JPanel();
	BorderLayout borderLayout4 = new BorderLayout();
	JScrollPane scrollpaneIndexes = new JScrollPane();
	JTable tableIndexes = new JTable(tableModelIndexes);
	JPanel panelInfo = new JPanel();
	BorderLayout borderLayout5 = new BorderLayout();
	JScrollPane scrollpaneInfo = new JScrollPane();
	JTable tableInfo = new JTable(tableModelInfo);
	JPanel jPanel1 = new JPanel();
	JButton buttonOpenSource = new JButton();
	FlowLayout flowLayout3 = new FlowLayout();
	JPanel panelFormats = new JPanel();
	BorderLayout layoutFormats = new BorderLayout();
	JScrollPane scrollpaneFormats = new JScrollPane();
	JTable tableFormats = new JTable(tableModelFormats);
	JButton buttonLogical = new JButton();
	Logger logger = LoggerFactory.getLogger(PanelLayout.class);
	
	public PanelLayout() {
		try {
			jbInit();
			menuSql1p.addActionListener(actionSqlGenerate);
			menuSql2p.addActionListener(actionSqlGenerate);
			menuSql3p.addActionListener(actionSqlGenerate);
			menuSql4p.addActionListener(actionSqlGenerate);
			menuSql5p.addActionListener(actionSqlGenerate);
			menuSql6p.addActionListener(actionSqlGenerate);
			menuSql7p.addActionListener(actionSqlGenerate);
			menuSql8p.addActionListener(actionSqlGenerate);
			menuSql9p.addActionListener(actionSqlGenerate);
			comboboxLibrary.addActionListener(actionLibrarySelected);
			buttonAlpha.addActionListener(actionAlphaToggle);
			buttonOpenSource.addActionListener(actionOpenSource);
			buttonLogical.addActionListener(actionCreateLogical);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		panelLibraryContainer.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		flowLayout1.setHgap(0);
		flowLayout1.setVgap(0);
		labelLibrary.setText(" Library: ");
		panelLibrary.setLayout(borderLayout2);
		panelFieldsContainer.setLayout(borderLayout3);
		labelLoading.setText("Loading...");
		buttonAlpha.setMargin(new Insets(0, 0, 0, 0));
		buttonAlpha.setText("Alpha");
		panelFieldButtons.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.LEFT);
		flowLayout2.setHgap(0);
		flowLayout2.setVgap(0);
		panelIndexes.setLayout(borderLayout4);
		panelInfo.setLayout(borderLayout5);
		buttonOpenSource.setMargin(new Insets(0, 0, 0, 0));
		buttonOpenSource.setText("Open Source");
		jPanel1.setLayout(flowLayout3);
		flowLayout3.setAlignment(FlowLayout.LEFT);
		flowLayout3.setHgap(0);
		flowLayout3.setVgap(0);
		panelFormats.setLayout(layoutFormats);
		buttonLogical.setMargin(new Insets(0, 0, 0, 0));
		buttonLogical.setText("Create Logical");
		this.add(panelLibraryContainer, BorderLayout.NORTH);
		panelLibraryContainer.add(panelLibrary, null);
		panelLibrary.add(labelLibrary, BorderLayout.CENTER);
		panelLibrary.add(comboboxLibrary, BorderLayout.EAST);
		this.add(tabbedpaneLayout, BorderLayout.CENTER);
		tabbedpaneLayout.add(panelFieldsContainer, "Fields");
		panelFieldsContainer.add(panelFields, BorderLayout.CENTER);
		panelFields.add(labelLoading, null);
		panelFieldsContainer.add(panelFieldButtons, BorderLayout.NORTH);
		panelFieldButtons.add(buttonAlpha, null);
		panelFieldButtons.add(buttonLogical, null);
		tabbedpaneLayout.add(panelIndexes, "Indexes");
		panelIndexes.add(scrollpaneIndexes, BorderLayout.CENTER);
		tabbedpaneLayout.add(panelInfo, "Info");
		panelInfo.add(scrollpaneInfo, BorderLayout.CENTER);
		panelInfo.add(jPanel1, BorderLayout.NORTH);
		jPanel1.add(buttonOpenSource, null);
		tabbedpaneLayout.add(panelFormats, "Formats");
		scrollpaneInfo.getViewport().add(tableInfo, null);
		scrollpaneIndexes.getViewport().add(tableIndexes, null);

		menuSql1p.setMnemonic('1');
		menuSql1p.setText("1 SELECT * FROM #TABLE");
		menuSql2p.setMnemonic('2');
		menuSql2p.setText("2 SELECT * FROM #TABLE FETCH FIRST 5 ROWS ONLY");
		menuSql3p.setMnemonic('3');
		menuSql3p.setText("3 SELECT COUNT(*) FROM #TABLE");
		menuSql4p.setMnemonic('4');
		menuSql4p.setText("4 SELECT * FROM #TABLE WHERE ?");
		menuSql5p.setMnemonic('5');
		menuSql5p.setText("5 SELECT {#FIELD} FROM #TABLE");
		menuSql6p.setMnemonic('6');
		menuSql6p.setText("6 SELECT DISTINCT {#FIELD} FROM #TABLE");
		menuSql6p.setToolTipText("");
		menuSql7p.setMnemonic('1');
		menuSql7p.setText("1 INSERT INTO #TABLE({#FIELD}) VALUES({?})");
		menuSql8p.setMnemonic('1');
		menuSql8p.setText("1 UPDATE #TABLE SET {#FIELD = ?}");
		menuSql9p.setMnemonic('1');
		menuSql9p.setText("1 DELETE #TABLE WHERE {#FIELD = ?}");

		jMenu11.setText("SELECT");
		jMenu11.setMnemonic('S');
		jMenu12.setText("INSERT");
		jMenu12.setMnemonic('I');
		jMenuItem3.setText("UPDATE");
		jMenuItem3.setMnemonic('U');
		jMenuItem14.setText("DELETE");
		jMenuItem14.setMnemonic('D');
		jMenu11.add(menuSql1p);
		jMenu11.add(menuSql2p);
		jMenu11.add(menuSql3p);
		jMenu11.add(menuSql4p);
		jMenu11.add(menuSql5p);
		jMenu11.add(menuSql6p);
		jMenu12.add(menuSql7p);
		jMenuItem3.add(menuSql8p);
		jMenuItem14.add(menuSql9p);
		popupTable.add(jMenu11);
		popupTable.add(jMenu12);
		popupTable.add(jMenuItem3);
		popupTable.add(jMenuItem14);
		scrollpane = new JScrollPane();
		table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new TableCellRendererLayout(
				tableModel));
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(195);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFont(new java.awt.Font("DialogInput", 0, 14));
		table.addMouseListener(popupListenerSql);
		tableIndexes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableIndexes.setFont(new java.awt.Font("DialogInput", 0, 14));
		tableInfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableInfo.setFont(new java.awt.Font("DialogInput", 0, 14));
		tableInfo.getColumnModel().getColumn(0).setPreferredWidth(150);
		tableInfo.getColumnModel().getColumn(1).setPreferredWidth(250);
		scrollpane.getViewport().add(table, null);
		panelFormats.add(scrollpaneFormats, BorderLayout.CENTER);
		scrollpaneFormats.getViewport().add(tableFormats, null);
		tableFormats.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableFormats.setFont(new java.awt.Font("DialogInput", 0, 14));
	}

	public void setLayoutRequest(LayoutRequest layoutRequest) {
		this.layoutRequest = layoutRequest;
		this.fileName = layoutRequest.getName().toUpperCase();
		this.as400 = layoutRequest.getSystem();
		if (as400 == null) {
			this.as400 = Environment.systems.getDefault();
		}
		new Thread(this).start();
	}

	/**
	 * gets called when then user clicks on the x icon. so, clicking on the x
	 * doesnt remove it from the tabbedpane.
	 */
	public void closeTab() {
		Environment.layout.close(layoutRequest, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
		ArrayList list;

		try {
			tableName = layoutRequest.getParsedName();
			list = as400.getLibraries(tableName);
			if (list.size() == 0) {
				labelLoading.setText("Unable to find " + tableName);
				return;
			}
			if (layoutRequest.getSchema() == null) {
				tableSchema = (String) list.get(0);
				layoutRequest.setSchema(tableSchema);
			} else {
				tableSchema = layoutRequest.getSchema();
			}
			comboboxModel = new DefaultComboBoxModel(list.toArray());
			comboboxLibrary.setModel(comboboxModel);
			// dont want this program modification of the combobox of libraries
			// to cause a lookup of data.
			comboboxLibrary.removeActionListener(actionLibrarySelected);
			comboboxLibrary.setSelectedItem(tableSchema);
			comboboxLibrary.addActionListener(actionLibrarySelected);
			tableModelInfo.set(tableSchema, tableName, as400);
			tableModelIndexes.set(tableSchema, tableName, as400);
			tableModelFormats.set(tableSchema, tableName, as400);
			tableModel.set(tableSchema, tableName, as400);
			showResults();
		} catch (SQLException e) {
			labelLoading.setText("Unable to find " + tableName);
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
	}

	protected void showResults() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panelFields.removeAll();
				if (tableModel.getRowCount() == 0) {
					labelLoading.setText(tableName + " not found.");
					panelFields.setLayout(new FlowLayout());
					panelFields.add(labelLoading, null);
				} else {
					panelFields.setLayout(new BorderLayout());
					panelFields.add(scrollpane, BorderLayout.CENTER);
				}
				revalidate();
				repaint();
			}
		});
	}

	class ActionOpenSource implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Member member;

			member = new Member(Environment.systems.getDefault(),
					tableModelInfo.getSourceLibrary(),
					tableModelInfo.getSourceFile(),
					tableModelInfo.getSourceMember());
			Environment.members.open(member);
		}
	}

	class ActionCreateLogical implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			DialogMemberNew.showDialog(null, as400, null, null, null,
					"LF", new PanelLayout.JobLogicalCreated());
		}
	}

	/**
	 * toggles whether or not the fields are shown in alpha order.
	 */
	class ActionAlphaToggle implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				tableModel.setAlpha(buttonAlpha.isSelected());
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * gets called when a user clicks one of the generate sql menus.
	 */
	class ActionSqlGenerate implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JMenuItem menu;
			StringBuffer text;
			int index;

			menu = (JMenuItem) evt.getSource();
			if (menu == null) {
				return;
			}
			text = new StringBuffer(menu.getText());
			// take off the number in front of the sql.
			index = text.indexOf(" ");
			if (index > -1) {
				text = text.delete(0, index + 1);
			}
			index = text.indexOf("#TABLE");
			if (fileName != null && index > -1) {
				text.replace(index, index + 6,
						comboboxLibrary.getSelectedItem() + "/" + tableName);
			}
			// replace all the fields.
			while (replaceFields(text)) {
			}
			if (text.indexOf("?") == -1) {
				SQL.executeSQL(text.toString() + "\n");
			} else {
				SQL.appendSQL(text.toString() + "\n");
			}
			SQL.focus();
		}

		public boolean replaceFields(StringBuffer text) {
			String fields = null;
			String field;
			String repeat = null;
			int index, index2;

			// fields.
			index = text.indexOf("{");
			index2 = text.indexOf("}", index);
			if (index == -1 || index2 == -1) {
				return false;
			}
			repeat = text.substring(index + 1, index2);
			// get fields selected
			int[] rows = table.getSelectedRows();
			for (int x = 0; x < rows.length; x++) {
				field = ((String) tableModel.getValueAt(rows[x], 0)).trim();
				field = repeat.replaceAll("#FIELD", field);
				if (fields == null) {
					fields = field;
				} else {
					fields = fields + ", " + field;
				}
			}
			text.replace(index, index2 + 1, fields);
			return true;
		}
	}

	/**
	 * gets called when the user clicks a library in the drop down box.
	 */
	class ActionLibrarySelected implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			final Object selected;

			selected = comboboxLibrary.getSelectedItem();
			if (selected == null) {
				return;
			}
			panelFields.removeAll();
			labelLoading.setText("Loading...");
			panelFields.setLayout(new FlowLayout());
			panelFields.add(labelLoading, null);
			Thread t = new Thread() {
				public void run() {
					try {
						tableModelInfo.setSchema((String) selected);
						tableModelIndexes.setSchema((String) selected);
						tableModel.setSchema((String) selected);
						tableModelFormats.setSchema(tableSchema);
						showResults();
					} catch (Exception e) {
						//e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			};
			t.start();
		}
	}

	class JobLogicalCreated implements ListenerMemberCreated, Runnable {
		Member member;

		JobLogicalCreated() {
		}

		public void memberCreated(Member member) {
			this.member = member;
			SwingUtilities.invokeLater(this);
		}

		public void run() {
			if (this.member == null) {
				return;
			}
			PanelMember panelMember = (PanelMember) Environment.members
					.open(this.member);
			if (panelMember == null) {
				return;
			}
			StringBuffer buffer = new StringBuffer(
					"     A          R                           PFILE("
							+ PanelLayout.this.tableName + ")\n");
			SourceLine.formatText(buffer, LinePosition.A_NAME,
					(String) PanelLayout.this.tableFormats.getValueAt(0, 0));
			int[] rows = PanelLayout.this.table.getSelectedRows();
			for (int x = 0; x < rows.length; x++) {
				int row = rows[x];
				buffer.append("     A          K ");
				buffer.append(PanelLayout.this.tableModel.getValueAt(row, 0));
				buffer.append("\n");
			}
			panelMember.setText(buffer.toString());
		}
	}

}
