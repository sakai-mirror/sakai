/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package org.sakaiproject.tool.gradebook.facades.sakai2impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;
import org.sakaiproject.tool.gradebook.facades.ContextManagement;

/**
 * Computes the default dispatch path for the user's role-appropriate view
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class JsfTool extends org.sakaiproject.jsf.util.JsfTool {
    private static Log logger = LogFactory.getLog(JsfTool.class);
	protected String computeDefaultTarget() {
        if(logger.isInfoEnabled()) logger.info("Entering gradebook... determining role appropriate view");

        ApplicationContext ac = (ApplicationContext)getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        Authn authnService = (Authn)ac.getBean("org_sakaiproject_tool_gradebook_facades_Authn");
        Authz authzService = (Authz)ac.getBean("org_sakaiproject_tool_gradebook_facades_Authz");
        GradebookService gradebookService = (GradebookService)ac.getBean("org.sakaiproject.service.gradebook.GradebookService");
        ContextManagement contextManagementService = (ContextManagement)ac.getBean("org_sakaiproject_tool_gradebook_facades_ContextManagement");

        String userUid = authnService.getUserUid();
        String gradebookUid = contextManagementService.getGradebookUid(null);

		// If the Gradebook doesn't exist, give up.
		if(!gradebookService.gradebookExists(gradebookUid)) {
			throw new RuntimeException("Gradebook " + gradebookUid + " doesn't exist");
		}

        String target;
        if(authzService.isUserAbleToGrade(gradebookUid)) {
            if(logger.isInfoEnabled()) logger.info("Sending user to the overview page");
            target = "/overview";
        } else if (authzService.isUserAbleToViewOwnGrades(gradebookUid)) {
            if(logger.isInfoEnabled()) logger.info("Sending user to the student view page");
            target = "/studentView";
        } else {
            // The role filter has not been invoked yet, so this could happen here
            throw new RuntimeException("User " + userUid + " attempted to access gradebook " + gradebookUid + " without any role");
        }
        if(logger.isInfoEnabled()) logger.info("target = " + target);
        return target;
    }
}


