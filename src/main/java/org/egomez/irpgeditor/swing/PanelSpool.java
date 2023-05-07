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

import java.awt.*;
import java.io.UnsupportedEncodingException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.*;
import java.io.IOException;

/**
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unused")
public class PanelSpool extends PanelTool implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2016638345182128334L;
	SpooledFile file;
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JTextArea textareaSpool = new JTextArea();
	String cadena;
	Logger logger = LoggerFactory.getLogger(PanelSpool.class);
	public PanelSpool() {
		try {
			jbInit();
			new HandlerKeyPressed(textareaSpool);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void setSpooledFile(SpooledFile file) {
		this.file = file;
		this.setName(file.getName());
		new Thread(this).start();
	}

        @Override
	public void run() {
		PrintObjectTransformedInputStream is;
		PrintParameterList printParms;
		final StringBuffer string;

		char[] buffer;

		textareaSpool.setText("Loading....");

		// no idea what this does, found it online.
		printParms = new PrintParameterList();
		printParms.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT, "/QSYS.LIB/QWPDEFAULT.WSCST");
		printParms.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");
		printParms.setParameter(PrintObject.ATTR_CODEPAGE, 284);
		string = new StringBuffer();
		cadena = new String();

		try {
			PrintObjectTransformedInputStream localInputStream = file.getTransformedInputStream(printParms);
			int j = localInputStream.available();
			int k = 0;
			int m = 0;
			byte[] arrayOfByte = new byte[j + 1];
			while (j > 0) {
				if (j > arrayOfByte.length)
					arrayOfByte = new byte[j + 1];
				k = localInputStream.read(arrayOfByte, 0, j);
				for (int n = 0; n < k; n++)
					switch (arrayOfByte[n]) {
					case 0:
						break;
					case 10:
						cadena = cadena + string.toString() + "\n";
						string.setLength(0);
						break;
					case 13:
						break;
					case 12:
						cadena = cadena + string.toString() + "\n";
						string.setLength(0);
						break;
					default:
						string.append(byte2char(arrayOfByte[n], "cp437"));
					}
				m += k;

				j = localInputStream.available();
			}

			SwingUtilities.invokeLater(() -> {
                            textareaSpool.setText(cadena);
                            textareaSpool.setSelectionStart(0);
                            textareaSpool.setSelectionEnd(0);
                        });
		} catch (AS400SecurityException | ErrorCompletingRequestException | RequestNotSupportedException | IOException | InterruptedException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public static char byte2char(byte paramByte, String paramString) {
		char c = ' ';
		try {
			byte[] arrayOfByte = { paramByte };
			c = new String(arrayOfByte, paramString).charAt(0);
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			System.err.println(localUnsupportedEncodingException);
			System.err.println("Error while converting byte to char, returning blank...");
		}
		return c;
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		this.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(textareaSpool, null);
		textareaSpool.setFont(new java.awt.Font("DialogInput", 0, 14));
	}
}
