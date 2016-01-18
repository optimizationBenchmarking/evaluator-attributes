package test.junit.org.optimizationBenchmarking.experimentation.attributes.functions;

import java.util.Random;

import org.junit.Assert;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.NamedParameterTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.Transformation;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;
import org.optimizationBenchmarking.utils.math.statistics.parameters.ArithmeticMean;
import org.optimizationBenchmarking.utils.math.statistics.parameters.InterQuartileRange;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Maximum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Median;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Minimum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StandardDeviation;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameter;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Variance;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/** Some utilities for testing functions. */
public final class FunctionTestUtils {

  /** the forbidden constructor */
  private FunctionTestUtils() {
    ErrorUtils.doNotCall();
  }

  /**
   * Check the function matrix
   *
   * @param matrix
   *          the function matrix
   */
  public static final void checkFunctionMatrix(final IMatrix matrix) {
    Assert.assertNotNull(matrix);
    Assert.assertEquals(2, matrix.n());
    Assert.assertTrue(matrix.m() > 0);
  }

  /**
   * Make a dimension string
   *
   * @param dimName
   *          the dimension name
   * @param random
   *          the random number generator
   * @param mto
   *          the memory text output
   */
  private static final void __makeDimString(final String dimName,
      final Random random, final MemoryTextOutput mto) {
    switch (random.nextInt(7)) {
      case 0: {
        mto.append('(');
        mto.append(random.nextDouble());
        mto.append(random.nextBoolean() ? '+' : '*');
        FunctionTestUtils.__makeDimString(dimName, random, mto);
        mto.append(')');
        return;
      }
      case 1: {
        mto.append('(');
        FunctionTestUtils.__makeDimString(dimName, random, mto);
        mto.append(random.nextBoolean() ? '/' : '-');
        mto.append(1d + random.nextDouble());
        mto.append(')');
        return;
      }
      default: {
        mto.append(dimName);
      }
    }
  }

  /**
   * Create the function parameters
   *
   * @param data
   *          the data
   * @param random
   *          the random number generator
   * @return the parameters
   */
  public static final FunctionParameters createFunctionParameters(
      final IExperimentSet data, final Random random) {
    final ArrayListView<? extends IDimension> dims;
    final int size;
    final DimensionTransformationParser dimParser;
    final MemoryTextOutput mto;
    final DimensionTransformation x, y;
    final Transformation trafo;
    int looper;
    IDimension dimX, dimY, dimTemp;
    EDimensionType typeX, typeY, typeTemp;

    dims = data.getDimensions().getData();
    Assert.assertNotNull(dims);
    size = dims.size();
    Assert.assertTrue(size > 0);

    looper = 4;
    findDims: do {
      dimX = dims.get(random.nextInt(size));
      typeX = dimX.getDimensionType();
      dimY = dims.get(random.nextInt(size));
      typeY = dimY.getDimensionType();

      if (typeX.isSolutionQualityMeasure() && typeY.isTimeMeasure()) {
        dimTemp = dimY;
        dimY = dimX;
        dimX = dimTemp;
        typeTemp = typeX;
        typeX = typeY;
        typeY = typeTemp;
      }
      if (typeX.isTimeMeasure() && typeY.isSolutionQualityMeasure()) {
        break findDims;
      }
    } while ((--looper) > 0);

    dimParser = new DimensionTransformationParser(data);
    mto = new MemoryTextOutput();
    FunctionTestUtils.__makeDimString(dimX.getName(), random, mto);
    x = dimParser.parseString(mto.toString());
    mto.clear();
    FunctionTestUtils.__makeDimString(dimY.getName(), random, mto);
    y = dimParser.parseString(mto.toString());
    mto.clear();
    FunctionTestUtils.__makeDimString(
        NamedParameterTransformationParser.DEFAULT_PARAMETER_NAME, random,
        mto);
    trafo = new NamedParameterTransformationParser(data,
        NamedParameterTransformationParser.DEFAULT_PARAMETER_NAME)
            .parseString(mto.toString());
    mto.clear();

    return new FunctionParameters(dimX, dimY, x, y, trafo);
  }

  /**
   * Get a statistical parameter
   *
   * @param random
   *          the random number generator
   * @return the randomly chosen parameter
   */
  public static final StatisticalParameter getStatisticalParameter(
      final Random random) {
    switch (random.nextInt(6)) {
      case 0: {
        return ArithmeticMean.INSTANCE;
      }
      case 1: {
        return InterQuartileRange.INSTANCE;
      }
      case 2: {
        return Maximum.INSTANCE;
      }
      case 3: {
        return Median.INSTANCE;
      }
      case 4: {
        return Minimum.INSTANCE;
      }
      case 5: {
        return StandardDeviation.INSTANCE;
      }
      default: {
        return Variance.INSTANCE;
      }
    }
  }

  /** The function parameters */
  public static final class FunctionParameters {

    /** the transformation of the {@code x}-axis */
    public final DimensionTransformation xAxisTransformation;

    /**
     * the transformation to be applied to the data of the {@code y}-axis
     * before being fed to the actual computation
     */
    public final DimensionTransformation yAxisInputTransformation;

    /**
     * the transformation of the result of the function applied to the data
     * on the {@code y}-axis
     */
    public final Transformation yAxisOutputTransformation;

    /** the {@code x}-dimension */
    public final IDimension xDim;

    /** the {@code y}-dimension */
    public final IDimension yDim;

    /**
     * create
     *
     * @param _xDim
     *          the {@code x}-dimension
     * @param _yDim
     *          the {@code y}-dimension
     * @param _xAxisTransformation
     *          the transformation of the {@code x}-axis
     * @param _yAxisInputTransformation
     *          the transformation to be applied to the data of the
     *          {@code y}-axis before being fed to the actual computation
     * @param _yAxisOutputTransformation
     *          the transformation of the result of the function applied to
     *          the data on the {@code y}-axis
     */
    FunctionParameters(final IDimension _xDim, final IDimension _yDim,
        final DimensionTransformation _xAxisTransformation,
        final DimensionTransformation _yAxisInputTransformation,
        final Transformation _yAxisOutputTransformation) {
      super();
      this.xDim = _xDim;
      this.yDim = _yDim;
      this.xAxisTransformation = _xAxisTransformation;
      this.yAxisInputTransformation = _yAxisInputTransformation;
      this.yAxisOutputTransformation = _yAxisOutputTransformation;
    }
  }
}