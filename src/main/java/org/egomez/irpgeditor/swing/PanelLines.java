package org.egomez.irpgeditor.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This panel is displayed below the source code. It displays lines. The lines
 * connect statements like "IF" and "ENDIF", "SELECT", and "ENDSL" and so on.
 *
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unchecked")
public class PanelLines extends JPanel implements Runnable, DocumentListener {

    /**
     *
     */
    private static final long serialVersionUID = 8350288023940606465L;
    SourceParser parser;
    Block first;
    Thread threadScan, threadScanWait;
    long lastModified = 0;
    FontMetrics fm;
    int fontHeight;
    Color green = new Color(0, 125, 0);
    Color brown = new Color(125, 125, 0);
    Color purple = new Color(125, 0, 125);
    Logger logger = LoggerFactory.getLogger(PanelLines.class);

    @SuppressWarnings("rawtypes")
    public static HashSet setStart = new HashSet();

    static {
        setStart.add("if");
        setStart.add("begsr");
        setStart.add("dou");
        setStart.add("dow");
        setStart.add("select");
        setStart.add("do");
        setStart.add("ifne");
        setStart.add("downe");
        setStart.add("iflt");
        setStart.add("ifgt");
        setStart.add("ifeq");
        setStart.add("doweq");
        setStart.add("ifge");
        setStart.add("ifle");
        setStart.add("doueq");
        setStart.add("dowlt");
        setStart.add("dowle");
        setStart.add("for");
        setStart.add("monitor");
    }

    @SuppressWarnings("rawtypes")
    public static HashSet setStop = new HashSet();

    static {
        setStop.add("endsr");
        setStop.add("endfor");
        setStop.add("endif");
        setStop.add("enddo");
        setStop.add("endsl");
        setStop.add("end");
        setStop.add("endmon");
    }

    public void setParser(SourceParser sourceParser) {
        this.parser = sourceParser;
    }

    public void setSourceFont(Font font) {
        fm = getFontMetrics(font);
        fontHeight = fm.getMaxAscent() + fm.getDescent();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (first == null) {
            return;
        }
        drawBlock(g, first);
    }

