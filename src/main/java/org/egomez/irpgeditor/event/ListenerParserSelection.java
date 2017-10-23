package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.*;

/**
 * @author Derek Van Kooten.
 */
public interface ListenerParserSelection {
  /**
   * is called when a line is requesting to be selected.
   * @param sourceLine SourceLine
   */
  public void requestingFocus(SourceLine sourceLine);
  
  public void requestingFocus(SourceBlock sourceBlock);
}
