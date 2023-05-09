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
import org.egomez.irpgeditor.*;

import java.util.*;
import javax.swing.table.*;

/**
 * holds run cofiguration information.
 *
 * @author Derek Van Kooten.
 */
public class TableModelRunConfigurations extends DefaultTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 4871468704709343189L;
    int selected = 0;
    Project project;
    @SuppressWarnings("rawtypes")
    ArrayList listRun;
    String[] columns = new String[]{
        "Current", "Program", "Parameters", "Libraries", "Debug Program", "Update Production Files", ""
    };

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            listRun = project.getRunConfigurations();
            selected = 0;
        }
        fireTableDataChanged();
    }

    public void remove(int row) {
        if (project == null) {
            return;
        }
        if (row == selected) {
            selected = 0;
        }
        listRun.remove(row);
        fireTableDataChanged();
    }

    public void remove(RunConfiguration config) {
        if (project == null) {
            return;
        }
        if (listRun.indexOf(config) == selected) {
            selected = 0;
        }
        listRun.remove(config);
        fireTableDataChanged();
    }

    public RunConfiguration getSelected() {
        if (project == null) {
            return null;
        }
        if (listRun.isEmpty()) {
            return null;
        }
        return (RunConfiguration) listRun.get(selected);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Class getColumnClass(int col) {
        if (col == 0) {
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
        return columns.length;
    }

    @Override
    public int getRowCount() {
        if (project == null) {
            return 0;
        }
        return listRun.size() + 1;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        RunConfiguration config;
        boolean add;

        if (project == null) {
            return;
        }
        if (row >= listRun.size()) {
            if (col == 0) {
                return;
            }
            config = new RunConfiguration();
            add = true;
        } else {
            config = (RunConfiguration) listRun.get(row);
            add = false;
        }
        if (col == 0) {
            if (((Boolean) value).booleanValue()) {
                fireTableRowsUpdated(selected, selected);
                selected = row;
            }
            return;
        }
        if (value == null || (((String) value).trim().length() == 0 && add)) {
            return;
        }
        switch (col) {
            case 1:
                config.program = (String) value;
                break;
            case 2:
                config.parms = (String) value;
                break;
            case 3:
                config.libraries = (String) value;
                break;
            case 4:
                config.debug = (String) value;
                break;
            default:
                break;
        }
        if (add) {
            project.addRunConfiguration(config);
            fireTableRowsInserted(row, row);
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        RunConfiguration config;

        if (project == null) {
            if (col == 0) {
                return Boolean.FALSE;
            }
            return "";
        }
        if (row >= listRun.size()) {
            if (col == 0) {
                return Boolean.FALSE;
            }
            return "";
        }
        config = (RunConfiguration) listRun.get(row);
        if (col == 0) {
            if (row == selected) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
            // return whether selected or not.
        }
        if (col == 1) {
            return config.program;
        }
        if (col == 2) {
            return config.parms;
        }
        if (col == 3) {
            return config.libraries;
        }
        if (col == 4) {
            return config.debug;
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }
}
