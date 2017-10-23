package org.egomez.irpgeditor.swing;

import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class HandlerActions implements ListenerActions {
	JToolBar toolbar;
	JMenuBar menubar;
	@SuppressWarnings("rawtypes")
	HashMap mapActions = new HashMap();

	public HandlerActions(JToolBar toolbar, JMenuBar menubar) {
		this.toolbar = toolbar;
		this.menubar = menubar;
		Environment.actions.addListener(this);
		actionsAdded(Environment.actions.getActions());
	}

	@SuppressWarnings("unchecked")
	public void actionsAdded(Action[] actions) {
		Action action;
		String menuName, name;
		ActionContainer container;
		JMenu menu;

		for (int x = 0; x < actions.length; x++) {
			action = actions[x];
			name = (String) action.getValue(Action.NAME);
			if (name == null) {
				// invalid action, skip it.
				continue;
			}
			menuName = (String) action.getValue("MENU");
			if (menuName == null) {
				// invalid action, skip it.
				continue;
			}
			container = (ActionContainer) mapActions.get(menuName + "|" + name);
			if (container == null) {
				container = new ActionContainer(action);
				menu = getMenuForName(menuName);
				menu.add(container.getJMenuItem());
				menu.revalidate();
				menu.repaint();
				// add a button only if there is an icon defined.
				if (container.getJButtion() != null) {
					toolbar.add(container.getJButtion());
					toolbar.revalidate();
					toolbar.repaint();
				}
				mapActions.put(menuName + "|" + name, container);
			} else {
				container.addAction(action);
			}
		}
	}

	public void actionsRemoved(Action[] actions) {
		Action action;
		String menuName, name;
		ActionContainer container;
		JMenu menu;

		for (int x = 0; x < actions.length; x++) {
			action = actions[x];
			name = (String) action.getValue(Action.NAME);
			if (name == null) {
				// invalid action, skip it.
				continue;
			}
			menuName = (String) action.getValue("MENU");
			if (menuName == null) {
				// invalid action, skip it.
				continue;
			}
			container = (ActionContainer) mapActions.get(menuName + "|" + name);
			if (container == null) {
				// couldnt find the action to remove it.
				continue;
			} else {
				if (container.removeAction(action)) {
					menu = getMenuForName(menuName);
					menu.remove(container.getJMenuItem());
					menu.revalidate();
					menu.repaint();
					// add a button only if there is an icon defined.
					if (container.getJButtion() != null) {
						toolbar.remove(container.getJButtion());
						toolbar.revalidate();
						toolbar.repaint();
					}
					mapActions.remove(menuName + "|" + name);
				}
			}
		}
	}

	private JMenu getMenuForName(String menuName) {
		JMenu menu;

		for (int m = 0; m < menubar.getMenuCount(); m++) {
			menu = menubar.getMenu(m);
			if (menu.getText().equalsIgnoreCase(menuName)) {
				return menu;
			}
		}
		menu = new JMenu(menuName);
		menubar.add(menu);
		return menu;
	}
}

@SuppressWarnings("serial")
class ActionContainer extends AbstractAction implements PropertyChangeListener {
	@SuppressWarnings("rawtypes")
	ArrayList listActions = new ArrayList();
	JButton button = null;
	JMenuItem menuItem;

	@SuppressWarnings("unchecked")
	public ActionContainer(Action action) {
		listActions.add(action);
		menuItem = new JMenuItem(this);
		if (action.getValue(Action.SMALL_ICON) != null) {
			button = new JButton(this);
			button.setText("");
			button.setToolTipText((String) action.getValue(Action.NAME));
			button.setMargin(new Insets(0, 0, 0, 0));
		}
		action.addPropertyChangeListener(this);
	}

	public void actionPerformed(ActionEvent evt) {
		((Action) listActions.get(0)).actionPerformed(evt);
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(listActions.get(0)) == false) {
			// if the property name is focus, then move this action to the top
			if (evt.getPropertyName().equals("FOCUS")) {
				listActions.remove(evt.getSource());
				listActions.add(0, evt.getSource());
				// see if the enabled state has changed.
				super.setEnabled(!isEnabled());
				super.setEnabled(isEnabled());
			}
			return;
		}
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	public Object getValue(String key) {
		return ((Action) listActions.get(0)).getValue(key);
	}

	public boolean isEnabled() {
		return ((Action) listActions.get(0)).isEnabled();
	}

	public void putValue(String key, Object value) {
		((Action) listActions.get(0)).putValue(key, value);
	}

	public void setEnabled(boolean b) {
		((Action) listActions.get(0)).setEnabled(b);
	}

	@SuppressWarnings("unchecked")
	public void addAction(Action action) {
		listActions.add(action);
		action.addPropertyChangeListener(this);
	}

	public boolean removeAction(Action action) {
		action.removePropertyChangeListener(this);
		listActions.remove(action);
		if (listActions.size() > 0) {
			super.setEnabled(!isEnabled());
			super.setEnabled(isEnabled());
			return false;
		}
		return true;
	}

	public JButton getJButtion() {
		return button;
	}

	public JMenuItem getJMenuItem() {
		return menuItem;
	}
}
