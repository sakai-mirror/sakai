/*
 * Created on Dec 10, 2004
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

import org.sakaiproject.service.help.Category;
import org.sakaiproject.service.help.Resource;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TocTreeRender extends Renderer
{
  
//	public boolean supportsComponentType(UIComponent component)
//	{
//		return (component instanceof UIData);
//	}
	
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
//	  String jsLibraryUrl = "/sakai-help-tool/js";
	  String jsLibraryUrl = "../js";
	  ResponseWriter writer = context.getResponseWriter();
      writer.write("<script type=\"text/javascript\">var _editor_url = \""+jsLibraryUrl+"/\";</script>\n");
      writer.write("<script type=\"text/javascript\" src=\""+jsLibraryUrl+"/divTree.js\"></script>\n");
      writer.write("<link href=\"../css/divTree.css\" type=\"text/css\" rel=\"stylesheet\">");

	  UIData data = (UIData)component;
	  Object value = data.getValue();
	  List categories =(List)value;
	  encodeRecursive(writer, categories);
	}
	
	private void encodeRecursive(ResponseWriter writer, List categories) throws IOException
	{
	  for(int i=0; i<categories.size(); i++)
	  {
		Category category = (Category)categories.get(i);
		writer.write("<a class=\"trigger\" href=\"javascript:showBranch('" + category.getName() +"');\">");
		writer.write("<img border=\"0\" alt=\"expand/collapse\" src=\"../image/toc_closed.gif\"");
		writer.write(" id=\"I" + category.getName()+ "\">");
		writer.write(category.getName()+ "</a>");
		writer.write("<div class=\"branch\" id=\""+ category.getName()+ "\">" );
		List resources = category.getResources();
		List subCategories = category.getCategories();
		encodeRecursive(writer, subCategories);
		if(resources != null)
		{
		  for(int j=0; j<resources.size(); j++)
		  {
		    Resource resource = (Resource)resources.get(j);
		    writer.write("<div>");
		    writer.write("<img src=\"../image/topic.gif\">");
		    writer.write("<a href=\"" +resource.getLocation()+"\" target = \"content\">" + resource.getName() +"</a></div>");
		  }
		}
		writer.write("</div>");
	  }
	}
}
