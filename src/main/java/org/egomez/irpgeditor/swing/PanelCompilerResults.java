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
import javax.swing.*;
import javax.swing.text.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.icons.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays compiler results.
 *
 * @author not attributable
 */

public class PanelCompilerResults extends PanelTool implements CompilerResultsOutput {

    /**
     *
     */
    private static final long serialVersionUID = 3388618378997281411L;
    transient Logger logger = LoggerFactory.getLogger(PanelCompilerResults.class);
    JTextComponent textComponent;

    transient MouseAdapterCompilerResults mouseAdapterCompilerResults = new MouseAdapterCompilerResults();

    ActionClear actionClear = new ActionClear();
    ActionFocus actionFocus = new ActionFocus();

    JButton buttonClear = new JButton();
    JTextPane textareaCompilerResults = new JTextPane();
    FlowLayout flowLayoutCompileResultsButtons = new FlowLayout();
    BorderLayout borderLayoutCompileResults = new BorderLayout();
    JPanel panelCompileResultsButtons = new JPanel();
    JScrollPane scrollpaneCompileResults = new JScrollPane();

    public PanelCompilerResults() {
        setName("Compile Results");
        try {
            jbInit();
            super.actions = new Action[]{actionClear, actionFocus};
            Environment.actions.addActions(actions);
            new HandlerKeyPressed(textareaCompilerResults);
            buttonClear.addActionListener(actionClear);
            textareaCompilerResults.addMouseListener(mouseAdapterCompilerResults);
        } catch (Exception e) {
            
            logger.error(e.getMessage());
        }
    }

    private void jbInit() throws Exception {
        setLayout(borderLayoutCompileResults);
        textareaCompilerResults.setFont(new java.awt.Font("DialogInput", 0, 14));
        buttonClear.setText("Clear");
        buttonClear.setMargin(new Insets(0, 0, 0, 0));
        flowLayoutCompileResultsButtons.setAlignment(FlowLayout.LEFT);
        flowLayoutCompileResultsButtons.setHgap(2);
        flowLayoutCompileResultsButtons.setVgap(2);
        panelCompileResultsButtons.setLayout(flowLayoutCompileResultsButtons);
        add(scrollpaneCompileResults, BorderLayout.CENTER);
        add(panelCompileResultsButtons, BorderLayout.NORTH);
        panelCompileResultsButtons.add(buttonClear, null);
        scrollpaneCompileResults.getViewport().add(textareaCompilerResults, null);
    }

    @Override
    public void clear() {
        textareaCompilerResults.setText("");
        textareaCompilerResults.paintImmediately(textareaCompilerResults.getBounds());
    }

    @Override
    public void setResults(String text, JTextComponent textComponent) {
        this.textComponent = textComponent;
        textareaCompilerResults.setText(text);
        textareaCompilerResults.paintImmediately(textareaCompilerResults.getBounds());
    }

    @Override
    public void focus() {
        // throw an event here, so that what ever container this panel is placed
        // into, it should show this panel as the focus.
        fireRequestingFocus();
    }

    /**
     * gets called when the user wants to clear the list of messages.
     */
    class ActionClear extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -6567327509221938385L;

        public ActionClear() {
            super("Clear Compiler Results");
            setEnabled(true);
            putValue("MENU", "Build");
            // F9 + SHIFT
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(120, InputEvent.SHIFT_DOWN_MASK, false));
            
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            textareaCompilerResults.setText("");
        }
    }

    /**
     */
    class ActionFocus extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -246285605237070855L;

        public ActionFocus() {
            super("Compiler Results", Icons.iconCompilePanel);
            setEnabled(true);
            putValue("MENU", "Tools");
            // F9 + CTRL
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(120, InputEvent.CTRL_DOWN_MASK, false));
            // putValue(Action.MNEMONIC_KEY, new Character('S'));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            focus();
        }
    }

    /**
     * listens for mouse double clicks on the compiler results. determines if
     * the user clicked on a line that has text that describes a compiler error,
     * if so then goes to that line in the source and highlights the error.
     */
    class MouseAdapterCompilerResults extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            String text;
            int rowStart;
            int colStart;
            int rowEnd;
            int colEnd;
            int position;
            int start;
            int end;
            StyledDocument document;
            SimpleAttributeSet attributes;
            StringTokenizer tokenizer;

            if (evt.getClickCount() > 1) {
                // get the text for the line the user clicked on.
                text = textareaCompilerResults.getText();
                position = textareaCompilerResults.getSelectionStart();
                start = text.lastIndexOf("\n", position);
                if (start == -1) {
                    start = 0;
                } else {
                    start++;
                }
                end = text.indexOf("\n", position);
                if (end == -1) {
                    end = text.length();
                }
                text = text.substring(start, end);
                // set the attributes for the entire document in order to remove
                // any
                // previous selections attributes that were set.
                attributes = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attributes, "DialogInput");
                StyleConstants.setFontSize(attributes, 14);
                StyleConstants.setBold(attributes, false);
                StyleConstants.setForeground(attributes, new Color(0, 0, 0));
                StyleConstants.setBackground(attributes, new Color(255, 255, 255));
                document = (StyledDocument) textareaCompilerResults.getDocument();
                document.setCharacterAttributes(0, document.getLength(), attributes, true);
                // highlight the line the user clicked on to show them what
                // error is being displayed.
                StyleConstants.setForeground(attributes, new Color(128, 0, 0));
                StyleConstants.setBackground(attributes, new Color(225, 225, 225));
                StyleConstants.setBold(attributes, true);
                document.setCharacterAttributes(start, end - start, attributes, true);
                if (textComponent == null) {
                    return;
                }
                // get the line start, position start, line end, and position
                // end of the code
                // that is in error.
                tokenizer = new StringTokenizer(text, " ");
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                // "ERROR" text.
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                // the next 5 tokens are placed by the system, not sure what
                // they are.
                for (int x = 0; x < 4; x++) {
                    tokenizer.nextToken();
                    if (!tokenizer.hasMoreTokens()) {
                        return;
                    }
                }
                rowStart = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                colStart = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                rowEnd = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                colEnd = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
                select(textComponent, rowStart, colStart, rowEnd, colEnd);
            }
        }

        public void select(JTextComponent source, int rowStart, int colStart, int rowEnd, int colEnd) {
            Container container;
            Container child;
            int row;
            int start;
            int index;
            String text;

            child = source;
            container = textComponent.getParent();
            while (container != null) {
                if (container instanceof JTabbedPane) {
                    ((JTabbedPane) container).setSelectedComponent(child);
                }
                container.requestFocus();
                child = container;
                container = container.getParent();
            }
            source.select(0, 0);
            source.requestFocus();
            source.grabFocus();

            // select the proper text
            text = source.getText();
            row = 1;
            index = 0;
            while (row < rowStart && index > -1) {
                row++;
                index = text.indexOf("\n", index + 1);
            }
            // if index == -1 then the row wasnt found.
            if (index == -1) {
                return;
            }
            start = index + colStart;
            while (row < rowEnd) {
                row++;
                index = text.indexOf("\n", index + 1);
            }
            // if index == -1 then the row wasnt found.
            if (index == -1) {
                return;
            }
            source.select(start, index + colEnd + 1);
        }
    }
}
