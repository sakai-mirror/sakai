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
 * <p>
 * Copyright: Copyright 2003 Trustees of Indiana University
 * </p>
 */
package org.navigoproject.osid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.navigoproject.data.AssessmentListing;
import org.sakaiproject.framework.Constants;
import org.sakaiproject.framework.ThreadLocalMapProvider;

import osid.OsidException;
import osid.authorization.Authorization;
import osid.authorization.AuthorizationException;
import osid.authorization.AuthorizationIterator;
import osid.authorization.AuthorizationManager;
import osid.authorization.Qualifier;
import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;
import osid.shared.Agent;
import osid.shared.Group;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 * DOCUMENT ME!
 * 
 * @author rpembry simplified wrapper for OKI Authorization interface (plus some
 *         Authentication)
 * @author Lance Speelmon
 * @version $Id: AuthorizationSupport.java,v 1.9 2004/05/28 21:25:50
 *          lancespeelmon Exp $
 */
public class AuthorizationSupport
{

  private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
      .getLogger(AuthorizationSupport.class);

  //TODO owner may need Request here
  private static SharedManager sm;

  private static AuthorizationManager am;

  public static Qualifier REALIZED_ASSESSMENT;

  public static Id realizedAssessmentRootQualifier;

  static{
		try
	  {
		  sm = OsidManagerFactory.createSharedManager();
			am = OsidManagerFactory.createAuthorizationManager();
			realizedAssessmentRootQualifier = sm.getId("ACL_CREATE_REALIZED");

      REALIZED_ASSESSMENT = am.getQualifier(realizedAssessmentRootQualifier);
      
      if (REALIZED_ASSESSMENT == null){
				REALIZED_ASSESSMENT = am.createRootQualifier(
				realizedAssessmentRootQualifier,
				"Realized Assessment Root Qualifier", "",
				TypeLib.QUALIFIER_REALIZED_ASSESSMENT, null);
      }
      
			
	  } catch (AuthorizationException e)
		{
			LOG
					.fatal("Unable to handle AuthorizationException when creating Functions");
			throw new Error(e.getMessage());
		} catch (SharedException e)
		{
			LOG.fatal("Unable to handle SharedException when creating Functions");
			throw new Error(e.getMessage());
		} catch (OsidException e)
		{
			LOG.fatal("Unable to handle OsidException when creating Functions");
				throw new Error(e.getMessage());
		}  	
  }

  /**
   * Creates a new AuthorizationSupport object.
   * 
   * @param request
   *          DOCUMENTATION PENDING
   * 
   * @throws Error
   *           DOCUMENTATION PENDING
   */
  public AuthorizationSupport(HttpServletRequest request)
  {
    //OsidOwner myOwner = OsidManagerFactory.getOsidOwner();
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @param request
   *          DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   */
  public Qualifier getQualifier(HttpServletRequest request)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getQualifier(" + request + ")");
    }

    Qualifier result = null;

