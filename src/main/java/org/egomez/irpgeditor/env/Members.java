package org.egomez.irpgeditor.env;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.swing.*;

/**
 * @author Derek Van Kooten
 */
public class Members {

    public PanelTool open(ProjectMember projectMember) {
        if (Environment.toolManager == null) {
            return null;
        }
        return Environment.toolManager.open(projectMember);
    }

    public PanelTool open(Member member) {
        Project project;
        ProjectMember projectMember;

        project = Environment.projects.getSelected();
        if (project == null) {
            return null;
        }
        projectMember = project.addMember(member);
        return open(projectMember);
    }

    public boolean isCached(ProjectMember member) {
        if (Environment.toolManager == null) {
            return false;
        }
        return Environment.toolManager.isCached(member);
    }

    public void close(ProjectMember projectMember, boolean cache) {
        if (Environment.toolManager == null) {
            return;
        }
        if (projectMember.getMember().isOkToClose() == false) {
            return;
        }
        Environment.toolManager.close(projectMember, cache);
    }

    public void close(ProjectMember projectMember) {
        if (Environment.toolManager == null) {
            return;
        }
        Environment.toolManager.close(projectMember, true);
    }

    public void select(ProjectMember projectMember) {
        if (Environment.toolManager == null) {
            return;
        }
        Environment.toolManager.select(projectMember);
    }

    public ProjectMember getSelected() {
        if (Environment.toolManager == null) {
            return null;
        }
        return (ProjectMember) Environment.toolManager.getSelected(ProjectMember.class);
    }

    public void closeAll(boolean cache) {
        if (Environment.toolManager == null) {
            return;
        }
        Environment.toolManager.closeAll(ProjectMember.class, cache);
    }
}
