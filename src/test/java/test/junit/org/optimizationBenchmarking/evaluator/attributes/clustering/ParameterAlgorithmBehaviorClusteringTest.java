package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties.ParameterAlgorithmBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.ml.clustering.impl.DefaultClusterer;

import shared.junit.CategorySlowTests;
import test.junit.org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipTest;

/** Test the clustering by instance behavior */
public class ParameterAlgorithmBehaviorClusteringTest
    extends ClusteringTest {

  /** create */
  public ParameterAlgorithmBehaviorClusteringTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected ParameterAlgorithmBehaviorClusterer getAttribute(
      final IExperimentSet experimentSet, final IExperimentSet data) {
    return new ParameterAlgorithmBehaviorClusterer(
        ClusteringTest.getTransformations(experimentSet), -1, -1);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean canUseAttribute() {
    return (DimensionRelationshipTest.canUse() && //
        DefaultClusterer.getDistanceInstance().canUse());
  }

  /** {@inheritDoc} */
  @Override
  @Ignore
  public void testAttributeOnExample1() {
    // do nothing
  }

  /** {@inheritDoc} */
  @Override
  @Test(timeout = 36000000)
  @Category(CategorySlowTests.class)
  public void testAttributeOnBBOB() {
    super.testAttributeOnBBOB();
  }

  /** {@inheritDoc} */
  @Override
  @Test(timeout = 36000000)
  @Category(CategorySlowTests.class)
  public void testAttributeOnTSPSuite() {
    super.testAttributeOnTSPSuite();
  }

  /** {@inheritDoc} */
  @Override
  @Ignore
  public void testAttributeOnRandomData() {
    // do nothing
  }
}
