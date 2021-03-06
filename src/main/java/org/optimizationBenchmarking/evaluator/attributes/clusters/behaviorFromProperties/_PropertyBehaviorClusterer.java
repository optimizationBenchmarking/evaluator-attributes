package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClusterUtils;
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
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.ml.classification.impl.DefaultClassifierTrainer;
import org.optimizationBenchmarking.utils.ml.classification.impl.multi.MultiClassifierTrainer;
import org.optimizationBenchmarking.utils.ml.classification.impl.quality.MCC;
import org.optimizationBenchmarking.utils.ml.classification.spec.ClassifiedSample;
import org.optimizationBenchmarking.utils.ml.classification.spec.EFeatureType;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifier;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierQualityMeasure;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierTrainer;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierTrainingJob;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.ITextable;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * the base class for property-based behavior clustering
 *
 * @param <ET>
 *          the element type
 */
abstract class _PropertyBehaviorClusterer<ET extends INamedElement>
    extends Attribute<IExperimentSet, IClustering> implements ITextable {

  /** the MCC classifier measure */
  static final IClassifierQualityMeasure<?> CLASSIFIER_QUALITY_MEASURE = MCC.INSTANCE;

  /** the used trainers */
  static final ArrayListView<IClassifierTrainer> TRAINERS = //
  DefaultClassifierTrainer.getAllInstance();

  /** the behavior clusterer */
  private final Attribute<IExperimentSet, ? extends IClustering> m_behaviorClusterer;

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

  /** {@inheritDoc} */
  @Override
  public void toText(final ITextOutput textOut) {
    textOut.append(' ');
    if (this.m_behaviorClusterer instanceof ITextable) {
      ((ITextable) (this.m_behaviorClusterer)).toText(textOut);
    } else {
      textOut.append(this.m_behaviorClusterer.toString());
    }
    textOut.append(' ');
    textOut.append('#');
    textOut.append(System.identityHashCode(this));
    textOut.append('/');
    textOut.append(this.hashCode());
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    final MemoryTextOutput textOut;

    textOut = new MemoryTextOutput();
    this.toText(textOut);
    return textOut.toString();
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
    double[] values;
    IClassifierTrainingJob job;
    DataSelection[] selections;
    DataSelection selection;
    String name;

    name = null;
    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      name = this.toString();
      logger.info(
          "Beginning the process of behavior modeling, clustering of models, and applying classification to map features to clusters using "//$NON-NLS-1$
              + name + " on dataset " + //$NON-NLS-1$
              ClusterUtils.dataIdentityString(data));
    }

    clustering = this.m_behaviorClusterer.get(data, logger);
    clusters = clustering.getData();

    // If there are not enough clusters, we don't need to classify them
    // anyway.
    if ((clusterSize = clusters.size()) <= 1) {
      if ((logger != null) && (logger.isLoggable(Level.INFO))) {
        if (name == null) {
          name = this.toString();
        }
        logger.info(
            "Found only a single behavior cluster, so we can skip classification and just return the single cluster using "//$NON-NLS-1$
                + name + " on dataset " + //$NON-NLS-1$
                ClusterUtils.dataIdentityString(data));
      }
      return clustering;
    }

    // If there are not enough properties, we cannot classify anyway.
    properties = this._getProperties(data);
    if ((properties == null)
        || ((propertySize = properties.size()) <= 0)) {
      if ((logger != null) && (logger.isLoggable(Level.INFO))) {
        if (name == null) {
          name = this.toString();
        }
        logger.info(
            "There are no properties that can be used for classification, so we return the behavior clusters using " //$NON-NLS-1$
                + name + " on dataset " + //$NON-NLS-1$
                ClusterUtils.dataIdentityString(data));
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
        values = new double[propertySize];
        _PropertyBehaviorClusterer.__fillIn(
            this._getPropertySetting(element), featureTypes, values);
        samples.add(new ClassifiedSample(index, values));
      }
    }

    // Now we can learn the relationship between element features and
    // clusters.
    sampleArray = samples.toArray(new ClassifiedSample[samples.size()]);
    samples = null;
    job = MultiClassifierTrainer.getInstance().use()//
        .setTrainers(_PropertyBehaviorClusterer.TRAINERS)//
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

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      if (name == null) {
        name = this.toString();
      }
      logger.info("Obtained " + selections.length + //$NON-NLS-1$
          " clusters using " + name//$NON-NLS-1$
          + " on dataset " + //$NON-NLS-1$
          ClusterUtils.dataIdentityString(data));
    }

    return this._createClustering(data, clustering, classifier,
        selections);
  }

  /**
   * Create the clustering
   *
   * @param owner
   *          the owner
   * @param behavior
   *          the behavior-based clustering underlying this clustering
   * @param classifier
   *          the classifier
   * @param selections
   *          the section
   * @return the clustering
   */
  abstract IClustering _createClustering(final IExperimentSet owner,
      final IClustering behavior, final IClassifier classifier,
      final DataSelection[] selections);

  /**
   * transform a property setting into a feature vector, i.e., feature
   * values to {@code double}s
   *
   * @param setting
   *          the property setting
   * @param featureTypes
   *          the feature types
   * @param dest
   *          the destination array
   */
  @SuppressWarnings("unchecked")
  private static final void __fillIn(final IPropertySetting setting,
      final EFeatureType[] featureTypes, final double[] dest) {
    int index, valueIndex;
    Object value;

    index = (-1);
    main: for (final IPropertyValue propertyValue : ((Iterable<IPropertyValue>) setting)) {
      ++index;

      if (propertyValue.isGeneralized()
          || ((propertyValue instanceof IParameterValue)
              && (((IParameterValue) propertyValue).isUnspecified()))) {
        dest[index] = EFeatureType.UNSPECIFIED_DOUBLE;
        continue;
      }

      value = propertyValue.getValue();
      switch (featureTypes[index]) {
        case NUMERICAL: {
          if (value instanceof Number) {
            dest[index] = EFeatureType
                .featureNumericalToDouble(((Number) value).doubleValue());
            continue main;
          }
          throw new IllegalStateException(//
              "Numerical property '" + propertyValue.getOwner() + //$NON-NLS-1$
                  "' with value '" + value + //$NON-NLS-1$
                  "', {" + propertyValue + //$NON-NLS-1$
                  "}. Really?");//$NON-NLS-1$
        }
        case BOOLEAN: {
          if (value instanceof Boolean) {
            dest[index] = EFeatureType
                .featureBooleanToDouble((Boolean) value);
            continue main;
          }
          throw new IllegalStateException(//
              "Boolean property '" + propertyValue.getOwner() + //$NON-NLS-1$
                  "' with value '" + value + //$NON-NLS-1$
                  "', {" + propertyValue + //$NON-NLS-1$
                  "}. Really?");//$NON-NLS-1$
        }
        case NOMINAL: {
          valueIndex = propertyValue.getOwner().getData()
              .indexOf(propertyValue);
          if (valueIndex >= 0) {
            dest[index] = EFeatureType.featureNominalToDouble(valueIndex);
            continue main;
          }

          throw new IllegalStateException((//
              "Cannot find value '" + value + //$NON-NLS-1$
                  "' of property value '" + propertyValue + //$NON-NLS-1$
                  "' of nominal property '" + propertyValue.getOwner() + //$NON-NLS-1$
                  '\'')
              + '.');
        }
        default: {
          throw new IllegalArgumentException((//
              "Unknown type '" + featureTypes[index]//$NON-NLS-1$
                  + "' of property value '" + propertyValue //$NON-NLS-1$
                  + "' for property '" + propertyValue.getOwner() //$NON-NLS-1$
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
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return HashUtils.combineHashes(this.getClass().hashCode(),
        this.m_behaviorClusterer.hashCode());
  }
}
