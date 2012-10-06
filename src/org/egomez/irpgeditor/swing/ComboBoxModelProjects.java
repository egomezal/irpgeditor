package org.egomez.irpgeditor.swing;

import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten
 */
public class ComboBoxModelProjects extends AbstractListModel implements javax.swing.ComboBoxModel, ListenerProjects {
  public ComboBoxModelProjects() {
    Environment.projects.addListener(this);
  }

  public Object getSelectedItem() {
    return Environment.projects.getSelected();
  }

  public void setSelectedItem(Object object) {
    Environment.projects.select((Project)object);
  }

  public Object getElementAt(int index) {
    return Environment.projects.get(index);
  }

  public int getSize() {
    return Environment.projects.getSize();
  }

  public void added(Project project, int index) {
    fireIntervalAdded(this, index, index);
  }

  public void removed(Project project, int index) {
    fireIntervalRemoved(this, index, index);
  }

  public void selected(Project project) {}
}
