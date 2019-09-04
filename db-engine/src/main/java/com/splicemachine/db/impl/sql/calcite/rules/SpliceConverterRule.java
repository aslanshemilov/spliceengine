package com.splicemachine.db.impl.sql.calcite.rules;

import com.splicemachine.db.impl.sql.calcite.reloperators.*;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalValues;

/**
 * Created by yxia on 9/5/19.
 */
abstract public class SpliceConverterRule extends ConverterRule {


    protected final Convention out;

    public static final RelOptRule[] RULES = {
            SpliceProjectRule.INSTANCE,
            SpliceFilterRule.INSTANCE,
            SpliceJoinRule.INSTANCE,
            SpliceValuesRule.INSTANCE
    };

    SpliceConverterRule(Class<? extends RelNode> clazz, RelTrait in,
                        Convention out, String description) {
        super(clazz, in, out, description);
        this.out = out;
    }

    public static class SpliceProjectRule extends SpliceConverterRule {
        private static final SpliceProjectRule INSTANCE = new SpliceProjectRule();

        private SpliceProjectRule() {
            super(LogicalProject.class, Convention.NONE, SpliceRelNode.CONVENTION,
                    "SpliceProjectRule");
        }

        public RelNode convert(RelNode rel) {
            final LogicalProject project = (LogicalProject) rel;
            final RelTraitSet traitSet = project.getTraitSet().replace(out);
            return new SpliceProject(project.getCluster(), traitSet,
                    convert(project.getInput(), out), project.getProjects(),
                    project.getRowType());
        }
    }

    private static class SpliceFilterRule extends SpliceConverterRule {
        private static final SpliceFilterRule INSTANCE = new SpliceFilterRule();

        private SpliceFilterRule() {
            super(LogicalFilter.class, Convention.NONE, SpliceRelNode.CONVENTION,
                    "SpliceFilterRule");
        }

        public RelNode convert(RelNode rel) {
            final LogicalFilter filter = (LogicalFilter) rel;
            final RelTraitSet traitSet = filter.getTraitSet().replace(out);
            return new SpliceFilter(
                    rel.getCluster(),
                    traitSet,
                    convert(filter.getInput(), out),
                    filter.getCondition());
        }
    }

    private static class SpliceJoinRule extends SpliceConverterRule {
        private static final SpliceJoinRule INSTANCE = new SpliceJoinRule();

        private SpliceJoinRule() {
            super(LogicalJoin.class, Convention.NONE, SpliceRelNode.CONVENTION, "SpliceJoinRule");
        }

        public RelNode convert(RelNode rel) {
            final LogicalJoin join = (LogicalJoin) rel;
            final RelTraitSet traitSet = join.getTraitSet().replace(out);
            return new SpliceJoin(join.getCluster(), traitSet, convert(join.getLeft(), out), convert(join.getRight(), out),
                    join.getCondition(), join.getJoinType());
        }
    }

    private static class SpliceValuesRule extends SpliceConverterRule {
        private static final SpliceValuesRule INSTANCE = new SpliceValuesRule();

        private SpliceValuesRule() {
            super(LogicalValues.class, Convention.NONE, SpliceRelNode.CONVENTION, "SpliceValuesRule");
        }

        public RelNode convert(RelNode rel) {
            final LogicalValues values = (LogicalValues) rel;
            final RelTraitSet traitSet = values.getTraitSet().replace(out);
            return new SpliceValues(values.getCluster(), values.getRowType(), values.getTuples(), traitSet);
        }
    }
}