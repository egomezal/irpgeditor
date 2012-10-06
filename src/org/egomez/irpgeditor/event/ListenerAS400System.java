package org.egomez.irpgeditor.event;

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

import org.egomez.irpgeditor.*;

/**
 * Used by the AS400System object to notify listeners when event happen.
 *
 * @author Derek Van Kooten.
 */
public interface ListenerAS400System {
  /**
   * Is called by the AS400System object when a connection is made.
   */
  public void connected(AS400System system);
  /**
   * Is called by the AS400System object when a disconnect happens.
   */
  public void disconnected(AS400System system);
}
