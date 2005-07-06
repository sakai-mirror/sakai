/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/impl/NodeSortComparator.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.model.impl;

import org.sakaiproject.metaobj.repository.Node;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 11, 2004
 * Time: 3:39:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeSortComparator implements Comparator {
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

      Node node1 = (Node) o1;
      Node node2 = (Node) o2;

      if (node1.isCollection() == node2.isCollection()) {
         return node1.getName().compareTo(node2.getName());
      }

      if (node1.isCollection()) {
         return -1;
      } else {
         return 1;
      }
   }
}
