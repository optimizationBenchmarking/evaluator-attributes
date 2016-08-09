package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * The algorithm behavior clustering contains a division of experiments or
 * instances according to the runtime behavior of the algorithms on them.
 */
public final class AlgorithmBehaviorClustering
    extends _BehaviorClustering<AlgorithmBehaviorCluster> {

  /**
   * create the algorithm behavior clustering
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
  AlgorithmBehaviorClustering(final IExperimentSet owner,
      final int[] clusters, final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    super(owner, clusters, source, names);
  }

  /** {@inheritDoc} */
  @Override
  final AlgorithmBehaviorCluster _create(final int nameIndex,
      final DataSelection selection) {
    return new AlgorithmBehaviorCluster(this, nameIndex, selection);
  }

  /** {@inheritDoc} */
  @Override
  final void _distanceAggregationText(final ITextOutput textOut) {
    textOut.append(
        "The distance between two algorithm setups is then the sum of the distances of their fitted models on the same benchmark instance and dimensions, over all benchmark instances."); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord("algorithm behavior clustering", textOut); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord("clustering by algorithm behavior", //$NON-NLS-1$
        textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    final ETextCase nextCase;

    nextCase = textCase.appendWord("the", textOut); //$NON-NLS-1$
    textOut.append(
        " experiments, i.e., algorithm setups, are divided into groups according to their runtime behavior. "); //$NON-NLS-1$
    return super.printDescription(textOut, nextCase);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPathComponentSuggestion() {
    return "algorithmBehavior" + this.getData().size(); //$NON-NLS-1$
  }
}
