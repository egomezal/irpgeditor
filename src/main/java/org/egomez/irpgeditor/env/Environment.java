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

    static final Logger logger = LoggerFactory.getLogger(Environment.class);
    public static final AS400Systems systems = new AS400Systems();
    public static final CompilerResults compilerResults = new CompilerResults();
    public static final Qcmdexec qcmdexec = new Qcmdexec();
    public static final SearchResults searchResults = new SearchResults();
    public static final SQL sql = new SQL();
    public static final Projects projects = new Projects();
    public static final Layout layout = new Layout();
    public static final SpoolFiles spoolFiles = new SpoolFiles();
    public static final Members members = new Members();
    public static final Actions actions = new Actions();
    public static final Structure structure = new Structure();
    public static final ToolManager toolManager = new ToolManager();
    public static File fileOpenDefault = new File(
            System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
            + "conf");
    public static Properties settings = new Properties();
    @SuppressWarnings("rawtypes")
    public static ArrayList copyBuffer = new ArrayList();

    private Environment() {
    }

    public static void loadSettings() {
        File file;

        file = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator + "conf"
                + File.separator + "irpgeditor.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                settings.load(fis);
                try {
                    fileOpenDefault = new File(settings.getProperty("fileDefault"));
                } catch (Exception e) {
                    fileOpenDefault = new File(System.getProperty("user.home") + File.separator + ".iRPGEditor" + File.separator
                            + "conf");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());

            }
        }

        try {
            systems.loadSettings();
        } catch (IOException e) {
            logger.error(e.getMessage());
            // e.printStackTrace();
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
