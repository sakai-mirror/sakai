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


/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: AssessmentQueries.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.hibernate.Hibernate;

import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.framework.Constants;
import org.sakaiproject.framework.ThreadLocalMapProvider;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import osid.OsidException;
import osid.authorization.Authorization;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 * @author jlannan
 */
public class AssessmentQueries
  extends HibernateDaoSupport
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentQueries.class);
  private DigitalRepositoryManager digitalRepositoryManager;
  private SharedManager sharedManager;
  
  public static final String PUBLISHED_KEY = "published_assessments";
  public static final String LATE_HANDLING_KEY = "late_handling_assessments";
  public static final String REVIEWABLE_ASSESSMENTS_TAKEN = "reviewable_assessments_taken";
	public static final String NONREVIEWABLE_ASSESSMENTS_TAKEN = "nonreviewable_assessments_taken";

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Map getTakeAssessmentMap()
  {
  	
  	LOG.debug("getTakeAssessmentMap()");
  	
    HttpServletRequest request =
      (HttpServletRequest) ThreadLocalMapProvider.getMap().get(
        Constants.HTTP_SERVLET_REQUEST);

    if(request == null)
    {
      throw new Error("request is null");
    }

    Agent user;

    int maxAttempts = 0;    

    Iterator aiAvailable;
    Iterator aiTakeable;
    Iterator aiFeedback;
    
    Collection collTakeable;
    Collection collAvailable;
    Collection collFeedback;

    Collection al = new ArrayList();
		Collection al2 = new ArrayList();		
		Collection al3 = new ArrayList();
		Collection al4 = new ArrayList();

    AuthorizationSupport authSupport = new AuthorizationSupport(request);

    Map mapTakeAssessments = new HashMap(4);
    
    String courseId = null;

    try
    {    	
			boolean bFilterByCourse = false;			
			if (ThreadLocalMapProvider.getMap().get(Constants.COURSE_ID) != null){
				bFilterByCourse = true;				
				
			}
						    	
      user = OsidManagerFactory.getAgent();
      collTakeable = authSupport.getTakeableAuthorizations(user, bFilterByCourse);
      collAvailable = authSupport.getAvailableAuthorizations(user, bFilterByCourse);
      collFeedback = authSupport.getFeedbackAuthorizations(user, bFilterByCourse);
                  
      /**** get Published Assessments which are Takeable and submitted realized count < maxattempts ****/
      
      aiTakeable = collTakeable.iterator();
      // filter out authorizations not in range            
      while(aiTakeable.hasNext()){
        LOG.debug(" in aiTakeable iter ");
        Authorization tempAuthorization = (Authorization) aiTakeable.next();
        
        // if begin date and due date are both not null and now is not between these dates then remove from Collection
				Calendar beginDate = tempAuthorization.getEffectiveDate();
				Calendar dueDate = tempAuthorization.getExpirationDate();								
				Calendar calNow = Calendar.getInstance();

				boolean b = (!(calNow.after(beginDate) && calNow.before(dueDate)));
				
				if (beginDate != null && dueDate != null){
					//Calendar calNow = Calendar.getInstance();
					if (!(calNow.after(beginDate) && calNow.before(dueDate))){
					  aiTakeable.remove();	
					}
				}         						
      }
      
      // reset iterator
      aiTakeable = collTakeable.iterator();
      
      List tempList = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBeanList(aiTakeable);
      LOG.debug(tempList);
      Iterator iterList = tempList.iterator();
      while(iterList.hasNext())
      {      	      	
      	QtiSettingsBean qsb = (QtiSettingsBean) iterList.next();                                	                                    
                   
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(qsb.getCreatedDate().getTime());                                      
        
        //  get max attempts for published assessment        
        maxAttempts = getMaxAttempts(qsb.getId());

        // get creation time for authorization
        //Calendar creationTime = tempAuthorization.getEffectiveDate();

        // add to list if criteria is met
        LOG.debug("getNumberOfRealizedAssessments(assetId): " + getNumberOfRealizedAssessments(qsb.getId()));
        LOG.debug("maxAttempts: " + maxAttempts);
                
        if(getNumberOfRealizedAssessments(qsb.getId()) < maxAttempts)
        {
          LOG.debug("Adding qtisettingsbean=" + qsb.getId());          
          al.add(qsb);
        }               
      }
      
      //Collections.sort((ArrayList) al);  
			mapTakeAssessments.put(AssessmentQueries.PUBLISHED_KEY, al);
			
			

//      /**** get Late handling assessments which are available then subtract published assessments from above ****/
//      while(aiAvailable.hasNext())
//      {
//        Authorization tempAuth = (Authorization) aiAvailable.next();
//        Id publishedId = tempAuth.getQualifier().getId();        
//
//        // if begin date and due date are both not null and now is not between these dates then continue
//				Calendar beginDate = tempAuth.getEffectiveDate();
//				Calendar endDate = tempAuth.getExpirationDate();
//				
//				if (beginDate != null && endDate != null){
//				  Calendar calNow = Calendar.getInstance();
//				  if (!(calNow.after(beginDate) && calNow.before(endDate))){
//					  continue;	
//				  }
//				}
//
//        // get creation time for authorization
//        Calendar creationTime = tempAuth.getEffectiveDate();
//
//				QtiSettingsBean qsb = null;
//        
//        // add to list if criteria is met
//        if(hasLateHandling(publishedId))
//        {
//          // add to list if not in first set
//          boolean addToList = true;
//          Collection tempAL = (ArrayList) mapTakeAssessments.get(AssessmentQueries.PUBLISHED_KEY); 
//          Iterator i = tempAL.iterator();
//          
//          while(i.hasNext())
//          {
//            qsb = (QtiSettingsBean) i.next();
//            if(publishedId == getSharedManager().getId(qsb.getId()))
//            {
//              addToList = false;
//              break;
//            }
//          }
//
//          if(addToList)
//          {
//            al2.add(qsb);
//          }
//        }
//      }
//			Collections.sort((List) al2);
      mapTakeAssessments.put(AssessmentQueries.LATE_HANDLING_KEY, al2);
              

      /**** get Feedback submitted realized assessments ****/
      aiFeedback = collFeedback.iterator();
      Collection listFeedback = null;
      while(aiFeedback.hasNext())
      {
        Authorization tempAuth = (Authorization) aiFeedback.next();
        Id publishedId = tempAuth.getQualifier().getId();
        
        // if begin date and due date are both not null and now is not between these dates then continue
				Calendar beginDate = tempAuth.getEffectiveDate();
				Calendar endDate = tempAuth.getExpirationDate();
				
				if (beginDate != null && endDate != null){
				  Calendar calNow = Calendar.getInstance();
				  if (!(calNow.after(beginDate) && calNow.before(endDate))){
					  aiFeedback.remove();					  
					}
				}        					
      }

      // reset iterator
      aiFeedback = collFeedback.iterator();
      // get all taken realized assessment for all publishedId      
      listFeedback = PersistenceService.getInstance().getQtiQueries().returnTakenRealizationList(aiFeedback);        			
      			
      mapTakeAssessments.put(AssessmentQueries.REVIEWABLE_ASSESSMENTS_TAKEN, listFeedback);      


//      /**** get all available submitted realized assessments then subtract late handling assessments set ****/
//      Collection collectionAllAvailable = new ArrayList();
//      while(aiAvailable.hasNext())
//      {
//        Authorization tempAuth = (Authorization) aiAvailable.next();
//        RealizationBean rb = null;
//        Id publishedId = tempAuth.getQualifier().getId();
//                
//        List l = PersistenceService.getInstance().getQtiQueries().getTakenRealizations(publishedId);
//				collectionAllAvailable.addAll(l);                          
//      }
//      
//      // now take all available submitted realized assessments and subtract late handling realized assessments
//			Collection collectionLateHandling = (ArrayList) mapTakeAssessments.get(AssessmentQueries.REVIEWABLE_ASSESSMENTS_TAKEN);
//      Iterator i = collectionAllAvailable.iterator();
//      while(i.hasNext()){
//      	RealizationBean rb = (RealizationBean) i.next();
//      	if (collectionLateHandling.contains(rb)){
//      		al4.add(rb);
//      	}      	
//      }                            					
//      mapTakeAssessments.put(AssessmentQueries.NONREVIEWABLE_ASSESSMENTS_TAKEN, al4);
      
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }

    return mapTakeAssessments;
  }

 

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean hasLateHandling(Id publishedId)
  {
    
    List l = getHibernateTemplate().find("select q from QtiSettingsBean as q where q.id = ?", publishedId.toString(), Hibernate.STRING);
               
    Iterator i = l.iterator();
    if(i.hasNext())
    {
      Character tempCh = ((QtiSettingsBean) i.next()).getLateHandling();
      if (tempCh == null){      
        return false;
      }  
      else if(tempCh.toString().equalsIgnoreCase("T"))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    else{
			return false;
    }    
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getMaxAttempts(String assetId)
  {
  	LOG.debug("getMaxAttmpts(" + assetId + ")");
    String query = "select q from QtiSettingsBean as q where q.id = ?";
    List l =
      getHibernateTemplate().find(query, assetId, Hibernate.STRING);
    Iterator i = l.iterator();
    if(i.hasNext())
    {
      try
      {
        int returnInt = ((QtiSettingsBean) i.next()).getMaxAttempts().intValue();
        if (returnInt == 0){
          returnInt = Integer.MAX_VALUE;
        }
        return returnInt;
      }
      catch(NumberFormatException e)
      {
        throw new Error(e);
      }
    }
    else
    {
      return 0;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getNumberOfRealizedAssessments(String publishedId)
  {
    
    Iterator i = null;
    String query =
      "select count(*) from RealizationBean as r where r.assessmentPublishedId = ? and submitted = 1";
    
    List l =
      getHibernateTemplate().find(query, publishedId, Hibernate.STRING);
        
    i = l.iterator();
    if(i.hasNext())
    {
      return ((Integer) i.next()).intValue();
    }
    else{
      throw new Error("list should have next element");
    }
                        
//    try
//    {    
//      return ((Integer) this.getSession()
//                           .iterate(
//        query, assetId.toString(), Hibernate.STRING).next()).intValue();
//    }
//    catch(HibernateException e)
//    {
//      throw new Error(e);
//    }		
//    finally{
//      try{
//        this.getSession().close();
//      }
//      catch(HibernateException e){
//        throw new Error(e);
//      }
//    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  private DigitalRepositoryManager getDigitalRepositoryManager()
    throws DigitalRepositoryException
  {
    if(digitalRepositoryManager == null)
    {
      try
      {
        digitalRepositoryManager =
          org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager();
      }
      catch(OsidException e)
      {
        LOG.warn("Unexpected OsidException: " + e.getMessage());
        throw new DigitalRepositoryException(e.getMessage());
      }
    }

    return digitalRepositoryManager;
  }
  
  private SharedManager getSharedManager()
    throws SharedException
  {
    if(sharedManager == null)
    {
      try
      {
        sharedManager =
          org.navigoproject.osid.OsidManagerFactory.createSharedManager();
      }
      catch(OsidException e)
      {
        LOG.warn("Unexpected OsidException: " + e.getMessage());
        throw new SharedException(e.getMessage());
      }
    }

    return sharedManager;
  }  
}
