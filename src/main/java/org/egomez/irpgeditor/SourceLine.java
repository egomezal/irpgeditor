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

import javax.swing.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * one line of the file.
 * 
 * @author Derek Van Kooten.
 */
public class SourceLine {
	SourceParser parser;
	float number = 0;
	int date = 0;
	int start, length;
	int lineIndex;
	public SourceBlock block;
	SourceLine parent = null;
	SourceLine next = null;
	// used by RPGLineParser to organize code blocks.
	public int type; // proc, sub, other block type....
	public int position; // start, end of block.
	// used when saving source to see if line needs to be saved.
	boolean changed = false;
	boolean created = false;
	boolean invalid = false; // is set to true when the line is deleted.

	public static int POSITION_START = 0;
	public static int POSITION_END = 1;

	public static int TYPE_NONE = 0;
	public static int TYPE_PROCEDURE = 1;
	public static int TYPE_SUBROUTINE = 2;
	public static int TYPE_SCREEN = 3;

	Logger logger = LoggerFactory.getLogger(SourceLine.class);

	public SourceLine(SourceParser parser, int start, int length) {
		this.parser = parser;
		this.start = start;
		this.length = length;
	}

	public SourceLine(SourceLine parent, int length) {
		this.parser = parent.parser;
		this.start = parent.start + parent.length;
		this.length = length;
		this.parent = parent;
		parent.setNext(this);
	}

