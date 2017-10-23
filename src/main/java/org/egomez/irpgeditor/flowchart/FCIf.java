package org.egomez.irpgeditor.flowchart;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Derek Van Kooten
 */
public class FCIf extends FCShape {
  FCShape shapeTrue, shapeFalse;
  boolean finishedTrue = false;
  boolean finished = false;
  
  public FCIf(FCOp op, FCShape previous, FCShape container) {
    super(op, previous, container);
  }
  
  public FCShape appendOp(FCOp op) {
    if ( finished ) {
      if ( op.getOp().startsWith("WHEN") || 
           op.getOp().startsWith("ELSE") || 
           op.getOp().startsWith("END") ) {
        // must notify the container that an when, else or end was received.
        return container.containerProcess(op);
      }
      next = FCShape.construct(op, this, container);
      return next;
    }
    if ( !finishedTrue && shapeTrue == null ) {
      shapeTrue = FCShape.construct(op, null, this);
      return shapeTrue;
    }
    else {
      shapeFalse = FCShape.construct(op, null, this);
      return shapeFalse;
    }
  }
  
  protected FCShape containerProcess(FCOp op) {
    if ( op.getOp().equals("ELSE") ) {
      finishedTrue = true;
      return this;
    }
    else if ( op.getOp().equals("END") ||
              op.getOp().equals("ENDIF") ) {
      finished = true;
      return this;
    }
    throw new RuntimeException("Invalid source structure: " + op.getOp() + ", " + op.getStart());
  }
  
  protected void calculateWidth(Graphics graphics) {
    if ( shapeFalse != null ) {
      width = shapeFalse.getSeriesWidth(graphics);
      center = width + (DEFAULT_HORIZONTAL_SPACING / 2);
      if ( shapeTrue != null ) {
        width += shapeTrue.getSeriesWidth(graphics);
      }
      else {
        width += (DEFAULT_WIDTH / 2);
      }
    }
    else if ( shapeTrue != null ) {
      width = DEFAULT_WIDTH / 2;
      center = width + (DEFAULT_HORIZONTAL_SPACING / 2);
      width += shapeTrue.getSeriesWidth(graphics);
    }
    else {
      width = DEFAULT_WIDTH;
      center = width / 2;
      return;
    }
    width += DEFAULT_HORIZONTAL_SPACING;
  }
  
  public void calculateHeight(Graphics graphics) {
    int h, htemp;
    FontMetrics fm;
    
    fm = graphics.getFontMetrics();
    height = (fm.getHeight() * 4) + (2 * DEFAULT_TEXT_VERTICAL_SPACING) + DEFAULT_VERTICAL_SPACING;
    h = 0;
    if ( shapeTrue != null ) {
      h = shapeTrue.getTotalHeight(graphics);
    }
    if ( shapeFalse != null ) {
      htemp = shapeFalse.getTotalHeight(graphics);
      if ( htemp > h ) {
        h = htemp;
      }
    }
    height += h;
    height += DEFAULT_VERTICAL_SPACING;
    height += 6;
    if ( next != null ) {
      height += DEFAULT_VERTICAL_SPACING;
    }
  }
  
