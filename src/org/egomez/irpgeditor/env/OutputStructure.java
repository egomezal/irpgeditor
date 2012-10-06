package org.egomez.irpgeditor.env;

import java.util.*;
import javax.swing.tree.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten
 */
public interface OutputStructure {
  public void setStructure(TreeModel model, Enumeration expands, ListenerStructure listener);
  public void removeStructure(TreeModel model);
}
