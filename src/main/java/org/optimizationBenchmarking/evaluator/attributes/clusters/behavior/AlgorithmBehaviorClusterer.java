package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.Arrays;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

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

  /** the algorithm behavior clustering name */
  private static final String NAME = "algorithmBehavior";//$NON-NLS-1$

  /**
   * create algorithm behavior clusterer
   *
   * @param transformations
   *          the dimension transformations
   * @param minClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   * @param maxClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   */
  public AlgorithmBehaviorClusterer(
      final DimensionTransformation[] transformations,
      final int minClusters, final int maxClusters) {
    super(transformations, minClusters, maxClusters,
        AlgorithmBehaviorClusterer.NAME);
  }

  /**
   * create the algorithm behavior clusterer from a given configuration
   *
   * @param experimentSet
   *          the experiment set
   * @param config
   *          the configuration
   */
  public AlgorithmBehaviorClusterer(final IExperimentSet experimentSet,
      final Configuration config) {
    super(experimentSet, config, AlgorithmBehaviorClusterer.NAME);
  }

  /** {@inheritDoc} */
  @Override
  final AlgorithmBehaviorClustering _create(final IExperimentSet data,
      final int[] clustering, final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    return new AlgorithmBehaviorClustering(data, clustering, source, names,
        this.m_pathComponentSuggestion);
  }

  /** {@inheritDoc} */
  @Override
  final INamedElementSet _getElementsToCluster(final IExperimentSet data) {
    return data;
  }

  /** {@inheritDoc} */
  @Override
  public final void toText(final ITextOutput textOut) {
    textOut.append(
        AlgorithmBehaviorClusterer.CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR);
    super.toText(textOut);
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
}
