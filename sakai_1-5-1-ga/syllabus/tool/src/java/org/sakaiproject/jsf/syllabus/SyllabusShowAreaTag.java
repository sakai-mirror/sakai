package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class SyllabusShowAreaTag extends UIComponentTag
{
  private String value;
  
  public void setvalue(String value)
  {
    this.value = value;
  }
  
  public String getvalue()
  {
    return value;
  }
  
  public String getComponentType()
  {
    return "SyllabusShowArea";
  }
  
  public String getRendererType()
  {
    return "SyllabusShowAreaRender";
  }
  protected void setProperties(UIComponent component)
  {
    super.setProperties(component);
    setString(component, "value", value);
  }
  
  public void release()
  {
    super.release();
    
    value = null;
  }
  
  public static void setString(UIComponent component, String attributeName,
      String attributeValue)
  {
    if (attributeValue == null) return;
    if (UIComponentTag.isValueReference(attributeValue)) setValueBinding(
        component, attributeName, attributeValue);
    else
      component.getAttributes().put(attributeName, attributeValue);
  }

  public static void setValueBinding(UIComponent component,
      String attributeName, String attributeValue)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Application app = context.getApplication();
    ValueBinding vb = app.createValueBinding(attributeValue);
    component.setValueBinding(attributeName, vb);
  }
}