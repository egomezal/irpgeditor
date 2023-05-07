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
import javax.swing.*;

import org.egomez.irpgeditor.icons.*;

/**
 * splash screen.
 *
 * @author Derek Van Kooten.
 */
public class WindowSplash extends Window {

    /**
     *
     */
    private static final long serialVersionUID = 5649928531370070037L;
    ImageIcon iconFrame = new ImageIcon(Icons.class.getResource("Splash.gif"));

    public WindowSplash(Frame owner) {
        super(owner);
        setSize(iconFrame.getIconWidth(), iconFrame.getIconHeight());
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle winDim = getBounds();
        setLocation((screenDim.width - winDim.width) / 2, (screenDim.height - winDim.height) / 2);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(iconFrame.getImage(), 0, 0, this);
    }
}
