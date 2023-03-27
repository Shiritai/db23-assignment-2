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
package org.vanilladb.bench.benchmarks.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.vanilladb.bench.benchmarks.as2.As2BenchTransactionType;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcExecutor;
import org.vanilladb.bench.rte.jdbc.JdbcJob;

public class As2BenchJdbcExecutor implements JdbcExecutor<As2BenchTransactionType> {

	@Override
	public SutResultSet execute(Connection conn, As2BenchTransactionType txType, Object[] pars)
			throws SQLException {
		JdbcJob job = null;
		switch (txType) {
		case TESTBED_LOADER:
			job = new LoadingTestbedJdbcJob();
			break;
		case CHECK_DATABASE:
			job = new CheckDatabaseJdbcJob();
			break;
		case READ_ITEM:
			job = new ReadItemTxnJdbcJob();
			break;
		case UPDATE_PRICE:
			job = new UpdatePriceJdbcJob();
			break;
		default:
			throw new UnsupportedOperationException(
					String.format("no JDBC implementation for '%s'", txType));
		}
		return job.execute(conn, pars);
	}

}
