package org.egomez.irpgeditor.swing;

import javax.swing.JFrame;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WindowAbout extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the application.
     */
    public WindowAbout() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        this.setModal(true);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/ibmas400iseries.jpg")));
        this.setResizable(false);
        this.setTitle("iRPGEditor");
        this.setBounds(100, 100, 450, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(null);

        JButton btncerrar = new JButton("Cerrar");
        btncerrar.addActionListener((ActionEvent e) -> {
            closeWindows();
        });
        btncerrar.setMnemonic(KeyEvent.VK_C);
        btncerrar.setBounds(345, 238, 89, 23);
        this.getContentPane().add(btncerrar);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(10, 11, 405, 151);
        this.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(10, 11, 120, 85);
        panel.add(lblNewLabel);
        lblNewLabel.setIcon(new ImageIcon(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/ibm-as400-iseries-ibmi-server.png")));

        JLabel lblNewLabel_1 = new JLabel("iRPGEditor");
        lblNewLabel_1.setBounds(173, 11, 109, 30);
        panel.add(lblNewLabel_1);
        lblNewLabel_1.setFont(new Font("Calibri", Font.BOLD, 24));

        JLabel lblVersion = new JLabel("Version 1.1b1");
        lblVersion.setBounds(232, 52, 140, 26);
        panel.add(lblVersion);
        lblVersion.setFont(new Font("Calibri", Font.BOLD, 20));

        JLabel lbloriginalDavid = new JLabel("<html><p>Original:  Derek Van Kooten.</p><p>Modified: Edwin G&oacute;mez Alm&eacute;star</p></html>");
        lbloriginalDavid.setBounds(148, 85, 155, 28);
        panel.add(lbloriginalDavid);

        JLabel lblBuildId = new JLabel("Build id: 20200312");
        lblBuildId.setBounds(148, 124, 124, 14);
        panel.add(lblBuildId);

        JButton button = new JButton("");
        button.setToolTipText("iText");
        button.setIcon(new ImageIcon(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/itext.gif")));
        button.setBounds(20, 173, 62, 57);
        this.getContentPane().add(button);

        JButton button_1 = new JButton("");
        button_1.setToolTipText("tn5250j");
        button_1.setIcon(new ImageIcon(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/g7831-48_med.png")));
        button_1.setBounds(92, 173, 62, 57);
        this.getContentPane().add(button_1);

        JButton button_2 = new JButton("");
        button_2.setIcon(new ImageIcon(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/jGoodies.jpg")));
        button_2.setToolTipText("jGoodies");
        button_2.setBounds(164, 173, 62, 57);
        this.getContentPane().add(button_2);

        JButton button_3 = new JButton("");
        button_3.setIcon(new ImageIcon(WindowAbout.class.getResource("/org/egomez/irpgeditor/icons/ibm-logo.png")));
        button_3.setToolTipText("IBM");
        button_3.setBounds(236, 173, 62, 57);
        getContentPane().add(button_3);
    }

    protected void closeWindows() {
        this.dispose();
    }
}
