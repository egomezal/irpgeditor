package org.egomez.irpgeditor.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;

/**
 * This panel is displayed on the right side of the source code. It displays a
 * date for each line of code. The date displayed is the date at which the line
 * was either created or last changed. If the line has been modified in the
 * source editor then the date will show up blue with the current date. If the
 * line is a new line of code then a number 0 will be displayed for the line in
 * the color green.
 * 
 * @author Derek Van Kooten.
 */
public class PanelChangedDate extends JPanel implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6115864237470299915L;
	SourceParser parser;
	int fontHeight;
	FontMetrics fm;
	int width;
	JScrollPane scrollPane;

	public PanelChangedDate() {
		super();
	}

	/**
	 * when you set the scrollpane this control will listen to change events
	 * that the scrollpane generates.
	 * 
	 * @param scrollPane
	 *            JScrollPane
	 */
	public void setScrollPane(JScrollPane scrollPane) {
		if (this.scrollPane != null) {
			scrollPane.getViewport().removeChangeListener(this);
		}
		this.scrollPane = scrollPane;
		if (this.scrollPane != null) {
			scrollPane.getViewport().addChangeListener(this);
		}
	}

	public void setRPGSourceParser(SourceParser parser) {
		this.parser = parser;
	}

	public void setSourceFont(Font font) {
		fm = getFontMetrics(font);
		width = fm.stringWidth("000000");
		fontHeight = fm.getMaxAscent() + fm.getDescent();
	}

	public void stateChanged(ChangeEvent evt) {
		repaint();
	}

	public void paintComponent(Graphics g) {
		String buffer;
		SourceLine line;
		int y, start, index, end;

		super.paintComponent(g);
		if (parser == null) {
			return;
		}
		start = scrollPane.getVerticalScrollBar().getValue() / fontHeight;
		end = scrollPane.getViewport().getHeight() / fontHeight;
		if (start > 0) {
			start -= 1;
		}
		end += start;
		end += 1;
		y = fontHeight * start;
		y += fontHeight;
		index = 0;
		line = parser.getFirst();
		while (line != null) {
			if (index > end) {
				return;
			}
			if (index >= start) {
				buffer = Integer.toString(line.getDate());
				if (line.isCreated()) {
					g.setColor(Color.green);
				} else if (line.isChanged()) {
					g.setColor(Color.blue);
				} else {
					g.setColor(Color.black);
				}
				g.drawString(buffer, (width - fm.stringWidth(buffer)) + 2, y - 2);
				y += fontHeight;
			}
			line = line.getNext();
			index++;
		}
	}
}
