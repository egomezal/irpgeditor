package org.egomez.irpgeditor.swing;

import java.io.*;

/**
 * used for selecting project files.
 */
public class FileFilteriRPGProjects extends javax.swing.filechooser.FileFilter {
  public boolean accept(File f) {
    String name;

    if ( f.isDirectory() ) {
      return true;
    }
    name = f.getName();
    if ( name.endsWith(".prj") ) {
      return true;
    }
    return false;
  }

  public String getDescription() {
    return "iRPGEditor Project Files";
  }
}
