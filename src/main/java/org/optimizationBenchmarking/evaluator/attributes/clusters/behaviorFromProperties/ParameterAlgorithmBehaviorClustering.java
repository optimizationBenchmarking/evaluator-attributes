package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.document.spec.ELabelType;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The property algorithm behavior clustering. */
public final class ParameterAlgorithmBehaviorClustering extends
    _PropertyBehaviorClustering<ParameterAlgorithmBehaviorCluster> {

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
  ParameterAlgorithmBehaviorClustering(final IExperimentSet owner,
      final IClustering behavior, final IClassifier classifier,
      final DataSelection[] selections) {
    super(owner, behavior, classifier, selections);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWords(
        "algorithm behavior-based clustering via parameter-based classification", //$NON-NLS-1$
        textOut);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPathComponentSuggestion() {
    return "parameterBased" + this.getData().size() //$NON-NLS-1$
        + super.getPathComponentSuggestion();
  }

  /** {@inheritDoc} */
  @Override
  final ParameterAlgorithmBehaviorCluster _create(final String name,
      final DataSelection selection) {
    return new ParameterAlgorithmBehaviorCluster(this, name, selection);
  }

  /** {@inheritDoc} */
  @Override
  final _ClassifierRenderer _getClassifierRenderer() {
    return new _ParameterClassifierRenderer(this);
  }

  /** {@inheritDoc} */
  @Override
  public final void printLongDescription(final ISectionBody body) {
    final ILabel behavior, classifier;

    behavior = body.createLabel(ELabelType.SECTION);
    classifier = body.createLabel(ELabelType.SECTION);

    this._printHeader_A(body);
    body.append(" algorithm setups that have similar behavior (see "); //$NON-NLS-1$
    body.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND, behavior);
    body.append(
        "). We then apply supervised Machine Learning, i.e., classification, to discover which algorithm setup parameters seem to be the reason for why a certain algorithm setup lands in one behavior cluster. The learned classifier is then used to induce the final set of clusters. This is described in "); //$NON-NLS-1$
    body.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND, classifier);
    body.append('.');

    this._printBehaviorLongDescription(body, behavior);

    try (final ISection section = body.section(classifier)) {
      try (final IComplexText title = section.title()) {
        title.append("Algorithm Parameter Setups Related to "); //$NON-NLS-1$
        this.m_behavior.printShortName(title, ETextCase.IN_TITLE);
      }
      try (final ISectionBody subBody = section.body()) {
        subBody.append(
            "We now use supervised Machine Learning, i.e., classification, to discover the relationship of the algorithm setups to the behavior clusters discovered in "); //$NON-NLS-1$
        subBody.reference(ETextCase.IN_SENTENCE, ESequenceMode.COMMA,
            behavior);
        subBody.append(
            ". In other words, we use the single experiment setups as vectors to be classified. Their features are the algorithm parameters, namely "); //$NON-NLS-1$
        this._printClassification(subBody);
        subBody.append(
            "The classifier is then applied classify all algorithm setups into clusters (again): "); //$NON-NLS-1$
        ClusterUtils.listClusters(this, subBody);
      }
    }
  }
}
