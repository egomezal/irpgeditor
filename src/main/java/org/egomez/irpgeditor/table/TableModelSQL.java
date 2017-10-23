package org.egomez.irpgeditor.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

import org.egomez.irpgeditor.AS400System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableModelSQL extends DefaultTableModel implements Runnable {
	private AS400System system;
	private String sql;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4760726822150241337L;
	private static final String[] titulo = new String[] { "CODE", "NAME" };
	private static final ArrayList<Object[]> registros = new ArrayList<Object[]>();
	public ResultSet rs;
	public ResultSetMetaData rmd = null;
	Logger logger = LoggerFactory.getLogger(TableModelSQL.class);

	public int getColumnCount() {
		if (registros != null && registros.size() != 0) {
			Object[] row = (Object[]) registros.get(0);
			return row.length;
		} else {
			return 0;
		}
	}

	public int getRowCount() {
		if (registros != null) {
			return registros.size();
		} else {
			return 0;
		}
	}

	public String getColumnName(int col) {
		return (String) titulo[col];
	}

	public Object getValueAt(int row, int col) {
		if (registros != null) {
			Object[] rowTable = (Object[]) registros.get(row);
			return rowTable[col];
		} else {
			return null;
		}
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void setAS400System(AS400System system) {

		this.system = system;
	}

	public void setSQL(String sql) {
		this.sql = sql;
		if (system != null) {
			Thread t1 = new Thread(this);
			t1.start();
		}

	}

	@Override
	public void run() {
		Statement st = null;
		ResultSet rs = null;
		ResultSetMetaData rmd = null;
		// String sql = txtSQL.getText();
		Connection cn = null;
		try {
			cn=system.getConnection();
			st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(sql);
			rmd = rs.getMetaData();
			int nReg = 0;
			int c = rmd.getColumnCount();
			int i, j;
			String[] titulo = new String[c];
			Object[] row = null;
			// Carga Archivo Texto Temporal
			for (i = 1; i <= c; i++) {
				titulo[i - 1] = rmd.getColumnName(i);
			}
			i = 0;

			while (rs.next()) {
				row = new Object[c];
				for (j = 1; j <= c; j++) {
					// solo se pone en la tabla mientras sea menor a 100
					if (nReg < 500) {
						row[j - 1] = rs.getString(j);
					}
				}
				if (nReg < 500)
					registros.add(row);
				i++;
				nReg++;
			}

			// Si no hay registros
			if (nReg == 0) {
				JOptionPane.showMessageDialog(null, "Query", "Not records fond", JOptionPane.INFORMATION_MESSAGE);
			}
			rs.close();
			cn.close();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			cn = null;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null, "Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			cn = null;
		}

	}

}