    protected int drawBlock(Graphics g, Block b) {
        int yStart, yEnd, x, nextx;

        if (b.child != null) {
            // the child blocks will set the level.
            x = drawBlock(g, b.child) - 10;
            if ((b.xmax - 10) < x) {
                x = b.xmax - 10;
            }
        } else {
            b.level = 0;
            x = b.xmax - 10;
        }
        // update the parents level.
        if (b.parent != null) {
            if (b.level + 1 > b.parent.level) {
                b.parent.level = b.level + 1;
            }
        }
        // draw this one
        if (b.lineEnd != null) {
            yStart = getY(b.lineStart.getLineIndex());
            yEnd = getY(b.lineEnd.getLineIndex());
            if (b.startType != b.endType && b.endType != 6) {
                g.setColor(Color.red);
            } else if (b.level % 5 == 0) {
                g.setColor(purple);
            } else if (b.level % 4 == 0) {
                g.setColor(brown);
            } else if (b.level % 3 == 0) {
                g.setColor(green);
            } else if (b.level % 2 == 0) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.BLUE);
            }
            g.drawLine(b.xstart, yStart, x, yStart);
            g.drawLine(b.xend, yEnd, x, yEnd);
            g.drawLine(x, yStart, x, yEnd);
        }

        // draw the next one.
        if (b.next != null) {
            nextx = drawBlock(g, b.next);
            if (nextx < x) {
                x = nextx;
            }
        }
        return x;
    }

    protected int getY(int line) {
        return (fontHeight * line) + (fontHeight / 2) + 3;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changed();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changed();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    public void changed() {
        first = null;
        repaint();
        if (threadScan != null) {
            threadScan.interrupt();
        }
        lastModified = System.currentTimeMillis();
        if (threadScanWait == null) {
            threadScanWait = new Thread(this);
            threadScanWait.start();
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (true) {
            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
            }
            if (System.currentTimeMillis() >= (lastModified + 3000)) {
                threadScanWait = null;
                lastModified = 0;
                startParse();
                return;
            }
        }
    }

    public void startParse() {
        threadScan = new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                try {
                    parse();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.error(e.getMessage());
                }
                threadScan = null;
            }
        };
        threadScan.start();
    }

    public void parse() {
        SourceLine line;
        Block start, temp;
        String op, text;
        char spec;
        int x, index;

        x = 0;
        start = null;
        temp = null;
        line = parser.getFirst();
        while (line != null) {
            if (line.isComment() == false && line.isInvalid() == false && line.isDirective() == false
                    && line.isSql() == false) {
                spec = line.getSpec();
                switch (spec) {
                    case 'C':
                    case 'c':
                        op = line.get(LinePosition.C_OPERATION);
                        text = line.getText();
                        index = text.indexOf(op);
                        try {
                            x = fm.stringWidth(text.substring(0, index));
                        } catch (Exception e) {
                            //System.out.println(op);
                            //System.out.println(text);
                            logger.info(op);
                            logger.info(text);
                            //e.printStackTrace();
                            logger.error(e.getMessage());
                        }
                        break;
                    case ' ':
                        op = line.getFreeFormFirst();
                        text = line.getText();
                        index = text.indexOf(op);
                        x = fm.stringWidth(text.substring(0, index));
                        break;
                    default:
                        op = "";
                        // continue;
                        break;
                }
                if (op == null) {
                    return;
                }
                op = op.trim().toLowerCase();
                if (setStart.contains(op)) {
                    if (temp == null) {
                        temp = new Block(op, line, null);
                        start = temp;
                    } else if (temp.lineEnd == null) {
                        temp.child = new Block(op, line, temp);
                        temp = temp.child;
                    } else {
                        temp.next = new Block(op, line, temp.parent);
                        temp = temp.next;
                    }
                    temp.xstart = x;
                    temp.xmax = x;
                } else if (setStop.contains(op) && temp != null) {
                    if (temp.lineEnd != null) {
                        if (temp.parent != null) {
                            temp = temp.parent;
                            temp.lineEnd = line;
                            temp.endType = temp.determineEndType(op);
                            temp.xend = x;
                            if (x < temp.xmax) {
                                temp.xmax = x;
                            }
                        }
                    } else {
                        temp.lineEnd = line;
                        temp.endType = temp.determineEndType(op);
                        temp.xend = x;
                        if (x < temp.xmax) {
                            temp.xmax = x;
                        }
                    }
                }
            }
            line = line.getNext();
        }
        first = start;
        repaint();
    }

    final class Block {

        int startType, endType; // 0 = IF, 1 = SELECT, 2 = BEGSR, 3 = DO, 4 =
        // MONITOR, 5 = FOR, 6 = END
        int xmax; // max x value that a line can be drawn for this block.
        int xstart;
        int xend;
        SourceLine lineStart, lineEnd;
        Block parent, next, child;
        int level = 0;

        public Block(String op, SourceLine lineStart, Block parent) {
            this.lineStart = lineStart;
            this.parent = parent;
            this.startType = determineStartType(op);
        }

        public int determineStartType(String op) {
            if (op.startsWith("if")) {
                return 0;
            } else if (op.startsWith("select")) {
                return 1;
            } else if (op.startsWith("begsr")) {
                return 2;
            } else if (op.startsWith("do")) {
                return 3;
            } else if (op.startsWith("monitor")) {
                return 4;
            }
            return 5;
        }

        public int determineEndType(String op) {
            if (op.startsWith("endif")) {
                return 0;
            } else if (op.startsWith("endsl")) {
                return 1;
            } else if (op.startsWith("endsr")) {
                return 2;
            } else if (op.startsWith("enddo")) {
                return 3;
            } else if (op.startsWith("endmon")) {
                return 4;
            } else if (op.startsWith("endfor")) {
                return 5;
            }
            return 6;
        }
    }
}
