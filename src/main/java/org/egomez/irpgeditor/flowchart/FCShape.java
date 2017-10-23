package org.egomez.irpgeditor.flowchart;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

import org.egomez.irpgeditor.*;

/**
 * @author Derek Van Kooten.
 */
public class FCShape {
  public static int DEFAULT_WIDTH = 100;
  public static int DEFAULT_VERTICAL_SPACING = 10;
  public static int DEFAULT_HORIZONTAL_SPACING = 10;
  public static int DEFAULT_TEXT_HORIZONTAL_SPACING = 8;
  public static int DEFAULT_TEXT_VERTICAL_SPACING = 8;
  public static Color COLOR_TEXT = new Color(0, 0, 0);
  public static Color COLOR_BACKGROUND = new Color(255, 255, 255);
  public static Color COLOR_BORDER_LIGHT = new Color(200, 200, 200);
  public static Color COLOR_BORDER = new Color(180, 180, 180);
  public static Color COLOR_BORDER_DARK = new Color(150, 150, 150);
  
  protected FCShape next, container, previous;
  @SuppressWarnings("rawtypes")
protected ArrayList listOps = new ArrayList();
  protected int height = 0;
  protected int width = 0;
  protected int center = 0;
  // if this shape is the first in a series of shapes, then the width
  // of the series as well as the center needs to be none.
  protected int seriesWidth = 0;
  protected int seriesCenter = 0;
  
  @SuppressWarnings("unchecked")
public FCShape(FCOp op, FCShape previous, FCShape container) {
    this.previous = previous;
    this.container = container;
    listOps.add(op);
  }
  
  @SuppressWarnings("unchecked")
public FCShape appendOp(FCOp op) {
    if ( op.getOp().equals("IF") ||
         op.getOp().startsWith("DOU") ||
         op.getOp().equals("SELECT") ) {
      next = construct(op, this, container);
      return next;
    }
    else if ( op.getOp().startsWith("WHEN") || 
              op.getOp().startsWith("ELSE") || 
              op.getOp().startsWith("END") ) {
      // must notify the container that an when, else or end was received.
      return container.containerProcess(op);
    }
    else {
      listOps.add(op);
    }
    return this;
  }

  public FCShape getNext() {
    return next;
  }

  public FCShape getPrevious() {
    return previous;
  }

  public FCShape getContainer() {
    return container;
  }
  
  protected FCShape containerProcess(FCOp op) {
    return this;
  }
  
  protected void calculateWidth(Graphics graphics) {
    width = DEFAULT_WIDTH;
    center = width / 2;
  }
  
  public int getWidth(Graphics graphics) {
    if ( width == 0 ) {
      calculateWidth(graphics);
    }
    return width;
  }
  
  public int getCenter(Graphics graphics) {
    if ( width == 0 ) {
      calculateWidth(graphics);
    }
    return center;
  }
  
  protected void calculateHeight(Graphics graphics) {
    FontMetrics fm;

    fm = graphics.getFontMetrics();
    height = (listOps.size() * fm.getHeight()) + (2 * DEFAULT_TEXT_VERTICAL_SPACING);
    if ( next != null ) {
      height += DEFAULT_VERTICAL_SPACING;
    }
  }
  
  public int getTotalHeight(Graphics graphics) {
    FCShape shape;
    int h = 0;
    
    shape = this;
    while ( shape != null ) {
      h += shape.getHeight(graphics);
      shape = shape.next;
    }
    return h;
  }
  
  /**
   * if there is a series of shapes in a container, the container
   * may call this method on the first shape to determine which shape is
   * the widest.
   * 
   * @param graphics Graphics
   * @return int
   */
  public int getSeriesWidth(Graphics graphics) {
    if ( seriesWidth == 0 ) {
      calculateSeriesWidth(graphics);
    }
    return seriesWidth;
  }
  
  public int getSeriesCenter(Graphics graphics) {
    if ( seriesWidth == 0 ) {
      calculateSeriesWidth(graphics);
    }
    return seriesCenter;
  }
  
  protected void calculateSeriesWidth(Graphics graphics) {
    FCShape shape;
    int wl, wr, widthLeft, widthRight, temp;
    
    widthLeft = 0;
    widthRight = 0;
    shape = this;
    while ( shape != null ) {
      temp = shape.getWidth(graphics);
      wl = shape.getCenter(graphics);
      wr = temp - wl;
      if ( wl > widthLeft ) {
        widthLeft = wl;
      }
      if ( wr > widthRight ) {
        widthRight = wr;
      }
      shape = shape.next;
    }
    seriesWidth = widthLeft + widthRight;
    seriesCenter = widthLeft;
  }
  
  public int getHeight(Graphics graphics) {
    if ( height == 0 ) {
      calculateHeight(graphics);
    }
    return height;
  }
  
  public void draw(Graphics graphics, int margin) {
    draw(graphics, getSeriesCenter(graphics) + margin, margin);
  }
  
