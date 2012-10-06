package org.egomez.irpgeditor.swing;

import com.ibm.as400.access.*;

/**
 * @author not attributable
 */
public class FactorySpoolFiles implements FactoryPanelTool {
  public PanelTool construct(Object object) {
    PanelSpool panelSpool;
    SpooledFile file;

    try {
      file = (SpooledFile)object;
      panelSpool = new PanelSpool();
      panelSpool.setSpooledFile(file);
      return panelSpool;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
