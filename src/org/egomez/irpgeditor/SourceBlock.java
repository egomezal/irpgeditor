package org.egomez.irpgeditor;

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
import javax.swing.text.*;

import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Derek Van Kooten.
 */
public class SourceBlock extends NodeAbstract {
	public SourceBlock parent, child, sibling;
	public SourceBlock original;
	public SourceLine lineStart, lineEnd;
	Logger logger = LoggerFactory.getLogger(SourceBlock.class);

	private SourceBlock() {
	}

	public SourceBlock(SourceLine lineStart, SourceLine lineEnd) {
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		if (lineStart != null) {
			lineStart.block = this;
		}
		if (lineEnd != null) {
			lineEnd.block = this;
		}
	}

	/**
	 * tests to see if a line is a member of this block.
	 * 
	 * @param line
	 *            RPGSourceLine
	 * @return boolean
	 */
	public boolean isMember(SourceLine lineTest) {
		SourceLine line;

		line = lineStart;
		while (line != null) {
			if (line.equals(lineTest)) {
				return true;
			}
			line = line.getNext();
		}
		return false;
	}

	public boolean isClosed() {
		if (lineStart == null || lineEnd == null) {
			return false;
		}
		return true;
	}

	/**
	 * returns the child for the object specified.
	 */
	public Object getChild(int index) {
		SourceBlock block;

		block = child;
		while (index > 0 && block != null) {
			block = block.sibling;
			index--;
		}
		return block;
	}

	/**
	 * return the child count for a given parent.
	 */
	public int getChildCount() {
		SourceBlock block;
		int count;

		block = child;
		count = 0;
		while (block != null) {
			count++;
			block = block.sibling;
		}
		return count;
	}

	public int getSiblingCount() {
		SourceBlock block;
		int count;

		count = 0;
		block = sibling;
		while (block != null) {
			count++;
			block = block.sibling;
		}
		return count;
	}

	/**
	 * return the index of the child.
	 */
	public int indexOfParent() {
		SourceBlock block;
		int index;

		if (parent == null) {
			return -1;
		}
		block = parent.child;
		index = 0;
		while (block != null) {
			if (block == this) {
				return index;
			}
			index++;
			block = block.sibling;
		}
		return -1;
	}

	/**
	 * return the index of the child.
	 */
	public int getIndexOfChild(Object child) {
		return ((SourceBlock) child).indexOfParent();
	}

	/**
	 * returns true if the object has no children.
	 */
	public boolean isLeaf() {
		if (child == null) {
			return true;
		}
		return false;
	}

	/**
	 * used to display in the tree model for the structure of the code.
	 * 
	 * @return Icon
	 */
	public Icon getIcon() {
		if (lineStart == null) {
			return null;
		}
		if (lineStart.type == SourceLine.TYPE_PROCEDURE) {
			return Icons.iconProcedure;
		} else if (lineStart.type == SourceLine.TYPE_SCREEN) {
			return Icons.iconScreen;
		} else if (lineStart.type == SourceLine.TYPE_SUBROUTINE) {
			return Icons.iconSubroutine;
		}
		return null;
	}

