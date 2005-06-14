/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ViewContainer.java,v 1.6 2004/08/07 22:53:17 ggolden.umich.edu Exp $
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
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.Setup;

/**
 * <p>ViewContainer is an HTML renderer which renders the head and body needed for an HTML view.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.6 $
 */
public class ViewContainer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		// this should be just UIViewRoot, but since that's not working now...
		return (component instanceof UIViewRoot) || (component instanceof UIOutput);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		// form the skin based on the site in the request (if any), and the defaults as configured
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
		String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-shared/css/");
		String siteId = ((ParameterParser) req.getAttribute(Setup.ATTR_PARAMS)).getString("site");
		String skin = skinRoot + SiteService.getSiteSkin(siteId);

		ResponseWriter writer = context.getResponseWriter();

		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n");
		writer.write("<head>\n");
		String title = (String) component.getAttributes().get("title");
		if (title != null)
		{
			writer.write("<title>");
			writer.write(title);
			writer.write("</title>\n");
		}

		// TODO: the links!
		writer.write("<link href=\"" + skin + "\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />\n");
		writer.write("<script type=\"text/javascript\" language=\"JavaScript\" src=\"/sakai-chef-tool/js/headscripts.js\">\n</script>\n");

		writer.write("</head>\n");

		writer.write("<body onload=\"");

		String pid = PortalService.getCurrentToolId();
		if (pid != null)
		{
			String element = escapeJavascript("Main" + pid);
			writer.write("setMainFrameHeight('" + element + "');");
		}
		
		writer.write("setFocus(focus_path);parent.updCourier(doubleDeep,ignoreCourier);\">\n");

		writer.write("<div class=\"chefPortletContainer\">\n");
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
		writer.write("</div></body></html>");
	}

	/**
	* Return a string based on value that is safe to place into a javascript / html identifier:
	* anything not alphanumeric change to 'x'. If the first character is not alphabetic, a
	* letter 'i' is prepended.
	* @param value The string to escape.
	* @return value fully escaped using javascript / html identifier rules.
	*/
	protected String escapeJavascript(String value)
	{
		if (value == null || value == "") return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			
			// prepend 'i' if first character is not a letter
			if(! java.lang.Character.isLetter(value.charAt(0)))
			{
				buf.append("i");
			}
			
			// change non-alphanumeric characters to 'x'
			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);
				if (! java.lang.Character.isLetterOrDigit(c))
				{
					buf.append("x");
				}
				else
				{
					buf.append(c);
				}
			}
			
			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			return value;
		}

	}	// escapeJavascript
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/renderer/ViewContainer.java,v 1.6 2004/08/07 22:53:17 ggolden.umich.edu Exp $
*
**********************************************************************************/
