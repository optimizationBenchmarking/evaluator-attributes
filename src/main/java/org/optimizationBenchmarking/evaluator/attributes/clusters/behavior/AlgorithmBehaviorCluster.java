package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A algorithm behavior cluster is a cluster which holds algorithm
 * belonging to one similarity group according to their runtime behavior.
 */
public class AlgorithmBehaviorCluster
    extends _BehaviorCluster<AlgorithmBehaviorClustering> {

  /**
   * create the algorithm behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param name
   *          the name of the cluster
   * @param selection
   *          the data selection
   */
  AlgorithmBehaviorCluster(final AlgorithmBehaviorClustering owner,
      final String name, final DataSelection selection) {
    super(owner, name, selection);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listExperiments(this, textCase, textOut);
  }
}
