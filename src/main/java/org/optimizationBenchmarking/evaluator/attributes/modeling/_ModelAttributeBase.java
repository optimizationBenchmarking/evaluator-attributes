package org.optimizationBenchmarking.evaluator.attributes.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.math.matrix.AbstractMatrix;
import org.optimizationBenchmarking.utils.math.matrix.impl.DoubleMatrix1D;
import org.optimizationBenchmarking.utils.math.matrix.impl.LongMatrix1D;
import org.optimizationBenchmarking.utils.ml.fitting.multi.MultiFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.quality.WeightedRootMeanSquareError;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingQualityMeasure;

/**
 * The base class for model attributes.
 *
 * @param <R>
 *          the result type
 */
abstract class _ModelAttributeBase<R> extends Attribute<IInstanceRuns, R> {

  /** return {@code long} data based on {@code long} data */
  private static final int LONG_IN_LONG_OUT = 0;
  /** return {@code double} data based on {@code long} inputs */
  private static final int LONG_IN_DOUBLE_OUT = (_ModelAttributeBase.LONG_IN_LONG_OUT
      + 1);
  /** return {@code double} data based on {@code double} inputs */
  private static final int DOUBLE_IN_DOUBLE_OUT = (_ModelAttributeBase.LONG_IN_DOUBLE_OUT
      + 1);

  /** the dimension to be used as model input */
  private final DimensionTransformation m_dimX;
  /** the dimension to be used as model output */
  private final DimensionTransformation m_dimY;
  /** the class */
  private final boolean m_class;

  /**
   * create the model attribute base.
   *
   * @param type
   *          the attribute type
   * @param dimX
   *          the model input dimension
   * @param dimY
   *          the model output dimension
   * @param clazz
   *          the class
   */
  _ModelAttributeBase(final EAttributeType type,
      final DimensionTransformation dimX,
      final DimensionTransformation dimY, final boolean clazz) {
    super(type);

    if (dimX == null) {
      throw new IllegalArgumentException("x dimension cannot be null."); //$NON-NLS-1$
    }

    if (dimY == null) {
      throw new IllegalArgumentException("y dimension cannot be null."); //$NON-NLS-1$
    }

    DimensionRelationshipModels._checkDimensions(
        dimX.getDimension().getDimensionType(),
        dimY.getDimension().getDimensionType());

    this.m_dimX = dimX;
    this.m_dimY = dimY;
    this.m_class = clazz;
  }

  /**
   * create the model attribute base.
   *
   * @param type
   *          the attribute type
   * @param clazz
   *          the class id
   * @param copy
   *          the attribute to copy
   */
  _ModelAttributeBase(final EAttributeType type, final boolean clazz,
      final _ModelAttributeBase<?> copy) {
    this(type, copy.m_dimX, copy.m_dimY, clazz);
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(//
        HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_dimX), //
            HashUtils.hashCode(this.m_dimY)), //
        HashUtils.hashCode(this.m_class));//
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    _ModelAttributeBase<?> eq;

    if (o == this) {
      return true;
    }
    if (o instanceof _ModelAttributeBase) {
      eq = ((_ModelAttributeBase<?>) o);
      return ((Compare.equals(this.m_dimX, eq.m_dimX)) && //
          (Compare.equals(this.m_dimY, eq.m_dimY)) && //
          (this.m_class == eq.m_class));
    }
    return false;
  }

  /**
   * compute the dimension type switch for a given dimension
   *
   * @param dim
   *          the dimension
   * @return the switch
   */
  private static final int __getDimSwitch(
      final DimensionTransformation dim) {
    if (dim.isIdentityTransformation() || dim.isLongArithmeticAccurate()) {
      return (dim.getDimension().getDataType().isInteger()
          ? _ModelAttributeBase.LONG_IN_LONG_OUT
          : _ModelAttributeBase.LONG_IN_DOUBLE_OUT);
    }
    return _ModelAttributeBase.DOUBLE_IN_DOUBLE_OUT;
  }

  /**
   * compute the data matrix.
   *
   * @param data
   *          the data
   * @return the matrix
   */
  final AbstractMatrix _getDataMatrix(final IInstanceRuns data) {
    final ArrayListView<? extends IRun> rawData;
    final double[] doubleMatrixData;
    final long[] longMatrixData;
    final UnaryFunction x, y;
    final int xFormat, yFormat, xIndex, yIndex;

    int totalRows, outIndex, inIndex;

    rawData = data.getData();
    totalRows = 0;
    for (final IRun run : rawData) {
      totalRows += run.m();
    }

    x = this.m_dimX.use(data);
    xFormat = _ModelAttributeBase.__getDimSwitch(this.m_dimX);
    xIndex = this.m_dimX.getDimension().getIndex();

    y = this.m_dimY.use(data);
    yFormat = _ModelAttributeBase.__getDimSwitch(this.m_dimY);
    yIndex = this.m_dimX.getDimension().getIndex();

    outIndex = (totalRows << 1);

    if (Math.max(xFormat,
        yFormat) >= _ModelAttributeBase.LONG_IN_DOUBLE_OUT) {

      doubleMatrixData = new double[outIndex];
      for (final IRun run : rawData) {
        for (inIndex = run.m(); (--inIndex) >= 0;) {
          doubleMatrixData[--outIndex] = //
          ((yFormat <= _ModelAttributeBase.LONG_IN_DOUBLE_OUT)//
              ? y.computeAsDouble(run.getLong(inIndex, yIndex))//
              : y.computeAsDouble(run.getDouble(inIndex, yIndex)));
          doubleMatrixData[--outIndex] = //
          ((xFormat <= _ModelAttributeBase.LONG_IN_DOUBLE_OUT)//
              ? x.computeAsDouble(run.getLong(inIndex, xIndex))//
              : x.computeAsDouble(run.getDouble(inIndex, xIndex)));
        }
      }

      if (outIndex == 0) {
        return new DoubleMatrix1D(doubleMatrixData, totalRows, 2);
      }
    } else {

      longMatrixData = new long[outIndex];
      for (final IRun run : rawData) {
        for (inIndex = run.m(); (--inIndex) >= 0;) {
          longMatrixData[--outIndex] = y
              .computeAsLong(run.getLong(inIndex, yIndex));

          longMatrixData[--outIndex] = x
              .computeAsLong(run.getLong(inIndex, xIndex));
        }
      }

      if (outIndex == 0) {
        return new LongMatrix1D(longMatrixData, totalRows, 2);
      }
    }

    throw new IllegalStateException("The lengths of the runs changed?"); //$NON-NLS-1$
  }

  /**
   * get the fitting quality measure
   *
   * @param matrix
   *          the matrix
   * @return the measure
   */
  final IFittingQualityMeasure _getMeasure(final AbstractMatrix matrix) {
    return new WeightedRootMeanSquareError(matrix);
  }

  /**
   * Perform the computation
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @return the result
   */
  final DimensionRelationshipData _compute(final IInstanceRuns data,
      final Logger logger) {
    final AbstractMatrix matrix;
    final IFittingQualityMeasure measure;

    matrix = this._getDataMatrix(data);
    measure = this._getMeasure(matrix);

    return new DimensionRelationshipData(//
        MultiFunctionFitter.getInstance().use()//
            .setLogger(logger)//
            .setFitters(DimensionRelationshipModels._getFitters())//
            .setFunctionsToFit(DimensionRelationshipModels.getModels(//
                this.m_dimX.getDimension(), this.m_dimY.getDimension()))//
            .setQualityMeasure(measure)//
            .setPoints(matrix).create().call(), //
        measure);
  }
}
