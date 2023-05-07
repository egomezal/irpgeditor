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

public class CopyMember implements CopyRequest {
  Member member;

  public CopyMember(Member member) {
    this.member = member;
  }

  @Override
  public void copyTo(AS400System as400, String library, String file) throws Exception {
	  this.member.copyTo(as400, library, file, this.member.getName(), null);
  }

  @Override
  public void copyTo(AS400System as400, String library) throws Exception {
  //Compatibilidad de interfase
  }
}
