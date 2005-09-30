/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/tag/TitleBarTag.java,v 1.1 2005/04/27 14:30:05 janderse.umich.edu Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;

/**
 * <p>TitleBar is a custom Sakai tag for JSF, implementing a tool title bar.
 * Provides for integration with the Sakai Help tool (for context-sensitive help)</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class TitleBarTag extends UIComponentTag
{
	private String value = null;
	private String helpDocId = null;

	public String getComponentType()
	{
		return "org.sakaiproject.TitleBar";
	}

	public String getRendererType()
	{
		return "org.sakaiproject.TitleBar";
	}

	public String getValue()
	{
		return value;
	}
	
	public String getHelpDocId()
	{
		return helpDocId;
	}

	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "value", value);
		TagUtil.setString(component, "helpDocId", helpDocId);
	}

	public void setValue(String string)
	{
		value = string;
	}
	
	public void setHelpDocId(String string)
	{
		helpDocId = string;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/tag/TitleBarTag.java,v 1.1 2005/04/27 14:30:05 janderse.umich.edu Exp $
*
**********************************************************************************/
