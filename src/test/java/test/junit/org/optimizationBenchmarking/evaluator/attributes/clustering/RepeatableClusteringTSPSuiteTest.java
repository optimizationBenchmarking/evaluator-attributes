package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.TSPSuiteExample;
import shared.junit.TestBase;

/** Test the clustering by instance behavior */
public abstract class RepeatableClusteringTSPSuiteTest
    extends RepeatableClusteringTestBase {

  /** create */
  public RepeatableClusteringTSPSuiteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IExperimentSet getExperimentSet() throws Exception {
    return new TSPSuiteExample(TestBase.getNullLogger()).call();
  }

}
