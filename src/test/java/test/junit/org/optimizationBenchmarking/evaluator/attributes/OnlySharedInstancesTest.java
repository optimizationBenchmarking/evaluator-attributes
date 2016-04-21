package test.junit.org.optimizationBenchmarking.evaluator.attributes;

import org.optimizationBenchmarking.evaluator.attributes.OnlySharedInstances;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.DataValidator;

/** The test for the "only-shared-instances" attribute */
public class OnlySharedInstancesTest extends
    ExperimentSetAttributeTest<IExperimentSet, OnlySharedInstances> {

  /** create */
  public OnlySharedInstancesTest() {
    super(OnlySharedInstances.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final OnlySharedInstances attribute,
      final IExperimentSet experimentSet, final IExperimentSet input,
      final IExperimentSet result) {
    DataValidator.checkExperimentSet(result);
  }
}
