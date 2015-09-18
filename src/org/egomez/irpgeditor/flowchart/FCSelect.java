package org.egomez.irpgeditor.flowchart;

import java.awt.*;

/**
 * @author Derek Van Kooten
 */
public class FCSelect extends FCShape {
  boolean finished = false;

  public FCSelect(FCOp op, FCShape previous, FCShape container) {
    super(op, previous, container);
  }

  @SuppressWarnings("unchecked")
public FCShape appendOp(FCOp op) {
    FCShape shape;
    
    if ( finished ) {
      next = FCShape.construct(op, this, container);
      return next;
    }
    else {
      shape = FCShape.construct(op, null, this);
      listOps.add(shape);
      return shape;
    }
  }
  
  @SuppressWarnings("unchecked")
protected FCShape containerProcess(FCOp op) {
    FCShape shape;
    
    if ( op.getOp().equals("WHEN") ) {
      shape = FCShape.construct(op, null, this);
      listOps.add(shape);
      return shape;
    }
    else if ( !op.getOp().equals("END") ) {
      throw new RuntimeException("Invalid source structure: " + op.getOp() + ", " + op.getStart());
    }
    finished = true;
    return this;
  }

  public int getWidth(Graphics graphics) {
    return 0;
  }

  public int getHeight() {
    return 0;
  }

  public void draw(Graphics graphics, int x, int y) {
  }
}

