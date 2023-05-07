package org.egomez.irpgeditor;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.ibm.as400.access.AS400JDBCConnectionPool;
import com.ibm.as400.access.AS400JDBCConnectionPoolDataSource;
import com.ibm.as400.access.ConnectionPoolEvent;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.access.ConnectionPoolListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AS400ConnectionPoolImp implements ConnectionPoolListener {

    private AS400JDBCConnectionPool pool = null;
    private int nMaxConexiones = 10;
    AS400JDBCConnectionPoolDataSource datasource = null;
    transient Logger logger = LoggerFactory.getLogger(AS400ConnectionPoolImp.class);
    public AS400JDBCConnectionPoolDataSource getDatasource() {
        return datasource;
    }
    
    public void setDatasource(AS400JDBCConnectionPoolDataSource datasource) {
        this.datasource = datasource;
        try {
            pool.setDataSource(datasource);
        } catch (PropertyVetoException e) {
          logger.error(e.getMessage());
        }
    }
    
    public AS400ConnectionPoolImp(String systemIP, String userName, String password) {
        datasource = new AS400JDBCConnectionPoolDataSource(systemIP, userName, password.toCharArray());
        datasource.setPackageCriteria("select");
        datasource.setNaming("system");
        datasource.setPrompt(false);
        datasource.setTransactionIsolation("none");
        datasource.setLazyClose(true);
        this.pool = new AS400JDBCConnectionPool(datasource);
        this.pool.addConnectionPoolListener(this);
        
        this.pool.setMaxConnections(nMaxConexiones);
        this.pool.setMaxInactivity(60 * 1000L);
        this.pool.setMaxLifetime(1000L * 60 * 4);
        this.pool.setRunMaintenance(true);
        this.pool.setCleanupInterval(15000);
    }
    
    @Override
    public void connectionCreated(ConnectionPoolEvent arg0) {
        //
    }
    
    @Override
    public void connectionExpired(ConnectionPoolEvent arg0) {
        //
    }
    
    @Override
    public void connectionPoolClosed(ConnectionPoolEvent arg0) {
        //
    }
    
    @Override
    public void connectionReleased(ConnectionPoolEvent arg0) {
        //
    }
    
    @Override
    public void connectionReturned(ConnectionPoolEvent arg0) {
        //
    }
    
    @Override
    public void maintenanceThreadRun(ConnectionPoolEvent arg0) {
        //
    }
    
    public void close() {
        this.pool.close();
        this.pool = null;
    }
    
    public synchronized Connection getConnection() throws SQLException {
        try {
            if (pool.getActiveConnectionCount() < pool.getMaxConnections()) {
                return pool.getConnection();
            } else {
                nMaxConexiones += 1;
                pool.setMaxConnections(nMaxConexiones);
                return pool.getConnection();
            }
        } catch (ConnectionPoolException cpe) {
            throw new SQLException(cpe.getMessage());
        }
    }
    
}
