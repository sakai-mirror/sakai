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

package org.navigoproject.business.criteria;

import java.lang.reflect.Constructor;

import java.util.Iterator;
import java.util.LinkedHashMap;

/** @todo should this be CriterionFactory? */
public class CriteriaFactory
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(CriteriaFactory.class);
  private LinkedHashMap knownCriterion = new LinkedHashMap();

  /**
   * Creates a new CriteriaFactory object.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public CriteriaFactory()
    throws Exception
  {
    this.registerCriterion(CriterionEq.class);
    this.registerCriterion(CriterionGt.class);
    this.registerCriterion(CriterionGte.class);
    this.registerCriterion(CriterionLt.class);
    this.registerCriterion(CriterionLte.class);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param crit DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void registerCriterion(Class crit)
    throws Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("registerCriterion(" + crit + ")");
    }

    // Need to look up the Criteria(Object o1, Object o2)  constructor
    Class[] signature = new Class[]{ Object.class, Object.class };
    Constructor cons = crit.getConstructor(signature);

    // Determine the symbol with which to associate this constructor
    String symbol = this.makeCriterion(cons, "", "").getSymbol();

    if(LOG.isDebugEnabled())
    {
      LOG.debug("Symbol is: (" + symbol + ")");
    }

    this.knownCriterion.put(symbol, cons);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param cons DOCUMENTATION PENDING
   * @param o1 DOCUMENTATION PENDING
   * @param o2 DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private Criterion makeCriterion(Constructor cons, Object o1, Object o2)
    throws Exception
  {
    Object[] arg = new Object[2];
    arg[0] = o1;
    arg[1] = o2;

    return (Criterion) cons.newInstance(arg);
  }

  /** @todo should this be static? */
  public Criterion makeCriterion(String symbol, Object o1, Object o2)
    throws Exception
  {
    return this.makeCriterion(
      (Constructor) this.knownCriterion.get(symbol), o1, o2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param o DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Iterator availableCriterion(Object o)
  {
    /** @todo need to filter to only include appropriate Criteria */
    return this.knownCriterion.keySet().iterator();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param symbol DOCUMENTATION PENDING
   * @param o DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean validOperator(String symbol, Object o)
  {
    /** @todo need to return true iff a given operator can operate on the given Object*/
    return true;
  }
}
