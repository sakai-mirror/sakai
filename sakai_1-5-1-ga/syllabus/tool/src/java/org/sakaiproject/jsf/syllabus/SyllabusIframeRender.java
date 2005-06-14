package org.sakaiproject.jsf.syllabus;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class SyllabusIframeRender extends Renderer
{
  public boolean supportsComponentType(UIComponent component)
  {
    return (component instanceof org.sakaiproject.jsf.syllabus.SyllabusIframeComponent);
  }
  
  public void encodeBegin(FacesContext context, UIComponent component)
  throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    
    String widthIn = (String) component.getAttributes().get("width");
    String heightIn = (String) component.getAttributes().get("height");
    String redirectUrl = (String) component.getAttributes().get("redirectUrl");
    
    if (widthIn == null) 
    {
      widthIn = new Integer(450).toString();
    }
    
    if (heightIn == null) 
    {
      heightIn = new Integer(80).toString();
    }
    
    if((redirectUrl != null) && (!redirectUrl.equals("")))
    {
      if(!redirectUrl.startsWith("http://"))
      {
        redirectUrl = "http://" + redirectUrl;
      }
      writer.write("<iframe src=\"" + redirectUrl + "\"");
      writer.write(" width=\"" + widthIn + "\"");
      writer.write(" height=\"" + heightIn + "\"");
      writer.write("></iframe>");
    }
  }
}
