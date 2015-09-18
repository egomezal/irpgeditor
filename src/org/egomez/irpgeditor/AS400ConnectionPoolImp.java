package org.egomez.irpgeditor;

import java.sql.Connection;
import java.sql.SQLException;

import com.ibm.as400.access.AS400JDBCConnectionPool;
import com.ibm.as400.access.AS400JDBCConnectionPoolDataSource;
import com.ibm.as400.access.ConnectionPoolEvent;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.access.ConnectionPoolListener;

public class AS400ConnectionPoolImp implements ConnectionPoolListener {
	  private AS400JDBCConnectionPool pool = null;
	    private int nMaxConexiones = 10;
	    
	    public AS400ConnectionPoolImp(String systemIP,
            String userName, String password ){
	    	 AS400JDBCConnectionPoolDataSource datasource = new AS400JDBCConnectionPoolDataSource(systemIP, userName, password);
	    	 datasource.setPackageCriteria("select");
	            datasource.setNaming("system");
	            datasource.setTransactionIsolation("none");
	            datasource.setLazyClose(true);
	            this.pool = new AS400JDBCConnectionPool(datasource);
	            this.pool.addConnectionPoolListener(this);
	            this.pool.setMaxConnections(nMaxConexiones);
	            this.pool.setMaxInactivity(60 * 1000);
	            this.pool.setMaxLifetime(1000 * 60 * 4);
	            this.pool.setRunMaintenance(true);
	            this.pool.setCleanupInterval(15000);
	    }
	@Override
	public void connectionCreated(ConnectionPoolEvent arg0) {

	}

	@Override
	public void connectionExpired(ConnectionPoolEvent arg0) {

	}

	@Override
	public void connectionPoolClosed(ConnectionPoolEvent arg0) {

	}

	@Override
	public void connectionReleased(ConnectionPoolEvent arg0) {

	}

	@Override
	public void connectionReturned(ConnectionPoolEvent arg0) {

	}

	@Override
	public void maintenanceThreadRun(ConnectionPoolEvent arg0) {

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