  public void draw(Graphics graphics, int center, int y) {
    int x, currenty;
    int height, width;
    FCOp op;
    FontMetrics fm;
    Shape clip;
    Area area;
    
    fm = graphics.getFontMetrics();
    x = center - (getWidth(graphics) / 2);
    height = getHeight(graphics);
    width = getWidth(graphics);
    
    clip = graphics.getClip();
    area = new Area(clip);
    area.intersect(new Area(new Rectangle(x, y, width, height)));
    graphics.setClip(area);
    
    // background.
    graphics.setColor(COLOR_BACKGROUND);
    graphics.fillRect(x, y, width, height);
    
    // draw text.
    currenty = y + fm.getAscent() + DEFAULT_TEXT_VERTICAL_SPACING;
    graphics.setColor(COLOR_TEXT);
    for ( int j = 0; j < listOps.size(); j++ ) {
      op = (FCOp)listOps.get(j);
      graphics.drawString(op.getText(), x + DEFAULT_TEXT_HORIZONTAL_SPACING, currenty);
      currenty += fm.getHeight();
    }
    
    // lines are in this order, 
    // top, bottom, left, right border.
    graphics.setColor(COLOR_BORDER_LIGHT);
    graphics.fillRect(x, y, width, 2);
    graphics.fillRect(x + 4, y + height - 6, width - 8, 2);
    graphics.fillRect(x, y, 2, height);
    graphics.fillRect(x + width - 6, y + 4, 2, height - 8);
    graphics.setColor(COLOR_BORDER);
    graphics.fillRect(x + 2, y + 2, width - 4, 2);
    graphics.fillRect(x + 2, y + height - 4, width - 4, 2);
    graphics.fillRect(x + 2, y + 2, 2, height - 4);
    graphics.fillRect(x + width - 4, y + 2, 2, height - 4);
    graphics.setColor(COLOR_BORDER_DARK);
    graphics.fillRect(x + 4, y + 4, width - 8, 2);
    graphics.fillRect(x, y + height - 2, width, 2);
    graphics.fillRect(x + 4, y + 4, 2, height - 8);
    graphics.fillRect(x + width - 2, y, 2, height);
    
    graphics.setClip(clip);
    if ( next != null ) {
      y += height;
      // draw connection line.
      graphics.setColor(COLOR_BORDER_LIGHT);
      graphics.fillRect(center - 2, y, 2, DEFAULT_VERTICAL_SPACING);
      graphics.setColor(COLOR_BORDER);
      graphics.fillRect(center, y, 2, DEFAULT_VERTICAL_SPACING);
      graphics.setColor(COLOR_BORDER_DARK);
      graphics.fillRect(center + 2, y, 2, DEFAULT_VERTICAL_SPACING);
      
      y += DEFAULT_VERTICAL_SPACING;
      next.draw(graphics, center, y);
    }
  }
  
  public static FCShape construct(FCOp op, FCShape previous, FCShape container) {
    if ( op.getOp().equals("IF") ) {
      return new FCIf(op, previous, container);
    }
    else if ( op.getOp().startsWith("DOU") ) {
      return new FCDou(op, previous, container);
    }
    else if ( op.getOp().equals("SELECT") ) {
      return new FCSelect(op, previous, container);
    }
    else {
      return new FCShape(op, previous, container);
    }
  }
  
  public static FCShape process(String text, int start, int end) {
    FCShape first, current;
    int lineStart, lineEnd;
    String op;
    
    first = null;
    current = null;
    lineStart = start;
    lineEnd = text.indexOf("\n", start);
    if ( lineEnd > end || lineEnd == -1 ) {
      lineEnd = end;
    }
    while ( lineStart < end ) {
      // if is start of a container or end of a container, then
      op = getOp(text, lineStart, lineEnd);
      if ( op != null ) {
        if ( first == null ) {
          first = construct(new FCOp(op, text, lineStart, lineEnd), null, null);
          current = first;
        }
        else {
          current = current.appendOp(new FCOp(op, text, lineStart, lineEnd));
        }
      }
      // get the next line.
      lineStart = lineEnd + 1;
      lineEnd = text.indexOf("\n", lineStart);
      if ( lineEnd > end || lineEnd == -1 ) {
        lineEnd = end;
      }
    }
    return first;
  }
  
  private static String getOp(String text, int start, int end) {
    String op;
    
    // if too short, is a comment, a directive, or sql then return this block.
    if ( start + 6 >= end ) {
      return null;
    }
    if ( text.charAt(start + 6) == '*' ||
         text.charAt(start + 6) == '/' ||
         text.charAt(start + 6) == '+' ) {
      return null;
    }
    // is free form or not?
    if ( text.charAt(start + 5) == ' ' ) {
      // free form.
      op = SourceLine.getFreeFormFirst(text, start, end);
      if ( op.startsWith("//") ) {
        return null;
      }
    }
    else {
      // not free form.
      op = SourceLine.get(text, start, end, LinePosition.C_OPERATION);
    }
    if ( op == null || op.length() == 0 ) {
      return null;
    }
    if ( op.equalsIgnoreCase("BEGSR") ||
         op.equalsIgnoreCase("ENDSR") ) {
      return null;
    }
    return op.toUpperCase();
  }
}
