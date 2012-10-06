package org.egomez.irpgeditor.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class PanelToolContainer extends PanelTool implements ListenerPanelTool, ContainerListener, ChangeListener {
  ArrayList listPanels = new ArrayList();
  PanelTool panelSelected;
  boolean alwaysTabs = true;
  
  BorderLayout borderLayout = new BorderLayout();
  JTabbedPaneWithCloseIcons tabbedpane = new JTabbedPaneWithCloseIcons();
  
  public PanelToolContainer() {
    setLayout(borderLayout);
    tabbedpane.addContainerListener(this);
    tabbedpane.getModel().addChangeListener(this);
    if ( alwaysTabs ) {
      add(tabbedpane, BorderLayout.CENTER);
    }
  }
  
  public void setTabPlacement(int placement) {
    tabbedpane.setTabPlacement(placement);
  }
  
  public boolean contains(PanelTool panel) {
    return listPanels.contains(panel);
  }
  
  public int indexOf(PanelTool panel) {
    return listPanels.indexOf(panel);
  }
  
  public PanelTool[] getPanels() {
    return (PanelTool[])listPanels.toArray(new PanelTool[listPanels.size()]);
  }
  
  public PanelTool getSelectedPanel() {
    if ( alwaysTabs || listPanels.size() > 1 ) {
      return (PanelTool)tabbedpane.getSelectedComponent();
    }
    if ( listPanels.size() == 1 ) {
      return (PanelTool)getComponent(0);
    }
    return null;
  }
  
  public void add(PanelTool panelTool) {
    listPanels.add(panelTool);
    tabbedpane.removeContainerListener(this);
    if ( alwaysTabs || listPanels.size() > 2 ) {
      tabbedpane.add(panelTool,  panelTool.getName());
    }
    else if ( listPanels.size() == 1 ) {
      add(panelTool, BorderLayout.CENTER);
    }
    else {
      removeAll();
      add(tabbedpane, BorderLayout.CENTER);
      PanelTool panel = (PanelTool)listPanels.get(0);
      tabbedpane.add(panel,  panel.getName());
      tabbedpane.add(panelTool,  panelTool.getName());
    }
    tabbedpane.addContainerListener(this);
    revalidate();
    panelTool.addListener(this);
  }
  
  public void select(PanelTool panelTool) {
    if ( listPanels.size() < 2 ) {
      return;
    }
    tabbedpane.setSelectedComponent(panelTool);
    tabbedpane.setTitleAt(tabbedpane.getSelectedIndex(), panelTool.getName());
  }
  
  public void remove(PanelTool panelTool) {
    boolean isSelected;
    
    panelTool.removeListener(this);
    isSelected = panelTool.equals(getSelectedPanel());
    listPanels.remove(panelTool);
    panelTool.close();
    // removeAll method generates removed events that we dont want to catch right now
    // so stop listening here.
    tabbedpane.removeContainerListener(this);
    if ( alwaysTabs ) {
      tabbedpane.remove(panelTool);
    }
    else if ( listPanels.size() == 0 ) {
      removeAll();
    }
    else {
      if ( listPanels.size() == 1 ) {
        removeAll();
        add((PanelTool)listPanels.get(0), BorderLayout.CENTER);
      }
      else {
        tabbedpane.remove(panelTool);
      }
    }
    tabbedpane.addContainerListener(this);
    revalidate();
    if ( isSelected ) {
      // the selected panel was removed, call select on the newly selected panel
      stateChanged(null);
    }
  }
  
  public void requestingFocus(PanelTool panelTool) {
    if ( alwaysTabs || listPanels.size() > 1 ) {
      tabbedpane.setSelectedComponent(panelTool);
    }
    fireRequestingFocus();
  }
  
  public void componentRemoved(ContainerEvent e) {
    Component c;

    c = e.getChild();
    remove((PanelTool)c);
  }

  public void componentAdded(ContainerEvent e) {}
  
  /**
   * gets called when a tab is selected in the tabbed pane.
   */
  public void stateChanged(ChangeEvent evt) {
    PanelTool panel;
    
    panel = getSelectedPanel();
    if ( panel == null ) {
      return;
    }
    panel.focusActions();
    panel.selected();
  }
}





