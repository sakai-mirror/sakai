/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ToolbarSpacer.java,v 1.4 2004/06/22 03:11:03 ggolden Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * <p>Comment is an HTML renderer for the Sakai ToolbarSpacer tag in JSF.</p>
 * <p>This does not render children, but can deal with children by surrounding them in a comment.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class ToolbarSpacer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof org.sakaiproject.jsf.component.ToolbarSpacer);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.write("<span class=\"chefToolBarDisabled\">&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</span>");

		return;
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
	}

	public boolean getRendersChildren()
	{
		return false;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ToolbarSpacer.java,v 1.4 2004/06/22 03:11:03 ggolden Exp $
*
**********************************************************************************/
