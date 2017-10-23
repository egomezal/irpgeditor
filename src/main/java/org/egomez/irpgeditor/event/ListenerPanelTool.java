package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.swing.*;

/**
 * Listens to the paneltool for when actions are added or removed.
 * 
 * @author Derek Van Kooten.
 */
public interface ListenerPanelTool {
  public void requestingFocus(PanelTool panel);
}
