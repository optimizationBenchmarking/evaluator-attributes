package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
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
    final ArrayListView<? extends IInstance> instances;
    final ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');

    this.printShortName(textOut, ETextCase.IN_SENTENCE);
    textOut.append(" contains the runs collected on ");//$NON-NLS-1$
    instances = this.getInstances().getData();
    InTextNumberAppender.INSTANCE.appendTo(instances.size(),
        ETextCase.IN_SENTENCE, textOut);
    textOut.append(" algorithm setups, namely "); //$NON-NLS-1$
    ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE, instances,
        true, textOut);
    textOut.append('.');
    return next;
  }
}
