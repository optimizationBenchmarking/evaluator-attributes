package org.optimizationBenchmarking.evaluator.attributes.functions;

import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IElementSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSetting;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.comparison.Compare;

/**
 * A constant representing an instance feature value.
 */
final class _FeatureConstant extends _PropertyConstant {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /**
   * create the feature constant
   *
   * @param feature
   *          the feature
   */
  _FeatureConstant(final IFeature feature) {
    super(feature);
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  final Object _getValue(final IDataElement element) {
    if (element instanceof IInstance) {
      return this.__getValue((IInstance) element);
    }
    if (element instanceof IFeatureSetting) {
      return this.__getValue((IFeatureSetting) element);
    }
    if (element instanceof IInstanceRuns) {
      return this.__getValue((IInstanceRuns) element);
    }
    if (element instanceof IRun) {
      return this.__getValue((IRun) element);
    }
    if (element instanceof IExperiment) {
      return this.__getValue((IExperiment) element);
    }
    if (element instanceof IExperimentSet) {
      return this.__getValue((IExperimentSet) element);
    }
    if (element instanceof IInstanceSet) {
      return this.__getValue((IInstanceSet) element);
    }
    if (element instanceof IElementSet) {
      return this.__getValue((IElementSet) element);
    }
    if (element instanceof Iterable) {
      return this.__getValue((Iterable) element);
    }

    throw new IllegalArgumentException(//
        "Cannot get a feature value from a " //$NON-NLS-1$
            + element);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ("Feature " + this.m_property.getName()); //$NON-NLS-1$
  }

  /**
   * Get the value of a feature from a feature setting
   *
   * @param setting
   *          the setting
   * @return the feature value
   */
  private final Object __getValue(final IFeatureSetting setting) {
    return setting.get(this.m_property);
  }

  /**
   * Get the value of a feature from an instance
   *
   * @param instance
   *          the instance
   * @return the feature value
   */
  private final Object __getValue(final IInstance instance) {
    return this.__getValue(instance.getFeatureSetting());
  }

  /**
   * Get the value of a feature from an instance run set
   *
   * @param instanceRuns
   *          the instance runs
   * @return the feature value
   */
  private final Object __getValue(final IInstanceRuns instanceRuns) {
    return this.__getValue(instanceRuns.getInstance());
  }

  /**
   * Get the value of a feature from a run
   *
   * @param run
   *          the run
   * @return the feature value
   */
  private final Object __getValue(final IRun run) {
    return this.__getValue(run.getOwner());
  }

  /**
   * Get the value from a collection of things
   *
   * @param list
   *          the list
   * @return the result
   */
  private final Object __getValue(final Iterable<? extends Object> list) {
    Object value, newVal;

    value = null;
    looper: for (final Object element : list) {
      if (element instanceof IDataElement) {
        newVal = this._getValue((IDataElement) element);
        if (newVal == null) {
          value = null;
          break looper;
        }
        if (value == null) {
          value = newVal;
        } else {
          if (!(Compare.equals(value, newVal))) {
            throw new IllegalArgumentException(//
                "Feature value must be the same over the whole experiment, otherwise computation is inconsistent, but we discovered the non-equal values "//$NON-NLS-1$
                    + value + " and " + newVal);//$NON-NLS-1$
          }
        }
      } else {
        throw new IllegalArgumentException(//
            "Cannot deal with collections containing "//$NON-NLS-1$
                + element);
      }
    }

    if (value == null) {
      throw new IllegalArgumentException("Feature value cannot be null."); //$NON-NLS-1$
    }

    return value;
  }

  /**
   * Get the value of a feature from an experiment
   *
   * @param experiment
   *          the experiment
   * @return the feature value
   */
  private final Object __getValue(final IExperiment experiment) {
    return this.__getValue(experiment.getData());
  }

  /**
   * Get the value of a feature from an experiment set
   *
   * @param experimentSet
   *          the experimentSet
   * @return the feature value
   */
  private final Object __getValue(final IExperimentSet experimentSet) {
    return this.__getValue(experimentSet.getData());
  }

  /**
   * Get the value of a feature from an element set
   *
   * @param elementSet
   *          the elementSet
   * @return the feature value
   */
  private final Object __getValue(final IElementSet elementSet) {
    return this.__getValue(elementSet.getData());
  }

  /**
   * Get the value of a feature from an instance set
   *
   * @param instanceSet
   *          the instanceSet
   * @return the feature value
   */
  private final Object __getValue(final IInstanceSet instanceSet) {
    return this.__getValue(instanceSet.getData());
  }

}
