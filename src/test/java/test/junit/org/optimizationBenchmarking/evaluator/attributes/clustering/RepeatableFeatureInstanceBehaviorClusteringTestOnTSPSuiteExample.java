package test.junit.org.optimizationBenchmarking.evaluator.attributes.clustering;

import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties.FeatureInstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.math.functions.basic.Identity;

/** the repeatable feature instance behavior clustering test */
public class RepeatableFeatureInstanceBehaviorClusteringTestOnTSPSuiteExample
    extends RepeatableClusteringTSPSuiteTest {

  /** the constructor */
  public RepeatableFeatureInstanceBehaviorClusteringTestOnTSPSuiteExample() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected Attribute<IExperimentSet, IClustering> getClusteringAttribute(
      final IExperimentSet experimentSet) {
    ArrayListView<? extends IDimension> set;
    DimensionTransformationParser parser;

    parser = new DimensionTransformationParser(experimentSet);
    set = experimentSet.getDimensions().getData();

    return new FeatureInstanceBehaviorClusterer(
        new DimensionTransformation[] { parser.parseString("FEs/n"), //$NON-NLS-1$
            new DimensionTransformation(Identity.INSTANCE, set.get(1)), //
            new DimensionTransformation(Identity.INSTANCE, set.get(2)), //
            new DimensionTransformation(Identity.INSTANCE, set.get(3)), //
            parser.parseString("(L-L.low)/L.low"), //$NON-NLS-1$
    }, -1, -1);
  }
}
