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

import java.sql.Timestamp;

import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.osid.impl.PersistenceService;

/**
 * DOCUMENT ME!
 *
 * @author chmaurer
 * @version $Id: QtiSettingsData.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class QtiSettingsData
{
  private QtiSettingsBean bean;
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QtiSettingsData.class);

  /**
   *
   */
  public QtiSettingsData()
  {
    LOG.debug("new QtiSettingsData()");
    bean = new QtiSettingsBean();
  }

  /**
   * Creates a new QtiSettingsData object.
   *
   * @param bean DOCUMENTATION PENDING
   *
   * @throws IllegalArgumentException DOCUMENTATION PENDING
   */
  public QtiSettingsData(QtiSettingsBean bean)
  {
    if(bean == null)
    {
      throw new IllegalArgumentException(
        "Illegal QtiSettingsDataBean argument" + bean);
    }

    this.bean = bean;
  }

  /**
   * Creates a new QtiSettingsData object.
   *
   * @param id DOCUMENTATION PENDING
   * @param maxAttempts DOCUMENTATION PENDING
   * @param autoSubmit DOCUMENTATION PENDING
   * @param testDisabled DOCUMENTATION PENDING
   * @param startDate DOCUMENTATION PENDING
   * @param endDate DOCUMENTATION PENDING
   *
   * @throws IllegalArgumentException DOCUMENTATION PENDING
   */
  public QtiSettingsData(
    String id, Integer maxAttempts, String autoSubmit, String testDisabled,
    Timestamp startDate, Timestamp endDate, Timestamp feedbackDate, Timestamp retractDate)
    throws IllegalArgumentException
  {
    if(id == null)
    {
      throw new IllegalArgumentException("Illegal String argument" + id);
    }

    bean = new QtiSettingsBean();
    setId(id);
    setMaxAttempts(maxAttempts);
    setAutoSubmit(autoSubmit);
    setTestDisabled(testDisabled);
    setStartDate(startDate);
    setEndDate(endDate);    
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getId()
  {
    return bean.getId();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   *
   * @throws IllegalArgumentException DOCUMENTATION PENDING
   */
  public void setId(String id)
  {
    if((id == null) || (id.length() < 1))
    {
      throw new IllegalArgumentException("Illegal String argument: " + id);
    }

    bean.setId(id);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer getMaxAttempts()
  {
    return bean.getMaxAttempts();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param maxAttempts DOCUMENTATION PENDING
   */
  public void setMaxAttempts(Integer maxAttempts)
  {
    bean.setMaxAttempts(maxAttempts);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAutoSubmit()
  {
    return bean.getAutoSubmit();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoSubmit DOCUMENTATION PENDING
   */
  public void setAutoSubmit(String autoSubmit)
  {
    bean.setAutoSubmit(autoSubmit);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTestDisabled()
  {
    return bean.getTestDisabled();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testDisabled DOCUMENTATION PENDING
   */
  public void setTestDisabled(String testDisabled)
  {
    bean.setTestDisabled(testDisabled);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Timestamp getStartDate()
  {
    return bean.getStartDate();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param startDate DOCUMENTATION PENDING
   */
  public void setStartDate(Timestamp startDate)
  {
    bean.setStartDate(startDate);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Timestamp getEndDate()
  {
    return bean.getEndDate();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param endDate DOCUMENTATION PENDING
   */
  public void setEndDate(Timestamp endDate)
  {
    bean.setEndDate(endDate);
  }
  
  /**
   * DOCUMENTATION PENDING
   */
  public void store()
  {
		PersistenceService.getInstance().getQtiQueries().persistSettingsBean(bean);    
  }

  
  /**
   * DOCUMENTATION PENDING
   */
  public void delete()
  {
		PersistenceService.getInstance().getQtiQueries().deleteSettingsBean(bean.getId());    
  }

 
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QtiSettingsBean getBean()
  {
    return bean;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected QtiSettingsBean getQtiSettingsDataBean()
  {
    return bean;
  }
}
