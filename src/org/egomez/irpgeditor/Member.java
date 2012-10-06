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

import java.io.*;
import java.sql.*;
import java.util.*;

import org.egomez.irpgeditor.env.*;
import org.egomez.irpgeditor.event.*;

import com.ibm.as400.data.*;

/**
 * Stores information about a source member on the as400.
 * 
 * @author Derek Van Kooten.
 */
public class Member {
	private static int alias = 0;
	AS400System as400system;
	String library;
	String file;
	String member;
	String sourceType = "UNKNOWN";
	String description;
	String created;
	String changed;
	int copyID;
	ArrayList listListeners = new ArrayList();

	public static String SOURCE_TYPE_DSPF = "DSPF";
	public static String SOURCE_TYPE_RPG = "RPG";
	public static String SOURCE_TYPE_RPGLE = "RPGLE";
	public static String SOURCE_TYPE_SQLRPGLE = "SQLRPGLE";
	public static String SOURCE_TYPE_CLP = "CLP";
	public static String SOURCE_TYPE_CLLE = "CLLE";
	public static String SOURCE_TYPE_PRTF = "PRTF";
	public static String SOURCE_TYPE_PF = "PF";
	public static String SOURCE_TYPE_LF = "LF";

	public Member(AS400System as400system, String library, String file,
			String member) {
		this.as400system = as400system;
		this.library = library.trim().toUpperCase();
		this.file = file.trim().toUpperCase();
		this.member = member.trim().toUpperCase();
		getInfo();
	}

	public Member(AS400System as400system, String library, String file,
			String member, String sourceType, String description,
			String created, String changed) {
		this.as400system = as400system;
		this.library = library.trim().toUpperCase();
		this.file = file.trim().toUpperCase();
		this.member = member.trim().toUpperCase();
		this.sourceType = sourceType.trim();
		this.description = description.trim();
		this.created = created.trim();
		this.changed = changed.trim();
	}

	public AS400System getSystem() {
		return as400system;
	}

	public String getLibrary() {
		return library;
	}

	public String getFile() {
		return file;
	}

	public String getName() {
		return member;
	}

	public void convertRPG4() throws Exception {
		as400system.call("QSYS/RMVM FILE(QGPL/QRPGLESRC) MBR(" + getName()
				+ ")");
		as400system.call("QSYS/CVTRPGSRC FROMFILE(" + getLibrary() + "/"
				+ getFile() + ") FROMMBR(" + getName()
				+ ") TOFILE(QGPL/QRPGLESRC) TOMBR(" + getName() + ")");
		as400system
				.call("CPYF FROMFILE(QGPL/QRPGLESRC) TOFILE(" + getLibrary()
						+ "/" + getFile() + ") FROMMBR(" + getName()
						+ ") TOMBR(" + getName()
						+ ") MBROPT(*REPLACE) CRTFILE(*YES) FMTOPT(*NOCHK)");
		setSourceType("RPGLE");
	}

	public void setName(String name) throws Exception {
		if (name == null || name.trim().length() == 0) {
			return;
		}
		if (name.trim().length() > 10) {
			return;
		}
		if (name.equalsIgnoreCase(member)) {
			return;
		}
		as400system.call("RNMM FILE(" + getLibrary() + "/" + getFile()
				+ ") MBR(" + getName() + ") NEWMBR(" + name.trim() + ")");
		this.member = name;
		fireChanged();
	}

	public void delete() throws Exception {
		as400system.call("RMVM FILE(" + getLibrary() + "/" + getFile()
				+ ") MBR(" + getName() + ")");
	}

	public void setSourceType(String sourceType) throws Exception {
		as400system.call("CHGPFM FILE(" + getLibrary() + "/" + getFile()
				+ ") MBR(" + getName() + ") SRCTYPE(" + sourceType + ")");
		getInfo();
		fireChanged();
	}

