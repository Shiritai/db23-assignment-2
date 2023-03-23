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
            float priceRaise = paramHelper.getPriceRaise(idx); // TODO: perhaps "double" ?
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
				throw new RuntimeException("Cloud not find item record with i_id = " + iid);
            s.close();
            double newPrice = priceRaise + price;
            int count;
            if (newPrice > As2BenchConstants.MAX_PRICE){
                count = StoredProcedureHelper.executeUpdate(
                    "UPDATE item SET i_price = " + As2BenchConstants.MIN_PRICE + "WHERE i_id = " + iid,
                    tx
                );
            } else {
                count = StoredProcedureHelper.executeUpdate(
                    "UPDATE item SET i_price = " + newPrice + "WHERE i_id = " + iid,
                    tx
                );
            } else // TODO  fix this (double else)
                throw new RuntimeException("Cloud not update item record with i_id = " + iid);
        }
    }

}
