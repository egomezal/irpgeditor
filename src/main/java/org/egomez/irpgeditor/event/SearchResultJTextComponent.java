package org.egomez.irpgeditor.event;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * @author Derek Van Kooten
 */
public class SearchResultJTextComponent extends Result {

    JTextComponent source;
    String found;
    int position;
    String text;

    public SearchResultJTextComponent(JTextComponent source, String found, int position) {
        int start, stop;

        this.source = source;
        this.found = found;
        this.position = position;

        text = source.getText();
        start = text.lastIndexOf("\n", position);
        if (start == -1) {
            start = 0;
        } else {
            start++;
        }
        stop = text.indexOf("\n", position);
        if (stop == -1) {
            stop = text.length();
        } else {
            stop++;
        }
        text = text.substring(start, stop);
    }

    public void select() {
        Container container, child;

        child = source;
        container = source.getParent();
        while (container != null) {
            if (container instanceof JTabbedPane) {
                ((JTabbedPane) container).setSelectedComponent(child);
            }
            container.requestFocus();
            child = container;
            container = container.getParent();
        }
        source.select(position, position + found.length());
        source.requestFocus();
        source.grabFocus();
    }

    @Override
    public String toString() {
        return text;
    }
}