	public SourceParser getSourceParser() {
		return parser;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean isCreated() {
		return created;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public int getDate() {
		return date;
	}

	public int getStart() {
		return start;
	}

	public String getText() {
		return parser.getText(start, start + length);
	}

	public String getText(int start, int length) {
		if (start > this.length) {
			return "";
		}
		if (start + length > this.length) {
			return "";
		}
		return parser.getText(this.start + start, this.start + start + length);
	}

	/**
	 * returns the spec type for this line.
	 */
	public char getSpec() {
		if (length < 5) {
			return ' ';
		}
		return parser.charAt(start + 5);
	}

	public String get(LinePosition linePosition) {
		return get(linePosition.start - 1, linePosition.end);
	}

	/**
	 * gets part of the line that the id represents.
	 * 
	 * @param id
	 *            int
	 * @return String
	 */
	public String get(int s, int e) {
		if (s > length) {
			return "";
		}
		if (e > length) {
			return parser.getText(start + s, start + length).trim();
		}
		return parser.getText(start + s, start + e).trim();
	}

	/**
	 * gets part of the line that the id represents.
	 * 
	 * @param id
	 *            int
	 * @return String
	 */
	public static String get(String text, int start, int length, int s, int e) {
		if (s > length) {
			return "";
		}
		if (e > length) {
			return text.substring(start + s, start + length).trim();
		}
		return text.substring(start + s, start + e).trim();
	}

	public static String get(String text, int start, int end, LinePosition linePosition) {
		return get(text, start, end - start, linePosition.start - 1, linePosition.end);
	}

	public int getLength() {
		return length;
	}

	public SourceLine getParent() {
		return parent;
	}

	public boolean isComment() {
		if (start + 6 >= parser.length()) {
			return false;
		}
		if (parser.charAt(start + 6) == '*') {
			return true;
		}
		return false;
	}

	public boolean isDirective() {
		if (start + 6 >= parser.length()) {
			return false;
		}
		if (parser.charAt(start + 6) == '/') {
			return true;
		}
		return false;
	}

	public boolean isSql() {
		if (start + 6 >= parser.length()) {
			return false;
		}
		if (parser.charAt(start + 6) == '+') {
			return true;
		}
		return false;
	}

	/**
	 * delets the text from the text pane.
	 */
	public void delete() {
		try {
			parser.getDocument().remove(start, length);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public char charAt(int index) {
		return parser.charAt(start + index);
	}

	public void setText(String text) {
		Document document;

		document = parser.getDocument();
		try {
			document.remove(start, length);
			document.insertString(start, text, null);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public boolean isBetween(SourceLine lineA, SourceLine lineB) {
		SourceLine line;

		line = lineA.getNext();
		while (line != lineB) {
			if (equals(line)) {
				return true;
			}
			line = line.getNext();
		}
		return false;
	}

	public boolean isBetween(SourceLine lineA, SourceLine lineB, boolean inclusive) {
		if (inclusive) {
			if (equals(lineA)) {
				return true;
			}
			if (equals(lineB)) {
				return true;
			}
		}
		return isBetween(lineA, lineB);
	}

	public boolean isSame(SourceLine line) {
		if (line == null) {
			return false;
		}
		return line.getText().equalsIgnoreCase(getText());
	}

	public static void formatText(StringBuffer buffer, LinePosition linePosition, int i) {
		formatText(buffer, linePosition, Integer.toString(i));
	}

	public static void formatText(StringBuffer buffer, LinePosition linePosition, String text) {
		int length, start, needed;

		start = linePosition.start - 1;
		length = linePosition.end - start;
		if (text.length() > length) {
			text = text.substring(0, length);
		}
		// determine if the buffer needs to be longer to accomadate the text.
		needed = (linePosition.end + 1) - buffer.length();
		for (int x = 0; x < needed; x++) {
			buffer.append(" ");
		}
		if (text.length() == length) {
			buffer.replace(start, linePosition.end, text);
		} else {
			// fill in remaining with blanks.
			// how much is left to fill in?
			length -= text.length();
			if (linePosition.align == LinePosition.ALIGN_LEFT) {
				// align left.
				buffer.replace(start, start + text.length(), text);
				start += text.length();
				while (length > 0) {
					buffer.replace(start, start + 1, " ");
					length--;
					start++;
				}
			} else {
				// align right.
				buffer.replace(start + length, linePosition.end, text);
				while (length > 0) {
					buffer.replace(start, start + 1, " ");
					length--;
					start++;
				}
			}
		}
	}

	public void appendText(String text) {
		Document document;

		document = parser.getDocument();
		try {
			document.insertString(start + (length - 1), text, null);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void setNext(SourceLine next, boolean makeParent) {
		this.next = next;
		if (makeParent && next != null) {
			next.parent = this;
		}
	}

	public void setNext(SourceLine next) {
		this.next = next;
	}

	public SourceLine getNext() {
		return next;
	}

	public void shuffle() {
		if (parent != null) {
			lineIndex = parent.lineIndex + 1;
			start = parent.start + parent.length;
		} else {
			lineIndex = 0;
		}
		if (next != null) {
			next.shuffle();
		}
	}

	public void requestFocus() {
		parser.fireRequestingFocus(this);
	}

	/**
	 * treats this line like a procedure, and gets the text that would be
	 * considered its name.
	 */
	public String getProcedureName() {
		return parser.getText(start + 6, start + 21).trim();
	}

	/**
	 * treats this line like a screen, and gets the text that would be
	 * considered its name.
	 */
	public String getScreenName() {
		if (length < 29) {
			return parser.getText(start + 18, start + length).trim();
		}
		return parser.getText(start + 18, start + 29).trim();
	}

	/**
	 * returns the first free form keyword.
	 */
	public String getFreeFormFirst() {
		String text;
		int index;

		text = getText().trim();
		index = text.indexOf(' ');
		if (index > -1) {
			text = text.substring(0, index);
		}
		if (text.endsWith(";")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	/**
	 * returns the second free form keyword.
	 */
	public String getFreeFormSecond() {
		String text;
		int index;

		text = getText().trim();
		index = text.indexOf(' ');
		if (index == -1) {
			return "";
		}
		text = text.substring(index + 1, text.length()).trim();
		index = text.indexOf(' ');
		if (index != -1) {
			text = text.substring(0, index);
		}
		if (text.endsWith(";")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	/**
	 * returns the second free form keyword.
	 */
	public static String getFreeFormSecond(String text, int start, int end) {
		text = text.substring(start, end).trim();
		start = text.indexOf(' ');
		if (start == -1) {
			return "";
		}
		text = text.substring(start + 1).trim();
		start = text.indexOf(' ');
		if (start != -1) {
			text = text.substring(0, start);
		}
		if (text.endsWith(";")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	/**
	 * returns the first free form keyword.
	 */
	public static String getFreeFormFirst(String text, int start, int end) {
		int index;

		text = text.substring(start, end).trim();
		index = text.indexOf(' ');
		if (index > -1) {
			text = text.substring(0, index);
		}
		if (text.endsWith(";")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	public String toString() {
		return "SourceLine: start: " + start + ", length: " + length;
	}
}
