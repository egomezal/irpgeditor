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
import java.beans.PropertyVetoException;

import javax.swing.*;
import javax.swing.table.*;

import org.egomez.irpgeditor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * holds spool information.
 *
 * @author Derek Van Kooten.
 */
public class TableModelSpool extends DefaultTableModel implements PrintObjectListListener {

    /**
     *
     */
    private static final long serialVersionUID = 2490562952531707460L;
    AS400System system;
    SpooledFileList spool;
    String[] columns = new String[]{"file", "user", "output queue", "user data", "status", "pages", "current page",
        "copies", "create date", "create time", "job number"};
    Logger logger = LoggerFactory.getLogger(TableModelSpool.class);

    public void setAS400System(AS400System system) {
        this.system = system;
        if (spool != null) {
            spool.removePrintObjectListListener(this);
            spool.close();
            spool = null;
        }
        fireTableDataChanged();
        if (system != null) {
            if (system.isConnected()) {
                spool = new SpooledFileList(system.getAS400());
                spool.addPrintObjectListListener(this);
                spool.openAsynchronously();
            }
        }
    }

    public void setAS400System(AS400System system, int type, String data) throws PropertyVetoException {
        this.system = system;
        if (spool != null) {
            spool.removePrintObjectListListener(this);
            spool.close();
            spool = null;
        }
        fireTableDataChanged();
        if (system != null) {
            spool = new SpooledFileList(system.getAS400());
            switch (type) {
                case 1:
                    spool.setUserFilter(data.toUpperCase());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
                    spool.setStartDateFilter("1" + simpleDateFormat.format(new Date()));
                    spool.setEndDateFilter("*LAST");
                    break;
                case 2:
                    spool.setUserFilter("*ALL");
                    spool.setUserDataFilter(data);
                    break;
            }

            spool.addPrintObjectListListener(this);
            //try {
            spool.openAsynchronously();
            //   spool.openSynchronously();
            //} catch (AS400SecurityException | ErrorCompletingRequestException | InterruptedException | IOException | RequestNotSupportedException ex) {
            //    logger.error(ex.getMessage());
            // }
            fireTableDataChanged();
        }
    }

    public SpooledFile getSpooledFile(int index) {
        return (SpooledFile) spool.getObject(index);
    }

    public void reset() {
        setAS400System(system);
    }

    public void reset(int type, String user) {
        try {
            setAS400System(system, type, user);
        } catch (PropertyVetoException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
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
        int size = 0;
        if (spool == null) {
            return 0;
        }
        try {
            size = spool.size();
        } catch (Exception e) {

        }
        return size;
    }

    @Override
    public Object getValueAt(int row, int col) {
        SpooledFile file;

        file = (SpooledFile) spool.getObject(row);
        switch (col) {
            case 0:
                return file.getName();
            case 1:
                return file.getJobUser();
            case 2:
                try {
                String queue = file.getStringAttribute(PrintObject.ATTR_OUTPUT_QUEUE);
                int index = queue.indexOf("/");
                int count = 1;
                while (count < 3 && index > -1) {
                    index = queue.indexOf("/", index + 1);
                    count++;
                }
                if (index == -1) {
                    return queue;
                }
                queue = queue.substring(index + 1);
                index = queue.indexOf(".");
                if (index == -1) {
                    return queue;
                }
                return queue.substring(0, index);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 3:
                try {
                return file.getStringAttribute(PrintObject.ATTR_USERDATA);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 4:
                try {
                return file.getStringAttribute(SpooledFile.ATTR_SPLFSTATUS);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 5:
                try {
                return file.getIntegerAttribute(PrintObject.ATTR_PAGES);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 6:
                try {
                return file.getIntegerAttribute(SpooledFile.ATTR_CURPAGE);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 7:
                try {
                return file.getIntegerAttribute(SpooledFile.ATTR_COPIES);
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
                return e.getMessage();
            }
            case 8:
                try {
                return formatDate(file.getStringAttribute(PrintObject.ATTR_DATE));
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException rnse) {
                return rnse.getMessage();
            }
            case 9:
                try {
                return formatTime(file.getStringAttribute(PrintObject.ATTR_TIME));
            } catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException rnse) {
                return rnse.getMessage();
            }
            case 10:
                return file.getJobNumber();
            default:
                break;
        }
        return "";
    }

    /**
     * Format the date string from the string passed format is cyymmdd c -
     * century - 0 1900 1 2000 yy - year mm - month dd - day
     *
     * @param dateString String in the format as above
     * @return formatted date string
     */
    protected String formatDate(String dateString) {
        if (dateString != null) {
            char[] dateArray = dateString.toCharArray();
            // check if the length is correct length for formatting the string
            // should
            // be in the format cyymmdd where
            // c = 0 -> 19
            // c = 1 -> 20
            if (dateArray.length != 7) {
                return dateString;
            }
            StringBuilder db = new StringBuilder(10);
            // this will strip out the starting century char as described above
            db.append(dateArray, 1, 6);
            // now we find out what the century byte was and insert the correct
            // 2 char number century in the buffer.
            if (dateArray[0] == '0') {
                db.insert(0, "19");
            } else {
                db.insert(0, "20");
            }
            db.insert(4, '/'); // add the first date seperator
            db.insert(7, '/'); // add the second date seperator
            return db.toString();
        } else {
            return "";
        }
    }

    /**
     * Format the time string with separator of ':'
     *
     * @param timeString
     * @return
     */
    protected String formatTime(String timeString) {
        if (timeString != null) {
            StringBuilder tb = new StringBuilder(timeString);
            tb.insert(tb.length() - 2, ':');
            tb.insert(tb.length() - 5, ':');
            return tb.toString();
        } else {
            return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void listClosed(PrintObjectListEvent e) {
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void listCompleted(PrintObjectListEvent e) {
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void listErrorOccurred(PrintObjectListEvent e) {
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void listObjectAdded(PrintObjectListEvent e) {
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void listOpened(PrintObjectListEvent e) {
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }
}
