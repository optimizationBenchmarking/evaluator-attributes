package org.optimizationBenchmarking.evaluator.attributes.clusters.byInstance;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.ShadowExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** an instance cluster */
final class _InstanceCluster extends ShadowExperimentSet<_InstanceGroups>
    implements ICluster {

  /**
   * Create an instance cluster
   *
   * @param owner
   *          the owner
   * @param selection
   *          the selection
   */
  _InstanceCluster(final _InstanceGroups owner,
      final DataSelection selection) {
    super(owner, selection);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPathComponentSuggestion() {
    final ArrayListView<? extends IExperiment> experiments;
    final ArrayListView<? extends IInstanceRuns> runs;
    final IInstance instance;

    findInstance: {
      experiments = this.getData();
      if (!(experiments.isEmpty())) {
        runs = experiments.get(0).getData();
        if (!(runs.isEmpty())) {
          instance = runs.get(0).getInstance();
          break findInstance;
        }
      }
      instance = this.getInstances().getData().get(0);
    }

    return instance.getPathComponentSuggestion(); // $NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    final ETextCase use;
    use = textCase.appendWord("instance", textOut); //$NON-NLS-1$
    textOut.append(' ');
    return this.__getInstance().printShortName(textOut, use);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    final ETextCase use;
    use = textCase.appendWord("instance", textOut); //$NON-NLS-1$
    textOut.append(' ');
    return this.__getInstance().printLongName(textOut, use);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    final ETextCase use;
    use = textCase.appendWord("run sets belonging to instance", textOut); //$NON-NLS-1$
    textOut.append(' ');
    return SemanticComponentUtils.printLongAndShortNameIfDifferent(
        this.__getInstance(), textOut, use);
  }

  /**
   * get the instance
   *
   * @return the instance
   */
  private final IInstance __getInstance() {
    return this.getInstances().getData().get(0);
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    this.__getInstance().mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    this.__getInstance().mathRender(out, renderer);
  }
}
