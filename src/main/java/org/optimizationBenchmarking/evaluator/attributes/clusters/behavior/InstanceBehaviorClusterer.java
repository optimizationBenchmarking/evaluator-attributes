package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.Arrays;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A clusterer for clustering via instance behavior.
 */
public final class InstanceBehaviorClusterer
    extends _BehaviorClusterer<InstanceBehaviorClustering> {

  /**
   * indicate that clustering should be performed by instance, based on the
   * behavior of the algorithms on the instance
   */
  public static final String CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR = "instances by algorithm behavior"; //$NON-NLS-1$

  /** the instance behavior clustering name */
  private static final String NAME = "instanceBehavior";//$NON-NLS-1$

  /**
   * create instance behavior clusterer
   *
   * @param transformations
   *          the dimension transformations, or {@code null} if all
   *          dimensions can be used directly
   * @param minClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   * @param maxClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   */
  public InstanceBehaviorClusterer(
      final DimensionTransformation[] transformations,
      final int minClusters, final int maxClusters) {
    super(transformations, minClusters, maxClusters,
        InstanceBehaviorClusterer.NAME);
  }

  /**
   * create the instance behavior clusterer from a given configuration
   *
   * @param experimentSet
   *          the experiment set
   * @param config
   *          the configuration
   */
  public InstanceBehaviorClusterer(final IExperimentSet experimentSet,
      final Configuration config) {
    super(experimentSet, config, InstanceBehaviorClusterer.NAME);
  }

  /** {@inheritDoc} */
  @Override
  final InstanceBehaviorClustering _create(final IExperimentSet data,
      final int[] clustering, final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    return new InstanceBehaviorClustering(data, clustering, source, names,
        this.m_pathComponentSuggestion);
  }

  /** {@inheritDoc} */
  @Override
  final INamedElementSet _getElementsToCluster(final IExperimentSet data) {
    return data.getInstances();
  }

  /** {@inheritDoc} */
  @Override
  public final void toText(final ITextOutput textOut) {
    textOut.append(
        InstanceBehaviorClusterer.CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR);
    super.toText(textOut);
  }

  /** {@inheritDoc} */
  @Override
  final IInstanceRuns[] _getRunsPerElement(final IExperimentSet data,
      final INamedElement element, final String[] categories) {
    final ArrayListView<? extends IExperiment> source;
    final String name;
    final IInstanceRuns[] runs;
    IExperiment exp;
    int index;

    source = data.getData();
    index = categories.length;
    runs = new IInstanceRuns[index];
    name = element.getName();

    outer: for (; (--index) >= 0;) {
      exp = source.get(index);
      for (final IInstanceRuns irun : exp.getData()) {
        if (irun.getInstance().getName().equals(name)) {
          runs[Arrays.binarySearch(categories, exp.getName())] = irun;
          continue outer;
        }
      }

      throw new IllegalStateException("Could not find run for instance '" //$NON-NLS-1$
          + name + "' in experiment '" + exp.getName() //$NON-NLS-1$
          + '\'' + '.');
    }

    return runs;
  }

  /** {@inheritDoc} */
  @Override
  final String[] _getRunCategories(final IExperimentSet data) {
    final ArrayListView<? extends IExperiment> experiments;
    final String[] strings;
    int index;

    experiments = data.getData();
    index = experiments.size();
    strings = new String[index];
    for (; (--index) >= 0;) {
      strings[index] = experiments.get(index).getName();
    }
    return strings;
  }
}
