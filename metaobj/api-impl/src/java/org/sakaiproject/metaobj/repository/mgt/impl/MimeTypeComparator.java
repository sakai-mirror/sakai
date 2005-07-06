/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/MimeTypeComparator.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.MimeType;

import java.util.Comparator;

public class MimeTypeComparator implements Comparator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public MimeTypeComparator() {
   }

   /**
    * Compares its two arguments for order.  Returns a negative integer,
    * zero, or a positive integer as the first argument is less than, equal
    * to, or greater than the second.<p>
    * <p/>
    * The implementor must ensure that <tt>sgn(compare(x, y)) ==
    * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
    * implies that <tt>compare(x, y)</tt> must throw an exception if and only
    * if <tt>compare(y, x)</tt> throws an exception.)<p>
    * <p/>
    * The implementor must also ensure that the relation is transitive:
    * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
    * <tt>compare(x, z)&gt;0</tt>.<p>
    * <p/>
    * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
    * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
    * <tt>z</tt>.<p>
    * <p/>
    * It is generally the case, but <i>not</i> strictly required that
    * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
    * any comparator that violates this condition should clearly indicate
    * this fact.  The recommended language is "Note: this comparator
    * imposes orderings that are inconsistent with equals."
    *
    * @param o1 the first object to be compared.
    * @param o2 the second object to be compared.
    * @return a negative integer, zero, or a positive integer as the
    *         first argument is less than, equal to, or greater than the
    *         second.
    * @throws ClassCastException if the arguments' types prevent them from
    *                            being compared by this Comparator.
    */
   public int compare(Object o1, Object o2) {
      MimeType type1 = (MimeType)o1;
      MimeType type2 = (MimeType)o2;

      if (!type1.getPrimaryType().equals(type2.getPrimaryType())) {
         return type1.getPrimaryType().compareTo(type2.getPrimaryType());
      }
      else {
         return type1.getSubType().compareTo(type2.getSubType());
      }
   }
}
