package org.egomez.irpgeditor.swing;

import javax.swing.*;
import java.awt.event.*;

/**
 * listens for mouse clicks and shows the popup if needed.
 */
public class PopupListener extends MouseAdapter {
  JPopupMenu popup;

  public PopupListener(JPopupMenu menu) {
    popup = menu;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  private void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
