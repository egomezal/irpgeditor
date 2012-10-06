package org.egomez.irpgeditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class Module {
  AS400System as400;
  String library, name;
  String sourceLib, sourceFile, sourceMember;
  
  public Module(AS400System as400, String library, String name, String sourceLib, String sourceFile, String sourceMember) {
    this.as400 = as400;
    this.library = library;
    this.name = name;
    this.sourceFile = sourceFile;
    this.sourceLib = sourceLib;
    this.sourceMember = sourceMember;
  }
  
  public AS400System getSystem() {
    return as400;
  }
  
  public String getLibrary() {
    return library;
  }
  
  public String getName() {
    return name;
  }
  
  public String getSourceLibrary() {
    return sourceLib;
  }
  
  public String getSourceFile() {
    return sourceFile;
  }
  
  public String getSourceMember() {
    return sourceMember;
  }
}
