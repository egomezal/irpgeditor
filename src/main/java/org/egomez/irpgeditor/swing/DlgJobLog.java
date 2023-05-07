package org.egomez.irpgeditor.swing;

import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.JXTextArea;

import com.ibm.as400.access.JobLog;
import com.ibm.as400.access.ObjectDoesNotExistException;

import com.ibm.as400.access.QueuedMessage;
import javax.swing.JScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class DlgJobLog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6528943919106177533L;
	private final JPanel contentPanel = new JPanel();
	private JXTextArea textArea = null;
	boolean flgError = false;
	/**
	 * Create the dialog.
     * @param title
	 */
	public DlgJobLog(String title, JobLog log) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if(flgError)
					dispose();
			}
		});
		setTitle(title);
		
		setBounds(100, 100, 583, 250);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winDim = getBounds();
		setLocation((screenDim.width - winDim.width) / 2, (screenDim.height - winDim.height) / 2);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			textArea = new JXTextArea();
			textArea.setEditable(false);
			QueuedMessage message;
			@SuppressWarnings("rawtypes")
			Enumeration listMessage;
			String datos = "";
			try {
				listMessage = log.getMessages();
				while (listMessage.hasMoreElements()) {
					message = (QueuedMessage) listMessage.nextElement();
					datos = datos + message.getID() + " - " + message.getText() + "\n";
				}
				textArea.setText(datos);
				JScrollPane scrollPane = new JScrollPane(textArea);
				contentPanel.add(scrollPane, BorderLayout.CENTER);
			} catch (AS400SecurityException | ErrorCompletingRequestException | ObjectDoesNotExistException | IOException | InterruptedException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				flgError=true;
			}
		}
		
	}

}
