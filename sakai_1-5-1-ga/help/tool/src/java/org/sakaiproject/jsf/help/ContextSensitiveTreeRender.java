/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.help.Category;
import org.sakaiproject.service.help.Resource;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContextSensitiveTreeRender extends Renderer
{

	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof UIData);
	}
	
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
	  String jsLibraryUrl = "../js";
	  ResponseWriter writer = context.getResponseWriter();
      writer.write("<script type=\"text/javascript\" src=\""+jsLibraryUrl+"/csTree.js\"></script>\n");
      writer.write("<link href=\"../css/csTree.css\" type=\"text/css\" rel=\"stylesheet\">");
      writer.write("<body onload='collapseAll([\"ol\"]); openBookMark();'>");
      writer.write("<ol id=\"root\">");
      UIData data = (UIData)component;
	  Object value = data.getValue();
	  String helpDocId = (String) component.getAttributes().get("helpDocId");
	  List categories =(List)value;
	  encodeRecursive(writer, categories, helpDocId);
      writer.write("</ol></body>");
	}
	
	private void encodeRecursive(ResponseWriter writer, List categories, String helpDocId) throws IOException
	{
	  for(int i=0; i<categories.size(); i++)
	  {
		Category category = (Category)categories.get(i);
		List resources = category.getResources();
		String id=category.getName();
		boolean contains = this.containsHelpDoc(helpDocId, resources);
		if(contains)
		{
		  id="default";
		}
		writer.write("<li><a id=\""+ id +"\" href=\"#" + category.getName() + "\" onclick=\"toggle(this)\"><img src=\"../image/toc_closed.gif\" border=\"0\"/id=\"I" + category.getName()+ "\"></a>"); 
		writer.write("<a href=\"#\" onclick=\"toggle(this)\">"+ category.getName()+"</a>");
        writer.write("<ol>");

		List subCategories = category.getCategories();
		encodeRecursive(writer, subCategories, helpDocId);
		if(resources != null)
		{
		  for(int j=0; j<resources.size(); j++)
		  {
		    writer.write("<ul>");
		    Resource resource = (Resource)resources.get(j);
		    writer.write("<a href=\"" +resource.getLocation()+
		        "\" target = \"content\"><img src=\"../image/topic.gif\" border=\"0\"/>"+
		        resource.getName() +"</a>");
		    writer.write("</ul>");
		  }
		}
		writer.write("</ol></li>");
	  }
	}
	
	private boolean containsHelpDoc(String helpDocId, List resources){
	    boolean contains = false;
		if(resources != null)
		{
		  for(int i=0; i<resources.size(); i++)
		  {
		    Resource resource = (Resource)resources.get(i);
		    if(resource.getDocId().equals(helpDocId))
		    {
		      contains = true;
		      break;
		    }
		  }
		}
	  return contains;
	}
}
