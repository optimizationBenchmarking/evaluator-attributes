package org.optimizationBenchmarking.evaluator.attributes.clusters.propertyValueGroups;

import org.optimizationBenchmarking.evaluator.data.impl.shadow.DataSelection;
import org.optimizationBenchmarking.utils.comparison.Compare;

/** The internal class to represent groups */
final class _Group implements Cloneable {
  /** the lower bound */
  Object m_lower;
  /** the upper bound */
  Object m_upper;
  /** is the upper bound exclusive */
  boolean m_isUpperExclusive;
  /** the group size */
  int m_size;

  /** the data selection */
  DataSelection m_selection;

  /** {@inheritDoc} */
  @Override
  public final _Group clone() {
    try {
      return ((_Group) (super.clone()));
    } catch (final Throwable t) {
      throw new UnsupportedOperationException(t);
    }
  }

  /**
   * Does this group contain the given object?
   *
   * @param o
   *          the object
   * @return {@code true} if it is in the group, {@code false} otherwise
   */
  final boolean _contains(final Object o) {
    final int res;

    if (Compare.equals(o, this.m_lower)) {
      return true;
    }
    if (Compare.equals(o, this.m_upper)) {
      return (!(this.m_isUpperExclusive));
    }
    if (Compare.compare(this.m_lower, o) <= 0) {
      res = Compare.compare(o, this.m_upper);
      return (res < (this.m_isUpperExclusive ? 0 : 1));
    }
    return false;
  }
}