	public void copyTo(AS400System systemTo, String libraryTo, String fileTo,
			String memberTo) throws Exception {
		// if copying to different system.
		if (as400system.equals(systemTo) == false) {
			systemTo.call("CRTDDMF FILE(QTEMP/DDMTEMP) RMTFILE(" + this.library
					+ "/" + this.file + ") RMTLOCNAME('" + as400system.address
					+ "' *IP)");
			systemTo.call("CPYF FROMFILE(QTEMP/DDMTEMP) TOFILE(" + libraryTo
					+ "/" + fileTo + ") CRTFILE(*YES) FMTOPT(*NOCHK) "
					+ "FROMMBR(" + this.member + ") TOMBR(" + memberTo
					+ ") MBROPT(*REPLACE)");
		} else {
			as400system.call("CPYF FROMFILE(" + this.library + "/" + this.file
					+ ") TOFILE(" + libraryTo + "/" + fileTo
					+ ") CRTFILE(*YES) FMTOPT(*NOCHK) " + "FROMMBR("
					+ this.member + ") TOMBR(" + memberTo
					+ ") MBROPT(*REPLACE)");
		}
	}

	public String getSourceType() {
		return sourceType;
	}

	public String getDescription() {
		return description;
	}

	public String getCreated() {
		return created;
	}

	public String getChanged() {
		return changed;
	}

	public boolean nameEquals(String name) {
		if (name == null) {
			return false;
		}
		if (name.equalsIgnoreCase(this.member)) {
			return true;
		}
		int index = name.indexOf("/");
		if (index == -1) {
			return false;
		}
		if (name.substring(index + 1).equalsIgnoreCase(this.member)) {
			return true;
		}
		return false;
	}

	public void dispose() {
		as400system = null;
		library = null;
		file = null;
		member = null;
		sourceType = null;
		description = null;
		created = null;
		changed = null;
	}

