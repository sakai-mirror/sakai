/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.service.help.Category;
import org.sakaiproject.service.help.Resource;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpFrameSetRender extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIData);
	}
	
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
	  ResponseWriter writer = context.getResponseWriter();
	  String helpWindowTitle = (String)component.getAttributes().get("helpWindowTitle");
	  String searchToolUrl = (String)component.getAttributes().get("searchToolUrl");
	  String tocToolUrl = (String)component.getAttributes().get("tocToolUrl");
	  String helpDocUrl = (String)component.getAttributes().get("helpDocUrl");
	  String helpDocId = (String)component.getAttributes().get("helpDocId");
	  
	  writer.write("<html><head><title>"+ helpWindowTitle +"</title></head>");
	  writer.write("<FRAMESET cols=\"30%, 70%\"><FRAMESET rows=\"250, 350\">");
	  writer.write("<FRAME src="+ searchToolUrl +" name=\"search\"/>");
	  writer.write("<FRAME src="+tocToolUrl+"&helpDocId="+helpDocId+" name=\"toc\"/>");
	  writer.write("</FRAMESET>");
	  writer.write("<FRAME src=\""+helpDocUrl+"\" name=\"content\" scrolling=\"yes\">");
	  writer.write("</FRAMESET></html>");  	
	}

}
