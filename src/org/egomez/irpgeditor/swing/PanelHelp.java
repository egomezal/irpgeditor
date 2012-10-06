package org.egomez.irpgeditor.swing;

import java.io.*;
import java.net.*;
import javax.swing.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.help.*;

import java.awt.*;

/**
 * @author not attributable
 */

public class PanelHelp extends PanelTool implements ClosableTab {
  HelpRequest helpRequest;
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JEditorPane editorpaneHelp = new JEditorPane();
  
  public PanelHelp() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    editorpaneHelp.setText("Loading help...");
    this.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(editorpaneHelp, null);
  }
  
  public void setHelpRequest(HelpRequest helpRequest) {
    this.helpRequest = helpRequest;
    setName(helpRequest.getWord());
    URL url = Help.getURL(helpRequest.getWord());
    if ( url == null ) {
      editorpaneHelp.setText("Unable to find help for " + helpRequest.getWord());
    }
    else {
      try {
        editorpaneHelp.setPage(url);
      }
      catch (IOException e) {
        editorpaneHelp.setText(e.getMessage());
      }
    }
  }
  
  /**
   * gets called when then user clicks on the x icon.
   * so, clicking on the x doesnt remove it from the tabbedpane.
   */
  public void closeTab() {
    Environment.toolManager.close(helpRequest, false);
  }
}
