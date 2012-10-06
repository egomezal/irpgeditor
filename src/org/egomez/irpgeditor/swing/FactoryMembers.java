package org.egomez.irpgeditor.swing;

import org.egomez.irpgeditor.*;

/**
 * @author not attributable
 */
public class FactoryMembers implements FactoryPanelTool {
  public PanelTool construct(Object object) {
    PanelTool panelTool;
    ProjectMember projectMember;

    try {
      projectMember = (ProjectMember)object;
      panelTool = new PanelMember(projectMember);
      panelTool.setName(projectMember.getMember().getName());
      return panelTool;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
