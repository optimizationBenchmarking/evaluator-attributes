package test.junit.org.optimizationBenchmarking.experimentation.attributes;

import org.optimizationBenchmarking.evaluator.attributes.OnlyUsedInstances;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;

/** The test for the "only-used-instances" attribute */
public class OnlyUsedInstancesTest
    extends ExperimentSetAttributeTest<IExperimentSet> {

  /** create */
  public OnlyUsedInstancesTest() {
    super(OnlyUsedInstances.INSTANCE, true);
  }
}
