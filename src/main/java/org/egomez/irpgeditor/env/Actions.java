package org.egomez.irpgeditor.env;

import java.util.*;
import javax.swing.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class Actions {

    @SuppressWarnings("rawtypes")
    ArrayList listListeners = new ArrayList();
    @SuppressWarnings("rawtypes")
    ArrayList listActions = new ArrayList();

    @SuppressWarnings("unchecked")
    public Action[] getActions() {
        return (Action[]) listActions.toArray(Action[]::new);
    }

    @SuppressWarnings("unchecked")
    public void addListener(ListenerActions l) {
        listListeners.add(l);
    }

    public void removeListener(ListenerActions l) {
        listListeners.remove(l);
    }

    @SuppressWarnings("unchecked")
    public void addActions(Action[] actions) {
        listActions.addAll(Arrays.asList(actions));
        fireActionsAdded(actions);
    }

    public void removeActions(Action[] actions) {
        for (Action action : actions) {
            listActions.remove(action);
        }
        fireActionsRemoved(actions);
    }

    protected void fireActionsAdded(Action[] actions) {
        Object[] temp;

        temp = listListeners.toArray();
        for (Object temp1 : temp) {
            ((ListenerActions) temp1).actionsAdded(actions);
        }
    }

    protected void fireActionsRemoved(Action[] actions) {
        Object[] temp;

        temp = listListeners.toArray();
        for (Object temp1 : temp) {
            ((ListenerActions) temp1).actionsRemoved(actions);
        }
    }
}
