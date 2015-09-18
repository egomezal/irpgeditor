package org.egomez.irpgeditor.swing;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
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

	protected TipOfTheDayModel createTipOfTheDayModel() {
		// Create a tip model with some tips
		DefaultTipOfTheDayModel tips = new DefaultTipOfTheDayModel();


		// html text
		tips.add(new DefaultTip("Create Project", "<html><p>You need to create Project for associated source files</p></html>"));
		tips.add(new DefaultTip("Using Panel SQL(Select)", "<html><p>You can export SQL Query to Excel or Txt files</p></html>"));
		tips.add(new DefaultTip("Export Spool to PDF", "<html><p>You can export Spool to PDF or Txt Files</p></html>"));
		return tips;
	}
}
