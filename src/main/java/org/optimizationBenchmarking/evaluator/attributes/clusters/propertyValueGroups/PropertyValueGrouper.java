package org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClustererLoader;
import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.EAttributeType;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.math.NumericalTypes;
import org.optimizationBenchmarking.utils.parsers.AnyNumberParser;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.tokenizers.WordBasedStringIterator;

/**
 * An attribute which can group property values. The groups are generated
 * according to
 * {@link org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.EGroupingMode
 * modes}, currently including
 * {@link org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.EGroupingMode#DISTINCT
 * distinct} values for a given property for arbitrarily-typed properties
 * and ranges which are
 * {@link org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.EGroupingMode#MULTIPLES
 * muliples} or
 * {@link org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups.EGroupingMode#POWERS}
 * of reasonable numbers for numerical properties.
 */
public final class PropertyValueGrouper
    extends Attribute<IProperty, PropertyValueGroups> {

  /** The suffix of the grouping parameter: {@value} */
  public static final String PARAM_GROUPING_SUFFIX = "ValueGroupingStructure"; //$NON-NLS-1$

  /** The default parameter for all grouping */
  public static final String PARAM_DEFAULT_GROUPING = //
  (Character.toLowerCase(//
      PropertyValueGrouper.PARAM_GROUPING_SUFFIX.charAt(0))
      + PropertyValueGrouper.PARAM_GROUPING_SUFFIX.substring(1));

  /** the default minimum number of anticipated groups */
  private static final int DEFAULT_MIN_GROUPS = 2;
  /** the default maximum number of anticipated groups */
  private static final int DEFAULT_MAX_GROUPS = 10;
  /** the default grouping mode */
  private static final EGroupingMode DEFAULT_GROUPING_MODE = EGroupingMode.ANY;

  /** the constant for the multiples mode */
  private static final String MULTIPLES = "multiples";//$NON-NLS-1$
  /** the constant for the powers mode */
  private static final String POWERS = "powers";//$NON-NLS-1$
  /** the constant for the distinct mode */
  private static final String DISTINCT = "distinct";//$NON-NLS-1$
  /** the constant for the any mode */
  private static final String ANY = "any";//$NON-NLS-1$
  /** the constant for "of" */
  private static final String OF = "of";//$NON-NLS-1$

  /** The default value grouper for experiment parameters */
  public static final PropertyValueGrouper DEFAULT_GROUPER//
  = new PropertyValueGrouper(PropertyValueGrouper.DEFAULT_GROUPING_MODE,
      null, PropertyValueGrouper.DEFAULT_MIN_GROUPS,
      PropertyValueGrouper.DEFAULT_MAX_GROUPS);

  /** the grouping mode to use */
  private final EGroupingMode m_groupingMode;

  /** the parameter to be passed to the grouping */
  private final Number m_groupingParameter;

  /**
   * the goal minimum of the number of groups &ndash; any number of groups
   * less than this would not be good
   */
  private final int m_minGroups;
  /**
   * the goal maximum of the number of groups &ndash; any number of groups
   * higher than this would not be good
   */
  private final int m_maxGroups;

  /**
   * create the property value grouper
   *
   * @param groupingMode
   *          the grouping mode
   * @param groupingParameter
   *          the parameter to be passed to the grouping
   * @param minGroups
   *          the goal minimum of the number of groups &ndash; any number
   *          of groups less than this would not be good
   * @param maxGroups
   *          the goal maximum of the number of groups &ndash; any number
   *          of groups higher than this would not be good
   */
  public PropertyValueGrouper(final EGroupingMode groupingMode,
      final Number groupingParameter, final int minGroups,
      final int maxGroups) {
    super(EAttributeType.TEMPORARILY_STORED);

    if (groupingMode == null) {
      throw new IllegalArgumentException(//
          "Grouping mode must not be null."); //$NON-NLS-1$
    }
    if (minGroups < 0) {
      throw new IllegalArgumentException(//
          "The minimum number of groups must be greater or equal to 0, but is " //$NON-NLS-1$
              + minGroups);
    }
    if (maxGroups < minGroups) {//
      throw new IllegalArgumentException(//
          "The maximum number of groups must be greater or equal to the minimum number, but is " //$NON-NLS-1$
              + maxGroups + " while the minimum is " + minGroups);//$NON-NLS-1$
    }

    this.m_groupingMode = groupingMode;
    this.m_groupingParameter = groupingParameter;
    this.m_minGroups = minGroups;
    this.m_maxGroups = maxGroups;
  }

  /**
   * Get the goal minimum number of groups
   *
   * @return the goal minimum number of groups
   */
  public final int getMinGroups() {
    return this.m_minGroups;
  }

  /**
   * Get the goal maximum number of groups
   *
   * @return the goal maximum number of groups
   */
  public final int getMaxGroups() {
    return this.m_maxGroups;
  }

  /**
   * Get the grouping parameter, or {@code null} if none is specified
   *
   * @return the grouping parameter, or {@code null} if none is specified
   */
  public final Number getGroupingParameter() {
    return this.m_groupingParameter;
  }

  /**
   * Get the grouping mode
   *
   * @return the grouping mode
   */
  public final EGroupingMode getGroupingMode() {
    return this.m_groupingMode;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(//
        HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_groupingMode), //
            HashUtils.hashCode(this.m_groupingParameter)), //
        HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_minGroups), //
            HashUtils.hashCode(this.m_maxGroups)));
  }

  /**
   * Get the values of a given property
   *
   * @param property
   *          the property
   * @return the values of the property
   */
  private static final Object[] __getValues(final IProperty property) {
    final Object[] values;
    final ArrayListView<? extends IPropertyValue> data;
    int i;

    data = property.getData();
    i = data.size();
    values = new Object[i];
    for (; (--i) >= 0;) {
      values[i] = data.get(i).getValue();
    }
    return values;
  }

  /**
   * create the message
   *
   * @param data
   *          the property name to create the message for
   * @return the message
   */
  final String _createMessage(final String data) {
    return ((((((((("the values of " + //$NON-NLS-1$
        data + " using mode ") + this.m_groupingMode) + //$NON-NLS-1$
        " and parameter ") + this.m_groupingParameter) + //$NON-NLS-1$
        " into [") + //$NON-NLS-1$
        this.m_minGroups) + ',') + this.m_maxGroups) + "] groups");//$NON-NLS-1$
  }

  /**
   * create the message
   *
   * @param data
   *          the data to create the message for
   * @return the message
   */
  private final String __createMessage(final IProperty data) {
    return this._createMessage(((data instanceof IFeature)//
        ? "instance feature " //$NON-NLS-1$
        : "experiment parameter ")//$NON-NLS-1$
        + data.getName());
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unused")
  @Override
  protected final PropertyValueGroups compute(final IProperty data,
      final Logger logger) {
    final _Groups groups;
    final IExperimentSet set;
    final PropertyValueGroups result;
    Object[] objData;
    Number[] numberData;
    _Group[] buffer;
    int index, type;
    String message;
    Object value;
    DataSelection unspecified;
    IParameterValue pv;

    if ((logger != null) && (logger.isLoggable(Level.FINE))) {
      message = this.__createMessage(data);
      logger.fine(("Now grouping data by " + message) + '.');//$NON-NLS-1$
    } else {
      message = null;
    }

    // extract all values
    objData = PropertyValueGrouper.__getValues(data);

    // get the numerical type, if any
    type = (-1);
    for (final Object object : objData) {
      if ((type &= NumericalTypes.getTypes(object)) == 0) {
        break;
      }
    }

    // sort the data
    try {
      Arrays.sort(objData);
    } catch (final Throwable ignoreable) {
      // ignore
    }

    // allocate the memory for the group
    buffer = new _Group[objData.length];
    for (index = buffer.length; (--index) >= 0;) {
      buffer[index] = new _Group();
    }

    // get groupings

    if (type == 0) {
      groups = this.m_groupingMode._groupObjects(this.m_groupingParameter,
          objData, this.m_minGroups, this.m_maxGroups, buffer);
    } else {
      numberData = new Number[objData.length];
      System.arraycopy(objData, 0, numberData, 0, objData.length);
      if ((type & NumericalTypes.IS_LONG) != 0) {
        groups = this.m_groupingMode._groupLongs(this.m_groupingParameter,
            numberData, this.m_minGroups, this.m_maxGroups, buffer);
      } else {
        groups = this.m_groupingMode._groupDoubles(
            this.m_groupingParameter, numberData, this.m_minGroups,
            this.m_maxGroups, buffer);
      }
      numberData = null;
    }
    buffer = null;

    // now compile data
    set = data.getOwner().getOwner();

    for (final _Group group : groups.m_groups) {
      group.m_selection = new DataSelection(set);
      for (final Object object : objData) {
        if (group._contains(object)) {
          group.m_selection.addPropertyValue(data, object);
        }
      }
    }
    objData = null;

    unspecified = null;
    value = null;
    if (data instanceof IParameter) {
      pv = ((IParameter) data).getUnspecified();
      if (pv != null) {
        value = pv.getValue();
        if (value != null) {
          unspecified = new DataSelection(set);
          unspecified.addParameterValue(pv);
        }
      }
    }

    // return the object

    switch (groups.m_groupingMode) {
      case DISTINCT: {
        result = new DistinctValueGroups(data, groups, unspecified, value);
        break;
      }

      case POWERS:
      case MULTIPLES: {
        result = new ValueRangeGroups(data, groups, unspecified, value);
        break;
      }

      default: {
        throw new IllegalArgumentException(//
            "Unknown grouping mode: " + groups.m_groupingMode); //$NON-NLS-1$
      }
    }

    if ((logger != null) && (logger.isLoggable(Level.FINE))) {
      if (message == null) {
        message = this.__createMessage(data);
      }
      message = (((("Finished grouping data by " + message) + //$NON-NLS-1$
          " created ") + result.getData().size())//$NON-NLS-1$
          + " regular groups");//$NON-NLS-1$
      if (result.getUnspecifiedGroup() != null) {
        message += " and one group for the unspecified parameter value.";//$NON-NLS-1$
      } else {
        message += '.';
      }
      logger.fine(message);
    }
    return result;
  }

  /**
   * Load a property value grouper from a configuration
   *
   * @param property
   *          the property
   * @param config
   *          the configuration
   * @return the grouper
   */
  public static final PropertyValueGrouper configure(
      final IProperty property, final Configuration config) {
    final String propertyName;
    final Number defParam;
    final WordBasedStringIterator iterator;
    int minGroups, maxGroups;
    EGroupingMode mode;
    String current, currentLC;
    Number param;
    String grouping, origGrouping, sourceParam, sourceParam2, message;

    if (config == null) {
      throw new IllegalArgumentException(//
          "Configuration cannot be null."); //$NON-NLS-1$
    }

    sourceParam = PropertyValueGrouper.PARAM_DEFAULT_GROUPING;
    origGrouping = grouping = TextUtils.prepare(//
        config.getString(sourceParam, null));
    if (property != null) {
      propertyName = property.getName();
      if (propertyName == null) {
        throw new IllegalStateException(//
            "Property name cannot be null.");//$NON-NLS-1$
      }
      sourceParam2 = (property.getName()
          + PropertyValueGrouper.PARAM_GROUPING_SUFFIX);
      grouping = TextUtils.prepare(//
          config.getString(sourceParam2, grouping));
      if (grouping != origGrouping) {
        sourceParam = sourceParam2;
        sourceParam2 = null;
      }
    } else {
      sourceParam2 = null;
    }

    minGroups = config.getInt(ClustererLoader.PARAM_MIN_GROUPS, -1,
        ClustererLoader.MAX_GROUPS, -1); //
    maxGroups = config.getInt(ClustererLoader.PARAM_MIN_GROUPS, -1,
        ClustererLoader.MAX_GROUPS, -1);

    if (minGroups <= 0) {
      if ((maxGroups <= 0)
          || (maxGroups >= PropertyValueGrouper.DEFAULT_MIN_GROUPS)) {
        minGroups = PropertyValueGrouper.DEFAULT_MIN_GROUPS;
      } else {
        minGroups = maxGroups;
      }
    }
    if (maxGroups <= 0) {
      if (minGroups <= PropertyValueGrouper.DEFAULT_MAX_GROUPS) {
        maxGroups = PropertyValueGrouper.DEFAULT_MAX_GROUPS;
      } else {
        maxGroups = minGroups;
      }
    }

    if ((minGroups == PropertyValueGrouper.DEFAULT_MIN_GROUPS) && //
        (maxGroups == PropertyValueGrouper.DEFAULT_MAX_GROUPS) && //
        ((grouping == null) || (PropertyValueGrouper.DEFAULT_GROUPING_MODE
            .toString().equalsIgnoreCase(grouping)))) {
      return PropertyValueGrouper.DEFAULT_GROUPER;
    }

    if (grouping == null) {
      return new PropertyValueGrouper(
          PropertyValueGrouper.DEFAULT_GROUPING_MODE, null, minGroups,
          maxGroups);
    }

    mode = PropertyValueGrouper.DEFAULT_GROUPING_MODE;
    param = defParam = //
    PropertyValueGrouper.DEFAULT_GROUPER.getGroupingParameter();

    try {
      iterator = new WordBasedStringIterator(grouping);

      current = iterator.next();
      currentLC = TextUtils.toLowerCase(current);

      define: {
        switch (currentLC) {
          case MULTIPLES: {
            mode = EGroupingMode.MULTIPLES;
            break;
          }
          case POWERS: {
            mode = EGroupingMode.POWERS;
            break;
          }
          case DISTINCT: {
            mode = EGroupingMode.DISTINCT;
            break define;
          }
          case ANY: {
            mode = EGroupingMode.ANY;
            break define;
          }
          default: {
            throw new IllegalArgumentException(((//
            "Unexpected token '" //$NON-NLS-1$
                + currentLC) + '\'') + '.');
          }
        }

        // the grouping parameter (may) follow

        if (iterator.hasNext()) {
          current = iterator.next();
          if (!(PropertyValueGrouper.OF.equalsIgnoreCase(current))) {
            throw new IllegalArgumentException((((//
            '\'' + PropertyValueGrouper.OF) + "' expected, but '")//$NON-NLS-1$
                + current) + "' found.");//$NON-NLS-1$
          }

          param = AnyNumberParser.INSTANCE.parseObject(iterator.next());
        }
      }

      if ((minGroups == PropertyValueGrouper.DEFAULT_MIN_GROUPS) && //
          (maxGroups == PropertyValueGrouper.DEFAULT_MAX_GROUPS) && //
          Compare.equals(mode, PropertyValueGrouper.DEFAULT_GROUPING_MODE)
          && //
          Compare.equals(param, defParam)) {
        return PropertyValueGrouper.DEFAULT_GROUPER;
      }

      return new PropertyValueGrouper(mode, param, minGroups, maxGroups);
    } catch (final Throwable cause) {
      message = ((("The string '" + grouping) + //$NON-NLS-1$
          "' is not valid for ") + sourceParam);//$NON-NLS-1$
      if (sourceParam2 != null) {
        message += " or " + sourceParam2;//$NON-NLS-1$
      }
      if (property != null) {
        message += (" and property " + property);//$NON-NLS-1$
      }
      throw new IllegalArgumentException((message + '.'), cause);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this._createMessage("a property");//$NON-NLS-1$
  }
}
