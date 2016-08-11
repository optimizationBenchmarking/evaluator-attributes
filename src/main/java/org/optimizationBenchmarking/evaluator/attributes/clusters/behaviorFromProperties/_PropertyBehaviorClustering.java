package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusteringBase;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.clusters.NamedCluster;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.impl.EListSequenceMode;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentSequenceable;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * The base class for property-based behavior clustering
 *
 * @param <CT>
 *          the cluster type
 */
abstract class _PropertyBehaviorClustering<CT extends NamedCluster<?>>
    extends ClusteringBase<IExperimentSet, CT> {

  /** the clusters */
  private final ArrayListView<CT> m_clusters;

  /** the behavior clustering underlying this clustering */
  final IClustering m_behavior;
  /** the obtained classifier */
  private final IClassifier m_classifier;

  /**
   * create the behavior-based clustering
   *
   * @param owner
   *          the owner
   * @param behavior
   *          the behavior-based clustering underlying this clustering
   * @param classifier
   *          the classifier
   * @param selections
   *          the section
   */
  @SuppressWarnings("rawtypes")
  _PropertyBehaviorClustering(final IExperimentSet owner,
      final IClustering behavior, final IClassifier classifier,
      final DataSelection[] selections) {
    super(owner);

    ArrayList<CT> list;
    int index;

    if (behavior == null) {
      throw new IllegalArgumentException(
          "Behavior clustering cannot be null."); //$NON-NLS-1$
    }
    if (classifier == null) {
      throw new IllegalArgumentException("Classifier cannot be null."); //$NON-NLS-1$
    }
    this.m_behavior = behavior;
    this.m_classifier = classifier;

    list = new ArrayList<>();
    index = (-1);
    for (final DataSelection selection : selections) {
      ++index;
      if (selection != null) {
        list.add(this._create(
            (((NamedCluster) (behavior.getData().get(index))).getName()),
            selection));
      }
    }
    this.m_clusters = ArrayListView.collectionToView(list, false);
    list = null;
  }

  /**
   * Create the cluster
   *
   * @param name
   *          the name
   * @param selection
   *          the selection
   * @return the cluster
   */
  abstract CT _create(final String name, final DataSelection selection);

  /**
   * Obtain a classifier renderer
   *
   * @return the renderer
   */
  abstract _ClassifierRenderer _getClassifierRenderer();

  /** {@inheritDoc} */
  @Override
  public final ArrayListView<CT> getData() {
    return this.m_clusters;
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    textOut.append("First, "); //$NON-NLS-1$
    this.m_behavior.printDescription(textOut, ETextCase.IN_SENTENCE);
    return ETextCase.AT_SENTENCE_START;
  }

  /**
   * Print the shared body data
   *
   * @param body
   *          the body
   * @param behavior
   *          the label for the algorithm behavior
   */
  final void _printBehaviorLongDescription(final ISectionBody body,
      final ILabel behavior) {
    try (final ISection section = body.section(behavior)) {
      try (final IComplexText title = section.title()) {
        this.m_behavior.printLongName(title, ETextCase.AT_TITLE_START);
      }
      try (final ISectionBody subBody = section.body()) {
        this.m_behavior.printLongDescription(subBody);
      }
    }
  }

  /**
   * print the information of the classifier.
   *
   * @param body
   *          the body
   */
  final void _printClassification(final ISectionBody body) {
    final int trainerSize;

    body.append(" and the known classes are the IDs of the "); //$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(
        this.m_behavior.getData().size(), ETextCase.IN_SENTENCE, body);
    body.append(" behavior clusters."); //$NON-NLS-1$
    body.appendLineBreak();

    switch (trainerSize = _PropertyBehaviorClusterer.TRAINERS.size()) {
      case 0: {
        body.append("No classifier trainer was available.");//$NON-NLS-1$
        break;
      }
      case 1: {
        body.append(
            "Only one classifier training engine could be used, namely ");//$NON-NLS-1$
        _PropertyBehaviorClusterer.TRAINERS.get(0).printDescription(body,
            ETextCase.IN_SENTENCE);
        body.append(" and it constructed a ");//$NON-NLS-1$
        this.m_classifier.printDescription(body, ETextCase.IN_SENTENCE);
        body.append('.');
        break;
      }
      default: {
        body.append("We tested ");//$NON-NLS-1$
        InTextNumberAppender.INSTANCE.appendTo(trainerSize,
            ETextCase.IN_SENTENCE, body);
        body.append(" different classification methods, namely"); //$NON-NLS-1$
        EListSequenceMode.ENUMERATION.appendSequence(ETextCase.IN_SENTENCE,
            SemanticComponentSequenceable.wrap(
                _PropertyBehaviorClusterer.TRAINERS, false, false, true),
            body, ESequenceMode.AND);
        body.append(
            "If sufficient samples were avaible, cross-validation was used to choose classifiers with good generalization ability first. We compared their result based on the "); //$NON-NLS-1$
        _PropertyBehaviorClusterer.CLASSIFIER_QUALITY_MEASURE
            .printDescription(body, ETextCase.IN_SENTENCE);
        body.append(
            ". The classifier that performed best on the whole data set in terms of the "); //$NON-NLS-1$
        _PropertyBehaviorClusterer.CLASSIFIER_QUALITY_MEASURE
            .printShortName(body, ETextCase.IN_SENTENCE);
        body.append(" was a ");//$NON-NLS-1$
        this.m_classifier.printDescription(body, ETextCase.IN_SENTENCE);
        body.append('.');
      }
    }

    body.append(' ');
    this.m_classifier.render(this._getClassifierRenderer(), body);
    body.appendLineBreak();

  }

  /**
   * The first heaver printing step
   *
   * @param body
   *          the target body
   */
  final void _printHeader_A(final ISectionBody body) {
    body.append("The "); //$NON-NLS-1$
    this.printLongName(body, ETextCase.IN_SENTENCE);
    body.append(
        " consists of two steps: First, we model the runtime behavior of the "); //$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(
        this.getOwner().getData().size(), ETextCase.IN_SENTENCE, body);
    body.append(" algorithm setups on the  "); //$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(
        this.getOwner().getInstances().getData().size(),
        ETextCase.IN_SENTENCE, body);
    body.append(
        " benchmark instances and apply unsupervised Machine Learning (i.e., clustering) to discover clusters of"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String getPathComponentSuggestion() {
    return this.m_behavior.getPathComponentSuggestion()
        + this.m_classifier.getPathComponentSuggestion();
  }
}
