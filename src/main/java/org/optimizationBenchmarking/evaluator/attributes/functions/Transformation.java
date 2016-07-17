package org.optimizationBenchmarking.evaluator.attributes.functions;

import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.utils.ICloneable;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.hash.HashObject;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.math.functions.basic.Identity;
import org.optimizationBenchmarking.utils.math.text.DefaultParameterRenderer;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * A unary function which receives values from a given dimension as input
 * and transforms potentially them based on information obtain from
 * experiment parameters or instance features.
 */
public class Transformation extends HashObject {

  /** the function to be applied to the input data */
  final UnaryFunction m_func;

  /**
   * Create the data transformation
   *
   * @param function
   *          the function to be applied
   */
  Transformation(final UnaryFunction function) {
    super();

    if (function == null) {
      throw new IllegalArgumentException(//
          "The transformation function cannot be null."); //$NON-NLS-1$
    }

    this.m_func = function;
  }

  /**
   * Create the identity transformation
   */
  public Transformation() {
    this(Identity.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected int calcHashCode() {
    return this.m_func.hashCode();
  }

  /**
   * Check whether this function represents an identity transformation of
   * the input to the output, i.e., if it just returns its input directly.
   *
   * @return {@code true} if this functions is an identity transformation,
   *         {@code false} otherwise
   */
  public final boolean isIdentityTransformation() {
    return (this.m_func instanceof Identity);
  }

  /**
   * Check whether this function is accurate in {@code long} arithmetic:
   * The result can always be represented as a {@code long} without loss of
   * fidelity, if a {@code long} is the input, if this method returns
   * {@code true}.
   *
   * @return {@code true} if the {@code long} arithmetic of this
   *         transformation is accurate
   */
  public boolean isLongArithmeticAccurate() {
    return this.m_func.isLongArithmeticAccurate();
  }

  /**
   * Provide the data transformation function based on a given data element
   *
   * @param element
   *          the data element
   * @return the transformation function
   */
  public final UnaryFunction use(final IDataElement element) {
    UnaryFunction useFunction;

    useFunction = ((this.m_func instanceof ICloneable)
        ? ((UnaryFunction) (((ICloneable) (this.m_func)).clone()))
        : this.m_func);

    Transformation.__use(useFunction, element);
    return useFunction;
  }

  /**
   * recursively iterate over the elements of a function to set up all
   * nested data-based constants
   *
   * @param setup
   *          the object to set up
   * @param element
   *          the data element
   */
  @SuppressWarnings("rawtypes")
  private static final void __use(final Object setup,
      final IDataElement element) {
    if (setup instanceof _DataBasedConstant) {
      ((_DataBasedConstant) setup)._update(element);
    }
    if (setup instanceof Iterable) {
      for (final Object object : ((Iterable) setup)) {
        Transformation.__use(object, element);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Transformation) {
      return Compare.equals(this.m_func, ((Transformation) o).m_func);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final MemoryTextOutput mto;

    mto = new MemoryTextOutput();
    this.m_func.mathRender(mto, DefaultParameterRenderer.INSTANCE);
    return mto.toString();
  }

}
