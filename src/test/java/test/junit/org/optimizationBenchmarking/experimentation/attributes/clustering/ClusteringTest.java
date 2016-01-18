package test.junit.org.optimizationBenchmarking.experimentation.attributes.clustering;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;

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
}
