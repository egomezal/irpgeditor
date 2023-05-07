package org.egomez.irpgeditor.swing;

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
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import com.ibm.as400.access.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.Icons;
import org.egomez.irpgeditor.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Works with spool files.
 *
 * @author Derek Van Kooten.
 */
@SuppressWarnings("serial")
public class PanelSpoolFiles extends PanelTool implements ListenerAS400Systems, ListSelectionListener, Runnable {

    TableModelSpool tableModelSpool = new TableModelSpool();
    AS400System as400;
    ActionSpoolRefresh actionSpoolRefresh = new ActionSpoolRefresh();
    ActionSpoolFileView actionSpoolFileView = new ActionSpoolFileView();
    ActionSpoolFileDelete actionSpoolFileDelete = new ActionSpoolFileDelete();
    ActionSpoolOtherUser actionSpoolOtherUser = new ActionSpoolOtherUser();
    ActionSpoolOtherDataUser actionSpoolOtherUserData = new ActionSpoolOtherDataUser();

    static final int SPOOL_EXPORT_TEXT = 1;
    static final int SPOOL_EXPORT_PDF = 2;
    ActionSelectAll actionSelectAll = new ActionSelectAll();
    ActionFocus actionFocus = new ActionFocus();

    JTable tableSpool = new JTable(tableModelSpool);
    JPopupMenu popupMenu = new JPopupMenu();

    JButton buttonSpooledFileView = new JButton();
    JButton buttonSpooledFileDelete = new JButton();
    JButton buttonSpooledOtherUser = new JButton();
    JButton buttonSpooledOtherUserData = new JButton();

    JScrollPane scrollpaneSpool = new JScrollPane();
    JButton buttonSpoolRefresh = new JButton();
    JPanel panelSpoolButtons = new JPanel();
    FlowLayout flowLayoutSpoolButtons = new FlowLayout();
    BorderLayout borderLayout1 = new BorderLayout();
    JButton buttonSelectAll = new JButton();
    Logger logger = LoggerFactory.getLogger(PanelSpoolFiles.class);
    // private PdfWriter bos;
    private Document document;

    int exportType = 0;
    SpooledFile file;
    PrintStream localPrintStream;
    File target;

