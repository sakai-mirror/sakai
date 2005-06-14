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

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: Criteria.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public abstract class Criteria
  extends ArrayList
  implements Evaluatable
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Criteria.class);
  public static final int AND = 1;
  public static final int OR = 2;
  private boolean negate = false;

  /**
   * Creates a new Criteria object.
   */
  public Criteria()
  {
    LOG.debug("Criteria()");
  }

  /**
   * Creates a new Criteria object.
   *
   * @param c DOCUMENTATION PENDING
   */
  public Criteria(Criteria c)
  {
    super(c);
    if(LOG.isDebugEnabled())
    {
      LOG.debug("Criteria(" + c + ")");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    return toStringHelper("");
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
    if(LOG.isDebugEnabled())
    {
      LOG.debug("toString()");
    }

    StringBuffer result = new StringBuffer();

    if(this.negate)
    {
      result.append("!");
    }

    Iterator iter = this.iterator();

    result.append("(");

    while(iter.hasNext())
    {
      Evaluatable c = (Evaluatable) iter.next();
      result.append(c.toString());
      if(iter.hasNext())
      {
        result.append(this.combinerString());
      }
    }

    result.append(")");

    return result.toString();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public final boolean isNegated()
  {
    LOG.debug("isNegated()");

    return this.negate;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public final void negate()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("negate()");
    }

    this.negate = ! this.negate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param data DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public abstract boolean evaluate(Object data)
    throws EvaluationException;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public abstract String combinerString();

  /**
   * DOCUMENTATION PENDING
   *
   * @param data DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public boolean eval(Object data)
    throws EvaluationException
  {
    boolean result = this.evaluate(data);

    if(this.negate)
    {
      return ! result;
    }
    else
    {
      return result;
    }
  }
}
