package org.egomez.irpgeditor.env;

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

import java.io.*;
import java.util.*;

import org.egomez.irpgeditor.*;
import org.egomez.irpgeditor.event.*;

/**
 *
 * @author Derek Van Kooten.
 */
public class AS400Systems {
  // static global members.
  // keep a list of systems defined, and notify listeners when more are created, or removed or changed.
  private ArrayList listSystems = new ArrayList();
  private ArrayList listSystemsListeners = new ArrayList();
  private AS400System systemDefault = null;

  public void addSystem(AS400System system) {
    listSystems.add(system);
    // notify listenenrs.
    fireAddedSystem(system);
  }

  public void removeSystem(AS400System system) {
    listSystems.remove(system);
    // notify listeners.
    fireRemovedSystem(system);
  }

  public void setDefault(AS400System system) {
    systemDefault = system;
    // notify listeners.
    fireDefaultSystem(system);
  }

  public AS400System getDefault() {
    return systemDefault;
  }

  public ArrayList getSystems() {
    return listSystems;
  }

  /**
   * loads the system settings.
   */
  public void loadSettings() throws IOException {
    FileInputStream fis;
    Properties props;
    int systemCount;
    AS400System system;
    String defaultName, name, address, user, password;
    File file;

    file = new File(System.getProperty("user.home") + File.separator + "systems.properties");
    if ( file.exists() == false ) {
      return;
    }
    fis = new FileInputStream(file);
    props = new Properties();
    props.load(fis);
    if ( props.getProperty("system.count") == null ) {
      fis.close();
      return;
    }
    defaultName = props.getProperty("system.default");
    systemCount = Integer.parseInt(props.getProperty("system.count"));
    for ( int x = 0; x < systemCount; x++ ) {
      name = props.getProperty("system." + x + ".name");
      address = props.getProperty("system." + x + ".address");
      user = props.getProperty("system." + x + ".user");
      password = props.getProperty("system." + x + ".password");
      system = new AS400System(name, address, user, password);
      system.attemptConnect();
      addSystem(system);
      if ( name.equalsIgnoreCase(defaultName) ) {
        setDefault(system);
      }
    }
    fis.close();
  }

  /**
   * saves the system settings.
   */
  public void saveSettings() throws IOException {
    FileOutputStream fos;
    Properties props;
    AS400System system;

    props = new Properties();
    props.setProperty("system.count", Integer.toString(listSystems.size()));
    system = getDefault();
    if ( system != null ) {
      props.setProperty("system.default", system.getName());
    }
    for ( int x = 0; x < listSystems.size(); x++ ) {
      system  = (AS400System)listSystems.get(x);
      props.setProperty("system." + x + ".name", system.getName());
      props.setProperty("system." + x + ".address", system.getAddress());
      props.setProperty("system." + x + ".user", system.getUser());
      props.setProperty("system." + x + ".password", system.getPassword());
    }
    fos = new FileOutputStream(System.getProperty("user.home") + File.separator + "systems.properties");
    props.store(fos, "");
  }

  /**
   * listen for when systems are created, removed, or changed.
   * 
   * @param object Object
   */
  public void addListener(ListenerAS400Systems listener) {
    listSystemsListeners.add(listener);
  }

  public void removeListener(ListenerAS400Systems listener) {
    listSystemsListeners.remove(listener);
  }

  /**
   * notifies listeners that a system was added.
   * 
   * @param system AS400System
   */
  protected void fireAddedSystem(AS400System system) {
    Object[] temp;

    temp = listSystemsListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerAS400Systems)temp[x]).addedSytem(system);
    }
  }

  /**
   * notifies listeners that a system was removed.
   * 
   * @param system AS400System
   */
  protected void fireRemovedSystem(AS400System system) {
    Object[] temp;

    temp = listSystemsListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerAS400Systems)temp[x]).removedSytem(system);
    }
  }

  /**
   * notifies listeners of a default system.
   * 
   * @param system AS400System
   */
  protected void fireDefaultSystem(AS400System system) {
    Object[] temp;

    temp = listSystemsListeners.toArray();
    for ( int x = 0; x < temp.length; x++ ) {
      ((ListenerAS400Systems)temp[x]).defaultSytem(system);
    }
  }
}

