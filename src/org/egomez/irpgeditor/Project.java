package org.egomez.irpgeditor;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

import java.io.*;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.tree.*;

/**
 * A group of source members. A list of run configurations.
 *
 * @author Derek van kooten.
 */
@SuppressWarnings("unused")
public class Project extends NodeAbstract {
	String name;
	String fileName;
	@SuppressWarnings("rawtypes")
	ArrayList listMembers = new ArrayList();
	@SuppressWarnings("rawtypes")
	ArrayList listRun = new ArrayList();
	@SuppressWarnings("rawtypes")
	ArrayList listListeners = new ArrayList();
	ProjectMember memberSelected;

	/**
	 * create a project with the specified name and specified file name to save
	 * the project to.
	 * 
	 * @param name
	 *            String The name of the project seen when displayed.
	 * @param fileName
	 *            String The name of the file that the project is saved to.
	 */
	public Project(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	/**
	 * Adds an object to a list of objects that will be notified of events that
	 * this object produces.
	 * 
	 * @param listener
	 *            ListenerProject
	 */
	@SuppressWarnings("unchecked")
	public void addListener(ListenerProject listener) {
		listListeners.add(listener);
	}

	/**
	 * Removes an object from the list of objects that will be notified of
	 * events that this object produces.
	 * 
	 * @param listener
	 *            ListenerProject
	 */
	public void removeListener(ListenerProject listener) {
		listListeners.remove(listener);
	}

	/**
	 * adds a source member to the project.
	 * 
	 * @param member
	 *            ProjectMember
	 */
	@SuppressWarnings("unchecked")
	public ProjectMember addMember(ProjectMember member) {
		int index;

		index = listMembers.indexOf(member);
		if (index > -1) {
			return (ProjectMember) listMembers.get(index);
		}
		listMembers.add(member);
		fireMemberAdded(member);
		return member;
	}

	public ProjectMember addMember(Member member) {
		return addMember(new ProjectMember(this, member));
	}

	/**
	 * removes a source member from the project.
	 * 
	 * @param member
	 *            ProjectMember
	 */
	public void removeMember(ProjectMember member) {
		if (listMembers.remove(member)) {
			fireMemberRemoved(member);
			Environment.members.close(member, false);
		}
	}

	/**
	 * gets called when a member is added to this project. notifies listeners
	 * that a member was added to the project.
	 * 
	 * @param member
	 *            Member
	 */
	@SuppressWarnings("rawtypes")
	protected void fireMemberAdded(ProjectMember member) {
		ArrayList temp;

		temp = (ArrayList) listListeners.clone();
		for (int x = 0; x < temp.size(); x++) {
			((ListenerProject) temp.get(x)).memberAdded(this, member);
		}
	}

	/**
	 * gets called when a member is removed from this project. notifies
	 * listeners that a member was removed from the project.
	 * 
	 * @param member
	 *            Member
	 */
	@SuppressWarnings("rawtypes")
	protected void fireMemberRemoved(ProjectMember member) {
		ArrayList temp;

		temp = (ArrayList) listListeners.clone();
		for (int x = 0; x < temp.size(); x++) {
			((ListenerProject) temp.get(x)).memberRemoved(this, member);
		}
	}

	public ProjectMember getProjectMemberSelected() {
		return memberSelected;
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * returns all the source members in the project.
	 * 
	 * @return ArrayList contains ProjectMember objects.
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getMembers() {
		return listMembers;
	}

	/**
	 * returns all the run configurations for this project.
	 * 
	 * @return ArrayList contains RunConfiguration objects.
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getRunConfigurations() {
		return listRun;
	}

	/**
	 * Adds a RunConfiguration to this project.
	 * 
	 * @param config
	 *            RunConfiguration
	 */
	@SuppressWarnings("unchecked")
	public void addRunConfiguration(RunConfiguration config) {
		listRun.add(config);
	}

	/**
	 * returns a ProjectMember that has a name the same as the one specified.
	 * 
	 * @param name
	 *            String
	 * @return ProjectMember
	 */
	public ProjectMember getMember(String name) {
		ProjectMember projectMember;

		for (int x = 0; x < listMembers.size(); x++) {
			projectMember = (ProjectMember) listMembers.get(x);
			if (projectMember.member.nameEquals(name)) {
				return projectMember;
			}
		}
		return null;
	}

	/**
	 * returns a ProjectMember that has a name the same as the one specified and
	 * is on the system specified.
	 * 
	 * @param name
	 *            String
	 * @param system
	 *            AS400System
	 * @return ProjectMember
	 */
	public ProjectMember getMember(String name, AS400System system) {
		ProjectMember projectMember;

		for (int x = 0; x < listMembers.size(); x++) {
			projectMember = (ProjectMember) listMembers.get(x);
			if (projectMember.member.nameEquals(name) && projectMember.member.as400system.name.equals(system.name)) {
				return projectMember;
			}
		}
		return null;
	}

	/**
	 * loads the settings for a project from the file specified and returns the
	 * project.
	 * 
	 * @param fileName
	 *            String
	 * @throws IOException
	 * @return Project
	 */
	@SuppressWarnings("unchecked")
	public static Project load(String fileName) throws IOException {
		Project project;
		FileInputStream fis;
		Properties props;
		int count, optionCount;
		ProjectMember projectMember;
		Member member;
		AS400System system;
		String systemName, library, file, memberName, compileType, sourceType, destinationLibrary;
		RunConfiguration run;

		fis = new FileInputStream(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
				+ "projects" + File.separator + fileName);
		props = new Properties();
		props.load(fis);
		fis.close();
		project = new Project(props.getProperty("name"), fileName);
		// Creamos el directorio
		File dirName = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
				+ "projects" + File.separator + project);
		if (!dirName.exists()) {
			dirName.mkdirs();
		}

		if (props.getProperty("member.count") == null) {
			return project;
		}

		count = Integer.parseInt(props.getProperty("run.count"));
		for (int x = 0; x < count; x++) {
			run = new RunConfiguration();
			run.name = props.getProperty("run." + x + ".name");
			run.program = props.getProperty("run." + x + ".program");
			run.debug = props.getProperty("run." + x + ".debug");
			run.parms = props.getProperty("run." + x + ".parms");
			run.libraries = props.getProperty("run." + x + ".libraries");
			project.listRun.add(run);
		}

		count = Integer.parseInt(props.getProperty("member.count"));
		for (int x = 0; x < count; x++) {
			systemName = props.getProperty("member." + x + ".system");
			system = getSystem(systemName);
			if (system == null) {
				continue;
			}
			library = props.getProperty("member." + x + ".library");
			file = props.getProperty("member." + x + ".file");
			memberName = props.getProperty("member." + x + ".name");
			sourceType = props.getProperty("member." + x + ".sourceType");
			member = new Member(system, library, file, memberName, sourceType, "", "", "");
			compileType = props.getProperty("member." + x + ".compileType");
			destinationLibrary = props.getProperty("member." + x + ".destinationLibrary");
			if (destinationLibrary == null) {
				destinationLibrary = library;
			}
			projectMember = new ProjectMember(project, member, compileType, destinationLibrary);
			optionCount = Integer.parseInt(props.getProperty("member." + x + ".optionCount"));
			for (int o = 0; o < optionCount; o++) {
				projectMember.addCompileOption(props.getProperty("member." + x + ".option." + o));
			}
			project.listMembers.add(projectMember);
		}
		return project;
	}

	/**
	 * saves the settings for this project to the file specified.
	 */
	public void save() throws IOException {
		FileOutputStream fos;
		Properties props;
		ProjectMember projectMember;
		Member member;
		RunConfiguration run;

		// Creamos los directorios
		File file = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator + "conf"
				+ File.separator + "systems.properties");
		if (file.exists() == false) {
			file = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor");
			file.mkdir();
			file = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator + "conf");
			file.mkdir();
			file = new File(
					System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator + "projects");
			file.mkdir();
		}

