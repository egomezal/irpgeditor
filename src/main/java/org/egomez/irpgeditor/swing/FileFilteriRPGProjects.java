package org.egomez.irpgeditor.swing;

import java.io.*;

/**
 * used for selecting project files.
 */
public class FileFilteriRPGProjects extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        String name;

        if (f.isDirectory()) {
            return true;
        }
        name = f.getName();
        return name.endsWith(".prj");
    }

    @Override
    public String getDescription() {
        return "iRPGEditor Project Files";
    }
}
