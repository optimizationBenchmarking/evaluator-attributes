package test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.ecdf;

import java.util.Random;

import org.optimizationBenchmarking.evaluator.attributes.functions.ecdf.ECDF;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.InstanceRunsAttributeTest;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils;
import test.junit.org.optimizationBenchmarking.experimentation.attributes.functions.FunctionTestUtils.FunctionParameters;

/** The test for modeling of dimension relationships */
public class InstanceRunsECDFTest
    extends InstanceRunsAttributeTest<IMatrix, ECDF> {

  /** create */
  public InstanceRunsECDFTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected int getMaxAttributeComputationsPerDataset() {
    return 2;
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final ECDF attribute,
      final IExperimentSet experimentSet, final IInstanceRuns input,
      final IMatrix result) {
    FunctionTestUtils.checkFunctionMatrix(result);
  }

  /** {@inheritDoc} */
  @Override
  protected ECDF getAttribute(final IExperimentSet experimentSet,
      final IInstanceRuns data) {
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
