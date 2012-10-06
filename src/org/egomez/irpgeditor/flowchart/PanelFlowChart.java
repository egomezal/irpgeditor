package org.egomez.irpgeditor.flowchart;

import java.awt.*;
import javax.swing.*;

/**
 * @author not attributable
 */
public class PanelFlowChart extends JPanel {
  FCShape shape;
  
  public PanelFlowChart() {
    super();
  }
  
  public void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    if ( shape != null ) {
      shape.draw(graphics, 5);
    }
    else {
      graphics.drawString("No flowchart selected.", 10, graphics.getFontMetrics().getAscent());
    }
  }
  
  public void setFCShape(FCShape shape) {
    this.shape = shape;
    repaint();
//    this.invalidate();
//    this.doLayout();
    this.revalidate();
    this.validate();
  }
  
  public int getWidth() {
    if ( shape == null ) {
      return super.getWidth();
    }
    return shape.getSeriesWidth(getGraphics());
  }
  
  public int getHeight() {
    if ( shape == null ) {
      return super.getHeight();
    }
    return shape.getTotalHeight(getGraphics());
  }
  
  public Dimension getPreferredSize() {
    return new Dimension(getWidth(), getHeight());
  }
  
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
  
  /*public Dimension getMaximumSize() {
    return getPreferredSize();
  }*/
}

