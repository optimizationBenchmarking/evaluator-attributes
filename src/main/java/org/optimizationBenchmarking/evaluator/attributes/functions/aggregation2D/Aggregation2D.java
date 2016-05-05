package org.optimizationBenchmarking.evaluator.attributes.functions.aggregation2D;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.MatrixIteration2DUtils;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.FunctionAttribute;
import org.optimizationBenchmarking.evaluator.attributes.functions.NamedParameterTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.Transformation;
import org.optimizationBenchmarking.evaluator.attributes.functions.TransformationFunction;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IElementSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;
import org.optimizationBenchmarking.utils.math.matrix.processing.ColumnTransformedMatrix;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.CallableMatrixIteration2DBuilder;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.Matrix2DAggregate;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.ScalarAggregate;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Median;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameter;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameterParser;
import org.optimizationBenchmarking.utils.math.text.DefaultParameterRenderer;
import org.optimizationBenchmarking.utils.parallel.Execute;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * The 2D statistical parameter curve, which can be computed over instance
 * runs, experiments, and experiment sets
 */
public final class Aggregation2D extends FunctionAttribute<IElementSet> {

  /**
   * The default parameter for the aggregate to be computed over the
   * instance runs.
   */
  public static final String PRIMARY_AGGREGATE_PARAM = "aggregate"; //$NON-NLS-1$

  /**
   * The default parameter for the aggregate to be used to aggregate the
   * results of the instance runs in an experiment or experiment set.
   */
  public static final String SECONDARY_AGGREGATE_PARAM = "secondaryAggregate"; //$NON-NLS-1$

  /**
   * the parameter to be computed after applying the
   * {@link #getYAxisInputTransformation()}
   */
  private final StatisticalParameter m_param;
  /** the x-dimension index */
  private final int m_xIndex;
  /** the y-dimension index */
  private final int m_yIndex;

  /** the secondary parameter */
  private final StatisticalParameter m_second;

  /**
   * create the raw statistics parameter
   *
   * @param xAxisTransformation
   *          the transformation to be applied to the {@code x}-axis
   * @param yAxisInputTransformation
   *          the transformation to be applied to the data of the {@code y}
   *          -axis before being fed to the actual computation
   * @param yAxisOutputTransformation
   *          the transformation of the result of the function applied to
   *          the data on the {@code y}-axis.
   * @param param
   *          the parameter to be computed after applying the
   *          {@code yTransformation} (if any {@code yTransformation} is
   *          specified, otherwise it is computed directly)
   * @param secondary
   *          the secondary aggregate, {@code null} for default (median)
   */
  public Aggregation2D(final DimensionTransformation xAxisTransformation,
      final DimensionTransformation yAxisInputTransformation,
      final Transformation yAxisOutputTransformation,
      final StatisticalParameter param,
      final StatisticalParameter secondary) {
    super(EAttributeType.NEVER_STORED, xAxisTransformation,
        yAxisInputTransformation, yAxisOutputTransformation);

    if (param == null) {
      throw new IllegalArgumentException(//
          "Statistical parameter cannot be null."); //$NON-NLS-1$
    }
    if (secondary == null) {
      this.m_second = Median.INSTANCE;
    } else {
      this.m_second = secondary;
    }

    this.m_xIndex = xAxisTransformation.getDimension().getIndex();
    this.m_yIndex = yAxisInputTransformation.getDimension().getIndex();
    this.m_param = param;
  }

  /**
   * Get the statistical parameter to be computed over the
   * {@link #getYAxisInputTransformation() transformed} values of the
   * {@code y}-axis for each instance run.
   *
   * @return the statistical parameter to be computed over the
   *         {@link #getYAxisInputTransformation() transformed} values of
   *         the {@code y}-axis.
   */
  public final StatisticalParameter getStatisticalParameter() {
    return this.m_param;
  }

