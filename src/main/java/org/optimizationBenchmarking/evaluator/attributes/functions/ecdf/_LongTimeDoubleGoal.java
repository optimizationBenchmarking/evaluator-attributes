package org.optimizationBenchmarking.evaluator.attributes.functions.ecdf;

import org.optimizationBenchmarking.evaluator.attributes.functions.TransformationFunction;
import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;

/** the time are longs and goals are doubles */
final class _LongTimeDoubleGoal extends _Longs {

  /** the goal */
  private final double m_goal;

  /**
   * create the {@code long} list
   *
   * @param timeDim
   *          the time dimension
   * @param goalDim
   *          the goal dimension
   * @param criterion
   *          the goal criterion
   * @param goalTransform
   *          the goal transformation
   * @param goal
   *          the goal
   */
  _LongTimeDoubleGoal(final IDimension timeDim, final IDimension goalDim,
      final EComparison criterion,
      final TransformationFunction goalTransform, final double goal) {
    super(timeDim, goalDim, criterion, goalTransform);
    this.m_goal = goal;
  }

  /** {@inheritDoc} */
  @Override
  final void _addRun(final IRun run) {
    final IDataPoint dp;
    final long last;
    final UnaryFunction goalTransform;

    if (run != null) {
      this.m_total++;

      if (this.m_useGoalTransformAndCriterion) {
        goalTransform = this.m_goalTransform;
        finder: {
          for (final IDataPoint dpx : run.getData()) {
            if (this.m_criterion.compare(//
                goalTransform.computeAsDouble(//
                    dpx.getDouble(this.m_goalIndex)),
                this.m_goal)) {
              dp = dpx;
              break finder;
            }
          }
          dp = null;
        }
      } else {
        dp = run.find(this.m_goalIndex, this.m_goal);
      }

      if (dp != null) {
        this._add(dp.getLong(this.m_timeIndex));
      }

      last = run.getLong((run.m() - 1), this.m_timeIndex);
      if (this.m_isTimeIncreasing) {
        this.m_last = Math.max(this.m_last, last);
      } else {
        this.m_last = Math.min(this.m_last, last);
      }
    }
  }
}
