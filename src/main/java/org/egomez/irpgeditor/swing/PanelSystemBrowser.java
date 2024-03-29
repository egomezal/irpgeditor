package org.egomez.irpgeditor.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;
import org.egomez.irpgeditor.icons.*;
import org.egomez.irpgeditor.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Van Kooten
 */
public class PanelSystemBrowser extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -7588764188326113402L;
    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane scrollpaneSystemBrowser = new JScrollPane();
    TreeModelNode treeModel = new TreeModelNode();
    JTree treeSystemBrowser = new JTree();
    TreeCellRendererNode treeCellRendererNode = new TreeCellRendererNode();
    NodeSystems nodeSystems = new NodeSystems(treeModel);
    Logger logger = LoggerFactory.getLogger(PanelSystemBrowser.class);

    public PanelSystemBrowser() {
        try {
            jbInit();
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        this.add(scrollpaneSystemBrowser, BorderLayout.CENTER);
        scrollpaneSystemBrowser.getViewport().add(treeSystemBrowser, null);
        treeModel.setRoot(nodeSystems);
        treeSystemBrowser.setCellRenderer(treeCellRendererNode);
        treeSystemBrowser.setRootVisible(false);
        treeSystemBrowser.setModel(treeModel);
        treeSystemBrowser.addTreeExpansionListener(new TreeExpansionListenerNode());
        new TreeClickHandler(treeSystemBrowser);
        ToolTipManager.sharedInstance().registerComponent(treeSystemBrowser);
    }
}

final class NodeSystems extends NodeDefault implements ListenerAS400Systems {

    TreeModelNode treeModel;

    @SuppressWarnings("rawtypes")
    public NodeSystems(TreeModelNode treeModel) {
        this.treeModel = treeModel;
        Environment.systems.addListener(this);
        ArrayList list1 = Environment.systems.getSystems();
        for (int x = 0; x < list1.size(); x++) {
            addedSytem((AS400System) list1.get(x));
        }
    }

    @Override
    public void addedSytem(AS400System system) {
        add(new NodeAS400(system, this, treeModel));
        SwingUtilities.invokeLater(() -> {
            treeModel.structureChanged((NodeDefault) treeModel.getRoot());
        });
    }

    @Override
    public void removedSytem(AS400System system) {
        NodeAS400 node;

        for (int x = 0; x < list.size(); x++) {
            node = (NodeAS400) list.get(x);
            if (node.as400.equals(system)) {
                list.remove(node);
                SwingUtilities.invokeLater(() -> {
                    treeModel.structureChanged((NodeDefault) treeModel.getRoot());
                });
                return;
            }
        }
    }

    @Override
    public void defaultSytem(AS400System system) {
    }
}

class NodeAS400 extends NodeDefault implements ListenerAS400System {

    AS400System as400;
    TreeModelNode treeModel;
    NodeDefault nodeWait = new NodeDefault(this, "Retrieving libraries...");
    boolean hasExpanded = false;
    Logger logger = LoggerFactory.getLogger(NodeAS400.class);

    public NodeAS400(AS400System as400, Node parent, TreeModelNode treeModel) {
        this.parent = parent;
        this.as400 = as400;
        this.treeModel = treeModel;
        as400.addListener(this);
        add(nodeWait);
    }

    @Override
    public String getText() {
        if (as400.isConnected()) {
            return as400.getName() + " (Connected)";
        }
        return as400.getName() + " (Disconnected)";
    }

    @Override
    public Icon getIcon() {
        return Icons.iconSystem;
    }

    @Override
    public boolean isLeaf() {
        if (hasExpanded == false) {
            return false;
        }
        return super.isLeaf();
    }

    public void expand() {
        if (hasExpanded == false) {
            startRetrieveLibraries();
        }
        hasExpanded = true;
    }

    protected void startRetrieveLibraries() {
        new Thread() {
            @Override
            public void run() {
                retrieveLibraries();
            }
        }.start();
    }

    @SuppressWarnings("rawtypes")
    protected void retrieveLibraries() {
        ArrayList libs;

        try {
            if (!as400.isConnected()) {
                as400.connect();
            }

            libs = as400.getLibraries();
            for (int x = 0; x < libs.size(); x++) {
                add(new NodeLibrary(this, as400, (String) libs.get(x), treeModel));
            }
            list.remove(nodeWait);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            nodeWait.setText(e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            treeModel.structureChanged(NodeAS400.this);
        });
    }

    /**
     * Is called by the AS400System object when a connection is made.
     */
    @Override
    public void connected(AS400System system) {
        // fire a changed event.
    }

    /**
     * Is called by the AS400System object when a disconnect happens.
     */
    @Override
    public void disconnected(AS400System system) {
    }
}

