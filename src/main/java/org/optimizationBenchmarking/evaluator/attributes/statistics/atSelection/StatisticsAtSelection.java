package org.optimizationBenchmarking.evaluator.attributes.statistics.atSelection;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IElementSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.ScalarAggregate;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Median;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameter;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * A statistic parameter is computed at a specified selection of an
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns}
 * set. For other objects, it is aggregated. This attribute works for
 * <ul>
 * <li>
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns}
 * </li>
 * <li>{@link org.optimizationBenchmarking.evaluator.data.spec.IExperiment}
 * </li>
 * <li>
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet}
 * </li>
 * <li>{@link org.optimizationBenchmarking.evaluator.data.spec.IInstance}
 * </li>
 * <li>
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet}
 * </li>
 * <li>
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue}
 * </li>
 * <li>
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IParameterValue}
 * </li>
 * </ul>
 */
public final class StatisticsAtSelection
    extends Attribute<IDataElement, Number> {

  /** the instance-runs based statistics */
  private final InstanceRunsStatisticsAtSelection m_runs;

  /**
   * how to aggregate the parameter, say over
   * {@link org.optimizationBenchmarking.evaluator.data.spec.IExperiment} s
   * or {@link org.optimizationBenchmarking.evaluator.data.spec.IInstance}
   * s.
   */
  private final StatisticalParameter m_aggregate;

  /** the hash code */
  private final int m_hashCode;

  /**
   * Create a statistics computer for a given selection criterion
   *
   * @param selection
   *          the selection criterion
   * @param dimension
   *          the dimension over which to compute the statistics
   * @param parameter
   *          the parameter to compute per
   *          {@link org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns}
   * @param aggregate
   *          how to aggregate the parameter, say over
   *          {@link org.optimizationBenchmarking.evaluator.data.spec.IExperiment}
   *          s or
   *          {@link org.optimizationBenchmarking.evaluator.data.spec.IInstance}
   *          s, or {@code null} for default (
   *          {@linkplain org.optimizationBenchmarking.utils.math.statistics.parameters.Median
   *          median}).
   */
  public StatisticsAtSelection(final SelectionCriterion selection,
      final IDimension dimension, final StatisticalParameter parameter,
      final StatisticalParameter aggregate) {
    super(EAttributeType.TEMPORARILY_STORED);

    this.m_runs = new InstanceRunsStatisticsAtSelection(selection,
        dimension, parameter);
    this.m_aggregate = ((aggregate == null) ? Median.INSTANCE : aggregate);
    this.m_hashCode = HashUtils.combineHashes(//
        HashUtils.hashCode(this.m_aggregate), //
        HashUtils.hashCode(this.m_runs));
  }

  /** {@inheritDoc} */
  @Override
  protected final Number compute(final IDataElement data,
      final Logger logger) {
    final ScalarAggregate agg;
    final IInstance instance;
    final IFeatureValue featureValue;
    final IParameterValue parameterValue;

    if (data instanceof IInstanceRuns) {
      return this.m_runs.get(((IInstanceRuns) data), logger);
    }

    finder: {
      if (data instanceof IInstance) {
        agg = this.m_aggregate.createSampleAggregate();
        instance = ((IInstance) data);
        exps: for (final IExperiment experiment : instance.getOwner()
            .getOwner().getData()) {
          for (final IInstanceRuns runs : experiment.getData()) {
            if (Compare.equals(runs.getInstance(), instance)) {
              agg.append(this.get(runs, logger));
              continue exps;
            }
          }
        }
        return agg.toNumber();
      }

      if (data instanceof IFeatureValue) {
        agg = this.m_aggregate.createSampleAggregate();
        featureValue = ((IFeatureValue) data);
        for (final IInstance finstance : featureValue.getOwner().getOwner()
            .getOwner().getInstances().getData()) {
          if (finstance.getFeatureSetting().contains(featureValue)) {
            agg.append(this.compute(finstance, logger));
          }
        }
        return agg.toNumber();
      }

      if (data instanceof IParameterValue) {
        agg = this.m_aggregate.createSampleAggregate();
        parameterValue = ((IParameterValue) data);
        for (final IExperiment exp : parameterValue.getOwner().getOwner()
            .getOwner().getData()) {
          if (exp.getParameterSetting().contains(parameterValue)) {
            agg.append(this.compute(exp, logger));
          }
        }
        return agg.toNumber();
      }

      if (data instanceof IElementSet) {
        agg = this.m_aggregate.createSampleAggregate();
        for (final Object element : ((IElementSet) data).getData()) {
          if (element instanceof IDataElement) {
            agg.append(this.compute(((IDataElement) element), logger));
          } else {
            break finder;
          }
        }
        return agg.toNumber();
      }
    }

    throw new IllegalArgumentException("Class " + //$NON-NLS-1$
        TextUtils.className(data) + //
        " not supported by attribute " + //$NON-NLS-1$
        TextUtils.className(this));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.m_hashCode;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final StatisticsAtSelection r;
    if (o == this) {
      return true;
    }
    if (o instanceof StatisticsAtSelection) {
      r = ((StatisticsAtSelection) o);
      return (Compare.equals(r.m_aggregate, this.m_aggregate) && //
          Compare.equals(r.m_runs, this.m_runs));
    }
    return false;
  }
}
