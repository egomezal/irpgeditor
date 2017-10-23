package org.egomez.irpgeditor.swing;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Van Kooten
 */
public class PanelProjects extends PanelTool implements ListenerProjects {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9076586743758404176L;

	ComboBoxModelProjects comboboxModelProjects = new ComboBoxModelProjects();
	Logger logger = LoggerFactory.getLogger(PanelProjects.class);
	ActionProjectNew actionProjectNew = new ActionProjectNew();
	ActionProjectOpen actionProjectOpen = new ActionProjectOpen();
	ActionProjectSelected actionProjectSelected = new ActionProjectSelected();
	ActionProjectClose actionProjectClose = new ActionProjectClose();
	ActionProjectSave actionProjectSave = new ActionProjectSave();
	ActionMemberNew actionMemberNew = new ActionMemberNew();
	ActionMemberOpen actionMemberOpen = new ActionMemberOpen();

	BorderLayout borderLayout1 = new BorderLayout();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	JComboBox comboboxProjects = new JComboBox(comboboxModelProjects);

	public PanelProjects() {
		try {
			super.actions = new Action[] { actionProjectNew, actionProjectOpen, actionProjectClose, actionProjectSave,
					actionMemberNew, actionMemberOpen };
			Environment.actions.addActions(actions);
			jbInit();
			Environment.projects.addListener(this);
			selected(Environment.projects.getSelected());
			comboboxProjects.addActionListener(actionProjectSelected);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		this.add(comboboxProjects, BorderLayout.CENTER);
		comboboxProjects.setFont(new java.awt.Font("DialogInput", 0, 14));
	}

	public void added(Project project, int index) {
	}

	public void removed(Project project, int index) {
	}

	public void selected(Project project) {
		if (project == null) {
			comboboxProjects.setSelectedIndex(-1);
			actionProjectClose.setEnabled(false);
			actionProjectSave.setEnabled(false);
			actionMemberNew.setEnabled(false);
			actionMemberOpen.setEnabled(false);
		} else {
			if (comboboxProjects.getSelectedItem().equals(project) == false) {
				comboboxProjects.setSelectedItem(project);
			}
			actionProjectClose.setEnabled(true);
			actionProjectSave.setEnabled(true);
			actionMemberNew.setEnabled(true);
			actionMemberOpen.setEnabled(true);
		}
	}

	/**
	 * gets called when the user selects a project from the combobox of
	 * projects.
	 */
	class ActionProjectSelected implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Project project;

			project = (Project) comboboxProjects.getSelectedItem();
			Environment.projects.select(project);
		}
	}

	/**
	 * saves the selected project.
	 */
	class ActionProjectSave extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4829152165984561347L;

		public ActionProjectSave() {
			super("Save Project", Icons.iconProjectSave);
			setEnabled(false);
			putValue("MENU", "File");
			// F6
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0,
			// false));
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}

		public void actionPerformed(ActionEvent evt) {
			Project project;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			try {
				project.save();
				JOptionPane.showMessageDialog(null, "Project save succesfull", "Project",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				// e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * gets called to close the currently selected project.
	 */
	class ActionProjectClose extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8814323482304386422L;

		public ActionProjectClose() {
			super("Close Project", Icons.iconProjectClose);
			setEnabled(false);
			putValue("MENU", "File");
			// F6
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0,
			// false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_J));
		}

		public void actionPerformed(ActionEvent evt) {
			Project project;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			Environment.projects.remove(project);
		}
	}

	/**
	 * gets called when the user wants to open a project/file.
	 */
	class ActionProjectOpen extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -59785096474263501L;

		public ActionProjectOpen() {
			super("Open Project", Icons.iconProjectOpen);
			setEnabled(true);
			putValue("MENU", "File");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK, false));
			// F6
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0,
			// false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		}

		public void actionPerformed(ActionEvent evt) {
			JFileChooser fc;
			File file;
			Project project;
			FileFilteriRPGProjects filter;

			fc = new JFileChooser();
			filter = new FileFilteriRPGProjects();
			fc.setFileFilter(filter);
			fc.setCurrentDirectory(Environment.fileOpenDefault);
			if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			file = fc.getSelectedFile();
			Environment.fileOpenDefault = file.getParentFile();
			try {
				project = Project.load(file.getName());
				Environment.projects.add(project);
				Environment.projects.select(project);
			} catch (IOException e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * gets called when the user wants a new project.
	 */
	class ActionProjectNew extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 573818411219704118L;

		public ActionProjectNew() {
			super("New Project", Icons.iconProjectNew);
			setEnabled(true);
			putValue("MENU", "File");
			// F6
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(117, 0,
			// false));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}

		public void actionPerformed(ActionEvent evt) {
			String name;
			Project project;

			name = JOptionPane.showInputDialog(null, "New Project Name", "New Project", JOptionPane.QUESTION_MESSAGE);
			if (name == null) {
				return;
			}
			project = new Project(name, "/" + name + ".prj");
			try {
				project.save();
				Environment.projects.add(project);
				Environment.projects.select(project);
			} catch (IOException e) {
				// e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * creates a new member.
	 */
	class ActionMemberNew extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7381676659289750204L;

		public ActionMemberNew() {
			super("New Member", Icons.iconMemberNewLibrary);
			setEnabled(false);
			putValue("MENU", "File");
			putValue("SEPARATOR", "true");
			// F6
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		}

		public void actionPerformed(ActionEvent evt) {
			DialogMemberNew.showDialog(null, null);
		}
	}

	class ActionMemberOpen extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2377857039716416571L;

		public ActionMemberOpen() {
			super("Open Member from Library", Icons.iconMemberOpenLibrary);
			setEnabled(false);
			putValue("MENU", "File");
			// F6
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK, false));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		}

		public void actionPerformed(ActionEvent evt) {
			DialogMemberOpen.showDialog(null, null);

		}
	}
}
