package org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;

/**
 * This class allows us to obtain one attribute value for each instance run
 *
 * @param <R>
 *          the result type
 */
public final class PerInstanceRuns<R> {

  /** the internal hash map */
  private final HashMap<String, HashMap<String, Map.Entry<IInstanceRuns, R>>> m_map;

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
  @SuppressWarnings("unchecked")
  public PerInstanceRuns(final IExperimentSet data,
      final Attribute<? super IInstanceRuns, R> attribute,
      final Logger logger) {
    super();

    final boolean inForkJoinPool;
    Callable<R> call;
    Object submit;
    int experimentIndex, instanceIndex;
    HashMap<String, Map.Entry<IInstanceRuns, R>> map;

    this.m_map = new HashMap<>();
    inForkJoinPool = ForkJoinTask.inForkJoinPool();
    experimentIndex = (-1);
    for (final IExperiment experiment : data.getData()) {
      ++experimentIndex;
      instanceIndex = (-1);
      map = new HashMap<>();
      this.m_map.put(experiment.getName(), map);
      for (final IInstanceRuns runs : experiment.getData()) {
        call = attribute.getter(runs, logger);
        if (inForkJoinPool) {
          submit = ForkJoinTask.adapt(call).fork();
        } else {
          try {
            submit = call.call();
          } catch (final RuntimeException rexec) {
            throw rexec;
          } catch (final Throwable error) {
            throw new IllegalStateException(error);
          }
        }
        map.put(runs.getInstance().getName(),
            new __Holder(runs, experimentIndex, ++instanceIndex, submit));
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
    final HashMap<String, Map.Entry<IInstanceRuns, R>> map;
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
    Map.Entry<IInstanceRuns, R> holder;

    size = this.m_map.size();
    data = new Map.Entry[size];
    index = 0;
    for (final HashMap<String, Map.Entry<IInstanceRuns, R>> map : this.m_map
        .values()) {
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
    for (final HashMap<String, Map.Entry<IInstanceRuns, R>> map : this.m_map
        .values()) {
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

  /** a holder */
  @SuppressWarnings("rawtypes")
  private static final class __Holder
      implements Comparable<__Holder>, Map.Entry {
    /** the experiment index */
    final int m_experimentIndex;
    /** the instance index */
    final int m_instanceIndex;
    /** the owning instance runs */
    private final IInstanceRuns m_instanceRuns;
    /** the data object */
    private Object m_data;

    /**
     * create
     *
     * @param experimentIndex
     *          the experiment index
     * @param instanceIndex
     *          the instance index
     * @param instanceRuns
     *          the instance runs
     * @param submit
     *          the submit
     */
    __Holder(final IInstanceRuns instanceRuns, final int experimentIndex,
        final int instanceIndex, final Object submit) {
      super();
      this.m_instanceRuns = instanceRuns;
      this.m_experimentIndex = experimentIndex;
      this.m_instanceIndex = instanceIndex;
      this.m_data = submit;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Holder o) {
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
    public final Object getValue() {
      Object data;

      data = this.m_data;
      if (data instanceof Future) {
        loop: for (;;) {
          try {
            this.m_data = data = ((Future) data).get();
            return data;
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

      return data;
    }

    @Override
    public Object setValue(final Object value) {
      throw new UnsupportedOperationException(//
          "Cannot set value of per-instance-runs job holder.");//$NON-NLS-1$
    }
  }
}
