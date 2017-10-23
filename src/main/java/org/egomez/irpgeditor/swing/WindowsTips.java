package org.egomez.irpgeditor.swing;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.jdesktop.swingx.JXPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.SwingConstants;

public class WindowsTips extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7578203158384233730L;
	private JXTipOfTheDay totd;
	private TipOfTheDayModel model;
	private JXHyperlink nextTipLink;

	/**
	 * Create the dialog.
	 */
	public WindowsTips() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Tip of Day");
		setModal(true);
		setBounds(100, 100, 450, 254);
		getContentPane().setLayout(new BorderLayout(0, 0));

		model = createTipOfTheDayModel();

		JXPanel panel = new JXPanel(new VerticalLayout());
		totd = new JXTipOfTheDay(model);

		totd.setName("totd");
		Random r = new Random();
		totd.setCurrentTip(r.nextInt(model.getTipCount()));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(totd);
		getContentPane().add(panel);

		nextTipLink = new JXHyperlink();
		nextTipLink.setHorizontalAlignment(SwingConstants.RIGHT);
		nextTipLink.setText("Next Tip >>");
		nextTipLink.setName("nextTipLink");
		panel.add(nextTipLink, BorderLayout.SOUTH);
		getContentPane().add(panel);
		nextTipLink.addActionListener(totd.getActionMap().get("nextTip"));

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winDim = getBounds();
		setLocation((screenDim.width - winDim.width) / 2, (screenDim.height - winDim.height) / 2);

	}

	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 543344866573376659L;

			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}

	protected TipOfTheDayModel createTipOfTheDayModel() {
		// Create a tip model with some tips
		DefaultTipOfTheDayModel tips = new DefaultTipOfTheDayModel();

		// html text
		tips.add(new DefaultTip("Create Project",
				"<html><p>You need to create Project for associated source files</p></html>"));
		tips.add(new DefaultTip("Using Panel SQL(Select)",
				"<html><p>You can export SQL Query to Excel or Txt files</p></html>"));
		tips.add(new DefaultTip("Export Spool to PDF", "<html><p>You can export Spool to PDF or Txt Files</p></html>"));
		tips.add(new DefaultTip("Save Member in Local Disk",
				"<html><p>Using Save Member on Disk (Ctrl + Alt + L) for save member in Local Disk</p></html>"));
		tips.add(new DefaultTip("Change Library List",
				"<html><p>Add library in Library List for compile program, run SQL Script and other. "
						+ "Use option Add to Library List in Session Menu</p></html>"));
		tips.add(new DefaultTip("Use File View",
				"<html><p>Show fields of Table and execute SQL Script. Use Database Menu -> Add File View </p></html>"));
		tips.add(new DefaultTip("Spool Tabbed",
				"<html><p>Show Spool Files in Tabbed Spool. You can show or delete file</p></html>"));
		tips.add(new DefaultTip("Find Text", "<html><p>Use Ctrl + F for text search in Member File</p></html>"));
		tips.add(new DefaultTip("Open Terminal",
				"<html><p>In System Tabbed Panel use Open for new tn250j session. You can use F6 too.</p></html>"));
		tips.add(new DefaultTip("SQL (Select)",
				"<html><p>In SQL (Select) Tabbed Panel execute SQL Script Select and export result to Excel o TXT Files.</p></html>"));
		tips.add(new DefaultTip("Open Member",
				"<html><p>Use Open Member from Library for Open Member quickly and easy.</p></html>"));
		tips.add(new DefaultTip("Open Files Description",
				"<html><p>Use Option Files -> Layout in Left Panel when edit member RPG. It's Open File View Option</p></html>"));
		return tips;
	}
}
