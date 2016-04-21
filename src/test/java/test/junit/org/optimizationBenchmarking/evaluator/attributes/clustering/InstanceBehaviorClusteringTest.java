package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.InstanceBehaviorClusterer;
import org.optimizationBenchmarking.utils.ml.clustering.impl.DefaultClusterer;

import shared.junit.CategorySlowTests;
import test.junit.org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipTest;

/** Test the clustering by instance behavior */
public class InstanceBehaviorClusteringTest extends ClusteringTest {

  /** create */
  public InstanceBehaviorClusteringTest() {
    super(InstanceBehaviorClusterer.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean canUseAttribute() {
    return (DimensionRelationshipTest.canUse() && //
        DefaultClusterer.getDistanceInstance().canUse());
  }

  /** {@inheritDoc} */
  @Override
  @Test(timeout = 36000000)
  public void testAttributeOnExample1() {
    super.testAttributeOnExample1();
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
  @Test(timeout = 36000000)
  public void testAttributeOnRandomData() {
    super.testAttributeOnRandomData();
  }
}
