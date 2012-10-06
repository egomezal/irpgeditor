package org.egomez.irpgeditor.env;

import java.util.*;
import javax.swing.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class Actions {
  ArrayList listListeners = new ArrayList();
  ArrayList listActions = new ArrayList();
  
  public Action[] getActions() {
    return (Action[])listActions.toArray(new Action[listActions.size()]);
  }
  
  public void addListener(ListenerActions l) {
    listListeners.add(l);
  }
  
  public void removeListener(ListenerActions l) {
    listListeners.remove(l);
  }
  
  public void addActions(Action[] actions) {
    for ( int x = 0; x < actions.length; x++ ) {
      listActions.add(actions[x]);
    }
    fireActionsAdded(actions);
  }
  
  public void removeActions(Action[] actions) {
    for ( int x = 0; x < actions.length; x++ ) {
      listActions.remove(actions[x]);
    }
    fireActionsRemoved(actions);
  }
  
  protected void fireActionsAdded(Action[] actions) {
    Object[] temp;
    
    temp = listListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerActions)temp[x]).actionsAdded(actions);
    }
  }
  
  protected void fireActionsRemoved(Action[] actions) {
    Object[] temp;
    
    temp = listListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerActions)temp[x]).actionsRemoved(actions);
    }
  }
}
