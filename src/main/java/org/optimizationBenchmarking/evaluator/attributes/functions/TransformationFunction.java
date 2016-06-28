package org.optimizationBenchmarking.evaluator.attributes.functions;

import java.util.Iterator;

import org.optimizationBenchmarking.utils.collections.iterators.InstanceIterator;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A function context allowing the use of an initialized data
 * transformation.
 */
public final class TransformationFunction extends UnaryFunction
    implements Iterable<Object> {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the data transformation */
  private final Transformation m_transformation;
  /** the internal transformation function */
  private final UnaryFunction m_function;

  /**
   * create the function context
   *
   * @param trafo
   *          the data transformation
   * @param function
   *          the internal transformation function
   */
  TransformationFunction(final Transformation trafo,
      final UnaryFunction function) {
    super();
    if (trafo == null) {
      throw new IllegalArgumentException(
          "Data transformation cannot be null."); //$NON-NLS-1$
    }
    this.m_transformation = trafo;
    this.m_function = function;
  }

  /**
   * Check whether this function represents an identity transformation of
   * the input to the output, i.e., if it just returns its input directly.
   *
   * @return {@code true} if this functions is an identity transformation,
   *         {@code false} otherwise
   */
  public final boolean isIdentityTransformation() {
    return this.m_transformation.isIdentityTransformation();
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (this.m_transformation.hashCode() ^ 3455269);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof TransformationFunction) {
      return Compare.equals(this.m_transformation,
          ((TransformationFunction) o).m_transformation);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final byte computeAsByte(final byte x0) {
    return this.m_function.computeAsByte(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final short computeAsShort(final short x0) {
    return this.m_function.computeAsShort(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final int computeAsInt(final int x0) {
    return this.m_function.computeAsInt(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final long computeAsLong(final long x0) {
    return this.m_function.computeAsLong(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final float computeAsFloat(final float x0) {
    return this.m_function.computeAsFloat(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final double computeAsDouble(final double x0) {
    return this.m_function.computeAsDouble(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final double computeAsDouble(final long x0) {
    return this.m_function.computeAsDouble(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final double computeAsDouble(final int x0) {
    return this.m_function.computeAsDouble(x0);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isLongArithmeticAccurate() {
    return this.m_function.isLongArithmeticAccurate();
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    this.m_function.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    this.m_function.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final Iterator<Object> iterator() {
    return new InstanceIterator<Object>(this.m_function);
  }
}