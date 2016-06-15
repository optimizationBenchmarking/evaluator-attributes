package org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.parallel.Execute;

/**
 * This class allows us to obtain one attribute value for each instance run
 *
 * @param <R>
 *          the result type
 */
public final class PerInstanceRuns<R> {

  /** the internal hash map */
  private final HashMap<String, HashMap<String, __Holder<R>>> m_map;

  /**
   * Create the per-instance runs
   *
   * @param data
   *          the data set
   * @param attribute
   *          the attribute to evaluate
   * @param logger
   *          the logger to use
   */
  public PerInstanceRuns(final IExperimentSet data,
      final Attribute<? super IInstanceRuns, R> attribute,
      final Logger logger) {
    super();

    int experimentIndex, instanceIndex;
    HashMap<String, __Holder<R>> map;

    this.m_map = new HashMap<>();
    experimentIndex = (-1);
    for (final IExperiment experiment : data.getData()) {
      ++experimentIndex;
      instanceIndex = (-1);
      map = new HashMap<>();
      this.m_map.put(experiment.getName(), map);
      for (final IInstanceRuns runs : experiment.getData()) {
        map.put(runs.getInstance().getName(),
            new __Holder<>(//
                runs, experimentIndex, ++instanceIndex, //
                Execute.parallel(attribute.getter(runs, logger))));
      }
    }
  }

  /**
   * Get the result for the given experiment and instance names.
   *
   * @param experimentName
   *          the experiment name
   * @param instanceName
   *          the instance name
   * @return the result
   */
  public final Map.Entry<IInstanceRuns, R> get(final String experimentName,
      final String instanceName) {
    return this.m_map.get(experimentName).get(instanceName);
  }

  /**
   * Get the result for the given experiment and instance.
   *
   * @param experiment
   *          the experiment
   * @param instance
   *          the instance
   * @return the result
   */
  public final Map.Entry<IInstanceRuns, R> get(
      final IExperiment experiment, final IInstance instance) {
    return this.get(experiment.getName(), instance.getName());
  }

  /**
   * Get the result for the given instance run set
   *
   * @param runs
   *          the instance runs
   * @return the result
   */
  public final Map.Entry<IInstanceRuns, R> get(final IInstanceRuns runs) {
    return this.get(runs.getOwner(), runs.getInstance());
  }

  /**
   * Get all results for a given experiment
   *
   * @param experiment
   *          the experiment
   * @return the results, or {@code null} if no data exists for that
   *         experiment
   */
  public final Map.Entry<IInstanceRuns, R>[] getAllForExperiment(
      final IExperiment experiment) {
    return this.getAllForExperiment(experiment.getName());
  }

  /**
   * Get all results for a given experiment
   *
   * @param experimentName
   *          the experiment name
   * @return the results, or {@code null} if no data exists for that
   *         experiment
   */
  public final Map.Entry<IInstanceRuns, R>[] getAllForExperiment(
      final String experimentName) {
    final HashMap<String, __Holder<R>> map;
    final Map.Entry<IInstanceRuns, R>[] entries;
    final int size;

    map = this.m_map.get(experimentName);
    if (map == null) {
      return null;
    }
    size = map.size();
    if (size <= 0) {
      return null;
    }
    entries = map.values().toArray(new Map.Entry[size]);
    Arrays.sort(entries);
    return entries;
  }

  /**
   * Get all results for a given instance
   *
   * @param instanceName
   *          the instance name
   * @return the results, or {@code null} if no data exists for that
   *         instance
   */
  @SuppressWarnings("unchecked")
  public final Map.Entry<IInstanceRuns, R>[] getAllForInstance(
      final String instanceName) {
    Map.Entry<IInstanceRuns, R>[] data, temp;
    int index, size;
    __Holder<R> holder;

    size = this.m_map.size();
    data = new Map.Entry[size];
    index = 0;
    for (final HashMap<String, __Holder<R>> map : this.m_map.values()) {
      holder = map.get(instanceName);
      if (holder != null) {
        data[index++] = holder;
      }
    }

    if (index <= 0) {
      return null;
    }

    if (index != size) {
      temp = new Map.Entry[index];
      System.arraycopy(data, 0, temp, 0, index);
      data = temp;
    }
    Arrays.sort(data);
    return data;
  }

