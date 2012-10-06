package org.egomez.irpgeditor.swing;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;

/**
 * @author Derek Van Kooten
 */
public class PanelProjects extends PanelTool implements ListenerProjects {
  ComboBoxModelProjects comboboxModelProjects = new ComboBoxModelProjects();
  
  ActionProjectNew actionProjectNew = new ActionProjectNew();
  ActionProjectOpen actionProjectOpen = new ActionProjectOpen();
  ActionProjectSelected actionProjectSelected = new ActionProjectSelected();
  ActionProjectClose actionProjectClose = new ActionProjectClose();
  ActionProjectSave actionProjectSave = new ActionProjectSave();
  ActionMemberNew actionMemberNew = new ActionMemberNew();
  
  BorderLayout borderLayout1 = new BorderLayout();
  JComboBox comboboxProjects = new JComboBox(comboboxModelProjects);
  
  public PanelProjects() {
    try {
      super.actions = new Action[] { actionProjectNew, actionProjectOpen, actionProjectClose, actionProjectSave, actionMemberNew };
      Environment.actions.addActions(actions);
      jbInit();
      Environment.projects.addListener(this);
      selected(Environment.projects.getSelected());
      comboboxProjects.addActionListener(actionProjectSelected);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.add(comboboxProjects, BorderLayout.CENTER);
    comboboxProjects.setFont(new java.awt.Font("DialogInput", 0, 14));
  }
  
  public void added(Project project, int index) {}
  public void removed(Project project, int index) {}
  
  public void selected(Project project) {
    if ( project == null ) {
      comboboxProjects.setSelectedIndex(-1);
      actionProjectClose.setEnabled(false);
      actionProjectSave.setEnabled(false);
      actionMemberNew.setEnabled(false);
    }
    else {
      if ( comboboxProjects.getSelectedItem().equals(project) == false ) {
        comboboxProjects.setSelectedItem(project);
      }
      actionProjectClose.setEnabled(true);
      actionProjectSave.setEnabled(true);
      actionMemberNew.setEnabled(true);
    }
  }

  /**
   * gets called when the user selects a project from the combobox of projects.
   */
  class ActionProjectSelected implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      Project project;

      project = (Project)comboboxProjects.getSelectedItem();
      Environment.projects.select(project);
    }
  }
  
  /**
   * saves the selected project.
   */
  class ActionProjectSave extends AbstractAction {
    public ActionProjectSave() {
      super("Save Project", Icons.iconProjectSave);
      setEnabled(false);
      putValue("MENU", "File");
      // F6
//      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
    }
    
    public void actionPerformed(ActionEvent evt) {
      Project project;

      project = (Project)Environment.projects.getSelected();
      if ( project == null ) {
        return;
      }
      try {
        project.save();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * gets called to close the currently selected project.
   */
  class ActionProjectClose extends AbstractAction {
    public ActionProjectClose() {
      super("Close Project", Icons.iconProjectClose);
      setEnabled(false);
      putValue("MENU", "File");
      // F6
//      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_J));
    }
    
    public void actionPerformed(ActionEvent evt) {
      Project project;
      
      project = (Project)Environment.projects.getSelected();
      if ( project == null ) {
        return;
      }
      Environment.projects.remove(project);
    }
  }

  /**
   * gets called when the user wants to open a project/file.
   */
  class ActionProjectOpen extends AbstractAction {
    public ActionProjectOpen() {
      super("Open Project", Icons.iconProjectOpen);
      setEnabled(true);
      putValue("MENU", "File");
      // F6
//      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
//      putValue(Action.MNEMONIC_KEY, new Character('S'));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
    }
    
    public void actionPerformed(ActionEvent evt) {
      JFileChooser fc;
      File file;
      Project project;
      FileFilterJSEUProjects filter;

      fc = new JFileChooser();
      filter = new FileFilterJSEUProjects();
      fc.setFileFilter(filter);
      fc.setCurrentDirectory(Environment.fileOpenDefault);
      if ( fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION ) {
        return;
      }
      file = fc.getSelectedFile();
      Environment.fileOpenDefault = file.getParentFile();
      try {
        project = Project.load(file.getAbsolutePath());
        Environment.projects.add(project);
        Environment.projects.select(project);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * gets called when the user wants a new project.
   */
  class ActionProjectNew extends AbstractAction {
    public ActionProjectNew() {
      super("New Project", Icons.iconProjectNew);
      setEnabled(true);
      putValue("MENU", "File");
      // F6
//      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0, false));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
    }
    
    public void actionPerformed(ActionEvent evt) {
      String name;
      Project project;

      name = JOptionPane.showInputDialog(null, "New Project Name", "New Project", JOptionPane.QUESTION_MESSAGE);
      if ( name == null ){
        return;
      }
      project = new Project(name, "/" + name + ".prj");
      try {
        project.save();
        Environment.projects.add(project);
        Environment.projects.select(project);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * creates a new member.
   */
  class ActionMemberNew extends AbstractAction {
    public ActionMemberNew() {
      super("New Member", Icons.iconMemberNew);
      setEnabled(false);
      putValue("MENU", "File");
      // F6
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK, false));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
    }
    
    public void actionPerformed(ActionEvent evt) {
      Member member;
      Project project;
      ProjectMember projectMember;
      
      member = DialogMemberNew.showDialog(null);
      if ( member == null ) {
        return;
      }
      
      // add it to the project if the project doesnt already exist.
      project = (Project)Environment.projects.getSelected();
      if ( project == null ) {
        return;
      }
      projectMember = project.addMember(member);
      Environment.members.open(projectMember);
      Environment.members.select(projectMember);
    }
  }
}
