package org.vanilladb.bench.server.procedure.as2;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.bench.server.param.as2.TestbedLoaderParamHelper;
import org.vanilladb.bench.server.procedure.StoredProcedureHelper;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.sql.storedprocedure.StoredProcedure;

public class As2CheckDatabaseProc extends StoredProcedure<TestbedLoaderParamHelper> {
	private static Logger logger = Logger.getLogger(As2CheckDatabaseProc.class.getName());
	
	public As2CheckDatabaseProc() {
		super(new TestbedLoaderParamHelper());
	}

	@Override
	protected void executeSql() {
		if (logger.isLoggable(Level.INFO))
			logger.info("Checking database for the as2 benchmarks...");

		// Checking item records
		if (!checkItemTable(1, getHelper().getNumberOfItems()))
			abort("checking database fails");

		if (logger.isLoggable(Level.INFO))
			logger.info("Checking completed.");
	}

	private boolean checkItemTable(int startIId, int endIId) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Checking items from i_id=" + startIId + " to i_id=" + endIId);
		
		// Use a bit array to record existence
		int total = endIId - startIId + 1;
		boolean[] checked = new boolean[total];
		for (int i = 0; i < total; i++)
			checked[i] = false;
		
		// Scan the table
		String sql = "SELECT i_id FROM item";
		Scan scan = StoredProcedureHelper.executeQuery(sql, getTransaction());
		scan.beforeFirst();
		for (int i = startIId, count = 0; i <= endIId; i++) {
			if (!scan.next()) {
				if (logger.isLoggable(Level.SEVERE))
					logger.severe(String.format("Only %d records are found (there should be %d records)",
							count, total));
				return false;
			}
			
			int id = (Integer) scan.getVal("i_id").asJavaVal();
			if (checked[id - 1]) {
				if (logger.isLoggable(Level.SEVERE))
					logger.severe(String.format("Found duplicated record (i_id = %d)", id));
				return false;
			}
			checked[id - 1] = true;
			count++;
		}
		scan.close();

		if (logger.isLoggable(Level.FINE))
			logger.fine("Checking items completed.");
		
		return true;
	}

}
