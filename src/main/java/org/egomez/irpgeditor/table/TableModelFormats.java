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

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.*;

/**
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class TableModelFormats extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5747951781765161869L;
	String schema, name;
	AS400System as400;
	String[] columnNames = new String[] { "Name" };
	@SuppressWarnings("rawtypes")
	ArrayList listData = new ArrayList();
	Logger logger = LoggerFactory.getLogger(TableModelFormats.class);
	public TableModelFormats() {
	}

	public TableModelFormats(String schema, String name, AS400System system) throws SQLException {
		set(schema, name, system);
	}

	public void setSchema(String schema) throws SQLException {
		this.schema = schema.trim().toUpperCase();
		getData();
	}

	public void set(String schema, String name, AS400System system) throws SQLException {
		this.name = name.trim().toUpperCase();
		this.schema = schema.trim().toUpperCase();
		this.as400 = system;
		getData();
	}

	@SuppressWarnings("unchecked")
	protected void getData() throws SQLException {
		AS400FileRecordDescription file;
		RecordFormat[] formats;

		listData.clear();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
		try {
			file = new AS400FileRecordDescription(as400.getAS400(), "/QSYS.LIB/" + schema + ".LIB/" + name + ".FILE");
			formats = file.retrieveRecordFormat();
			for (int x = 0; x < formats.length; x++) {
				listData.add(formats[x].getName());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());			
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
	}

	private String isNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

	public String getName() {
		return name;
	}

	public String getSchema() {
		return schema;
	}

	public int getRowCount() {
		return listData.size() / getColumnCount();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int index) {
		return columnNames[index];
	}

	public Object getValueAt(int row, int column) {
		int index;

		index = (row * getColumnCount()) + column;
		if (index >= listData.size()) {
			return "";
		}
		return listData.get(index);
	}
}
