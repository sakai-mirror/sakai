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

import java.lang.reflect.InvocationTargetException;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: Criterion.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public abstract class Criterion
  implements Evaluatable
{
  private Object o1;
  private Object o2;

  /**
   * Creates a new Criterion object.
   */
  public Criterion()
  {
  }

  /**
   * Creates a new Criterion object.
   *
   * @param o1 DOCUMENTATION PENDING
   * @param o2 DOCUMENTATION PENDING
   */
  public Criterion(Object o1, Object o2)
  {
    this.o1 = o1;
    this.o2 = o2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param o1 DOCUMENTATION PENDING
   * @param o2 DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public abstract boolean eval(Object o1, Object o2)
    throws EvaluationException;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public abstract String getSymbol();

  /**
   * DOCUMENTATION PENDING
   *
   * @param data DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public final boolean eval(Object data)
    throws EvaluationException
  {
    boolean result;

    if(data instanceof Map)
    {
      result = this.eval(((Map) data).get(this.o1), this.o2);
    }
    else //assume it is a JavaBean
    {
      try
      {
        result =
          this.eval(PropertyUtils.getProperty(data, (String) this.o1), this.o2);
      }
      catch(IllegalAccessException ex)
      {
        throw new EvaluationException(ex);
      }
      catch(InvocationTargetException ex)
      {
        throw new EvaluationException(ex);
      }
      catch(NoSuchMethodException ex)
      {
        throw new EvaluationException(ex);
      }
    }

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    return this.toStringHelper("");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prefix DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toStringHelper(String prefix)
  {
    return prefix + this.o1 + " " + this.getSymbol() + " " + this.o2;
  }
}
