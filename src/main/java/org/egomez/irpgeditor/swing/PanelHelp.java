package org.egomez.irpgeditor.swing;

import java.io.*;
import java.net.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.help.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * @author not attributable
 */

public class PanelHelp extends PanelTool implements ClosableTab {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1172114414972937849L;
	HelpRequest helpRequest;
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JEditorPane editorpaneHelp = new JEditorPane();
	Logger logger = LoggerFactory.getLogger(PanelHelp.class);
	public PanelHelp() {
		try {
			jbInit();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		editorpaneHelp.setText("Loading help...");
		this.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(editorpaneHelp, null);
	}

	public void setHelpRequest(HelpRequest helpRequest) {
		this.helpRequest = helpRequest;
		setName(helpRequest.getWord());
		URL url = Help.getURL(helpRequest.getWord());
		if (url == null) {
			editorpaneHelp.setText("Unable to find help for " + helpRequest.getWord());
		} else {
			try {
				editorpaneHelp.setPage(url);
			} catch (IOException e) {
				editorpaneHelp.setText(e.getMessage());
			}
		}
	}

	/**
	 * gets called when then user clicks on the x icon. so, clicking on the x
	 * doesnt remove it from the tabbedpane.
	 */
	public void closeTab() {
		Environment.toolManager.close(helpRequest, false);
	}
}
