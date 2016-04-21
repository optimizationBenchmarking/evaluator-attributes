package test.junit.org.optimizationBenchmarking.evaluator.attributes.modeling;

import java.util.Random;

import org.junit.Assert;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationship;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.math.MathUtils;
import org.optimizationBenchmarking.utils.ml.fitting.impl.DefaultFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.multi.MultiFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFunctionFitter;

import shared.junit.org.optimizationBenchmarking.evaluator.attributes.InstanceRunsAttributeTest;

/** The test for modeling of dimension relationships */
public class DimensionRelationshipTest extends
    InstanceRunsAttributeTest<IFittingResult, DimensionRelationship> {

  /** create */
  public DimensionRelationshipTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected int getMaxAttributeComputationsPerDataset() {
    return 2;
  }

  /** {@inheritDoc} */
  @Override
  protected void checkResult(final DimensionRelationship attribute,
      final IExperimentSet experimentSet, final IInstanceRuns input,
      final IFittingResult result) {
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getFittedFunction());
    Assert.assertNotNull(result.getFittedParametersRef());
    Assert.assertTrue(MathUtils.isFinite(result.getQuality()));
    Assert.assertTrue(result.getQuality() > 0d);
  }

  /**
   * Check if dimension relationship modeling can be used
   *
   * @return {@code true} if it can be used, {@code false} otherwise
   */
  public static final boolean canUse() {
    final ArrayListView<IFunctionFitter> fitters;
    boolean result;

    if (MultiFunctionFitter.getInstance().canUse()) {
      fitters = DefaultFunctionFitter.getAllInstance();
      if ((fitters != null) && (fitters.size() > 0)) {
        result = false;
        for (final IFunctionFitter fitter : fitters) {
          if (!(fitter.canUse())) {
            return false;
          }
          result = true;
        }
        return result;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean canUseAttribute() {
    return DimensionRelationshipTest.canUse();
  }

  /** {@inheritDoc} */
  @Override
  protected DimensionRelationship getAttribute(
      final IExperimentSet experimentSet, final IInstanceRuns data) {
    final ArrayListView<? extends IDimension> dims;
    final int size;
    final Random random;
    IDimension dimA, dimB, temp;

    dims = experimentSet.getDimensions().getData();
    Assert.assertNotNull(dims);
    size = dims.size();
    Assert.assertTrue(size > 1);

    random = new Random();
    dimA = dims.get(random.nextInt(size));
    do {
      dimB = dims.get(random.nextInt(size));
    } while (dimA == dimB);

    if (dimA.getDimensionType().isSolutionQualityMeasure() && //
        dimB.getDimensionType().isTimeMeasure()) {
      temp = dimA;
      dimA = dimB;
      dimB = temp;
    }

    return new DimensionRelationship(dimA, dimB);
  }
}
