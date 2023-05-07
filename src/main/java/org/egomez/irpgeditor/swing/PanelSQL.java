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
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.icons.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * work with sql.
 *
 * @author not attributable
 */
public final class PanelSQL extends PanelTool implements OutputSQL {

    /**
     *
     */
    private static final long serialVersionUID = -1965501320180579481L;
    ActionSqlExecute actionSqlExecute = new ActionSqlExecute();
    ActionSqlClear actionSqlClear = new ActionSqlClear();
    ActionSqlFocus actionSqlFocus = new ActionSqlFocus();
    transient Logger logger = LoggerFactory.getLogger(PanelSQL.class);

    JPanel jPanel8 = new JPanel();
    JPanel jPanel7 = new JPanel();
    JScrollPane jScrollPane5 = new JScrollPane();
    JTextPane textareaSqlResults = new JTextPane();
    BorderLayout borderLayout8 = new BorderLayout();
    BorderLayout borderLayout5 = new BorderLayout();
    BorderLayout borderLayout7 = new BorderLayout();
    JTextArea textareaSql = new JTextArea();
    BorderLayout borderLayout6 = new BorderLayout();
    JPanel jPanel9 = new JPanel();
    JSplitPane jSplitPane5 = new JSplitPane();
    JScrollPane scrollpaneSqlResults = new JScrollPane();

