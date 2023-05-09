package org.egomez.irpgeditor.tree;

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

/**
 *
 * @author Derek Van Kooten.
 */
abstract public class NodeAbstract implements Node {

    public NodeAbstract() {
    }

    @Override
    abstract public Node getParent();

    /**
     * returns the child for the object specified.
     *
     * @param index
     * @return
     */
    @Override
    public Object getChild(int index) {
        return null;
    }

    /**
     * return the child count for a given parent.
     */
    @Override
    public int getChildCount() {
        return 0;
    }

    /**
     * return the index of the child.
     */
    @Override
    public int getIndexOfChild(Object child) {
        return -1;
    }

    /**
     * returns true if the object has no children.
     */
    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public String getToolTipText() {
        return getText();
    }

    public void expand() {
    }

    public void collapse() {
    }

    @Override
    public void selected() {
    }

    @Override
    public void rightClick(Component invoker, int x, int y) {
    }

    public void click(int count) {
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isCheckBox() {
        return false;
    }
}
