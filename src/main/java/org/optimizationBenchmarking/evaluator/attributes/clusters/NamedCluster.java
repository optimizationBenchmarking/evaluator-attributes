package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.ShadowExperimentSet;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IMathName;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.AlphabeticNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A basic, named cluster
 *
 * @param <OT>
 *          the clustering type
 */
public class NamedCluster<OT extends IClustering>
    extends ShadowExperimentSet<OT> implements ICluster {

  /** the name of the cluster */
  private final String m_name;

  /**
   * create behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param name
   *          the name of the cluster
   * @param selection
   *          the data selection
   */
  protected NamedCluster(final OT owner, final String name,
      final DataSelection selection) {
    super(owner, selection);
    this.m_name = name;
  }

  /**
   * create behavior cluster
   *
   * @param owner
   *          the owning element set
   * @param nameIndex
   *          the 0-based name index to be transformed to a name string
   * @param selection
   *          the data selection
   */
  protected NamedCluster(final OT owner, final int nameIndex,
      final DataSelection selection) {
    this(owner, AlphabeticNumberAppender.UPPER_CASE_INSTANCE
        .toString(nameIndex, ETextCase.IN_SENTENCE), selection);
  }

  /**
   * Get the name of this cluster
   *
   * @return the name of this cluster
   */
  public String getName() {
    return this.m_name;
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord(this.getName(), textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord(this.getName(), textOut);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return textCase.appendWord(this.getName(), textOut);
  }

  /** {@inheritDoc} */
  @Override
  public String getPathComponentSuggestion() {
    return this.getName();
  }

  /** {@inheritDoc} */
  @Override
  public void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    out.append(this.getName());
  }

  /** {@inheritDoc} */
  @Override
  public void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    try (final IMathName name = out.name()) {
      name.append(this.getName());
    }
  }
}
