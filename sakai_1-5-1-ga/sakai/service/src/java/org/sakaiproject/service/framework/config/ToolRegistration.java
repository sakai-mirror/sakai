/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/config/ToolRegistration.java,v 1.5 2004/12/10 15:26:01 casong.indiana.edu Exp $
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

// package
package org.sakaiproject.service.framework.config;

// imports
import java.util.List;

import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;

/**
* <p>ToolRegistration is the information about a tool available for use in a CHEF system.
* The id is used in Site's ToolConfigurations, and the properties hold some meta data about this tool.
* The default configuration parameters are held in the additional properties of this object.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public interface ToolRegistration
	extends Resource, Comparable
{
	/** @return The human readable Title of the tool.*/
	public String getTitle();

	/** @return A longer text Description of the tool.*/
	public String getDescription();

	/** @return A list of categories (String) for this tool.*/
	public List getCategories();
	
	/** @return The help registration xml file url. */
	public String getHelpUrl();

	/**
	* Access the set of configuration values that are the defaults for this tool.
	* @return The ResourceProperties that hold the set of configuration values that are the defaults for this tool.
	*/
	public ResourceProperties getDefaultConfiguration();

}	// DefaultToolConfiguration

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/config/ToolRegistration.java,v 1.5 2004/12/10 15:26:01 casong.indiana.edu Exp $
*
**********************************************************************************/
