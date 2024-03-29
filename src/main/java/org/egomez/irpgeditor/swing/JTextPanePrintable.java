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
import java.awt.print.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Derek Van Kooten.
 */
@SuppressWarnings("serial")
public class JTextPanePrintable extends JTextPane implements Printable {

    transient PageFormat pageFormat;
    transient Logger logger = LoggerFactory.getLogger(JTextPanePrintable.class);

    public JTextPanePrintable() {
        super();
        pageFormat = new PageFormat();
    }

    /**
     * The method @print@ must be implemented for @Printable@
     * interface.Parameters are supplied by system.
     *
     * @param g
     * @param pf
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.black);    //set default foreground color to black 
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        Dimension d = this.getSize();    //get size of document 
        double panelWidth = d.width;    //width in pixels 
        double panelHeight = d.height;   //height in pixels 
        double pageHeight = pf.getImageableHeight();   //height of printer page 
        double pageWidth = pf.getImageableWidth();    //width of printer page 
        double scale = pageWidth / panelWidth;
        int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);

        // Make sure not print empty pages 
        if (pageIndex >= totalNumPages) {
            return Printable.NO_SUCH_PAGE;
        }

        // Shift Graphic to line up with beginning of print-imageable region 
        g2.translate(pf.getImageableX(), pf.getImageableY());
        // Shift Graphic to line up with beginning of next page to print 
        g2.translate(0f, -pageIndex * pageHeight);
        // Scale the page so the width fits... 
        g2.scale(scale, scale);
        this.paint(g2);   //repaint the page for printing 
        return Printable.PAGE_EXISTS;
    }

    public void doPrintActions() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pageFormat = pj.pageDialog(pj.defaultPage());
        pj.setPrintable(this, pageFormat);
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
