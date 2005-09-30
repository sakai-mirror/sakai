/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/PeerRefresh.java,v 1.1 2005/06/04 23:35:25 ggolden.umich.edu Exp $
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

/**
 * <p>PeerRefresh is an HTML renderer for the Sakai "peer_refresh" tag in JSF.</p>
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class PeerRefresh extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIOutput);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.write("<div class =\"instruction\">");
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
		String txt = (String) component.getAttributes().get("value");
		if ((txt != null) && (txt.length() > 0))
		{
			writer.write("<script type=\"text/javascript\" language=\"JavaScript\">\n");
			writer.write("try\n");
			writer.write("{\n");
			writer.write("	if (parent." + txt + ".location.toString().length > 1)\n");
			writer.write("	{\n");
			writer.write("		parent." + txt + ".location.replace(parent." + txt + ".location);\n");
			writer.write("	}\n");
			writer.write("}\n");
			writer.write("catch (e1)\n");
			writer.write("{\n");
			writer.write("	try\n");
			writer.write("	{\n");
			writer.write("		if (parent.parent." + txt + ".location.toString().length > 1)\n");
			writer.write("		{\n");
			writer.write("			parent.parent." + txt + ".location.replace(parent.parent." + txt + ".location);\n");
			writer.write("		}\n");
			writer.write("	}\n");
			writer.write("	catch (e2)\n");
			writer.write("	{\n");
			writer.write("	}\n");
			writer.write("}\n");
			writer.write("</script>\n");
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/PeerRefresh.java,v 1.1 2005/06/04 23:35:25 ggolden.umich.edu Exp $
*
**********************************************************************************/
