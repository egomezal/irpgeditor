package org.egomez.irpgeditor.env;

import java.awt.Color;

import org.egomez.irpgeditor.AS400System;
import org.egomez.irpgeditor.event.ListenerSubmitJob;

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

/**
 * Handles showing qcmdexec output.
 * 
 * @author Derek Van Kooten.
 */
public interface QcmdexecOutput {
	public static final Color colorCall = Color.BLACK;
	public static final Color colorResult = Color.BLUE;

	public void clear();

	public abstract void append(String paramString, Color paramColor);

	public void focus();
	
	 public abstract void submitJob(AS400System paramAS400System, String paramString, ListenerSubmitJob paramListenerSubmitJob);
}
