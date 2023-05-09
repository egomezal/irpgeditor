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
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import org.egomez.irpgeditor.icons.*;

/**
 * changes the color or whatever for the given type of field being displayed.
 *
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class TableCellRendererLayout extends DefaultTableCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 5848387114033539281L;
    TableModelLayout tableModel;
    Color blue = new Color(146, 216, 237);
    Color blueSelected = new Color(53, 143, 170);
    Color green = new Color(222, 255, 222);
    Color greenSelected = new Color(64, 128, 64);
    Color oarnge = new Color(255, 173, 64);
    Color oarngeSelected = new Color(170, 64, 0);
    Color yellow = new Color(255, 253, 163);
    Color yellowSelected = new Color(230, 227, 71);

    public TableCellRendererLayout(TableModelLayout tableModel) {
        this.tableModel = tableModel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component c;

        c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (tableModel.getType(row).startsWith("CHAR")) {
            if (isSelected) {
                c.setBackground(greenSelected);
            } else {
                c.setBackground(green);
            }
        } else if (tableModel.getType(row).startsWith("DATE")) {
            if (isSelected) {
                c.setBackground(oarngeSelected);
            } else {
                c.setBackground(oarnge);
            }
        } else if (tableModel.getType(row).startsWith("TIME")) {
            if (isSelected) {
                c.setBackground(yellowSelected);
            } else {
                c.setBackground(yellow);
            }
        } else {
            if (isSelected) {
                c.setBackground(blueSelected);
            } else {
                c.setBackground(blue);
            }
        }
        if (column == 0 && tableModel.isKey(row)) {
            ((JLabel) c).setIcon(Icons.iconKey);
        } else {
            ((JLabel) c).setIcon(null);
        }
        return c;
    }
}
