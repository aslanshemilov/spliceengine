/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.splicemachine.derby.impl.sql.execute.operations;

import com.splicemachine.derby.iapi.sql.execute.*;
import com.splicemachine.derby.stream.function.CountJoinedLeftFunction;
import com.splicemachine.derby.stream.function.merge.MergeAntiJoinFlatMapFunction;
import com.splicemachine.derby.stream.function.merge.MergeInnerJoinFlatMapFunction;
import com.splicemachine.derby.stream.function.merge.MergeOuterJoinFlatMapFunction;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.utils.SpliceLogUtils;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.services.loader.GeneratedMethod;
import com.splicemachine.db.iapi.sql.Activation;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * @author P Trolard
 *         Date: 18/11/2013
 */
public class MergeJoinOperation extends JoinOperation {
    private static final Logger LOG = Logger.getLogger(MergeJoinOperation.class);
    private int leftHashKeyItem;
    private int rightHashKeyItem;
    public int[] leftHashKeys;
    public int[] rightHashKeys;
    // for overriding
    public boolean wasRightOuterJoin = false;

    protected static final String NAME = MergeJoinOperation.class.getSimpleName().replaceAll("Operation","");

	@Override
	public String getName() {
			return NAME;
	}

    
    public MergeJoinOperation() {
        super();
    }

    public MergeJoinOperation(SpliceOperation leftResultSet,
                              int leftNumCols,
                              SpliceOperation rightResultSet,
                              int rightNumCols,
                              int leftHashKeyItem,
                              int rightHashKeyItem,
                              Activation activation,
                              GeneratedMethod restriction,
                              int resultSetNumber,
                              boolean oneRowRightSide,
                              boolean notExistsRightSide,
                              double optimizerEstimatedRowCount,
                              double optimizerEstimatedCost,
                              String userSuppliedOptimizerOverrides)
            throws StandardException {
        super(leftResultSet, leftNumCols, rightResultSet, rightNumCols,
                 activation, restriction, resultSetNumber, oneRowRightSide,
                 notExistsRightSide, optimizerEstimatedRowCount,
                 optimizerEstimatedCost, userSuppliedOptimizerOverrides);
        this.leftHashKeyItem = leftHashKeyItem;
        this.rightHashKeyItem = rightHashKeyItem;
        init();
    }

    @Override
    public void init(SpliceOperationContext context) throws StandardException, IOException {
    	super.init(context);
        leftHashKeys = generateHashKeys(leftHashKeyItem);
        rightHashKeys = generateHashKeys(rightHashKeyItem);
    	if (LOG.isDebugEnabled()) {
    		SpliceLogUtils.debug(LOG,"left hash keys {%s}",Arrays.toString(leftHashKeys));
    		SpliceLogUtils.debug(LOG,"right hash keys {%s}",Arrays.toString(rightHashKeys));
    	}
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(leftHashKeyItem);
        out.writeInt(rightHashKeyItem);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        leftHashKeyItem = in.readInt();
        rightHashKeyItem = in.readInt();
    }

    @Override
    public DataSet<LocatedRow> getDataSet(DataSetProcessor dsp) throws StandardException {
        OperationContext<JoinOperation> operationContext = dsp.<JoinOperation>createOperationContext(this);
        DataSet<LocatedRow> left = leftResultSet.getDataSet(dsp);
        
        operationContext.pushScope();
        try {
            left = left.map(new CountJoinedLeftFunction(operationContext));
            if (isOuterJoin)
                return left.mapPartitions(new MergeOuterJoinFlatMapFunction(operationContext), true);
            else {
                if (notExistsRightSide)
                    return left.mapPartitions(new MergeAntiJoinFlatMapFunction(operationContext), true);
                else {
                    return left.mapPartitions(new MergeInnerJoinFlatMapFunction(operationContext), true);
                }
            }
        } finally {
            operationContext.popScope();
        }
    }

    @Override
    public int[] getLeftHashKeys() {
        return leftHashKeys;
    }

    @Override
    public int[] getRightHashKeys() {
        return rightHashKeys;
    }
}
