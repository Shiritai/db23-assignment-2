package org.vanilladb.bench.server.procedure.as2;

import org.vanilladb.bench.server.param.as2.UpdatePriceProcParamHelper;
import org.vanilladb.core.sql.storedprocedure.StoredProcedure;

/**
 * Implementation of Stored Procedure of UpdateItemPriceTxn
 */
public class UpdatePriceProc extends StoredProcedure<UpdatePriceProcParamHelper> {

    public UpdatePriceProc(UpdatePriceProcParamHelper helper) {
        super(helper);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void executeSql() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeSql'");
    }
    
}
