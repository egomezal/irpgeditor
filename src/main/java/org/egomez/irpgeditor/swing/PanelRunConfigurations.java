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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays a projects run configurations.
 *
 * @author not attributable
 */
public class PanelRunConfigurations extends PanelTool implements ListenerProjects, ListSelectionListener {

    /**
     *
     */
    private static final long serialVersionUID = -5478006094238354646L;

    TableModelRunConfigurations tableModelRunConfigurations = new TableModelRunConfigurations();
    transient Logger logger = LoggerFactory.getLogger(PanelRunConfigurations.class);
    ActionRun actionRun = new ActionRun();
    ActionFocus actionFocus = new ActionFocus();
    ActionDelete actionDelete = new ActionDelete();

    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTable tableRunConfigurations = new JTable(tableModelRunConfigurations);
    JPanel jPanel1 = new JPanel();
    JButton buttonDelete = new JButton();
    FlowLayout flowLayout1 = new FlowLayout();

    public PanelRunConfigurations() {
        setName("Run Configurations");
        try {
            super.actions = new Action[] { actionRun, actionFocus, actionDelete };
            Environment.actions.addActions(actions);
            jbInit();
            Environment.projects.addListener(this);
            selected(Environment.projects.getSelected());
            buttonDelete.addActionListener(actionDelete);
            tableRunConfigurations.getSelectionModel().addListSelectionListener(this);
        } catch (Exception e) {

            logger.error(e.getMessage());
        }
    }

    private void jbInit() {
        this.setLayout(borderLayout1);
        buttonDelete.setEnabled(false);
        buttonDelete.setMargin(new Insets(0, 0, 0, 0));
        buttonDelete.setText("Delete");
        jPanel1.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(0);
        flowLayout1.setVgap(0);
        this.add(jScrollPane1, BorderLayout.CENTER);
        this.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(buttonDelete, null);
        jScrollPane1.getViewport().add(tableRunConfigurations, null);
    }

    @Override
    public void selected(Project project) {
        tableModelRunConfigurations.setProject(project);
    }

    @Override
    public void added(Project project, int index) {
        //
    }

    @Override
    public void removed(Project project, int index) {
        //
    }

    public RunConfiguration getSelected() {
        return tableModelRunConfigurations.getSelected();
    }

    /**
     * when a user clicks a item in the table of run configurations.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e != null && e.getValueIsAdjusting()) {
            return;
        }
        int count = tableRunConfigurations.getSelectedRowCount();
        if (count > 0) {
            actionDelete.setEnabled(true);
            buttonDelete.setEnabled(true);
        } else {
            actionDelete.setEnabled(false);
            buttonDelete.setEnabled(false);
        }
    }

    /**
     * starts a green screen and runs it in debug.
     */
    class ActionRun extends AbstractAction implements Runnable {

        /**
         *
         */
        private static final long serialVersionUID = 5140638312970456453L;

        public ActionRun() {
            super("Run Program", Icons.iconProgramRun);
            setEnabled(true);
            putValue("MENU", "Build");
            // F8
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(119, 0, false));

        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            new Thread(this).start();
        }

        @Override
        public void run() {
            FrameTN5250J frame;
            RunConfiguration config;

            config = getSelected();
            if (config == null) {
                return;
            }
            frame = new FrameTN5250J();
            frame.run(config);
            if (frame.isSSLFlg()) {
                frame.setVisible(false);

                frame = new FrameTN5250J(true);
                frame.run(config);
            }
        }
    }

    /**
     * starts a green screen and runs it in debug.
     */
    class ActionFocus extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -2998764314259081370L;

        public ActionFocus() {
            super("Run Configurations", Icons.iconRunConfiguration);
            setEnabled(true);
            putValue("MENU", "Tools");
            // F8 + CTRL
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(119, InputEvent.CTRL_DOWN_MASK, false));

        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
        }
    }

    class ActionDelete extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 231196126957114511L;

        public ActionDelete() {
            super("Delete Run Configuration");
            setEnabled(false);
            putValue("MENU", "Build");

        }

        public void actionPerformed(ActionEvent evt) {
            int[] rows = tableRunConfigurations.getSelectedRows();
            if (rows == null || rows.length == 0) {
                return;
            }
            if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete All Selected Run Configurations",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                return;
            }
            for (int x = rows.length - 1; x >= 0; x--) {
                tableModelRunConfigurations.remove(rows[x]);
            }
        }
    }
}
