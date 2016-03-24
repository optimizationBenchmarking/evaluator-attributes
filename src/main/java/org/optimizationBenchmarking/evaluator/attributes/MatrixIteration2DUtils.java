package org.optimizationBenchmarking.evaluator.attributes;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.CallableMatrixIteration2DBuilder;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.EIterationDirection;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.EIterationMode;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.EMissingValueMode;

/** Some static helper function for supporting 2D matrix iterations. */
public final class MatrixIteration2DUtils {

  /**
   * Set up a builder to iterate over data coming from the dimensions
   * specified.
   *
   * @param builder
   *          the builder
   * @param x
   *          the {@code x} dimension
   * @param y
   *          the {@code y} dimension
   * @param allowLateStart
   *          if some of the matrices start earlier, can we just skip the
   *          rest ({@code true}) or should we use extremal values (
   *          {@code false})?
   * @param allowEarlyEnd
   *          if some of the matrices have larger {@code x} values than
   *          others, can we just skip the result ({@code true}) or set
   *          extremal values ({@code false})?
   */
  public static final void setupDimensionProperties(
      final CallableMatrixIteration2DBuilder<?> builder,
      final IDimension x, final IDimension y, final boolean allowLateStart,
      final boolean allowEarlyEnd) {
    final boolean yIsTime;

    builder.setXDimension(x.getIndex());
    builder.setXDirection(x.getDirection().isIncreasing() //
        ? EIterationDirection.INCREASING//
        : EIterationDirection.DECREASING);

    builder.setYDimension(y.getIndex());

    // If y is an objective value dimension and there is no y value for the
    // current x value, we assume that we did not yet improve and take the
    // previous y value. If y is a time dimension and there is no y value
    // for the current x value, we assume the time of the next x value.
    yIsTime = y.getDimensionType().isTimeMeasure();
    builder.setIterationMode(yIsTime//
        ? EIterationMode.PREVIEW_NEXT//
        : EIterationMode.KEEP_PREVIOUS);

    if (allowLateStart) {
      builder.setStartMode(EMissingValueMode.SKIP);
    } else {
      builder.setStartReplacement(y.getDirection().isIncreasing()//
          ? Double.valueOf(Double.NEGATIVE_INFINITY)//
          : Double.valueOf(Double.POSITIVE_INFINITY));
    }

    if (allowEarlyEnd) {
      builder.setEndMode(EMissingValueMode.SKIP);
    } else {
      if (yIsTime) {
        builder.setEndReplacement(y.getDirection().isIncreasing()//
            ? Double.valueOf(Double.POSITIVE_INFINITY)//
            : Double.valueOf(Double.NEGATIVE_INFINITY));
      } else {
        builder.setEndMode(EMissingValueMode.USE_ITERATION_MODE);
      }
    }
  }

  /** the forbidden constructor */
  private MatrixIteration2DUtils() {
    ErrorUtils.doNotCall();
  }
}
