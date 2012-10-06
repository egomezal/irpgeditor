package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.*;

/**
 * Listens for when projects are added, or removed from the environment.
 * 
 * @author Derek Van Kooten.
 */
public interface ListenerProjects {
  public void added(Project project, int index);
  public void removed(Project project, int index);
  public void selected(Project project);
}
