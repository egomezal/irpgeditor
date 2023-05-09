package org.egomez.irpgeditor.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.AbstractTableModel;

import org.egomez.irpgeditor.AS400System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResultSetTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private AS400System system;
    private final Connection connection;
    private final Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;
    Logger logger = LoggerFactory.getLogger(ResultSetTableModel.class);

    // keep track of database connection status
    private boolean connectedToDatabase = false;

    // constructor initializes resultSet and obtains its meta data object;
    // determines number of rows
    public ResultSetTableModel(String query, AS400System system)
            throws SQLException {
        // connect to database
        this.setSystem(system);
        connection = system.getConnectionPool();

        // create Statement to query database
        statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // update database connection status
        connectedToDatabase = true;

        // set query and execute it
        setQuery(query);
    } // end constructor ResultSetTableModel

    // get class that represents column type
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getColumnClass(int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // determine Java class of column
        try {
            String className = metaData.getColumnClassName(column + 1);

            // return Class object that represents className
            return Class.forName(className);
        } // end try
        catch (ClassNotFoundException | SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        } // end catch

        return Object.class; // if problems occur above, assume type Object
    } // end method getColumnClass

    // get number of columns in ResultSet
    @Override
    public int getColumnCount() throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // determine number of columns
        try {
            return metaData.getColumnCount();
        } // end try
        catch (SQLException sqlException) {
            //sqlException.printStackTrace();
            logger.error(sqlException.getMessage());
        } // end catch

        return 0; // if problems occur above, return 0 for number of columns
    } // end method getColumnCount

    // get name of a particular column in ResultSet
    @Override
    public String getColumnName(int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // determine column name
        try {
            return metaData.getColumnName(column + 1);
        } // end try
        catch (SQLException sqlException) {
            //sqlException.printStackTrace();
            logger.error(sqlException.getMessage());
        } // end catch

        return ""; // if problems, return empty string for column name
    } // end method getColumnName

    // return number of rows in ResultSet
    @Override
    public int getRowCount() throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        return numberOfRows;
    } // end method getRowCount

    // obtain value in particular row and column
    @Override
    public Object getValueAt(int row, int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // obtain a value at specified ResultSet row and column
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } // end try
        catch (SQLException sqlException) {
            //sqlException.printStackTrace();
            logger.error(sqlException.getMessage());
        } // end catch

        return ""; // if problems, return empty string object
    } // end method getValueAt

    public void setAS400System(AS400System system) {

        this.setSystem(system);
    }

    // set new database query string
    public void setQuery(String query) throws SQLException,
            IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // specify query and execute it
        resultSet = statement.executeQuery(query);

        // obtain meta data for ResultSet
        metaData = resultSet.getMetaData();

        // determine number of rows in ResultSet
        resultSet.last(); // move to last row
        numberOfRows = resultSet.getRow(); // get row number

        // notify JTable that model has changed
        fireTableStructureChanged();
    } // end method setQuery

    // close Statement and Connection
    public void disconnectFromDatabase() {
        if (!connectedToDatabase) {
            return;
        }

        // close Statement and Connection
        try {
            statement.close();
            connection.close();
        } // end try
        catch (SQLException sqlException) {
            //sqlException.printStackTrace();
            logger.error(sqlException.getMessage());
        } // end catch
        finally // update database connection status
        {
            connectedToDatabase = false;
        } // end finally
    } // end method disconnectFromDatabase

    public AS400System getSystem() {
        return system;
    }

    public void setSystem(AS400System system) {
        this.system = system;
    }
} // end class ResultSetTableModel
