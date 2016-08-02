package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.spec.IElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;

/**
 * A clustering is an element set containing clusters.
 */
public interface IClustering extends IElementSet, ISemanticComponent {

  /**
   * Obtain the clusters
   *
   * @return the list of clusters
   */
  @Override
  public abstract ArrayListView<? extends ICluster> getData();

  /**
   * Print the long description of this clustering into a section body
   *
   * @param body
   *          the section body to write to
   */
  public abstract void printLongDescription(final ISectionBody body);
}
