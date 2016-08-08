package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusteringBase;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipModels;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A behavior-based clustering uses algorithm behavior to divide algorithms
 * or instances into groups.
 *
 * @param <CT>
 *          the cluster type
 */
abstract class _BehaviorClustering<CT extends NamedCluster<?>>
    extends ClusteringBase<IExperimentSet, CT> {

  /** the data */
  private final ArrayListView<CT> m_data;

  /**
   * create the behavior-based clustering
   *
   * @param owner
   *          the owner
   * @param clusters
   *          the matrix of clusters
   * @param source
   *          the source where to draw the named elements from
   * @param names
   *          the names
   */
  _BehaviorClustering(final IExperimentSet owner, final int[] clusters,
      final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    super(owner);
    final ArrayList<CT> list;
    DataSelection selection;
    int clusterIndex, find;
    INamedElement ne;
    String name;
    int total;

    list = new ArrayList<>(20);

    total = 0;
    for (clusterIndex = 0;; ++clusterIndex) {
      selection = null;
      for (find = clusters.length; (--find) >= 0;) {
        if (clusters[find] == clusterIndex) {
          if (selection == null) {
            selection = new DataSelection(owner);
          }
          name = names.get(find).getName();
          ne = source.find(name);
          if (ne == null) {
            throw new IllegalStateException("Cannot find element of name '" //$NON-NLS-1$
                + name + "' in " + names); //$NON-NLS-1$
          }
          if (ne instanceof IInstance) {
            selection.addInstance((IInstance) ne);
            total++;
          } else {
            if (ne instanceof IExperiment) {
              selection.addExperiment((IExperiment) ne);
              total++;
            } else {
              throw new IllegalStateException(//
                  "Element of name '" + name + //$NON-NLS-1$
                      "' is neither an experiment nor a benchmark instance.");//$NON-NLS-1$
            }
          }
        }
      }

      if (selection == null) {
        break;
      }
      list.add(this._create(list.size(), selection));
    }

    if (total != names.size()) {
      throw new IllegalStateException(//
          "There are " + names.size() + //$NON-NLS-1$
              " elements that should be in clusters, but " + total + //$NON-NLS-1$
              " have actually been assigned.");//$NON-NLS-1$
    }

    this.m_data = ArrayListView.collectionToView(list);
  }

  /**
   * create a new cluster
   *
   * @param nameIndex
   *          the 0-based name index to be transformed to a name string
   * @param selection
   *          the selection
   * @return the cluster
   */
  abstract CT _create(final int nameIndex, final DataSelection selection);

  /** {@inheritDoc} */
  @Override
  public final ArrayListView<CT> getData() {
    return this.m_data;
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    textOut.append(
        "We therefore first model the relationships of all time dimensions to all objective dimensions. Therefore "); //$NON-NLS-1$
    DimensionRelationshipModels.printModelingDescription(textOut,
        ETextCase.IN_SENTENCE, true, false);
    textOut.append(
        " Based on the obtained models, we cluster the data by appying distance-based clustering. The quality that a fitted model for algorithm setup would have if it would represents the measured points from other setup is used as distance metric. By using several different clustering algorithms. The number of clusters is dynamically decided in order to achieve the best average silhouette width."); //$NON-NLS-1$
    return ETextCase.AT_SENTENCE_START;
  }

  /** {@inheritDoc} */
  @Override
  public void printLongDescription(final ISectionBody body) {
    super.printLongDescription(body);
    body.appendLineBreak();
    ClusterUtils.listClusters(this, body);
  }
}
