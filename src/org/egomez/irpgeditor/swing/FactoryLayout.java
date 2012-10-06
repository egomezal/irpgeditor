package org.egomez.irpgeditor.swing;

import org.egomez.irpgeditor.*;

/**
 * gets called when someone wants to see the layout of a file.
 * 
 * @author not attributable
 */
public class FactoryLayout implements FactoryPanelTool {
  public PanelTool construct(Object object) {
    PanelLayout panelLayout;
    LayoutRequest request;

    try {
      request = (LayoutRequest)object;
      panelLayout = new PanelLayout();
      panelLayout.setName(request.getParsedName().toUpperCase());
      panelLayout.setLayoutRequest(request);
      return panelLayout;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
