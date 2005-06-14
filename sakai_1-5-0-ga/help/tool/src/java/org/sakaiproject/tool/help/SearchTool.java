/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.sakaiproject.jsf.ToolBean;
import org.sakaiproject.service.help.HelpManager;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchTool implements ToolBean{
    
    private HelpManager helpManager;
    private List searchResults;
    private String searchString = "enter search term";
    private String numberOfResult = "";
    private String showLinkToQuestionTool;
    private String emailAddress;
    
    /**
     * @return Returns the emailAddress.
     */
    public String getEmailAddress()
    {
      return emailAddress;
    }
    /**
     * @return Returns the searchString.
     */
    public String getSearchString() {
        return searchString;
    }
    /**
     * @param searchString The searchString to set.
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    /**
     * @return Returns the searchResults.
     */
    public List getSearchResults() {
        return searchResults;
    }
    /**
     * @param searchResults The searchResults to set.
     */
    public void setSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }
    
    public String processActionSearch()
    {
        Set resultSet = getHelpManager().searchResources(this.searchString);
        TreeSet treeSet = new TreeSet(resultSet);
        searchResults = new ArrayList();
        searchResults.addAll(treeSet);
        String searchStr = this.searchString;
        this.setNumberOfResult(searchResults.size());
        return "main";
    }
    
    public String submitCancel()
    {
      this.searchString ="";
      return "main";
    }
    /**
     * @return Returns the helpManager.
     */
    public HelpManager getHelpManager() {
//      if(this.helpManager == null)
//      {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        VariableResolver resolver = facesContext.getApplication().getVariableResolver();
//        helpManager = ((TableOfContentsTool)resolver.resolveVariable(facesContext, "TableOfContentsTool")).getHelpManager();
//      }
      return helpManager;
    }
    /**
     * @param helpManager The helpManager to set.
     */
    public void setHelpManager(HelpManager helpManager) {
        this.helpManager = helpManager;
    }
   
    /**
     * @return Returns the numberOfResult.
     */
    public String getNumberOfResult()
    {
      return numberOfResult;
    }
    /**
     * @param numberOfResult The numberOfResult to set.
     */
    public void setNumberOfResult(int numberOfResultInt)
    {
      if(numberOfResultInt == 0 )
      {
        this.numberOfResult = "No results were found for your search";
      }
      else{
        this.numberOfResult = numberOfResultInt +" results found";
      }
    }
    /**
     * @return Returns the showLinkToQuestionTool.
     */
    public String getShowLinkToQuestionTool()
    {
      emailAddress = helpManager.getSupportEmailAddress();
      if(!"".equals(emailAddress) && emailAddress != null)
      {
        showLinkToQuestionTool = "true";
      }
      else{
        showLinkToQuestionTool = "false";
      }
      return showLinkToQuestionTool;
    }
}
