package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.*;

/**
 * @author not attributable
 */
public interface ListenerParserDirty {
  public void parserDirty(SourceParser parser, boolean dirty);
}
