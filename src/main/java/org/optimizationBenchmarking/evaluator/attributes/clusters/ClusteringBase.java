package org.optimizationBenchmarking.evaluator.attributes.clusters;

import org.optimizationBenchmarking.evaluator.data.spec.DataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A base class for clustering.
 *
 * @param <OT>
 *          the owner type
 * @param <CT>
 *          the cluster type
 */
public abstract class ClusteringBase<OT extends IDataElement, CT extends ICluster>
    extends DataElement implements IClustering {

  /** the owner */
  private final OT m_owner;

  /**
   * Create the clustering base
   *
   * @param owner
   *          the owner
   */
  protected ClusteringBase(final OT owner) {
    super();

    if (owner == null) {
      throw new IllegalArgumentException("Owner of cluster type " //$NON-NLS-1$
          + this.getClass().getSimpleName() + " cannot be null.");//$NON-NLS-1$
    }
    this.m_owner = owner;
  }

  /** {@inheritDoc} */
  @Override
  public final OT getOwner() {
    return this.m_owner;
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return this.printShortName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public abstract ArrayListView<CT> getData();

  /** {@inheritDoc} */
  @Override
  public void printLongDescription(final ISectionBody body) {
    this.printDescription(body, ETextCase.AT_SENTENCE_START);
  }
}
