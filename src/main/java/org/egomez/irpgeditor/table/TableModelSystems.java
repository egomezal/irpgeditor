package org.egomez.irpgeditor.table;

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
import javax.swing.table.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * holds run system information.
 *
 * @author Derek Van Kooten.
 */
public class TableModelSystems extends DefaultTableModel implements ListenerAS400Systems {
	/**
	 * 
	 */
	Logger logger = LoggerFactory.getLogger(TableModelSystems.class);
	private static final long serialVersionUID = 6453373667511114433L;
	int selected = 0;
	String[] columns = new String[] { "Current", "Name", "IP Address", "User", "Password", "Connected", "Message","",
			"ID" };

	@SuppressWarnings("rawtypes")
	public TableModelSystems() {
		super();
		Environment.systems.addListener(this);
		ArrayList listSystems = Environment.systems.getSystems();
		for (int x = 0; x < listSystems.size(); x++) {
			if (listSystems.get(x) == Environment.systems.getDefault()) {
				selected = x;
				return;
			}
		}
	}

        @Override
	public void addedSytem(AS400System system) {
		fireTableDataChanged();
	}

        @Override
	public void removedSytem(AS400System system) {
		fireTableDataChanged();
	}

        @Override
	public void defaultSytem(AS400System system) {
		fireTableDataChanged();
	}

	/*
	 * public AS400System agetSelected() { ArrayList listSystems;
	 * 
	 * listSystems = Environment.systems.getSystems(); if ( listSystems.size()
	 * == 0 ) { return null; } return (AS400System)listSystems.get(selected); }
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
	public Class getColumnClass(int col) {
		if (col == 0 || col == 5) {
			return Boolean.class;
		}
		return Object.class;
	}

        @Override
	public String getColumnName(int index) {
		return columns[index];
	}

        @Override
	public int getColumnCount() {
		return 9;
	}

	@SuppressWarnings("rawtypes")
        @Override
	public int getRowCount() {
		ArrayList listSystems;

		listSystems = Environment.systems.getSystems();
		return listSystems.size() + 1;
	}

	@SuppressWarnings("rawtypes")
	public AS400System getSystem(int row) {
		ArrayList listSystems;

		listSystems = Environment.systems.getSystems();
		return (AS400System) listSystems.get(row);
	}

	@SuppressWarnings("rawtypes")
        @Override
	public void setValueAt(Object value, int row, int col) {
		AS400System system;
		boolean add;
		ArrayList listSystems;

		listSystems = Environment.systems.getSystems();
		if (row >= listSystems.size()) {
			if (col == 0) {
				return;
			}
			system = new AS400System();
			add = true;
		} else {
			system = (AS400System) listSystems.get(row);
			add = false;
		}
		if (col == 0) {
			if (((Boolean) value)) {
				if (system.isConnected() == false) {
					return;
				}
				fireTableRowsUpdated(selected, selected);
				selected = row;
				Environment.systems.setDefault(system);
			}
			return;
		}
		if (value == null || (((String) value).trim().length() == 0 && add)) {
			return;
		}
            switch (col) {
                case 1:
                    system.setName((String) value);
                    break;
                case 2:
                    system.setAddress((String) value);
                    system.setUser(null);
                    system.setPassword(null);
                    break;
                case 3:
                    system.setUser((String) value);
                    system.setPassword(null);
                    break;
                case 9:
                    if (system.getPassword() == null) {
                        system.setPassword((String) value);
                    }
                    break;
                case 4:
                    if (!value.equals("********")) {
                        system.setPassword((String) value);
                    }
                    break;
                default:
                    break;
            }

		if (add) {
			Environment.systems.addSystem(system);
		} else {
			try {
				system.connect();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@SuppressWarnings("rawtypes")
        @Override
	public Object getValueAt(int row, int col) {
		AS400System system;
		ArrayList listSystems;

		listSystems = Environment.systems.getSystems();
		if (row >= listSystems.size()) {
			if (col == 0 || col == 5) {
				return Boolean.FALSE;
			}
			return "";
		}
		system = (AS400System) listSystems.get(row);
		if (col == 0) {
			if (row == selected) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
			// return whether selected or not.
		}
		if (col == 1) {
			return system.getName();
		}
		if (col == 2) {
			return system.getAddress();
		}
		if (col == 3) {
			return system.getUser();
		}
		if (col == 4) {
			return "********";
		}
		if (col == 5) {
			if (system.isConnected()) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		if (col == 6) {
			return system.getErrorMessage();
		}
		if (col == 9) {
			return system.getPassword();
		}
		return "";
	}

        @Override
	public boolean isCellEditable(int row, int col) {
		return !(col == 5 || col == 6);
	}

}
