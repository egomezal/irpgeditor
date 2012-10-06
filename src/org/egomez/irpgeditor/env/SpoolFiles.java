package org.egomez.irpgeditor.env;

import com.ibm.as400.access.*;

/**
 * @author Derek Van Kooten
 */
public class SpoolFiles {
  public void open(SpooledFile file) {
    Environment.toolManager.open(file);
  }
  
  public void select(SpooledFile file) {
    Environment.toolManager.select(file);
  }
}
