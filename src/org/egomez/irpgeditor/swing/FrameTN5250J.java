package org.egomez.irpgeditor.swing;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tn5250j.*;
import org.tn5250j.framework.common.SessionManager;

/**
 * 
 * @author Derek Van Kooten.
 */
public class FrameTN5250J extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1146630124521323863L;
	SessionGUI sp;
	Session5250 sesion;
	Logger logger = LoggerFactory.getLogger(FrameTN5250J.class);
	
	protected SessionManager manager;

	public FrameTN5250J() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		manager = SessionManager.instance();
		org.tn5250j.tools.LangTool.init();
	}

	public void center() {
		validate();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}

	// Overridden so we can disconnect and exit on System Close
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			// disconnect from the session
			sp.disconnect();
			setVisible(false);
			// hide();
		}
	}

	public void setSystem(String system, boolean ssl) {
		java.util.Properties sesProps = new java.util.Properties();
		// String propFileName = null;
		String session = system;
		// Start loading properties
		if (ssl){
			sesProps.put(org.tn5250j.TN5250jConstants.SESSION_HOST_PORT, 992);
		}
		sesProps.put(org.tn5250j.TN5250jConstants.SESSION_HOST, session);
		sesProps.put(org.tn5250j.TN5250jConstants.SESSION_CODE_PAGE, "284");
		sesProps.put(org.tn5250j.TN5250jConstants.SCREEN_SIZE_27X132_STR, "1");
		sesProps.put(org.tn5250j.TN5250jConstants.SESSION_SCREEN_SIZE,
				org.tn5250j.TN5250jConstants.SCREEN_SIZE_27X132_STR);
		try {
			sesProps.put(org.tn5250j.TN5250jConstants.SESSION_DEVICE_NAME, InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
		sesion = manager.openSession(sesProps, "", "");
		sp = new SessionGUI(sesion);

		this.getContentPane().add(sp, BorderLayout.CENTER);
	}

	public void connect() {
		sp.connect();
		sp.grabFocus();
	}

	public void determineSize() {
		FontMetrics fm;
		int w, h;

		if (sp == null) {
			return;
		}
		fm = sp.getFontMetrics(sp.getFont());
		w = fm.charWidth('W') * 160;
		h = fm.getHeight() * 48;
		setSize(w, h);
	}

	public void sendKeys(String s, boolean wait) {
		sp.getScreen().sendKeys(s);
	}

	/**
	 * runs the specified run configuration.
	 */
	public void run(RunConfiguration config) {
		AS400System as400system;
		StringBuffer buffer;
		@SuppressWarnings("rawtypes")
		ArrayList breakPoints;
		Project project;
		ProjectMember member;
		StringTokenizer tokenizer;
		String text;
		String position = "*LAST";
		String token;

		project = (Project) Environment.projects.getSelected();
		if (project == null) {
			return;
		}
		as400system = Environment.systems.getDefault();
		
		setSystem(as400system.getAddress(), as400system.isSsl());
		determineSize();
		center();
		setVisible(true);
		try {
			connect();
			sendKeys(as400system.getUser() + TN5250jConstants.MNEMONIC_FIELD_EXIT + as400system.getPassword()
					+ TN5250jConstants.MNEMONIC_ENTER, true);
			sendKeys(TN5250jConstants.MNEMONIC_ENTER, true);
			sendKeys(TN5250jConstants.MNEMONIC_ENTER, true);
			sendKeys(TN5250jConstants.MNEMONIC_ENTER, true);
			// check for bad password here sometime in future.
			// add in libraries.
			if (config.libraries.trim().length() > 0) {
				tokenizer = new StringTokenizer(config.libraries.trim(), " ;,:");
				while (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();
					if (token.startsWith("*")) {
						position = token;
					} else {
						text = "ADDLIBLE " + token + " POSITION(" + position + ")"
								+ TN5250jConstants.MNEMONIC_FIELD_PLUS + TN5250jConstants.MNEMONIC_ENTER;
						sendKeys(text, true);
					}
				}
			}
			buffer = new StringBuffer();
			member = project.getMember(config.debug, as400system);
			if (member != null && config.debug.trim().length() > 0) {
				if (member.getCompileType().equalsIgnoreCase("CRTRPGPGM")) {
					buffer.append("STRISDB PGM(");
				} else {
					buffer.append("STRDBG PGM(");
				}
				buffer.append(config.debug);
				buffer.append(") UPDPROD(*YES) ");
				buffer.append(TN5250jConstants.MNEMONIC_ENTER);
				if (member != null) {
					breakPoints = member.getBreakPoints();
					for (int x = 0; x < breakPoints.size(); x++) {
						buffer.append("BREAK ");
						if (member.getCompileType().equalsIgnoreCase("CRTRPGPGM")) {
							buffer.append(Integer.parseInt(breakPoints.get(x).toString()) * 100);
						} else {
							buffer.append(breakPoints.get(x));
						}
						buffer.append(TN5250jConstants.MNEMONIC_ENTER);
					}
				}
				if (member.getCompileType().equalsIgnoreCase("CRTRPGPGM")) {
					buffer.append(TN5250jConstants.MNEMONIC_PF17);
					// return because a call isnt needed with this version of
					// debug.
					sendKeys(buffer.toString(), true);
					return;
				} else {
					buffer.append(TN5250jConstants.MNEMONIC_PF12);
				}
			}
			if (config.program.trim().length() > 0) {
				buffer.append("CALL ");
				buffer.append(config.program);
			}
			if (config.parms.trim().length() > 0) {
				buffer.append(" PARM(");
				buffer.append(config.parms.trim());
				buffer.append(")");
			}
			if (config.program.trim().length() > 0) {
				buffer.append(TN5250jConstants.MNEMONIC_ENTER);
			}
			sendKeys(buffer.toString(), true);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
