/**
 * 
 */
package org.egomez.irpgeditor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.egomez.parm.ArrayNode;
import org.egomez.parm.SubsistemaAS400;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * @author Edwin Gomez Almestar
 *
 */
public class TreeJob extends Thread {
	private SubsistemaAS400 x = null;
	private ArrayNode root = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	Logger logger = LoggerFactory.getLogger(TreeJob.class);

	public TreeJob(SubsistemaAS400 subsystem, ArrayNode node) {
		root = node;
		x = subsystem;
	}

	public void run() {
		agregarSubsistema();
	}

	private void agregarSubsistema() {
		try {

			ArrayNode node = new ArrayNode(new Object[] { x.getSubsistema().getName(), x.getSubsistema().getUser(),
					x.getSubsistema().getType(), x.getSubsistema().getCPUUsed() / 1000, "",
					x.getSubsistema().getStatus(), "", "" });
			node.setAllowsChildren(true);

			Job j = null;
			if (x.getListaJob() != null) {
				Iterator<Job> listaJob = x.getListaJob().iterator();
				while (listaJob.hasNext()) {
					j = listaJob.next();
					agregarTrabajo(j, node);
				}
			}
			root.add(node);
		} catch (AS400SecurityException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ErrorCompletingRequestException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ObjectDoesNotExistException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void agregarTrabajo(Job j, ArrayNode parent) {
		// GridItem item2 = new GridItem(parent, SWT.NONE);
		// TreeItem item2 = new TreeItem(parent, SWT.NONE);
		try {
			parent.add(new ArrayNode(new Object[] { j.getName(), j.getUser(), j.getType(), j.getCPUUsed() / 1000,
					j.getRunPriority(), j.getStatus(), sdf.format(j.getJobEnterSystemDate()), j.getNumber() }));
		} catch (AS400SecurityException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ErrorCompletingRequestException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ObjectDoesNotExistException e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}
