package org.egomez.irpgeditor.env;

import org.egomez.irpgeditor.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CopyFile implements CopyRequest {
  AS400System as400;
  String lib, file;

  public CopyFile(AS400System as400, String lib, String file) {
    this.as400 = as400;
    this.lib = lib;
    this.file = file;
  }
  public void copyTo(AS400System as400, String library, String file) throws Exception {
   // Para implementacion de interfase
  }

  public void copyTo(AS400System as400, String library) throws Exception {
	  this.as400.copyTo(this.lib, this.file, as400, library, this.file, true, null);
  }
}