  @SuppressWarnings("unused")
public void draw(Graphics graphics, int center, int y) {
    int x, w, height, bottom, ifbottom, middle, end, currentx, currenty, 
        currentcenter, truecenter, falsecenter, joiny;
    FontMetrics fm;
    Polygon poly;
    Shape clip;
    Area area;
    
    fm = graphics.getFontMetrics();
    height = (fm.getHeight() * 4) + (2 * DEFAULT_TEXT_VERTICAL_SPACING);
    middle = y + (height / 2);
    bottom = y + getHeight(graphics);
    x = center - this.center;
    currentcenter = x + this.center;
    currentx = currentcenter - (DEFAULT_WIDTH / 2);
    end = currentx + DEFAULT_WIDTH;
    ifbottom = y + height;
    joiny = bottom - 6;
    if ( next != null ) {
      joiny -= DEFAULT_VERTICAL_SPACING;
    }
    
    poly = new Polygon();
    // left point
    poly.addPoint(currentx, middle);
    // top point
    poly.addPoint(currentcenter, y);
    // right point
    poly.addPoint(end, middle);
    // bottom point
    poly.addPoint(currentcenter, ifbottom);
    
    // background
    graphics.setColor(COLOR_BACKGROUND);
    graphics.fillPolygon(poly);
    
    // draw text.
    clip = graphics.getClip();
    area = new Area(clip);
    area.intersect(new Area(poly));
    graphics.setClip(area);
    
    graphics.setColor(COLOR_TEXT);
    graphics.drawString(((FCOp)listOps.get(0)).getText(), currentx + DEFAULT_TEXT_HORIZONTAL_SPACING, y + ((height + fm.getAscent()) / 2));
    
    // top left line, top right line, bottom left line, bottom right line.
    graphics.setColor(COLOR_BORDER_LIGHT);
    graphics.drawLine(currentx, middle, currentcenter, y);
    graphics.drawLine(currentx + 1, middle, currentcenter, y + 1);
    graphics.drawLine(currentx + 2, middle, currentcenter, y + 2);
    graphics.drawLine(currentcenter, y, end, middle);
    graphics.drawLine(currentcenter, y + 1, end - 1, middle);
    graphics.drawLine(currentcenter, y + 2, end - 2, middle);
    graphics.drawLine(currentx + 5, middle, currentcenter, ifbottom - 5);
    graphics.drawLine(currentx + 6, middle, currentcenter, ifbottom - 6);
    graphics.drawLine(currentcenter, ifbottom - 5, end - 5, middle);
    graphics.drawLine(currentcenter, ifbottom - 6, end - 6, middle);
    graphics.setColor(COLOR_BORDER);
    graphics.drawLine(currentx + 3, middle, currentcenter, y + 3);
    graphics.drawLine(currentx + 4, middle, currentcenter, y + 4);
    graphics.drawLine(currentcenter, y + 3, end - 3, middle);
    graphics.drawLine(currentcenter, y + 4, end - 4, middle);
    graphics.drawLine(currentx + 3, middle, currentcenter, ifbottom - 3);
    graphics.drawLine(currentx + 4, middle, currentcenter, ifbottom - 4);
    graphics.drawLine(currentcenter, ifbottom - 3, end - 3, middle);
    graphics.drawLine(currentcenter, ifbottom - 4, end - 4, middle);
    graphics.setColor(COLOR_BORDER_DARK);
    graphics.drawLine(currentx + 5, middle, currentcenter, y + 5);
    graphics.drawLine(currentx + 6, middle, currentcenter, y + 6);
    graphics.drawLine(currentcenter, y + 5, end - 5, middle);
    graphics.drawLine(currentcenter, y + 6, end - 6, middle);
    graphics.drawLine(currentx, middle, currentcenter, ifbottom);
    graphics.drawLine(currentx + 1, middle, currentcenter, ifbottom - 1);
    graphics.drawLine(currentx + 2, middle, currentcenter, ifbottom - 2);
    graphics.drawLine(currentcenter, y + height, currentx + DEFAULT_WIDTH, middle);
    graphics.drawLine(currentcenter, ifbottom - 1, end - 1, middle);
    graphics.drawLine(currentcenter, ifbottom - 2, end - 2, middle);
    
    // connect lines.
    currenty = ifbottom;
    currenty += DEFAULT_VERTICAL_SPACING;
    
    graphics.setColor(COLOR_BORDER);
    
    // draw true and false sides.
    graphics.setClip(clip);
    graphics.setColor(Color.RED);
    if ( shapeFalse != null ) {
      falsecenter = x + shapeFalse.getSeriesCenter(graphics);
      graphics.fillRect(falsecenter, middle, currentx - falsecenter, 4);
      graphics.fillRect(falsecenter, middle, 4, currenty - middle);
      //w = shapeFalse.getWidth(graphics);
      
      //falsecenter = x + (w / 2);
//      shapeFalse.draw(graphics, x + (w / 2), currenty + DEFAULT_VERTICAL_SPACING);
      shapeFalse.draw(graphics, falsecenter, currenty);
      
      graphics.setColor(Color.RED);
      graphics.fillRect(falsecenter, joiny - DEFAULT_VERTICAL_SPACING, 4, DEFAULT_VERTICAL_SPACING);
    }
    else {
      // draw the line.
      falsecenter = currentx;
      graphics.fillRect(falsecenter, middle, 4, joiny - middle);
      w = DEFAULT_WIDTH / 2;
    }
    graphics.setColor(Color.GREEN);
    if ( shapeTrue != null ) {
      truecenter = currentcenter + shapeTrue.getSeriesCenter(graphics);
      graphics.fillRect(end, middle, truecenter - end, 4);
      graphics.fillRect(truecenter, middle, 4, currenty - middle);
      
      //currentx = x + w + DEFAULT_HORIZONTAL_SPACING;
      //w = shapeTrue.getWidth(graphics);
      // the left hand side of the true side.
      //truecenter = currentx + (w / 2);
//      shapeTrue.draw(graphics, currentx + (w / 2), currenty);
      shapeTrue.draw(graphics, truecenter, currenty);
      
      graphics.setColor(Color.GREEN);
      graphics.fillRect(truecenter, joiny - DEFAULT_VERTICAL_SPACING, 4, DEFAULT_VERTICAL_SPACING);
    }
    else {
      // draw the line.
      truecenter = currentx + DEFAULT_WIDTH;
      graphics.fillRect(truecenter, middle, 4, joiny - middle);
    }
    
    // connect the lines.
    graphics.setColor(COLOR_BORDER_LIGHT);
    graphics.fillRect(falsecenter, joiny, truecenter - falsecenter, 2);
    graphics.setColor(COLOR_BORDER);
    graphics.fillRect(falsecenter, joiny + 2, truecenter - falsecenter, 2);
    graphics.setColor(COLOR_BORDER_DARK);
    graphics.fillRect(falsecenter, joiny + 4, truecenter - falsecenter, 2);
    
    if ( next != null ) {
      next.draw(graphics, center, bottom);
    }
  }
}


