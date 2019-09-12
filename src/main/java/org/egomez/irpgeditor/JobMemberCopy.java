/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.egomez.irpgeditor;

import javax.swing.SwingUtilities;
import org.egomez.irpgeditor.event.ListenerMemberCreated;
import org.egomez.irpgeditor.event.ListenerSubmitJob;

/**
 *
 * @author CORTIZ
 */
public class JobMemberCopy implements ListenerSubmitJob, Runnable {

    private final String fileTo;
    private final String libraryTo;
    private final ListenerMemberCreated listener;
    private final String memberTo;
    private final AS400System systemTo;

    @Override
    public void jobCompleted(SubmitJob submitJob) {
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        if (listener != null) {
            listener.memberCreated(new Member(systemTo, libraryTo, fileTo, memberTo));
        }
    }

    public JobMemberCopy(AS400System systemTo, String libraryTo, String fileTo, String memberTo,
            ListenerMemberCreated listener) {
        super();
        this.systemTo = systemTo;
        this.libraryTo = libraryTo;
        this.fileTo = fileTo;
        this.memberTo = memberTo;
        this.listener = listener;
    }
}
