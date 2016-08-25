package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** An algorithm behavior cluster */
public final class ParameterAlgorithmBehaviorCluster
    extends NamedCluster<ParameterAlgorithmBehaviorClustering> {

  /**
   * create behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param name
   *          the name of the cluster
   * @param selection
   *          the data selection
   */
  ParameterAlgorithmBehaviorCluster(
      final ParameterAlgorithmBehaviorClustering owner, final String name,
      final DataSelection selection) {
    super(owner, name, selection);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listExperiments(this, 42, textCase, textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return ClusterUtils.listExperiments(this, 3, textCase, textOut);
  }
}
