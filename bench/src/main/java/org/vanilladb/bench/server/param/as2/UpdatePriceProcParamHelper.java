package org.vanilladb.bench.server.param.as2;

import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureHelper;

/**
 * Helper to parse parameters of
 * UpdateItemPriceTxn in server side
 */
public class UpdatePriceProcParamHelper implements StoredProcedureHelper {

    @Override
    public Schema getResultSetSchema() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResultSetSchema'");
    }

    @Override
    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isReadOnly'");
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
