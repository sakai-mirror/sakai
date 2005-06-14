/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/ToolBean.java,v 1.2 2004/11/25 02:08:57 janderse.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.jsf;

/**
 * The ToolBean interface marks Sakai tool beans that use JSF.
 * A Sakai tool that uses JSF will have a tool bean that manages the tool state and 
 * performs the tool actions.  This bean must implement this ToolBean marker interface.
 * The tool bean should be registered in faces-config.xml with the "request" scope; but the 
 * SakaiVariableResolver will promote the ToolBean an implicit "tool" scope.  For example,
 * the tool bean org.sakaiproject.tool.annc.AnnouncementTool implements ToolBean.
 * 
 * The faces-config.xml file should look like:<xmp>
 * 	<managed-bean>
 *		<description>Tool Bean for announcements</description>
 *		<managed-bean-name>AnnouncementTool</managed-bean-name>
 *		<managed-bean-class>org.sakaiproject.tool.annc.AnnouncementTool</managed-bean-class>
 *		<managed-bean-scope>request</managed-bean-scope>
 *      .
 *      .
 *      .
 *  </managed-bean>
 * </xmp>
 */
public interface ToolBean {}


/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/ToolBean.java,v 1.2 2004/11/25 02:08:57 janderse.umich.edu Exp $
*
**********************************************************************************/