		// Creamos el directorio
		File dirName = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
				+ "projects" + File.separator + name);
		if (!dirName.exists()) {
			dirName.mkdirs();
		}
		
		fos = new FileOutputStream(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
				+ "projects" + File.separator + fileName);
		props = new Properties();
		props.setProperty("name", name);

		props.setProperty("run.count", Integer.toString(listRun.size()));
		for (int x = 0; x < listRun.size(); x++) {
			run = (RunConfiguration) listRun.get(x);
			props.setProperty("run." + x + ".name", run.name);
			props.setProperty("run." + x + ".program", run.program);
			props.setProperty("run." + x + ".debug", run.debug);
			props.setProperty("run." + x + ".libraries", run.libraries);
			props.setProperty("run." + x + ".parms", run.parms);
		}

		props.setProperty("member.count", Integer.toString(listMembers.size()));
		for (int x = 0; x < listMembers.size(); x++) {
			projectMember = (ProjectMember) listMembers.get(x);
			member = projectMember.member;
			props.setProperty("member." + x + ".library", member.library);
			props.setProperty("member." + x + ".file", member.file);
			props.setProperty("member." + x + ".name", member.member);
			props.setProperty("member." + x + ".sourceType", member.sourceType);
			props.setProperty("member." + x + ".system", member.as400system.name);
			props.setProperty("member." + x + ".compileType", projectMember.compileType);
			props.setProperty("member." + x + ".destinationLibrary", projectMember.destinationLibrary);
			props.setProperty("member." + x + ".optionCount", Integer.toString(projectMember.compileOptionCount()));
			for (int o = 0; o < projectMember.compileOptionCount(); o++) {
				props.setProperty("member." + x + ".option." + o, projectMember.getCompileOption(o));
			}
		}
		props.store(fos, "");
		fos.close();
		saveRepo(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
				+ "projects" + File.separator + fileName);
	}

	protected void saveRepo(String name) {
		String workingDirectory = System.getProperty("user.home") + File.separator + ".iRPGEditor";
		Repository repo;
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			repo = builder.readEnvironment().findGitDir(new File(workingDirectory)).build();
			Git git = new Git(repo);

			git.add().addFilepattern(name).call();
			@SuppressWarnings("unused")
			RevCommit rev = git.commit()
					.setAuthor("iRPGEditor", "iRPGEditor")
					.setMessage("Open "
							+ new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))
					.call();
			git.close();
		} catch (IOException e1) {
		
		} catch (Exception e1) {
			
		}
		
	}
	/**
	 * returns a system for the system name.
	 */
	@SuppressWarnings("rawtypes")
	private static AS400System getSystem(String systemName) {
		ArrayList list;
		AS400System system;

		list = Environment.systems.getSystems();
		for (int x = 0; x < list.size(); x++) {
			system = (AS400System) list.get(x);
			if (system.name.equals(systemName)) {
				return system;
			}
		}
		return null;
	}

	/**
	 * returns the child for the object specified.
	 */
	public Object getChild(int index) {
		return listMembers.get(index);
	}

	/**
	 * return the child count for a given parent.
	 */
	public int getChildCount() {
		return listMembers.size();
	}

	/**
	 * return the index of the child.
	 */
	public int getIndexOfChild(Object child) {
		for (int x = 0; x < listMembers.size(); x++) {
			if (listMembers.get(x) == child) {
				return x;
			}
		}
		return -1;
	}

	/**
	 * returns true if the object has no children.
	 */
	public boolean isLeaf() {
		if (listMembers.size() == 0) {
			return true;
		}
		return false;
	}

	public String getText() {
		return name;
	}

	public Node getParent() {
		return null;
	}

	public String toString() {
		return name;
	}
}
