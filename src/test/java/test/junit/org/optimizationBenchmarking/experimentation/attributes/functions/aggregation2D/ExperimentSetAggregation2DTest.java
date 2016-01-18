package test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.aggregation2D;

import java.util.Random;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.optimizationBenchmarking.evaluator.attributes.functions.aggregation2D.Aggregation2D;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;

import shared.junit.CategorySlowTests;
import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils.FunctionParameters;

/** The test for the aggregation 2D computed over experiment sets */
public class ExperimentSetAggregation2DTest
    extends ExperimentSetAttributeTest<IMatrix, Aggregation2D> {

  /** create */
  public ExperimentSetAggregation2DTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final Aggregation2D attribute,
      final IExperimentSet experimentSet, final IExperimentSet input,
      final IMatrix result) {
    FunctionTestUtils.checkFunctionMatrix(result);
  }

  /** {@inheritDoc} */
  @Override
  @Category(CategorySlowTests.class)
  @Test(timeout = 3600000)
  public void testAttributeOnTSPSuite() {
    super.testAttributeOnTSPSuite();
  }

  /** {@inheritDoc} */
  @Override
  protected Aggregation2D getAttribute(final IExperimentSet experimentSet,
      final IExperimentSet data) {
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
