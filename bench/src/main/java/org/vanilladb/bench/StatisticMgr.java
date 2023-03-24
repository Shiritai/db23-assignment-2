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
package org.vanilladb.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticMgr {
	private static Logger logger = Logger.getLogger(StatisticMgr.class.getName());

	private static class TxnStatistic {
		private BenchTransactionType mType;
		private int txnCount = 0;
		private long totalResponseTimeNs = 0;

		public TxnStatistic(BenchTransactionType txnType) {
			this.mType = txnType;
		}

		public BenchTransactionType getmType() {
			return mType;
		}

		public void addTxnResponseTime(long responseTime) {
			txnCount++;
			totalResponseTimeNs += responseTime;
		}

		public int getTxnCount() {
			return txnCount;
		}

		public long getTotalResponseTime() {
			return totalResponseTimeNs;
		}
	}

	// marked unused since this is in template code, may be useful in future
	@SuppressWarnings("unused")
	private int timelineGranularity;

	private File outputDir;
	private List<TxnResultSet> resultSets = new ArrayList<TxnResultSet>();
	private List<BenchTransactionType> allTxTypes;
	private String fileNamePostfix = "";
	private long recordStartTime = -1;

	public StatisticMgr(Collection<BenchTransactionType> txTypes, File outputDir, int timelineGranularity) {
		this.allTxTypes = new LinkedList<BenchTransactionType>(txTypes);
		this.outputDir = outputDir;
		this.timelineGranularity = timelineGranularity;
	}

	public StatisticMgr(Collection<BenchTransactionType> txTypes, File outputDir, String namePostfix,
			int timelineGranularity) {
		this.allTxTypes = new LinkedList<BenchTransactionType>(txTypes);
		this.outputDir = outputDir;
		this.fileNamePostfix = namePostfix;
		this.timelineGranularity = timelineGranularity;
	}

	/**
	 * We use the time that this method is called at as the start time for
	 * recording.
	 */
	public synchronized void setRecordStartTime() {
		if (recordStartTime == -1)
			recordStartTime = System.nanoTime();
	}

	public synchronized void processTxnResult(TxnResultSet trs) {
		if (recordStartTime == -1)
			recordStartTime = trs.getTxnEndTime();
		resultSets.add(trs);
	}

	public synchronized void outputReport() {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss"); // E.g. "20220324-200824"
			String fileName = formatter.format(Calendar.getInstance().getTime());
			if (fileNamePostfix != null && !fileNamePostfix.isEmpty())
				fileName += "-" + fileNamePostfix; // E.g. "20220324-200824-postfix"

			outputDetailReport(fileName);
			outputStatisticReport(fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.INFO))
			logger.info("Finish creating benchmark report.");
	}

	/**
	 * Write execution time report in txt format
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private void outputDetailReport(String fileName) throws IOException {
		Map<BenchTransactionType, TxnStatistic> txnStatistics = new HashMap<BenchTransactionType, TxnStatistic>();
		Map<BenchTransactionType, Integer> abortedCounts = new HashMap<BenchTransactionType, Integer>();

		for (BenchTransactionType type : allTxTypes) {
			txnStatistics.put(type, new TxnStatistic(type));
			abortedCounts.put(type, 0);
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName + ".txt")))) {
			// First line: total transaction count
			writer.write("# of txns (including aborted) during benchmark period: " + resultSets.size());
			writer.newLine();

			// Detail latency report
			for (TxnResultSet resultSet : resultSets) {
				if (resultSet.isTxnIsCommited()) {
					// Write a line: {[Tx Type]: [Latency]}
					writer.write(resultSet.getTxnType() + ": "
							+ TimeUnit.NANOSECONDS.toMillis(resultSet.getTxnResponseTime()) + " ms");
					writer.newLine();

					// Count transaction for each type
					TxnStatistic txnStatistic = txnStatistics.get(resultSet.getTxnType());
					txnStatistic.addTxnResponseTime(resultSet.getTxnResponseTime());

				} else {
					writer.write(resultSet.getTxnType() + ": ABORTED");
					writer.newLine();

					// Count transaction for each type
					Integer count = abortedCounts.get(resultSet.getTxnType());
					abortedCounts.put(resultSet.getTxnType(), count + 1);
				}
			}
			writer.newLine();

			// Last few lines: show the statistics for each type of transactions
			int abortedTotal = 0;
			for (Entry<BenchTransactionType, TxnStatistic> entry : txnStatistics.entrySet()) {
				TxnStatistic value = entry.getValue();
				int abortedCount = abortedCounts.get(entry.getKey());
				abortedTotal += abortedCount;
				long avgResTimeMs = 0;

				if (value.txnCount > 0) {
					avgResTimeMs = TimeUnit.NANOSECONDS.toMillis(value.getTotalResponseTime() / value.txnCount);
				}

				writer.write(value.getmType() + " - committed: " + value.getTxnCount() + ", aborted: " + abortedCount
						+ ", avg latency: " + avgResTimeMs + " ms");

				writer.newLine();
			}

			// Last line: Total statistics
			int finishedCount = resultSets.size() - abortedTotal;
			double avgResTimeMs = 0;
			if (finishedCount > 0) { // Avoid "Divide By Zero"
				for (TxnResultSet rs : resultSets)
					avgResTimeMs += rs.getTxnResponseTime() / finishedCount;
			}
			writer.write(String.format("TOTAL - committed: %d, aborted: %d, avg latency: %d ms", finishedCount,
					abortedTotal, Math.round(avgResTimeMs / 1000000)));
		}
	}

	/**
	 * Write execution time report with statistic analysis in csv format
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private void outputStatisticReport(String fileName) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName + ".csv")))) {
			// First line: total transaction count
			writer.write(
					"time(sec), throughput(txs), avg_latency(ms), min(ms), max(ms), 25th_lat(ms), median_lat(ms), 75th_lat(ms)\n");

			// Detail latency report
			long lead = recordStartTime, currentTime = 0;
			final long step = VanillaBenchParameters.ANALYZE_INTERVAL * 1000000000; // in nanosecond
			ArrayList<Long> batch = new ArrayList<>();
			for (TxnResultSet resultSet : resultSets) {
				if (resultSet.getTxnEndTime() >= lead + step) {
					lead += step;
					currentTime += VanillaBenchParameters.ANALYZE_INTERVAL;
					// start analysis
					long throughput = batch.size(), avg_latency = 0,
							min = Long.MAX_VALUE, max = Long.MIN_VALUE,
							lat_25th, lat_median, lat_75th;
					for (long n : batch) {
						avg_latency += n;
						min = Math.min(min, n);
						max = Math.max(max, n);
					}
					avg_latency /= throughput;
					PriorityQueue<Long> pq = new PriorityQueue<>(batch);
					while ((pq.size() << 2) > throughput * 3) { // haven't reach 25th
						pq.poll();
					}
					lat_25th = pq.peek();
					while ((pq.size() << 1) > throughput) { // haven't reach median
						pq.poll();
					}
					lat_median = pq.peek();
					while ((pq.size() << 2) > throughput) { // haven't reach 75th
						pq.poll();
					}
					lat_75th = pq.peek();

					/**
					 * Write a line:
					 * time(sec), throughput(txs), avg_latency(ms), min(ms),
					 * max(ms), 25th_lat(ms), median_lat(ms), 75th_lat(ms)
					 */
					writer.write(String.format("%d,%d,%d,%d,%d,%d,%d,%d\n",
							currentTime, throughput, avg_latency,
							min, max, lat_25th, lat_median, lat_75th));
					
					batch.clear();
				}
				batch.add(resultSet.getTxnResponseTime());
			}
		}
	}
}
