package org.vanilladb.bench.server.param.as2;

import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureHelper;

/**
 * Helper to parse parameters of
 * UpdateItemPriceTxn in server side
 */
public class UpdatePriceProcParamHelper implements StoredProcedureHelper {

    private int updateCount;
    private int[] updateItemId;
    private String[] itemName;
    private double[] itemPrice;
    private double[] priceRaise;

    public int getUpdateCount() {
        return updateCount;
    }

    public int getUpdateItemId(int index) {
        return updateItemId[index];
    }

    public double getPriceRaise(int index) {
        return priceRaise[index];
    }

    public void setItemName(String s, int idx) {
		itemName[idx] = s;
	}

	public void setItemPrice(double d, int idx) {
		itemPrice[idx] = d;
	}

    public void set

    @Override
    public Schema getResultSetSchema() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResultSetSchema'");
    }

    @Override
    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SpResultRecord newResultSetRecord() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newResultSetRecord'");
    }

    @Override
    public void prepareParameters(Object... arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'prepareParameters'");
    }
    
}
