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

/**
 * 
 * @author Derek Van Kooten.
 */
public class TableModelLayout extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7163643692271903459L;
	String schema, name;
	AS400System as400;
	String[] columnNames = new String[] { "NAME", "SIZE", "DESC", "NULL" };
	int[] pkColumns;

	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> types = new ArrayList<String>();
	ArrayList<String> sizes = new ArrayList<String>();
	ArrayList<String> descriptions = new ArrayList<String>();
	ArrayList<String> nullables = new ArrayList<String>();
	boolean alpha = false;

	public TableModelLayout() {
	}

	public TableModelLayout(String schema, String name, AS400System system) throws SQLException {
		set(schema, name, system);
	}

	public void setSchema(String schema) throws SQLException {
		this.schema = schema;
		getData();
	}

	public void set(String schema, String name, AS400System system) throws SQLException {
		this.name = name.toUpperCase();
		this.schema = schema.toUpperCase();
		this.as400 = system;
		//System.out.println("name: " + name + ", " + schema);
		getData();
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	protected void getData() throws SQLException {
		Connection cn;
		Statement stmt;
		ResultSet rs;
		Properties properties;
		String buffer;
		ArrayList list;
		int index;

		names.clear();
		types.clear();
		sizes.clear();
		descriptions.clear();
		cn = as400.getConnection();
		synchronized (cn) {
			stmt = cn.createStatement();
			if (alpha) {
				rs = stmt.executeQuery("select distinct SYSTEM_COLUMN_NAME, DATA_TYPE, NUMERIC_SCALE, "
						+ "LENGTH, COLUMN_TEXT, ordinal_position, IS_NULLABLE from syscolumns where table_name = '"
						+ this.name + "' and table_schema = '" + schema + "' order by SYSTEM_COLUMN_NAME");
			} else {
				rs = stmt.executeQuery("select distinct SYSTEM_COLUMN_NAME, DATA_TYPE, NUMERIC_SCALE, LENGTH,"
						+ " COLUMN_TEXT, ordinal_position, IS_NULLABLE from syscolumns where table_name = '" + this.name
						+ "' and table_schema = '" + schema + "' order by ordinal_position");
			}
			while (rs.next()) {
				names.add(rs.getString("SYSTEM_COLUMN_NAME"));
				types.add(rs.getString("DATA_TYPE"));
				buffer = rs.getString("NUMERIC_SCALE");
				if (buffer == null) {
					buffer = rs.getString("LENGTH");
				} else {
					if (buffer.equals("0")) {
						if (rs.getString("DATA_TYPE").startsWith("CHAR") || rs.getString("DATA_TYPE").startsWith("TIME")
								|| rs.getString("DATA_TYPE").startsWith("DATE")) {
							buffer = rs.getString("LENGTH");
						} else {
							buffer = rs.getString("LENGTH") + ",0";
						}
					} else {
						buffer = rs.getString("LENGTH") + "," + buffer;
					}
				}
				sizes.add(buffer);
				descriptions.add(rs.getString("COLUMN_TEXT"));
				nullables.add(rs.getString("IS_NULLABLE"));
			}
			rs.close();
			list = new ArrayList();
			rs = stmt.executeQuery(
					"select column_position from qsys2/syskeycst a inner join qsys2/syscst b on a.constraint_schema  = b.constraint_schema and a.constraint_name = b.constraint_name where constraint_type = 'PRIMARY KEY' and a.table_name = '"
							+ this.name + "'");
			while (rs.next()) {
				list.add(new Integer(rs.getInt(1)));
			}
			rs.close();
			stmt.close();
		}
		pkColumns = new int[list.size()];
		for (int x = 0; x < pkColumns.length; x++) {
			pkColumns[x] = ((Integer) list.get(x)).intValue();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
	}

	public void setAlpha(boolean alpha) throws SQLException {
		this.alpha = alpha;
		getData();
	}

	public String getName() {
		return name;
	}

	public String getSchema() {
		return schema;
	}

	public boolean isKey(int row) {
		for (int x = 0; x < pkColumns.length; x++) {
			if (pkColumns[x] == row) {
				return true;
			}
		}
		return false;
	}

	public int getRowCount() {
		return names.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int index) {
		return columnNames[index];
	}

	public String getType(int row) {
		return (String) types.get(row);
	}

	public String getNullable(int row) {
		return (String) nullables.get(row);
	}

	public Object getValueAt(int row, int column) {
		if (column == 0) { // name
			return names.get(row);
		}
		if (column == 1) { // sizes
			return sizes.get(row);
		}
		if (column == 2) { // descriptions
			return descriptions.get(row);
		}
		if (column == 3) { // null
			return nullables.get(row);
		}
		return "";
	}

	public String getTips(int row, int column) {
		return types.get(row)+ "("+ sizes.get(row) + ")";
	}
}
