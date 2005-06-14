/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ToolbarMessage.java,v 1.5 2004/06/22 03:11:04 ggolden Exp $
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
 * <p>ToolbarMessage is a custom Sakai tag for JSF, to place a message in place of a toolbar.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.5 $
 */
public class ToolbarMessage extends UIComponentTag
{
	private String value = null;

	public String getComponentType()
	{
		return "SakaiToolbarMessage";
	}

	public String getRendererType()
	{
		return "SakaiToolbarMessage";
	}

	public String getValue()
	{
		return value;
	}

	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);

		if (getValue() != null)
		{
			if (isValueReference(getValue()))
			{
				component.setValueBinding("value", getFacesContext().getApplication().createValueBinding(getValue()));
			}
			else
			{
				component.getAttributes().put("value", getValue());
			}
		}
	}

	public void setValue(String string)
	{
		value = string;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ToolbarMessage.java,v 1.5 2004/06/22 03:11:04 ggolden Exp $
*
**********************************************************************************/
