/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuestionLinkRender extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIData);
	}
	
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
	  ResponseWriter writer = context.getResponseWriter();
	  String showLink = (String)component.getAttributes().get("showLink");
	  String URL = (String)component.getAttributes().get("URL");
	  String message = (String)component.getAttributes().get("message");
	  if("true".equals(showLink)){
		  writer.write("<a href=\"");
		  writer.write(URL);
		  writer.write("\" target=\"content\">");
		  writer.write(message);
		  writer.write("</a>");
	  }
	}

}
