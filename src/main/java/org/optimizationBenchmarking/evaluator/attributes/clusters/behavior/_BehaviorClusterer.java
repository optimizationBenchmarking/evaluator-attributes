package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.OnlySharedInstances;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ClustererLoader;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipAndData;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipData;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.math.matrix.impl.DistanceMatrix;
import org.optimizationBenchmarking.utils.ml.clustering.impl.DefaultClusterer;
import org.optimizationBenchmarking.utils.ml.clustering.spec.IClusteringJob;
import org.optimizationBenchmarking.utils.ml.clustering.spec.IDistanceClusteringJobBuilder;
import org.optimizationBenchmarking.utils.parallel.Execute;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * Cluster experimental data based on the runtime behavior of algorithms.
 *
 * @param <CCT>
 *          the clustering type
 */
abstract class _BehaviorClusterer<CCT extends IClustering>
    extends Attribute<IExperimentSet, CCT> {

  /**
   * the minimum number of clusters ot be used, {@code -1} for undefined
   */
  private final int m_minClusters;
  /**
   * the maximum number of clusters ot be used, {@code -1} for undefined
   */
  private final int m_maxClusters;

  /**
   * create the clusterer
   *
   * @param minClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   * @param maxClusters
   *          the minimum number of clusters ot be used, {@code -1} for
   *          undefined
   */
  _BehaviorClusterer(final int minClusters, final int maxClusters) {
    super(EAttributeType.TEMPORARILY_STORED);

    if ((minClusters > 0) && (maxClusters > 0)
        && (minClusters > maxClusters)) {
      throw new IllegalArgumentException((((((//
      "The minimum number of clusters ("//$NON-NLS-1$
          + minClusters) + //
          ") cannot be bigger than the maximum number of clusters (")//$NON-NLS-1$
          + maxClusters) + //
          ") in behavior-based grouping of ") + //$NON-NLS-1$
          this.toString()) + '.');
    }

    this.m_minClusters = minClusters;
    this.m_maxClusters = maxClusters;
  }

  /**
   * create the clusterer
   *
   * @param config
   *          the configuration
   */
  _BehaviorClusterer(final Configuration config) {
    this(
        config.getInt(ClustererLoader.PARAM_MIN_GROUPS, -1,
            ClustererLoader.MAX_GROUPS, -1), //
        config.getInt(ClustererLoader.PARAM_MAX_GROUPS, -1,
            ClustererLoader.MAX_GROUPS, -1));
  }

  /**
   * Get the source of the data to be clustered
   *
   * @param data
   *          the data
   * @return the source of the data to be clustered
   */
  abstract INamedElementSet _getElementsToCluster(
      final IExperimentSet data);

  /**
   * Get the categories of the runs
   *
   * @param data
   *          the data
   * @return the categories
   */
  abstract String[] _getRunCategories(final IExperimentSet data);

  /**
   * Get the runs belonging to each element
   *
   * @param data
   *          the data
   * @param element
   *          the element
   * @param categories
   *          the run categories
   * @return the runs
   */
  abstract IInstanceRuns[] _getRunsPerElement(final IExperimentSet data,
      final INamedElement element, final String[] categories);

  /**
   * Get the attributes for the fitting.
   *
   * @param data
   *          the experiment data
   * @return the attributes
   */
  private static final DimensionRelationshipAndData[] __getFittingAttributes(
      final IExperimentSet data) {
    final ArrayListView<? extends IDimension> dims;
    int index, index2;
    ArrayList<DimensionRelationshipAndData> list;
    IDimension dimA, dimB, useDimA;
    boolean dimAIsTime, dimBIsTime;

    dims = data.getDimensions().getData();
    index = dims.size();
    list = new ArrayList<>((index * (index - 1)) >>> 1);
    for (; (--index) > 0;) {
      dimA = dims.get(index);
      dimAIsTime = dimA.getDimensionType().isTimeMeasure();
      for (index2 = index; (--index2) >= 0;) {
        dimB = dims.get(index2);
        dimBIsTime = dimB.getDimensionType().isTimeMeasure();

        if (dimBIsTime != dimAIsTime) {

          if (dimBIsTime) {
            useDimA = dimB;
            dimB = dimA;
          } else {
            useDimA = dimA;
          }

          list.add(new DimensionRelationshipAndData(useDimA, dimB));
        }
      }
    }

    return list.toArray(new DimensionRelationshipAndData[list.size()]);
  }

  /**
   * Create the clustering
   *
   * @param owner
   *          the owning experiment set
   * @param clusters
   *          the discovered clusterings
   * @param source
   *          the source data structure
   * @param names
   *          the named elements used as basic for clustering (might be
   *          less than in {@code source})
   * @return the result
   */
  abstract CCT _create(final IExperimentSet owner, final int[] clusters,
      final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names);

  /**
   * Compute the result.
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @return the result
   */
  @SuppressWarnings("unchecked")
  private final CCT __compute(final IExperimentSet data,
      final Logger logger) {
    final INamedElementSet names;
    final ArrayListView<? extends INamedElement> elements;
    final int size;
    final String what;
    final CCT result;
    IDistanceClusteringJobBuilder clusterBuilder;
    String[] categories;
    IExperimentSet shared;
    DistanceMatrix distances;
    int index;
    Future<DimensionRelationshipData[][]>[] fittingsFutures;
    DimensionRelationshipData[][][] fittings;
    DimensionRelationshipAndData[] attrs;
    int[] clusters;
    MemoryTextOutput textOut;
    IClusteringJob job;

    shared = OnlySharedInstances.INSTANCE.get(data, logger);
    names = this._getElementsToCluster(shared);

    if (names.getData().isEmpty()) {
      throw new IllegalArgumentException(//
          "There is not even one benchmark instance for which all experiments contain at least one run. Since there is no such instance, there is no basis for behavior-based clustering.");//$NON-NLS-1$
    }

    categories = this._getRunCategories(shared);
    Arrays.sort(categories);
    shared = null;

    if ((logger != null) && (logger.isLoggable(Level.FINE))) {
      what = ((" the set of " + names.getData().size()) + //$NON-NLS-1$
          ((names instanceof IInstanceSet)//
              ? " instances" //$NON-NLS-1$
              : ((names instanceof IExperimentSet)//
                  ? " experiments" //$NON-NLS-1$
                  : "? i am confused?")));//$NON-NLS-1$
      logger.finer("Beginning to cluster" + what + //$NON-NLS-1$
          " based on algorithm runtime behavior.");//$NON-NLS-1$
      if (logger.isLoggable(Level.FINER)) {
        logger.finer(//
            "First, we model the runtime behavior of each algorithm on each benchmark instance for each runtime/objective dimension pair.");//$NON-NLS-1$
      }
    } else {
      what = null;
    }

    attrs = _BehaviorClusterer.__getFittingAttributes(data);

    elements = names.getData();
    size = elements.size();
    fittingsFutures = new Future[size];
    for (index = size; (--index) >= 0;) {
      fittingsFutures[index] = Execute.parallel(new _ElementFittingsJob(
          this._getRunsPerElement(data, elements.get(index), categories),
          attrs, logger));
    }
    categories = null;
    attrs = null;
    fittings = new DimensionRelationshipData[size][][];
    Execute.join(fittingsFutures, fittings, 0, false);
    fittingsFutures = null;

    if ((logger != null) && (what != null)
        && (logger.isLoggable(Level.FINER))) {
      logger.finer(//
          "Modeling completed, now computing a distance matrix which represents how well models for one instance runs set can represent the data from another one.");//$NON-NLS-1$
    }

    distances = new _DistanceBuilder(fittings).call();
    fittings = null;

    if ((logger != null) && (what != null)
        && logger.isLoggable(Level.FINER)) {
      textOut = new MemoryTextOutput(512);
      textOut.append("Distance matrix computed, now we cluster"); //$NON-NLS-1$
      textOut.append(what);
      textOut.append(" based on this matrix");//$NON-NLS-1$
      if ((this.m_minClusters > 0) || (this.m_maxClusters > 0)) {
        textOut.append(" into [");//$NON-NLS-1$
        if (this.m_minClusters > 0) {
          textOut.append(this.m_minClusters);
        }
        textOut.append(',');
        if (this.m_maxClusters > 0) {
          textOut.append(this.m_maxClusters);
        }
        textOut.append(" clusters.");//$NON-NLS-1$
      } else {
        textOut.append('.');
      }
      logger.finer(textOut.toString());
      textOut = null;
    }

    clusterBuilder = DefaultClusterer.getDistanceInstance().use()//
        .setLogger(logger)//
        .setDistanceMatrix(distances);
    distances = null;
    if (this.m_minClusters > 0) {
      clusterBuilder.setMinClusters(this.m_minClusters);
    }
    if (this.m_maxClusters > 0) {
      clusterBuilder.setMaxClusters(this.m_maxClusters);
    }
    job = clusterBuilder.create();
    clusterBuilder = null;
    clusters = job.call().getClustersRef();
    job = null;

    if ((logger != null) && (what != null)
        && logger.isLoggable(Level.FINER)) {
      logger.finer(//
          "Clustering completed, now we group" + //$NON-NLS-1$
              what + " based on the discovered clusters.");//$NON-NLS-1$
    }

    result = this._create(data, clusters, this._getElementsToCluster(data),
        elements);
    clusters = null;
    if ((logger != null) && (what != null)
        && (logger.isLoggable(Level.FINE))) {
      logger.finer("Finished clustering" + what + //$NON-NLS-1$
          " based on algorithm performance fingerprints, obtained "//$NON-NLS-1$
          + result.getData().size() + " clusters.");//$NON-NLS-1$
    }

    return result;
  }

  /** {@inheritDoc} */
  @Override
  protected final CCT compute(final IExperimentSet data,
      final Logger logger) {
    final CCT result;
    result = this.__compute(data, logger);
    MemoryUtils.fullGC();
    return result;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(final Object o) {
    _BehaviorClusterer<CCT> other;
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o.getClass() == this.getClass()) {
      other = ((_BehaviorClusterer<CCT>) o);
      return ((this.m_minClusters == other.m_minClusters)//
          && (this.m_maxClusters == other.m_maxClusters));
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  protected int calcHashCode() {
    return HashUtils.combineHashes(HashUtils.hashCode(this.m_maxClusters),
        HashUtils.hashCode(this.m_minClusters));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final MemoryTextOutput textOut;

    if ((this.m_minClusters <= 0) && (this.m_maxClusters <= 0)) {
      return ""; //$NON-NLS-1$
    }

    textOut = new MemoryTextOutput();
    textOut.append("into [");//$NON-NLS-1$
    if (this.m_minClusters > 0) {
      textOut.append(this.m_minClusters);
    }
    textOut.append(',');
    if (this.m_maxClusters > 0) {
      textOut.append(this.m_maxClusters);
    }
    textOut.append("] groups");//$NON-NLS-1$
    return textOut.toString();
  }
}
