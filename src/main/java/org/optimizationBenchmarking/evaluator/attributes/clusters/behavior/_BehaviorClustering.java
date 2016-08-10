package org.optimizationBenchmarking.evaluator.attributes.clusters.behavior;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusteringBase;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipModels;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.EMathComparison;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.IText;
import org.optimizationBenchmarking.utils.ml.clustering.impl.DefaultClusterer;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A behavior-based clustering uses algorithm behavior to divide algorithms
 * or instances into groups.
 *
 * @param <CT>
 *          the cluster type
 */
abstract class _BehaviorClustering<CT extends NamedCluster<?>>
    extends ClusteringBase<IExperimentSet, CT> {

  /** the data */
  private final ArrayListView<CT> m_data;

  /**
   * create the behavior-based clustering
   *
   * @param owner
   *          the owner
   * @param clusters
   *          the matrix of clusters
   * @param source
   *          the source where to draw the named elements from
   * @param names
   *          the names
   */
  _BehaviorClustering(final IExperimentSet owner, final int[] clusters,
      final INamedElementSet source,
      final ArrayListView<? extends INamedElement> names) {
    super(owner);
    final ArrayList<CT> list;
    DataSelection selection;
    int clusterIndex, find;
    INamedElement ne;
    String name;
    int total;

    list = new ArrayList<>(20);

    total = 0;
    for (clusterIndex = 0;; ++clusterIndex) {
      selection = null;
      for (find = clusters.length; (--find) >= 0;) {
        if (clusters[find] == clusterIndex) {
          if (selection == null) {
            selection = new DataSelection(owner);
          }
          name = names.get(find).getName();
          ne = source.find(name);
          if (ne == null) {
            throw new IllegalStateException("Cannot find element of name '" //$NON-NLS-1$
                + name + "' in " + names); //$NON-NLS-1$
          }
          if (ne instanceof IInstance) {
            selection.addInstance((IInstance) ne);
            total++;
          } else {
            if (ne instanceof IExperiment) {
              selection.addExperiment((IExperiment) ne);
              total++;
            } else {
              throw new IllegalStateException(//
                  "Element of name '" + name + //$NON-NLS-1$
                      "' is neither an experiment nor a benchmark instance.");//$NON-NLS-1$
            }
          }
        }
      }

      if (selection == null) {
        break;
      }
      list.add(this._create(list.size(), selection));
    }

    if (total != names.size()) {
      throw new IllegalStateException(//
          "There are " + names.size() + //$NON-NLS-1$
              " elements that should be in clusters, but " + total + //$NON-NLS-1$
              " have actually been assigned.");//$NON-NLS-1$
    }

    this.m_data = ArrayListView.collectionToView(list);
  }

  /**
   * create a new cluster
   *
   * @param nameIndex
   *          the 0-based name index to be transformed to a name string
   * @param selection
   *          the selection
   * @return the cluster
   */
  abstract CT _create(final int nameIndex, final DataSelection selection);

  /** {@inheritDoc} */
  @Override
  public final ArrayListView<CT> getData() {
    return this.m_data;
  }

  /**
   * The text describing how distances are aggregated
   *
   * @param textOut
   *          the text output
   */
  abstract void _distanceAggregationText(final ITextOutput textOut);

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    final ArrayList<IDimension> time, objective;
    final int timeSize, objectiveSize;
    IComplexText complex;

    time = new ArrayList<>();
    objective = new ArrayList<>();
    _BehaviorClusterer._findDimensions(this.getOwner(), time, objective);

    timeSize = time.size();
    textOut.append("We therefore first model the relationships of the "); //$NON-NLS-1$
    if (timeSize > 1) {
      InTextNumberAppender.INSTANCE.appendTo(timeSize,
          ETextCase.IN_SENTENCE, textOut);
      textOut.append(" time dimensions "); //$NON-NLS-1$
      ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE, time,
          textOut);
    } else {
      textOut.append(" time dimension "); //$NON-NLS-1$
      time.get(0).printShortName(textOut, ETextCase.IN_SENTENCE);
    }

    objectiveSize = objective.size();
    textOut.append(" to the "); //$NON-NLS-1$
    if (objectiveSize > 1) {
      InTextNumberAppender.INSTANCE.appendTo(objectiveSize,
          ETextCase.IN_SENTENCE, textOut);
      textOut.append(" solution quality dimensions "); //$NON-NLS-1$
      ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE, objective,
          textOut);
    } else {
      textOut.append(" solution quality dimension "); //$NON-NLS-1$
      objective.get(0).printShortName(textOut, ETextCase.IN_SENTENCE);
    }

    textOut.append(
        " as simple mathematical functions. This is done for each algorithm setup on each benchmark instance based on all data (runs) available for a given setup on a specific instance."); //$NON-NLS-1$
    textOut.appendLineBreak();
    textOut.append("For this purpose, "); //$NON-NLS-1$
    DimensionRelationshipModels.printModelingDescription(textOut,
        ETextCase.IN_SENTENCE, true, false);
    textOut.appendLineBreak();
    textOut.append(
        "We now cluster the data by using the obtained models and by appying "); //$NON-NLS-1$
    DefaultClusterer.getDistanceInstance().printDescription(textOut,
        ETextCase.IN_SENTENCE);
    textOut.append(
        " Since we apply the clustering for different target cluster numbers, we this way can also automatically detect into how many groups the data should be split."); //$NON-NLS-1$

    textOut.appendLineBreak();

    textOut.append(
        "For clustering, we need a distance measure (also called dissimilarity measure), i.e., we need to know how different the fitted models are. The distance measure between two fitted models "); //$NON-NLS-1$
    if (textOut instanceof IComplexText) {
      complex = ((IComplexText) textOut);
      try (final IMath math = complex.inlineMath()) {
        _BehaviorClustering.__printModel(math, 0);
      }
      textOut.append(" and "); //$NON-NLS-1$
      try (final IMath math = complex.inlineMath()) {
        _BehaviorClustering.__printModel(math, 1);
      }
      textOut.append(
          " is the quality that a fitted model for an algorithm setup would have if it would represents the measured points used to build the model for the other setup, i.e.,"); //$NON-NLS-1$

      if (complex instanceof ISectionBody) {
        try (final IMath math = ((ISectionBody) complex).equation(null)) {
          _BehaviorClustering.__printDistance(math);
        }
      } else {
        complex.append(' ');
        try (final IMath math = complex.inlineMath()) {
          _BehaviorClustering.__printDistance(math);
        }
        complex.append('.');
        complex.append(' ');
      }
    } else {
      complex = null;
      textOut.append(
          " f and g is the quality that a fitted model for an algorithm setup would have if it would represents the measured points from the other setup, i.e., min(quality(f, pointsUsedToBuild(g)), quality(g, pointsUsedToBuild(f)). "); //$NON-NLS-1$
    }

    if (complex != null) {
      textOut.append("If one model ");//$NON-NLS-1$
      try (final IMath math = complex.inlineMath()) {
        _BehaviorClustering.__printModel(math, 0);
      }
      textOut.append(
          " can better represent the points (data from the runs) ");//$NON-NLS-1$
      try (final IMath math = complex.inlineMath()) {
        _BehaviorClustering.__printDataUsedForModel(math, 1);
      }
      textOut.append(" used to fit another model ");//$NON-NLS-1$
      try (final IMath math = complex.inlineMath()) {
        _BehaviorClustering.__printModel(math, 1);
      }
      textOut.append(" than that other model, i.e., if ");//$NON-NLS-1$
      try (final IMath math = complex.inlineMath()) {
        try (final IMath compare = math
            .compare(EMathComparison.LESS_OR_EQUAL)) {
          _BehaviorClustering.__printQuality(compare, 0, 1);
          _BehaviorClustering.__printQuality(compare, 1, 1);
        }
      }
    } else {
      textOut.append(
          "If one model can better represent the points of a different data set than the model originally trained on that set");//$NON-NLS-1$
    }
    textOut.append(
        " (which is unlikely to occur), then the distance of the models is considered to be zero, i.e., we assume that the two algorithms have the same behavior.");//$NON-NLS-1$
    this._distanceAggregationText(textOut);

    return ETextCase.AT_SENTENCE_START;
  }

  /**
   * print a given model
   *
   * @param math
   *          the destination math component
   * @param model
   *          the zero-based model index
   */
  private static final void __printModel(final IMath math,
      final int model) {
    try (final IText text = math.name()) {
      text.append((char) ('f' + model));
    }
  }

  /**
   * print the data used to build a given model
   *
   * @param math
   *          the destination math component
   * @param model
   *          the zero-based model index
   */
  private static final void __printDataUsedForModel(final IMath math,
      final int model) {
    try (
        final IMath dataUsedToBuild = math.nAryFunction("dataUsedToFit", 1, //$NON-NLS-1$
            1)) {
      try (final IMath braces = dataUsedToBuild.inBraces()) {
        _BehaviorClustering.__printModel(braces, model);
      }
    }
  }

  /**
   * print the quality of a given model
   *
   * @param math
   *          the destination math component
   * @param qualityModel
   *          the model for which we obtain the quality
   * @param dataModel
   *          the model whose data we use
   */
  private static final void __printQuality(final IMath math,
      final int qualityModel, final int dataModel) {
    try (final IMath quality = math.nAryFunction("quality", 2, 2)) { //$NON-NLS-1$
      _BehaviorClustering.__printModel(quality, qualityModel);
      _BehaviorClustering.__printDataUsedForModel(quality, dataModel);
    }
  }

  /**
   * print the real normalized quality of a given model on a given data set
   *
   * @param math
   *          the destination math component
   * @param qualityModel
   *          the model for which we obtain the quality
   * @param dataModel
   *          the model whose data we use
   */
  private static final void __printRealQuality(final IMath math,
      final int qualityModel, final int dataModel) {

    try (final IMath div = math.div()) {
      try (final IMath sub = math.sub()) {
        _BehaviorClustering.__printQuality(sub, qualityModel, dataModel);
        _BehaviorClustering.__printQuality(sub, dataModel, dataModel);
      }
      _BehaviorClustering.__printQuality(div, dataModel, dataModel);
    }
  }

  /**
   * print the distance
   *
   * @param math
   *          the math context
   */
  private static final void __printDistance(final IMath math) {
    try (final IMath min = math.min()) {
      _BehaviorClustering.__printRealQuality(min, 0, 1);
      _BehaviorClustering.__printRealQuality(min, 1, 0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void printLongDescription(final ISectionBody body) {
    super.printLongDescription(body);
    body.appendLineBreak();
    ClusterUtils.listClusters(this, body);
  }
}
