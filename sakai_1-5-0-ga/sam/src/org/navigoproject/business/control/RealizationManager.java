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
 * Created on Oct 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.control;

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.XmlStringBuffer;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import osid.assessment.AssessmentException;
import osid.assessment.AssessmentPublished;
import osid.assessment.AssessmentTaken;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class RealizationManager
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RealizationManager.class);
  private String data;

  public Serializable realize(String publishedAssessmentId)
  {
    //TODO check QTI_ASSESSMENT_TAKEN table to see if there is any realized assessment.
    /**
     * select realizedAssessmentId from QTI_ASSESSMENT_TAKEN where publishedID = 'publishedAssessmentID'
     * and submitted = 0
     */   
    /**
     * If result = null, check QTI_SETTING for max_attempt
     * select 
     */
   
    return null;
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentPublished DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment realizeAssessment(
    AssessmentPublished assessmentPublished)
  {
    Assessment assessment = null;
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "realizeAssessment(AssessmentPublished" + assessmentPublished + ")");
    }

    Serializable data;
    try
    {
      data = assessmentPublished.getData();
      assessment = this.realizeAssessment(data);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param data DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment realizeAssessment(Serializable data)
  {
    Assessment assessment = null;
    try
    {
      if(data instanceof String)
      {
        assessment = new Assessment((String) data);
      }
      else if(data instanceof Assessment)
      {
        assessment = (Assessment) data;
      }
      else if(data instanceof XmlStringBuffer)
      {
        assessment = new Assessment(((XmlStringBuffer) data).stringValue());
      }

      if(assessment != null)
      {
        assessment.selectAndOrder();
        LOG.debug(assessment.stringValue());
        this.data = assessment.stringValue();
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return assessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getData()
  {
    return this.data;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param data DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String compose(String data)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("compose(String " + data + ")");
    }

    Assessment decomposed = new Assessment(data);
    try
    {
      decomposed.recompose(false);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return decomposed.stringValue();
  }
}