class NodeLibrary extends NodeDefault {

    AS400System as400;
    TreeModelNode treeModel;
    boolean hasExpanded = false;
    boolean isRetrieving = false;
    NodeDefault nodeWait = new NodeDefault(this, "Retrieving files...");
    Logger logger = LoggerFactory.getLogger(NodeLibrary.class);

    public NodeLibrary(Node parent, AS400System as400, String library, TreeModelNode treeModel) {
        super(parent, library);
        this.as400 = as400;
        this.treeModel = treeModel;
        this.icon = Icons.iconLibrary;
        add(nodeWait);
    }

    @Override
    public String getToolTipText() {
        return as400.getName() + " - " + text;
    }

    @Override
    public void expand() {
        if (hasExpanded == false) {
            if (!as400.isConnected()) {
                try {
                    as400.connect();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            startRetrieveFiles();
        }
        hasExpanded = true;
    }

    protected void startRetrieveFiles() {
        new Thread() {
            @Override
            public void run() {
                retrieveFiles();
            }
        }.start();
    }

    @SuppressWarnings("rawtypes")
    protected void retrieveFiles() {
        ArrayList files;

        isRetrieving = true;
        try {
            if (!as400.isConnected()) {
                as400.connect();
            }

            files = as400.getFiles(text);
            for (int x = 0; x < files.size(); x += 3) {
                add(new NodeFile(this, as400, text, (String) files.get(x), (String) files.get(x + 1),
                        (String) files.get(x + 2), treeModel));
            }
            list.remove(nodeWait);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            nodeWait.setText(e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            treeModel.structureChanged(NodeLibrary.this);
        });
        isRetrieving = false;
    }

    public void rightClick(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuDelete = new JMenuItem();
        JMenuItem menuRename = new JMenuItem();
        JMenuItem menuRefresh = new JMenuItem();
        JMenuItem menuPaste = new JMenuItem();
        JMenuItem menuAddFirst = new JMenuItem();
        JMenuItem menuAddLast = new JMenuItem();
        JMenuItem menuNewSourceFile = new JMenuItem();

        menuDelete.setText("Delete");
        menuRename.setText("Rename");
        menuRefresh.setText("Refresh");
        menuPaste.setText("Paste from Copy Buffer");
        menuAddFirst.setText("Add to First of Library List");
        menuAddLast.setText("Add to Last of Library List");
        menuNewSourceFile.setText("New Source File");
        if (!Environment.copyBuffer.isEmpty()) {
            menuPaste.setEnabled(true);
        } else {
            menuPaste.setEnabled(false);
        }
        if (this.isRetrieving || this.hasExpanded == false) {
            menuRefresh.setEnabled(false);
        } else {
            menuRefresh.setEnabled(true);
        }
        popupMenu.add(menuRefresh);
        popupMenu.add(menuPaste);
        popupMenu.add(menuDelete);
        popupMenu.add(menuRename);
        popupMenu.add(menuNewSourceFile);
        popupMenu.add(menuAddFirst);
        popupMenu.add(menuAddLast);

        menuRefresh.addActionListener(new ActionRefresh());
        menuAddFirst.addActionListener(new ActionAdd(true));
        menuAddLast.addActionListener(new ActionAdd(false));
        menuNewSourceFile.addActionListener(new ActionNewSourceFile());
        menuPaste.addActionListener(new ActionPaste());

        popupMenu.show(invoker, x, y);
    }

    protected void disposeFiles() {
        Object object;

        for (int x = 0; x < list.size(); x++) {
            object = list.get(x);
            if (object instanceof NodeFile) {
                ((NodeFile) object).disposeMembers();
            }
        }
    }

    class ActionPaste implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object object;

            try {
                for (int x = 0; x < Environment.copyBuffer.size(); x++) {
                    object = Environment.copyBuffer.get(x);
                    ((CopyRequest) object).copyTo(as400, text);
                }
                Environment.copyBuffer.clear();
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * creates a source file.
     */
    class ActionNewSourceFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            String name, description;

            name = JOptionPane.showInputDialog(null, "Name for new source file?", "New Source File",
                    JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.trim().length() == 0) {
                return;
            }
            if (name.trim().length() > 10) {
                JOptionPane.showMessageDialog(null, "Name must be no more than 10 characters.");
                return;
            }
            if (name.contains(" ")) {
                JOptionPane.showMessageDialog(null, "Name must not contain spaces.");
                return;
            }
            description = JOptionPane.showInputDialog(null, "Description for new source file?", "New Source File",
                    JOptionPane.QUESTION_MESSAGE);
            if (description == null) {
                description = "";
            }
            try {
                as400.call("CRTSRCPF FILE(" + text + "/" + name + ") RCDLEN(120) TEXT('" + description + "')");
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    /**
     */
    class ActionRefresh implements ActionListener {

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent evt) {
            disposeFiles();
            list.clear();
            list.add(nodeWait);
            SwingUtilities.invokeLater(() -> {
                treeModel.structureChanged(NodeLibrary.this);
            });
            hasExpanded = false;
            expand();
        }
    }

    /**
     */
    class ActionAdd implements ActionListener {

        boolean first;

        public ActionAdd(boolean first) {
            this.first = first;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                if (first) {
                    as400.call("ADDLIBLE " + text + " POSITION(*FIRST)");
                } else {
                    as400.call("ADDLIBLE " + text + " POSITION(*LAST)");
                }
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }
}

class NodeFile extends NodeDefault {

    AS400System as400;
    String library;
    String fileType;
    TreeModelNode treeModel;
    boolean hasExpanded = false;
    boolean isRetrieving = false;
    NodeDefault nodeWait = new NodeDefault(this, "Retrieving members...");
    Logger logger = LoggerFactory.getLogger(NodeFile.class);

    public NodeFile(Node parent, AS400System as400, String library, String file, String fileType, String tableType,
            TreeModelNode treeModel) {
        super(parent, file);
        this.as400 = as400;
        this.fileType = fileType;
        this.library = library;
        this.treeModel = treeModel;
        if (fileType.equalsIgnoreCase("S")) {
            this.icon = Icons.iconSourceFile;
            add(nodeWait);
        } else if (tableType.equalsIgnoreCase("A")) {
            this.icon = Icons.iconAlias;
        } else if (tableType.equalsIgnoreCase("L")) {
            this.icon = Icons.iconLogicalFile;
        } else if (tableType.equalsIgnoreCase("P")) {
            this.icon = Icons.iconPhyssicalFile;
        } else if (tableType.equalsIgnoreCase("T")) {
            this.icon = Icons.iconTable;
        } else if (tableType.equalsIgnoreCase("V")) {
            this.icon = Icons.iconView;
        }
    }

    @Override
    public String getToolTipText() {
        return as400.getName() + " - " + library + " - " + text;
    }

    @Override
    public void expand() {
        if (hasExpanded == false) {
            startRetrieveMembers();
        }
        hasExpanded = true;
    }

    protected void startRetrieveMembers() {
        new Thread() {
            @Override
            public void run() {
                retrieveMembers();
            }
        }.start();
    }

    @SuppressWarnings("rawtypes")
    protected void retrieveMembers() {
        ArrayList members;

        isRetrieving = true;
        try {
            members = as400.listMembers(library, text);
            for (int x = 0; x < members.size(); x++) {
                add(new NodeMember(this, as400, (Member) members.get(x), treeModel));
            }
            list.remove(nodeWait);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            nodeWait.setText(e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            treeModel.structureChanged(NodeFile.this);
        });
        isRetrieving = false;
    }

    @Override
    public void rightClick(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuDelete = new JMenuItem();
        JMenuItem menuRename = new JMenuItem();
        JMenuItem menuRefresh = new JMenuItem();
        JMenuItem menuCopy = new JMenuItem();
        JMenuItem menuPaste = new JMenuItem();
        JMenuItem menuNewMember = new JMenuItem();
        JMenuItem menuLayout = new JMenuItem();

        menuDelete.setText("Delete");
        menuRename.setText("Rename");
        menuRefresh.setText("Refresh");
        menuCopy.setText("Add to Copy Buffer");
        menuNewMember.setText("New Member");
        menuPaste.setText("Paste from Copy Buffer");
        menuLayout.setText("Layout");
        menuPaste.setEnabled(false);
        if (!Environment.copyBuffer.isEmpty()) {
            menuPaste.setEnabled(true);
        }
        if (this.isRetrieving || this.hasExpanded == false) {
            menuRefresh.setEnabled(false);
        } else {
            menuRefresh.setEnabled(true);
        }
        menuNewMember.setEnabled(fileType.equalsIgnoreCase("S"));
        popupMenu.add(menuRefresh);
        popupMenu.add(menuCopy);
        popupMenu.add(menuPaste);
        popupMenu.add(menuDelete);
        popupMenu.add(menuRename);
        popupMenu.add(menuNewMember);
        popupMenu.add(menuLayout);

        menuDelete.addActionListener(new ActionDelete());
        menuRefresh.addActionListener(new ActionRefresh());
        menuCopy.addActionListener(new ActionCopy());
        menuPaste.addActionListener(new ActionPaste());
        menuNewMember.addActionListener(new ActionNewMember());
        menuLayout.addActionListener(new ActionLayout());

        popupMenu.show(invoker, x, y);
    }

    protected void disposeMembers() {
        Object object;

        for (int x = 0; x < list.size(); x++) {
            object = list.get(x);
            if (object instanceof NodeMember) {
                ((NodeMember) object).dispose();
            }
        }
    }

    class ActionDelete implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete File", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                return;
            }
            try {
                as400.call("DLTF FILE(" + library + "/" + text + ")");
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    /**
     */
    class ActionNewMember implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            DialogMemberNew.showDialog(null, as400, library, text, null, null, null);
        }
    }

    /**
     */
    class ActionLayout implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.layout.open(new LayoutRequest(as400, library + "/" + text));
        }
    }

