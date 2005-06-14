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

package org.navigoproject.ui.web.asi.author;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.navigoproject.Constants;
import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.business.entity.properties.AssessmentAuthorHelper;
import org.navigoproject.data.AssessmentListing;
import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.impl.PublishingRequest;
import org.navigoproject.osid.assessment.impl.PublishingService;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.settings.PathInfo;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentActionForm;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;
import org.navigoproject.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import osid.authorization.AuthorizationException;
import osid.dr.DigitalRepositoryException;
import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.Type;

import com.oroad.stxx.action.Action;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: OKI based implementation
 * </p>
 *
 * <p>
 * Copyright: Copyright 2003 Trustees of Indiana University
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author <a href="">Rashmi Shastri</a>
 * @version $Id: SelectAuthoredAssessment.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class SelectAuthoredAssessment
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SelectAuthoredAssessment.class);

  // Qingru: just temporarily put two global variables here and set them to "true" at this time. 
  // Those dummy values could be changed accordingly later when the full definition for this kind of policy ready.
  static String TemplatesEnabled = "true";
  static String EditTemplate = "true";

  //Chen update
  static
  {
    try
    {
      Properties p =
        PathInfo.getInstance().getSettingsProperties(Constants.SETTINGS_FILE);
      if(p == null)
      {
        throw new Error("Could not find file: " + Constants.SETTINGS_FILE);
      }

      Boolean b = null;

      // disableTemplates
      b = new Boolean(p.getProperty("TemplatesEnabled", "true"));
      if(! b.booleanValue())
      {
        TemplatesEnabled = "false";
      }
    }
    catch(IOException e)
    {
      LOG.fatal(e);
      throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "execute(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest request, " +
        "HttpServletResponse response)");
    }

    String action = request.getParameter("action");
    AssessmentActionForm assessmentActionForm =
      (AssessmentActionForm) actionForm;
    AuthoringHelper authoringHelper = new AuthoringHelper();
    List listOfActivePublishedAssessments = new ArrayList();
    List listOfCompletedPublishedAssessments = new ArrayList();
    List listOfExpiredPublishedAssessments = new ArrayList();

    listOfActivePublishedAssessments =
      getPublishedAssessmentsByType(
        authoringHelper.getRemoteUserID(request),
        org.navigoproject.osid.TypeLib.FILTER_ACTIVE);

    //listOfCompletedPublishedAssessments=getPublishedAssessmentsByType(authoringHelper.getRemoteUserID(request),org.navigoproject.osid.TypeLib.FILTER_COMPLETED);
    listOfExpiredPublishedAssessments =
      getPublishedAssessmentsByType(
        authoringHelper.getRemoteUserID(request),
        org.navigoproject.osid.TypeLib.FILTER_EXPIRED);

    if(
      (request.getAttribute("Error") != null) &&
        request.getAttribute("Error").equals("Error"))
    {
      assessmentActionForm.setErrorMsg(
        request.getAttribute("ErrorID").toString());
    }

    Agent user = null;
    Document assessments = null;
    Document coreAssessments = null;
    String role = "";

    try
    {
      user = OsidManagerFactory.getAgent();
      try
      {
        coreAssessments =
          (new XmlDeliveryService()).getCoreAssessments(
            user.getId().getIdString());
      }
      catch(SharedException e1)
      {
        LOG.error(e1.getMessage(), e1);
      }

      assessments = getAuthorizedAssessments(user, request);
      ArrayList assessmentElements = getAssessmentElements(coreAssessments);

      //ArrayList assessmentPublishedElements =
      //  getAssessmentElements(assessments);// NO LONGER USED
      assessmentActionForm.setAssessmentPublishedElements(
        listOfActivePublishedAssessments);

      //assessmentActionForm.setAssessmentCompletedPublishedElements(listOfCompletedPublishedAssessments);
      assessmentActionForm.setAssessmentExpiredPublishedElements(
        listOfExpiredPublishedAssessments);
      assessmentActionForm.setAssessmentElements(assessmentElements);

      assessmentActionForm.setTemplatesEnabled(TemplatesEnabled);

      //Qingru: again, the following are dummy codes. They could be changed accordingly when the full role definition is ready.
      if(request.getParameter("roleSelection") != null)
      {
        role = request.getParameter("roleSelection");
        if(role.equals("Admin"))
        {
          EditTemplate = "true";
        }

        if(role.equals("Instructor"))
        {
          EditTemplate = "true";
        }

        if(role.equals("TA"))
        {
          EditTemplate = "true";
        }
        else
        {
          EditTemplate = "true";
        }
      }

      assessmentActionForm.setEditTemplate(EditTemplate);

      ArrayList assessmentTemplates = new ArrayList();
      AssessmentAuthorHelper authhelper = new AssessmentAuthorHelper();
      AssessmentTemplateIterator iter = null;

      if(authhelper != null)
      {
        iter = authhelper.getAssessmentTemplates();
      }

      if(iter != null)
      {
        while(iter.hasNext())
        {
          AssessmentTemplate template = (AssessmentTemplate) iter.next();
          if(template.getTemplateName() != null)
          {
            assessmentTemplates.add(
              template.getId().toString() + "+" + template.getTemplateName());
          }
        }
      }

      assessmentActionForm.setExistingTemplates(assessmentTemplates);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }

    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    LOG.debug("forward to input");
    this.saveDocument(request, assessments);
    XmlStringBuffer xsb = new XmlStringBuffer(coreAssessments);

    return mapping.findForward("main");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessments DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList getAssessmentElements(Document assessments)
  {
    //		return new ArrayList(
    //			assessments.getRootElement().getChildren(QTIConstantStrings.ASSESSMENT));
    ArrayList list = new ArrayList();
    NodeList nodes =
      assessments.getDocumentElement().getElementsByTagName(
        QTIConstantStrings.ASSESSMENT);
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Element testElem = (Element) nodes.item(i);
      list.add(testElem);
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param user DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Document getAuthorizedAssessments(
    Agent user, HttpServletRequest request)
  {
    Document assessments = XmlUtil.createDocument();
    Element assessmentElements =
      assessments.createElement("authorizedAssessments");
    assessments.appendChild(assessmentElements);

    AuthorizationSupport authorization = new AuthorizationSupport(request);
    try
    {
      Collection activeAssessments =
        authorization.getActiveAssessments(user, null);
      Collection inActiveAssessments =
        authorization.getInActiveAssessments(user, null);

      Element activeAssessmentElements =
        getAuthorizedAssessmentsElement(
          assessments, activeAssessments, "activeAssessments");
      Element inActiveAssessmentElements =
        getAuthorizedAssessmentsElement(
          assessments, inActiveAssessments, "inActiveAssessments");

      assessmentElements.appendChild(activeAssessmentElements);
      assessmentElements.appendChild(inActiveAssessmentElements);
    }
    catch(AuthorizationException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessmentId DOCUMENTATION PENDING
   * @param filterType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List getPublishedAssessmentsByType(
    Id publishedAssessmentId, Type filterType)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getPublishedAssessmentsByType(Id " + publishedAssessmentId +
        ", Type " + filterType + ")");
    }

    PublishingService publishingService = PublishingService.getInstance();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    ArrayList listOfPublishedAssessments = new ArrayList();
    Iterator publishedAssess =
      publishingService.getPublishedAssessments(
        publishedAssessmentId, filterType);
    while((publishedAssess != null) && publishedAssess.hasNext())
    {
      String publishedId = (String) publishedAssess.next();
      if((publishedId != null) && (publishedId.trim().length() > 0))
      {
        try
        {
          PublishingRequest pb =
            publishingService.getPublishedAssessmentSettings(
              authoringHelper.getId(publishedId));
          ArrayList internalElements = new ArrayList();
          ArrayList publishedto = new ArrayList();
          String beginDate = "--";
          String endDate = "--";

          // get Assigned to 	
          if(pb != null)
          {
            Iterator locations = pb.getAgentIdStrings();
            if(locations != null)
            {
              while(locations.hasNext())
              {
                publishedto.add(
                  authoringHelper.getIdName((String) locations.next()));
              }
            }

            // start date
            if(pb.getBeginDate() != null)
            {
              beginDate =
                (new Integer(pb.getBeginDate().get(Calendar.MONTH) + 1)).toString() +
                "/" +
                (new Integer(pb.getBeginDate().get(Calendar.DATE))).toString() +
                "/" +
                (new Integer(pb.getBeginDate().get(Calendar.YEAR))).toString();
            }

            //end date 
            if(pb.getDueDate() != null)
            {
              endDate =
                (new Integer(pb.getDueDate().get(Calendar.MONTH) + 1)).toString() +
                "/" +
                (new Integer(pb.getDueDate().get(Calendar.DATE))).toString() +
                "/" +
                (new Integer(pb.getDueDate().get(Calendar.YEAR))).toString();
            }

            PublishedAssessmentDisplayListing alTemp =
              new PublishedAssessmentDisplayListing();
            alTemp.setPublishedId(publishedId);
            alTemp.setCoreAssessmentIdString(pb.getCoreAssessmentIdString());
            alTemp.setAssessmentDisplayName(pb.getAssessmentDisplayName());
            alTemp.setPublishedTo(publishedto);
            alTemp.setBeginDate(beginDate);
            alTemp.setEndDate(endDate);
            
            // find out if any assessments have been submitted for this published id
            int i = PersistenceService.getInstance().getAssessmentQueries().getNumberOfRealizedAssessments(publishedId);
            boolean b = (i == 0) ? false : true;
            alTemp.setHasResponses(b);
            

            //				  internalElements.add(0,publishedId);	//element 1st Publishing ID
            //					internalElements.add(1,pb.getCoreAssessmentIdString());//element 2nd Core ID
            //					internalElements.add(2,pb.getAssessmentDisplayName());//element 3rd Assigned to
            //					internalElements.add(3,publishedto);
            //					internalElements.add(4,beginDate);//element 4th Start_date
            //					internalElements.add(5,endDate);//element 5th Due date
            //					listOfPublishedAssessments.add(internalElements);
            listOfPublishedAssessments.add(alTemp);
          }
        }
        catch(Exception e)
        {
          LOG.warn("Unable to find published assessment " + publishedId);
        }
      }
    }

    Collections.sort(listOfPublishedAssessments);

    return listOfPublishedAssessments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessments DOCUMENTATION PENDING
   * @param authorizedName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Element getAuthorizedAssessmentsElement(
    Document ower, Collection assessments, String authorizedName)
  {
    Element assessmentElements = ower.createElement(authorizedName);
    if(assessments != null)
    {
      Iterator iterator = assessments.iterator();
      while(iterator.hasNext())
      {
        AssessmentListing listing = (AssessmentListing) iterator.next();
        Element assessment = ower.createElement(QTIConstantStrings.ASSESSMENT);
        assessment.setAttribute(
          QTIConstantStrings.IDENT, listing.getAssessmentAssetId().toString());
        assessment.setAttribute(
          QTIConstantStrings.TITLE, listing.getDisplayName());
        assessment.setAttribute("description", "" + listing.getDescription());
        
        Date effectiveDate = null,
             expirationDate = null;
        
        if (listing.getEffectiveDate() != null){
          effectiveDate = listing.getEffectiveDate().getTime();  
        }
        
        if (listing.getExpirationDate() != null){
          expirationDate = listing.getExpirationDate().getTime();  
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        
        if (effectiveDate != null){
          assessment.setAttribute("effectiveDate", sdf.format(effectiveDate));
        }
        
        if (expirationDate != null){
          assessment.setAttribute("expirationDate", sdf.format(expirationDate));
        }

        //				assessmentElements.addContent(assessment);
        assessmentElements.appendChild(assessment);
      }
    }

    return assessmentElements;
  }
}
