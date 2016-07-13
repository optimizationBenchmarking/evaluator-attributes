package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Some utilities for the clusters */
public final class ClusterUtils {

  /**
   * List the benchmark instances of a cluster
   *
   * @param cluster
   *          the cluster
   * @param textCase
   *          the text case
   * @param textOut
   *          the text destination the next text case
   * @return
   */
  public static final ETextCase listInstances(final ICluster cluster,
      final ETextCase textCase, final ITextOutput textOut) {
    final ArrayListView<? extends IInstance> instances;
    final int size;
    ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = cluster.printShortName(textOut, next);

    textOut.append(' ');

    instances = cluster.getInstances().getData();
    size = instances.size();

    if (size <= 0) {
      next = next.appendWords("contains no data", textOut);//$NON-NLS-1$
      textOut.append('.');
      return next;
    }

    next = next.appendWords("contains the runs collected on", textOut);//$NON-NLS-1$
    textOut.append(' ');

    next = InTextNumberAppender.INSTANCE.appendTo(instances.size(), next,
        textOut);

    textOut.append(' ');
    next = next.appendWord("benchmark", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = next.appendWord((size > 1) ? "instances" : "instance", textOut);//$NON-NLS-1$//$NON-NLS-2$
    textOut.append(' ');
    next = next.appendWord("namely", textOut);//$NON-NLS-1$
    textOut.append(' ');
    ESequenceMode.AND.appendSequence(next, instances, true, textOut);
    textOut.append('.');
    return next.nextAfterSentenceEnd();
  }

  /**
   * List the algorithm setups of a cluster
   *
   * @param cluster
   *          the cluster
   * @param textCase
   *          the text case
   * @param textOut
   *          the text destination the next text case
   * @return
   */
  public static final ETextCase listExperiments(final ICluster cluster,
      final ETextCase textCase, final ITextOutput textOut) {
    final ArrayListView<? extends IExperiment> experiments;
    final int size;
    ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = cluster.printShortName(textOut, next);

    textOut.append(' ');

    experiments = cluster.getData();
    size = experiments.size();

    if (size <= 0) {
      next = next.appendWords("contains no data", textOut);//$NON-NLS-1$
      textOut.append('.');
      return next;
    }

    next = next.appendWords("contains the runs collected on", textOut);//$NON-NLS-1$
    textOut.append(' ');

    next = InTextNumberAppender.INSTANCE.appendTo(experiments.size(), next,
        textOut);

    textOut.append(' ');
    next = next.appendWord("algorithm", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = next.appendWord((size > 1) ? "setups" : "setup", textOut);//$NON-NLS-1$//$NON-NLS-2$
    textOut.append(' ');
    next = next.appendWord("namely", textOut);//$NON-NLS-1$
    textOut.append(' ');
    ESequenceMode.AND.appendSequence(next, experiments, true, textOut);
    textOut.append('.');

    return next.nextAfterSentenceEnd();
  }

  /** the forbidden constructor */
  private ClusterUtils() {
    ErrorUtils.doNotCall();
  }
}