  /**
   * Get the statistical parameter to be computed over the
   * {@link #getStatisticalParameter() statistics} obtained from an
   * instance run set to joint the values for experiments.
   *
   * @return the statistical parameter to be computed over the
   *         {@link #getStatisticalParameter() statistics} obtained from an
   *         instance run set to joint the values for experiments.
   */
  public final StatisticalParameter getSecondaryStatisticalParameter() {
    return this.m_second;
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    final DimensionTransformation xIn, yIn;
    final Transformation yOut;
    ETextCase use;

    use = super.printDescription(textOut, textCase).nextCase();

    yIn = this.getYAxisInputTransformation();
    xIn = this.getXAxisTransformation();
    yOut = this.getYAxisOutputTransformation();

    textOut.append(" The "); //$NON-NLS-1$
    if (textOut instanceof IComplexText) {
      try (final IMath math = ((IComplexText) textOut).inlineMath()) {
        this.yAxisMathRender(math, DefaultParameterRenderer.INSTANCE);
      }
    } else {
      this.yAxisMathRender(textOut, DefaultParameterRenderer.INSTANCE);
    }
    textOut.append(" represents the "); //$NON-NLS-1$
    this.m_param.printLongName(textOut, use);
    textOut.append(" of the "); //$NON-NLS-1$
    yIn.printShortName(textOut, use);
    textOut.append(' ');
    textOut.append(" for a given ellapsed runtime measured in "); //$NON-NLS-1$
    xIn.getDimension().printShortName(textOut, use);

    if (yOut.isIdentityTransformation()) {
      textOut.append(". The "); //$NON-NLS-1$
      this.m_param.printLongName(textOut, use);
    } else {
      textOut.append(//
          ". We do not use these values directly, but instead compute "); //$NON-NLS-1$
      if (textOut instanceof IComplexText) {
        try (final IMath math = ((IComplexText) textOut).inlineMath()) {
          this.yAxisMathRender(math, DefaultParameterRenderer.INSTANCE);
        }
      } else {
        this.yAxisMathRender(textOut, DefaultParameterRenderer.INSTANCE);
      }
      textOut.append(". The result of this formula"); //$NON-NLS-1$
    }
    textOut.append(//
        " is always computed over the runs of an experiment for a given benchmark instance. If runs for multiple instances are available, we aggregate these "); //$NON-NLS-1$
    if (yOut.isIdentityTransformation()) {
      this.m_param.printLongName(textOut, use);
    } else {
      textOut.append("result"); //$NON-NLS-1$
    }
    textOut.append("s by computing their "); //$NON-NLS-1$
    this.m_second.printLongName(textOut, use);
    textOut.append('.');

    if (!(xIn.isIdentityTransformation())) {
      textOut.append(" The x-axis does not represent the values of "); //$NON-NLS-1$
      xIn.getDimension().printShortName(textOut, use);
      textOut.append(" directly, but instead "); //$NON-NLS-1$
      if (textOut instanceof IComplexText) {
        try (final IMath math = ((IComplexText) textOut).inlineMath()) {
          xIn.mathRender(math, DefaultParameterRenderer.INSTANCE);
        }
      } else {
        xIn.mathRender(textOut, DefaultParameterRenderer.INSTANCE);
      }
      textOut.append('.');
    }

    return use;
  }

  /**
   * Compute the aggregate per instance runs
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @return the aggregated data
   */
  final IMatrix _computeInstanceRuns(final IInstanceRuns data,
      final Logger logger) {
    final IMatrix[] matrices;
    final ArrayListView<? extends IRun> runs;
    final IMatrix result;
    final DimensionTransformation xIn, yIn;
    final Transformation yOut;
    CallableMatrixIteration2DBuilder<IMatrix> builder;
    ScalarAggregate aggregate;
    String name;
    int i;

    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      name = this.getNameForLogging(data);
      logger.finer("Beginning to compute the " + name + '.'); //$NON-NLS-1$
    } else {
      name = null;
    }

    xIn = this.getXAxisTransformation();
    synchronized (xIn) {
      try (final TransformationFunction xFunction = xIn.use(data)) {
        yIn = this.getYAxisInputTransformation();
        synchronized (yIn) {
          try (final TransformationFunction yInputFunction = yIn
              .use(data)) {

            runs = data.getData();
            i = runs.size();
            matrices = new IMatrix[i];

            for (; (--i) >= 0;) {
              matrices[i] = new ColumnTransformedMatrix(//
                  runs.get(i).selectColumns(this.m_xIndex, this.m_yIndex), //
                  xFunction, yInputFunction).copy();
            }
          }
        }
      }
    }

