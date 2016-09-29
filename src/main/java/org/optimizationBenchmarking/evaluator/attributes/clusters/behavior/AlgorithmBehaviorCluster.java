package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A algorithm behavior cluster is a cluster which holds algorithm
 * belonging to one similarity group according to their runtime behavior.
 */
public class AlgorithmBehaviorCluster
    extends NamedCluster<AlgorithmBehaviorClustering> {

  /**
   * create the algorithm behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param nameIndex
   *          the 0-based name index to be transformed to a name string
   * @param selection
   *          the data selection
   */
  AlgorithmBehaviorCluster(final AlgorithmBehaviorClustering owner,
      final int nameIndex, final DataSelection selection) {
    super(owner, nameIndex, selection);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listExperiments(this, 1000, textCase, textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listExperiments(this, 3, textCase, textOut);
  }
}
