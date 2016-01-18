package test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.ecdf;

import java.util.Random;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.optimizationBenchmarking.evaluator.attributes.functions.ecdf.ECDF;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;

import shared.junit.CategorySlowTests;
import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils.FunctionParameters;

/** The test for the ECDF computed over whole experiment sets */
public class ExperimentSetECDFTest
    extends ExperimentSetAttributeTest<IMatrix, ECDF> {

  /** create */
  public ExperimentSetECDFTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final ECDF attribute,
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
  protected ECDF getAttribute(final IExperimentSet experimentSet,
      final IExperimentSet data) {
    final Random random;
    final FunctionParameters params;

    random = new Random();
    params = FunctionTestUtils.createFunctionParameters(experimentSet,
        random);

    return new ECDF(//
        params.xAxisTransformation, //
        params.yAxisInputTransformation, //
        params.yAxisOutputTransformation, //
        Double.valueOf(random.nextDouble()), //
        (params.yDim.getDirection().isIncreasing() ? //
            EComparison.GREATER_OR_EQUAL
            : //
            EComparison.LESS_OR_EQUAL), //
        FunctionTestUtils.getStatisticalParameter(random)//
    );
  }
}