    public PanelSpoolFiles() {
        setName("Spool Files");
        Environment.systems.addListener(this);
        as400 = Environment.systems.getDefault();
        try {
            jbInit();
            super.actions = new Action[]{actionSpoolRefresh, actionSpoolFileView, actionSpoolFileDelete,
                actionSelectAll, actionFocus};
            Environment.actions.addActions(actions);
            buttonSpooledFileDelete.addActionListener(actionSpoolFileDelete);
            buttonSpooledFileView.addActionListener(actionSpoolFileView);
            buttonSpoolRefresh.addActionListener(actionSpoolRefresh);
            buttonSelectAll.addActionListener(actionSelectAll);
            buttonSpooledOtherUser.addActionListener(actionSpoolOtherUser);
            buttonSpooledOtherUserData.addActionListener(actionSpoolOtherUserData);
            tableModelSpool.setAS400System(as400);
            tableSpool.getSelectionModel().addListSelectionListener(this);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void jbInit() throws Exception {
        tableSpool.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Agregando PopUp Menu
        addPopup(tableSpool, popupMenu);
        JMenuItem mntmView = new JMenuItem("View");
        mntmView.setIcon(Icons.iconSpoolPreview);
        mntmView.addActionListener((ActionEvent e) -> {
            SpooledFile file1;
            int[] rows;
            focus();
            rows = tableSpool.getSelectedRows();
            if (rows == null || rows.length == 0) {
                return;
            }
            for (int x1 = 0; x1 < rows.length; x1++) {
                file1 = (SpooledFile) tableModelSpool.getSpooledFile(rows[x1]);
                Environment.spoolFiles.open(file1);
            }
        });
        popupMenu.add(mntmView);

        JMenuItem mntmExportToText = new JMenuItem("Export to Text");
        mntmExportToText.setIcon(Icons.iconSpooltoText);
        mntmExportToText.addActionListener((ActionEvent e) -> {
            JFileChooser dlgArchivo = new JFileChooser();
            dlgArchivo.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            dlgArchivo.setDialogTitle("Save File");
            dlgArchivo.setDialogType(JFileChooser.SAVE_DIALOG);
            FileFilter filter1 = new ExtensionFileFilter("Text File (*.txt)", new String[]{"TXT", "txt"});
            dlgArchivo.setFileFilter(filter1);
            int retval = dlgArchivo.showDialog(getPanel(), null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                if (dlgArchivo.getSelectedFile().exists()) {
                    dlgArchivo.getSelectedFile().delete();
                }
                target = dlgArchivo.getSelectedFile();
                String name1 = addFileExtIfNecessary(target.getName(), ".txt");
                if (!name1.toUpperCase().equals(target.getName().toUpperCase())) {
                    target = new File(target.getAbsolutePath() + ".txt");
                }
                focus();
                int row = tableSpool.getSelectedRow();
                file = (SpooledFile) tableModelSpool.getSpooledFile(row);
                exportType = SPOOL_EXPORT_TEXT;
                Thread t1 = new Thread(getPanel());
                t1.start();
            }
        });
        popupMenu.add(mntmExportToText);

        JMenuItem mntmExportToPdf = new JMenuItem("Export to PDF");
        mntmExportToPdf.setIcon(Icons.iconSpooltoPDF);
        mntmExportToPdf.addActionListener((ActionEvent e) -> {
            JFileChooser dlgArchivo = new JFileChooser();
            dlgArchivo.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            dlgArchivo.setDialogTitle("Save File");
            dlgArchivo.setDialogType(JFileChooser.SAVE_DIALOG);
            FileFilter filter1 = new ExtensionFileFilter("PDF Files (*.pdf)", new String[]{"PDF", "pdf"});
            dlgArchivo.setFileFilter(filter1);
            int retval = dlgArchivo.showDialog(getPanel(), null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                if (dlgArchivo.getSelectedFile().exists()) {
                    dlgArchivo.getSelectedFile().delete();
                }
                target = dlgArchivo.getSelectedFile();
                String name1 = addFileExtIfNecessary(target.getName(), ".pdf");
                if (!name1.toUpperCase().equals(target.getName().toUpperCase())) {
                    target = new File(target.getAbsolutePath() + ".pdf");
                }
                focus();
                int row = tableSpool.getSelectedRow();
                file = (SpooledFile) tableModelSpool.getSpooledFile(row);
                exportType = SPOOL_EXPORT_PDF;
                Thread t1 = new Thread(getPanel());
                t1.start();
            }
        });
        popupMenu.add(mntmExportToPdf);

        // Fin d Menu
        buttonSpooledFileView.setEnabled(false);
        buttonSpooledFileView.setMargin(new Insets(0, 0, 0, 0));
        buttonSpooledFileView.setText("View");
        buttonSpooledFileDelete.setText("Delete");
        buttonSpooledFileDelete.setEnabled(false);
        buttonSpooledFileDelete.setMargin(new Insets(0, 0, 0, 0));

        buttonSpooledOtherUser.setText("Other User");
        buttonSpooledOtherUser.setEnabled(true);
        buttonSpooledOtherUser.setMargin(new Insets(0, 0, 0, 0));

        buttonSpooledOtherUserData.setText("User Data");
        buttonSpooledOtherUserData.setEnabled(true);
        buttonSpooledOtherUserData.setMargin(new Insets(0, 0, 0, 0));

        buttonSpoolRefresh.setMargin(new Insets(0, 0, 0, 0));
        buttonSpoolRefresh.setText("Refresh");
        panelSpoolButtons.setLayout(flowLayoutSpoolButtons);
        flowLayoutSpoolButtons.setVgap(2);
        flowLayoutSpoolButtons.setHgap(2);
        flowLayoutSpoolButtons.setAlignment(FlowLayout.LEFT);
        this.setLayout(borderLayout1);
        buttonSelectAll.setMargin(new Insets(0, 0, 0, 0));
        buttonSelectAll.setMnemonic('A');
        buttonSelectAll.setText("Select All");
        panelSpoolButtons.add(buttonSpoolRefresh, null);
        panelSpoolButtons.add(buttonSpooledFileDelete, null);
        panelSpoolButtons.add(buttonSpooledFileView, null);
        panelSpoolButtons.add(buttonSpooledOtherUser, null);
        panelSpoolButtons.add(buttonSpooledOtherUserData, null);

        panelSpoolButtons.add(buttonSelectAll, null);
        add(scrollpaneSpool, BorderLayout.CENTER);
        scrollpaneSpool.getViewport().add(tableSpool, null);
        add(panelSpoolButtons, BorderLayout.NORTH);
    }

    private PanelSpoolFiles getPanel() {
        return this;
    }

    private String addFileExtIfNecessary(String file, String ext) {
        if (file.lastIndexOf('.') == -1) {
            file += ext;
        }

        return file;
    }

    @Override
    public void addedSytem(AS400System system) {
    }

    @Override
    public void removedSytem(AS400System system) {
    }

    @Override
    public void defaultSytem(AS400System system) {
        tableModelSpool.setAS400System(system);
        as400 = system;
    }

    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            @Override
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

    /**
     * when a user clicks a item in the table of spool files.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e != null && e.getValueIsAdjusting()) {
            return;
        }
        int count = tableSpool.getSelectedRowCount();
        if (count > 0) {
            actionSpoolFileDelete.setEnabled(true);
            actionSpoolFileView.setEnabled(true);
            buttonSpooledFileDelete.setEnabled(true);
            buttonSpooledFileView.setEnabled(true);
        } else {
            actionSpoolFileDelete.setEnabled(false);
            actionSpoolFileView.setEnabled(false);
            buttonSpooledFileDelete.setEnabled(false);
            buttonSpooledFileView.setEnabled(false);
        }
    }

    /**
     * gets called when the user wants to refresh the spool list.
     */
    class ActionSpoolRefresh extends AbstractAction {

        public ActionSpoolRefresh() {
            super("Refresh");
            setEnabled(true);
            putValue("MENU", "Spool");
            // F7 + CTRL
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118,
            // KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
            tableModelSpool.reset();
        }
    }

    class ActionSpoolOtherUser extends AbstractAction {

        public ActionSpoolOtherUser() {
            super("Other User");
            setEnabled(true);
            putValue("MENU", "Spool");
            // F7 + CTRL
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118,
            // KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
            String user = JOptionPane.showInputDialog("User:");
            if (user.equals("")) {
                tableModelSpool.reset();
            } else {
                //JOptionPane.showMessageDialog(null, "Wait for process...");
                tableModelSpool.reset(1, user);
            }
        }
    }

    /**
     *
     */
    class ActionSpoolOtherDataUser extends AbstractAction {

        public ActionSpoolOtherDataUser() {
            super("User Data");
            setEnabled(true);
            putValue("MENU", "Spool");
            // F7 + CTRL
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118,
            // KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
            String program = JOptionPane.showInputDialog("User Data:");
            if (program.equals("")) {
                tableModelSpool.reset();
            } else {
                //JOptionPane.showMessageDialog(null, "Wait for process...");
                tableModelSpool.reset(2, program);
            }
        }
    }

    /**
     * gets called when the user wants to delete a spooled file.
     */
    class ActionSpoolFileDelete extends AbstractAction {

        public ActionSpoolFileDelete() {
            super("Delete");
            setEnabled(false);
            putValue("MENU", "Spool");
            // F7 + CTRL
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118,
            // KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void actionPerformed(ActionEvent evt) {
            SpooledFile file;
            int[] rows;
            ArrayList list;

            focus();
            rows = tableSpool.getSelectedRows();
            if (rows == null || rows.length == 0) {
                return;
            }
            if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete Spooled File",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                return;
            }
            list = new ArrayList();
            for (int x = 0; x < rows.length; x++) {
                list.add(tableModelSpool.getSpooledFile(rows[x]));
            }
            while (!list.isEmpty()) {
                file = (SpooledFile) list.remove(0);
                try {
                    file.delete();
                } catch (AS400SecurityException | ErrorCompletingRequestException | IOException | InterruptedException e) {
                    // e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            tableModelSpool.reset();
        }
    }

    /**
     * gets called when the user wants to view a spooled file.
     */
    class ActionSpoolFileView extends AbstractAction {

        public ActionSpoolFileView() {
            super("View", Icons.iconSpoolPreview);
            setEnabled(false);
            putValue("MENU", "Spool");
            // F7
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118, 0, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            SpooledFile file;
            int[] rows;

            focus();
            rows = tableSpool.getSelectedRows();
            if (rows == null || rows.length == 0) {
                return;
            }

            for (int x = 0; x < rows.length; x++) {
                file = (SpooledFile) tableModelSpool.getSpooledFile(rows[x]);
                Environment.spoolFiles.open(file);
            }
        }
    }

    /**
     * gets called when the user wants to select all spooled files.
     */
    class ActionSelectAll extends AbstractAction {

        public ActionSelectAll() {
            super("Select All");
            setEnabled(true);
            putValue("MENU", "Spool");
            // F7
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118, 0,
            // false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
            tableSpool.selectAll();
        }
    }

    /**
     * starts a green screen and runs it in debug.
     */
    class ActionFocus extends AbstractAction {

        public ActionFocus() {
            super("Spool Files", Icons.iconSpool);
            setEnabled(true);
            putValue("MENU", "Tools");
            // F7 + CTRL
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(118, KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
        }
    }

    class ExtensionFileFilter extends FileFilter {

        String description;

        String extensions[];

        public ExtensionFileFilter(String description, String extension) {
            this(description, new String[]{extension});
        }

        public ExtensionFileFilter(String description, String extensions[]) {
            if (description == null) {
                this.description = extensions[0];
            } else {
                this.description = description;
            }
            this.extensions = (String[]) extensions.clone();
            toLower(this.extensions);
        }

        private void toLower(String array[]) {
            for (int i = 0, n = array.length; i < n; i++) {
                array[i] = array[i].toLowerCase();
            }
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                for (int i = 0, n = extensions.length; i < n; i++) {
                    String extension = extensions[i];
                    if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                localPrintStream = new PrintStream(target);

                switch (exportType) {
                    case SPOOL_EXPORT_TEXT:
                        break;
                    case SPOOL_EXPORT_PDF:
                        document = new Document(PageSize.A4);
                        PdfWriter.getInstance(document, localPrintStream);
                        document.open();
                        break;
                }

                PrintParameterList localPrintParameterList = new PrintParameterList();
                localPrintParameterList.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT,
                        "/QSYS.LIB/QWPDEFAULT.WSCST");
                localPrintParameterList.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");
                localPrintParameterList.setParameter(PrintObject.ATTR_CODEPAGE, 284);
                PrintObjectTransformedInputStream localPrintObjectTransformedInputStream = file
                        .getTransformedInputStream(localPrintParameterList);
                byte[] buf = new byte[32767];
                StringBuilder buffer = new StringBuilder();
                int bytesRead = 0;
                do {
                    bytesRead = localPrintObjectTransformedInputStream.read(buf);
                    // System.out.println(bytesRead);
                    if (bytesRead > 0) {
                        buffer.append(new String(buf, 0, bytesRead, "cp437"));
                    }
                } while (bytesRead != -1);

                String cadena = buffer.toString();
                cadena = cadena.replace('\r', ' ');
                switch (exportType) {
                    case SPOOL_EXPORT_TEXT:
                        if (buffer.length() > 0) {
                            localPrintStream.println(cadena.toCharArray());
                        }
                        localPrintStream.flush();
                        localPrintStream.close();
                        break;
                    case SPOOL_EXPORT_PDF:
                        Paragraph p = new Paragraph(cadena,
                                FontFactory.getFont(FontFactory.COURIER, 6, com.itextpdf.text.Font.NORMAL));
                        // p.setFont(courierFont);
                        if (buffer.length() > 0) {
                            document.add(p);
                        }

                        document.close();
                        document = null;
                        break;
                }

                JOptionPane.showMessageDialog(null, "File " + target.getAbsolutePath() + " was saved.");

            } catch (FileNotFoundException e) {
                
                logger.error(e.getMessage());
            }catch (IOException | AS400SecurityException | InterruptedException | RequestNotSupportedException | DocumentException | ErrorCompletingRequestException e) {
                logger.error(e.getMessage());
            }
       
            
        }
    }
}
