/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ViewContainer.java,v 1.3 2005/02/15 00:26:02 janderse.umich.edu Exp $
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

/**
 * <p>ViewContainer is ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class ViewContainer extends UIComponentTag
{
	private String m_title = null;
	private String m_rendered = null;

	public String getComponentType()
	{
		return "SakaiViewContainer";
	}

	public String getRendererType()
	{
		return "SakaiViewContainer";
	}

	public String getTitle()
	{
		return m_title;
	}

	public String getRendered()
	{
		return m_rendered;
	}

	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		if (getTitle() != null)
		{
			if (isValueReference(getTitle()))
			{
				component.setValueBinding("title", getFacesContext().getApplication().createValueBinding(getTitle()));
			}
			else
			{
				component.getAttributes().put("title", getTitle());
			}
		}
		
		if (getRendered() != null)
		{
			if (isValueReference(getRendered()))
			{
				component.setValueBinding("rendered", getFacesContext().getApplication().createValueBinding(getRendered()));
			}
			else
			{
				component.getAttributes().put("rendered", getRendered());
			}
		}		
	}

	public void setTitle(String string)
	{
		m_title = string;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ViewContainer.java,v 1.3 2005/02/15 00:26:02 janderse.umich.edu Exp $
*
**********************************************************************************/
