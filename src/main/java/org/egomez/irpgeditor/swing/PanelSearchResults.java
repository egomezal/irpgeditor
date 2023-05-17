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
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays search results.
 *
 * @author Derek Van Kooten.
 */

public class PanelSearchResults extends PanelTool implements SearchResultsOutput {

    /**
     *
     */
    private static final long serialVersionUID = -1483691745354763317L;

    @SuppressWarnings("rawtypes")
    ArrayList listResults = new ArrayList();

    ActionSearch actionSearch = new ActionSearch();
    ActionSearchFocus actionSearchFocus = new ActionSearchFocus();
    ActionSearchClear actionSearchClear = new ActionSearchClear();

    transient MouseAdapterSearchResults mouseAdapterSearchResults = new MouseAdapterSearchResults();

    JScrollPane scrollpaneSearchResults = new JScrollPane();
    BorderLayout borderLayoutSearchResults = new BorderLayout();
    JTextPane textareaSearchResults = new JTextPane();
    JPanel jPanel1 = new JPanel();
    JButton buttonClear = new JButton();
    FlowLayout flowLayout1 = new FlowLayout();
    transient Logger logger = LoggerFactory.getLogger(PanelSearchResults.class);

    public PanelSearchResults() {
        setName("Search Results");
        try {
            jbInit();
            super.actions = new Action[]{actionSearch, actionSearchFocus, actionSearchClear};
            Environment.actions.addActions(actions);
            textareaSearchResults.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
            textareaSearchResults.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    e.consume();
                }
            });
            textareaSearchResults.addMouseListener(mouseAdapterSearchResults);
            buttonClear.addActionListener(actionSearchClear);
        } catch (Exception e) {
            
            logger.error(e.getMessage());
        }
    }

    private void jbInit() throws Exception {
        setLayout(borderLayoutSearchResults);
        textareaSearchResults.setFont(new java.awt.Font("DialogInput", 0, 14));
        buttonClear.setMargin(new Insets(0, 0, 0, 0));
        buttonClear.setText("clear");
        jPanel1.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(0);
        flowLayout1.setVgap(0);
        add(scrollpaneSearchResults, BorderLayout.CENTER);
        this.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(buttonClear, null);
        scrollpaneSearchResults.setViewportView(textareaSearchResults);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void searchResultFound(Result result) {
        String num;

        listResults.add(result);
        try {
            if (listResults.size() == 1) {
                textareaSearchResults.setText("00001: " + result.toString());
            } else {
                num = Integer.toString(listResults.size() + 100000).substring(1);
                textareaSearchResults.getDocument().insertString(textareaSearchResults.getDocument().getLength(),
                        num + ": " + result.toString(), null);
            }
        } catch (BadLocationException e) {
            
            logger.error(e.getMessage());
        }
        textareaSearchResults.select(0, 0);
    }

    @Override
    public void clear() {
        textareaSearchResults.setText("");
        listResults.clear();
    }

    @Override
    public void focus() {
        // throw an event here, so that what ever container this panel is placed
        // into, it should show this panel as the focus.
        fireRequestingFocus();
    }

    /**
     * searches the active member for a string of text.
     */
    class ActionSearch extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 4871170781477163380L;

        public ActionSearch() {
            super("Search");
            setEnabled(true);
            putValue("MENU", "Edit");
            // F10
            putValue(Action.ACCELERATOR_KEY,
                    javax.swing.KeyStroke.getKeyStroke('F', java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
         
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Component component;
            JTextComponent text;
            String find;
            String upper;
            int position;

            component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (component == null) {
                return;
            }
            if (component instanceof JTextComponent == false) {
                return;
            }
            find = JOptionPane.showInputDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
                    "Text to search for", "Search", JOptionPane.QUESTION_MESSAGE);
            if (find == null || find.trim().length() == 0) {
                return;
            }
            Environment.searchResults.clear();
            Environment.searchResults.focus();
            find = find.toUpperCase();
            text = (JTextComponent) component;
            upper = text.getText().toUpperCase();
            position = upper.indexOf(find);
            while (position > -1) {
                Environment.searchResults.searchResultFound(new SearchResultJTextComponent(text, find, position));
                position = upper.indexOf(find, position + 1);
            }
        }
    }

    class ActionSearchClear extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 7382640408954563810L;

        public ActionSearchClear() {
            super("Clear Search Results");
            setEnabled(true);
            putValue("MENU", "Edit");
      
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.searchResults.clear();
        }
    }

    class ActionSearchFocus extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -8851180023786985812L;

        public ActionSearchFocus() {
            super("Search Results", Icons.iconSearch);
            setEnabled(true);
            putValue("MENU", "Tools");
           
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.searchResults.focus();
        }
    }

    /**
     * listens for mouse double clicks on the search results. determines if the
     * user clicked on a line that has text that describes a search result, if
     * so then goes to that line in the source and highlights the result.
     */
    class MouseAdapterSearchResults extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            String text;
            int position;
            int start;
            int end;
            int index;
            StyledDocument document;
            SimpleAttributeSet attributes;

            if (evt.getClickCount() < 2) {
                return;
            }
            // get the text for the line the user clicked on.
            text = textareaSearchResults.getText();
            position = textareaSearchResults.getSelectionStart();
            textareaSearchResults.select(position, position);
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
            if (start == -1) {
                start = 0;
            }
            text = text.substring(start, end);
            // set the attributes for the entire document in order to remove any
            // previous selections attributes that were set.
            attributes = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attributes, "DialogInput");
            StyleConstants.setFontSize(attributes, 14);
            StyleConstants.setBold(attributes, false);
            StyleConstants.setForeground(attributes, new Color(0, 0, 0));
            StyleConstants.setBackground(attributes, new Color(255, 255, 255));
            document = (StyledDocument) textareaSearchResults.getDocument();
            document.setCharacterAttributes(0, document.getLength(), attributes, true);
            // highlight the line the user clicked on to show them what error is
            // being displayed.
            StyleConstants.setForeground(attributes, new Color(128, 0, 0));
            StyleConstants.setBackground(attributes, new Color(225, 225, 225));
            StyleConstants.setBold(attributes, true);
            document.setCharacterAttributes(start, end - start, attributes, true);
            index = text.indexOf(":");
            if (index == -1) {
                return;
            }
            index = Integer.parseInt(text.substring(0, index));
            ((Result) listResults.get(index - 1)).select();
        }
    }
}