  /**
   * Get all results for a given instance
   *
   * @param instance
   *          the instance
   * @return the results, or {@code null} if no data exists for that
   *         instance
   */
  public final Map.Entry<IInstanceRuns, R>[] getAllForInstance(
      final IInstance instance) {
    return this.getAllForInstance(instance.getName());
  }

  /**
   * Get all results
   *
   * @return the results, or {@code null} if no data exists
   */
  public final Map.Entry<IInstanceRuns, R>[] getAll() {
    final int size;
    ArrayList<Map.Entry<IInstanceRuns, R>> list;
    Map.Entry<IInstanceRuns, R>[] data;

    if (this.m_map.size() <= 0) {
      return null;
    }
    list = new ArrayList<>();
    for (final HashMap<String, __Holder<R>> map : this.m_map.values()) {
      list.addAll(map.values());
    }
    size = list.size();
    if (size <= 0) {
      return null;
    }
    data = list.toArray(new Map.Entry[size]);
    list = null;
    Arrays.sort(data);
    return data;
  }

  /**
   * a holder
   *
   * @param <X>
   *          the return type
   */
  private static final class __Holder<X>
      implements Comparable<__Holder<?>>, Map.Entry<IInstanceRuns, X> {
    /** the experiment index */
    final int m_experimentIndex;
    /** the instance index */
    final int m_instanceIndex;
    /** the owning instance runs */
    private final IInstanceRuns m_instanceRuns;
    /** the future */
    private Future<X> m_future;
    /** the data */
    private X m_data;

    /**
     * create
     *
     * @param experimentIndex
     *          the experiment index
     * @param instanceIndex
     *          the instance index
     * @param instanceRuns
     *          the instance runs
     * @param future
     *          the future
     */
    __Holder(final IInstanceRuns instanceRuns, final int experimentIndex,
        final int instanceIndex, final Future<X> future) {
      super();
      this.m_instanceRuns = instanceRuns;
      this.m_experimentIndex = experimentIndex;
      this.m_instanceIndex = instanceIndex;
      this.m_future = future;
      this.m_data = null;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Holder<?> o) {
      final int r;
      r = Integer.compare(this.m_experimentIndex, o.m_experimentIndex);
      if (r != 0) {
        return r;
      }
      return Integer.compare(this.m_instanceIndex, o.m_instanceIndex);
    }

    /** {@inheritDoc} */
    @Override
    public final IInstanceRuns getKey() {
      return this.m_instanceRuns;
    }

    /** {@inheritDoc} */
    @Override
    public final synchronized X getValue() {
      X result;
      Future<X> future;

      result = this.m_data;
      if (result == null) {
        future = this.m_future;
        this.m_future = null;
        if (future != null) {
          loop: for (;;) {
            try {
              this.m_data = result = future.get();
            } catch (@SuppressWarnings("unused") final InterruptedException interExp) {
              continue loop;
            } catch (final Throwable exec) {
              throw new IllegalStateException(((((((///
              "Error while obtaining attribute job result for instance '") //$NON-NLS-1$
                  + this.m_instanceRuns.getInstance().getName())
                  + "' and experiment '") + //$NON-NLS-1$
                  this.m_instanceRuns.getOwner().getName()) + '\'') + '.'),
                  exec);
            }
          }
        }
      }

      return result;
    }

    @Override
    public X setValue(final X value) {
      throw new UnsupportedOperationException(//
          "Cannot set value of per-instance-runs job holder.");//$NON-NLS-1$
    }
  }
}
