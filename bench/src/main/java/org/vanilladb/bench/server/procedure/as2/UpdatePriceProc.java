package org.vanilladb.bench.server.procedure.as2;

import org.vanilladb.bench.benchmarks.as2.As2BenchConstants;
import org.vanilladb.bench.server.param.as2.UpdatePriceProcParamHelper;
import org.vanilladb.bench.server.procedure.StoredProcedureHelper;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.sql.storedprocedure.StoredProcedure;
import org.vanilladb.core.storage.tx.Transaction;

/**
 * Implementation of Stored Procedure of UpdateItemPriceTxn
 */
public class UpdatePriceProc extends StoredProcedure<UpdatePriceProcParamHelper> {

    public UpdatePriceProc() {
        super(new UpdatePriceProcParamHelper());
    }

    @Override
    protected void executeSql() {
        // TODO Auto-generated method stub
        UpdatePriceProcParamHelper paramHelper = getHelper();
        Transaction tx = getTransaction();

        for (int idx = 0; idx < paramHelper.getUpdateCount(); idx++){
            int iid = paramHelper.getUpdateItemId(idx);
            double priceRaise = paramHelper.getPriceRaise(idx);
            double price;
            Scan s = StoredProcedureHelper.executeQuery(
                "SELECT i_name, i_price FROM item WHERE i_id = " + iid,
                tx
            );
            s.beforeFirst();
			if (s.next()) {
				String name = (String) s.getVal("i_name").asJavaVal();
				price = (Double) s.getVal("i_price").asJavaVal();
				paramHelper.setItemName(name, idx);
				paramHelper.setItemPrice(price, idx);
			} else
				throw new RuntimeException("Could not find item record with i_id = " + iid);
            s.close();
            double newPrice = priceRaise + price;
            if (newPrice > As2BenchConstants.MAX_PRICE){
                newPrice = As2BenchConstants.MIN_PRICE;
            } 
            int count = StoredProcedureHelper.executeUpdate(
                "UPDATE item SET i_price = " + newPrice + "WHERE i_id = " + iid,
                tx
            );
            paramHelper.setItemPrice(newPrice, idx);
        }
    }

}
