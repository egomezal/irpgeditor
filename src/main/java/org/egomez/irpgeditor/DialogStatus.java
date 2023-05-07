package org.egomez.irpgeditor;

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
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Derek Van Kooten.
 */
public class DialogStatus extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -1716951681605043935L;
    static DialogStatus dialog = null;
    static Runnable runnable;

    Frame frame;
    JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    JProgressBar progressbar = new JProgressBar();
    static Logger logger = LoggerFactory.getLogger(DialogStatus.class);

    public static void showDialog(final Frame frame, final String message, Runnable runnable) {
        DialogStatus.runnable = runnable;
        if (dialog == null) {
            dialog = new DialogStatus(frame);
        }
        dialog.progressbar.setValue(0);
        dialog.jLabel1.setText(message);
        // center on frame.
        dialog.center();
        dialog.show();
    }

    public static void setStatus(final Frame frame, final String message, final int value) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (dialog == null) {
                dialog = new DialogStatus(frame);
            }
            if (message != null) {
                dialog.jLabel1.setText(message);
            }
            dialog.progressbar.setValue(value);
            dialog.show();
        });
    }

    public static void hideDialog() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                if (dialog == null) {
                    return;
                }
                dialog.progressbar.setValue(100);
                dialog.setVisible(false);

            });
        } catch (InterruptedException | InvocationTargetException e) {

            logger.error(e.getMessage());
        }
    }

    public DialogStatus(Frame frame) {
        super(frame, "iRPGEditor", true);
        this.frame = frame;
        try {
            jbInit();
            pack();
        } catch (Exception ex) {

            logger.error(ex.getMessage());
        }
    }

    void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel1.setText("Message.");
        progressbar.setValue(50);
        getContentPane().add(panel1);
        panel1.add(jLabel1, BorderLayout.CENTER);
        panel1.add(progressbar, BorderLayout.SOUTH);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void show() {
        new Thread(runnable).start();
        super.show();
    }

    /**
     * centers the frame.
     */
    public void center() {
        int x, y;
        Dimension screenSize;
        if (frame == null) {
            x = 0;
            y = 0;
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            x = frame.getX();
            y = frame.getY();
            screenSize = frame.getSize();
        }
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation(x + ((screenSize.width - frameSize.width) / 2), y + ((screenSize.height - frameSize.height) / 2));
    }
}
