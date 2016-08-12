package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A instance behavior cluster is a cluster which holds instances belonging
 * to one similarity group according to the runtime behavior of algorithms
 * on them.
 */
public class InstanceBehaviorCluster
    extends NamedCluster<InstanceBehaviorClustering> {

  /**
   * create the instance behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param nameIndex
   *          the 0-based name index to be transformed to a name string
   * @param selection
   *          the data selection
   */
  InstanceBehaviorCluster(final InstanceBehaviorClustering owner,
      final int nameIndex, final DataSelection selection) {
    super(owner, nameIndex, selection);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listInstances(this, 42, textCase, textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    ETextCase next;
    next = super.printLongName(textOut, textCase);
    textOut.append(' ');
    textOut.append('(');
    next = ClusterUtils.listInstances(this, 3, next, textOut);
    textOut.append(')');
    return next;
  }
}
