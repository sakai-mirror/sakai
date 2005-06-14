package org.sakaiproject.jsf.syllabus;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class SyllabusIfTag extends UIComponentTag
{
  private String test;
  
  public void setTest(String test)
  {
    this.test = test;
  }
  
  public String getTest()
  {
    return test;
  }
  
  public String getComponentType()
  {
    return "SakaiSyllabusIf";
  }
  
  public String getRendererType()
  {
    return "SakaiSyllabusIfRender";
  }
  
  protected void setProperties(UIComponent component)
  {
    super.setProperties(component);
    setString(component, "test", test);
  }
  
  public void release()
  {
    super.release();
    
    test = null;
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