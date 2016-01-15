package org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.ShadowExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;

/**
 * This checks all experiments in an experiment set and chooses only
 * instance runs for which data from at least one experiment exists. Assume
 * that we have four instances, {@code A}, {@code B}, {@code C}, and
 * {@code D} as well as three experiments {@code I}, {@code II}, and
 * {@code III}. Experiment {@code I} has runs for instance {@code A},
 * {@code B}, {@code C}, but not {@code D}. Experiment {@code II} has runs
 * for instances {@code A} and {@code C}. Experiment {@code III} has runs
 * only for instance {@code A} and {@code B}. Then a shadow copy of the
 * original experiment set will be created with shadow copies of the
 * experiments only containing the runs for instances {@code A}, {@code B},
 * and {@code C}. If all experiments have data for all instances, this
 * attribute returns the original experiment set.
 */
public final class OnlyUsedInstances
    extends Attribute<IExperimentSet, IExperimentSet> {

  /**
   * The globally shared instance of the {@link OnlyUsedInstances}
   * attribute
   */
  public static final OnlyUsedInstances INSTANCE = new OnlyUsedInstances();

  /** create the instance of this attribute */
  private OnlyUsedInstances() {
    super(EAttributeType.TEMPORARILY_STORED);
  }

  /** {@inheritDoc} */
  @Override
  protected final IExperimentSet compute(final IExperimentSet data,
      final Logger logger) {
    final ArrayListView<? extends IInstance> origInstances;
    final ArrayList<IInstance> instances;
    final int origSize;
    final DataSelection selection;

    origInstances = data.getInstances().getData();
    origSize = origInstances.size();

    if (origSize <= 0) {
      return data;
    }

    instances = new ArrayList<>(origInstances.size());
    outer: for (final IInstance instance : origInstances) {
      for (final IExperiment exp : data.getData()) {
        for (final IInstanceRuns runs : exp.getData()) {
          if (Compare.equals(runs.getInstance(), instance)) {
            instances.add(instance);
            continue outer;
          }
        }
      }
    }

    if (instances.size() >= origSize) {
      return data;
    }

    selection = new DataSelection(data);
    selection.addInstances(instances);

    return new ShadowExperimentSet<>(data, selection);
  }
}
