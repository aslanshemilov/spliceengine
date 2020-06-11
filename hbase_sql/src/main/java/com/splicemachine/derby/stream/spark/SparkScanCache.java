package com.splicemachine.derby.stream.spark;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.RowLocation;
import com.splicemachine.derby.stream.iapi.DataSet;
import org.apache.spark.api.java.JavaPairRDD;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SparkScanCache {
    public static Cache<String, JavaPairRDD<RowLocation, ExecRow>> cache =
            CacheBuilder.newBuilder().concurrencyLevel(8).maximumSize(1024)
                    .expireAfterAccess(5, TimeUnit.MINUTES)
                    .removalListener(l -> ((JavaPairRDD)l.getValue()).unpersist(false)).build();

    public static Cache<Id, DataSet> dataSetCache =
            CacheBuilder.newBuilder().concurrencyLevel(8).maximumSize(1024)
                    .expireAfterAccess(5, TimeUnit.MINUTES)
                    .removalListener(l -> ((NativeSparkDataSet)l.getValue()).dataset.unpersist(false)).build();

    public static class Id {
        String name;
        int resultSetNumber;
        long txnId;

        public Id(String name, int resultSetNumber, long txnId) {
            this.name = name;
            this.resultSetNumber = resultSetNumber;
            this.txnId = txnId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return resultSetNumber == id.resultSetNumber &&
                    txnId == id.txnId &&
                    Objects.equals(name, id.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, resultSetNumber, txnId);
        }
    }
}

