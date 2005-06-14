/*
 * Created on Oct 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.help.HelpManager;
import org.sakaiproject.service.help.Resource;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpTool {
    private HelpManager helpManager;
    private Resource resource;
    private String helpDocId;
    
    
    /**
     * @return Returns the helpDocId.
     */
    public String getHelpDocId()
    {
      if(helpDocId == null)
      {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String helpDocIdString = request.getParameter("helpDocId");
        this.setHelpDocId(helpDocIdString);
      }
      return helpDocId;
    }
    /**
     * @param helpDocId The helpDocId to set.
     */
    public void setHelpDocId(String helpDocId)
    {
      this.helpDocId = helpDocId;
    }
    public HelpTool()
    {

    }

    /**
     * @return Returns the resource.
     */
    public Resource getResource() {
      HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
      String helpDocIdString = request.getParameter("helpDocId");
      if(helpDocIdString != null)
      {
          resource = getHelpManager().getResourceByDocId(helpDocIdString);
      }
      if(helpDocIdString == null || resource == null){
        resource = getHelpManager().createResource();
        resource.setLocation("/tunnel/sakai-help-tool/helpDocs/overview.html");
      }
        return resource;
    }
    /**
     * @param resource The resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * @return Returns the helpManager.
     */
    private HelpManager getHelpManager()
    { 
      if(this.helpManager == null)
      {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        VariableResolver resolver = facesContext.getApplication().getVariableResolver();
        helpManager = ((TableOfContentsTool)resolver.resolveVariable(facesContext, "TableOfContentsTool")).getHelpManager();
      }
      return helpManager;
    }

}
