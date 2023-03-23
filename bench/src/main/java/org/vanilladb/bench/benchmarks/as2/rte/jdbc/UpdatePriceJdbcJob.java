package org.vanilladb.bench.benchmarks.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcJob;

/**
 * JDBC implementation of UpdateItemPriceTxn
 */
public class UpdatePriceJdbcJob implements JdbcJob {

    @Override
    public SutResultSet execute(Connection conn, Object[] pars) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
