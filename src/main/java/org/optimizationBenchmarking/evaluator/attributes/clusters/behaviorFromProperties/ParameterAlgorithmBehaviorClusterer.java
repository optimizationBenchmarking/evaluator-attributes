package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.LinkedHashSet;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.AlgorithmBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;

/**
 * A clusterer for clustering via algorithm behavior and instance features.
 */
public final class ParameterAlgorithmBehaviorClusterer
    extends _PropertyBehaviorClusterer<IExperiment> {

  /**
   * indicate that clustering should be performed by algorithm setup, based
   * on the behavior on the benchmark instances and the algorithm setup
   * parameters
   */
  public static final String CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR_AND_PARAMETERS = "algorithms by behavior and parameters"; //$NON-NLS-1$

  /**
   * create the instance behavior clusterer from a given configuration
   *
   * @param experimentSet
   *          the experiment set
   * @param config
   *          the configuration
   */
  public ParameterAlgorithmBehaviorClusterer(
      final IExperimentSet experimentSet, final Configuration config) {
    super(new AlgorithmBehaviorClusterer(experimentSet, config));
  }

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
  public ParameterAlgorithmBehaviorClusterer(
      final DimensionTransformation[] transformations,
      final int minClusters, final int maxClusters) {
    super(new AlgorithmBehaviorClusterer(transformations, minClusters,
        maxClusters));
  }

  /** {@inheritDoc} */
  @Override
  final void _getElementsToClassify(final ICluster cluster,
      final IExperimentSet data,
      final LinkedHashSet<IExperiment> elements) {
    for (final IExperiment experiment : cluster.getData()) {
      elements.add(data.find(experiment.getName()));
    }
  }

  /** {@inheritDoc} */
  @Override
  final ArrayListView<? extends IProperty> _getProperties(
      final IExperimentSet data) {
    return data.getParameters().getData();
  }

  /** {@inheritDoc} */
  @Override
  final IPropertySetting _getPropertySetting(final IExperiment element) {
    return element.getParameterSetting();
  }

  /** {@inheritDoc} */
  @Override
  final IClustering _createClustering(final IExperimentSet owner,
      final IClustering behavior, final IClassifier classifier,
      final DataSelection[] selections) {
    return new ParameterAlgorithmBehaviorClustering(owner, behavior,
        classifier, selections);
  }
}
