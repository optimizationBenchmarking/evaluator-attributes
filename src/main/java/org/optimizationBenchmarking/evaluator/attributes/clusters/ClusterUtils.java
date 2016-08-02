package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.IList;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.IText;
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
   * @return the next text case
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
    next = next.appendWord(", namely", textOut);//$NON-NLS-1$
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
   * @return the next text case
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
    next = next.appendWord(", namely", textOut);//$NON-NLS-1$
    textOut.append(' ');
    ESequenceMode.AND.appendSequence(next, experiments, true, textOut);
    textOut.append('.');

    return next.nextAfterSentenceEnd();
  }

  /**
   * Print the list of clusters belonging to this clustering
   *
   * @param clustering
   *          the clusters
   * @param body
   *          the target section body
   */
  public static final void listClusters(final IClustering clustering,
      final ISectionBody body) {
    final ArrayListView<? extends ICluster> list;
    final int size;

    list = clustering.getData();
    size = list.size();

    if (size <= 0) {
      body.append("Not a single cluster was formed. Odd."); //$NON-NLS-1$
      return;
    }

    if (size == 1) {
      body.append("Only one single cluster was formed: "); //$NON-NLS-1$
      list.get(0).printDescription(body, ETextCase.AT_SENTENCE_START);
      return;
    }

    body.append("The following ");//$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(list.size(),
        ETextCase.IN_SENTENCE, body);
    body.append(" clusters were formed:"); //$NON-NLS-1$

    try (final IList enumeration = body.enumeration()) {
      for (final ICluster cluster : list) {
        try (final IText text = enumeration.item()) {
          cluster.printDescription(text, ETextCase.AT_SENTENCE_START);
        }
      }
    }
  }

  /** the forbidden constructor */
  private ClusterUtils() {
    ErrorUtils.doNotCall();
  }
}
