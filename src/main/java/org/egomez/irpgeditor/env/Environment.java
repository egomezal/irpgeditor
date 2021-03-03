package org.egomez.irpgeditor.env;

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

import java.io.*;
import java.util.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author not attributable
 */
public class Environment {
	final static Logger logger = LoggerFactory.getLogger(Environment.class);
	public static AS400Systems systems = new AS400Systems();
	public static CompilerResults compilerResults = new CompilerResults();
	public static Qcmdexec qcmdexec = new Qcmdexec();
	public static SearchResults searchResults = new SearchResults();
	public static SQL sql = new SQL();
	public static Projects projects = new Projects();
	public static Layout layout = new Layout();
	public static SpoolFiles spoolFiles = new SpoolFiles();
	public static Members members = new Members();
	public static Actions actions = new Actions();
	public static Structure structure = new Structure();
	public static ToolManager toolManager = new ToolManager();
	public static File fileOpenDefault = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
			+ "conf" );
	public static Properties settings = new Properties();
	@SuppressWarnings("rawtypes")
	public static ArrayList copyBuffer = new ArrayList();

	private Environment() {
	}

	public static void loadSettings() {
		File file;
		FileInputStream fis;

		file = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator + "conf"
				+ File.separator + "irpgeditor.properties");
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				settings.load(fis);
				fis.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
				//e.printStackTrace();
			}
		}
		try {
			fileOpenDefault = new File(settings.getProperty("fileDefault"));
		} catch (Exception e) {
			fileOpenDefault = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
					+ "conf");
		}
		try {
			systems.loadSettings();
		} catch (IOException e) {
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
		projects.loadSettings();
	}

	@SuppressWarnings("static-access")
	public static void saveSettings() {
		FileOutputStream fos;

		sql.saveSettings();
		projects.saveSettings();
		try {
			systems.saveSettings();
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
		settings.setProperty("fileDefault", fileOpenDefault.getAbsolutePath());
		try {
			fos = new FileOutputStream(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
					+ "conf" + File.separator + "irpgeditor.properties");
			settings.store(fos, "");
			fos.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public static void addToCopyBuffer(Member member) {
		copyBuffer.add(new CopyMember(member));
	}

	@SuppressWarnings("unchecked")
	public static void addToCopyBuffer(AS400System as400, String lib, String file) {
		copyBuffer.add(new CopyFile(as400, lib, file));
	}
}