    /**
     */
    class ActionCopy implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.addToCopyBuffer(as400, library, text);
        }
    }

    class ActionPaste implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object object;

            try {
                for (int x = 0; x < Environment.copyBuffer.size(); x++) {
                    object = Environment.copyBuffer.get(x);
                    ((CopyRequest) object).copyTo(as400, library, text);
                }
                Environment.copyBuffer.clear();
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    /**
     */
    class ActionRefresh implements ActionListener {

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent evt) {
            disposeMembers();
            list.clear();
            list.add(nodeWait);
            SwingUtilities.invokeLater(() -> {
                treeModel.structureChanged(NodeFile.this);
            });
            hasExpanded = false;
            expand();
        }
    }
}

class NodeMember extends NodeDefault implements ListenerMember {

    Member member;
    TreeModelNode treeModel;
    Logger logger = LoggerFactory.getLogger(NodeMember.class);

    public NodeMember(Node parent, AS400System as400, Member member, TreeModelNode treeModel) {
        super(parent, member.getName());
        this.member = member;
        this.treeModel = treeModel;
        determineIcon();
        member.addListener(this);
    }

    public void dispose() {
        member.removeListener(this);
    }

    @Override
    public void memberChanged(Member member) {
        this.text = member.getName();
        determineIcon();
        SwingUtilities.invokeLater(() -> {
            treeModel.nodeChanged(NodeMember.this);
        });
    }

