package org.optimizationBenchmarking.evaluator.attributes.clusters.behaviorFromProperties;

import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.ml.classification.impl.abstr.ClassificationTools;
import org.optimizationBenchmarking.utils.ml.classification.spec.IClassifierParameterRenderer;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A classifier renderer based on a given experiment set */
abstract class _ClassifierRenderer
    implements IClassifierParameterRenderer {

  /** the clustering from which we get the data */
  final _PropertyBehaviorClustering<?> m_clustering;

  /**
   * create the classifier renderer base
   *
   * @param clustering
   *          the clustering
   */
  _ClassifierRenderer(final _PropertyBehaviorClustering<?> clustering) {
    super();
    if (clustering == null) {
      throw new IllegalArgumentException("Clustering cannot be null."); //$NON-NLS-1$
    }
    this.m_clustering = clustering;
  }

  /** {@inheritDoc} */
  @Override
  public final void renderShortClassName(final int classIndex,
      final ITextOutput textOut) {
    this.m_clustering.getData().get(classIndex).printShortName(textOut,
        ETextCase.IN_SENTENCE);
  }

  /** {@inheritDoc} */
  @Override
  public final void renderLongClassName(final int classIndex,
      final ITextOutput textOut) {
    this.m_clustering.getData().get(classIndex).printLongName(textOut,
        ETextCase.IN_SENTENCE);
  }

  /**
   * get the property at the given index
   *
   * @param index
   *          the index
   * @return the property
   */
  abstract IProperty _getProperty(final int index);

  /** {@inheritDoc} */
  @Override
  public final void renderShortFeatureName(final int featureIndex,
      final ITextOutput textOut) {
    this._getProperty(featureIndex).printShortName(textOut,
        ETextCase.IN_SENTENCE);
  }

  /** {@inheritDoc} */
  @Override
  public final void renderLongFeatureName(final int featureIndex,
      final ITextOutput textOut) {
    this._getProperty(featureIndex).printLongName(textOut,
        ETextCase.IN_SENTENCE);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("incomplete-switch")
  @Override
  public final void renderFeatureValue(final int featureIndex,
      final double featureValue, final ITextOutput textOut) {
    final IProperty property;
    final EPrimitiveType type;
    final byte _byte;
    final short _short;
    final int _int;
    final long _long;
    final double useValue;
    final ArrayListView<? extends IPropertyValue> data;

    property = this._getProperty(featureIndex);
    if (property == null) {
      throw new IllegalArgumentException(//
          "There is no property with index " //$NON-NLS-1$
              + featureIndex);
    }
    type = property.getPrimitiveType();

    if (ClassificationTools.featureDoubleIsUnspecified(featureValue)) {
      throw new IllegalArgumentException("Feature " + property//$NON-NLS-1$
          + " is unspecified, but such cases should have been handled already.");//$NON-NLS-1$
    }

    if (type == EPrimitiveType.BOOLEAN) {
      textOut.append(
          ClassificationTools.featureDoubleToBoolean(featureValue));
      return;

    }

    if (type != null) {
      useValue = ClassificationTools
          .featureDoubleToNumerical(featureValue);

      switch (type) {

        case BYTE: {
          _byte = ((byte) useValue);
          if (_byte == useValue) {
            textOut.append(_byte);
            return;
          }
        }

          //$FALL-THROUGH$
        case SHORT: {
          _short = ((short) useValue);
          if (_short == useValue) {
            textOut.append(_short);
            return;
          }
        }

          //$FALL-THROUGH$
        case INT: {
          _int = ((int) useValue);
          if (_int == useValue) {
            textOut.append(_int);
            return;
          }
        }

          //$FALL-THROUGH$
        case LONG: {
          _long = ((long) useValue);
          if (_long == useValue) {
            textOut.append(_long);
            return;
          }
        }

          //$FALL-THROUGH$
        case FLOAT:
        case DOUBLE: {
          textOut.append(useValue);
          return;
        }
      }
    }

    _int = ClassificationTools.featureDoubleToNominal(featureValue);
    data = property.getData();
    if ((_int >= 0) && (_int < property.getData().size())) {
      textOut.append(data.get(_int).getValue());
      return;
    }
    throw new IllegalArgumentException(((((//
    "Invalid (double-encoded) value '" + featureValue) //$NON-NLS-1$
        + "' for property '") + property.getName()) //$NON-NLS-1$
        + '\'') + '.');
  }
}