	public void getInfo() {
		ProgramCallDocument pcml;
		boolean result;
		String buffer;

		buffer = file;
		while (buffer.length() < 10) {
			buffer = buffer + " ";
		}
		buffer = buffer + library;
		while (buffer.length() < 20) {
			buffer = buffer + " ";
		}
		try {
			pcml = new ProgramCallDocument(as400system.as400, "api");
			pcml.setValue("qusrmbrd.receiverLength",
					new Integer(pcml.getOutputsize("qusrmbrd.receiver")));
			pcml.setValue("qusrmbrd.fileName", "" + buffer);
			pcml.setValue("qusrmbrd.memberName", member);
			result = pcml.callProgram("qusrmbrd");
			if (result == false) {
				Environment.qcmdexec.append(pcml.getMessageList("qusrmbrd"));
			} else {
				sourceType = pcml.getValue("qusrmbrd.receiver.sourceType")
						.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getSource(SourceLoader sourceLoader) throws SQLException {
		copyID = as400system.getSource(library, file, member, sourceLoader);
	}

	public String saveBackup(SourceParser parser) throws Exception {
		FileOutputStream fos;
		File file;

		file = new File("./backup.txt");
		fos = new FileOutputStream(file);
		fos.write(parser.getDocument()
				.getText(0, parser.getDocument().getLength()).getBytes());
		fos.close();
		return file.getAbsolutePath();
	}

	public void save(SourceParser parser, ListenerSave listener)
			throws SQLException {
		Connection connection;
		Statement stmt;
		StringBuffer buffer;
		int row, date, today;
		Calendar cal;
		SourceLine line;
		String backup = null;
		int a;

		connection = as400system.getConnection();
		stmt = connection.createStatement();

		if (file.equals("QRPGLESRC")) {
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/DLTF FILE(QTEMP/SRCUPLOAD)"));
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/CRTSRCPF FILE(QTEMP/SRCUPLOAD) RCDLEN(112)"));
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/ADDPFM FILE(QTEMP/SRCUPLOAD) MBR(SOURCE)"));
		} else {
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/DLTF FILE(QTEMP/SRCUPLOAD)"));
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/CRTSRCPF FILE(QTEMP/SRCUPLOAD)"));
			stmt.execute(as400system
					.buildSqlForCmd("QSYS/ADDPFM FILE(QTEMP/SRCUPLOAD) MBR(SOURCE)"));
		}

		saveBulk(parser, listener);

		/*
		 * synchronized ( as400system ) { alias++; a = alias; } try { backup =
		 * "Backup file saved to: " + saveBackup(parser); cal =
		 * Calendar.getInstance(); today = (cal.get(Calendar.YEAR) - 2000) *
		 * 10000; today += ((cal.get(Calendar.MONTH) + 1) * 100); today +=
		 * cal.get(Calendar.DAY_OF_MONTH); connection =
		 * as400system.getConnection(); synchronized (connection) { stmt =
		 * connection.createStatement(); stmt.execute("create alias qtemp/a" + a
		 * + " for " + library + "/" + file + "(" + member + ")");
		 * stmt.execute("delete from qtemp/a" + a); buffer = new StringBuffer();
		 * line = parser.first; row = 1; while ( line != null ) { format(line,
		 * buffer); date = line.date; if ( line.changed || line.created ) { date
		 * = today; } line.changed = false; line.created = false; line.date =
		 * date; stmt.execute("insert into qtemp/a" + a + " values(" + row +
		 * ", " + date + ", '" + buffer + "')"); row++; if ( listener != null )
		 * { listener.lineSaved(row); } line = line.getNext(); }
		 * stmt.execute("drop alias qtemp/a" + a); stmt.close(); } if ( listener
		 * != null ) { listener.saveComplete(row, true, backup); }
		 * setDirty(false); } catch (Exception e) { e.printStackTrace(); if (
		 * listener != null ) { listener.saveComplete(0, false, e.getMessage() +
		 * "\n" + backup); } }
		 */
	}

	public void saveBulk(SourceParser parser, ListenerSave listener)
			throws SQLException {
		Connection connection;
		Statement stmt;
		int today, date, row;
		Calendar cal;
		SourceLine line;
		StringBuffer buffer = new StringBuffer();
		String backup = null, append = " ";

		// test length of lines first.
		row = 1;
		line = parser.getFirst();
		while (line != null) {
			// 81 because the cr/lf is counted.
			if (file.equals("QRPGLESRC")) {
				if (line.getText().length() > 100) {
					if (listener != null) {
						listener.saveComplete(0, false, "Line number: " + row
								+ " is over 100 characters.");
					}
					return;
				}
			} else {
				if (line.getText().length() > 81) {
					if (listener != null) {
						listener.saveComplete(0, false, "Line number: " + row
								+ " is over 80 characters.");
					}
					return;
				}
			}
			row++;
			line = line.getNext();
		}

		try {
			backup = "Backup file saved to: " + saveBackup(parser);
			row = 0;
			cal = Calendar.getInstance();
			today = (cal.get(Calendar.YEAR) - 2000) * 10000;
			today += ((cal.get(Calendar.MONTH) + 1) * 100);
			today += cal.get(Calendar.DAY_OF_MONTH);
			connection = as400system.getConnection();

			synchronized (connection) {
				stmt = connection.createStatement();
				// Para fuentes RPG o SQLRPG

				/*
				 * try { stmt.execute("drop alias qtemp/srcupload"); } catch
				 * (Exception e2 ) { }
				 */
				// stmt.execute("create alias qtemp/srcupload for " + library +
				// "/" + file + "(" + member + ")");
				line = parser.getFirst();
				while (line != null) {
					buffer.append(line.getText());
					date = line.date;
					if (line.changed || line.created) {
						date = today;
					}
					line.changed = false;
					line.created = false;
					line.date = date;
					buffer.append(line.date);
					buffer.append("\n");
					row++;
					if (buffer.length() > 32000) {
						stmt.execute("call qgpl/prcupload('"
								+ buffer.toString().replaceAll("'", "''")
								+ "',  '\n', '" + append + "')");
						append = "T";
						buffer = new StringBuffer();
						if (listener != null) {
							listener.lineSaved(row);
						}
					}
					line = line.getNext();
				}
				// if append is blank then nothing has been uploaded, must
				// upload
				// an empty string at least to delete the previous contents.
				if (buffer.length() > 0 || append.equalsIgnoreCase(" ")) {
					// System.out.println("call qgpl/prcupload('" +
					// buffer.toString().replaceAll("'", "''") +
					// "',  X'0D25', '" + append + "')");
					stmt.execute("call qgpl/prcupload('"
							+ buffer.toString().replaceAll("'", "''")
							+ "',  '\n', '" + append + "')");
				}
				/*
				 * try { stmt.execute("drop alias qtemp/srcupload"); } catch
				 * (Exception e2) { }
				 */
				stmt.execute(as400system
						.buildSqlForCmd("CPYF FROMFILE(QTEMP/SRCUPLOAD) TOFILE("
								+ library
								+ "/"
								+ file
								+ ") TOMBR("
								+ member
								+ ") MBROPT(*REPLACE) FMTOPT(*MAP *DROP)"));
				stmt.close();
			}
			if (listener != null) {
				listener.saveComplete(row, true, backup);
			}
			parser.setDirty(false);
		} catch (Exception e) {
			System.out.println("buffer: ("
					+ buffer.toString().replaceAll("'", "''") + ")");
			e.printStackTrace();
			if (listener != null) {
				listener.saveComplete(0, false, e.getMessage() + "\n" + backup);
			}
		}
	}

	public void asaveNew(SourceParser parser, ListenerSave listener)
			throws Exception {
		Connection connection;
		Statement stmt;
		StringBuffer buffer;
		int date, today;
		Calendar cal;
		SourceLine line;
		boolean changed;
		int a;

		saveBackup(parser);
		changed = false;
		cal = Calendar.getInstance();
		today = (cal.get(Calendar.YEAR) - 2000) * 10000;
		today += ((cal.get(Calendar.MONTH) + 1) * 100);
		today += cal.get(Calendar.DAY_OF_MONTH);
		connection = as400system.getConnection();
		synchronized (connection) {
			stmt = connection.createStatement();
			buffer = new StringBuffer();
			// deletes.
			for (int x = 0; x < parser.listDeleted.size(); x++) {
				line = (SourceLine) parser.listDeleted.get(x);
				if (line.created == false) {
					stmt.execute("delete from qtemp/c" + copyID
							+ " where srcseq = " + line.number);
				}
			}
			line = parser.first;
			// inserts and changes.
			while (line != null) {
				date = line.date;
				if (line.created) { // new line.
					format(line, buffer);
					date = today;
					// calculate the row for this new line.
					getRow(line, stmt, today, buffer);
					stmt.execute("insert into qtemp/c" + copyID
							+ "(srcseq, srcdat, srcdta) values(" + line.number
							+ ", " + date + ", '" + buffer + "')");
					if (listener != null) {
						listener.lineSaved(1);
					}
					changed = true;
				} else if (line.changed) { // changed line.
					format(line, buffer);
					date = today;
					stmt.execute("update qtemp/c" + copyID + " set srcdta = '"
							+ buffer + "', srcdat = " + date
							+ " where srcseq = " + line.number);
					if (listener != null) {
						listener.lineSaved(1);
					}
					changed = true;
				}
				line.changed = false;
				line.created = false;
				line.date = date;
				line = line.getNext();
			}
			if (changed || parser.listDeleted.size() > 0) {
				synchronized (as400system) {
					alias++;
					a = alias;
				}
				stmt.execute("create alias qtemp/a" + a + " for " + library
						+ "/" + file + "(" + member + ")");
				stmt.execute("delete from qtemp/a" + a);
				stmt.execute("insert into qtemp/a" + a
						+ " select * from qtemp/c" + copyID
						+ " order by srcseq");
				stmt.execute("drop alias qtemp/a" + a);
			}
			stmt.close();
		}
		if (listener != null) {
			listener.saveComplete(1, true, "");
		}
		parser.listDeleted.clear();
		parser.setDirty(false);
	}

	/**
	 * gets the row number for the line, if other lines need to be moved, then
	 * it moves them and updates them too.
	 * 
	 * @param line
	 *            RPGSourceLine
	 * @param stmt
	 *            Statement
	 * @throws SQLException
	 */
	private void getRow(SourceLine line, Statement stmt, int today,
			StringBuffer buffer) throws SQLException {
		int previousRow, nextRow;
		float row;

		if (line.parent == null) {
			previousRow = 0;
		} else {
			previousRow = (int) (line.parent.number * 100);
		}
		if (line.next == null) {
			row = (float) (previousRow + 100);
			row = (row / 100);
			line.number = row;
		} else {
			nextRow = (int) (line.next.number * 100);
			if (line.next.created) {
				// the next row is a new row.
				// set the row for the current line before doing the next line.
				row = (float) (previousRow + 100);
				row = (row / 100);
				line.number = row;
				// calculate the row for the next new line.
				getRow(line.next, stmt, today, buffer);
				format(line.next, buffer);
				line.next.date = today;
				stmt.execute("insert into qtemp/c" + copyID
						+ "(srcseq, srcdat, srcdta) values(" + line.next.number
						+ ", " + today + ", '" + buffer + "')");
				line.next.changed = false;
				line.next.created = false;
			} else if ((nextRow - previousRow) > 1) {
				row = (float) (previousRow + ((nextRow - previousRow) / 2));
				row = (row / 100);
				line.number = row;
			} else {
				// the difference between these two rows is not enought to put a
				// line inbetween
				// need to move the other line forward.
				// take over the next lines number then move the next line
				// forward.
				if (nextRow > previousRow) {
					row = nextRow;
				} else {
					row = (float) (previousRow + 100);
				}
				row = (row / 100);
				line.number = row;
				getRow(line.next, stmt, today, buffer);
				// if this line has changed, then we should go ahead and update
				// the changes also instead of doing another
				// update again later for the source changes.
				if (line.next.changed) {
					format(line.next, buffer);
					line.next.date = today;
					stmt.execute("update qtemp/c" + copyID + " set srcseq = "
							+ line.next.number + ", srcdta = '" + buffer
							+ "', srcdat = " + today + " where srcseq = " + row);
					line.next.changed = false;
				} else {
					stmt.execute("update qtemp/c" + copyID + " set srcseq = "
							+ line.next.number + " where srcseq = " + row);
				}
			}
		}
	}

	/**
	 * replaces the ' character with a ''.
	 */
	public void format(SourceLine line, StringBuffer buffer) {
		int index;

		try {
			buffer.replace(
					0,
					buffer.length(),
					line.parser.getText(line.start, line.start + line.length
							- 1));
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("buffer: " + buffer + ", "
					+ buffer.toString().length() + ", " + line.start + ", "
					+ line.length + ", " + line.parser.length());
			e.printStackTrace();
			throw e;
		}
		if (buffer.length() > 80) {
			buffer.delete(80, buffer.length() - 1);
		}
		index = buffer.indexOf("'");
		while (index > -1) {
			buffer.insert(index, "'");
			index = buffer.indexOf("'", index + 2);
		}
		while (buffer.length() < 80) {
			buffer.append(" ");
		}
	}

	public int hashCode() {
		return member.hashCode();
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof Member) {
			Member member;

			member = (Member) object;
			if (member.member.trim().equalsIgnoreCase(this.member.trim()) == false) {
				return false;
			}
			if (member.file.trim().equalsIgnoreCase(file.trim()) == false) {
				return false;
			}
			if (member.library.trim().equalsIgnoreCase(library.trim()) == false) {
				return false;
			}
			if (member.as400system.getName().trim()
					.equalsIgnoreCase(as400system.getName().trim()) == false) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void addListener(ListenerMember listener) {
		listListeners.add(listener);
	}

	public void removeListener(ListenerMember listener) {
		listListeners.remove(listener);
	}

	public void fireChanged() {
		ListenerMember[] temp;

		temp = (ListenerMember[]) listListeners
				.toArray(new ListenerMember[listListeners.size()]);
		for (int x = 0; x < temp.length; x++) {
			temp[x].memberChanged(this);
		}
	}

	/**
	 * returns true if it is ok to close the member. otherwise returns a false.
	 * 
	 * @return boolean
	 */
	public boolean isOkToClose() {
		ListenerMember[] temp;

		temp = (ListenerMember[]) listListeners
				.toArray(new ListenerMember[listListeners.size()]);
		for (int x = 0; x < temp.length; x++) {
			if (temp[x].isOkToClose(this) == false) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return member;
	}
}
