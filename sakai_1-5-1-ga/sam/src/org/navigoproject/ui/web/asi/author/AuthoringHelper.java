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
 * Created on Aug 4, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.osid.assessment.impl.PublishingService;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.oroad.stxx.action.Action;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import osid.OsidException;
import osid.OsidOwner;

import osid.assessment.AssessmentException;
import osid.assessment.AssessmentManager;
import osid.authentication.AuthenticationException;

import osid.coursemanagement.CourseManagementException;
import osid.coursemanagement.CourseSection;
import osid.coursemanagement.CourseSectionIterator;
import osid.dr.Asset;
import osid.dr.DigitalRepository;

import osid.shared.Agent;
import osid.shared.AgentIterator;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class AuthoringHelper
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthoringHelper.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return Document
   */
  public Document getPublishedAssessment(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getPublishedAssessment(String " + assessmentId + ")");
    }
    Id id = this.getId(assessmentId);
    AssessmentManager assessmentManager = new AssessmentManagerImpl();
    try
    {
      osid.assessment.Assessment assessment = assessmentManager.getAssessment(id);
      if(assessment != null)
      {
        return ((Assessment)assessment.getData()).getDocument();
      }
    }
    catch (ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    return null;
    /**
    Document qtiAssessment =
      (new XmlDeliveryService()).getAssessmentQTIInterop(assessmentId);

    return qtiAssessment;
    **/
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fileName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public XmlStringBuffer readFile(String fileName)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readFile(String" + fileName + ")");
    }

    XmlStringBuffer xmlString = null;
    FileReader reader;
    String xmlExample;
    try
    {
      reader = new FileReader(fileName);
      StringWriter out = new StringWriter();
      int c;

      while((c = reader.read()) != -1)
      {
        out.write(c);
      }

      reader.close();
      xmlExample = (String) out.toString();
      xmlString = new XmlStringBuffer(xmlExample);
    }
    catch(FileNotFoundException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e1)
    {
      LOG.error(e1.getMessage(), e1);
    }

    return xmlString;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param strID DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Id getId(String strID)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getId( String" + strID + ")");
    }

    OsidOwner myOwner = new OsidOwner();
    Id id = null;
    try
    {
      SharedManager sharedManager =
        org.navigoproject.osid.OsidManagerFactory.createSharedManager(myOwner);
        id = sharedManager.getId(strID);
 
    }
    catch(OsidException e)
    {
      LOG.error(e.getMessage(), e);
      LOG.error("Error occured when creating Shared Manager");
    }

    return id;
  }

	public String getIdName(String strID)
		{
			Id  id = getId(strID);
			String nameId = "";
			OsidOwner myOwner = new OsidOwner();
			try
				 {
					 SharedManager sharedManager =
						 org.navigoproject.osid.OsidManagerFactory.createSharedManager(myOwner);
						  nameId = sharedManager.getAgent(id).getDisplayName();
				 }
				 catch(OsidException e)
				 {
					 LOG.error(e.getMessage(), e);
					 LOG.error("Error occured when creating Shared Manager");
				 }

			return nameId;
		}
 
 
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String createNewID()
  {
    LOG.debug("createNewID()");

    String strID = null;
    LOG.debug("getID");
    OsidOwner myOwner = new OsidOwner();
    Id id = null;
    try
    {
      SharedManager sharedManager =
        org.navigoproject.osid.OsidManagerFactory.createSharedManager(myOwner);
      id = sharedManager.createId();
      strID = id.getIdString();
    }
    catch(OsidException e)
    {
      LOG.error("Error occured when creating Shared Manager");
    }

    return strID;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessment DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public Asset createPublishedAssessment(
    Document publishedAssessment, HttpServletRequest request)
    throws Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createRealizedAssessment(  Document" + publishedAssessment +
        ", HttpServletRequest " + request + ")");
    }

    Asset asset = null;
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(publishedAssessment);

      // TODO Auto-generated method stub
      // Copy the given Assessment and create a new asset authorize the user to use it. 
      RepositoryManager rm = new RepositoryManager();
      DigitalRepository dr =
        rm.getDigitalRepository(
          org.navigoproject.business.entity.Assessment.class);

      NodeList list =
        publishedAssessment.getDocumentElement().getElementsByTagName(
          QTIConstantStrings.ASSESSMENT);
      String assessmentTitle =
        ((Element) list.item(0)).getAttribute(QTIConstantStrings.TITLE);
      String assessmentDescription = assessmentTitle;
      asset =
        dr.createAsset(
          assessmentTitle, assessmentDescription,
          org.navigoproject.osid.TypeLib.DR_QTI_ASSESSMENT);

      Id myAssetId = asset.getId();
      Id myDrId = dr.getId();

      LOG.debug("myAssetId:" + myAssetId);

      LOG.debug("myDrId:" + myDrId);

      Document doc = xsb.getDocument();
      Element root = doc.getDocumentElement();
      NodeList nodes = root.getElementsByTagName(QTIConstantStrings.ASSESSMENT);
      if(nodes.getLength() > 0)
      {
        Element assessment = (Element) nodes.item(0);
        assessment.setAttribute(QTIConstantStrings.IDENT, myAssetId.toString());
        xsb = new XmlStringBuffer(doc);
        asset.updateContent(xsb.stringValue());
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return asset;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public XmlStringBuffer readXMLDocument(InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Document document = null;
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);
    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.parse(inputStream);
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

    return new XmlStringBuffer(document);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRemoteUserName(HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getRemoteUserName(HttpServletRequest" + request + ")");
    }

    String remoteUser = null;
    try
    {
      remoteUser =
        org.navigoproject.osid.OsidManagerFactory.getAgent()
                                                .getDisplayName();			                                     
    }
    catch(SharedException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return remoteUser;
  }

	public Agent getRemoteUserAgent(HttpServletRequest request)
	{
		  Agent agent = null;
			try
      {
        agent=org.navigoproject.osid.OsidManagerFactory.getAgent();
      }
      catch (Exception e)
      {
				LOG.error(e.getMessage(), e);
      }
			return agent;			
	}
	
	public Id getRemoteUserID(HttpServletRequest request)
	 {
		 if(LOG.isDebugEnabled())
		 {
			 LOG.debug("getRemoteUserName(HttpServletRequest" + request + ")");
		 }

		Id id = null;
		 try
		 {
			  id = org.navigoproject.osid.OsidManagerFactory.getAgentId();                                    
		 }
		 catch(Exception e)
		 {
			 LOG.error(e.getMessage(), e);
       throw new Error(e);
		 }

		 return id;
	 }
	 
	public ArrayList changeDelimitedStringtoArray(String inputStr, String delimiter)
		{
			ArrayList selectedList = new ArrayList();
			if(inputStr != null && inputStr.trim().length() >0 )
			{
				StringTokenizer st = new StringTokenizer(inputStr, delimiter );
				if(st != null)
				{
			
				while(st.hasMoreTokens())
				{
					selectedList.add(st.nextToken());
				}
				}
				else
				{
					selectedList.add(inputStr);
				}
			}
			return selectedList;
		}
		
	public ArrayList getCourseGroup(HttpServletRequest request)
	 {   	
		 ArrayList courseGrp = new ArrayList();
		 AuthoringHelper authoringHelper = new AuthoringHelper();
		PublishingService publishingService = PublishingService.getInstance();
		 AgentIterator courseGroups =  publishingService.getCourseGroupList();
	
		 if(courseGroups != null)
		 { 
  	
				 try
				 {
					 while(courseGroups.hasNext())
					 {
						Agent cg = (Agent) courseGroups.next();
						 courseGrp.add(cg.getId().getIdString() +"+"+cg.getDisplayName());
					 }
				 }
				 catch (SharedException e)
				 {
					 LOG.error(e.getMessage(),e);
				 }  
		 		  
		 }
		 return courseGrp;
 
	 }

	 public ArrayList getCourseSections(HttpServletRequest request)
	 { 		
		 ArrayList courseSec = new ArrayList();
		 // Hard core some value to test it.
		 AuthoringHelper authoringHelper = new AuthoringHelper();
		PublishingService publishingService = PublishingService.getInstance();
		 CourseSectionIterator courseSections =  publishingService.getCourseSectionList(authoringHelper.getRemoteUserID(request));
		 if(courseSections != null)
		 { 
			try
		  
			 {
				 while(courseSections.hasNext())
				 {
					 try
					 {
						 CourseSection cs = (CourseSection) courseSections.next();
						 courseSec.add(cs.getId().getIdString()+"+"+ cs.getTitle());
					 }
					 catch (SharedException e1)
					 {
						 LOG.error(e1.getMessage(), e1);
					 }
				 }
			 }
			 catch (CourseManagementException e)
			 {
		  
				 LOG.error(e.getMessage(), e);
			 }
		 }
		 return courseSec;
	 
	 }
	 
	 
	 public String getCurrentDateAndTime()
	 {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    
				String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
				java.text.SimpleDateFormat sdf = 
							new java.text.SimpleDateFormat(DATE_FORMAT);
			 	sdf.setTimeZone(TimeZone.getDefault());          
				LOG.debug("Now : " + sdf.format(cal.getTime()));
				return sdf.format(cal.getTime());
	 }
	 
	 
}
