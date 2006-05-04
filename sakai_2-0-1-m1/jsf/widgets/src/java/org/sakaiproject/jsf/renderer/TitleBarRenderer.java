/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/TitleBarRenderer.java,v 1.4 2005/05/28 03:04:30 ggolden.umich.edu Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;

//import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;

/**
 * TODO: Renderer should NOT be interacting with the Sakai ServerConfigurationService!
 * It should be configured through the component attributes.  The component attributes
 * should come from the tag attributes.  The tag attributes might be configured
 * through a Sakai service, but that is inside the tool, _not_ the generic renderer.
 */
public class TitleBarRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 */
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		writer.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"toolTitle\" summary=\"layout\">\n<tr>\n");
		writer.write("\t<td class=\"title\">");

		// TODO: here's where the reset goes...
		// <a href="#toolLink("$action" "doReset")" title="Reset"><img src="#imageLink("toolhome.gif")" alt="Reset" border="0"></a>

		String txt = (String) RendererUtil.getAttribute(context, component, "value");
		if (txt != null)
		{
			writer.write(txt);
		}
		writer.write("</td>\n");

		// TODO: here's where right hand icons go (i.e. float, dock...)
		writer.write("<td class=\"action\" align=\"right\">");
		String helpDocId = (String) RendererUtil.getAttribute(context, component, "helpDocId");
		if(helpDocId != null)
		{
			String sakai_HelpURL = "/sakai-help-tool/help/jsf.tool?pid=/sakai-help-tool/help/jsf.tool&helpDocId=";
			String linkToHelp = "<a href=\"javascript:;\" onClick=\"window.open('" + sakai_HelpURL;
			linkToHelp += helpDocId;
			linkToHelp += "','Help','resizable=1,toolbar=no,scrollbars=yes, width=800,height=600')\">";
			linkToHelp += "<img src=\"/library/image/help.gif\" border=\"0\"></a>";
			writer.write(linkToHelp);
		}
		writer.write("</td>\n");
		writer.write("</tr></table>\n");
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/TitleBarRenderer.java,v 1.4 2005/05/28 03:04:30 ggolden.umich.edu Exp $
*
**********************************************************************************/