package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.LinkedHashSet;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.InstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * A clusterer for clustering via algorithm behavior and instance features.
 */
public final class ParameterAlgorithmBehaviorClusterer
    extends _PropertyBehaviorClusterer<IExperiment> {

  /**
   * indicate that clustering should be performed by algorithm setup, based
   * on the behavior on the benchmark instances and the algorithm setup
   * parameters
   */
  public static final String CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR_AND_PARAMETERS = "algorithms by behavior and parameters"; //$NON-NLS-1$

  /**
   * create the instance behavior clusterer from a given configuration
   *
   * @param config
   *          the configuration
   */
  public ParameterAlgorithmBehaviorClusterer(final Configuration config) {
    super(new InstanceBehaviorClusterer(config));
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(
        HashUtils.hashCode(this.m_behaviorClusterer), 177634489);
  }

  /** {@inheritDoc} */
  @Override
  final void _getElementsToClassify(final ICluster cluster,
      final IExperimentSet data,
      final LinkedHashSet<IExperiment> elements) {
    for (final IExperiment experiment : cluster.getData()) {
      elements.add(data.find(experiment.getName()));
    }
  }

  /** {@inheritDoc} */
  @Override
  final ArrayListView<? extends IProperty> _getProperties(
      final IExperimentSet data) {
    return data.getParameters().getData();
  }

  /** {@inheritDoc} */
  @Override
  final IPropertySetting _getPropertySetting(final IExperiment element) {
    return element.getParameterSetting();
  }
}
