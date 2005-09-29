/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
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
package org.sakaiproject.tool.assessment.integration.helper.ifc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;

/**
 *
 * <p>Description:
 * This is a context implementation helper delegate interface for
 * the AgentFacade class.  Using Spring injection via the integrationContext.xml
 * selected by the build process to find the implementation.
 * </p>
 * <p>Sakai Project Copyright (c) 2005</p>
 * <p> </p>
 * @author Ed Smiley <esmiley@stanford.edu>
 */

public interface AgentHelper
{
  public AgentImpl getAgent();

  public String getAgentString();

  public String getAgentString(HttpServletRequest req, HttpServletResponse res);

  public String getDisplayName(String agentS);

  public String getFirstName(String agentString);

  public String getLastName(String agentString);

  public String getRole(String agentString); // for static call

  public String getRoleForCurrentAgent(String agentString); // for instance call

  public String getCurrentSiteId();

  public String getCurrentSiteName();

  public String getSiteName(String siteId);

  public String getDisplayNameByAgentId(String agentId);

  public String createAnonymous();

  public boolean isStandaloneEnvironment();

  public boolean isIntegratedEnvironment();

  public String getCurrentSiteIdFromExternalServlet(HttpServletRequest req,
    HttpServletResponse res);

  public String getAnonymousId();


}