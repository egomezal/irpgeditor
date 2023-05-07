package org.egomez.irpgeditor;

import javax.swing.JDialog;
import com.borland.jbcl.layout.VerticalFlowLayout;
import com.ibm.as400.access.AS400JDBCDriver;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.egomez.irpgeditor.env.Environment;
import org.egomez.irpgeditor.event.ListenerMemberCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogMemberDuplicate extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 319576545761625719L;

    ActionCancel actionCancel = new ActionCancel();

    ActionOk actionOk = new ActionOk();

    ActionSelectFile actionSelectFile = new ActionSelectFile();

    ActionSelectLibrary actionSelectLibrary = new ActionSelectLibrary();

    ActionSelectSystem actionSelectSystem = new ActionSelectSystem();
    BorderLayout borderLayout1 = new BorderLayout();

    BorderLayout borderLayout3 = new BorderLayout();

    BorderLayout borderLayout4 = new BorderLayout();

    BorderLayout borderLayout5 = new BorderLayout();

    BorderLayout borderLayout6 = new BorderLayout();

    JButton buttonCancel = new JButton();
    JButton buttonOk = new JButton();
    @SuppressWarnings("rawtypes")
    DefaultComboBoxModel listModelFiles = new DefaultComboBoxModel();

    @SuppressWarnings("rawtypes")
    DefaultComboBoxModel listModelLibraries = new DefaultComboBoxModel();

    @SuppressWarnings("rawtypes")
    DefaultComboBoxModel listModelSystems = new DefaultComboBoxModel();

    @SuppressWarnings({"unchecked", "rawtypes"})
    JComboBox comboboxFile = new JComboBox(listModelFiles);

    @SuppressWarnings({"rawtypes", "unchecked"})
    JComboBox comboboxLibrary = new JComboBox(listModelLibraries);

    @SuppressWarnings({"unchecked", "rawtypes"})
    JComboBox comboboxSystem = new JComboBox(listModelSystems);

    AS400JDBCDriver driver = new AS400JDBCDriver();

    FlowLayout flowLayout1 = new FlowLayout();

    JPanel jPanel12 = new JPanel();

    JPanel jPanel15 = new JPanel();

    JPanel jPanel3 = new JPanel();

    JPanel jPanel4 = new JPanel();

    JPanel jPanel5 = new JPanel();

    JPanel jPanel6 = new JPanel();

    JLabel labelFile = new JLabel();

    JLabel labelLibrary = new JLabel();

    JLabel labelMember = new JLabel();

    JLabel labelSystem = new JLabel();

    ListenerMemberCreated listener;
    Member member;
    JPanel panel1 = new JPanel();
    Logger logger = LoggerFactory.getLogger(DialogMemberDuplicate.class);
    JTextField textfieldMember = new JTextField();

    VerticalFlowLayout verticalFlowLayout4 = new VerticalFlowLayout();

    public DialogMemberDuplicate(Frame frame) {
        super(frame, "Duplicate Member", true);
        enableEvents(64L);
        try {
            jbInit();
            pack();
            addActions();
        } catch (Exception ex) {

            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    protected void addActions() {
        ArrayList<AS400System> listSystems = Environment.systems.getSystems();
        for (int x = 0; x < listSystems.size(); x++) {
            listModelSystems.addElement(listSystems.get(x));
        }
        comboboxSystem.setSelectedIndex(-1);

        comboboxSystem.addActionListener(actionSelectSystem);
        comboboxLibrary.addActionListener(actionSelectLibrary);
        comboboxFile.addActionListener(actionSelectFile);
        buttonOk.addActionListener(actionOk);
        buttonCancel.addActionListener(actionCancel);
    }

    void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        setTitle("Duplicate Member");
        buttonCancel.setMnemonic('C');
        buttonCancel.setText("Cancel");
        buttonOk.setMnemonic('O');
        buttonOk.setText("Ok");
        jPanel15.setLayout(flowLayout1);
        flowLayout1.setAlignment(2);
        flowLayout1.setHgap(2);
        flowLayout1.setVgap(2);
        jPanel4.setLayout(borderLayout4);
        jPanel3.setLayout(borderLayout3);
        jPanel12.setLayout(verticalFlowLayout4);
        labelFile.setEnabled(false);
        labelFile.setText("File: ");
        comboboxLibrary.setEnabled(false);
        labelLibrary.setEnabled(false);
        labelLibrary.setText("Library: ");
        verticalFlowLayout4.setHgap(2);
        verticalFlowLayout4.setVgap(2);
        comboboxFile.setEnabled(false);
        jPanel5.setLayout(borderLayout5);
        labelSystem.setText("System: ");
        labelMember.setText("Member: ");
        labelMember.setEnabled(false);
        jPanel6.setLayout(borderLayout6);
        textfieldMember.setEnabled(false);
        getContentPane().add(panel1, "Center");
        panel1.add(jPanel15, "South");
        jPanel15.add(buttonOk, null);
        jPanel15.add(buttonCancel, null);
        panel1.add(jPanel12, "Center");
        jPanel12.add(jPanel5, null);
        jPanel5.add(labelSystem, "West");
        jPanel5.add(comboboxSystem, "Center");
        jPanel12.add(jPanel4, null);
        jPanel4.add(labelLibrary, "West");
        jPanel4.add(comboboxLibrary, "Center");
        jPanel12.add(jPanel3, null);
        jPanel3.add(labelFile, "West");
        jPanel3.add(comboboxFile, "Center");
        jPanel12.add(jPanel6, null);
        jPanel6.add(labelMember, "West");
        jPanel6.add(textfieldMember, "Center");
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == 201) {
            this.actionCancel.actionPerformed(null);
        }
    }

    public void set(Member member) {
        this.member = member;
        AS400System system = member.getSystem();
        if (system != null) {
            this.comboboxSystem.setSelectedItem(system);
            String library = member.getLibrary();
            if (library != null) {
                this.comboboxLibrary.setSelectedItem(library);
                String file = member.getFile();
                if (file != null) {
                    this.comboboxFile.setSelectedItem(file);
                }
            }
        }
        String memberName = member.getName();
        if (memberName != null) {
            this.textfieldMember.setText(memberName);
        }
    }

    public static void showDialog(Frame frame, Member member,
            ListenerMemberCreated listener) {
        DialogMemberDuplicate dialog = new DialogMemberDuplicate(frame);
        dialog.listener = listener;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialog.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialog.set(member);
        dialog.setVisible(true);
        dialog.dispose();
    }

    class ActionOk implements ActionListener {

        ActionOk() {
        }

        @SuppressWarnings("deprecation")
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (DialogMemberDuplicate.this.textfieldMember.getText().trim()
                    .length() > 10) {
                JOptionPane.showMessageDialog(KeyboardFocusManager
                        .getCurrentKeyboardFocusManager().getActiveWindow(),
                        "Name must be no more than 10 characters.");
                return;
            }
            if (DialogMemberDuplicate.this.textfieldMember.getText().trim().contains(" ")) {
                JOptionPane.showMessageDialog(KeyboardFocusManager
                        .getCurrentKeyboardFocusManager().getActiveWindow(),
                        "Name must not contain spaces.");
                return;
            }

            if ((DialogMemberDuplicate.this.comboboxSystem.getSelectedIndex() == -1)
                    || (DialogMemberDuplicate.this.comboboxLibrary
                            .getSelectedIndex() == -1)
                    || (DialogMemberDuplicate.this.comboboxFile
                            .getSelectedIndex() == -1)
                    || (DialogMemberDuplicate.this.textfieldMember.getText()
                            .trim().length() == 0)) {
                return;
            }
            AS400System systemTo = (AS400System) DialogMemberDuplicate.this.comboboxSystem
                    .getSelectedItem();
            String library = (String) DialogMemberDuplicate.this.comboboxLibrary
                    .getSelectedItem();
            String file = (String) DialogMemberDuplicate.this.comboboxFile
                    .getSelectedItem();
            String name = DialogMemberDuplicate.this.textfieldMember.getText();
            try {
                DialogMemberDuplicate.this.member.copyTo(systemTo, library,
                        file, name, DialogMemberDuplicate.this.listener);
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
            }
            DialogMemberDuplicate.this.hide();
        }
    }

    class ActionSelectFile implements ActionListener {

        ActionSelectFile() {
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            DialogMemberDuplicate.this.textfieldMember.setEnabled(true);
            DialogMemberDuplicate.this.labelMember.setEnabled(true);
        }
    }

    class ActionSelectLibrary implements ActionListener {

        ActionSelectLibrary() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (DialogMemberDuplicate.this.comboboxLibrary.getSelectedIndex() == -1) {
                return;
            }
            AS400System system = (AS400System) DialogMemberDuplicate.this.listModelSystems
                    .getSelectedItem();
            if (system == null) {
                return;
            }
            try {
                DialogMemberDuplicate.this.comboboxFile
                        .removeActionListener(DialogMemberDuplicate.this.actionSelectFile);

                DialogMemberDuplicate.this.listModelFiles.removeAllElements();
                ArrayList<String> list = system
                        .getSourceFiles((String) DialogMemberDuplicate.this.comboboxLibrary
                                .getSelectedItem());
                while (!list.isEmpty()) {
                    DialogMemberDuplicate.this.listModelFiles
                            .addElement((String) list.remove(0));
                }
                DialogMemberDuplicate.this.comboboxFile.setSelectedIndex(-1);
                DialogMemberDuplicate.this.labelFile.setEnabled(true);
                DialogMemberDuplicate.this.comboboxFile.setEnabled(true);
                DialogMemberDuplicate.this.comboboxFile
                        .addActionListener(DialogMemberDuplicate.this.actionSelectFile);
            } catch (SQLException e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
                JOptionPane.showMessageDialog(KeyboardFocusManager
                        .getCurrentKeyboardFocusManager().getActiveWindow(), e
                                .getMessage());
            }
        }
    }

    class ActionSelectSystem implements ActionListener {

        ActionSelectSystem() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent evt) {
            AS400System system = (AS400System) DialogMemberDuplicate.this.listModelSystems
                    .getSelectedItem();
            if (system == null) {
                return;
            }
            try {
                ArrayList<String> list = system.getSourceLibraries();
                while (!list.isEmpty()) {
                    DialogMemberDuplicate.this.listModelLibraries
                            .addElement(list.remove(0));
                }
                DialogMemberDuplicate.this.comboboxLibrary.setSelectedIndex(-1);
                DialogMemberDuplicate.this.comboboxLibrary
                        .addActionListener(DialogMemberDuplicate.this.actionSelectLibrary);
                DialogMemberDuplicate.this.labelLibrary.setEnabled(true);
                DialogMemberDuplicate.this.comboboxLibrary.setEnabled(true);
            } catch (SQLException e) {

                logger.error(e.getMessage());
                JOptionPane.showMessageDialog(KeyboardFocusManager
                        .getCurrentKeyboardFocusManager().getActiveWindow(), e
                                .getMessage());
            }
            DialogMemberDuplicate.this.labelFile.setEnabled(false);
            DialogMemberDuplicate.this.comboboxFile.setEnabled(false);
            DialogMemberDuplicate.this.listModelFiles.removeAllElements();
        }
    }

    class ActionCancel implements ActionListener {

        ActionCancel() {
        }

        @SuppressWarnings("deprecation")
        @Override
        public void actionPerformed(ActionEvent evt) {
            DialogMemberDuplicate.this.hide();
        }
    }
}
