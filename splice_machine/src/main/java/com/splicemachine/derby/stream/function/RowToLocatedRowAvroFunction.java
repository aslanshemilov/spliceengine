/*
 * Copyright (c) 2012 - 2020 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.splicemachine.derby.stream.function;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.scanner.SITableScanner;
import com.splicemachine.derby.stream.iapi.OperationContext;
import org.apache.spark.TaskContext;
import org.apache.spark.sql.Row;
import java.io.*;

import org.apache.spark.api.java.function.Function;

/**
 *
 * Allows a map to convert from RDD<ExecRow> to RDD<Row>
 *
 */
public class RowToLocatedRowAvroFunction implements Function <Row, ExecRow>, Serializable, Externalizable {
    protected SpliceOperation op;
    protected ExecRow execRow;
    protected OperationContext operationContext;
    public RowToLocatedRowAvroFunction() {
        super();
    }
    protected boolean initialized = false;

    public RowToLocatedRowAvroFunction(OperationContext<SpliceOperation> operationContext) throws StandardException {
        this(operationContext, operationContext.getOperation().getExecRowDefinition());
    }

    public RowToLocatedRowAvroFunction(OperationContext<SpliceOperation> operationContext,ExecRow execRow) throws StandardException{
        this.operationContext = operationContext;
        this.execRow = execRow;
        supportDateType();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(operationContext);
        out.writeObject(execRow);
    }

    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        operationContext = (OperationContext) in.readObject();
        execRow = (ExecRow) in.readObject();
    }
    @Override
    public ExecRow call(Row row) throws Exception {
        if (!initialized) {
            if (operationContext!=null)
                op = operationContext.getOperation();
            else
                SITableScanner.regionId.set(""+TaskContext.getPartitionId()); // Sets PartitionId for columnar files.
            initialized = true;
        }
        ExecRow ExecRow = execRow.getNewNullRow().fromSparkRow(row);
        if (op!=null)
            op.setCurrentRow(ExecRow);
        return ExecRow;
    }

    public void supportDateType() throws StandardException {
        for(int i = 0; i < execRow.size(); i++){
            if (execRow.getColumn(i + 1).getTypeName().equals("DATE")){
                if (execRow.getColumn(i + 1).isNull()){
                    execRow.setColumn(i + 1, new SQLVarchar());
                } else {
                    execRow.setColumn(i + 1, new SQLVarchar(execRow.getColumn(i + 1).getString().toCharArray()));
                }
            }
        }
    }
}
