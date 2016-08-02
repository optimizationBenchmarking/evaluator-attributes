package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.AlgorithmBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behavior.InstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties.FeatureInstanceBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties.ParameterAlgorithmBehaviorClusterer;
import org.optimizationBenchmarking.evaluator.attributes.clusters.byInstance.ByInstanceGrouping;
import org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.PropertyValueSelector;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * This class allows for load a clustering method.
 */
public final class ClustererLoader {
  /** The the group-by parameter: {@value} */
  public static final String PARAM_GROUPING = "grouping"; //$NON-NLS-1$
  /** the minimum number of groups to use */
  public static final String PARAM_MIN_GROUPS = "minGroups"; //$NON-NLS-1$
  /** the maximum number of clusters to use */
  public static final String PARAM_MAX_GROUPS = "maxGroups"; //$NON-NLS-1$
  /**
   * the maximum value for parameters {@link #PARAM_MAX_GROUPS} and
   * {@link #PARAM_MIN_GROUPS}
   */
  public static final int MAX_GROUPS = 100;

  /**
   * Obtain the attribute used to get the clustering
   *
   * @param data
   *          the data
   * @param config
   *          the configuration
   * @return the clustering, or {@code null} if no clustering is provided
   */
  public static final Attribute<? super IExperimentSet, ? extends IClustering> configureClustering(
      final IExperimentSet data, final Configuration config) {
    final String grouping;

    grouping = config.getString(ClustererLoader.PARAM_GROUPING, null);
    if (grouping == null) {
      return null;
    }

    switch (TextUtils.toLowerCase(grouping)) {
      case ByInstanceGrouping.CHOICE_BY_INSTANCE: {
        return ByInstanceGrouping.INSTANCE;
      }
      case PropertyValueSelector.CHOICE_EXPERIMENTS_BY_PARAMETER_VALUE:
      case PropertyValueSelector.CHOICE_INSTANCES_BY_FEATURE_VALUE: {
        return PropertyValueSelector.configure(data, config);
      }
      case InstanceBehaviorClusterer.CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR: {
        return new InstanceBehaviorClusterer(config);
      }
      case FeatureInstanceBehaviorClusterer.CHOICE_INSTANCES_BY_ALGORITHM_BEHAVIOR_AND_FEATURES: {
        return new FeatureInstanceBehaviorClusterer(config);
      }
      case AlgorithmBehaviorClusterer.CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR: {
        return new AlgorithmBehaviorClusterer(config);
      }
      case ParameterAlgorithmBehaviorClusterer.CHOICE_ALGORITHMS_BY_ALGORITHM_BEHAVIOR_AND_PARAMETERS: {
        return new ParameterAlgorithmBehaviorClusterer(config);
      }
      default: {
        throw new IllegalArgumentException(((//
        "Unknown grouping scheme: '" + grouping) + '\'') + '.');//$NON-NLS-1$
      }
    }
  }

  /** the forbidden constructor */
  private ClustererLoader() {
    ErrorUtils.doNotCall();
  }
}
