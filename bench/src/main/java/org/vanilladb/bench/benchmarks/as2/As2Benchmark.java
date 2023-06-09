/*******************************************************************************
 * Copyright 2016, 2018 vanilladb.org contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.vanilladb.bench.benchmarks.as2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.vanilladb.bench.BenchTransactionType;
import org.vanilladb.bench.Benchmark;
import org.vanilladb.bench.StatisticMgr;
import org.vanilladb.bench.VanillaBenchParameters;
import org.vanilladb.bench.benchmarks.as2.rte.As2BenchmarkRte;
import org.vanilladb.bench.benchmarks.as2.rte.jdbc.As2BenchJdbcExecutor;
import org.vanilladb.bench.remote.SutConnection;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.rte.RemoteTerminalEmulator;

public class As2Benchmark extends Benchmark {

	@Override
	public Set<BenchTransactionType> getBenchmarkingTxTypes() {
		Set<BenchTransactionType> txTypes = new HashSet<BenchTransactionType>();
		for (As2BenchTransactionType txType : As2BenchTransactionType.values()) {
			if (txType.isBenchmarkingProcedure())
				txTypes.add(txType);
		}
		return txTypes;
	}

	@Override
	public void executeLoadingProcedure(SutConnection conn) throws SQLException {
		As2BenchTransactionType txnType = As2BenchTransactionType.TESTBED_LOADER;
		Object[] params = new Object[] {As2BenchConstants.NUM_ITEMS};
		
		switch (VanillaBenchParameters.CONNECTION_MODE) {
		case JDBC:
			Connection jdbcConn = conn.toJdbcConnection();
			jdbcConn.setAutoCommit(false);
			As2BenchJdbcExecutor executor = new As2BenchJdbcExecutor();
			executor.execute(jdbcConn, txnType, params);
			break;
		case SP:
			conn.callStoredProc(txnType.getProcedureId(), params);
			break;
		}
	}

	@Override
	public RemoteTerminalEmulator<As2BenchTransactionType> createRte(SutConnection conn, StatisticMgr statMgr, long rteSleepTime) {
		return new As2BenchmarkRte(conn, statMgr, rteSleepTime);
	}

	@Override
	public boolean executeDatabaseCheckProcedure(SutConnection conn) throws SQLException {
		SutResultSet result = null;
		As2BenchTransactionType txnType = As2BenchTransactionType.CHECK_DATABASE;
		Object[] params = new Object[] {As2BenchConstants.NUM_ITEMS};
		
		switch (VanillaBenchParameters.CONNECTION_MODE) {
		case JDBC:
			Connection jdbcConn = conn.toJdbcConnection();
			jdbcConn.setAutoCommit(false);
			As2BenchJdbcExecutor executor = new As2BenchJdbcExecutor();
			result = executor.execute(jdbcConn, txnType, params);
			break;
		case SP:
			result = conn.callStoredProc(txnType.getProcedureId(), params);
			break;
		}
		
		return result.isCommitted();
	}

	@Override
	public String getBenchmarkName() {
		return "as2bench";
	}
}
