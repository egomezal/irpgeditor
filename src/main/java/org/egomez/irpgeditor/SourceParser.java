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
import java.util.*;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * breaks the source up into lines.
 *
 * @author Derek Van Kooten.
 */
public class SourceParser implements DocumentListener, SourceLoader {

    Document document;
    private final StringBuffer sourceNew = new StringBuffer();
    SourceLine first, last;
    @SuppressWarnings("rawtypes")
    ArrayList listListeners = new ArrayList();
    @SuppressWarnings("rawtypes")
    ArrayList listListenersFocus = new ArrayList();
    @SuppressWarnings("rawtypes")
    ArrayList listListenersFlowChart = new ArrayList();
    @SuppressWarnings("rawtypes")
    ArrayList listListenersDirty = new ArrayList();
    @SuppressWarnings("rawtypes")
    ArrayList listDeleted = new ArrayList();
    boolean isDirty = false;
    Logger logger = LoggerFactory.getLogger(SourceParser.class);

    public SourceParser() {
    }

    @SuppressWarnings("unchecked")
    public void addListener(ListenerParser listener) {
        listListeners.add(listener);
    }

    public void removeListener(ListenerParser listener) {
        listListeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void addListenerSelection(ListenerParserSelection listener) {
        listListenersFocus.add(listener);
    }

    public void removeListenerSelection(ListenerParserSelection listener) {
        listListenersFocus.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void addListenerFlowChart(ListenerParserFlowChart listener) {
        listListenersFlowChart.add(listener);
    }

    public void removeListenerFlowChart(ListenerParserFlowChart listener) {
        listListenersFlowChart.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void addListenerDirty(ListenerParserDirty listener) {
        listListenersDirty.add(listener);
    }

    public void removeListenerDirty(ListenerParserDirty listener) {
        listListenersDirty.remove(listener);
    }

    public void watch(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setDirty(boolean dirty) {
        if (this.isDirty == dirty) {
            return;
        }
        this.isDirty = dirty;
        fireDirty();
    }

    public boolean isDirty() {
        return isDirty;
    }

    /**
     * gets called when the style is changed in the document.
     *
     * @param evt
     */
    @Override
    public void changedUpdate(DocumentEvent evt) {
    }

    /**
     * gets called when the document has text inserted.
     *
     * @param evt
     */
    @Override
    public void insertUpdate(DocumentEvent evt) {
        try {
            setDirty(true);
            sourceNew.insert(evt.getOffset(), document.getText(evt.getOffset(), evt.getLength()));
            added(evt.getOffset(), evt.getLength());
        } catch (BadLocationException e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * gets called when the document has text removed.
     *
     * @param evt
     */
    @Override
    public void removeUpdate(DocumentEvent evt) {
        try {
            setDirty(true);
            sourceNew.delete(evt.getOffset(), evt.getOffset() + evt.getLength());
            removedNew(evt.getOffset(), evt.getLength());
            // removed(evt.getOffset(), evt.getLength());
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public void showBytes(String string) {
        for (int x = 0; x < string.length(); x++) {
            System.out.print((int) string.charAt(x));
            System.out.print(" ");
        }
        System.out.print("\n");
    }

    /**
     * call this when text is added to the source file.
     * @param start
     * @param length
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void added(int start, int length) {
        String string;
        int begin, end, remainder;
        SourceLine line, lastx, parent;
        ArrayList listEvents;
        String crlf = "\n";

        listEvents = new ArrayList();
        // add it into the local source.
        // get the added text
        string = getText(start, start + length);
        // string = source.substring(start, start + length);
        // get the current line.
        line = getLine(start);
        // see if there is a \n added or not.
        end = string.indexOf(crlf);
        if (end == -1) {
            // added to current line only.
            if (line == null) {
                // no line was found because the content was added to the end or
                // this is the first time content is added.
                if (first == null) {
                    line = new SourceLine(this, start, length);
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    first = line;
                } else {
                    parent = first;
                    while (parent.getNext() != null) {
                        parent = parent.getNext();
                    }
                    // if the last line has a "\n" in it, then a new line must
                    // be created.
                    // otherwise, append the content to the last line.
                    if (!parent.getText().contains(crlf)) {
                        parent.length += length;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, parent));
                        parent.changed = true;
                        // needed to be set for the shuffle() at end of method.
                        line = parent;
                    } else {
                        line = new SourceLine(parent, length);
                        line.created = true;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    }
                }
            } else {
                line.length += length;
                listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, line));
                line.changed = true;
            }
        } else {
            if (line == null) {
                // no line was found because the content was added to the end,
                // or there is no content yet..
                if (first == null) {
                    line = new SourceLine(this, 0, end + 1);
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    first = line;
                } else {
                    line = first;
                    while (line.getNext() != null) {
                        line = line.getNext();
                    }
                    // if the last line has a "\n" in it, then create a new
                    // line.
                    if (!line.getText().contains(crlf)) {
                        line.length += (end + 1);
                        listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, line));
                        line.changed = true;
                    } else {
                        line = new SourceLine(line, end + 1);
                        line.created = true;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    }
                }
                parent = line;
                begin = end + 1;
                end = string.indexOf(crlf, begin);
                // get number of lines inserted.
                while (end > -1) {
                    line = new SourceLine(parent, (end - begin) + 1);
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    parent = line;
                    begin = end + 1;
                    end = string.indexOf(crlf, begin);
                }
                // any more left after the last \n?
                if (begin < string.length()) {
                    line = new SourceLine(parent, string.length() - begin);
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                }
            } else {
                // at this point, content has been added to a line. now
                // determine
                // if it was added to the very first part of the line, because
                // this determines whether the first line gets a changed event,
                // or an add
                // event.
                // if the start is the start of the line, then a line was added.
                if (start == line.start) {
                    lastx = line;
                    parent = line.parent;
                    if (parent == null) {
                        line = new SourceLine(this, start, end + 1);
                        line.setNext(first);
                        first = line;
                    } else {
                        line = new SourceLine(parent, end + 1);
                    }
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                    // get number of lines inserted.
                    begin = end + 1;
                    end = string.indexOf(crlf, begin);
                    while (end > -1) {
                        parent = line;
                        line = new SourceLine(parent, (end - begin) + 1);
                        line.created = true;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                        begin = end + 1;
                        end = string.indexOf(crlf, begin);
                    }
                    line.setNext(lastx);
                    lastx.parent = line;
                    lastx.length = (string.length() - begin) + lastx.length;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lastx));
                    lastx.changed = true;
                } else {
                    lastx = line.getNext();
                    // get the length of the line that the new text was added
                    // to.
                    remainder = (line.start + line.length) - start;
                    line.length = (start - line.start) + end + 1;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, line));
                    line.changed = true;
                    // get number of lines inserted.
                    begin = end + 1;
                    end = string.indexOf(crlf, begin);
                    while (end > -1) {
                        parent = line;
                        line = new SourceLine(parent, (end - begin) + 1);
                        line.created = true;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                        begin = end + 1;
                        end = string.indexOf(crlf, begin);
                    }
                    parent = line;
                    line = new SourceLine(parent, (remainder + string.length()) - begin);
                    line.setNext(lastx);
                    if (lastx != null) {
                        lastx.parent = line;
                    }
                    line.created = true;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, line));
                }
            }
        }
        // line.shuffle();
        if (first != null) {
            first.shuffle();
        }
        fireEvents(listEvents);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void removedNew(int start, int length) {
        SourceLine lineStart, lineEnd, line;
        ArrayList listEvents;

        listEvents = new ArrayList();
        lineStart = getLine(start);
        lineEnd = getLineOrEnd(start + length);

        if (lineStart != lineEnd) {
            // remove all the lines inbetween.
            line = lineStart.getNext();
            while (line != lineEnd) {
                listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                listDeleted.add(line);
                line.invalid = true;
                line = line.getNext();
            }

            // the end line is always removed in order for the two lines to be
            // combined.
            listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, lineEnd));
            listDeleted.add(lineEnd);
            lineEnd.invalid = true;
            line = lineEnd.getNext();

            // was both lines removed?
            if (start == lineStart.start && start + length == lineEnd.start + lineEnd.length) {
                // both lines were removed.
                listEvents.add(0, new SourceParserEvent(SourceParserEvent.REMOVED, lineStart));
                listDeleted.add(lineStart);
                lineStart.invalid = true;
                if (lineStart.parent == null) {
                    first = line;
                    lineStart = line;
                    if (line != null) {
                        line.start = 0;
                    }
                } else {
                    lineStart = lineStart.parent;
                    lineStart.setNext(line);
                    if (line != null) {
                        line.parent = lineStart;
                    }
                }
            } else {
                // start with whats remaining in the end line, if nothing is
                // remaining, it will be 0.
                lineStart.length = (lineEnd.start + lineEnd.length) - (start + length);
                // was the cr lf removed from the end line?
                if (start + length == lineEnd.start + lineEnd.length) {
                    // the one following the end line has to be combined with
                    // the start line
                    // so it has to be removed as well.
                    listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                    listDeleted.add(line);
                    line.invalid = true;
                    // add in the length for this line.
                    lineStart.length += line.length;
                    line = line.getNext();
                }

                // both lines were not removed, append end line to start line.
                lineStart.setNext(line);
                if (line != null) {
                    line.parent = lineStart;
                }
                lineStart.changed = true;
                lineStart.length += (start - lineStart.start);
                listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
            }
        } else { // else line start equals line end.
            // was all of the first line removed?
            if (start == lineStart.start && length >= lineStart.length) {
                listEvents.add(0, new SourceParserEvent(SourceParserEvent.REMOVED, lineStart));
                listDeleted.add(lineStart);
                lineStart.invalid = true;
                line = lineStart.getNext();
                if (lineStart.parent == null) {
                    first = line;
                    if (line != null) {
                        line.parent = null;
                        line.start = 0;
                    }
                    lineStart = first;
                } else {
                    lineStart.parent.setNext(line);
                    if (line != null) {
                        line.parent = lineStart.parent;
                    }
                    lineStart = lineStart.parent;
                }
            } else {
                // otherwise the first line was changed.
                // was the end of line removed from the line?
                if (start + length == lineStart.start + lineStart.length) {
                    // the end of the line was removed.
                    // remove the next line if there is one.
                    line = lineStart.getNext();
                    if (line != null) {
                        // add the length of the next line to the start line.
                        lineStart.length += line.length;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                        listDeleted.add(line);
                        line.invalid = true;
                        lineStart.setNext(line.getNext(), true);
                    }
                }
                lineStart.changed = true;
                listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
                lineStart.length -= length;
            }
        }

        // if ( lineStart != null ) {
        // lineStart.shuffle();
        // }
        if (first != null) {
            first.shuffle();
        }
        fireEvents(listEvents);
    }

    /**
     * call this when text is removed from the source file.
     * @param start
     * @param length
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void removed(int start, int length) {
        SourceLine lineStart, lineEnd, line;
        ArrayList listEvents;

        listEvents = new ArrayList();
        lineStart = getLine(start);
        if (length < lineStart.length) {
            //System.out.println("a");
            // changed a line.
            // removed from current line only.
            // was the \n removed from the line?
            if (start + length == lineStart.start + lineStart.length) {
                //System.out.println("b");
                // the \n was removed from this line.
                // the next line gets removed, and its content gets appended to
                // the start line.
                line = lineStart.getNext();
                if (line != null) {
                    //System.out.println("c");
                    listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                    listDeleted.add(line);
                    line.invalid = true;
                    lineStart.length += line.length;
                    line = line.getNext();
                    lineStart.setNext(line);
                    if (line != null) {
                        //System.out.println("d");
                        line.parent = lineStart;
                    }
                }
            }
            //System.out.println("e");
            lineStart.length -= length;
            listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
            lineStart.changed = true;
        } else if (length > lineStart.length) {
            //System.out.println("f");
            lineEnd = getLine(start + length);
            // was the first one removed completely?
            if (start == lineStart.start) {
                //System.out.println("g");
                // is all the content to the end removed.
                if (lineEnd == null) {
                    //System.out.println("h");
                    if (lineStart.parent == null) {
                        //System.out.println("i");
                        first = null;
                    } else {
                        //System.out.println("j");
                        lineStart.parent.setNext(null);
                    }
                    // let the listener know which lines were removed.
                    line = lineStart;
                    while (line != lineEnd) {
                        //System.out.println("k");
                        listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                        listDeleted.add(line);
                        line.invalid = true;
                        line = line.getNext();
                    }
                    // set it to null so that a shuffle is not done at the end
                    // on lines
                    // that dont exist anymore.
                    lineStart = null;
                    //System.out.println("l");
                } else {
                    //System.out.println("m");
                    int newlength = (lineEnd.start + lineEnd.length) - (start + length);
                    // let the listener know which lines were removed.
                    line = lineStart;
                    while (line != lineEnd) {
                        //System.out.println("n");
                        listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                        listDeleted.add(line);
                        line.invalid = true;
                        line = line.getNext();
                    }
                    lineEnd.parent = lineStart.parent;
                    if (lineEnd.parent == null) {
                        //System.out.println("o");
                        first = lineEnd;
                        // do this so that the shuffle is correct.
                        first.start = 0;
                        lineStart = lineEnd;
                    } else {
                        //System.out.println("p");
                        lineEnd.parent.setNext(lineEnd);
                        // do this so that the shuffle is correct.
                        lineStart = lineEnd.parent;
                    }
                    // set the new size for the end line.
                    if (lineEnd.length != newlength) {
                        //System.out.println("q");
                        lineEnd.length = newlength;
                        listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineEnd));
                        lineEnd.changed = true;
                    }
                }
            } else {
                //System.out.println("r");
                // removed a line plus.
                // the start line gets appended to always.
                // the start line gets the rest of the end line.
                // if all of the end line is removed, then the one
                // after the end line is used.
                if (lineEnd == null) {
                    //System.out.println("s");
                    line = lineStart.getNext();
                    lineStart.setNext(null);
                    lineStart.length = start - lineStart.start;
                    listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
                    lineStart.changed = true;
                    // let the listener know which lines were removed.
                    while (line != null) {
                        //System.out.println("t");
                        listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                        listDeleted.add(line);
                        line.invalid = true;
                        line = line.getNext();
                    }
                } else {
                    //System.out.println("u");
                    if (start + length == lineEnd.start + lineEnd.length) {
                        //System.out.println("v");
                        // if the end line is all removed, and is the last line
                        // then the start line becomes the end line and is
                        // shorter.
                        if (lineEnd.getNext() == null) {
                            //System.out.println("w");
                            lineStart.setNext(null);
                            lineStart.length = start - lineStart.length;
                            listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
                            lineStart.changed = true;
                            return;
                        }
                        // the line after the lineEnd is the lineEnd, since all
                        // of the
                        // previous one was removed.
                        lineEnd = lineEnd.getNext();
                    }
                    //System.out.println("x");
                    // let the listener know which lines were removed.
                    line = lineStart.getNext();
                    while (line != lineEnd.getNext()) {
                        //System.out.println("y");
                        listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, line));
                        listDeleted.add(line);
                        line.invalid = true;
                        line = line.getNext();
                    }
                    // append the remaining line end to the line start and
                    // adjust length.
                    line = lineEnd.getNext();
                    lineStart.setNext(line);
                    if (line != null) {
                        //System.out.println("z");
                        line.parent = lineStart;
                    }
                    lineStart.length = (start - lineStart.start)
                            + ((lineEnd.start + lineEnd.length) - (start + length));
                    listEvents.add(new SourceParserEvent(SourceParserEvent.CHANGED, lineStart));
                    lineStart.changed = true;
                }
            }
        } else {
            //System.out.println("aa");
            // removed a line.
            // removed from current line only.
            // get the current line.
            listEvents.add(new SourceParserEvent(SourceParserEvent.REMOVED, lineStart));
            listDeleted.add(lineStart);
            lineStart.invalid = true;
            line = lineStart.getNext();
            if (lineStart.parent == null) {
                //System.out.println("ab");
                first = line;
                if (line != null) {
                    //System.out.println("ac");
                    line.parent = null;
                    line.start = 0;
                }
            } else {
                //System.out.println("ad");
                lineStart.parent.setNext(line);
                if (line != null) {
                    //System.out.println("ae");
                    line.parent = lineStart.parent;
                }
                lineStart = lineStart.parent;
            }
        }
        if (lineStart != null) {
            //System.out.println("af");
            lineStart.shuffle();
        }
        fireEvents(listEvents);
    }

    public StringBuffer getText() {
        return sourceNew;
    }

    public String getText(int start, int end) {
        return sourceNew.substring(start, end);
    }

    public char charAt(int index) {
        return sourceNew.charAt(index);
    }

    public int length() {
        return sourceNew.length();
    }

    /**
     * this is needed so that the row and column position can be given.
     */
    /*
	 * public void parseLines() { RPGSourceLine parent, next; int index1,
	 * index2; ArrayList listEvents;
	 * 
	 * parent = null; first = null; // get the line lengths. index1 = 0; index2
	 * = sourceNew.indexOf("\n"); listEvents = new ArrayList(); while ( index2 >
	 * -1 ) { if ( first == null ) { first = new RPGSourceLine(this, index1,
	 * (index2 - index1) + 1); parent = first; } else { next = new
	 * RPGSourceLine(parent, (index2 - index1) + 1); parent = next; }
	 * listEvents.add(new RPGSourceParserEvent(RPGSourceParserEvent.ADDED,
	 * parent)); index1 = index2 + 1; index2 = sourceNew.indexOf("\n", index1);
	 * } fireEvents(listEvents); }
     */
    /**
     * gets called when a line is loaded from the as400. the source file is
     * being loaded one line at a time.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void lineLoaded(float number, int date, String line) {
        ArrayList listEvents;
        int index;

        listEvents = new ArrayList();
        line = "a" + line;
        line = line.trim() + "\n";
        line = line.substring(1);
        sourceNew.append(line);
        if (first == null) {
            first = new SourceLine(this, 0, line.length());
            index = 0;
            last = first;
        } else {
            index = last.lineIndex + 1;
            last = new SourceLine(last, line.length());
        }
        last.number = number;
        last.date = date;
        last.lineIndex = index;
        listEvents.add(new SourceParserEvent(SourceParserEvent.ADDED, last));
        fireEvents(listEvents);
    }

    public Point getPoint(int position) {
        int row;// , col;
        SourceLine line, lasty;

        row = 1;
        line = first;
        lasty = null;
        while (line != null) {
            if (position < line.start + line.length) {
                return new Point((position + 1) - line.start, row);
            }
            lasty = line;
            line = line.getNext();
            row++;
        }
        // if this is reached, then the position is after all content.
        // if there is no content, then this is the first row, first position.
        if (lasty == null) {
            return new Point(1, row);
        } else {
            // determine row and position based on if there is a CR LF at end of
            // last line.
            if (sourceNew.charAt((lasty.start + lasty.length) - 1) == '\n') {
                return new Point(1, row);
            } else {
                return new Point((position + 1) - lasty.start, row - 1);
            }
        }
    }

    public int getRow(int position) {
        int row;// , col;
        SourceLine line;

        row = 1;
        line = first;
        while (line != null) {
            if (position < line.start + line.length) {
                return row;
            }
            line = line.getNext();
            row++;
        }
        return row;
    }

    public int getLineCount() {
        SourceLine line;
        int count = 0;

        line = first;
        while (line != null) {
            count++;
            line = line.getNext();
        }
        return count;
    }

    public SourceLine getLineForRow(int row) {
        SourceLine line;

        line = first;
        while (line != null) {
            if (row == 1) {
                return line;
            }
            line = line.getNext();
            row--;
        }
        return line;
    }

    @SuppressWarnings("unused")
    public SourceLine getLineOrEnd(int position) {
        SourceLine line, lastx;

        line = first;
        lastx = first;
        while (line != null) {
            if (position <= line.start + line.length) {
                return line;
            }
            lastx = line;
            line = line.getNext();
        }
        if (line == null) {
            return lastx;
        }
        return line;
    }

    public SourceLine getLine(int position) {
        SourceLine line;

        line = first;
        while (line != null) {
            if (position < line.start + line.length) {
                return line;
            }
            line = line.getNext();
        }
        return line;
    }

    public SourceLine getFirst() {
        return first;
    }

    public SourceLine getLast() {
        SourceLine line;

        line = first;
        if (line == null) {
            return null;
        }
        while (line.next != null) {
            line = line.next;
        }
        return line;
    }

    @SuppressWarnings("rawtypes")
    protected void fireEvents(ArrayList listEvents) {
        ArrayList temp;

        if (listEvents.isEmpty()) {
            return;
        }
        temp = (ArrayList) listListeners.clone();
        for (int x = 0; x < temp.size(); x++) {
            ((ListenerParser) temp.get(x)).parserEvents(listEvents);
        }
    }

    @SuppressWarnings("unchecked")
    protected void fireRequestingFocus(SourceLine sourceLine) {
        ListenerParserSelection[] temp;

        temp = (ListenerParserSelection[]) listListenersFocus
                .toArray(ListenerParserSelection[]::new);
        for (ListenerParserSelection temp1 : temp) {
            temp1.requestingFocus(sourceLine);
        }
    }

    @SuppressWarnings("unchecked")
    protected void fireRequestingFocus(SourceBlock sourceBlock) {
        ListenerParserSelection[] temp;

        temp = (ListenerParserSelection[]) listListenersFocus
                .toArray(ListenerParserSelection[]::new);
        for (ListenerParserSelection temp1 : temp) {
            temp1.requestingFocus(sourceBlock);
        }
    }

    @SuppressWarnings("unchecked")
    protected void fireRequestingFlowChart(SourceBlock sourceBlock) {
        ListenerParserFlowChart[] temp;

        temp = (ListenerParserFlowChart[]) listListenersFocus
                .toArray(new ListenerParserFlowChart[listListenersFlowChart.size()]);
        for (ListenerParserFlowChart temp1 : temp) {
            temp1.requestingFlowChart(sourceBlock);
        }
    }

    @SuppressWarnings("unchecked")
    protected void fireDirty() {
        ListenerParserDirty[] temp;
        boolean d = isDirty;

        temp = (ListenerParserDirty[]) listListenersDirty.toArray(ListenerParserDirty[]::new);
        for (ListenerParserDirty temp1 : temp) {
            temp1.parserDirty(this, d);
        }
    }
}
