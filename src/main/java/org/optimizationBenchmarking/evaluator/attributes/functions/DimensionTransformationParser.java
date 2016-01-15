package org.optimizationBenchmarking.evaluator.attributes.functions;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.math.functions.compound.FunctionBuilder;

/**
 * A parser for dimension transformations.
 */
public final class DimensionTransformationParser
    extends _TransformationParser<DimensionTransformation> {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the dimension */
  private transient IDimension m_dimension;

  /**
   * create the dimension transformation parser
   *
   * @param experimentSet
   *          the experiment set
   */
  public DimensionTransformationParser(
      final IExperimentSet experimentSet) {
    super(experimentSet);
  }

  /** {@inheritDoc} */
  @Override
  final DimensionTransformation _createTransformation(
      final UnaryFunction function, final _DataBasedConstant[] constants) {
    final IDimension dim;

    dim = this.m_dimension;
    this.m_dimension = null;
    this.m_unary = null;

    if (dim == null) {
      throw new IllegalArgumentException(//
          "You must specify a source dimension for a dimension transformation source."); //$NON-NLS-1$
    }
    return new DimensionTransformation(function, constants, dim);
  }

  /** {@inheritDoc} */
  @Override
  final UnaryFunction _resolveUnknownName(final String name,
      final FunctionBuilder<UnaryFunction> builder) {
    IDimension dim;

    dim = this.m_experimentSet.getDimensions().find(name);
    if (dim != null) {
      if (this.m_dimension == null) {
        this.m_dimension = dim;
        this.m_unary = builder.parameter(0);
      } else {
        if (!(Compare.equals(this.m_dimension, dim))) {
          throw new IllegalArgumentException(//
              "A dimension transformation can only have one source dimension, but you specified " //$NON-NLS-1$
                  + this.m_dimension + " and " + dim);//$NON-NLS-1$
        }
      }

      return this.m_unary;
    }

    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final Class<DimensionTransformation> getOutputClass() {
    return DimensionTransformation.class;
  }
}
