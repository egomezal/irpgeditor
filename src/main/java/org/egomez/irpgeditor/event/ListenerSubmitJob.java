package org.egomez.irpgeditor.event;

import org.egomez.irpgeditor.SubmitJob;

public abstract interface ListenerSubmitJob
{
  public abstract void jobCompleted(SubmitJob paramSubmitJob);
}
