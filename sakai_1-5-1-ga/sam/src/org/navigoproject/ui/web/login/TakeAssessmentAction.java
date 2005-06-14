/*
 * Created on Jan 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.TypeLib;

import osid.OsidException;
import osid.OsidOwner;
import osid.authentication.AuthenticationException;
import osid.authorization.AuthorizationException;
import osid.authorization.Function;
import osid.authorization.Qualifier;
import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author ajpoland
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TakeAssessmentAction extends Action {
  
  private static final org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(TakeAssessmentAction.class);

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
      OsidOwner myOwner = OsidManagerFactory.getOsidOwner();
      
      osid.authorization.AuthorizationManager am = null;
      osid.shared.SharedManager sm = null;
      
      Agent agent = null;
              
      try {
        agent = OsidManagerFactory.getAgent();
      } catch (Exception e) {
        LOG.error(e); throw new Error(e);
      }
         
      
      try {
        am = OsidManagerFactory.createAuthorizationManager(myOwner);
        sm = OsidManagerFactory.createSharedManager(myOwner);
        
        
        
      } catch (OsidException e) {
              LOG.debug("Unable to instantiate OKI Managers");
      }
            
      //get the ID that was passed here
      Id assessmentId = null;
      try {
        assessmentId = sm.getId(request.getParameter("assessmentId"));
      } catch (SharedException e1) {
        LOG.debug("Unable to create ID from assessmentId");
      }
      
    
      LOG.debug("assessmentId = " + assessmentId);
      
      Qualifier qualifier = null;
      try {
        qualifier = am.createQualifier(assessmentId, "", "", null, null);
      } catch (AuthorizationException e2) {
        LOG.debug("Unable to create qualifier");
      }
       
      Id functionId = null;
      try {
        functionId = sm.createId();
      } catch (SharedException e) {
        LOG.error(e); throw new Error(e);
      }
      
     
      Function function = null;
      try {
        
        function = am.createFunction(functionId, "Execute", "Able to Execute Content", TypeLib.FUNCTION_ACL_HTTP_REQUEST, null);
      } catch (AuthorizationException e) {
        LOG.error(e); throw new Error(e);
      }
      
      
      
 /*     
      try {
        if (am.isAuthorized(agent.getId(), AuthorizationSupport.TAKE_ASSESSMENT.getId(), assessmentId)) {
          
          LOG.debug("Agent is authorized, start the assessment");
          //return mapping.findForward("START_ASSESSMENT");
          
          
       } else {
        
          //refuse, send to Login.do
          LOG.debug("Agent not authorized for this assessment");
          return mapping.findForward("LOGIN");
        
        }
      } catch (AuthorizationException e) {
        LOG.error(e); throw new Error(e);
      } catch (SharedException e) {
        LOG.error(e); throw new Error(e);
      }
      
     */ 
      
     
     

      return null;
    }


}