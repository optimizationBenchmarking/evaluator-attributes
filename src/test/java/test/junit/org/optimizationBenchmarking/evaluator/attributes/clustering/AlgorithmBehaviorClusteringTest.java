package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.AlgorithmBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.math.functions.basic.Identity;
import org.optimizationBenchmarking.utils.ml.clustering.impl.DefaultClusterer;

import shared.junit.CategorySlowTests;
import test.junit.org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipTest;

/** Test the clustering by instance behavior */
public class AlgorithmBehaviorClusteringTest extends ClusteringTest {

  /** create */
  public AlgorithmBehaviorClusteringTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected AlgorithmBehaviorClusterer getAttribute(
      final IExperimentSet experimentSet, final IExperimentSet data) {
    return new AlgorithmBehaviorClusterer(AlgorithmBehaviorClusteringTest
        .__getTransformations(experimentSet), -1, -1);
  }

  /**
   * Get the dimension transformations from the given data
   *
   * @param experimentSet
   *          the experiment set
   * @return the transformations
   */
  private static final DimensionTransformation[] __getTransformations(
      final IExperimentSet experimentSet) {
    final ArrayListView<? extends IDimension> dimensions;
    final DimensionTransformation[] transformations;
    IDimension dimension;
    int index;

    dimensions = experimentSet.getDimensions().getData();

    transformations = new DimensionTransformation[dimensions.size()];
    for (index = transformations.length; (--index) >= 0;) {
      dimension = dimensions.get(index);
      transformations[index] = new DimensionTransformation(
          Identity.INSTANCE, dimension);
    }
    return transformations;
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
