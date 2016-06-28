package org.optimizationBenchmarking.evaluator.attributes.functions;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.utils.math.functions.MathematicalFunction;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.math.functions.compound.FunctionBuilder;
import org.optimizationBenchmarking.utils.math.functions.compound.UnaryFunctionBuilder;
import org.optimizationBenchmarking.utils.math.text.AbstractNameResolver;
import org.optimizationBenchmarking.utils.math.text.CompoundFunctionParser;
import org.optimizationBenchmarking.utils.parsers.Parser;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * A parser which can translate a string to a data transformation.
 *
 * @param <TT>
 *          the transformation type
 */
abstract class _TransformationParser<TT extends Transformation>
    extends Parser<TT> {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the internal function parser */
  private final CompoundFunctionParser<UnaryFunction> m_functionParser;

  /** the property resolver */
  private final __PropertyResolver m_resolver;

  /** the experiment set */
  final IExperimentSet m_experimentSet;

  /**
   * the unary function for the parameter: must be reset to {@code null} by
   * {@link #_createTransformation(UnaryFunction)}
   */
  transient UnaryFunction m_unary;

  /**
   * create the transformation parser
   *
   * @param experimentSet
   *          the experiment set
   */
  _TransformationParser(final IExperimentSet experimentSet) {
    super();

    if (experimentSet == null) {
      throw new IllegalArgumentException(//
          "Experiment set cannot be null."); //$NON-NLS-1$
    }
    this.m_experimentSet = experimentSet;

    this.m_resolver = new __PropertyResolver();
    this.m_functionParser = new CompoundFunctionParser<>(
        UnaryFunctionBuilder.getInstance(), this.m_resolver);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final TT parseString(final String string) {
    final UnaryFunction function;

    function = this.m_functionParser.parseString(string);
    return this._createTransformation(function);
  }

  /**
   * Create the transformation
   *
   * @param function
   *          the parsed function
   * @return the created transformation
   */
  abstract TT _createTransformation(final UnaryFunction function);

  /**
   * Resolve an otherwise unassigned name
   *
   * @param name
   *          the name
   * @param builder
   *          the function builder
   * @return the function corresponding to the name
   */
  UnaryFunction _resolveUnknownName(final String name,
      final FunctionBuilder<UnaryFunction> builder) {
    return null;
  }

  /** the internal entity resolver */
  private final class __PropertyResolver extends AbstractNameResolver {

    /** create the internal property resolver */
    __PropertyResolver() {
      super();
    }

    /** {@inheritDoc} */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public synchronized final MathematicalFunction resolve(
        final String name, final FunctionBuilder<?> builder) {
      final String processed, lower, use;
      _DataBasedConstant constant;
      final IDimension dim;
      final IFeature feature;
      final IParameter parameter;
      Number namedConst;
      UnaryFunction unknown;
      boolean upper;

      processed = TextUtils.prepare(name);

      if (processed != null) {
        constant = null;

        feature = _TransformationParser.this.m_experimentSet.getFeatures()
            .find(name);
        if (feature != null) {
          constant = new _FeatureConstant(feature);
        } else {
          parameter = _TransformationParser.this.m_experimentSet
              .getParameters().find(name);
          if (parameter != null) {
            constant = new _ParameterConstant(parameter);
          } else {
            lower = TextUtils.toLowerCase(processed);

            bound: {
              if (lower.endsWith(_BoundConstant.LOWER_BOUND_END)) {
                use = processed.substring(0, (processed.length()
                    - _BoundConstant.LOWER_BOUND_END.length()));
                upper = false;
              } else {
                if (lower.endsWith(_BoundConstant.UPPER_BOUND_END)) {
                  upper = true;
                  use = processed.substring(0, (processed.length()
                      - _BoundConstant.UPPER_BOUND_END.length()));
                } else {
                  break bound;
                }
              }

              dim = _TransformationParser.this.m_experimentSet
                  .getDimensions().find(use);
              if (dim != null) {
                constant = new _BoundConstant(dim, upper);
              }
            }
          }
        }

        if (constant != null) {
          return builder.constant(constant);
        }

        namedConst = AbstractNameResolver.resolveDefaultConstant(name);
        if (namedConst != null) {
          return builder.constant(namedConst);
        }

        unknown = _TransformationParser.this._resolveUnknownName(name,
            ((FunctionBuilder) builder));
        if (unknown != null) {
          return unknown;
        }
      }

      return super.resolve(name, builder);
    }
  }
}
