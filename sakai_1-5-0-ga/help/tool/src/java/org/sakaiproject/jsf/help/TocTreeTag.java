/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TocTreeTag extends UIComponentTag
{
	private String value = null;
	private String var = null;
    private String categories = null;

	public String getComponentType()
	{
		return "javax.faces.Data";
	}

	public String getRendererType()
	{
		return "SakaiTocTree";
	}
	
	public String getCategories()
	{
	  return categories;
	}
	
	public void setCategories(String categories)
	{
	  this.categories = categories;
	}

	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		FacesContext context = getFacesContext();
		if(value != null)
		{
		  ValueBinding vb = context.getApplication().createValueBinding(value);
		  component.setValueBinding("value", vb);
		}
		if(var != null)
		{
		  ((UIData)component).setVar(var);
		}

	}


  /**
   * @return Returns the value.
   */
  public String getValue()
  {
    return value;
  }
  /**
   * @param value The value to set.
   */
  public void setValue(String value)
  {
    this.value = value;
  }
  /**
   * @return Returns the var.
   */
  public String getVar()
  {
    return var;
  }
  /**
   * @param var The var to set.
   */
  public void setVar(String var)
  {
    this.var = var;
  }
}
