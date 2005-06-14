/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.help.Category;
import org.sakaiproject.service.help.HelpManager;
import org.sakaiproject.service.help.Resource;
import org.sakaiproject.service.help.TableOfContents;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.InputStreamResource;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TableOfContentsTool {
    
    private TableOfContents tableOfContents;
    private HelpManager helpManager;
    private Logger logger;
    private String baseUrl=null;
    private static final String HELP_DOC_LOCATION = "/sakai-help-tool/helpDocs/";
    /**
     * @return Returns the tableOfContents.
     */
    public TableOfContents getTableOfContents() {
      if(tableOfContents == null)
      {
        this.setTableOfContents(helpManager.getTableOfContents());
      }
        return tableOfContents;
    }
    /**
     * @param tableOfContents The tableOfContents to set.
     */
    public void setTableOfContents(TableOfContents tableOfContents) {
        this.tableOfContents = tableOfContents;

    }
    
    /**
     * @return Returns the helpManager.
     */
    public HelpManager getHelpManager()
    {
      return helpManager;
    }
    /** Set helpManager and register the help content. 
     * @param helpManager The helpManager to set.
     */
    public void setHelpManager(HelpManager helpManager)
    {
      this.helpManager = helpManager;
      registerHelpContent();
    }
    
    /**
     * @return Returns the logger.
     */
    public Logger getLogger()
    {
      return logger;
    }
    /**
     * @param logger The logger to set.
     */
    public void setLogger(Logger logger)
    {
      this.logger = logger;
    }
    
    private void registerHelpContent()
    {
      tableOfContents = helpManager.getTableOfContents();
      //register all help docs
      List toolRegistrations = helpManager.getServerConfigurationService().getToolRegistrations();

      List urlList = this.getRegistrationFileList();
      for(int i=0; i<toolRegistrations.size(); i++)
      {
        ToolRegistration tr = (ToolRegistration)toolRegistrations.get(i);
        String toolHelpUrl = tr.getHelpUrl();
        if(toolHelpUrl != null)
        {
          urlList.add(toolHelpUrl);
        }
      }
      List allCategories = new ArrayList();
      for(int i=0; i<urlList.size(); i++)
      {

        try{
          String urlString = (String)urlList.get(i);
          org.springframework.core.io.Resource resource = 
            new InputStreamResource((new URL(urlString)).openStream(), urlString);
          BeanFactory beanFactory = new XmlBeanFactory(resource);
          TableOfContents toc = (TableOfContents)beanFactory.getBean("org.sakaiproject.service.help.TableOfContents");
          List categories = toc.getCategories();
          allCategories.addAll(categories);
          storeRecursive(categories);
        }
        catch(Exception e)
        {
          logger.error(e);
          break;
        }
      }
      tableOfContents.setCategories(allCategories);  
    }
    /**
     * @return
     */
    private List getRegistrationFileList()
    {
      String tunnelUrl = helpManager.getServerConfigurationService().getPortalTunnelUrl();
      List returnList = new ArrayList();
      String baseUrl = tunnelUrl + "/sakai-help-tool/helpReg/";
      String urlString = baseUrl + "helpRegistration.xml";
      org.springframework.core.io.Resource resource;
      try
      {
        resource = new InputStreamResource((new URL(urlString)).openStream(), urlString);
        BeanFactory beanFactory = new XmlBeanFactory(resource);
        HelpRegistration registration = (HelpRegistration)beanFactory.getBean("org.sakaiproject.tool.help.HelpRegistration");
        List fileList = registration.getRegistrationFiles();
        for(int i=0; i<fileList.size(); i++)
        {
          RegistrationFile file = (RegistrationFile)fileList.get(i);
          returnList.add(baseUrl + file.getName());
        }
      }
      catch (MalformedURLException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error(e.getMessage(), e);
      }
      catch (IOException e)
      {
        logger.error(e.getMessage(), e);
      }
      return returnList;

    }
    /**
     * @param categories
     */
    private void storeRecursive(List categories)
    {
      for(int j=0; j<categories.size(); j++)
      {
        Category category = (Category)categories.get(j);
        List resources = category.getResources();
        for(int i=0; i<resources.size(); i++)
        {
          Resource resource = (Resource)resources.get(i);
          helpManager.storeResource(this.processResource(resource));
        }
        List subCategories = category.getCategories();
        storeRecursive(subCategories);
      }
    }
    
  /**
     * @param resource
     * @return
     */
    private Resource processResource(Resource resource)
    {
      String location = resource.getLocation();
     // int pos = location.indexOf("/sakai-help-tool/servlet/HelpServlet?docid=");
      int pos = location.indexOf(HELP_DOC_LOCATION);
      if (pos != -1)
      {
        resource.setLocation(this.getBaseUrl() + location);
      }
      return resource;
    }
    
  private String getBaseUrl()
	{
	  if(baseUrl== null)
	  {
		  HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		  StringBuffer base = request.getRequestURL();
		  String servletPath = request.getServletPath();
		  String contextPath = request.getContextPath();
	
		  int pos = base.indexOf(contextPath);
		  if (pos != -1)
		  {
			base.setLength(pos);
		  }
	      baseUrl =  base.toString();
	  }
	  return baseUrl;
	}
}
