package org.egomez.irpgeditor.swing;

import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten
 */
@SuppressWarnings("rawtypes")
public class ComboBoxModelProjects extends AbstractListModel implements javax.swing.ComboBoxModel, ListenerProjects {

    /**
     *
     */
    private static final long serialVersionUID = 3303495099645947565L;

    public ComboBoxModelProjects() {
        Environment.projects.addListener(this);
    }

    public Object getSelectedItem() {
        return Environment.projects.getSelected();
    }

    public void setSelectedItem(Object object) {
        Environment.projects.select((Project) object);
    }

    public Object getElementAt(int index) {
        return Environment.projects.get(index);
    }

    /**
     *
     * @return
     */
    @Override
    public int getSize() {
        return Environment.projects.getSize();
    }

    @Override
    public void added(Project project, int index) {
        fireIntervalAdded(this, index, index);
    }

    @Override
    public void removed(Project project, int index) {
        fireIntervalRemoved(this, index, index);
    }

    @Override
    public void selected(Project project) {
    }
}
