package org.egomez.irpgeditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BindingDirectoryEntry {
  BindingDirectory bd;
  String library, name, type;
  
  public BindingDirectoryEntry(BindingDirectory bd, String library, String name, String type) {
    this.bd = bd;
    this.library = library;
    this.name = name;
    this.type = type;
  }
  
  public BindingDirectory getBindingDirectory() {
    return bd;
  }
  
  public String getLibrary() {
    return library;
  }
  
  public String getName() {
    return name;
  }
  
  public String getType() {
    return type;
  }
}
