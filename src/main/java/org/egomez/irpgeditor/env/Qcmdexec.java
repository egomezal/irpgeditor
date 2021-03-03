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
import java.awt.Color;

import org.egomez.irpgeditor.AS400System;
import org.egomez.irpgeditor.event.ListenerSubmitJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.*;
import java.io.IOException;

/**
 * All commands issued on a system should show the commands issued here. Another
 * class will handle displaying the messages.
 *
 * @author not attributable
 */
public class Qcmdexec {

    QcmdexecOutput output;
    final Logger logger = LoggerFactory.getLogger(Qcmdexec.class);

    public void append(AS400Message[] messages, Color color, boolean result) {
        for (AS400Message message : messages) {
            appendLine(message.getText(), color);
            if (!result)
                try {
                    message.load();
                    appendLine(message.getHelp(), color);
                } catch (AS400SecurityException | ErrorCompletingRequestException | ObjectDoesNotExistException | IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
        }
    }

    public void append(String text, Color color) {
        this.output.append(text, color);
        focus();
    }

    public void submitJob(AS400System system, String command,
            ListenerSubmitJob listener) {
        this.output.submitJob(system, command, listener);
    }

    public void submitJob(AS400System system, String command) {
        this.output.submitJob(system, command, null);
    }

    public void setOutput(QcmdexecOutput o) {
        output = o;
    }

    public void clear() {
        output.clear();
        focus();
    }

    public void appendLine(String text, Color color) {
        append(text, color);
        append("\n", color);
    }

    public void focus() {
        output.focus();
    }

}
