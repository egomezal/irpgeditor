package org.egomez.irpgeditor.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.egomez.irpgeditor.AS400System;
import org.egomez.irpgeditor.env.Environment;
import org.egomez.irpgeditor.event.ListenerAS400Systems;
import org.egomez.irpgeditor.icons.Icons;
import org.egomez.irpgeditor.table.ResultSetTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanelSQLPlus extends PanelTool implements ListenerAS400Systems,
		Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5475955503622513425L;
	AS400System as400;
	ResultSetTableModel tableModel;
	TableRowSorter<TableModel> sorter;
	int exportType = 0;
	String fileTemp;
	static final int QUERY_EXPORT_EXCEL = 1;
	static final int QUERY_EXPORT_TEXT = 2;
	final JTextArea txtSQL = new JTextArea();
	JTable table = new JTable();
	JScrollPane scrollPane = new JScrollPane(table);
	Logger logger = LoggerFactory.getLogger(PanelSQLPlus.class);
	
	/**
	 * Create the panel.
	 */
	public PanelSQLPlus() {
		setLayout(null);
		setName("SQL (Select)");
		Environment.systems.addListener(this);
		defaultSytem(Environment.systems.getDefault());
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 11, 1024, 70);
		add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("SQL:");
		lblNewLabel.setBounds(10, 11, 28, 14);
		panel.add(lblNewLabel);

		txtSQL.setLineWrap(true);
		txtSQL.setBounds(48, 6, 871, 57);
		panel.add(txtSQL);

		JButton btnSQL = new JButton("Process");
		btnSQL.setMnemonic(KeyEvent.VK_P);
		btnSQL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!txtSQL.getText().trim().equals("")) {
					try {
						if (tableModel == null) {
							if (txtSQL.getText().trim().length() > 6) {
								if (txtSQL.getText().trim().substring(0, 6)
										.toUpperCase().equals("SELECT")) {
									tableModel = new ResultSetTableModel(txtSQL
											.getText().trim(), as400);
									tableModel.setAS400System(as400);
									table.setModel(tableModel);
								} else {
									JOptionPane.showMessageDialog(null,
											"Only SELECT Query is permitted");
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Only SELECT Query is permitted");
							}
						} else {
							tableModel.setQuery(txtSQL.getText().trim());
						}
						table.setVisible(true);
						scrollPane.setVisible(true);
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),"Error", JOptionPane.WARNING_MESSAGE);
					}					
				}
			}
		});
		btnSQL.setBounds(929, 6, 85, 23);
		panel.add(btnSQL);

		JButton btnClear = new JButton("Clear");
		btnClear.setMnemonic(KeyEvent.VK_C);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtSQL.setText("");
				scrollPane.setVisible(false);
				table.setVisible(false);
			}
		});
		btnClear.setBounds(929, 40, 85, 23);
		panel.add(btnClear);
		// tableModel= new TableModelSQL();
		// table = new JTable();
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setVisible(false);
		// table.setModel(tableModel);
		scrollPane.setBounds(20, 92, 986, 157);
		scrollPane.setVisible(false);
		add(scrollPane);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(table, popupMenu);

		JMenuItem mntmExportToText = new JMenuItem("Export to Text");
		mntmExportToText.setIcon(Icons.iconSpooltoText);
		mntmExportToText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dlgArchivo = new JFileChooser();
				dlgArchivo.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				dlgArchivo.setDialogTitle("Save File");
				dlgArchivo.setDialogType(JFileChooser.SAVE_DIALOG);
				FileFilter filter1 = new ExtensionFileFilter(
						"Text Files (*.txt)", new String[] { "TXT", "txt" });
				dlgArchivo.setFileFilter(filter1);
				int retval = dlgArchivo.showDialog(getPanel(), null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					if (dlgArchivo.getSelectedFile().exists()) {
						dlgArchivo.getSelectedFile().delete();
					}
					fileTemp = dlgArchivo.getSelectedFile().getAbsolutePath();
					String temp = addFileExtIfNecessary(dlgArchivo
							.getSelectedFile().getName(), ".txt");
					if (!temp.toUpperCase().trim().equals(temp)) {
						fileTemp = dlgArchivo.getSelectedFile()
								.getAbsolutePath() + ".txt";
					}

					exportType = QUERY_EXPORT_TEXT;
					Thread t1 = new Thread(getPanel());
					t1.start();
				}
			}
		});
		popupMenu.add(mntmExportToText);

		JMenuItem mntmExportToExcel = new JMenuItem("Export to MS Excel");
		mntmExportToExcel.setIcon(Icons.iconExporttoExcel);
		mntmExportToExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dlgArchivo = new JFileChooser();
				dlgArchivo.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				dlgArchivo.setDialogTitle("Save File");
				dlgArchivo.setDialogType(JFileChooser.SAVE_DIALOG);
				FileFilter filter1 = new ExtensionFileFilter(
						"Microsoft Excel Files (*.xls)", new String[] { "XLS", "xls" });
				dlgArchivo.setFileFilter(filter1);
				int retval = dlgArchivo.showDialog(getPanel(), null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					if (dlgArchivo.getSelectedFile().exists()) {
						dlgArchivo.getSelectedFile().delete();
					}
					String temp = addFileExtIfNecessary(dlgArchivo
							.getSelectedFile().getName(), ".xls");
					fileTemp = dlgArchivo.getSelectedFile().getAbsolutePath();
					if (!temp.toUpperCase().trim().equals(temp)) {
						fileTemp = dlgArchivo.getSelectedFile()
								.getAbsolutePath() + ".xls";
					}

					exportType = QUERY_EXPORT_EXCEL;
					Thread t1 = new Thread(getPanel());
					t1.start();
				}
			}
		});
		popupMenu.add(mntmExportToExcel);

	}

	private String addFileExtIfNecessary(String file, String ext) {
		if (file.lastIndexOf('.') == -1)
			file += ext;

		return file;
	}

	protected PanelSQLPlus getPanel() {

		return this;
	}

	/*
	 * private String getTemporalFile() { Random rnd = new Random(); return
	 * System.getProperty("java.io.tmpdir") +
	 * System.getProperty("file.separator") + rnd.nextLong() + "File" +
	 * rnd.nextLong() + ".tmp"; }
	 */
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void addedSytem(AS400System system) {

	}

	@Override
	public void removedSytem(AS400System system) {

	}

	@Override
	public void defaultSytem(AS400System system) {
		as400 = system;
	}

	@Override
	public void run() {
		Statement st = null;
		ResultSet rs = null;
		ResultSetMetaData rmd = null;
		Connection cn = null;
		synchronized (this) {
			if (!txtSQL.getText().trim().equals("")) {
				if (txtSQL.getText().trim().substring(0, 6).toUpperCase()
						.equals("SELECT")) {
					try {
						cn = as400.getConnectionPool();
						st = cn.createStatement(
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						rs = st.executeQuery(txtSQL.getText().trim());
						rmd = rs.getMetaData();
						@SuppressWarnings("unused")
						boolean bFlg = false;

						int c = rmd.getColumnCount();
						int i, j;

						switch (exportType) {
						case QUERY_EXPORT_TEXT:
							BufferedWriter salida = new BufferedWriter(
									new FileWriter(fileTemp, true));
							String cadena = "";
							for (i = 1; i <= c; i++) {
								cadena = cadena + rmd.getColumnName(i)
										+ (char) 9;
							}
							salida.write(cadena);
							salida.newLine();
							i = 0;
							while (rs.next()) {
								cadena = "";
								for (j = 1; j <= c; j++) {
									// solo se pone en la tabla mientras sea
									// menor a
									// 50000
									cadena = cadena + rs.getString(j)
											+ (char) 9;
								}
								salida.write(cadena);
								salida.newLine();
								i++;
							}
							salida.close();
							break;
						case QUERY_EXPORT_EXCEL:
							WritableWorkbook workbook = Workbook
									.createWorkbook(new File(fileTemp));
							Workbook wrk1 = null;
							int nColumn = rmd.getColumnCount();
							// int nHojas = (nReg / 50000) + 1;
							WritableSheet sheet = null;
							Label label = null;
							DateFormat customDateFormat = null;
							WritableCellFormat dateFormat = null;
							DateTime dateCell = null;

							WritableCellFormat integerFormat = null;
							jxl.write.Number number2 = null;

							WritableCellFormat floatFormat = null;
							jxl.write.Number number3 = null;

							int k;
							sheet = workbook
									.createSheet("Hoja 0 de Archivo", 0);
							for (i = 1; i <= nColumn; i++) {
								label = new Label(i - 1, 0,
										rmd.getColumnName(i));
								sheet.addCell(label);
							}
							k = 0;
							i = 0;
							customDateFormat = new DateFormat("dd/MMM/yyyy");
							dateFormat = new WritableCellFormat(
									customDateFormat);
							integerFormat = new WritableCellFormat(
									NumberFormats.INTEGER);
							floatFormat = new WritableCellFormat(
									NumberFormats.FLOAT);

							sheet = workbook.getSheet(0);
							while (rs.next()) {
								for (j = 1; j <= nColumn; j++) {

									if (rmd.getColumnTypeName(j).equals("DATE")) {
										if (rs.getDate(j) != null) {
											dateCell = new DateTime(j - 1,
													i + 1, rs.getDate(j),
													dateFormat);
											sheet.addCell(dateCell);
										} else {
											label = new Label(j - 1, i + 1,
													"00/00/0000");
											sheet.addCell(label);
										}
									} else {
										if (rmd.getColumnTypeName(j).equals(
												"INTEGER")) {
											number2 = new jxl.write.Number(
													j - 1, i + 1, rs.getInt(j),
													integerFormat);
											sheet.addCell(number2);
										} else {
											if (rmd.getColumnTypeName(j)
													.equals("DECIMAL")) {
												if (rmd.getScale(j) != 0) {
													number3 = new jxl.write.Number(
															j - 1, i + 1,
															rs.getDouble(j),
															floatFormat);
												} else {
													number3 = new jxl.write.Number(
															j - 1, i + 1,
															rs.getDouble(j),
															integerFormat);
												}
												sheet.addCell(number3);

											} else {
												label = new Label(j - 1, i + 1,
														rs.getString(j));
												sheet.addCell(label);
											}
										}
									}
								}
								i++;
								if (i >= 50000) {
									k++;
									workbook.write();
									workbook.close();
									if (wrk1 != null)
										wrk1.close();

									wrk1 = Workbook.getWorkbook(new File(
											fileTemp));
									workbook = Workbook.createWorkbook(
											new File(fileTemp), wrk1);
									bFlg = true;
									// crea una nueva hoja
									sheet = workbook.createSheet("Hoja " + k
											+ " de Archivo", k);
									for (i = 1; i <= nColumn; i++) {
										label = new Label(i - 1, 0,
												rmd.getColumnName(i));
										sheet.addCell(label);
									}
									i = 0;
									// System.out.println("El valor de k es "+
									// k);
									// sheet = workbook.getSheet(k);
								}
							}
							workbook.write();
							workbook.close();
							break;
						}
						JOptionPane.showMessageDialog(null,
								"File was created succesfully", "Export",
								JOptionPane.INFORMATION_MESSAGE);
						cn.close();

					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
						logger.error(e.getMessage());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} catch (RowsExceededException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} catch (WriteException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} catch (BiffException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						if (cn != null) {
							try {
								if (!cn.isClosed()) {
									cn.close();
								}
							} catch (SQLException e) {
								//e.printStackTrace();
								logger.error(e.getMessage());
							}
						}
					}
				}
			}
		}
	}

	class ExtensionFileFilter extends FileFilter {
		String description;

		String extensions[];

		public ExtensionFileFilter(String description, String extension) {
			this(description, new String[] { extension });
		}

		public ExtensionFileFilter(String description, String extensions[]) {
			if (description == null) {
				this.description = extensions[0];
			} else {
				this.description = description;
			}
			this.extensions = (String[]) extensions.clone();
			toLower(this.extensions);
		}

		private void toLower(String array[]) {
			for (int i = 0, n = array.length; i < n; i++) {
				array[i] = array[i].toLowerCase();
			}
		}

		public String getDescription() {
			return description;
		}

		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				String path = file.getAbsolutePath().toLowerCase();
				for (int i = 0, n = extensions.length; i < n; i++) {
					String extension = extensions[i];
					if ((path.endsWith(extension) && (path.charAt(path.length()
							- extension.length() - 1)) == '.')) {
						return true;
					}
				}
			}
			return false;
		}

	}

}
