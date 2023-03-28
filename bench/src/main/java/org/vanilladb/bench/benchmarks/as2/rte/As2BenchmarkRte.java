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
package org.vanilladb.bench.benchmarks.as2.rte;

import org.vanilladb.bench.StatisticMgr;
import org.vanilladb.bench.VanillaBenchParameters;
import org.vanilladb.bench.benchmarks.as2.As2BenchTransactionType;
import org.vanilladb.bench.remote.SutConnection;
import org.vanilladb.bench.rte.RemoteTerminalEmulator;
import org.vanilladb.bench.util.RandomValueGenerator;

public class As2BenchmarkRte extends RemoteTerminalEmulator<As2BenchTransactionType> {

	private static As2BenchmarkTxExecutor[] executors;

	static {
		executors = new As2BenchmarkTxExecutor[] {
			new As2BenchmarkTxExecutor(new As2ReadItemParamGen()),
			new As2BenchmarkTxExecutor(new As2UpdatePriceParamGen())
		};
	};

	RandomValueGenerator rv;
	int current;

	public As2BenchmarkRte(SutConnection conn, StatisticMgr statMgr, long sleepTime) {
		super(conn, statMgr, sleepTime);
		rv = new RandomValueGenerator(sleepTime);
		current = 0;
	}

	protected As2BenchTransactionType getNextTxType() {
		current = rv.randomChooseFromDistribution(VanillaBenchParameters.TASK_TX_DIST);
		return executors[current].getTxnType();
	}

	protected As2BenchmarkTxExecutor getTxExecutor(As2BenchTransactionType type) {
		return executors[current];
	}
}
