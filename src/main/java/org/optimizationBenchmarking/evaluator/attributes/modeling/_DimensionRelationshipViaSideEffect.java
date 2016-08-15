package org.optimizationBenchmarking.evaluator.attributes.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/**
 * This class is used to insert a dimension relationship via a side effect.
 */
final class _DimensionRelationshipViaSideEffect
    extends _ModelAttributeBase<IFittingResult> {

  /** the side-effect result, will be {@code null}ed after computation */
  IFittingResult m_result;

  /**
   * create the model attribute base.
   *
   * @param owner
   *          the owner
   */
  public _DimensionRelationshipViaSideEffect(
      final DimensionRelationshipAndData owner) {
    super(EAttributeType.PERMANENTLY_STORED, true, owner);
  }

  /** {@inheritDoc} */
  @Override
  protected final IFittingResult compute(final IInstanceRuns data,
      final Logger logger) {
    return this.m_result;
  }
}
