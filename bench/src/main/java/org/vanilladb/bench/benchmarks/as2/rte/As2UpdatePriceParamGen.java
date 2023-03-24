package org.vanilladb.bench.benchmarks.as2.rte;

import java.util.ArrayList;

import org.vanilladb.bench.benchmarks.as2.As2BenchConstants;
import org.vanilladb.bench.benchmarks.as2.As2BenchTransactionType;
import org.vanilladb.bench.rte.TxParamGenerator;
import org.vanilladb.bench.util.BenchProperties;
import org.vanilladb.bench.util.RandomValueGenerator;

/**
 * Parameter generator for UpdateItemPriceTxn
 */
public class As2UpdatePriceParamGen implements TxParamGenerator<As2BenchTransactionType> {

    // Update Counts
	private static final int TOTAL_UPDATE_COUNT;
	
	static {
		TOTAL_UPDATE_COUNT = BenchProperties.getLoader()
				.getPropertyAsInteger(As2UpdatePriceParamGen.class.getName() + ".TOTAL_UPDATE_COUNT", 10);
	}
	
	@Override
    public As2BenchTransactionType getTxnType() {
        return As2BenchTransactionType.UPDATE_PRICE;
    }

    @Override
    public Object[] generateParameter() {
       RandomValueGenerator rvg = new RandomValueGenerator();
       ArrayList<Object> paramList = new ArrayList<Object>();
       
       // Set r count
		paramList.add(TOTAL_UPDATE_COUNT);
		for (int i = 0; i < TOTAL_UPDATE_COUNT; i++){
            paramList.add(rvg.number(1, As2BenchConstants.NUM_ITEMS)); // Randomly pick an item id
            paramList.add(rvg.randomDoubleIncrRange(0.0, 5.0, 1000.0)); // Randomly generate a number 0.0 - 5.0 for price raise
        }
			

		return paramList.toArray(new Object[0]);
    }
    
}
