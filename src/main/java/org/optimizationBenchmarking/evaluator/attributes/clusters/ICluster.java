package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.document.spec.ISemanticMathComponent;

/**
 * A cluster is a sub-set of an experiment set.
 */
public interface ICluster extends IExperimentSet, ISemanticMathComponent {

  /**
   * Obtain the owning clustering
   *
   * @return the owning clustering
   */
  @Override
  public abstract IClustering getOwner();
}
