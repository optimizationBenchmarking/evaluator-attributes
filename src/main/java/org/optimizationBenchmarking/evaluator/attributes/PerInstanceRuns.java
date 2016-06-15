package org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.parallel.Execute;

/**
 * This class allows us to obtain one attribute value for each instance run
 *
 * @param <R>
 *          the result type
 */
public final class PerInstanceRuns<R> {

  /** the internal hash map */
  private final HashMap<String, HashMap<String, __Holder>> m_map;

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
    HashMap<String, __Holder> map;

    this.m_map = new HashMap<>();
    experimentIndex = (-1);
    for (final IExperiment experiment : data.getData()) {
      ++experimentIndex;
      instanceIndex = (-1);
      map = new HashMap<>();
      this.m_map.remove(experiment.getName(), map);
      for (final IInstanceRuns runs : experiment.getData()) {
        map.put(runs.getInstance().getName(),
            new __Holder(//
                experimentIndex, ++instanceIndex, //
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
  @SuppressWarnings("unchecked")
  public final R get(final String experimentName,
      final String instanceName) {
    return ((R) (this.m_map.get(experimentName).get(instanceName)._get()));
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
  public final R get(final IExperiment experiment,
      final IInstance instance) {
    return this.get(experiment.getName(), instance.getName());
  }

  /**
   * Get the result for the given instance run set
   *
   * @param runs
   *          the instance runs
   * @return the result
   */
  public final R get(final IInstanceRuns runs) {
    return this.get(runs.getOwner(), runs.getInstance());
  }

  /**
   * finalize a data array
   *
   * @param data
   *          the array
   * @return the list view
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final <X> ArrayListView<X> __finalize(
      final Object[] data) {
    int index;

    Arrays.sort(data);
    for (index = data.length; (--index) >= 0;) {
      data[index] = ((__Holder) (data[index]))._get();
    }
    return new ArrayListView(data);
  }

  /**
   * Get all results for a given experiment
   *
   * @param experiment
   *          the experiment
   * @return the results
   */
  public final ArrayListView<R> getAllForExperiment(
      final IExperiment experiment) {
    return this.getAllForExperiment(experiment.getName());
  }

  /**
   * Get all results for a given experiment
   *
   * @param experimentName
   *          the experiment name
   * @return the results
   */
  public final ArrayListView<R> getAllForExperiment(
      final String experimentName) {
    final HashMap<String, __Holder> map;

    map = this.m_map.get(experimentName);
    return PerInstanceRuns
        .__finalize(map.values().toArray(new Object[map.size()]));
  }

  /**
   * Get all results for a given instance
   *
   * @param instanceName
   *          the instance name
   * @return the results
   */
  public final ArrayListView<R> getAllForInstance(
      final String instanceName) {
    Object[] data, temp;
    int index, size;
    __Holder holder;

    size = this.m_map.size();
    data = new Object[size];
    index = 0;
    for (final HashMap<String, __Holder> map : this.m_map.values()) {
      holder = map.get(instanceName);
      if (holder != null) {
        data[index++] = holder;
      }
    }

    if (index != size) {
      temp = new Object[index];
      System.arraycopy(data, 0, temp, 0, index);
      data = temp;
    }
    return PerInstanceRuns.__finalize(data);
  }

  /**
   * Get all results for a given instance
   *
   * @param instance
   *          the instance
   * @return the results
   */
  public final ArrayListView<R> getAllForInstance(
      final IInstance instance) {
    return this.getAllForInstance(instance.getName());
  }

  /**
   * Get all results
   *
   * @return the results
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final ArrayListView<R> getAll() {
    final ArrayList list;

    list = new ArrayList<>();
    for (final HashMap<String, __Holder> map : this.m_map.values()) {
      list.addAll(map.values());
    }
    return PerInstanceRuns
        .__finalize(list.toArray(new Object[list.size()]));
  }

  /** a holder */
  private static final class __Holder implements Comparable<__Holder> {
    /** the experiment index */
    final int m_experimentIndex;
    /** the instance index */
    final int m_instanceIndex;
    /** the data */
    private Object m_data;

    /**
     * create
     *
     * @param experimentIndex
     *          the experiment index
     * @param instanceIndex
     *          the instance index
     * @param data
     *          the data
     */
    __Holder(final int experimentIndex, final int instanceIndex,
        final Future<?> data) {
      super();
      this.m_experimentIndex = experimentIndex;
      this.m_instanceIndex = instanceIndex;
      this.m_data = data;
    }

    /**
     * get the value
     *
     * @return the result
     */
    @SuppressWarnings("rawtypes")
    final Object _get() {
      Throwable cause;

      if (this.m_data instanceof Future) {
        loop: for (;;) {
          try {
            this.m_data = ((Future) (this.m_data)).get();
          } catch (@SuppressWarnings("unused") final InterruptedException interExp) {
            continue loop;
          } catch (final ExecutionException exec) {
            cause = exec.getCause();
            if (cause instanceof RuntimeException) {
              throw ((RuntimeException) cause);
            }
            throw new IllegalStateException(
                "Error while obtaining attribute job result.", //$NON-NLS-1$
                exec);
          }

          break loop;
        }
      }
      return this.m_data;
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
  }
}
