package org.egomez.irpgeditor.swing;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * watches for specific short cut keys.
 * 
 * @author Derek Van Kooten.
 */
public class HandlerKeyPressed extends KeyAdapter implements Runnable {
  JScrollBar scrollbar;
  JTextComponent textarea;
  int direction = 0; // 1 = up, 2 = down.
  Thread thread;
  Logger logger = LoggerFactory.getLogger(HandlerKeyPressed.class);
		  
  /**
   * call this AFTER jbInit() so that the parent components of
   * the textarea are already associated.
   */
  public HandlerKeyPressed(JTextComponent textarea) {
    this.textarea = textarea;
    textarea.addKeyListener(this);
    unregister();
    setIncrement();
  }
  
  public void dispose() {
    scrollbar = null;
    if ( textarea != null ) {
      textarea.removeKeyListener(this);
    }
    textarea = null;
    thread = null;
    direction = 0;
  }
  
  public void keyPressed(KeyEvent e) {
    int code;
    
    if ( e.getModifiers() == KeyEvent.CTRL_MASK ) {
      code = e.getKeyCode();
      if ( code == 38 ) { // UP ARROW
        direction = 1;
        getScrollBar();
        startScroll();
      }
      else if ( code == 40 ) { // DOWN ARROW
        direction = 2;
        getScrollBar();
        startScroll();
      }
    }
  }
  
  public void keyReleased(KeyEvent e) {
    synchronized ( this ) {
      direction = 0;
    }
  }
  
  public void startScroll() {
    if ( thread == null ) {
      thread = new Thread(this);
      thread.start();
    }
  }
  
  @SuppressWarnings("static-access")
public void run() {
    boolean loop = true;
    
    try {
      // scroll one time.
      scroll();
      for ( int x = 0; x < 10; x++ ) {
        synchronized ( this ) {
          if ( direction == 0 ) {
            thread = null;
            return;
          }
        }
        Thread.currentThread().sleep(50);
      }
      while ( loop ) {
        scroll();
        synchronized ( this ) {
          if ( direction == 0 ) {
            thread = null;
            return;
          }
        }
				Thread.currentThread().sleep(50);
        synchronized ( this ) {
          if ( direction == 0 ) {
            thread = null;
            return;
          }
        }
      }
    }
    catch (Exception e) {
      //e.printStackTrace();
    	logger.error(e.getMessage());
    }
    thread = null;
  }
  
  private void scroll() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if ( scrollbar == null ) {
          return;
        }
        if ( direction == 1 ) {
          scrollbar.setValue(scrollbar.getValue() - scrollbar.getUnitIncrement());
        }
        else {
          scrollbar.setValue(scrollbar.getValue() + scrollbar.getUnitIncrement());
        }
      }
    });
  }
  
  /**
   * finds the scrollbar to use.
   */
  private void getScrollBar() {
    Container container;
    
    container = textarea.getParent();
    while ( container != null ) {
      if ( container instanceof JScrollPane ) {
        scrollbar = ((JScrollPane)container).getVerticalScrollBar();
        return;
      }
      container = container.getParent();
    }
  }
  
  public void setIncrement() {
    Font font;
    FontMetrics fm;
    
    getScrollBar();
    if ( scrollbar == null ) {
      return;
    }
    font = textarea.getFont();
    fm = textarea.getFontMetrics(font);
    scrollbar.setUnitIncrement(fm.getMaxAscent() + fm.getDescent());
  }
  
  public void unregister() {
    InputMap im;
    KeyStroke[] keys;
    JComponent component;
    Container container;
    
    component = textarea;
    while ( component != null ) {
      for ( int c = 0; c < 3; c++ ) {
        im = component.getInputMap(c);
        keys = im.allKeys();
        if ( keys == null ) {
          continue;
        }
        for ( int x = 0; x < keys.length; x++ ) {
          if ( keys[x].getKeyCode() == 38 && (keys[x].getModifiers() & KeyEvent.CTRL_MASK) > 0 ) {
            im = component.getInputMap(c);
            while ( im != null ) {
              im.remove(keys[x]);
              im = im.getParent();
            }
          }
        }
      }
      container = component.getParent();
      if ( container instanceof JComponent ) {
        component = (JComponent)container;
      }
      else {
        return;
      }
    }
  }
}

