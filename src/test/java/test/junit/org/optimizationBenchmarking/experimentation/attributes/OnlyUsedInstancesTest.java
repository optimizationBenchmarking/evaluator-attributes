package test.junit.org.optimizationBenchmarking.experimentation.attributes;

import org.optimizationBenchmarking.evaluator.attributes.OnlyUsedInstances;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.DataValidator;

/** The test for the "only-used-instances" attribute */
public class OnlyUsedInstancesTest
    extends ExperimentSetAttributeTest<IExperimentSet> {

  /** create */
  public OnlyUsedInstancesTest() {
    super(OnlyUsedInstances.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(
      final Attribute<? super IExperimentSet, ? extends IExperimentSet> attribute,
      final IExperimentSet experimentSet, final IExperimentSet input,
      final IExperimentSet result) {
    DataValidator.checkExperimentSet(result);
  }
}
