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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Derek Van Kooten.
 */
public final class TableModelIndexes extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = -5094704929427566536L;
    Logger logger = LoggerFactory.getLogger(TableModelIndexes.class);
    String schema, name;
    AS400System as400;
    String[] columnNames = new String[]{"Library", "Index", "Column", "Sort Order", "Filter"};
    @SuppressWarnings("rawtypes")
    ArrayList listData = new ArrayList();

    public TableModelIndexes() {
    }

    public TableModelIndexes(String schema, String name, AS400System system) throws SQLException {
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

    @SuppressWarnings({"unused", "unchecked"})
    protected void getData() throws SQLException {
        DatabaseMetaData md;
        ResultSetMetaData rm;
        Connection cn;
        ResultSet rs;
        String libTemp, libSave, nameTemp, nameSave;

        listData.clear();
        SwingUtilities.invokeLater(() -> {
            fireTableDataChanged();
        });
        try {
            cn = as400.getConnection();
            md = cn.getMetaData();
            rs = md.getIndexInfo(null, schema, name, false, true);
            rm = rs.getMetaData();
            // fields: 1)TABLE_CAT 2)TABLE_SCHEM 3)TABLE_NAME 4)NON_UNIQUE
            // 5)INDEX_QUALIFIER 6)INDEX_NAME 7)TYPE 8)ORDINAL_POSITION
            // 9)COLUMN_NAME 10)ASC_OR_DESC 11)CARDINALITY 12)PAGES
            // 13)FILTER_CONDITION
            libSave = "";
            nameSave = "";
            while (rs.next()) {
                libTemp = isNull(rs.getString(5));
                nameTemp = isNull(rs.getString(6));
                if (libTemp.equalsIgnoreCase(libSave)) {
                    listData.add("");
                } else {
                    listData.add(libTemp);
                    libSave = libTemp;
                }
                if (nameTemp.equalsIgnoreCase(nameSave)) {
                    listData.add("");
                } else {
                    listData.add(nameTemp);
                    nameSave = nameTemp;
                }
                listData.add(isNull(rs.getString(9)));
                listData.add(isNull(rs.getString(10)));
                listData.add(isNull(rs.getString(13)));
            }
            rs.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            fireTableDataChanged();
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

    @Override
    public int getRowCount() {
        return listData.size() / getColumnCount();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public Object getValueAt(int row, int column) {
        int index;

        index = (row * getColumnCount()) + column;
        if (index >= listData.size()) {
            return "";
        }
        return listData.get(index);
    }
}
