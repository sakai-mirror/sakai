/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

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
public class ContextSensitiveTreeTag extends UIComponentTag
{
  
	private String value = null;
	private String var = null;
    private String helpDocId = null;

	public String getComponentType()
	{
		return "javax.faces.Data";
	}

	public String getRendererType()
	{
		return "CSTree";
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
		
		if (getHelpDocId() != null)
		{
			if (isValueReference(getHelpDocId()))
			{
				component.setValueBinding("helpDocId", getFacesContext().getApplication().createValueBinding(getHelpDocId()));
			}
			else
			{
				component.getAttributes().put("helpDocId", getHelpDocId());
			}
		}
	}

    /**
     * @return Returns the helpDocId.
     */
    public String getHelpDocId()
    {
      return helpDocId;
    }
    /**
     * @param helpDocId The helpDocId to set.
     */
    public void setHelpDocId(String helpDocId)
    {
      this.helpDocId = helpDocId;
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
