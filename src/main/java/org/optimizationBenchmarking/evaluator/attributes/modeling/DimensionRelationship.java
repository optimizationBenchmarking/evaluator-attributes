package org.optimizationBenchmarking.evaluator.attributes.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/**
 * A dimension relationship model is a function which relates the values of
 * one dimension to the values of another. Computing such fittings is very
 * expensive and somewhat non-deterministic, so we preserve them at any
 * cost.
 */
public final class DimensionRelationship
    extends _ModelAttributeBase<IFittingResult> {

  /**
   * create the model attribute base.
   *
   * @param dimX
   *          the model input dimension
   * @param dimY
   *          the model output dimension
   */
  public DimensionRelationship(final DimensionTransformation dimX,
      final DimensionTransformation dimY) {
    super(EAttributeType.PERMANENTLY_STORED, dimX, dimY, true);
  }

  /** {@inheritDoc} */
  @Override
  protected final IFittingResult compute(final IInstanceRuns data,
      final Logger logger) {
    return this._compute(data, logger).fitting;
  }
}
