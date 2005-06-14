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
 * Created on Feb 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity.properties;

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;

import osid.OsidException;

import osid.assessment.AssessmentException;

/**
 * @author casong
 * @version $Id:
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssessmentAuthorHelper
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentAuthorHelper.class);

  /**
   * Creates a new AssessmentAuthorHelper object.
   */
  public AssessmentAuthorHelper()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public AssessmentTemplateIterator getAssessmentTemplates()
  {
    LOG.debug("getAssessmentTemplates()");
    AssessmentTemplateIterator iterator = null;
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      long templateId = 0;
      iterator = delegate.getAssessmentTemplate(templateId);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(OsidException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return iterator;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param template DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment getAssessmentMetadata(
    AssessmentTemplatePropertiesImpl template, Assessment assessXml)
  {
    LOG.debug("getAssessmentMetadata()");
    PropertyToMetadataConvertor convertor =
      new PropertyToMetadataConvertor();

    Assessment assessment = convertor.translateTemplateToMetadata(template, assessXml);

    return assessment;
  }
  
  public Assessment getAssessmentMetadata(String templateId, Assessment assessXml)
  {
    Assessment assessment = null;
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      AssessmentTemplateIterator iter = delegate.getAssessmentTemplate(Long.parseLong(templateId));
      AssessmentTemplate myTemplate = (AssessmentTemplate) iter.next();
      AssessmentTemplateProperties props = (AssessmentTemplateProperties) myTemplate.getData(); 
      PropertyToMetadataConvertor convertor = new PropertyToMetadataConvertor();
      assessment = convertor.translateTemplateToMetadata(props, assessXml);
    }
    catch (AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (OsidException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (NumberFormatException e)
    {
      LOG.error(e.getMessage(), e);
    }
    return assessment;
  }
}
