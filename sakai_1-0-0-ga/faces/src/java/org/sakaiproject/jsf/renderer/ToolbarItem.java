/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ToolbarItem.java,v 1.4 2004/06/22 03:11:03 ggolden Exp $
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

package org.sakaiproject.jsf.renderer;

import java.io.IOException;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.html_basic.CommandLinkRenderer;

/**
 * <p>ToolbarItem is an HTML renderer for the ...</p>
 * <p>Like the defaul CommandLinkRenderer, except that we don't have children for the display portion of our link,
 * we use the value instead. </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class ToolbarItem extends CommandLinkRenderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof org.sakaiproject.jsf.component.ToolbarItem);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		// if disabled, don't render the link
		if (!isDisabled(component))
		{
			super.encodeBegin(context, component);
		}

		// setup the span for the "children" text
		else
		{
			ResponseWriter writer = context.getResponseWriter();
			writer.write("<span class=\"chefToolBarDisabled\">");
		}
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
		String label = "";
		Object value = ((UICommand) component).getValue();
		if (value != null)
		{
			label = value.toString();
		}

		ResponseWriter writer = context.getResponseWriter();
		writer.write(label);
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		// if disabled, don't render the link
		if (!isDisabled(component))
		{
			super.encodeEnd(context, component);
		}

		// end the span for the "children" text
		else
		{
			ResponseWriter writer = context.getResponseWriter();
			writer.write("</span>");
		}
	}

	/**
	 * Check if the component is disabled.
	 * @param component
	 * @return true if the component has a boolean "disabled" attribute set, false if not
	 */
	protected boolean isDisabled(UIComponent component)
	{
		boolean disabled = false;
		Object value = component.getAttributes().get("disabled");
		if (value != null)
		{
			if (value instanceof Boolean)
			{
				disabled = ((Boolean) value).booleanValue();
			}
			else
			{
				if (!(value instanceof String))
				{
					value = value.toString();
				}
				disabled = (new Boolean((String) value)).booleanValue();
			}
		}

		return disabled;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ToolbarItem.java,v 1.4 2004/06/22 03:11:03 ggolden Exp $
*
**********************************************************************************/