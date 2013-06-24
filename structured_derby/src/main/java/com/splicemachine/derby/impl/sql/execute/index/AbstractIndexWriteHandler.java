package com.splicemachine.derby.impl.sql.execute.index;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.splicemachine.constants.SpliceConstants;
import com.splicemachine.hbase.*;
import com.splicemachine.hbase.batch.WriteContext;
import com.splicemachine.hbase.batch.WriteHandler;
import com.splicemachine.utils.SpliceLogUtils;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Scott Fines
 * Created on: 5/1/13
 */
abstract class AbstractIndexWriteHandler extends SpliceConstants implements WriteHandler {
    /*
    * Maps the columns in the index to the columns in the main table.
    * e.g. if indexColsToMainColMap[0] = 1, then the first entry
    * in the index is the second column in the main table, and so on.
    */
    protected final int[] indexColsToMainColMap;
    /*
     * A cache of column positions in the main table puts. This speeds
     * access and transformation of Puts and Deletes into Index Puts and
     * Deletes.
     */
    protected final byte[][] mainColPos;
    /*
     * The id for the index table
     *
     * indexConglomBytes is a cached byte[] representation of the indexConglomId
     * to speed up transformations.
     */
    protected final byte[] indexConglomBytes;
    private static final Logger LOG = Logger.getLogger(AbstractIndexWriteHandler.class);

    protected boolean failed;

    protected final Map<Mutation,Mutation> indexToMainMutationMap = Maps.newHashMap();

    protected AbstractIndexWriteHandler(int[] indexColsToMainColMap,
                                        byte[][] mainColPos,
                                        byte[] indexConglomBytes) {
        this.indexColsToMainColMap = indexColsToMainColMap;
        this.mainColPos = mainColPos;
        this.indexConglomBytes = indexConglomBytes;
    }

    @Override
    public void next(Mutation mutation, WriteContext ctx) {
        if(failed)
            ctx.notRun(mutation);
        else if(mutation.getAttribute(IndexSet.INDEX_UPDATED)!=null)
            ctx.sendUpstream(mutation);
        else{
            boolean sendUp = updateIndex(mutation,ctx);
            if(sendUp){
                ctx.sendUpstream(mutation);
            }
        }
    }

    @Override
    public void finishWrites(WriteContext ctx) throws IOException {
        try{
            finish(ctx);
        }catch(Exception e){
            SpliceLogUtils.error(LOG,e);
            if(e instanceof WriteFailedException){
                WriteFailedException wfe = (WriteFailedException)e;
                for(Mutation originalMutation:indexToMainMutationMap.values()){
                    ctx.failed(originalMutation, new MutationResult(MutationResult.Code.FAILED, wfe.getMessage()));
                }
            }
            else throw new IOException(e); //something unexpected went bad, need to propagate

        }
    }

    protected byte[][] getDataArray() {
        return new byte[indexColsToMainColMap.length+1][];
    }

    protected abstract boolean updateIndex(Mutation mutation, WriteContext ctx);

    protected abstract void finish(WriteContext ctx) throws Exception;

    protected CallBuffer<Mutation> getWriteBuffer(final WriteContext ctx) {
        return ctx.getWriteBuffer(indexConglomBytes,new TableWriter.FlushWatcher() {
            @Override
            public List<Mutation> preFlush(List<Mutation> mutations) throws Exception {
                /*
                 * If a row has failed already, there's no sense in writing it out, so
                 * we filter out already failed items.
                 */
                return Lists.newArrayList(Collections2.filter(mutations, new Predicate<Mutation>() {
                    @Override
                    public boolean apply(@Nullable Mutation input) {
                        return ctx.canRun(new Pair<Mutation,Integer>(input,null));
                    }
                }));
            }

            @Override
            public Response globalError(Throwable t) throws Exception {
                /*
                 * When writing, you can get a NotServingRegionException before attempting the write
                 * of any individual record, so you know if you got this error, then you can
                 * retry the entire batch safely.
                 */
                return t instanceof NotServingRegionException ? Response.RETRY: Response.THROW_ERROR;
            }

            @Override
            public Response partialFailure(MutationRequest request,MutationResponse response) throws Exception {
                Map<Integer,MutationResult> failedRows = response.getFailedRows();
                boolean canRetry=true;
                for(Integer row:failedRows.keySet()){
                    MutationResult result = failedRows.get(row);
                    if(!result.getErrorMsg().contains("NotServingRegion")&&
                            !result.getErrorMsg().contains("WrongRegion")){
                        canRetry=false;
                        break;
                    }
                }
                if(canRetry) return Response.RETRY;
                else{
                    /*
                     * We don't want to retry, but neither do we want to throw an exception.
                     *
                     * Instead, map the error messages back onto the original Mutation, and record the
                     * error message into the context
                     */
                    List<Mutation> indexMutations = request.getMutations();
                    for(Integer indexRow:failedRows.keySet()){
                        Mutation indexMutation = indexMutations.get(indexRow);
                        ctx.failed(indexToMainMutationMap.get(indexMutation),failedRows.get(indexRow));
                    }
                    return Response.RETURN;
                }
            }
        });
    }
}
