/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/ToolConfigurationEdit.java,v 1.4 2004/06/22 03:14:51 ggolden Exp $
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
package org.sakaiproject.service.legacy.site;

// imports
import org.sakaiproject.service.legacy.resource.Edit;

/**
* <p>ToolConfigurationEdit is an editable ToolConfiguration.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface ToolConfigurationEdit
	extends Edit, ToolConfiguration
{
	/**
	* Set the id of the Tool that this configuration represents.
	* @param toolId The new tool id.
	*/
	public void setToolId(String toolId);

	/**
	* Set the tool's title.
	* @param title The new title.
	*/
	public void setTitle(String title);

	/**
	* Set the layout hints for this tool.
	* @param hints The new layout hints.
	*/
	public void setLayoutHints(String hints);

	/**
	* Move this tool one step towards the start of the order of tools in this page.
	*/
	public void moveUp();

	/**
	* Move this tool one step towards the end of the order of tools in this page.
	*/
	public void moveDown();

}	// ToolConfigurationEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/ToolConfigurationEdit.java,v 1.4 2004/06/22 03:14:51 ggolden Exp $
*
**********************************************************************************/
