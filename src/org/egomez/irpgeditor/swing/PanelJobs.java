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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableModel;

import org.egomez.irpgeditor.AS400System;
import org.egomez.irpgeditor.env.Environment;
import org.egomez.irpgeditor.event.ListenerAS400Systems;
import org.egomez.irpgeditor.icons.Icons;
import org.egomez.parm.ArrayNode;
import org.egomez.parm.SubsistemaAS400;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.JobList;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * @author EDGO
 * 
 */
public class PanelJobs extends PanelTool implements ListenerAS400Systems,
		Runnable {
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

	/**
	 * Create the panel.
	 */
	public PanelJobs() {
		setLayout(null);
		setName("Jobs");
		Environment.systems.addListener(this);
		defaultSytem(Environment.systems.getDefault());
		ColumnFactory factory = new ColumnFactory() {
			String[] columnNameKeys = { "JobName", "User", "Type", "%CPU",
					"Priority", "State", "Date Creation", "Number" };

			@Override
			public void configureTableColumn(TableModel model,
					TableColumnExt columnExt) {
				super.configureTableColumn(model, columnExt);
				if (columnExt.getModelIndex() < columnNameKeys.length) {
					columnExt
							.setTitle(columnNameKeys[columnExt.getModelIndex()]);
				}
			}

		};
		treeTable.setColumnFactory(factory);

		treeTable.setRootVisible(true);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(treeTable, popupMenu);

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
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		lblCargandoTrabajosActivos = new JLabel(
				"Cargando Trabajos Activos.... Espere un momento");
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
		// TODO Auto-generated method stub

	}

	@Override
	public void removedSytem(AS400System system) {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultSytem(AS400System system) {
		as400 = system;
	}

	protected void listaTrabajos(int tipo) {
		treeTable.removeAll();
		lblCargandoTrabajosActivos.setVisible(true);
		scrollPane.setVisible(false);
		root = new ArrayNode(new Object[] { as400.getName(), "", "", "", "",
				"", "", "" });
		root.setAllowsChildren(true);
		if (!flg) {
			flg = true;
			JobList jobList = new JobList(as400.getAS400());

			try {
				jobList.addJobAttributeToRetrieve(Job.SUBSYSTEM);
				jobList.addJobAttributeToRetrieve(Job.JOB_NAME);
				jobList.addJobSelectionCriteria(
						JobList.SELECTION_PRIMARY_JOB_STATUS_JOBQ, Boolean.TRUE);
				jobList.addJobSelectionCriteria(
						JobList.SELECTION_PRIMARY_JOB_STATUS_JOBQ,
						Boolean.FALSE);
				jobList.addJobSelectionCriteria(
						JobList.SELECTION_PRIMARY_JOB_STATUS_OUTQ,
						Boolean.FALSE);
				// Subsystem[] subsistemas = Subsystem
				// .listAllSubsystems(server);
				@SuppressWarnings("rawtypes")
				Enumeration list1 = jobList.getJobs();
				SubsistemaAS400 x = null;
				Job j = null;
				Hashtable<String, SubsistemaAS400> listaSubSistemas = new Hashtable<String, SubsistemaAS400>();
				while (list1.hasMoreElements()) {
					j = (Job) list1.nextElement();
					// System.out.println("name1: " + j.getName());
					// System.out.println("name1: " + j.getSubsystem());
					if (j.getType().equals(Job.JOB_TYPE_SUBSYSTEM_MONITOR)) {
						x = new SubsistemaAS400();
						x.setSubsistema(j);
						listaSubSistemas.put(j.getName(), x);
					}
				}
				/*
				 * for (int i = 0; i < subsistemas.length; i++) {
				 * //subsistemas[i].refresh(); if
				 * (subsistemas[i].getCurrentActiveJobs() != 0) { x = new
				 * SubsistemaAS400(); x.setSubsistema(subsistemas[i]);
				 * listaSubSistemas.put(subsistemas[i].getName(), x); } }
				 */
				ArrayList<Job> lista = null;
				@SuppressWarnings("rawtypes")
				Enumeration list = jobList.getJobs();
				// jobList.addJobSelectionCriteria(JobList.SELECTION_PRIMARY_JOB_STATUS_ACTIVE,Boolean.TRUE);

				String nombreSub = "";
				while (list.hasMoreElements()) {
					// System.out.println("i es " + n++);
					j = (Job) list.nextElement();
					// System.out.println("name: " + j.getName());
					// System.out.println("Subsistema: "+ j.get);
					if (!j.getType().equals("M")) {
						if (tipo == LIST_ALL) {
							if (j.getSubsystem() != null
									&& !j.getSubsystem().equals("")) {
								nombreSub = j.getSubsystem().substring(
										j.getSubsystem().lastIndexOf("/") + 1);
								nombreSub = nombreSub.substring(0,
										nombreSub.indexOf("."));
								// System.out.println("Subsistema1: " +
								// nombreSub);
								if (listaSubSistemas.get(nombreSub)
										.getListaJob() == null) {
									lista = new ArrayList<Job>();
									lista.add(j);
									listaSubSistemas.get(nombreSub)
											.setListaJob(lista);
								} else {
									listaSubSistemas.get(nombreSub)
											.getListaJob().add(j);
								}
							} else {
								x = new SubsistemaAS400();
								x.setSubsistema(j);
								listaSubSistemas.put(j.getName(), x);
							}
						} else {
							if (j.getUser().toUpperCase().trim()
									.equals(as400.getUser())) {
								nombreSub = j.getSubsystem().substring(
										j.getSubsystem().lastIndexOf("/") + 1);
								nombreSub = nombreSub.substring(0,
										nombreSub.indexOf("."));
								if (listaSubSistemas.get(nombreSub)
										.getListaJob() == null) {
									lista = new ArrayList<Job>();
									lista.add(j);
									listaSubSistemas.get(nombreSub)
											.setListaJob(lista);
								} else {
									listaSubSistemas.get(nombreSub)
											.getListaJob().add(j);
								}
							}
						}
						// System.out.println("El nombre del job es "+
						// j.getName()+" - "+ j.getStatus()+" - "+
						// j.getType() +
						// "-"+j.getSubsystem());
					}
				}
				Enumeration<SubsistemaAS400> e = listaSubSistemas.elements();
				while (e.hasMoreElements()) {
					x = e.nextElement();
					agregarSubsistema(x);
				}
				treeTable.setTreeTableModel(new DefaultTreeTableModel(root));
				treeTable.expandAll();
			} catch (AS400SecurityException e) {
				e.printStackTrace();
			} catch (ErrorCompletingRequestException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ObjectDoesNotExistException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			} finally {
				flg = false;
				lblCargandoTrabajosActivos.setVisible(false);
				scrollPane.setVisible(true);
			}
		}
	}

	private void agregarSubsistema(SubsistemaAS400 x) {
		try {

			ArrayNode node = new ArrayNode(new Object[] {
					x.getSubsistema().getName(), x.getSubsistema().getUser(),
					x.getSubsistema().getType(),
					x.getSubsistema().getCPUUsed() / 1000, "",
					x.getSubsistema().getStatus(), "","" });
			node.setAllowsChildren(true);

			Job j = null;
			if (x.getListaJob() != null) {
				Iterator<Job> listaJob = x.getListaJob().iterator();
				while (listaJob.hasNext()) {
					j = listaJob.next();
					agregarTrabajo(j, node);
				}
			}
			root.add(node);
		} catch (AS400SecurityException e) {
			// e.printStackTrace();
		} catch (ErrorCompletingRequestException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ObjectDoesNotExistException e) {
			e.printStackTrace();
		}
	}

	private void agregarTrabajo(Job j, ArrayNode parent) {
		// GridItem item2 = new GridItem(parent, SWT.NONE);
		// TreeItem item2 = new TreeItem(parent, SWT.NONE);
		try {
			parent.add(new ArrayNode(new Object[] { j.getName(), j.getUser(),
					j.getType(), j.getCPUUsed() / 1000, j.getRunPriority(),
					j.getStatus(), j.getJobEnterSystemDate().toString(),
					j.getNumber() }));
		} catch (AS400SecurityException e) {
			e.printStackTrace();
		} catch (ErrorCompletingRequestException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ObjectDoesNotExistException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		listaTrabajos(LIST_ALL);
	}
}
