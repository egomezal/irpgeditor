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
import javax.swing.*;
import javax.swing.table.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.data.*;
import java.util.logging.Level;

/**
 *
 * @author Derek Van Kooten.
 */
public final class TableModelFileDescription extends AbstractTableModel {

    /**
     *
     */
    Logger logger = LoggerFactory.getLogger(TableModelFileDescription.class);
    private static final long serialVersionUID = -5087106156444123173L;
    String schema, name;
    AS400System as400;
    String[] columnNames = new String[]{"Name", "Value"};
    String[] names = new String[]{"Public Authority", "Description", "Source Library", "Source File", "Source Member",
        "Member Count", "Keyed Fields", "Max Key Length", "Maximum Members", "CCSID", "Access Path Maintenance"};
    String[] values = new String[names.length];

    public TableModelFileDescription() {
    }

    public TableModelFileDescription(String schema, String name, AS400System system) throws SQLException {
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
        if (!as400.getAS400().isConnectionAlive()) {
            as400.disconnect();
            try {
                as400.connect();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(TableModelFileDescription.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        getData();
    }

    protected void getData() throws SQLException {
        ProgramCallDocument pcml;
        boolean result;
        String buffer;

        values = new String[names.length];
        SwingUtilities.invokeLater(() -> {
            fireTableDataChanged();
        });
        buffer = name;
        while (buffer.length() < 10) {
            buffer = buffer + " ";
        }
        buffer = buffer + schema;
        while (buffer.length() < 20) {
            buffer = buffer + " ";
        }
        try {
            pcml = new ProgramCallDocument(Environment.systems.getDefault().getAS400(), "api");
            pcml.setValue("qdbrtvfd.receiverLength", pcml.getOutputsize("qdbrtvfd.receiver"));
            // pcml.setValue("qdbrtvfd.receiverLength", new Integer(1024 * 10));
            pcml.setValue("qdbrtvfd.fileName", "" + buffer);
            result = pcml.callProgram("qdbrtvfd");
            if (result == false) {
                Environment.qcmdexec.append(pcml.getMessageList("qdbrtvfd"), QcmdexecOutput.colorResult, result);
            } else {
                values[0] = isNull(pcml.getValue("qdbrtvfd.receiver.publicAuthority"));
                values[1] = isNull(pcml.getValue("qdbrtvfd.receiver.description"));
                values[2] = isNull(pcml.getValue("qdbrtvfd.receiver.sourceLibrary"));
                values[3] = isNull(pcml.getValue("qdbrtvfd.receiver.sourceFile"));
                values[4] = isNull(pcml.getValue("qdbrtvfd.receiver.sourceMember"));
                values[5] = isNull(pcml.getValue("qdbrtvfd.receiver.memberCount"));
                values[6] = isNull(pcml.getValue("qdbrtvfd.receiver.keyFields"));
                values[7] = isNull(pcml.getValue("qdbrtvfd.receiver.maxKeyLength"));
                values[8] = isNull(pcml.getValue("qdbrtvfd.receiver.maxMembers"));
                values[9] = isNull(pcml.getValue("qdbrtvfd.receiver.ccsid"));
                values[10] = isNull(pcml.getValue("qdbrtvfd.receiver.accessMaint"));
                if (values[10].equalsIgnoreCase("I")) {
                    values[10] = "Immediate";
                } else if (values[10].equalsIgnoreCase("D")) {
                    values[10] = "Delayed";
                } else if (values[10].equalsIgnoreCase("R")) {
                    values[10] = "Rebuild";
                }
            }
        } catch (PcmlException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            fireTableDataChanged();
        });
    }

    private String isNull(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public String getName() {
        return name;
    }

    public String getSourceLibrary() {
        return values[2];
    }

    public String getSourceFile() {
        return values[3];
    }

    public String getSourceMember() {
        return values[4];
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public int getRowCount() {
        return names.length;
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
        if (column == 0) {
            return names[row];
        } else {
            return isNull(values[row]);
        }
    }
}
