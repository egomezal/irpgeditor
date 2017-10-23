package org.egomez.irpgeditor.swing;

import java.io.*;
import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Derek Van Kooten.
 */
public class JPrinter {
	final static Logger logger = LoggerFactory.getLogger(JPrinter.class);
	public static void doPrintActions(JComponent component) {
		PrinterJob pj;
		PageFormat pageFormat;

		pj = PrinterJob.getPrinterJob();
		pageFormat = pj.pageDialog(pj.defaultPage());
		if (pj.isCancelled()) {
			return;
		}
		pj.setPrintable(new Print(component), pageFormat);
		if (pj.printDialog()) {
			try {
				pj.print();
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}
}

class Print implements Printable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5686520811931792121L;
	JComponent component;

	public Print(JComponent component) {
		this.component = component;
	}

	/**
	 * The method @print@ must be implemented for @Printable@ interface.
	 * Parameters are supplied by system.
	 */
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.black); // set default foreground color to black
		RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);
		Dimension d = component.getSize(); // get size of document
		double panelWidth = d.width; // width in pixels
		double panelHeight = d.height; // height in pixels
		double pageHeight = pf.getImageableHeight(); // height of printer page
		double pageWidth = pf.getImageableWidth(); // width of printer page
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
		component.paint(g2); // repaint the page for printing
		return Printable.PAGE_EXISTS;
	}
}
