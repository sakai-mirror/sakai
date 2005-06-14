/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuestionLinkTag extends UIComponentTag
{
  String URL = null;
  String message = null;
  String showLink = "false";
  
  public String getComponentType()
  {
    return "javax.faces.Data";
  }

  public String getRendererType()
  {
    return "QuestionLink";
  }
  
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		FacesContext context = getFacesContext();
		

		if( URL != null )
		{
			if (isValueReference(URL))
			{
				component.setValueBinding("URL", context.getApplication().createValueBinding(URL));
			}
			else
			{
				component.getAttributes().put("URL", URL);
			}
		}
		if( message != null )
		{
			if (isValueReference(message))
			{
				component.setValueBinding("message", context.getApplication().createValueBinding(message));
			}
			else
			{
				component.getAttributes().put("message", message);
			}
		}
		if (showLink != null)
		{
			if (isValueReference(showLink))
			{
				component.setValueBinding("showLink", context.getApplication().createValueBinding(showLink));
			}
			else
			{
				component.getAttributes().put("showLink", showLink);
			}
		}
	}
 
  /**
   * @return Returns the message.
   */
  public String getMessage()
  {
    return message;
  }
  /**
   * @param message The message to set.
   */
  public void setMessage(String message)
  {
    this.message = message;
  }
  /**
   * @return Returns the showLink.
   */
  public String getShowLink()
  {
    return showLink;
  }
  /**
   * @param showLink The showLink to set.
   */
  public void setShowLink(String showLink)
  {
    this.showLink = showLink;
  }
  /**
   * @return Returns the uRL.
   */
  public String getURL()
  {
    return URL;
  }
  /**
   * @param url The uRL to set.
   */
  public void setURL(String url)
  {
    URL = url;
  }

}
