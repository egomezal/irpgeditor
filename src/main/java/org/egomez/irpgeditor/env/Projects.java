package org.egomez.irpgeditor.env;

import java.io.*;
import java.util.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps a list of projects.
 * 
 * @author Derek Van Kooten.
 */
public class Projects {
	final Logger logger = LoggerFactory.getLogger(Projects.class);
	@SuppressWarnings("rawtypes")
	ArrayList listProjects = new ArrayList();
	@SuppressWarnings("rawtypes")
	ArrayList listListeners = new ArrayList();
	Project selected;
	int count;

	public void loadSettings() {
		String buffer, selected;
		Project project;

		// load open projects.
		buffer = Environment.settings.getProperty("openprojectcount");
		if (buffer == null) {
			return;
		}
		selected = Environment.settings.getProperty("projectselected");
		count = Integer.parseInt(buffer);
		for (int x = 0; x < count; x++) {
			try {
				project = Project.load(Environment.settings.getProperty("openproject." + x));
				if (project.getName().equalsIgnoreCase(selected)) {
					select(project);
				}
				add(project);
			} catch (IOException e) {
				logger.error(e.getMessage());
				//e.printStackTrace();
			}
		}
	}

	public void saveSettings() {
		Project project;
		int count;

		count = listProjects.size();
		// save what projects are loaded, and what the selected project is.
		Environment.settings.setProperty("openprojectcount", Integer.toString(count));
		for (int x = 0; x < count; x++) {
			project = (Project) listProjects.get(x);
			Environment.settings.setProperty("openproject." + x, project.getFileName());
		}
		if (selected == null) {
			Environment.settings.setProperty("projectselected", "");
		} else {
			Environment.settings.setProperty("projectselected", selected.getName());
		}
	}

	@SuppressWarnings("unchecked")
	public void add(Project project) {
		listProjects.add(project);
		fireAdded(project, listProjects.indexOf(project));
	}

	public void remove(Project project) {
		int index;

		index = listProjects.indexOf(project);
		listProjects.remove(project);
		fireRemoved(project, index);
		if (this.selected.equals(project)) {
			select(null);
		}
	}

	public Project get(int index) {
		return (Project) listProjects.get(index);
	}

	public int getSize() {
		return listProjects.size();
	}

	@SuppressWarnings("rawtypes")
	public void select(Project project) {
		ProjectMember projectMember;

		if (project == null && selected == null) {
			return;
		}
		if (project != null && project.equals(this.selected)) {
			return;
		}
		this.selected = project;
		// only one project can be selected at a time, close other projects open
		// members.
		Environment.members.closeAll(true);
		// add members.
		if (project != null) {
			ArrayList list = project.getMembers();
			for (int x = 0; x < list.size(); x++) {
				projectMember = (ProjectMember) list.get(x);
				// was already opened?
				if (Environment.members.isCached(projectMember)) {
					Environment.members.open(projectMember);
				}
			}
			Environment.members.select(project.getProjectMemberSelected());
		}

		fireSelected(project);
	}

	public Project getSelected() {
		return selected;
	}

	@SuppressWarnings("unchecked")
	public void addListener(ListenerProjects l) {
		listListeners.add(l);
	}

	public void removeListener(ListenerProjects l) {
		listListeners.remove(l);
	}

	protected void fireAdded(Project project, int index) {
		Object[] temp;

		temp = listListeners.toArray();
		for (int x = 0; x < temp.length; x++) {
			((ListenerProjects) temp[x]).added(project, index);
		}
	}

	protected void fireRemoved(Project project, int index) {
		Object[] temp;

		temp = listListeners.toArray();
		for (int x = 0; x < temp.length; x++) {
			((ListenerProjects) temp[x]).removed(project, index);
		}
	}

	protected void fireSelected(Project project) {
		Object[] temp;

		temp = listListeners.toArray();
		for (int x = 0; x < temp.length; x++) {
			((ListenerProjects) temp[x]).selected(project);
		}
	}
}
