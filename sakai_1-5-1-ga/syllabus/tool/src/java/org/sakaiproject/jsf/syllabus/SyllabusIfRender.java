package org.sakaiproject.jsf.syllabus;

import java.io.IOException;
import java.util.Map;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class SyllabusIfRender extends Renderer
{
  public boolean supportsComponentType(UIComponent component)
  {
    return (component instanceof org.sakaiproject.jsf.syllabus.SyllabusIfComponent);
  }
  
  public void encodeBegin(FacesContext context, UIComponent component)
  throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    
    String test = (String) component.getAttributes().get("test");
    if(test!=null)
      test = test.trim();
    
    if((test==null) || (test.equals("")))
    {
      writer.write("<div>");
    }
  }

  public void encodeEnd(FacesContext context, UIComponent component)
  throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    
    String test = (String) component.getAttributes().get("test");
    if(test!=null)
      test = test.trim();

    if((test==null) || (test.equals("")))
    {
      writer.write("</div>");
    }  
  }
  
  public void encodeChildren(FacesContext context, UIComponent component)
  	throws IOException 
  {
    if (context == null || component == null) 
    {
      throw new NullPointerException();
    }

    String test = (String) component.getAttributes().get("test");
    if(test!=null)
      test = test.trim();
    
    if((test==null) || (test.equals("")))
    {
      Iterator kids = component.getChildren().iterator();
      
      while (kids.hasNext()) 
      {
        UIComponent kid = (UIComponent) kids.next();
        kid.encodeBegin(context);
        if (kid.getRendersChildren()) {
          kid.encodeChildren(context);
        }
        kid.encodeEnd(context);
      }
    }
    else
    {
    }
  }
}
