package org.egomez.irpgeditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BindingDirectory {
  AS400System as400;
  String name, library, type;
  
  public BindingDirectory(AS400System as400, String name, String library, String type) {
    this.as400 = as400;
    this.name = name.trim();
    this.library = library.trim();
    this.type = type.trim();
  }
  
  public String getName() {
    return name;
  }
  
  public String getLibrary() {
    return library;
  }
  
  public String getType() {
    return type;
  }
  
  public AS400System getAS400() {
    return as400;
  }
  
  public String toString() {
    return "org.egomez.irpgeditor.BindingDirectory {name: " + name + ", library: " + library + ", type: " + type + "}";
  }
}
