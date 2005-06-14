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

package org.navigoproject.ui.web.asi.delivery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.ResultReporter;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.ui.web.asi.author.CoreAssessmentDisplayListing;
import org.navigoproject.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import osid.dr.Asset;
import osid.dr.AssetIterator;
import osid.dr.DigitalRepository;
import osid.dr.DigitalRepositoryException;
import osid.shared.Id;
import osid.shared.SharedException;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: XmlDeliveryService.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class XmlDeliveryService
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlDeliveryService.class);
//  public final static String ASSESSMENT_PATH =
//    "navigo_taken_assessment/questestinterop/assessment";
  public final static String QUESTTESTINTEROP_ASSESSMENT = "questestinterop/assessment";

  /**
   * Creates a new XmlDeliveryService object.
   */
  public XmlDeliveryService()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Document getAssessmentQTIInterop(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessmentQTIInterop( " + assessmentId + ")");
    }

    Document document = null;
    try
    {
      RepositoryManager rm = new RepositoryManager();
      Id assetId = rm.getId(assessmentId);
      Asset asset = rm.getAsset(assetId);
      Object content = asset.getContent();

      Assessment da = new Assessment((String) content);
//      da.recompose(true); // TODO get realized assessment shouldn't do recompose any more
      document = da.getDocument();

      LOG.debug("Assessment retrieved from DigitalRepository");
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return document;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Element getAssessment(String assessmentId)
  {
    Document document = this.getAssessmentQTIInterop(assessmentId);
    if(
      document.getDocumentElement().getNodeName().equals(
          QTIConstantStrings.ASSESSMENT))
    {
      return document.getDocumentElement();
    }
    else
    {
      Element assessment = null;
      NodeList nodes = document.getDocumentElement().getChildNodes();
      if(nodes.getLength() > 0)
      {
        assessment = (Element) nodes.item(0);
      }

      return assessment;
    }
  }
 
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Document getQtiResultReport(String assessmentId)
    throws DigitalRepositoryException, SharedException
  {
    Document document = ResultReporter.getAssessmentResult(assessmentId); 
    return document;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List getItemResult(String assessmentId)
  {
    List itemResultBeans = null;
    try
    {
      RepositoryManager rm = new RepositoryManager();
      Id aid = rm.getId(assessmentId);
      itemResultBeans = rm.getItemResults(aid);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SharedException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return itemResultBeans;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param username DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getAssets(String username)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssets(String " + username + ")");
    }

    Document document = null;
    ArrayList list = new ArrayList();

    // use the OsidLoader to get an instance of a Manager
    try
    {
      RepositoryManager rm = new RepositoryManager();
      DigitalRepository dr = rm.getDigitalRepository(Assessment.class);

      AssetIterator iter =
        dr.getAssetsBySearch(username, TypeLib.SEARCH_UNIQUE_ID);
      if(iter != null) {
        while(iter.hasNext())
        {
          Asset a = iter.next();
          if(a!=null){
            LOG.debug(a.getId());
            list.add(a);
          }
        }
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return list;
  }  
	
  /**
   * DOCUMENTATION PENDING
   *
   * @param username DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Document getCoreAssessments(String username)
    throws DigitalRepositoryException
  {
    LOG.debug("getAssessmentElements(String " + username + ")");
    Document assessments = XmlUtil.createDocument();
    Element root = assessments.createElement("assessments");
    assessments.appendChild(root);
    ArrayList assets = this.getAssets(username);
    ArrayList buildList = new ArrayList();
    int size = assets.size();
    
    for(int i = 0; i < size; i++)
    {    	
      Asset asset = (Asset) assets.get(i);
      // filter to only get core assessments
      if (!asset.getAssetType().isEqual(TypeLib.DR_QTI_ASSESSMENT)){
      	continue;
      }
      
      String assetName = asset.getDisplayName();
      String assetId = asset.getId().toString();
      
      // build list so we can sort
      CoreAssessmentDisplayListing cadl = new CoreAssessmentDisplayListing(assetName, assetId);
      buildList.add(cadl);
    }
    
    Collections.sort(buildList);
    size = buildList.size();
    for (int i = 0; i < size; i++){
			CoreAssessmentDisplayListing cadlTemp = (CoreAssessmentDisplayListing) buildList.get(i);
			Element assessment =
				assessments.createElement(QTIConstantStrings.ASSESSMENT);
			assessment.setAttribute(QTIConstantStrings.TITLE, cadlTemp.getAssessmentDisplayName());
			assessment.setAttribute(QTIConstantStrings.IDENT, cadlTemp.getCoreAssessmentIdString());
			root.appendChild(assessment);
    }

    return assessments;
  }
  
  public Document realizeAssessmentQTIInterop(String assessmentId, Calendar date)
  {
    Document document = null;
    try
    {
      RepositoryManager rm = new RepositoryManager();
      Id assetId = rm.getId(assessmentId);
      Asset asset = rm.getAsset(assetId);
      Object content = asset.getContent();

      Assessment da = new Assessment((String) content);
      da.realize(true, date);
      document = da.getDocument();

      LOG.debug("Assessment retrieved from DigitalRepository");
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return document;
  }

  /**
   * @param coreAssessmentId
   * @param createdDate
   * @return
   */
  public Document getAssessmentQTIInterop(String coreAssessmentId, Calendar createdDate)
  {

    Document document = null;
    try
    {
      RepositoryManager rm = new RepositoryManager();
      Id assetId = rm.getId(coreAssessmentId);
      Asset asset = rm.getAsset(assetId, createdDate);
      Object content = asset.getContent();

      Assessment da = new Assessment((String) content);
//      da.recompose(true); // TODO get realized assessment shouldn't do recompose any more
      document = da.getDocument();

      LOG.debug("Assessment retrieved from DigitalRepository");
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return document;
  }
  

}
