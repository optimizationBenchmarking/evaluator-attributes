package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.math.functions.basic.Identity;
import org.optimizationBenchmarking.utils.math.functions.power.Sqr;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.ExperimentSetAttributeTest;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.DataValidator;

/** The test for the "only-shared-instances" attribute */
@Ignore
public class ClusteringTest extends
    ExperimentSetAttributeTest<IClustering, Attribute<? super IExperimentSet, ? extends IClustering>> {

  /**
   * create
   *
   * @param attribute
   *          the attribute
   */
  public ClusteringTest(
      final Attribute<? super IExperimentSet, ? extends IClustering> attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(
      final Attribute<? super IExperimentSet, ? extends IClustering> attribute,
      final IExperimentSet experimentSet, final IExperimentSet input,
      final IClustering result) {
    final ArrayListView<? extends IExperimentSet> list;

    Assert.assertNotNull(result);
    list = result.getData();
    Assert.assertNotNull(list);
    Assert.assertTrue(list.size() > 0);

    for (final IExperimentSet set : list) {
      DataValidator.checkExperimentSet(set);
    }
  }

  /**
   * Get the dimension transformations from the given data
   *
   * @param experimentSet
   *          the experiment set
   * @return the transformations
   */
  public static final DimensionTransformation[] getTransformations(
      final IExperimentSet experimentSet) {
    final ArrayListView<? extends IDimension> dimensions;
    final DimensionTransformation[] transformations;
    final Random random;
    IDimension dimension;
    int index;

    dimensions = experimentSet.getDimensions().getData();
    random = new Random();
    transformations = new DimensionTransformation[dimensions.size()];
    for (index = transformations.length; (--index) >= 0;) {
      dimension = dimensions.get(index);
      transformations[index] = new DimensionTransformation(
          (random.nextBoolean() ? Identity.INSTANCE : Sqr.INSTANCE),
          dimension);
    }
    return transformations;
  }
}
