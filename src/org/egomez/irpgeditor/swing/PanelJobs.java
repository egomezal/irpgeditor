/**
 * 
 */
package org.egomez.irpgeditor.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
//import java.util.Iterator;
//import java.util.Iterator;

//import javax.swing.Icon;
//import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
//import javax.swing.SwingWorker;
import javax.swing.table.TableModel;

import org.egomez.irpgeditor.AS400System;
//import org.egomez.irpgeditor.TreeJob;
import org.egomez.irpgeditor.env.Environment;
import org.egomez.irpgeditor.event.ListenerAS400Systems;
import org.egomez.irpgeditor.icons.Icons;
import org.egomez.parm.ArrayNode;
import org.egomez.parm.SubsistemaAS400;
import org.jdesktop.swingx.JXTreeTable;
//import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
//import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.JobList;
import com.ibm.as400.access.JobLog;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QueuedMessage;

/**
 * @author EDGO
 * 
 */
public class PanelJobs extends PanelTool implements ListenerAS400Systems, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AS400System as400;
	JXTreeTable treeTable = new JXTreeTable();
	ArrayNode root;
	JScrollPane scrollPane = new JScrollPane(treeTable);
	public static final int LIST_USER = 1;
	public static final int LIST_ALL = 0;
	private boolean flg = false;
	Thread t1;
	JLabel lblCargandoTrabajosActivos;
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	Hashtable<String, SubsistemaAS400> listaSubSistemas = null;
	Logger logger = LoggerFactory.getLogger(PanelJobs.class);
	Hashtable<String, ArrayNode> listaNodos = null;

	/**
	 * Create the panel.
	 */
	public PanelJobs() {
		setLayout(null);
		setName("Jobs");
		Environment.systems.addListener(this);
		defaultSytem(Environment.systems.getDefault());
		ColumnFactory factory = new ColumnFactory() {
			String[] columnNameKeys = { "JobName", "User", "Type", "%CPU", "Priority", "State", "Date Creation",
					"Number" };

			@Override
			public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
				super.configureTableColumn(model, columnExt);
				if (columnExt.getModelIndex() < columnNameKeys.length) {
					columnExt.setTitle(columnNameKeys[columnExt.getModelIndex()]);
				}
			}

		};
		treeTable.setColumnFactory(factory);

		treeTable.setRootVisible(true);
		treeTable.setLeafIcon(Icons.iconJobs);
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(treeTable, popupMenu);

		JMenuItem mnuJobLog = new JMenuItem("View Job Log");
		mnuJobLog.setIcon(Icons.iconJobLog);
		mnuJobLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = treeTable.getSelectedRow();
				String name = (String) treeTable.getValueAt(row, 0);
				String user = (String) treeTable.getValueAt(row, 1);
				if (name.equalsIgnoreCase("QZDASOINIT") || name.equalsIgnoreCase("QZRCSRVS")) {
					user = "QUSER";
				}

				String number = (String) treeTable.getValueAt(row, 7);
				Job j = new Job(as400.getAS400(), name, user, number);
				new DlgJobLog("Job " + name + "/" + user + "/" + number, j.getJobLog()).setVisible(true);
			}
		});
		popupMenu.add(mnuJobLog);

		JMenuItem mnuRefresh = new JMenuItem("Refresh");
		mnuRefresh.setIcon(Icons.iconRefresh);
		mnuRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!flg) {
					t1 = new Thread(getPanel());
					t1.start();
				}
			}
		});
		popupMenu.add(mnuRefresh);

		scrollPane.setBounds(20, 15, 1286, 257);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		lblCargandoTrabajosActivos = new JLabel("Cargando Trabajos Activos.... Espere un momento");
		lblCargandoTrabajosActivos.setBounds(91, 15, 476, 41);
		lblCargandoTrabajosActivos.setVisible(false);
		add(lblCargandoTrabajosActivos);
		t1 = new Thread(getPanel());
		t1.start();

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

	protected PanelJobs getPanel() {
		return this;
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

	protected void listaTrabajos(int tipo) {
		treeTable.removeAll();
		lblCargandoTrabajosActivos.setVisible(true);
		scrollPane.setVisible(false);
		if (as400 != null) {
			if (as400.isConnected()) {
				root = new ArrayNode(new Object[] { as400.getName(), "", "", "", "", "", "", "" });
				root.setAllowsChildren(true);
				if (!flg) {
					flg = true;
					JobList jobList = new JobList(as400.getAS400());

					try {
						jobList.clearJobAttributesToRetrieve();
						jobList.addJobAttributeToRetrieve(Job.SUBSYSTEM);
						jobList.addJobAttributeToRetrieve(Job.JOB_NAME);
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_JOBQ, Boolean.FALSE);
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_OUTQ, Boolean.FALSE);
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_ACTIVE, Boolean.TRUE);

						// Subsystem[] subsistemas = Subsystem
						// .listAllSubsystems(server);

						@SuppressWarnings("rawtypes")
						Enumeration list1 = jobList.getJobs();
						// SubsistemaAS400 x = null;
						ArrayNode node = null;
						Job j = null;
						listaSubSistemas = new Hashtable<String, SubsistemaAS400>();

						listaNodos = new Hashtable<String, ArrayNode>();

						while (list1.hasMoreElements()) {
							j = (Job) list1.nextElement();
							if (j.getName().equals("QINTER") || j.getName().startsWith("QBATCH")
									|| j.getName().equals("QUSRWRK") || j.getName().equals("QMQM")) {
								try {
									node = new ArrayNode(new Object[] { j.getName(), j.getUser(), j.getType(),
											j.getCPUUsed() / 1000, "", j.getStatus(), "", "" });
									node.setAllowsChildren(true);

									listaNodos.put(j.getName(), node);
								} catch (AS400Exception e) {

								}
							}

						}

						jobList.clearJobAttributesToRetrieve();
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_JOBQ, Boolean.FALSE);
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_OUTQ, Boolean.FALSE);
						jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_ACTIVE, Boolean.TRUE);
						@SuppressWarnings("rawtypes")
						Enumeration list = jobList.getJobs();

						String nombreSub = "";
						String user = "";
						JobLog log = null;
						QueuedMessage message = null;
						@SuppressWarnings("rawtypes")
						Enumeration listMessage = null;
						while (list.hasMoreElements()) {
							j = (Job) list.nextElement();

							if (!j.getType().equals("M")) {
								if (tipo == LIST_ALL) {
									try {
										if (j.getSubsystem() != null && !j.getSubsystem().equals("")) {
											if (j.getSubsystem().equals("/QSYS.LIB/QINTER.SBSD")
													|| j.getSubsystem().startsWith("/QSYS.LIB/QBATCH")
													|| j.getSubsystem().equals("/QSYS.LIB/QUSRWRK.SBSD")
													|| j.getSubsystem().equals("/QSYS.LIB/QMQM.SBSD")) {
												nombreSub = j.getSubsystem()
														.substring(j.getSubsystem().lastIndexOf("/") + 1);
												nombreSub = nombreSub.substring(0, nombreSub.indexOf("."));

												user = j.getUser();
												try {
													if (j.getName().equalsIgnoreCase("QZDASOINIT")
															|| j.getName().equalsIgnoreCase("QZRCSRVS")) {
														log = j.getJobLog();
														listMessage = log.getMessages();
														while (listMessage.hasMoreElements()) {
															message = (QueuedMessage) listMessage.nextElement();
															if (message.getID().equals("CPIAD02")) {
																user = message.getText().substring(5, 14);
															}
														}
													}
												} catch (Exception e) {

												}
												listaNodos.get(nombreSub)
														.add(new ArrayNode(new Object[] { j.getName(), user,
																j.getType(), j.getCPUUsed() / 1000, j.getRunPriority(),
																j.getStatus(), sdf.format(j.getJobEnterSystemDate()),
																j.getNumber() }));
											}
										}
									} catch (AS400Exception e) {
										logger.error(e.getMessage());
									}

								} else {
									if (j.getUser().toUpperCase().trim().equals(as400.getUser())) {
										nombreSub = j.getSubsystem().substring(j.getSubsystem().lastIndexOf("/") + 1);
										nombreSub = nombreSub.substring(0, nombreSub.indexOf("."));
										listaNodos.get(nombreSub)
												.add(new ArrayNode(new Object[] { j.getName(), j.getUser(), j.getType(),
														j.getCPUUsed() / 1000, j.getRunPriority(), j.getStatus(),
														sdf.format(j.getJobEnterSystemDate()), j.getNumber() }));
									}
								}

							}
						}

						for (ArrayNode n : listaNodos.values()) {
							root.add(n);
						}

						treeTable.setTreeTableModel(new DefaultTreeTableModel(root));
						treeTable.expandAll();

					} catch (AS400SecurityException e) {
						logger.error(e.getMessage());
					} catch (ErrorCompletingRequestException e) {
						logger.error(e.getMessage());
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					} catch (IOException e) {
						logger.error(e.getMessage());
					} catch (ObjectDoesNotExistException e) {
						logger.error(e.getMessage());
					} catch (PropertyVetoException e1) {
						logger.error(e1.getMessage());
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					} finally {
						flg = false;
						lblCargandoTrabajosActivos.setVisible(false);
						scrollPane.setVisible(true);
					}
				}
			}
		}
	}

	@Override
	public void run() {
		listaTrabajos(LIST_ALL);
	}
}
