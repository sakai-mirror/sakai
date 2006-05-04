package org.sakaiproject.metaobj.utils.xml.impl;

import org.sakaiproject.metaobj.utils.xml.ValueRange;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 19, 2005
 * Time: 5:18:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberValueRange extends ValueRange {

   private Class rangeClass = null;

   public NumberValueRange(Comparable max, Comparable min, boolean maxInclusive, boolean minInclusive) {
      super(max, min, maxInclusive, minInclusive);
      if (max != null) {
         rangeClass = max.getClass();
      }
      else if (min != null) {
         rangeClass = min.getClass();
      }
      else {
         rangeClass = Comparable.class;
      }
   }

   public boolean inRange(Comparable value) {
      if (!rangeClass.isAssignableFrom(value.getClass())) {
         if (rangeClass.equals(Integer.class)) {
            value = createInteger(value);
         }
         else if (rangeClass.equals(Long.class)) {
            value = createLong(value);
         }
         else if (rangeClass.equals(Double.class)) {
            value = createDouble(value);
         }
         else if (rangeClass.equals(Float.class)) {
            value = createFloat(value);
         }
      }
      return super.inRange(value);
   }

   protected Integer createInteger(Comparable value) {
      Number number = (Number) value;
      return new Integer(number.intValue());
   }

   protected Long createLong(Comparable value) {
      Number number = (Number) value;
      return new Long(number.longValue());
   }

   protected Float createFloat(Comparable value) {
      Number number = (Number) value;
      return new Float(number.floatValue());
   }

   protected Double createDouble(Comparable value) {
      Number number = (Number) value;
      return new Double(number.doubleValue());
   }

}
