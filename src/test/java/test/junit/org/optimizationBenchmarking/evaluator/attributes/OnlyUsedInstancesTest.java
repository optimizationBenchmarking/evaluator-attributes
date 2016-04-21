package test.junit.org.optimizationBenchmarking.evaluator.attributes;

import org.optimizationBenchmarking.evaluator.attributes.OnlyUsedInstances;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.DataValidator;

/** The test for the "only-used-instances" attribute */
public class OnlyUsedInstancesTest
    extends ExperimentSetAttributeTest<IExperimentSet, OnlyUsedInstances> {

  /** create */
  public OnlyUsedInstancesTest() {
    super(OnlyUsedInstances.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final OnlyUsedInstances attribute,
      final IExperimentSet experimentSet, final IExperimentSet input,
      final IExperimentSet result) {
    DataValidator.checkExperimentSet(result);
  }
}
