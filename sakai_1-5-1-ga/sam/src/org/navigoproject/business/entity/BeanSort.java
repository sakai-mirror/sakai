/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.business.entity;

import java.util.Arrays;
import java.util.Collection;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: BeanSort.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class BeanSort
{
  private Collection collection;
  private String property;
  private BeanSortComparator bsc;
  private boolean string = true;
  private boolean numeric = false;

  // for later date support
  //  private boolean date = false;

  /**
   * The only public constructor.  Requires a valid property name for a a Java
   * Bean as a sole parameter.
   *
   * @param c the property name for Java Bean to sort by
   * @param onProperty DOCUMENTATION PENDING
   */
  public BeanSort(Collection c, String onProperty)
  {
    collection = c;
    property = onProperty;
  }

  /**
   * Creates a new BeanSort object.
   */
  private BeanSort()
  {
  }
  ;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] arraySort()
  {
    Object[] array = collection.toArray();
    if(string)
    {
      bsc = new BeanSortComparator(property);
    }
    else if(numeric)
    {
      bsc = new BeanIntegerComparator(property);
    }

    Arrays.sort(array, getBeanSortComparator(property));

    return array;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection sort()
  {
    Object[] array = arraySort();
    collection.clear();
    for(int i = 0; i < array.length; i++)
    {
      collection.add(array[i]);
    }

    return collection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param property DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private BeanSortComparator getBeanSortComparator(String property)
  {
    BeanSortComparator bsc = null;

    if(string)
    {
      bsc = new BeanSortComparator(property);
    }
    else if(numeric)
    {
      bsc = new BeanIntegerComparator(property);
    }

    return bsc;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void toStringSort()
  {
    string = true;
    numeric = false;

    //    date = false;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void toNumericSort()
  {
    string = false;
    numeric = true;

    //    date = false;
  }

  /**
   * @todo add date support
   */

  //  public void toDateSort()
  //  {
  //    string = false;
  //    numeric = false;
  //    date = true;
  //  }
}
