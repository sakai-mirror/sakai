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

/*
 * Created on Nov 21, 2003
 */
package org.navigoproject.data.imsglobal.qti;

import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;

/**
 * DOCUMENT ME!
 *
 * @author chmaurer
 */
public class QtiIpAddressDataBean
{
  private String id;
  private String startIp;
  private String endIp;
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QtiIpAddressDataBean.class);
  private boolean deadPersistentState = false;

  /**
   *
   */
  public String getId()
  {
    return this.id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   */
  public void setId(String id)
  {
    this.id = id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getStartIp()
  {
    return this.startIp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param startIp DOCUMENTATION PENDING
   */
  public void setStartIp(String startIp)
  {
    this.startIp = startIp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEndIp()
  {
    return this.endIp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param endIp DOCUMENTATION PENDING
   */
  public void setEndIp(String endIp)
  {
    this.endIp = endIp;
  }

  /**
   * DOCUMENT ME!
   *
   * @param broker {@link org.apache.ojb.broker.PersistenceBroker
   *        PersistenceBroker}
   *
   * @throws IllegalArgumentException If any passed parameters are null.
   */
  public void store(PersistenceBroker broker)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("store(PersistenceBroker " + broker + ")");
    }

    if(broker == null)
    {
      throw new IllegalArgumentException(
        "Illegal PersistenceBroker argument: " + broker);
    }

    try
    {
      broker.store(this);
    }
    catch(PersistenceBrokerException ex)
    {
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @throws IllegalStateException If a {@link
   *         org.apache.ojb.broker.PersistenceBroker PersistenceBroker} could
   *         not be instantiated.
   */
  public void store()
  {
    LOG.debug("store()");
    PersistenceBroker broker = null;
    try
    {
      broker = PersistenceBrokerFactory.defaultPersistenceBroker();
      broker.beginTransaction();
      broker.store(this);
      broker.commitTransaction();
    }
    catch(PBFactoryException ex)
    {
      LOG.error(ex.getMessage(), ex);
      throw new IllegalStateException(ex.getMessage());
    }
    catch(PersistenceBrokerException ex)
    {
      broker.abortTransaction();
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }
    finally
    {
      if(broker != null)
      {
        broker.close();
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param broker {@link org.apache.ojb.broker.PersistenceBroker
   *        PersistenceBroker}
   *
   * @throws IllegalArgumentException If any passed parameters are null.
   * @throws IllegalStateException If object is in a dead persistent state.
   */
  public void delete(PersistenceBroker broker)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("delete(PersistenceBroker " + broker + ")");
    }

    if(broker == null)
    {
      throw new IllegalArgumentException(
        "Illegal PersistenceBroker argument: " + broker);
    }

    if(deadPersistentState)
    {
      throw new IllegalStateException(this.getClass().getName() + " is dead");
    }

    try
    {
      broker.delete(this);
      this.deadPersistentState = true;
    }
    catch(PersistenceBrokerException ex)
    {
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @throws IllegalStateException If object is in a dead persistent state.
   */
  public void delete()
  {
    LOG.debug("delete()");
    PersistenceBroker broker = null;
    try
    {
      broker = PersistenceBrokerFactory.defaultPersistenceBroker();
      broker.beginTransaction();
      this.delete(broker);
      broker.commitTransaction();
    }
    catch(PBFactoryException ex)
    {
      LOG.error(ex.getMessage(), ex);
      throw new IllegalStateException(ex.getMessage());
    }
    catch(PersistenceBrokerException ex)
    {
      broker.abortTransaction();
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }
    finally
    {
      if(broker != null)
      {
        broker.close();
      }
    }
  }
}
