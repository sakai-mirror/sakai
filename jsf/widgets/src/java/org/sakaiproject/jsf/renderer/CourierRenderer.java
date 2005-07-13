/**********************************************************************************
* $URL$
* $Id$
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

import org.sakaiproject.jsf.util.RendererUtil;


public class CourierRenderer extends Renderer
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


	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();

		// update time, in seconds
		String updateTime = (String) RendererUtil.getAttribute(context, component, "refresh");
		if (updateTime == null || updateTime.length() == 0)
		{
			updateTime = "10";
		}
		
		// the current tool's placement ID
		String placementId = (String) req.getAttribute("sakai.tool.placement.id");
		if (placementId == null)
		{
			// FIXME:
			// TODO: Report an error
		}
		writer.write("<script type=\"text/javascript\" language=\"JavaScript\">\n");
		writer.write("updateTime = " + updateTime + "000;\n");
		writer.write("updateUrl = \"" + serverUrl(req) + "/courier/" + placementId + "\";\n");
		writer.write("scheduleUpdate();\n");
		writer.write("</script>\n");
	}
	
	/** 
	 * This method is a duplicate of org.sakaiproject.util.web.Web.serverUrl()
	 * Duplicated here from org.sakaiproject.util.web.Web.java so that 
	 * the JSF tag library doesn't have a direct jar dependency on more of Sakai.
	 */
	private static String serverUrl(HttpServletRequest req)
	{
		StringBuffer url = new StringBuffer();
		url.append(req.getScheme());
		url.append("://");
		url.append(req.getServerName());
		if (((req.getServerPort() != 80) && (!req.isSecure())) || ((req.getServerPort() != 443) && (req.isSecure())))
		{
			url.append(":");
			url.append(req.getServerPort());
		}

		return url.toString();
	}
}

/**********************************************************************************
* $URL$
* $Id$
**********************************************************************************/
