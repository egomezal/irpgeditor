package org.egomez.irpgeditor.event;

import java.awt.*;

/**
 * @author not attributable
 */
public interface ListenerRightClick {
  // return true if the implementing class handled the event
  // and doesnt want the individual nodes to receive the event.
  public boolean rightClick(Component source, int x, int y);
}
