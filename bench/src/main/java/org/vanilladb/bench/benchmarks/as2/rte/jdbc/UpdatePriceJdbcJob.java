package org.vanilladb.bench.benchmarks.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.bench.benchmarks.as2.As2BenchConstants;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.remote.jdbc.VanillaDbJdbcResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcJob;
import org.vanilladb.bench.server.param.as2.UpdatePriceProcParamHelper;

/**
 * JDBC implementation of UpdateItemPriceTxn
 */
public class UpdatePriceJdbcJob implements JdbcJob {

	private static Logger logger = Logger.getLogger(UpdatePriceJdbcJob.class
			.getName());
	
	@Override
	public SutResultSet execute(Connection conn, Object[] pars) throws SQLException {
		UpdatePriceProcParamHelper paramHelper = new UpdatePriceProcParamHelper();
		paramHelper.prepareParameters(pars);
		
		// Output message
		StringBuilder outputMsg = new StringBuilder("[");
		
		// Execute logic
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = null;
			String sql = null;
			
			
			for (int i = 0; i < paramHelper.getUpdateCount(); i++) {
				int iid = paramHelper.getUpdateItemId(i);
				double priceRaise = paramHelper.getUpdatePriceRaise(i);
	            double price;
	            
				// SELECT
				sql = "SELECT i_name, i_price FROM item WHERE i_id = " + iid;
				rs = statement.executeQuery(sql);
				rs.beforeFirst();
				if (rs.next()) {			
					outputMsg.append(String.format("'%s', ", rs.getString("i_name")));
					price = rs.getDouble("i_price");
				} else
					throw new RuntimeException("cannot find the record with i_id = " + iid);
				rs.close();
				
				// UPDATE
				double newPrice = priceRaise + price;				
	            if (newPrice > As2BenchConstants.MAX_PRICE){
	                newPrice = As2BenchConstants.MIN_PRICE;
	            } 
	            
	            sql = "UPDATE item SET i_price = " + newPrice + " WHERE i_id = " + iid;
	            statement.executeUpdate(sql);
			}
			
			conn.commit();
			
			outputMsg.deleteCharAt(outputMsg.length() - 2);
			outputMsg.append("]");
			
			return new VanillaDbJdbcResultSet(true, outputMsg.toString());
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning(e.toString());
			return new VanillaDbJdbcResultSet(false, "");
		}
	}
    
}
