package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.Arrays;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * A clusterer for clustering via algorithm behavior.
 */
public final class AlgorithmBehaviorClusterer
    extends _BehaviorClusterer<AlgorithmBehaviorClustering> {

  /**
   * indicate that clustering should be performed by algorithm, based on
   * their runtime behavior
   */
  public static final String CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR = "algorithms by behavior"; //$NON-NLS-1$

  /**
   * create algorithm behavior clusterer
   *
   * @param minClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   * @param maxClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   */
  public AlgorithmBehaviorClusterer(final int minClusters,
      final int maxClusters) {
    super(minClusters, maxClusters);
  }

  /**
   * create the algorithm behavior clusterer from a given configuration
   *
   * @param config
   *          the configuration
   */
  public AlgorithmBehaviorClusterer(final Configuration config) {
    super(config);
  }

  /** {@inheritDoc} */
  @Override
  final AlgorithmBehaviorClustering _create(final IExperimentSet data,
      final int[] clustering, final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    return new AlgorithmBehaviorClustering(data, clustering, source,
        names);
  }

  /** {@inheritDoc} */
  @Override
  final INamedElementSet _getElementsToCluster(final IExperimentSet data) {
    return data;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return AlgorithmBehaviorClusterer.CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR;
  }

  /** {@inheritDoc} */
  @Override
  final IInstanceRuns[] _getRunsPerElement(final IExperimentSet data,
      final INamedElement element, final String[] categories) {
    final ArrayListView<? extends IInstanceRuns> list;
    final IInstanceRuns[] runs;
    IInstanceRuns run;
    int index;

    list = ((IExperiment) element).getData();
    index = categories.length;
    runs = new IInstanceRuns[index];

    for (; (--index) >= 0;) {
      run = list.get(index);
      runs[Arrays.binarySearch(categories,
          run.getInstance().getName())] = run;
    }

    return runs;
  }

  /** {@inheritDoc} */
  @Override
  final String[] _getRunCategories(final IExperimentSet data) {
    final ArrayListView<? extends IInstance> instances;
    final String[] strings;
    int index;

    instances = data.getInstances().getData();
    index = instances.size();
    strings = new String[index];
    for (; (--index) >= 0;) {
      strings[index] = instances.get(index).getName();
    }
    return strings;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(973543481, super.calcHashCode());
  }
}
