package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A instance behavior cluster is a cluster which holds instances belonging
 * to one similarity group according to the runtime behavior of algorithms
 * on them.
 */
public class InstanceBehaviorCluster
    extends _BehaviorCluster<InstanceBehaviorClustering> {

  /**
   * create the instance behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param name
   *          the name of the cluster
   * @param selection
   *          the data selection
   */
  InstanceBehaviorCluster(final InstanceBehaviorClustering owner,
      final String name, final DataSelection selection) {
    super(owner, name, selection);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listInstances(this, textCase, textOut);
  }
}