    yOut = this.getYAxisOutputTransformation();
    builder = new CallableMatrixIteration2DBuilder<>();
    MatrixIteration2DUtils.setupDimensionProperties(builder,
        xIn.getDimension(), yIn.getDimension(), false, false);
    builder.setXDimension(0);
    builder.setYDimension(1);
    builder.setMatrices(matrices);
    aggregate = this.m_param.createSampleAggregate();
    synchronized (yOut) {
      try (final TransformationFunction yOutputFunction = yOut.use(data)) {
        result = builder
            .setVisitor(new Matrix2DAggregate(aggregate, yOutputFunction))//
            .create().call();
      }
    }
    aggregate = null;
    builder = null;
    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      if (name == null) {
        name = this.getNameForLogging(data);
      }
      logger.finer("Finished computing the " + name + //$NON-NLS-1$
          ", resulting in a " + result.m() + '*'//$NON-NLS-1$
          + result.n() + " matrix.");//$NON-NLS-1$
    }

    return result;
  }

  /**
   * compute the secondary aggregate.
   *
   * @param matrices
   *          the matrices
   * @param logger
   *          the logger
   * @return the aggregation result
   */
  private final IMatrix __secondaryAggregate(final IMatrix[] matrices,
      final Logger logger) {
    CallableMatrixIteration2DBuilder<IMatrix> builder;

    builder = new CallableMatrixIteration2DBuilder<>();
    MatrixIteration2DUtils.setupDimensionProperties(builder,
        this.getXAxisTransformation().getDimension(),
        this.getYAxisInputTransformation().getDimension(), false, false);
    builder.setXDimension(0);
    builder.setYDimension(1);
    builder.setMatrices(matrices);
    builder.setSkipLeadingAndTrailingNaNsOnXAxis(true);

    if (this.m_second.isDispersionStatistic()) {
      builder.setSkipLeadingAndTrailingNaNsOnYAxis(false);
      builder.setNaNReplacementForYAxis(Double.POSITIVE_INFINITY);
    } else {
      if (this.m_second.isRepresentativeValueStatistic()) {
        builder.setSkipLeadingAndTrailingNaNsOnYAxis(false);
        builder.setNaNReplacementForYAxis(//
            this.getYAxisInputTransformation().getDimension()
                .getDirection().isIncreasing() ? Double.NEGATIVE_INFINITY
                    : Double.POSITIVE_INFINITY);
      } else {
        builder.setSkipLeadingAndTrailingNaNsOnYAxis(true);
      }
    }

    builder.setVisitor(new Matrix2DAggregate(
        this.m_second.createSampleAggregate(), null));
    return builder.create().call();
  }

  /**
   * Compute the aggregate per experiment
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @return the aggregated data
   */
  @SuppressWarnings("unchecked")
  private final IMatrix __computeExperiment(final IExperiment data,
      final Logger logger) {
    final IMatrix[] matrices;
    final ArrayListView<? extends IInstanceRuns> runs;
    final IMatrix result;
    final Future<IMatrix>[] tasks;
    String name;
    int i;

    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      name = this.getNameForLogging(data);
      logger.finer("Beginning to compute the " + name + '.'); //$NON-NLS-1$
    } else {
      name = null;
    }

    runs = data.getData();
    i = runs.size();
    tasks = new Future[i];

    for (; (--i) >= 0;) {
      tasks[i] = Execute.parallel(//
          new __ComputeInstanceRuns(runs.get(i), logger));
    }

    matrices = new IMatrix[tasks.length];
    Execute.join(tasks, matrices, 0, true);

    result = this.__secondaryAggregate(matrices, logger);

    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      if (name == null) {
        name = this.getNameForLogging(data);
      }
      logger.finer("Finished computing the " + name + //$NON-NLS-1$
          " by computing the " + this.m_second.getShortName() + //$NON-NLS-1$
          " of this function over its " + runs.size() + //$NON-NLS-1$
          " instance runs, resulting in a " + result.m() + //$NON-NLS-1$
          '*' + result.n() + " matrix.");//$NON-NLS-1$
    }

    return result;
  }

  /**
   * Compute the aggregate per experiment set
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @return the aggregated data
   */
  private final IMatrix __computeExperimentSet(final IExperimentSet data,
      final Logger logger) {
    final IMatrix[] matrices;
    final ArrayList<Future<IMatrix>> tasks;
    final IMatrix result;
    String name;

    name = null;
    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      name = this.getNameForLogging();
      logger.finer("Beginning to compute the " + name + '.'); //$NON-NLS-1$
    }

    tasks = new ArrayList<>();
    for (final IExperiment exp : data.getData()) {
      for (final IInstanceRuns irs : exp.getData()) {
        tasks.add(Execute.parallel(//
            new __ComputeInstanceRuns(irs, logger)));
      }
    }

    matrices = new IMatrix[tasks.size()];
    Execute.join(tasks, matrices, 0, true);
    result = this.__secondaryAggregate(matrices, logger);

    if ((logger != null) && (logger.isLoggable(Level.FINER))) {
      if (name == null) {
        name = this.getNameForLogging();
      }
      logger.finer("Finished computing the " + name + //$NON-NLS-1$
          " by computing the " + this.m_second.getShortName() + //$NON-NLS-1$
          " of this function over all of its  instance runs, resulting in a " //$NON-NLS-1$
          + result.m() + '*' + result.n() + " matrix.");//$NON-NLS-1$
    }

    return result;
  }

  /** {@inheritDoc} */
  @Override
  protected final boolean isEqual(
      final FunctionAttribute<IElementSet> other) {
    final Aggregation2D agg;

    agg = ((Aggregation2D) other);
    return (this.m_param.equals(agg.m_param) && //
        this.m_second.equals(agg.m_second));
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(//
        super.calcHashCode(), //
        HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_param), //
            HashUtils.hashCode(this.m_second)));//
  }

  /** {@inheritDoc} */
  @Override
  protected final IMatrix compute(final IElementSet data,
      final Logger logger) {
    if (data instanceof IInstanceRuns) {
      return this._computeInstanceRuns(((IInstanceRuns) data), logger);
    }
    if (data instanceof IExperiment) {
      return this.__computeExperiment(((IExperiment) data), logger);
    }
    if (data instanceof IExperimentSet) {
      return this.__computeExperimentSet(((IExperimentSet) data), logger);
    }
    throw new IllegalArgumentException("Cannot computed 2d-aggregate over " //$NON-NLS-1$
        + data);
  }

  /** {@inheritDoc} */
  @Override
  protected final String getShortName() {
    return ((this.m_second.getShortName() + ' ') + //
        this.m_param.getShortName());
  }

  /** {@inheritDoc} */
  @Override
  protected final String getLongName() {
    return (this.m_second.getLongName() + " of " + //$NON-NLS-1$
        (this.m_param.getLongName() + 's'));
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    final boolean isComplex;
    DimensionTransformation trafo;
    ETextCase nextCase;

    nextCase = textCase.appendWords(this.getLongName(), textOut);
    textOut.append(' ');
    nextCase = nextCase.appendWord("of", textOut); //$NON-NLS-1$
    textOut.append(' ');
    trafo = this.getYAxisInputTransformation();
    isComplex = (textOut instanceof IComplexText);
    if (isComplex) {
      try (final IMath math = ((IComplexText) textOut).inlineMath()) {
        trafo.mathRender(math, null);
      }
    } else {
      trafo.mathRender(textOut, null);
    }

    textOut.append(' ');
    nextCase = nextCase.appendWord("over", textOut); //$NON-NLS-1$
    textOut.append(' ');

    trafo = this.getXAxisTransformation();
    if (isComplex) {
      try (final IMath math = ((IComplexText) textOut).inlineMath()) {
        trafo.mathRender(math, null);
      }
    } else {
      trafo.mathRender(textOut, null);
    }
    return nextCase;
  }

  /**
   * Create an instance of {@link Aggregation2D} based on an experiment set
   * and a configuration
   *
   * @param data
   *          the data (experiment set)
   * @param config
   *          the configuration
   * @return the instance of the aggregate
   */
  public static final Aggregation2D create(final IExperimentSet data,
      final Configuration config) {
    DimensionTransformationParser dimParser;
    final DimensionTransformation x, yIn;
    final Transformation yOut;
    final StatisticalParameter first, second;

    dimParser = new DimensionTransformationParser(data);
    x = config.get(FunctionAttribute.X_AXIS_PARAM, dimParser, null);
    if (x == null) {//
      throw new IllegalArgumentException(
          "Must specify an x-dimension via parameter '" //$NON-NLS-1$
              + FunctionAttribute.X_AXIS_PARAM + '\'');
    }

    yIn = config.get(FunctionAttribute.Y_INPUT_AXIS_PARAM, dimParser,
        null);
    if (yIn == null) {//
      throw new IllegalArgumentException(
          "Must specify an input dimension for the y-axis via parameter '" //$NON-NLS-1$
              + FunctionAttribute.Y_INPUT_AXIS_PARAM + '\'');
    }

    dimParser = null;

    yOut = config.get(FunctionAttribute.Y_AXIS_OUTPUT_PARAM,
        new NamedParameterTransformationParser(data),
        new Transformation());

    first = config.get(Aggregation2D.PRIMARY_AGGREGATE_PARAM,
        StatisticalParameterParser.getInstance(), null);
    if (first == null) {//
      throw new IllegalArgumentException(
          "Must specify a statistical parameter (aggregate) to be computed, via parameter '" //$NON-NLS-1$
              + Aggregation2D.PRIMARY_AGGREGATE_PARAM + '\'');
    }

    second = config.get(Aggregation2D.SECONDARY_AGGREGATE_PARAM,
        StatisticalParameterParser.getInstance(), Median.INSTANCE);

    return new Aggregation2D(x, yIn, yOut, first, second);
  }

  /** compute the matrix for a given set of instance runs */
  private final class __ComputeInstanceRuns implements Callable<IMatrix> {
    /** the instance runs */
    private final IInstanceRuns m_runs;
    /** the logger */
    private final Logger m_logger;

    /**
     * compute
     *
     * @param runs
     *          the runs
     * @param logger
     *          the logger
     */
    __ComputeInstanceRuns(final IInstanceRuns runs, final Logger logger) {
      super();
      this.m_runs = runs;
      this.m_logger = logger;
    }

    /** {@inheritDoc} */
    @Override
    public final IMatrix call() {
      return Aggregation2D.this._computeInstanceRuns(this.m_runs,
          this.m_logger);
    }
  }
}
