package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.junit.Assert;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.ml.fitting.impl.DefaultFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.impl.debug.DebugFitter;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFunctionFitter;

import shared.junit.TestBase;

/** Test the clustering by instance behavior */
public abstract class RepeatableClusteringTestBase {

  static {
    DefaultFunctionFitter.setAllInstances(new ArrayListView<>(
        new IFunctionFitter[] { DebugFitter.getInstance() }));
  }

  /** create */
  public RepeatableClusteringTestBase() {
    super();
  }

  /**
   * get the experiment set
   *
   * @return the experiment set
   * @throws Exception
   *           if i/o fails
   */
  protected abstract IExperimentSet getExperimentSet() throws Exception;

  /**
   * create the attribute
   *
   * @param experimentSet
   *          the experiment set
   * @return the attribute
   */
  protected abstract Attribute<IExperimentSet, IClustering> getClusteringAttribute(
      final IExperimentSet experimentSet);

  /**
   * check the clustering
   *
   * @param clustering
   *          the clustering
   */
  private static final void __checkClustering(
      final IClustering clustering) {
    Assert.assertNotNull(clustering);
    Assert.assertTrue(clustering.getData().size() > 0);
    for (final ICluster cluster : clustering.getData()) {
      Assert.assertNotNull(cluster);
      Assert.assertTrue(cluster.getData().size() > 0);
      Assert.assertTrue(cluster.getInstances().getData().size() > 0);
      Assert.assertTrue(cluster.getDimensions().getData().size() > 0);
      Assert.assertTrue(cluster.getFeatures().getData().size() > 0);
      Assert.assertTrue(cluster.getParameters().getData().size() > 0);
    }
  }

  /**
   * Test the attribute's creon the same data
   *
   * @throws Exception
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public void testSameAttributeOnSameData() throws Exception {
    IExperimentSet data;
    Attribute<IExperimentSet, IClustering> attribute1, attribute2;
    int i;

    data = this.getExperimentSet();
    Assert.assertNotNull(data);

    attribute1 = this.getClusteringAttribute(data);
    Assert.assertNotNull(attribute1);

    for (i = 200; i >= 0; --i) {
      attribute2 = this.getClusteringAttribute(data);
      Assert.assertNotNull(attribute2);
      Assert.assertEquals(attribute1, attribute2);
      Assert.assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }
  }

  /**
   * Test the attribute's functionality on the same data
   *
   * @throws Exception
   *           if i/o fails
   */
  @Test(timeout = 136000000)
  public void testRepeatableOnSameData() throws Exception {
    IExperimentSet data;
    Attribute<IExperimentSet, IClustering> attribute1, attribute2;
    IClustering a, b;
    ICluster ac, bc;
    int i, j;

    data = this.getExperimentSet();
    Assert.assertNotNull(data);

    attribute1 = this.getClusteringAttribute(data);
    Assert.assertNotNull(attribute1);

    a = attribute1.get(data, TestBase.getNullLogger());
    RepeatableClusteringTestBase.__checkClustering(a);

    for (i = 3; i >= 0; --i) {
      attribute2 = this.getClusteringAttribute(data);
      Assert.assertNotNull(attribute2);
      Assert.assertEquals(attribute1, attribute2);
      Assert.assertEquals(attribute1.hashCode(), attribute2.hashCode());

      b = attribute2.get(data, TestBase.getNullLogger());
      RepeatableClusteringTestBase.__checkClustering(b);

      if (a == b) {
        continue;
      }

      Assert.assertEquals(j = a.getData().size(), b.getData().size());
      for (; (--j) >= 0;) {
        ac = a.getData().get(j);
        bc = b.getData().get(j);
        Assert.assertEquals(ac.getData().size(), bc.getData().size());
        Assert.assertEquals(ac.getInstances().getData().size(),
            bc.getInstances().getData().size());
      }
    }
  }
}
