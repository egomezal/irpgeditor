package org.egomez.irpgeditor.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;

/**
 * This panel is displayed on the left side of the source code. It displays line
 * numbers. It also displays a red square on the lines that have been marked
 * with break points. The user may mark a line with a break point by clicking
 * on this panel next to the line on which the break point should be.
 *  
 * @author Derek Van Kooten.
 */
public class PanelBreakPoints extends JPanel implements ChangeListener {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7237738665464327958L;
ProjectMember member;
  int fontHeight;
  FontMetrics fm;
  JScrollPane scrollPane;

  public PanelBreakPoints() {
    super();
  }

  public void setScrollPane(JScrollPane scrollPane) {
    if ( this.scrollPane != null ) {
      scrollPane.getViewport().removeChangeListener(this);
    }
    this.scrollPane = scrollPane;
    if ( this.scrollPane != null ) {
      scrollPane.getViewport().addChangeListener(this);
    }
  }

  public void setMember(ProjectMember member) {
    this.member = member;
  }

  public void setSourceFont(Font font) {
    fm = getFontMetrics(font);
    fontHeight = fm.getMaxAscent() + fm.getDescent();
  }

  public void stateChanged(ChangeEvent evt) {
    repaint();
  }

  @SuppressWarnings("rawtypes")
public void paintComponent(Graphics g) {
    ArrayList list;
    Integer i;
    int line, width;
    int y, start, end;
    String buffer;

    super.paintComponent(g);
    width = getWidth();
    start = scrollPane.getVerticalScrollBar().getValue() / fontHeight;
    end = scrollPane.getViewport().getHeight() / fontHeight;
    if ( start > 0 ) {
      start -= 1;
    }
    end += start;
    end += 1;
    y = fontHeight * start;
    y += fontHeight;
    list = member.getBreakPoints();
    for ( int x = 0; x < list.size(); x++ ) {
      i = (Integer)list.get(x);
      line = i.intValue();
      if ( line >= start && line <= end ) {
        g.setColor(Color.red);
        g.fill3DRect(2, (fontHeight * (line - 1)) + 5, width - 4, fontHeight - 2, true);
        g.setColor(Color.black);
        g.drawRect(2, (fontHeight * (line - 1)) + 5, width - 4, fontHeight - 2);
      }
    }
    g.setColor(Color.black);
    while ( start <= end ) {
      buffer = Integer.toString(start + 1);
      g.drawString(buffer, (width - fm.stringWidth(buffer)) - 2, y - 2);
      y+= fontHeight;
      start++;
    }
  }
}
