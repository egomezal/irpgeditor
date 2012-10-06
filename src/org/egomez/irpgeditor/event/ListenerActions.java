package org.egomez.irpgeditor.event;

import javax.swing.*;

import org.egomez.irpgeditor.swing.*;

/**
 * @author Derek Van Kooten
 */
public interface ListenerActions {
  public void actionsAdded(Action[] actions);
  public void actionsRemoved(Action[] actions);
}
