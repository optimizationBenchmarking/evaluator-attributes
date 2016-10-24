package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.LinkedHashSet;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.InstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A clusterer for clustering via instance behavior and instance features.
 */
public final class FeatureInstanceBehaviorClusterer
    extends _PropertyBehaviorClusterer<IInstance> {

  /**
   * indicate that clustering should be performed by instance, based on the
   * behavior of the algorithms on the instance and the instance features
   */
  public static final String CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR_AND_FEATURES = "instances by algorithm behavior and features"; //$NON-NLS-1$

  /**
   * create the instance behavior clusterer from a given configuration
   *
   * @param experimentSet
   *          the experiment set
   * @param config
   *          the configuration
   */
  public FeatureInstanceBehaviorClusterer(
      final IExperimentSet experimentSet, final Configuration config) {
    super(new InstanceBehaviorClusterer(experimentSet, config));
  }

  /** {@inheritDoc} */
  @Override
  public void toText(final ITextOutput textOut) {
    textOut.append(
        FeatureInstanceBehaviorClusterer.CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR_AND_FEATURES);
    super.toText(textOut);
  }

  /**
   * create instance behavior clusterer
   *
   * @param transformations
   *          the dimension transformations, or {@code null} if all
   *          dimensions can be used directly
   * @param minClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   * @param maxClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   */
  public FeatureInstanceBehaviorClusterer(
      final DimensionTransformation[] transformations,
      final int minClusters, final int maxClusters) {
    super(new InstanceBehaviorClusterer(transformations, minClusters,
        maxClusters));
  }

  /** {@inheritDoc} */
  @Override
  final void _getElementsToClassify(final ICluster cluster,
      final IExperimentSet data, final LinkedHashSet<IInstance> elements) {
    for (final IInstance instance : cluster.getInstances().getData()) {
      elements.add(data.getInstances().find(instance.getName()));
    }
  }

  /** {@inheritDoc} */
  @Override
  final ArrayListView<? extends IProperty> _getProperties(
      final IExperimentSet data) {
    return data.getFeatures().getData();
  }

  /** {@inheritDoc} */
  @Override
  final IPropertySetting _getPropertySetting(final IInstance element) {
    return element.getFeatureSetting();
  }

  /** {@inheritDoc} */
  @Override
  final IClustering _createClustering(final IExperimentSet owner,
      final IClustering behavior, final IClassifier classifier,
      final DataSelection[] selections) {
    return new FeatureInstanceBehaviorClustering(owner, behavior,
        classifier, selections);
  }
}
