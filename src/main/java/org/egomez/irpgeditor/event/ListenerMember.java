package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.*;

/**
 * @author not attributable
 */
public interface ListenerMember {
  public void memberChanged(Member member);
  // return true if it is ok to close, otherwise return a false.
  public boolean isOkToClose(Member member);
}
