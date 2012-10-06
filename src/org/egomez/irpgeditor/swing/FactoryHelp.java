package org.egomez.irpgeditor.swing;

import org.egomez.irpgeditor.*;


/**
 * gets called when someone wants to see help
 * 
 * @author not attributable
 */
public class FactoryHelp implements FactoryPanelTool {
  PanelHelp panelHelp = new PanelHelp();
  
  public PanelTool construct(Object object) {
    HelpRequest request;

    try {
      request = (HelpRequest)object;
      panelHelp.setHelpRequest(request);
      return panelHelp;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