    public PanelSQL() {
        try {
            jbInit();
            loadSettings();
            new HandlerKeyPressed(textareaSql);
            new HandlerKeyPressed(textareaSqlResults);
            super.actions = new Action[]{actionSqlExecute, actionSqlClear, actionSqlFocus};
            Environment.actions.addActions(actions);
            setName("SQL");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void jbInit() throws Exception {
        jPanel8.setLayout(borderLayout6);
        setLayout(borderLayout5);
        jPanel7.setLayout(borderLayout7);
        textareaSqlResults.setAutoscrolls(false);
        textareaSqlResults.setFont(new java.awt.Font("DialogInput", 0, 14));
        textareaSql.setFont(new java.awt.Font("DialogInput", 0, 14));
        jPanel9.setLayout(borderLayout8);
        jSplitPane5.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane5.setDividerLocation(50);
        jSplitPane5.add(jPanel7, JSplitPane.TOP);
        jPanel7.add(jScrollPane5, BorderLayout.CENTER);
        jSplitPane5.add(jPanel8, JSplitPane.BOTTOM);
        jPanel8.add(scrollpaneSqlResults, BorderLayout.CENTER);
        scrollpaneSqlResults.getViewport().add(jPanel9, null);
        jPanel9.add(textareaSqlResults, BorderLayout.CENTER);
        jScrollPane5.getViewport().add(textareaSql, null);
        add(jSplitPane5, BorderLayout.CENTER);
    }

    @Override
    public void saveSettings() {
        Environment.settings.setProperty("sql", textareaSql.getText());
        Environment.settings.setProperty("panelSql.jSplitPane5.dividerLocation",
                Integer.toString(jSplitPane5.getDividerLocation()));
    }

    public void loadSettings() {
        if (Environment.settings.getProperty("sql") != null) {
            textareaSql.setText(Environment.settings.getProperty("sql"));
            jSplitPane5.setDividerLocation(
                    Integer.parseInt(Environment.settings.getProperty("panelSql.jSplitPane5.dividerLocation")));
        }
    }

    @Override
    public void executeSQL(String sql) {
        appendSQL(sql);
        startExecute(sql);
    }

    @Override
    public void clear() {
        // textareaSql.setText("");
        textareaSqlResults.setText("");
    }

    @Override
    public void focus() {
        // throw an event here, so that what ever container this panel is placed
        // into, it should show this panel as the focus.
        fireRequestingFocus();
        textareaSql.requestFocus();
    }

    @Override
    public void setSQL(String sql) {
        textareaSql.setText(sql);
    }

    @Override
    public void appendSQL(String sql) {
        if (sql == null) {
            return;
        }
        if (textareaSql.getText().endsWith("\n") == false && textareaSql.getText().length() > 0) {
            textareaSql.append("\n");
        }
        textareaSql.append(sql);
    }

    public String getSQL() {
        return textareaSql.getText();
    }

    protected void startExecute(String sql) {
        final String temp;

        fireRequestingFocus();
        if (sql == null) {
            sql = textareaSql.getSelectedText();
            if (sql == null) {
                sql = textareaSql.getText();
                if (sql.trim().length() == 0) {
                    textareaSqlResults.setText("No sql to execute...");
                    textareaSqlResults.setSelectionStart(0);
                    textareaSqlResults.setSelectionEnd(0);
                    return;
                }
            }
        }
        temp = sql;
        textareaSqlResults.setText("Executing sql...");
        textareaSqlResults.setSelectionStart(0);
        textareaSqlResults.setSelectionEnd(0);
        new Thread() {
            @Override
            public void run() {
                execute(temp);
            }
        }.start();
    }

    protected void execute(String sql) {
        int index, type, direction;
        String name, lib, values, value;
        StringTokenizer tokenizer;
        Connection connection;
        try {
            connection = Environment.systems.getDefault().getConnectionPool();
            synchronized (connection) {
                // is a callable statement?
                if (sql.trim().toUpperCase().startsWith("CALL")) {
                    // pull the values for the procedure from the string.
                    index = sql.indexOf("\n");
                    if (index > -1) {
                        values = sql.substring(index + 1);
                        sql = sql.substring(0, index);
                    } else {
                        values = "";
                    }
                    CallableStatement call = connection.prepareCall(sql);
                    DatabaseMetaData meta = connection.getMetaData();
                    // get the name of the procedure and lib if possible.
                    name = sql.trim().toUpperCase();
                    index = name.indexOf("CALL");
                    name = name.substring(index + 4).trim();
                    index = name.indexOf("(");
                    if (index > -1) {
                        name = name.substring(0, index);
                    }
                    // is there a library?
                    index = name.indexOf("/");
                    if (index > -1) {
                        lib = name.substring(0, index);
                        name = name.substring(index + 1);
                    } else {
                        lib = "";
                    }
                    // register output parameters and set parameter values.
                    tokenizer = new StringTokenizer(values, ",");
                    ResultSet rs = meta.getProcedureColumns(null, lib, name, null);
                    index = 1;
                    while (rs.next()) {
                        type = rs.getInt(6);
                        direction = rs.getInt(5);
                        if (direction == DatabaseMetaData.procedureColumnInOut
                                || direction == DatabaseMetaData.procedureColumnOut) {
                            call.registerOutParameter(index, type);
                        }
                        if (direction == DatabaseMetaData.procedureColumnInOut
                                || direction == DatabaseMetaData.procedureColumnIn) {
                            if (tokenizer.hasMoreTokens()) {
                                value = tokenizer.nextToken().trim();
                                if (type == Types.CHAR || type == Types.LONGVARCHAR || type == Types.VARCHAR) {
                                    call.setString(index, value);
                                } else {
                                    call.setInt(index, Integer.parseInt(value));
                                }
                            } else {
                                if (type == Types.CHAR || type == Types.LONGVARCHAR || type == Types.VARCHAR) {
                                    call.setString(index, "");
                                } else {
                                    call.setInt(index, 0);
                                }
                            }
                        }
                        index++;
                    }
                    call.execute();
                    startBuildResults(call, (StyledDocument) textareaSqlResults.getDocument());
                } else {
                    Statement stmt = connection.createStatement();
                    stmt.execute(sql);
                    startBuildResults(stmt, (StyledDocument) textareaSqlResults.getDocument());
                }
            }
        } catch (final NumberFormatException | SQLException e) {

            logger.error(e.getMessage());
            SwingUtilities.invokeLater(() -> {
                textareaSqlResults.setText(e.getMessage());
                textareaSqlResults.setSelectionStart(0);
                textareaSqlResults.setSelectionEnd(0);
            });
        }
    }

    protected void startBuildResults(final Statement stmt, final StyledDocument document) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                SQLWarning warn;
                StringBuffer results, parms;
                results = new StringBuffer();
                try {
                    warn = stmt.getWarnings();
                    while (warn != null) {
                        results.append(warn.getErrorCode());
                        results.append(warn.getMessage());
                        results.append("\n");
                        warn = warn.getNextWarning();
                    }
                    parms = getParmOutput(stmt);
                    results.append(parms.toString());
                    rs = stmt.getResultSet();
                    if (rs == null) {
                        results.append("Statement completed.\n");
                        textareaSqlResults.setText(results.toString());
                    } else {
                        textareaSqlResults.setText(results.toString());
                        while (rs != null) {
                            buildResults(rs, document);
                            if (stmt.getMoreResults()) {
                                rs = stmt.getResultSet();
                            } else {
                                rs = null;
                            }
                        }
                    }
                    stmt.close();
                } catch (Exception e) {

                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                    logger.error(e.getMessage());
                }
                textareaSqlResults.setSelectionStart(0);
                textareaSqlResults.setSelectionEnd(0);
            }
        });
    }

    
    protected StringBuffer getParmOutput(Statement stmt) throws SQLException {
        CallableStatement call;
        ParameterMetaData meta;
        StringBuffer buffer;

        buffer = new StringBuffer();
        if (stmt instanceof CallableStatement == false) {
            return buffer;
        }
        call = (CallableStatement) stmt;
        meta = call.getParameterMetaData();
        for (int x = 1; x <= meta.getParameterCount(); x++) {
            if (meta.getParameterMode(x) == ParameterMetaData.parameterModeInOut
                    || meta.getParameterMode(x) == ParameterMetaData.parameterModeOut) {
                buffer.append("PARM: ");
                buffer.append(x);
                meta.getParameterType(x);
                buffer.append(", VALUE: ");
                buffer.append(call.getObject(x).toString());
                buffer.append("\n");
            }
        }
        return buffer;
    }

    protected void buildResults(ResultSet rs, StyledDocument document) throws Exception {
        ResultSetMetaData meta;
        int needed;
        int[] sizes;
        int temp;
        int row;// index;
        String buffer;
        Object o;
        SimpleAttributeSet attributes;

        attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, "DialogInput");
        StyleConstants.setFontSize(attributes, 14);
        StyleConstants.setForeground(attributes, new Color(0, 0, 0));
        meta = rs.getMetaData();
        sizes = new int[meta.getColumnCount()];
        document.insertString(document.getLength(), "       ROW | ", attributes);
        for (int x = 0; x < meta.getColumnCount(); x++) {
            buffer = meta.getColumnName(x + 1);
            document.insertString(document.getLength(), buffer, attributes);
            sizes[x] = buffer.length();
            if (meta.getColumnDisplaySize(x + 1) > sizes[x]) {
                sizes[x] = meta.getColumnDisplaySize(x + 1);
            }
            temp = meta.getScale(x + 1);
            if (meta.getPrecision(x + 1) > 0) {
                temp += meta.getPrecision(x + 1);
                // add decimal.
                temp += 1;
            }
            if (temp > sizes[x]) {
                sizes[x] = temp;
            }
            needed = sizes[x] - buffer.length();
            while (needed > 0) {
                document.insertString(document.getLength(), " ", attributes);
                needed--;
            }
            document.insertString(document.getLength(), " ", attributes);
        }
        document.insertString(document.getLength(), "\n-----------| ", attributes);
        needed = 0;
        for (int x = 0; x < sizes.length; x++) {
            needed = sizes[x];
            while (needed > 0) {
                document.insertString(document.getLength(), "-", attributes);
                needed--;
            }
            document.insertString(document.getLength(), " ", attributes);
        }
        document.insertString(document.getLength(), "\n", attributes);
        row = 1;
        while (rs.next() && row <= 200) {
            StyleConstants.setForeground(attributes, Color.black);
            StyleConstants.setBackground(attributes, Color.white);
            StyleConstants.setForeground(attributes, new Color(0, 0, 0));
            needed = 10 - Integer.toString(row).length();
            while (needed > 0) {
                document.insertString(document.getLength(), " ", attributes);
                needed--;
            }
            document.insertString(document.getLength(), Integer.toString(row), attributes);
            document.insertString(document.getLength(), " | ", attributes);

            for (int x = 0; x < sizes.length; x++) {
                if (row % 2 == 0) {
                    if (x % 2 == 0) {
                        StyleConstants.setBackground(attributes, new Color(215, 215, 255));
                    } else {
                        StyleConstants.setBackground(attributes, new Color(195, 195, 255));
                    }
                } else {
                    if (x % 2 == 0) {
                        StyleConstants.setBackground(attributes, new Color(215, 255, 215));
                    } else {
                        StyleConstants.setBackground(attributes, new Color(195, 255, 195));
                    }
                }
                o = rs.getObject(x + 1);
                if (o == null) {
                    buffer = "";
                } else {
                    buffer = o.toString();
                }
                needed = sizes[x] - buffer.length();
                if (meta.getColumnTypeName(x + 1).startsWith("C")) {
                    // character.
                    while (needed > 0) {
                        buffer = buffer + " ";
                        needed--;
                    }
                } else {
                    // decimal.
                    while (needed > 0) {
                        buffer = " " + buffer;
                        needed--;
                    }
                }
                document.insertString(document.getLength(), buffer, attributes);
                document.insertString(document.getLength(), " ", attributes);
            }
            document.insertString(document.getLength(), "\n", attributes);
            row++;
        }
        rs.close();
    }

    /**
     * gets caled when the user wants to execute sql.
     */
    class ActionSqlExecute extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 7809719090732589943L;

        public ActionSqlExecute() {
            super("Execute SQL", Icons.iconSqlExecute);
            setEnabled(true);
            putValue("MENU", "Database");
            // F11
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(122, 0, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @SuppressWarnings("static-access")
        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.sql.executeSQL(null);
            Environment.sql.focus();
        }
    }

    class ActionSqlClear extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -6760370446992217704L;

        public ActionSqlClear() {
            super("Clear SQL Results");
            setEnabled(true);
            putValue("MENU", "Database");
            // F11 + SHIFT
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(122, KeyEvent.SHIFT_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @SuppressWarnings("static-access")
        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.sql.clear();
            Environment.sql.focus();
        }
    }

    @SuppressWarnings("serial")
    class ActionSqlFocus extends AbstractAction {

        public ActionSqlFocus() {
            super("SQL", Icons.iconDatabase);
            setEnabled(true);
            putValue("MENU", "Tools");
            // F11 + CTRL
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(122, KeyEvent.CTRL_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @SuppressWarnings("static-access")
        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.sql.focus();
        }
    }
}
