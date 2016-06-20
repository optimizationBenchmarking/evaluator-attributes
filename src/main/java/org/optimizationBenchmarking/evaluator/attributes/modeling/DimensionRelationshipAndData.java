package org.optimizationBenchmarking.evaluator.attributes.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/**
 * A dimension relationship model is a function which relates the values of
 * one dimension to the values of another. This attribute returns such a
 * fitting plus a quality metric which can be used to assess the quality of
 * other fittings on the same data. This combined result is never stored.
 * However, the fitting will be preserved as {@link DimensionRelationship}
 * attribute.
 */
public final class DimensionRelationshipAndData
    extends _ModelAttributeBase<DimensionRelationshipData> {

  /**
   * create the model attribute base.
   *
   * @param dimX
   *          the model input dimension
   * @param dimY
   *          the model output dimension
   */
  public DimensionRelationshipAndData(final IDimension dimX,
      final IDimension dimY) {
    super(EAttributeType.NEVER_STORED, dimX, dimY, 332287);
  }

  /** {@inheritDoc} */
  @Override
  protected final DimensionRelationshipData compute(
      final IInstanceRuns data, final Logger logger) {
    final DimensionRelationshipData res;
    IFittingResult fitting1, fitting2;
    _DimensionRelationshipViaSideEffect side;

    side = new _DimensionRelationshipViaSideEffect(this);
    try {
      fitting1 = side.get(data, logger);
    } catch (@SuppressWarnings("unused") final Throwable expected) {
      // we expect an error here, since {@code null} is no valid attribute
      // value
      fitting1 = null;
    }

    if (fitting1 != null) {
      // Oh, a fitting has already been computed before. We just need to
      // set up the quality measure.
      return new DimensionRelationshipData(fitting1, //
          this._getMeasure(this._getDataMatrix(data)));
    }

    // No fitting has been computed yet. Let's do it.
    res = this._compute(data, logger);

    // Since computing the fitting is very expensive, we try to preserve
    // it. Unfortunately, this must be done via some side-effects. To sneak
    // it under DimensionRelationship.
    fitting1 = res.fitting;

    side.m_result = res.fitting;
    fitting2 = side.get(data, logger);
    side.m_result = null;
    side = null;

    if (fitting1 != fitting2) {
      // Oh, there was already a fitting ... then use the stored one
      return new DimensionRelationshipData(fitting2, res.measure);
    }
    return res;
  }
}
