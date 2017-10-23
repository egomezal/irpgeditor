package org.egomez.irpgeditor;
import org.egomez.irpgeditor.event.ListenerSubmitJob;


public class SubmitJob implements Runnable {

	  String command;
	  boolean completed = false;
	  ListenerSubmitJob listener;
	  boolean result;
	  AS400System system;

	  public SubmitJob(AS400System system, String command, ListenerSubmitJob listener)
	  {
	    this.system = system;
	    this.command = command;
	    this.listener = listener;
	  }

	  protected void completed()
	  {
	    this.completed = true;
	    if (this.listener == null) {
	      return;
	    }
	    new Thread(this).start();
	  }

	  public void execute()
	    throws Exception
	  {
	    this.result = this.system.call(this.command);
	    completed();
	  }

	  public String getCommand()
	  {
	    return this.command;
	  }

	  public AS400System getSystem()
	  {
	    return this.system;
	  }

	  public void run()
	  {
	    this.listener.jobCompleted(this);
	  }

	  public String toString() {
	    return this.command;
	  }
}
