/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/Courier.java,v 1.1 2005/05/31 20:21:39 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.util.web.Web;

/**
 * <p>Courier renderer to place the Sakai Courier in the rendered view.</p>
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class Courier extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	public void decode(FacesContext context, UIComponent component)
	{
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
	}

	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();

		// update time, in seconds
		String updateTime = (String) component.getAttributes().get("refresh");
		if (updateTime == null || updateTime.length() == 0)
		{
			updateTime = "10";
		}
		
		// the current tool's placement
		Placement placement = ToolManager.getCurrentPlacement();

		writer.write("<script type=\"text/javascript\" language=\"JavaScript\">\n");
		writer.write("updateTime = " + updateTime + "000;\n");
		writer.write("updateUrl = \"" + Web.serverUrl(req) + "/courier/" + placement.getId() + "\";\n");
		writer.write("scheduleUpdate();\n");
		writer.write("</script>\n");
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/Courier.java,v 1.1 2005/05/31 20:21:39 ggolden.umich.edu Exp $
*
**********************************************************************************/
