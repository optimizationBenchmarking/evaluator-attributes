package test.junit.org.optimizationBenchmarking.evaluator.attributes.functions.aggregation2D;

import java.util.Random;

import org.optimizationBenchmarking.evaluator.attributes.functions.aggregation2D.Aggregation2D;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.InstanceRunsAttributeTest;
import test.junit.org.optimizationBenchmarking.evaluator.attributes.functions.FunctionTestUtils;
import test.junit.org.optimizationBenchmarking.evaluator.attributes.functions.FunctionTestUtils.FunctionParameters;

/** The test for the aggregation 2D computed over instance runs */
public class InstanceRunsAggregation2DTest
    extends InstanceRunsAttributeTest<IMatrix, Aggregation2D> {

  /** create */
  public InstanceRunsAggregation2DTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected int getMaxAttributeComputationsPerDataset() {
    return 2;
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final Aggregation2D attribute,
      final IExperimentSet experimentSet, final IInstanceRuns input,
      final IMatrix result) {
    FunctionTestUtils.checkFunctionMatrix(result);
  }

  /** {@inheritDoc} */
  @Override
  protected Aggregation2D getAttribute(final IExperimentSet experimentSet,
      final IInstanceRuns data) {
    final Random random;
    final FunctionParameters params;

    random = new Random();
    params = FunctionTestUtils.createFunctionParameters(experimentSet,
        random);

    return new Aggregation2D(//
        params.xAxisTransformation, //
        params.yAxisInputTransformation, //
        params.yAxisOutputTransformation, //
        FunctionTestUtils.getStatisticalParameter(random), //
        FunctionTestUtils.getStatisticalParameter(random)//
    );
  }
}
