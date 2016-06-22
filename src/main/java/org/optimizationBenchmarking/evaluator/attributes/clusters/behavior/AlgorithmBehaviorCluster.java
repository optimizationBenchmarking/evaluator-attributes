package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
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
    final ArrayListView<? extends IExperiment> experiments;
    final ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');
    this.printShortName(textOut, ETextCase.IN_SENTENCE);
    textOut.append(" contains the runs from ");//$NON-NLS-1$
    experiments = this.getData();
    InTextNumberAppender.INSTANCE.appendTo(experiments.size(),
        ETextCase.IN_SENTENCE, textOut);
    textOut.append(" algorithm setups, namely "); //$NON-NLS-1$
    ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE, experiments,
        true, textOut);
    textOut.append('.');
    return next;
  }
}
