package org.egomez.irpgeditor.swing;

import java.util.*;
import javax.swing.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class PanelTool extends JPanel {
  /**
	 * 
	 */
	private static final long serialVersionUID = -4567933460014597096L;
PanelToolContainer panelToolContainer;
  @SuppressWarnings("rawtypes")
private final ArrayList listListeners = new ArrayList();
  protected Action[] actions = new Action[0];
  int i = 0;
  
  public void setContainer(PanelToolContainer panelToolContainer) {
    if ( this.panelToolContainer != null ) {
      this.panelToolContainer.remove(this);
    }
    this.panelToolContainer = panelToolContainer;
    if ( panelToolContainer == null ) {
      return;
    }
    panelToolContainer.add(this);
  }
  /*
  public PanelToolContainer getContainer() {
    return panelToolContainer;
  }*/
  
  public Action[] getActions() {
    return actions;
  }
  
  @SuppressWarnings("unchecked")
public void addListener(ListenerPanelTool l) {
    listListeners.add(l);
  }
  
  public void removeListener(ListenerPanelTool l) {
    listListeners.remove(l);
  }
  
  public void focus() {
    fireRequestingFocus();
  }
  
  protected void fireRequestingFocus() {
    Object[] temp;

    temp = listListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerPanelTool)temp[x]).requestingFocus(this);
    }
  }
  
  public void focusActions() {
    i++;
    for ( int x = 0; x < actions.length; x++ ) {
      ((AbstractAction)actions[x]).putValue("FOCUS", Integer.toString(i));
    }
  }
  
  public void close() {
    Environment.actions.removeActions(actions);
  }
  
  public void dispose() {
  }
  
  public void selected() {
  }
}
