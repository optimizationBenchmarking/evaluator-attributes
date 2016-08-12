package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.IList;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.IText;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Some utilities for the clusters */
public final class ClusterUtils {

  /**
   * List the benchmark instances of a cluster
   *
   * @param cluster
   *          the cluster
   * @param maxElementsForListing
   *          the maximum number of instances that should be listed
   * @param textCase
   *          the text case
   * @param textOut
   *          the text destination the next text case
   * @return the next text case
   */
  public static final ETextCase listInstances(final ICluster cluster,
      final int maxElementsForListing, final ETextCase textCase,
      final ITextOutput textOut) {
    ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = cluster.printShortName(textOut, next);
    textOut.append(' ');
    next = next.appendWord("with", textOut);//$NON-NLS-1$
    textOut.append(' ');
    return TextUtils.appendElements(cluster.getInstances().getData(),
        "benchmark instance", "benchmark instances", //$NON-NLS-1$//$NON-NLS-2$
        maxElementsForListing, next, ESequenceMode.AND, textOut);
  }

  /**
   * List the algorithm setups of a cluster
   *
   * @param cluster
   *          the cluster
   * @param maxElementsForListing
   *          the maximum number of experiments that should be listed
   * @param textCase
   *          the text case
   * @param textOut
   *          the text destination the next text case
   * @return the next text case
   */
  public static final ETextCase listExperiments(final ICluster cluster,
      final int maxElementsForListing, final ETextCase textCase,
      final ITextOutput textOut) {
    ETextCase next;

    next = textCase.appendWord("cluster", textOut);//$NON-NLS-1$
    textOut.append(' ');
    next = cluster.printShortName(textOut, next);
    textOut.append(' ');
    next = next.appendWord("with", textOut);//$NON-NLS-1$
    textOut.append(' ');
    return TextUtils.appendElements(cluster.getData(), "algorithm setup", //$NON-NLS-1$
        "algorithm setups", //$NON-NLS-1$
        maxElementsForListing, next, ESequenceMode.AND, textOut);
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
