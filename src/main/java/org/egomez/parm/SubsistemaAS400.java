package org.egomez.parm;

import java.util.ArrayList;

import com.ibm.as400.access.Job;

public class SubsistemaAS400 {
	private Job subsistema;
	private ArrayList<Job> listaJob;

	public void setListaJob(ArrayList<Job> listaJob) {
		this.listaJob = listaJob;
	}

	public ArrayList<Job> getListaJob() {
		return listaJob;
	}

	public void setSubsistema(Job subsistema) {
		this.subsistema = subsistema;
	}

	public Job getSubsistema() {
		return subsistema;
	}
}
