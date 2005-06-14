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

package org.navigoproject.data;

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.Item;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.osid.assessment.impl.AssessmentPublishedImpl;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;

import java.io.IOException;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import osid.assessment.AssessmentException;
import osid.assessment.AssessmentManager;
import osid.assessment.AssessmentPublished;
import osid.assessment.SectionIterator;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author casong
 */
public class RealizationManager
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RealizationManager.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTakenId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public RealizationBean getNonSubmittedRealizationBean(String assessmentTakenId)
  {
    List results =
      PersistenceService.getInstance().getQtiQueries().returnGetRealizations(
        assessmentTakenId);

    Iterator i = results.iterator();
    if(i.hasNext())
    {
      LOG.debug("findByOkiId() found");

      return (RealizationBean) i.next();
    }
    else
    {
      LOG.debug("findByOkiId() not found");

      return null;
    }
  }
  
  public RealizationBean getRealizationBean(String assessmentTakenId)
  {
    List results =
      PersistenceService.getInstance().getQtiQueries().returnGetAllRealizations(assessmentTakenId);

    Iterator i = results.iterator();
    if(i.hasNext())
    {
      LOG.debug("findByOkiId() found");

      return (RealizationBean) i.next();
    }
    else
    {
      LOG.debug("findByOkiId() not found");

      return null;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessmentId DOCUMENTATION PENDING
   * @param agentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public RealizationBean getNonSubmittedRealizationBean(
    String publishedAssessmentId, String agentId)
  {
    List results =
      PersistenceService.getInstance().getQtiQueries()
                        .returnNonSubmittedRealizations(
        publishedAssessmentId, agentId);

    Iterator i = results.iterator();
    if(i.hasNext())
    {
      LOG.debug("findByOkiId() found");

      return (RealizationBean) i.next();
    }
    else
    {
      LOG.debug("findByOkiId() not found");

      return null;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param realizationBean DOCUMENTATION PENDING
   */
  public void saveRealizationBean(RealizationBean realizationBean)
  {
    PersistenceService.getInstance().getQtiQueries().persistRealizationBean(
      realizationBean);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentPublishedId DOCUMENTATION PENDING
   * @param agentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   * @throws SharedException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public Document getRealizedAssessment(
    String assessmentPublishedId, String agentId)
    throws ParserConfigurationException, SAXException, IOException, 
      DigitalRepositoryException, SharedException, AssessmentException
  {
    Document assessmentDoc = null;
    RealizationBean bean =
      this.getNonSubmittedRealizationBean(assessmentPublishedId, agentId);
    if(bean == null)
    {

      QtiSettingsBean settingBean =
        PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(assessmentPublishedId);
      if(settingBean != null)
      {
        Timestamp date = settingBean.getCreatedDate();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTimeInMillis(date.getTime());
  
        //create a new assessment.
        AssessmentManager manager = new AssessmentManagerImpl();
//        osid.assessment.Assessment assessmentPublished =
//          manager.getAssessment(
//            (new RepositoryManager()).getId(assessmentPublishedId));
        RepositoryManager rm = new RepositoryManager();
        PublishedAssessmentBean pab = PersistenceService.getInstance().getPublishedAssessmentQueries().findCoreIdByPublishedId(assessmentPublishedId);
        Id coreAssessmentId = (new RepositoryManager()).getId(pab.getCoreId());
        Asset coreAssessmentAsset = rm.getAsset(coreAssessmentId, createdDate);
        
        if(coreAssessmentAsset != null)
        {
          String name = coreAssessmentAsset.getDisplayName();
          String description = coreAssessmentAsset.getDescription();
    
          //TODO might need to put this realized assessment into assessment_taken table.
          osid.assessment.Assessment assessmentTaken =
            manager.createAssessment(
              name, description, TypeLib.DR_QTI_ASSESSMENT_TAKEN);
          assessmentTaken.updateData(coreAssessmentAsset.getContent());
          Assessment assessmentTakenXml = (Assessment) assessmentTaken.getData();
          assessmentTakenXml.setIdent(assessmentTaken.getId().getIdString());
          assessmentTakenXml.realize(true, createdDate);
          Collection items = assessmentTakenXml.getItems();
          Iterator iter = items.iterator();
          while (iter.hasNext())
          {
			Item item = (Item) iter.next();
			item.shuffleResponse();
          }
          assessmentTaken.updateData(assessmentTakenXml.stringValue());
          assessmentDoc = assessmentTakenXml.getDocument();
        }
      }
    }
    else
    {
      //get the existing assessment;
      String assessmentTakenId = bean.getAssessmentTakenId();
      assessmentDoc =
        (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentTakenId);
    }

    return assessmentDoc;
  }
}