    return result;
  }

  /**
   * Grants the named agent permission to take the specified exam during the
   * specified date ranges
   * 
   * @param agent
   * @param realizedAssessment
   * @param effectiveDate
   * @param expirationDate
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws AuthorizationException
   * @throws DigitalRepositoryException
   */
  public Authorization authorizeAssessment(Agent agent,
      Asset realizedAssessment, Calendar effectiveDate, Calendar expirationDate)
      throws AuthorizationException, DigitalRepositoryException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("authorizeAssessment(" + agent + ", " + realizedAssessment
          + ", " + effectiveDate + ", " + expirationDate + ")");
    }

    if (realizedAssessment == null)
    {
      throw new DigitalRepositoryException("can't authorize null Assessment");
    }

    Qualifier qualifier = am.createQualifier(realizedAssessment.getId(),
        realizedAssessment.getDisplayName(), realizedAssessment
            .getDescription(), TypeLib.QUALIFIER_REALIZED_ASSESSMENT,
        REALIZED_ASSESSMENT.getId());

    LOG.debug("Newly created Qualifier Id is: " + qualifier.getId());
    LOG.debug("realized Assessment Id is: " + realizedAssessment.getId());

    try
    {
      return am.createDatedAuthorization(agent.getId(),
          FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(),
          effectiveDate, expirationDate);
    } catch (SharedException e)
    {
      LOG.fatal("Wrapping Error around unhandled Exception: " + e.getMessage());
      throw new AuthorizationException(e.getMessage());
    }
  }

  /**
   * Retrieves pending assessments for a given Agent. If group is null, it
   * returns all assessments; otherwise, it filters to assessments correpsonding
   * to the group. Typically, group would be a particular Course or Course
   * Section.
   * 
   * @param agent
   * @param group
   * 
   * @return Collection of AssessmentListings
   * 
   * @throws AuthorizationException
   *           DOCUMENTATION PENDING
   */
  public Collection getActiveAssessments(Agent agent, Group group)
      throws AuthorizationException
  {
    return getAssessmentsHelper(agent, group, true);
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @param agent
   *          DOCUMENTATION PENDING
   * @param group
   *          DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws AuthorizationException
   *           DOCUMENTATION PENDING
   */
  public Collection getInActiveAssessments(Agent agent, Group group)
      throws AuthorizationException
  {
    return getAssessmentsHelper(agent, group, false);
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @param agent
   *          DOCUMENTATION PENDING
   * @param group
   *          DOCUMENTATION PENDING
   * @param isActiveNow
   *          DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws AuthorizationException
   *           DOCUMENTATION PENDING
   * @throws Error
   *           DOCUMENTATION PENDING
   */
  private Collection getAssessmentsHelper(Agent agent, Group group,
      boolean isActiveNow) throws AuthorizationException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAssessmentsHelper(" + agent + ", " + group + ", "
          + isActiveNow + ")");
    }

    Collection result = new ArrayList();
    AuthorizationIterator iter;

    if (agent == null)
    {
      throw new AuthorizationException("refusing to process null Agent");
    }

    try
    {
      if (group == null)
      {
        iter = am.getExplicitAZs(agent.getId(), FunctionLib.TAKE_ASSESSMENT
            .getId(), null, isActiveNow);
      } else
      {
        iter = am.getExplicitAZs(group.getId(), FunctionLib.TAKE_ASSESSMENT
            .getId(), null, isActiveNow);
      }
    } catch (SharedException e)
    {
      LOG.fatal("Wrapping Error around unhandled Exception: " + e.getMessage());
      throw new Error(e.getMessage());
    }

    Authorization authz;
    Qualifier qualifier;
    while (iter.hasNext())
    {
      LOG.debug("looping through Explicit authorizations");
      authz = (Authorization) iter.next();
      qualifier = authz.getQualifier();
      if (group != null) //ensure this Agent hasn't already taken it:
      {
        try
        {
          if (!am.isAuthorized(agent.getId(), FunctionLib.TAKE_ASSESSMENT
              .getId(), qualifier.getId()))
          {
            continue;
          }
        } catch (SharedException e1)
        {
          LOG.fatal("Wrapping Error around unhandled Exception: "
              + e1.getMessage());
          throw new Error(e1.getMessage());
        }
      }

      result.add(
      // note that the dates returned are that of the authorization
          new AssessmentListing(qualifier.getId(), //Note that this
              // intentionally casts a
              // qualifier id to an Asset
              // Id
              qualifier.getDisplayName(), qualifier.getDescription(), authz
                  .getEffectiveDate(), authz.getExpirationDate()));
    }

    return result;
  }

  public Collection getTakeableAuthorizations(Agent agent, boolean bFilterByCourse)
  {
    String courseId = null;
    AuthorizationIterator iter = null;
    Collection returnCollection = new ArrayList();                             

    try
    {
      iter = am.getExplicitAZs(agent.getId(), FunctionLib.TAKE_ASSESSMENT
          .getId(), null, true);          
          
      if (bFilterByCourse){      	
				courseId = (String) ThreadLocalMapProvider.getMap().get(Constants.COURSE_ID);			  
			   
      	while(iter.hasNext()){
      		Authorization tempAuth = (Authorization) iter.next();
      		if (tempAuth.getAgent().getDisplayName().trim().equals(courseId.trim())){
						returnCollection.add(tempAuth);
      		}
      	}
      }
      else{
      	while(iter.hasNext()){
					returnCollection.add((Authorization) iter.next());
      	}
      }
      
    } catch (Exception e)
    {
      throw new Error(e);
    }
    
    return returnCollection;    
  }

  public Collection getFeedbackAuthorizations(Agent agent, boolean bFilterByCourse)
  {
		String courseId = null;
    AuthorizationIterator iter = null;
		Collection returnCollection = new ArrayList();		

    try
    {
      iter = am.getExplicitAZs(agent.getId(),
          FunctionLib.VIEW_ASSESSMENT_FEEDBACK.getId(), null, true);
          
			if (bFilterByCourse){      	
			  courseId = (String) ThreadLocalMapProvider.getMap().get(Constants.COURSE_ID);	
			   
				while(iter.hasNext()){
				  Authorization tempAuth = (Authorization) iter.next();
					if (tempAuth.getAgent().getDisplayName().trim().equals(courseId.trim())){
						returnCollection.add(tempAuth);
					}
				}
			}  
			else{
				while(iter.hasNext()){
				  returnCollection.add((Authorization) iter.next());
				}     
			}
    } catch (Exception e)
    {
      throw new Error(e);
    }

    return returnCollection;
  }

  public Collection getAvailableAuthorizations(Agent agent, boolean bFilterByCourse)
  {

    // Combine both TAKE_ASSESSMENT and VIEW_ASSESSMENT_FEEDBACK
		String courseId = null;
    AuthorizationIterator iter;    
		Collection returnCollection = new ArrayList();

    try
    {
      iter = am.getExplicitAZs(agent.getId(), FunctionLib.AVAILABLE_ASSESSMENT
          .getId(), null, true);
          
			if (bFilterByCourse){      	
			  courseId = (String) ThreadLocalMapProvider.getMap().get(Constants.COURSE_ID);			  	
			   
				while(iter.hasNext()){
				  Authorization tempAuth = (Authorization) iter.next();
					if (tempAuth.getAgent().getDisplayName().trim().equals(courseId.trim())){
						returnCollection.add(tempAuth);
					}
				}
			}    
			else{
				while(iter.hasNext()){
					returnCollection.add((Authorization) iter.next());
				}     
			}      
    } catch (Exception e)
    {
      throw new Error(e);
    }
        
    return returnCollection;
  }

  public boolean isAuthorizedToTakeAssessment(String publishedId)
      throws org.navigoproject.business.exception.AuthorizationException
  {
    try
    {
      Id pid = OsidManagerFactory.createSharedManager().getId(publishedId);
      return isAuthorizedToTakeAssessment(pid);
    } catch (OsidException e)
    {
      LOG.error(e);
      throw new org.navigoproject.business.exception.AuthorizationException(e);
    }
  }

  public boolean isAuthorizedToTakeAssessment(Id publishedId)
      throws org.navigoproject.business.exception.AuthorizationException
  {
  	
  	LOG.debug("isAuthorizedToTakeAssessment(" + publishedId + ")");
    try
    {
      return OsidManagerFactory.createAuthorizationManager().isUserAuthorized(
          FunctionLib.TAKE_ASSESSMENT.getId(), publishedId);
    } catch (OsidException e)
    {
      LOG.error(e);
      throw new org.navigoproject.business.exception.AuthorizationException(e);
    }
  }
}