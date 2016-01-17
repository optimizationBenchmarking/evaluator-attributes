package test.junit.org.optimizationBenchmarking.experimentation.attributes.clustering;

import java.util.ArrayList;
import java.util.Random;

import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.EGroupingMode;
import org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.PropertyValueGrouper;
import org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.PropertyValueSelector;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.utils.math.NumericalTypes;

/** Test the clustering by feature group */
public class FeatureValueGroupingTest extends ClusteringTest {

  /** create */
  public FeatureValueGroupingTest() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  protected Attribute<? super IExperimentSet, ? extends IClustering> getAttribute(
      final IExperimentSet experimentSet, final IExperimentSet data) {
    final Random random;
    final EGroupingMode[] modes;
    final EGroupingMode mode;
    final int min, max;
    final double numParam;
    final boolean pickNum;
    final ArrayList<IFeature> numerical, plain, pick;

    random = new Random();

    numerical = new ArrayList<>();
    plain = new ArrayList<>();
    outer: for (final IFeature param : data.getFeatures().getData()) {

      for (final IFeatureValue value : param.getData()) {
        if (!(value instanceof Number)) {
          plain.add(param);
          continue outer;
        }
      }
      numerical.add(param);
    }

    pickNum = ((numerical.size() > 0) && (random.nextBoolean()));
    if (pickNum) {
      modes = EGroupingMode.values();
      mode = modes[random.nextInt(modes.length)];
    } else {
      mode = EGroupingMode.DISTINCT;
    }
    pick = (pickNum ? numerical : plain);
    min = 1 + random.nextInt(3);
    max = min + 1 + random.nextInt(10);
    numParam = (2d + random.nextInt(10));

    return new PropertyValueSelector(//
        pick.get(random.nextInt(pick.size())).getName(), //
        new PropertyValueGrouper(mode, NumericalTypes.valueOf(numParam),
            min, max));
  }
}