    @Override
    public boolean isOkToClose(Member member) {
        return true;
    }

    @Override
    public void click(int count) {
        if (count != 2) {
            return;
        }
        Environment.members.open(member);
    }

    @Override
    public String getToolTipText() {
        return member.getSystem().getName() + " - " + member.getLibrary() + " - " + member.getFile() + " - "
                + member.getName();
    }

    @Override
    public void rightClick(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuDelete = new JMenuItem();
        JMenuItem menuRename = new JMenuItem();
        JMenuItem menuCopy = new JMenuItem();

        menuDelete.setText("Delete");
        menuRename.setText("Rename");
        menuCopy.setText("Add to Copy Buffer");
        popupMenu.add(menuCopy);
        popupMenu.add(menuDelete);
        popupMenu.add(menuRename);

        menuCopy.addActionListener(new ActionCopy());
        menuRename.addActionListener(new ActionRename());
        menuDelete.addActionListener(new ActionDelete());

        popupMenu.show(invoker, x, y);
    }

    /**
     */
    class ActionCopy implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Environment.addToCopyBuffer(member);
        }
    }

    /**
     */
    class ActionRename implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            String name;

            name = JOptionPane.showInputDialog(null, "New name for member?", "Rename", JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.trim().length() == 0) {
                return;
            }
            if (name.trim().length() > 10) {
                JOptionPane.showMessageDialog(null, "Name must be no more than 10 characters.");
                return;
            }
            if (name.contains(" ")) {
                JOptionPane.showMessageDialog(null, "Name must not contain spaces.");
                return;
            }
            try {
                member.setName(name);
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    /**
     */
    class ActionDelete implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete Member", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                return;
            }
            try {
                member.delete();
            } catch (Exception e) {
                // e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    public void determineIcon() {
        if (member.getSourceType().equals(Member.SOURCE_TYPE_DSPF)) {
            icon = Icons.iconScreen;
        } else if (member.getSourceType().equals(Member.SOURCE_TYPE_RPGLE)) {
            icon = Icons.iconRpgle;
        } else if (member.getSourceType().equals(Member.SOURCE_TYPE_SQLRPGLE)) {
            icon = Icons.iconSqlrpgle;
        } else if (member.getSourceType().equals(Member.SOURCE_TYPE_CLP)) {
            icon = Icons.iconCl;
        } else if (member.getSourceType().equals(Member.SOURCE_TYPE_CLLE)) {
            icon = Icons.iconCl;
        } else if (member.getSourceType().equals(Member.SOURCE_TYPE_PRTF)) {
            icon = Icons.iconPrtf;
        } else {
            icon = Icons.iconMember;
        }
    }
}
