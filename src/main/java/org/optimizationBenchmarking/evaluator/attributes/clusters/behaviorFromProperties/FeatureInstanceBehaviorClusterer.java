package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.LinkedHashSet;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.InstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;

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
   * @param config
   *          the configuration
   */
  public FeatureInstanceBehaviorClusterer(final Configuration config) {
    super(new InstanceBehaviorClusterer(config));
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(
        HashUtils.hashCode(this.m_behaviorClusterer), 45675661);
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
}
