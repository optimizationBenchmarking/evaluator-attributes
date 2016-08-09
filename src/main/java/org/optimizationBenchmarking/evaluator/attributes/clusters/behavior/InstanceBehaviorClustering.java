package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * The instance behavior clustering contains a division of experiments or
 * instances according to the runtime behavior of the algorithms on them.
 */
public final class InstanceBehaviorClustering
    extends _BehaviorClustering<InstanceBehaviorCluster> {

  /**
   * create the instance behavior clustering
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
  InstanceBehaviorClustering(final IExperimentSet owner,
      final int[] clusters, final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    super(owner, clusters, source, names);
  }

  /** {@inheritDoc} */
  @Override
  final InstanceBehaviorCluster _create(final int nameIndex,
      final DataSelection selection) {
    return new InstanceBehaviorCluster(this, nameIndex, selection);
  }

  /** {@inheritDoc} */
  @Override
  final void _distanceAggregationText(final ITextOutput textOut) {
    textOut.append(
        "The distance between two benchmark instances is then the sum of the distances of their fitted models for the same algorithm setups and dimensions, over all algorithm setups."); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord("instance behavior clustering", textOut); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord("clustering by instance behavior", textOut); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    final ETextCase nextCase;

    nextCase = textCase.appendWord("the", textOut); //$NON-NLS-1$
    textOut.append(
        "  benchmark instances are divided into groups according to the runtime behavior of the algorithm setups applied to them. "); //$NON-NLS-1$
    return super.printDescription(textOut, nextCase);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPathComponentSuggestion() {
    return "instanceBehavior" + this.getData().size(); //$NON-NLS-1$
  }
}
