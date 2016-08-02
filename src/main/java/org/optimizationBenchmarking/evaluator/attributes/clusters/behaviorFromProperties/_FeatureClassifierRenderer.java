package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import org.optimizationBenchmarking.evaluator.data.spec.IProperty;

/** the feature-based classifier renderer */
final class _FeatureClassifierRenderer extends _ClassifierRenderer {
  /**
   * create the feature-based set classifier renderer
   *
   * @param clustering
   *          the clustering
   */
  _FeatureClassifierRenderer(
      final FeatureInstanceBehaviorClustering clustering) {
    super(clustering);
  }

  /** {@inheritDoc} */
  @Override
  final IProperty _getProperty(final int index) {
    return this.m_clustering.getOwner().getFeatures().getData().get(index);
  }
}
