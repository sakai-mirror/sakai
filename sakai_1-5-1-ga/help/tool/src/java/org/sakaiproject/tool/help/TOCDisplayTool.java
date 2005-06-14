/*
 * Created on Jan 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.sakaiproject.service.help.TableOfContents;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TOCDisplayTool
{
  private TableOfContents tableOfContents;
  /**
   * @return Returns the tableOfContents.
   */
  public TableOfContents getTableOfContents()
  {
    if(tableOfContents == null)
    {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      VariableResolver resolver = facesContext.getApplication().getVariableResolver();
      tableOfContents = ((TableOfContentsTool)resolver.resolveVariable(facesContext, "TableOfContentsTool")).getTableOfContents();
    }
    return tableOfContents;
  }
}
