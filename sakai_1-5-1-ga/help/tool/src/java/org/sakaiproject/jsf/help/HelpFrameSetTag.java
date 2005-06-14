/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.jsf.help;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpFrameSetTag extends UIComponentTag
{
  private String helpWindowTitle;
  private String searchToolUrl;
  private String tocToolUrl;
  private String helpDocUrl;
  private String helpDocId;
  
  public String getComponentType()
  {
    return "javax.faces.Data";
  }

  public String getRendererType()
  {
    return "HelpFrameSet";
  }
  
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		FacesContext context = getFacesContext();
		

		if( searchToolUrl != null )
		{
			if (isValueReference(searchToolUrl))
			{
				component.setValueBinding("searchToolUrl", context.getApplication().createValueBinding(searchToolUrl));
			}
			else
			{
				component.getAttributes().put("searchToolUrl", searchToolUrl);
			}
		}
		if( tocToolUrl != null )
		{
			if (isValueReference(tocToolUrl))
			{
				component.setValueBinding("tocToolUrl", context.getApplication().createValueBinding(tocToolUrl));
			}
			else
			{
				component.getAttributes().put("tocToolUrl", tocToolUrl);
			}
		}
		if (helpWindowTitle != null)
		{
			if (isValueReference(helpWindowTitle))
			{
				component.setValueBinding("helpWindowTitle", context.getApplication().createValueBinding(helpWindowTitle));
			}
			else
			{
				component.getAttributes().put("helpWindowTitle", helpWindowTitle);
			}
		}
		if (helpDocUrl != null)
		{
			if (isValueReference(helpDocUrl))
			{
				component.setValueBinding("helpDocUrl", context.getApplication().createValueBinding(helpDocUrl));
			}
			else
			{
				component.getAttributes().put("helpDocUrl", helpDocUrl);
			}
		}
		
		if (helpDocId != null)
		{
			if (isValueReference(helpDocId))
			{
				component.setValueBinding("helpDocId", context.getApplication().createValueBinding(helpDocId));
			}
			else
			{
				component.getAttributes().put("helpDocId", helpDocId);
			}
		}
	}
  
  /**
   * @return Returns the searchTooolUrl.
   */
  public String getSearchToolUrl()
  {
    return searchToolUrl;
  }
  /**
   * @param searchTooolUrl The searchTooolUrl to set.
   */
  public void setSearchToolUrl(String searchToolUrl)
  {
    this.searchToolUrl = searchToolUrl;
  }
  /**
   * @return Returns the tocToolUrl.
   */
  public String getTocToolUrl()
  {
    return tocToolUrl;
  }
  /**
   * @param tocToolUrl The tocToolUrl to set.
   */
  public void setTocToolUrl(String tocToolUrl)
  {
    this.tocToolUrl = tocToolUrl;
  }

  /**
   * @return Returns the helpWindowTitle.
   */
  public String getHelpWindowTitle()
  {
    return helpWindowTitle;
  }
  /**
   * @param helpWindowTitle The helpWindowTitle to set.
   */
  public void setHelpWindowTitle(String helpWindowTitle)
  {
    this.helpWindowTitle = helpWindowTitle;
  }
  
  
  /**
   * @return Returns the helpDocUrl.
   */
  public String getHelpDocUrl()
  {
    return helpDocUrl;
  }
  /**
   * @param helpDocUrl The helpDocUrl to set.
   */
  public void setHelpDocUrl(String helpDocUrl)
  {
    this.helpDocUrl = helpDocUrl;
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
}
