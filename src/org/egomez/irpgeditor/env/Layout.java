package org.egomez.irpgeditor.env;

import org.egomez.irpgeditor.*;

/**
 * @author Derek Van Kooten.
 */
public class Layout {
  public void open(LayoutRequest layout) {
    Environment.toolManager.open(layout);
  }
  
  public void close(LayoutRequest request, boolean cache) {
    Environment.toolManager.close(request, cache);
  }
  
  public void select(LayoutRequest layout) {
    Environment.toolManager.select(layout);
  }
}
