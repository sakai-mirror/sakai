/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/tag/ToolbarSpacer.java,v 1.1 2005/03/31 04:16:55 ggolden.umich.edu Exp $
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

package org.sakaiproject.jsf.tag;

import javax.faces.webapp.UIComponentTag;

/**
 * <p>ToolbarItem is a custom Sakai tag for JSF, to place a toolbar item within a toolbar in the response.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ToolbarSpacer extends UIComponentTag
{
	public String getComponentType()
	{
		return "SakaiToolbarSpacer";
	}

	public String getRendererType()
	{
		return "SakaiToolbarSpacer";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/tag/ToolbarSpacer.java,v 1.1 2005/03/31 04:16:55 ggolden.umich.edu Exp $
*
**********************************************************************************/
