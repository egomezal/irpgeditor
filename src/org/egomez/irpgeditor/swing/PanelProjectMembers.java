package org.egomez.irpgeditor.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.swing.*;
import org.egomez.irpgeditor.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class PanelProjectMembers extends PanelTool {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2554200723067021497L;

	TreeModelProject treeModelFiles = new TreeModelProject();
	Logger logger = LoggerFactory.getLogger(PanelProjectMembers.class);
	ActionMemberOpen actionMemberOpen = new ActionMemberOpen();

	ActionMemberRemove actionMemberRemove = new ActionMemberRemove();
	ActionMemberRefactorRPG4 actionMemberRefactorRPG4 = new ActionMemberRefactorRPG4();
	ActionMemberRefreshInfo actionMemberRefreshInfo = new ActionMemberRefreshInfo();
	ActionMemberRename actionMemberRename = new ActionMemberRename();
	ActionMemberDelete actionMemberDelete = new ActionMemberDelete();
	ActionMemberCopy actionMemberCopy = new ActionMemberCopy();

	HandlerRightClick handlerRightClick = new HandlerRightClick();
	MouseAdapterTreeProject mouseAdapterTreeProject = new MouseAdapterTreeProject();
	HandlerMemberSelected handlerMemberSelected = new HandlerMemberSelected();
	TreeCellRendererNode treeCellRendererNode = new TreeCellRendererNode();

	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane scrollpaneMembers = new JScrollPane();
	JTree treeMembers = new JTree(treeModelFiles);

	public PanelProjectMembers() {
		try {
			super.actions = new Action[] { actionMemberOpen };
			Environment.actions.addActions(actions);
			jbInit();
			treeMembers.addMouseListener(mouseAdapterTreeProject);
			treeMembers.addTreeSelectionListener(handlerMemberSelected);
			treeMembers.setCellRenderer(treeCellRendererNode);
			ToolTipManager.sharedInstance().registerComponent(treeMembers);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		this.add(scrollpaneMembers, BorderLayout.CENTER);
		scrollpaneMembers.getViewport().add(treeMembers, null);
		treeMembers.setFont(new java.awt.Font("DialogInput", 0, 14));
		treeMembers.setRootVisible(false);
		new TreeClickHandler(treeMembers, handlerRightClick);
	}

	/**
	 * listens for mouse double clicks on a member.
	 */
	class MouseAdapterTreeProject extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() > 1) {
				actionMemberOpen.actionPerformed(null);
			}
		}
	}

	/**
	 * listens for when the user selects a member from the tree in order to
	 * enable or disable buttons and menus.
	 */
	class HandlerMemberSelected implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath path;
			Object selected;

			path = treeMembers.getSelectionPath();
			if (path != null) {
				selected = path.getLastPathComponent();
				if (selected instanceof ProjectMember) {
					actionMemberOpen.setEnabled(true);
					return;
				}
			}
			actionMemberOpen.setEnabled(false);
		}
	}

	class HandlerRightClick implements ListenerRightClick {
		public boolean rightClick(Component source, int x, int y) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem menuRemove = new JMenuItem();
			JMenuItem menuRename = new JMenuItem();
			JMenuItem menuCopy = new JMenuItem();
			JMenuItem menuDelete = new JMenuItem();
			JMenuItem menuOpen = new JMenuItem();
			JMenuItem menuRefresh = new JMenuItem();
			JMenuItem menuRefactor = new JMenuItem();

			menuRemove.setText("Remove");
			menuRename.setText("Rename");
			menuCopy.setText("Copy");
			menuDelete.setText("Delete");
			menuOpen.setText("Open");
			menuRefresh.setText("Refresh");
			menuRefactor.setText("Refactor to RPGIV");

			// if more than one node is selected then rename is not valid.
			if (treeMembers.getSelectionCount() > 1) {
				menuRename.setEnabled(false);
			}
			// see if all are rpg type.
			if (!allRPG()) {
				menuRefactor.setEnabled(false);
			}

			popupMenu.add(menuOpen);
			popupMenu.add(menuRemove);
			popupMenu.add(menuRename);
			popupMenu.add(menuCopy);
			popupMenu.add(menuDelete);
			popupMenu.add(menuRefresh);
			popupMenu.add(menuRefactor);

			menuRemove.addActionListener(actionMemberRemove);
			menuRename.addActionListener(actionMemberRename);
			menuCopy.addActionListener(actionMemberCopy);
			menuDelete.addActionListener(actionMemberDelete);
			menuOpen.addActionListener(actionMemberOpen);
			menuRefresh.addActionListener(actionMemberRefreshInfo);
			menuRefactor.addActionListener(actionMemberRefactorRPG4);

			popupMenu.show(source, x, y);
			return true;
		}

		public boolean allRPG() {
			TreePath[] paths;
			TreePath path;
			Object object;

			paths = treeMembers.getSelectionPaths();
			if (paths == null || paths.length == 0) {
				return false;
			}
			for (int x = 0; x < paths.length; x++) {
				path = paths[x];
				object = path.getLastPathComponent();
				if (object instanceof ProjectMember == false) {
					return false;
				}
				if (((ProjectMember) object).getMember().getSourceType().equals(Member.SOURCE_TYPE_RPG) == false) {
					return false;
				}
			}
			return true;
		}
	}

	class ActionMemberRename implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Project project;
			Member member;
			AS400System system;
			String name;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			treePath = treePaths[0];
			object = treePath.getLastPathComponent();
			if ((object instanceof ProjectMember) == false) {
				return;
			}
			name = JOptionPane.showInputDialog(null, "New name for member?", "Rename", JOptionPane.QUESTION_MESSAGE);
			if (name == null || name.trim().length() == 0) {
				return;
			}
			if (name.trim().length() > 10) {
				JOptionPane.showMessageDialog(null, "Name must be no more than 10 characters.");
				return;
			}
			projectMember = (ProjectMember) object;
			member = projectMember.getMember();
			system = member.getSystem();
			try {
				member.setName(name);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	class ActionMemberDelete implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Project project;
			Member member;
			String name;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete Member(s)", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember)) {
					projectMember = (ProjectMember) object;
					project.removeMember(projectMember);
					member = projectMember.getMember();
					try {
						member.delete();
					} catch (Exception e) {
						//e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
		}
	}

	class ActionMemberCopy implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Member member;

			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember)) {
					projectMember = (ProjectMember) object;
					member = projectMember.getMember();
					Environment.addToCopyBuffer(member);
				}
			}
		}
	}

	/**
	 * gets called when a user wants to open and edit a member.
	 */
	class ActionMemberOpen extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5085849889390534108L;

		public ActionMemberOpen() {
			super("Open Member", Icons.iconMemberOpen);
			setEnabled(false);
			putValue("MENU", "File");
			// F5 + CTRL
			// putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(116,
			// KeyEvent.CTRL_MASK, false));
			// putValue(Action.MNEMONIC_KEY, new Character('S'));
		}

		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;

			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember) == false) {
					return;
				}
				projectMember = (ProjectMember) object;
				Environment.members.open(projectMember);
			}
		}
	}

	class ActionMemberRefactorRPG4 implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Project project;
			Member member;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember) == false) {
					continue;
				}
				projectMember = (ProjectMember) object;
				member = projectMember.getMember();
				try {
					member.convertRPG4();
				} catch (Exception e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
	}

	class ActionMemberRefreshInfo implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Project project;
			Member member;
			AS400System system;

			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember) == false) {
					continue;
				}
				projectMember = (ProjectMember) object;
				member = projectMember.getMember();
				member.getInfo();
				projectMember.determineIcon();
			}
		}
	}

	/**
	 * gets called when the user wants to remove a member from the project.
	 */
	class ActionMemberRemove implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath treePath;
			TreePath[] treePaths;
			Object object;
			ProjectMember projectMember;
			Project project;

			if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Remove Project Member(s)",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
				return;
			}
			project = (Project) Environment.projects.getSelected();
			if (project == null) {
				return;
			}
			treePaths = treeMembers.getSelectionPaths();
			if (treePaths == null || treePaths.length == 0) {
				return;
			}
			for (int x = 0; x < treePaths.length; x++) {
				treePath = treePaths[x];
				object = treePath.getLastPathComponent();
				if ((object instanceof ProjectMember) == false) {
					continue;
				}
				projectMember = (ProjectMember) object;
				project.removeMember(projectMember);
			}
		}
	}
}
