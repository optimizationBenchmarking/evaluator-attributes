package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.ml.classification.impl.multi.MultiClassifierTrainer;
import org.optimizationBenchmarking.utils.ml.classification.impl.quality.MCC;
import org.optimizationBenchmarking.utils.ml.classification.spec.ClassifiedSample;
import org.optimizationBenchmarking.utils.ml.classification.spec.EFeatureType;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierQualityMeasure;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierTrainingJob;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;

/**
 * the base class for property-based behavior clustering
 *
 * @param <ET>
 *          the element type
 */
abstract class _PropertyBehaviorClusterer<ET extends INamedElement>
    extends Attribute<IExperimentSet, IClustering> {

  /** the MCC classifier measure */
  static final IClassifierQualityMeasure<?> CLASSIFIER_QUALITY_MEASURE = MCC.INSTANCE;

  /** the behavior clusterer */
  final Attribute<IExperimentSet, ? extends IClustering> m_behaviorClusterer;

  /**
   * create the clusterer
   *
   * @param behaviorClusterer
   *          the behavior clusterer
   */
  _PropertyBehaviorClusterer(
      final Attribute<IExperimentSet, ? extends IClustering> behaviorClusterer) {
    super(EAttributeType.PERMANENTLY_STORED);
    if (behaviorClusterer == null) {
      throw new IllegalArgumentException(
          "Behavior clusterer cannot be null."); //$NON-NLS-1$
    }
    this.m_behaviorClusterer = behaviorClusterer;
  }

  /**
   * Get the elements to be clustered.
   *
   * @param cluster
   *          the cluster
   * @param data
   *          the data
   * @param elements
   *          the elements to classify
   */
  abstract void _getElementsToClassify(final ICluster cluster,
      IExperimentSet data, final LinkedHashSet<ET> elements);

  /**
   * Get the property set of the given data
   *
   * @param data
   *          the experiment set
   * @return the property set
   */
  abstract ArrayListView<? extends IProperty> _getProperties(
      final IExperimentSet data);

  /**
   * Get the properties of the element to classify.
   *
   * @param element
   *          the element
   * @return the properties
   */
  abstract IPropertySetting _getPropertySetting(final ET element);

  /** {@inheritDoc} */
  @Override
  protected final IClustering compute(final IExperimentSet data,
      final Logger logger) {
    final IClustering clustering;
    final ArrayListView<? extends ICluster> clusters;
    final IClustering result;
    final ArrayListView<? extends IProperty> properties;
    final EFeatureType[] featureTypes;
    final int propertySize, clusterSize;
    LinkedHashSet<ET> elements;
    ArrayList<ET> allElements;
    final IClassifier classifier;
    ClassifiedSample[] sampleArray;
    ArrayList<ClassifiedSample> samples;
    int index, clazz;
    EPrimitiveType primitiveType;
    IPropertySetting propertySetting;
    double[] values;
    IClassifierTrainingJob job;
    DataSelection[] selections;
    DataSelection selection;

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info(
          "Beginning the process of behavior modeling, clustering of models, and applying classification to map features to clusters."); //$NON-NLS-1$
    }

    clustering = this.m_behaviorClusterer.get(data, logger);
    clusters = clustering.getData();

    // If there are not enough clusters, we don't need to classify them
    // anyway.
    if ((clusterSize = clusters.size()) <= 1) {
      if ((logger != null) && (logger.isLoggable(Level.INFO))) {
        logger.info(
            "Found only a single behavior cluster, so we can skip classification and just return the single cluster."); //$NON-NLS-1$
      }
      return clustering;
    }

    // If there are not enough properties, we cannot classify anyway.
    properties = this._getProperties(data);
    if ((properties == null)
        || ((propertySize = properties.size()) <= 0)) {
      if ((logger != null) && (logger.isLoggable(Level.INFO))) {
        logger.info(
            "There are no properties that can be used for classification, so we return the behavior clusters."); //$NON-NLS-1$
      }
      return clustering;
    }

    // First we build the feature types.
    featureTypes = new EFeatureType[propertySize];
    index = (-1);
    for (final IProperty property : properties) {
      primitiveType = property.getPrimitiveType();
      featureTypes[++index] = //
      ((primitiveType == EPrimitiveType.BOOLEAN) ? EFeatureType.BOOLEAN
          : (((primitiveType != null) && (primitiveType.isNumber())
              ? EFeatureType.NUMERICAL : EFeatureType.NOMINAL)));
    }

    // We can now translate the clusters to classified samples: Each
    // element (i.e., experiment or instance) has a set of features and
    // belongs to a cluster. The cluster index becomes the class id.
    samples = new ArrayList<>();
    elements = new LinkedHashSet<>();
    allElements = new ArrayList<>();
    index = (-1);
    for (final ICluster cluster : clusters) {
      ++index;
      elements.clear();
      this._getElementsToClassify(cluster, data, elements);
      allElements.addAll(elements);
      for (final ET element : elements) {
        propertySetting = this._getPropertySetting(element);
        values = new double[propertySize];
        _PropertyBehaviorClusterer.__fillIn(properties, propertySetting,
            featureTypes, values);
        samples.add(new ClassifiedSample(index, values));
      }
    }

    result = null;

    // Now we can learn the relationship between element features and
    // clusters.
    sampleArray = samples.toArray(new ClassifiedSample[samples.size()]);
    samples = null;
    job = MultiClassifierTrainer.getInstance().use()//
        .setLogger(logger)//
        .setFeatureTypes(featureTypes)//
        .setQualityMeasure(
            _PropertyBehaviorClusterer.CLASSIFIER_QUALITY_MEASURE)//
        .setTrainingSamples(sampleArray)//
        .create();
    elements = null;
    classifier = job.call().getClassifier();

    // Now that this is done, we can use the classifier to classify the
    // instances or experiments.
    selections = new DataSelection[clusterSize];
    index = (-1);
    for (final ET element : allElements) {
      clazz = classifier.classify(sampleArray[++index].featureValues);
      if ((selection = selections[clazz]) == null) {
        selections[clazz] = selection = new DataSelection(data);
      }
      if (element instanceof IExperiment) {
        selection.addExperiment((IExperiment) element);
      } else {
        if (element instanceof IInstance) {
          selection.addInstance((IInstance) element);
        } else {
          throw new IllegalStateException(
              "Cannot process element " + element); //$NON-NLS-1$
        }
      }
    }
    allElements = null;

    MemoryUtils.fullGC();
    return result;
  }

  /**
   * transform a property setting into a feature vector, i.e., feature
   * values to {@code double}s
   *
   * @param properties
   *          the properties
   * @param setting
   *          the property setting
   * @param featureTypes
   *          the feature types
   * @param dest
   *          the destination array
   */
  private static final void __fillIn(
      final ArrayListView<? extends IProperty> properties,
      final IPropertySetting setting, final EFeatureType[] featureTypes,
      final double[] dest) {
    int index, valueIndex;
    Object value;
    IPropertyValue propertyValue;

    index = (-1);
    main: for (final IProperty property : properties) {
      value = setting.get(property);
      ++index;
      switch (featureTypes[index]) {
        case NUMERICAL: {
          if (value instanceof Number) {
            dest[index] = ((Number) value).doubleValue();
            continue main;
          }
          throw new IllegalStateException(//
              "Numerical property '" + property + //$NON-NLS-1$
                  "' with value '" + value + //$NON-NLS-1$
                  "'. Really?");//$NON-NLS-1$
        }
        case BOOLEAN: {
          if (value instanceof Boolean) {
            dest[index] = (((Boolean) value).booleanValue() ? 1d : 0d);
            continue main;
          }
          throw new IllegalStateException(//
              "Boolean property '" + property + //$NON-NLS-1$
                  "' with value '" + value + //$NON-NLS-1$
                  "'. Really?");//$NON-NLS-1$
        }
        case NOMINAL: {
          propertyValue = property.findValue(value);
          if (propertyValue == null) {
            throw new IllegalStateException((//
                "Cannot find value '" + value + //$NON-NLS-1$
                    "' of nominal property '" + property + //$NON-NLS-1$
                    '\'')
                + '.');
          }
          if ((propertyValue instanceof IParameterValue) && //
              (((IParameterValue) propertyValue).isUnspecified())) {
            dest[index] = property.getData().size();
            continue main;
          }
          valueIndex = property.getData().indexOf(propertyValue);
          if (valueIndex >= 0) {
            dest[index] = valueIndex;
            continue main;
          }

          throw new IllegalStateException((//
              "Cannot find value '" + value + //$NON-NLS-1$
                  "' of property value '" + propertyValue + //$NON-NLS-1$
                  "' of nominal property '" + property + //$NON-NLS-1$
                  '\'')
              + '.');
        }
        default: {
          throw new IllegalArgumentException((//
              "Unknown type '" + featureTypes[index]//$NON-NLS-1$
                  + "' for property '" + property //$NON-NLS-1$
                  + '\'')
              + '.');
        }
      }
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public final boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o.getClass() == this.getClass()) {
      return Compare.equals(this.m_behaviorClusterer,
          ((_PropertyBehaviorClusterer) o).m_behaviorClusterer);
    }
    return true;
  }
}