	/**
	 * used to display in the tree model for the structure of the code.
	 * 
	 * @return String
	 */
	public String getText() {
		/*
		 * if ( original != null ) { return toString() + ", original: " +
		 * original.toString(); } return toString();
		 */
		try {
			if (lineStart == null) {
				return "Line start: NULL";
			}
			if (lineStart.type == SourceLine.TYPE_PROCEDURE) {
				return lineStart.getProcedureName();
			} else if (lineStart.type == SourceLine.TYPE_SCREEN) {
				return lineStart.getScreenName();
			} else if (lineStart.type == SourceLine.TYPE_SUBROUTINE) {
				char spec = lineStart.getSpec();
				if (spec == 'C' || spec == 'c') {
					return lineStart.get(LinePosition.C_FACTOR_1);
				}
				return lineStart.getFreeFormSecond();
			}
			return "Type: " + lineStart.type + ", position: " + lineStart.position;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public void rightClick(Component invoker, int x, int y) {
		if (lineStart.type != SourceLine.TYPE_SUBROUTINE) {
			return;
		}

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuFileRemove = new JMenuItem();
		JMenuItem menuInline = new JMenuItem();
		JMenuItem menuFlowChart = new JMenuItem();

		menuFileRemove.setText("Remove");
		menuInline.setText("Inline");
		menuFlowChart.setText("FlowChart");

		popupMenu.add(menuFileRemove);
		popupMenu.add(menuInline);
		popupMenu.add(menuFlowChart);

		if (lineStart == null || lineEnd == null) {
			menuFlowChart.setEnabled(false);
		}

		menuInline.addActionListener(new ActionInline());
		menuFileRemove.addActionListener(new ActionRemove());
		menuFlowChart.addActionListener(new ActionFlowChart());

		popupMenu.show(invoker, x, y);
	}

	public Node getParent() {
		return parent;
	}

	public void selected() {
		requestFocus();
	}

	public void requestFocus() {
		if (lineStart != null) {
			lineStart.parser.fireRequestingFocus(this);
		} else if (lineEnd != null) {
			lineEnd.parser.fireRequestingFocus(this);
		}
	}

	/**
	 */
	class ActionFlowChart implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			if (lineStart == null || lineEnd == null) {
				return;
			}
			lineStart.parser.fireRequestingFlowChart(SourceBlock.this);
		}
	}

	/**
	 * removes the block of code from the editor.
	 */
	class ActionRemove implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			SourceParser sourceParser;
			Document doc;

			sourceParser = lineStart.parser;
			doc = sourceParser.getDocument();
			try {
				doc.remove(lineStart.start, (lineEnd.start + lineEnd.length) - lineStart.start);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	class ActionInline implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String text, name;
			int start, end;
			SourceLine line;
			SourceParser sourceParser;

			if (lineStart == null || lineEnd == null) {
				return;
			}
			start = lineStart.getNext().start;
			end = lineEnd.start - 1;
			if (start >= end) {
				return;
			}
			sourceParser = lineStart.parser;
			name = getName();
			text = sourceParser.getText(start, end);
			try {
				sourceParser.getDocument().remove(lineStart.start, (lineEnd.start + lineEnd.length) - lineStart.start);
				line = sourceParser.getFirst();
				// find the places to inline it.
				while (line != null) {
					// does this line get replaced?
					process(line, name, text);
					line = line.getNext();
				}
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		public void process(SourceLine line, String name, String text) {
			char spec;

			spec = line.getSpec();
			if (spec == 'C' || spec == 'c') {
				if (line.get(LinePosition.C_OPERATION).trim().equalsIgnoreCase("exsr") == false) {
					return;
				}
				if (line.get(LinePosition.C_FACTOR_2).trim().equalsIgnoreCase(name) == false) {
					return;
				}
			} else {
				if (line.getFreeFormFirst().trim().equalsIgnoreCase("exsr") == false) {
					return;
				}
				String buffer = line.getFreeFormSecond().trim();
				if (buffer.endsWith(";")) {
					buffer = buffer.substring(0, name.length() - 1);
				}
				if (buffer.equalsIgnoreCase(name) == false) {
					return;
				}
			}
			// this is a line to be replaced.
			line.setText(text + "\n");
		}

		public String getName() {
			String name;
			char spec;

			spec = lineStart.getSpec();
			// free form or c spec line?
			if (spec == 'C' || spec == 'c') {
				return lineStart.get(LinePosition.C_FACTOR_1).trim();
			} else {
				// free form sub start.
				name = lineStart.getFreeFormSecond();
				if (name.endsWith(";")) {
					name = name.substring(0, name.length() - 1);
				}
				return name;
			}
		}
	}

	public Object clone() {
		SourceBlock block;

		block = new SourceBlock();
		block.lineStart = lineStart;
		block.lineEnd = lineEnd;
		block.original = this;
		return block;
	}
}
