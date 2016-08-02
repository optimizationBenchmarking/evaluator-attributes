package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import org.optimizationBenchmarking.evaluator.data.spec.IProperty;

/** the parameter-based classifier renderer */
final class _ParameterClassifierRenderer extends _ClassifierRenderer {
  /**
   * create the parameter-based set classifier renderer
   *
   * @param clustering
   *          the clustering
   */
  _ParameterClassifierRenderer(
      final ParameterAlgorithmBehaviorClustering clustering) {
    super(clustering);
  }

  /** {@inheritDoc} */
  @Override
  final IProperty _getProperty(final int index) {
    return this.m_clustering.getOwner().getParameters().getData()
        .get(index);
  }
}
