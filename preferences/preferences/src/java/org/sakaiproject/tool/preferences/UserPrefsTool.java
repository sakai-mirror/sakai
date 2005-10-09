/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

package org.sakaiproject.tool.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.PreferencesEdit;
import org.sakaiproject.service.legacy.preference.PreferencesService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.cover.TimeService;

/**
 * UserPrefsTool is a Sakai Admin tool to view and edit anyone's preferences.
 * 
 * @author <a href="mailto:htripath@indiana.edu">Himansu Tripathy</a>
 * @version $Id$
 */
public class UserPrefsTool
{
  /**
   * Represents a name value pair in a keyed preferences set.
   */
  public class KeyNameValue
  {
    /** Is this value a list?. */
    protected boolean m_isList = false;

    /** The key. */
    protected String m_key = null;

    /** The name. */
    protected String m_name = null;

    /** The original is this value a list?. */
    protected boolean m_origIsList = false;

    /** The original key. */
    protected String m_origKey = null;

    /** The original name. */
    protected String m_origName = null;

    /** The original value. */
    protected String m_origValue = null;

    /** The value. */
    protected String m_value = null;

    public KeyNameValue(String key, String name, String value, boolean isList)
    {
      m_key = key;
      m_origKey = key;
      m_name = name;
      m_origName = name;
      m_value = value;
      m_origValue = value;
      m_isList = isList;
      m_origIsList = isList;
    }

    public String getKey()
    {
      return m_key;
    }

    public String getName()
    {
      return m_name;
    }

    public String getOrigKey()
    {
      return m_origKey;
    }

    public String getOrigName()
    {
      return m_origName;
    }

    public String getOrigValue()
    {
      return m_origValue;
    }

    public String getValue()
    {
      return m_value;
    }

    public boolean isChanged()
    {
      return ((!m_name.equals(m_origName)) || (!m_value.equals(m_origValue))
          || (!m_key.equals(m_origKey)) || (m_isList != m_origIsList));
    }

    public boolean isList()
    {
      return m_isList;
    }

    public boolean origIsList()
    {
      return m_origIsList;
    }

    public void setKey(String value)
    {
      if (!m_key.equals(value))
      {
        m_key = value;
      }
    }

    public void setList(boolean b)
    {
      m_isList = b;
    }

    public void setName(String value)
    {
      if (!m_name.equals(value))
      {
        m_name = value;
      }
    }

    public void setValue(String value)
    {
      if (!m_value.equals(value))
      {
        m_value = value;
      }
    }
  }

  /** Our log (commons). */
  private static final Log LOG = LogFactory.getLog(UserPrefsTool.class);

  /** The PreferencesEdit being worked on. */
  protected PreferencesEdit m_edit = null;

  /** Preferences service (injected dependency) */
  protected PreferencesService m_preferencesService = null;

  /** Session manager (injected dependency) */
  protected SessionManager m_sessionManager = null;

  /** The PreferencesEdit in KeyNameValue collection form. */
  protected Collection m_stuff = null;

  //	/** The user id (from the end user) to edit. */
  //	protected String m_userId = null;
  /** For display and selection of Items in JSF-- edit.jsp */
  private List prefExcludeItems = new ArrayList();
  private List prefOrderItems = new ArrayList();
  private List prefTimeZones = new ArrayList();
  private String[] selectedExcludeItems;
  private String[] selectedOrderItems;
  protected final static String EXCLUDE_SITE_LISTS = "exclude";
  protected final static String ORDER_SITE_LISTS = "order";
  protected boolean isNewUser = false;
  protected boolean tabUpdated=false ;		//display of text message upon updation
  // user's currently selected time zone
  private TimeZone m_timeZone = null;
  
  /** The user id retrieved from UsageSessionService	*/
  private String userId = "";
  
  //////////////////////////////////		PROPERTY GETTER AND SETTER		////////////////////////////////////////////	
  /**
   * @return Returns the prefExcludeItems.
   */
  public List getPrefExcludeItems()
  {
    return prefExcludeItems;
  }

  /**
   * @param prefExcludeItems The prefExcludeItems to set.
   */
  public void setPrefExcludeItems(List prefExcludeItems)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setPrefExcludeItems(List " + prefExcludeItems + ")");
    }

    this.prefExcludeItems = prefExcludeItems;
  }

  /**
   * @return Returns the prefOrderItems.
   */
  public List getPrefOrderItems()
  {
    return prefOrderItems;
  }

  /**
   * @param prefOrderItems The prefOrderItems to set.
   */
  public void setPrefOrderItems(List prefOrderItems)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setPrefOrderItems(List " + prefOrderItems + ")");
    }

    this.prefOrderItems = prefOrderItems;
  }

  /**
   * @return Returns the prefTimeZones.
   */
  public List getPrefTimeZones()
  {
    if ( prefTimeZones.size() == 0 )
    {
       String[] timeZoneArray = TimeZone.getAvailableIDs();
       Arrays.sort( timeZoneArray );
       for ( int i=0; i<timeZoneArray.length; i++ )
          prefTimeZones.add( new SelectItem(timeZoneArray[i], timeZoneArray[i]) );
    }
      
    return prefTimeZones;
  }

  /**
   * @param prefTimeZoness The prefTimeZones to set.
   */
  public void setPrefTimeZones(List prefTimeZones)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setPrefTimeZones(List " + prefTimeZones + ")");
    }

    this.prefTimeZones = prefTimeZones;
  }

  /**
   * @return Returns the selectedExcludeItems.
   */
  public String[] getSelectedExcludeItems()
  {
    return selectedExcludeItems;
  }

  /**
   * @param selectedExcludeItems The selectedExcludeItems to set.
   */
  public void setSelectedExcludeItems(String[] selectedExcludeItems)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedExcludeItems(String[] " + selectedExcludeItems
          + ")");
    }

    this.selectedExcludeItems = selectedExcludeItems;
  }

  /**
   * @return Returns the selectedOrderItems.
   */
  public String[] getSelectedOrderItems()
  {
    return selectedOrderItems;
  }

  /**
   * @param selectedOrderItems The selectedOrderItems to set.
   */
  public void setSelectedOrderItems(String[] selectedOrderItems)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedOrderItems(String[] " + selectedOrderItems + ")");
    }

    this.selectedOrderItems = selectedOrderItems;
  }

  /**
   * @return Returns the user's selected TimeZone ID
   */
  public String getSelectedTimeZone()
  {
     if ( m_timeZone != null )
        return m_timeZone.getID();
             
     Preferences prefs = (PreferencesEdit) m_preferencesService
         .getPreferences(getUserId());
     ResourceProperties props = prefs.getProperties(TimeService.SERVICE_NAME);
     String timeZone = props.getProperty( TimeService.TIMEZONE_KEY );
        
     if (hasValue(timeZone))
        m_timeZone = TimeZone.getTimeZone( timeZone );
     else
        m_timeZone = TimeZone.getDefault();
        
     return m_timeZone.getID();
  }

  /**
   * @param selectedTimeZone The selectedTimeZone to set.
   */
  public void setSelectedTimeZone(String selectedTimeZone)
  {
    m_timeZone = TimeZone.getTimeZone( selectedTimeZone );
  }

  /**
   * @return Returns the userId.
   */
  public String getUserId()
  {
    return m_sessionManager.getCurrentSessionUserId();
  }

  /**
   * @param userId The userId to set.
   */
  public void setUserId(String userId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setUserId(String " + userId + ")");
    }
    this.userId = userId;
  }

  /**
   * @param mgr  The preferences service.
   */
  public void setPreferencesService(PreferencesService mgr)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setPreferencesService(PreferencesService " + mgr + ")");
    }

    m_preferencesService = mgr;
  }

  /** 
   * @param mgr The session manager.
   */
  public void setSessionManager(SessionManager mgr)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSessionManager(SessionManager " + mgr + ")");
    }

    m_sessionManager = mgr;
  }  
  /**
   * @return Returns the tabUpdated.
   */
  public boolean isTabUpdated()
  {
    return tabUpdated;
  }
  /**
   * @param tabUpdated The tabUpdated to set.
   */
  public void setTabUpdated(boolean tabUpdated)
  {
    this.tabUpdated = tabUpdated;
  }
  ///////////////////////////////////////// CONSTRUCTOR		////////////////////////////////////////////
  /**
   * no-arg constructor.
   */
  public UserPrefsTool()
  {
    LOG.debug("new UserPrefsTool()");
  }

  // Naming in faces-config.xml Refresh jsp- "refresh" , Notification jsp- "noti" , tab cutomization jsp- "tab"
  ///////////////////////////////////////  PROCESS ACTION		///////////////////////////////////////////			
  /**
   * Process the add command from the edit view.
   * @return navigation outcome to tab customization page (edit)
   */
  public String processActionAdd()
  {
    LOG.debug("processActionAdd()");
    tabUpdated=false;				//reset successful text message if existing with same jsp
    String[] values = getSelectedExcludeItems();
    if (values.length < 1)
    {
      FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage("Please select a site to add to Sites Visible in Tabs."));
      return "tab";
    }

    for (int i = 0; i < values.length; i++)
    {
      String value = values[i];
      getPrefOrderItems().add(
          removeItems(value, getPrefExcludeItems(), ORDER_SITE_LISTS,
              EXCLUDE_SITE_LISTS));
    }
    return "tab";
  }

  /**
   * Process remove from order list command
   * @return navigation output to tab customization page (edit)
   */
  public String processActionRemove()
  {
    LOG.debug("processActionRemove()");
    tabUpdated=false;				//reset successful text message if existing in jsp
    String[] values = getSelectedOrderItems();
    if (values.length < 1)
    {
      FacesContext.getCurrentInstance()
          .addMessage(
              null,
              new FacesMessage(
                  "Please select a site to remove from Sites Visible in Tabs."));
      return "tab";
    }

    for (int i = 0; i < values.length; i++)
    {
      String value = values[i];
      getPrefExcludeItems().add(
          removeItems(value, getPrefOrderItems(), EXCLUDE_SITE_LISTS,
              ORDER_SITE_LISTS));
    }
    return "tab";
  }

  /**
   * Process Add All action
   * @return navigation output to tab customization page (edit)
   */
  public String processActionAddAll()
  {
    LOG.debug("processActionAddAll()");

    getPrefOrderItems().addAll(getPrefExcludeItems());
    getPrefExcludeItems().clear();
    tabUpdated=false;				//reset successful text message if existing in jsp
    return "tab";
  }

  /**
   * Process Remove All command
   * @return navigation output to tab customization page (edit)
   */
  public String processActionRemoveAll()
  {
    LOG.debug("processActionRemoveAll()");

    getPrefExcludeItems().addAll(getPrefOrderItems());
    getPrefOrderItems().clear();
    tabUpdated=false;				//reset successful text message if existing in jsp
    return "tab";
  }

  /**
   * Move Up the selected item in Ordered List
   * @return navigation output to tab customization page (edit)
   */
  public String processActionMoveUp()
  {
    LOG.debug("processActionMoveUp()");
    tabUpdated=false;				//reset successful text message if existing in jsp
    String[] selvalues = getSelectedOrderItems();
    if (!(selvalues.length == 1))
    {
      FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage("Please select one site to move up."));
      return "tab";
    }
    int itmPos = 0;
    SelectItem swapData = null;
    Iterator iterUp = getPrefOrderItems().iterator();
    while (iterUp.hasNext())
    {
      SelectItem dataUpSite = (SelectItem) iterUp.next();
      if (selectedOrderItems[0].equals(dataUpSite.getValue()))
      {
        break;
      }
      else
      {
        swapData = dataUpSite;
      }
      itmPos++;
    }
    //Swap the position
    if (swapData != null)
    {
      if (itmPos >= 1 && (itmPos + 1 <= prefOrderItems.size()))
      {
        SelectItem temp = (SelectItem) prefOrderItems.get(itmPos - 1);
        prefOrderItems.set(itmPos - 1, prefOrderItems.get(itmPos));
        prefOrderItems.set(itmPos, temp);
      }
    }
    return "tab";
  }

  /**
   * Move down the selected item in Ordered List
   * @return navigation output to tab customization page (edit)
   */
  public String processActionMoveDown()
  {
    LOG.debug("processActionMoveDown()");
    tabUpdated=false;				//reset successful text message if existing in jsp
    String[] values = getSelectedOrderItems();
    if (!(values.length == 1))
    {
      FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage("Please select one site to move down."));
      return "tab";
    }
    SelectItem swapDataSite = null;
    int elemPos = 0;
    Iterator iter = getPrefOrderItems().iterator();
    while (iter.hasNext())
    {
      elemPos++;
      SelectItem dataSite = (SelectItem) iter.next();
      if (selectedOrderItems[0].equals(dataSite.getValue()))
      {
        if (iter.hasNext()) swapDataSite = (SelectItem) iter.next();
        break;
      }
    }
    // swap the position - elemPos is the final moving position
    if (swapDataSite != null)
    {
      if (elemPos >= 1 && (elemPos < prefOrderItems.size()))
      {
        SelectItem temp = (SelectItem) prefOrderItems.get(elemPos);
        prefOrderItems.set(elemPos, prefOrderItems.get(elemPos - 1));
        prefOrderItems.set(elemPos - 1, temp);
      }
    }
    return "tab";
  }

  /**
   * Process the edit command.
   * @return navigation outcome to tab customization page (edit)
   */
  public String processActionEdit()
  {
    LOG.debug("processActionEdit()");

    if (!hasValue(getUserId()))
    {
      FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage("UserId is missing."));
      return null;
    }
    tabUpdated=false;		//Reset display of text message on JSP
    prefExcludeItems = new ArrayList();
    prefOrderItems = new ArrayList();
    setUserEditingOn();
    List prefExclude = new Vector();
    List prefOrder = new Vector();
    
    Preferences prefs = m_preferencesService.getPreferences(getUserId());
    ResourceProperties props = prefs.getProperties("sakai.portal.sitenav");
    List l = props.getPropertyList("exclude");
    if (l != null)
    {
      prefExclude = l;
    }

    l = props.getPropertyList("order");
    if (l != null)
    {
      prefOrder = l;
    }

    List mySites = SiteService.getSites(
        org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
        null, null, null,
        org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC,
        null);
    // create excluded and order list of Sites and add balance mySites to excluded Site list for display in Form
    List ordered = new Vector();
    List excluded = new Vector();
    for (Iterator i = prefOrder.iterator(); i.hasNext();)
    {
      String id = (String) i.next();
      // find this site in the mySites list
      int pos = indexOf(id, mySites);
      if (pos != -1)
      {
        // move it from mySites to order
        Site s = (Site) mySites.get(pos);
        ordered.add(s);
        mySites.remove(pos);
      }
    }
    for (Iterator iter = prefExclude.iterator(); iter.hasNext();)
    {
      String element = (String) iter.next();
      int pos = indexOf(element, mySites);
      if (pos != -1)
      {
        Site s = (Site) mySites.get(pos);
        excluded.add(s);
        mySites.remove(pos);
      }
    }
    // pick up the rest of the sites if not available with exclude and order list 
    // and add to the ordered list as when a new site is created it is displayed in header
    ordered.addAll(mySites);

    //Now convert to SelectItem for display in JSF
    for (Iterator iter = excluded.iterator(); iter.hasNext();)
    {
      Site element = (Site) iter.next();
      SelectItem excludeItem = new SelectItem(element.getId(), element
          .getTitle());
      prefExcludeItems.add(excludeItem);
    }

    for (Iterator iter = ordered.iterator(); iter.hasNext();)
    {
      Site element = (Site) iter.next();
      SelectItem orderItem = new SelectItem(element.getId(), element.getTitle());
      prefOrderItems.add(orderItem);
    }
    //release lock
    m_preferencesService.cancel(m_edit);
    return "tab";
  }

  /**
   * Process the save command from the edit view.
   * @return navigation outcome to tab customization page (edit)
   */
  public String processActionSave()
  {
    LOG.debug("processActionSave()");

    setUserEditingOn();
    //Remove existing property	
    ResourcePropertiesEdit props = m_edit
        .getPropertiesEdit("sakai.portal.sitenav");
    props.removeProperty("exclude");
    props.removeProperty("order");
    //Commit to remove from database, for next set of value storing
    m_preferencesService.commit(m_edit);

    m_stuff = new Vector();
    String oparts = "";
    String eparts = "";
    for (int i = 0; i < prefExcludeItems.size(); i++)
    {
      SelectItem item = (SelectItem) prefExcludeItems.get(i);
      String evalue = (String) item.getValue();
      eparts += evalue + ", ";
    }
    for (int i = 0; i < prefOrderItems.size(); i++)
    {
      SelectItem item = (SelectItem) prefOrderItems.get(i);
      String value = (String) item.getValue();
      oparts += value + ", ";
    }
    //add property name and value for saving
    m_stuff.add(new KeyNameValue("sakai.portal.sitenav", "exclude", eparts,
        true));
    m_stuff
        .add(new KeyNameValue("sakai.portal.sitenav", "order", oparts, true));
    //TODO tab size is set to 4 by default. i can't set null , not "" as in portal code "" will be number to display on tab
    //m_stuff.add(new KeyNameValue("sakai.portal.sitenav", "tabs", "4", false));

    // save
    saveEdit();
    // release lock and clear session variables
    cancelEdit();
    //To stay on the same page - load the page data
    processActionEdit();
    tabUpdated=true;		//set for display of text message on JSP
    
    // schedule a "peer" html element refresh, to update the site nav tabs
    // TODO: hard coding this frame id is fragile, portal dependent, and needs to be fixed -ggolden
    setRefreshElement("sitenav");

    return "tab";
  }

  /**
   * Process the cancel command from the edit view.
   * @return navigation outcome to tab customization page (edit)
   */
  public String processActionCancel()
  {
    LOG.debug("processActionCancel()");

    // remove session variables
    cancelEdit();
    //To stay on the same page - load the page data
    processActionEdit();
    return "tab";
  }

  /**
   * Process the cancel command from the edit view.
   * @return navigation outcome to Notification page (list)
   */
  public String processActionNotiFrmEdit()
  {
    LOG.debug("processActionNotiFrmEdit()");

    cancelEdit();
    // navigation page data are loaded through getter method as navigation is the default page for 'sakai.preferences' tool.
    return "noti";
  }

  /**
   * Process the cancel command from the edit view.
   * @return navigation outcome to timezone page (list)
   */
  public String processActionTZFrmEdit()
  {
    LOG.debug("processActionTZFrmEdit()");

    cancelEdit();
    // navigation page data are loaded through getter method as navigation is the default page for 'sakai.preferences' tool.
    return "timezone";
  }

  /**
   * This is called from edit page for navigation to refresh page
   * @return navigation outcome to refresh page (refresh)
   */
  public String processActionRefreshFrmEdit()
  {
    LOG.debug("processActionRefreshFrmEdit()");

    //is required as user editing is set on while entering to tab customization page
    cancelEdit();
    loadRefreshData();
    return "refresh";
  }

  //////////////////////////////////////   HELPER METHODS TO ACTIONS	////////////////////////////////////////////
  /**
   * Cancel the edit and cleanup.
   */
  protected void cancelEdit()
  {
    LOG.debug("cancelEdit()");

    // cleanup
    m_stuff = null;
    m_edit = null;
    prefExcludeItems = new ArrayList();
    prefOrderItems = new ArrayList();
    isNewUser = false;
    
    tabUpdated=false;
    notiUpdated=false;
    tzUpdated=false;    
    refreshUpdated=false;
  }

  /**
   * used with processActionAdd() and processActionRemove()
   * @return	SelectItem
   */
  private SelectItem removeItems(String value, List items, String addtype,
      String removetype)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("removeItems(String " + value + ", List " + items + ", String "
          + addtype + ", String " + removetype + ")");
    }

    SelectItem result = null;
    for (int i = 0; i < items.size(); i++)
    {
      SelectItem item = (SelectItem) items.get(i);
      if (value.equals(item.getValue()))
      {
        result = (SelectItem) items.remove(i);
        break;
      }
    }
    return result;
  }

  /**
   * Set editing mode on for user and add user if not existing
   */
  protected void setUserEditingOn()
  {
    LOG.debug("setUserEditingOn()");

    try
    {
      m_edit = m_preferencesService.edit(getUserId());
    }
    catch (IdUnusedException e)
    {
      try
      {
        m_edit = m_preferencesService.add(getUserId());
        isNewUser = true;
      }
      catch (Exception ee)
      {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(ee.toString()));
      }
    }
    catch (Exception e)
    {
      FacesContext.getCurrentInstance().addMessage(null,
          new FacesMessage(e.toString()));
    }
  }

  /**
   * Save any changed values from the edit and cleanup.
   */
  protected void saveEdit()
  {
    LOG.debug("saveEdit()");

    //user editing is required as commit() disable isActive() flag
    setUserEditingOn();
    // move the stuff from m_stuff into the edit
    for (Iterator i = m_stuff.iterator(); i.hasNext();)
    {
      KeyNameValue knv = (KeyNameValue) i.next();
      // find the original to remove (unless this one was new)
      if (!knv.getOrigKey().equals(""))
      {
        ResourcePropertiesEdit props = m_edit.getPropertiesEdit(knv
            .getOrigKey());
        props.removeProperty(knv.getOrigName());
      }
      // add the new if we have a key and name and value
      if ((!knv.getKey().equals("")) && (!knv.getName().equals(""))
          && (!knv.getValue().equals("")))
      {
        ResourcePropertiesEdit props = m_edit.getPropertiesEdit(knv.getKey());
        if (knv.isList())
        {
          // split by ", "
          String[] parts = knv.getValue().split(", ");
          for (int p = 0; p < parts.length; p++)
          {
            props.addPropertyToList(knv.getName(), parts[p]);
          }
        }
        else
        {
          props.addProperty(knv.getName(), knv.getValue());
        }
      }
    }
    // save the preferences, release the edit
    m_preferencesService.commit(m_edit);
  }

  /**
   * Check String has value, not null
   * @return boolean
   */
  protected boolean hasValue(String eval)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("hasValue(String " + eval + ")");
    }

    if (eval != null && !eval.trim().equals(""))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  // Copied from CheronPortal.java
  /**
   * Find the site in the list that has this id - return the position.	 * 
   * @param value The site id to find.
   * @param siteList The list of Site objects.
   * @return The index position in siteList of the site with site id = value, or -1 if not found.
   */
  protected int indexOf(String value, List siteList)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("indexOf(String " + value + ", List " + siteList + ")");
    }

    for (int i = 0; i < siteList.size(); i++)
    {
      Site site = (Site) siteList.get(i);
      if (site.equals(value))
      {
        return i;
      }
    }
    return -1;
  }

  ////////////////////////////////////   NOTIFICATION ACTIONS  ////////////////////////////////
  private String selectedAnnItem = "";
  private String selectedMailItem = "";
  private String selectedRsrcItem = "";
  private String selectedSyllItem = ""; //syllabus notification
  protected boolean notiUpdated=false ;
  protected boolean tzUpdated=false;  
  ///////////////////////////////////		GETTER AND SETTER		///////////////////////////////////
  //TODO chec for any preprocessor for handling request for first time. This can simplify getter() methods as below	
  /**
   * @return Returns the selectedAnnItem.
   */
  public String getSelectedAnnItem()
  {
    LOG.debug("getSelectedAnnItem()");

    if (!hasValue(selectedAnnItem))
    {
      Preferences prefs = (PreferencesEdit) m_preferencesService
          .getPreferences(getUserId());
      String a = buildTypePrefsContext(AnnouncementService.SERVICE_NAME,
          "annc", selectedAnnItem, prefs);
      if (hasValue(a))
      {
        selectedAnnItem = a; //load from saved data
      }
      else
      {
        selectedAnnItem = "3"; //default setting
      }
    }
    return selectedAnnItem;
  }

  /**
   * @param selectedAnnItem The selectedAnnItem to set.
   */
  public void setSelectedAnnItem(String selectedAnnItem)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedAnnItem(String " + selectedAnnItem + ")");
    }

    this.selectedAnnItem = selectedAnnItem;
  }

  /**
   * @return Returns the selectedMailItem.
   */
  public String getSelectedMailItem()
  {
    LOG.debug("getSelectedMailItem()");

    if (!hasValue(this.selectedMailItem))
    {
      Preferences prefs = (PreferencesEdit) m_preferencesService
          .getPreferences(getUserId());
      String a = buildTypePrefsContext(MailArchiveService.SERVICE_NAME, "mail",
          selectedMailItem, prefs);
      if (hasValue(a))
      {
        selectedMailItem = a; //load from saved data
      }
      else
      {
        selectedMailItem = "3"; //default setting
      }
    }
    return selectedMailItem;
  }

  /**
   * @param selectedMailItem The selectedMailItem to set.
   */
  public void setSelectedMailItem(String selectedMailItem)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedMailItem(String " + selectedMailItem + ")");
    }

    this.selectedMailItem = selectedMailItem;
  }

  /**
   * @return Returns the selectedRsrcItem.
   */
  public String getSelectedRsrcItem()
  {
    LOG.debug("getSelectedRsrcItem()");

    if (!hasValue(this.selectedRsrcItem))
    {
      Preferences prefs = (PreferencesEdit) m_preferencesService
          .getPreferences(getUserId());
      String a = buildTypePrefsContext(ContentHostingService.SERVICE_NAME,
          "rsrc", selectedRsrcItem, prefs);
      if (hasValue(a))
      {
        selectedRsrcItem = a; //load from saved data
      }
      else
      {
        selectedRsrcItem = "3"; //default setting
      }
    }
    return selectedRsrcItem;
  }

  /**
   * @param selectedRsrcItem The selectedRsrcItem to set.
   */
  public void setSelectedRsrcItem(String selectedRsrcItem)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedRsrcItem(String " + selectedRsrcItem + ")");
    }

    this.selectedRsrcItem = selectedRsrcItem;
  }

  //syllabus
  public String getSelectedSyllItem()
  {
    LOG.debug("getSelectedSyllItem()");

    if (!hasValue(this.selectedSyllItem))
    {
      Preferences prefs = (PreferencesEdit) m_preferencesService
          .getPreferences(getUserId());
      String a = buildTypePrefsContext("org.sakaiproject.api.app.syllabus.SyllabusService",
          "syll", selectedSyllItem, prefs);
      if (hasValue(a))
      {
        selectedSyllItem = a; //load from saved data
      }
      else
      {
        selectedSyllItem = "2"; //default setting
      }
    }
    return selectedSyllItem;
  }
  public void setSelectedSyllItem(String selectedSyllItem)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedRsrcItem(String " + selectedRsrcItem + ")");
    }

    this.selectedSyllItem = selectedSyllItem;
  }
  
  /**
   * @return Returns the notiUpdated.
   */
  public boolean getNotiUpdated()
  {
    return notiUpdated;
  }
  /**
   * @param notiUpdated The notiUpdated to set.
   */
  public void setNotiUpdated(boolean notiUpdated)
  {
    this.notiUpdated = notiUpdated;
  }
  
  /**
   * @return Returns the tzUpdated.
   */
  public boolean getTzUpdated()
  {
    return tzUpdated;
  }
  /**
   * @param notiUpdated The tzUpdated to set.
   */
  public void setTzUpdated(boolean tzUpdated)
  {
    this.tzUpdated = tzUpdated;
  }
  
  /////////////////////////////////////////NOTIFICATION ACTION - copied from NotificationprefsAction.java////////
  //TODO - clean up method call. These are basically copied from legacy legacy implementations.
  /**
   * Process the save command from the edit view.
   * @return navigation outcome to notification page
   */
  public String processActionNotiSave()
  {
    LOG.debug("processActionNotiSave()");

    // get an edit
    setUserEditingOn();
    if (m_edit != null)
    {
      //ResourcePropertiesEdit props = m_edit.getPropertiesEdit(NotificationService.PREFS_DEFAULT);

      // read each type specific %%% hard coded! -ggolden
      readTypePrefs(AnnouncementService.SERVICE_NAME, "annc", m_edit,
          getSelectedAnnItem());
      readTypePrefs(MailArchiveService.SERVICE_NAME, "mail", m_edit,
          getSelectedMailItem());
      readTypePrefs(ContentHostingService.SERVICE_NAME, "rsrc", m_edit,
          getSelectedRsrcItem());
      //save syllabus 
      readTypePrefs("org.sakaiproject.api.app.syllabus.SyllabusService", "syll", m_edit,
          getSelectedSyllItem());
      // update the edit and release it
      m_preferencesService.commit(m_edit);
    }
    //release lock not required as comit disable isActive() flag
    //		m_preferencesService.cancel(m_edit);	
    notiUpdated=true ;		//set for display of text massage
    return "noti";
  }

  /**
   * process notification cancel
   * @return navigation outcome to notification page
   */
  public String processActionNotiCancel()
  {
    LOG.debug("processActionNotiCancel()");

    loadNotiData();
    return "noti";
  }

  /**
   * Process the save command from the edit view.
   * @return navigation outcome to timezone page
   */
  public String processActionTzSave()
  {
     setUserEditingOn();
     ResourcePropertiesEdit props = m_edit.getPropertiesEdit( TimeService.SERVICE_NAME );
     props.addProperty( TimeService.TIMEZONE_KEY, m_timeZone.getID()  );  
     m_preferencesService.commit(m_edit);
     TimeService.clearLocalTimeZone( getUserId() );
    
     tzUpdated=true ;		//set for display of text massage
     return "timezone";
  }

  /**
   * process notification cancel
   * @return navigation outcome to timezone page
   */
  public String processActionTzCancel()
  {
    LOG.debug("processActionTzCancel()");
    
    // restore original time zone
    m_timeZone = null; 
    getSelectedTimeZone();
    
    return "timezone";
  }

  /**
   * This is called from notification page for navigation to Refresh page
   * @return navigation outcome to refresh page
   */
  public String processActionRefreshFrmNoti()
  {
    LOG.debug("processActionRefreshFrmNoti()");

    loadRefreshData();
    return "refresh";
  }

  ////////////////////////////////////////	HELPER	METHODS FOR NOTIFICATIONS	/////////////////////////////////////	
  /**
   * Load saved notification data - 
   * this is called from cancel button of notification page as navigation stays in the page
   */
  protected void loadNotiData()
  {
    LOG.debug("loadNotiData()");

    selectedAnnItem = "";
    selectedMailItem = "";
    selectedRsrcItem = "";
    selectedSyllItem = "";
    notiUpdated=false;
    Preferences prefs = (PreferencesEdit) m_preferencesService
        .getPreferences(getUserId());
    String a = buildTypePrefsContext(AnnouncementService.SERVICE_NAME, "annc",
        selectedAnnItem, prefs);
    if (hasValue(a))
    {
      selectedAnnItem = a; //load from saved data
    }
    else
    {
      selectedAnnItem = "3"; //default setting
    }
    String m = buildTypePrefsContext(MailArchiveService.SERVICE_NAME, "mail",
        selectedMailItem, prefs);
    if (hasValue(m))
    {
      selectedMailItem = m; //load from saved data
    }
    else
    {
      selectedMailItem = "3"; //default setting
    }
    String r = buildTypePrefsContext(ContentHostingService.SERVICE_NAME,
        "rsrc", selectedRsrcItem, prefs);
    if (hasValue(r))
    {
      selectedRsrcItem = r; //load from saved data
    }
    else
    {
      selectedRsrcItem = "3"; //default setting
    }
    //syllabus
    String s = buildTypePrefsContext("org.sakaiproject.api.app.syllabus.SyllabusService",
        "syll", selectedSyllItem, prefs);
    if (hasValue(s))
    {
      selectedSyllItem = s; //load from saved data
    }
    else
    {
      selectedSyllItem = "2"; //default setting
    }
  }

  /**
   * Read the two context references for defaults for this type from the form.
   * @param type The resource type (i.e. a service name).
   * @param prefix The prefix for context references.
   * @param edit The preferences being edited.
   * @param data The rundata with the form fields.
   */
  protected void readTypePrefs(String type, String prefix,
      PreferencesEdit edit, String data)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("readTypePrefs(String " + type + ", String " + prefix
          + ", PreferencesEdit " + edit + ", String " + data + ")");
    }

    // update the default settings from the form
    ResourcePropertiesEdit props = edit
        .getPropertiesEdit(NotificationService.PREFS_TYPE + type);
    // read the defaults
    props
        .addProperty(Integer.toString(NotificationService.NOTI_OPTIONAL), data);

  } // readTypePrefs

  /**
   * Add the two context references for defaults for this type.
   * @param type The resource type (i.e. a service name).
   * @param prefix The prefix for context references.
   * @param context The context.
   * @param prefs The full set of preferences.
   */
  protected String buildTypePrefsContext(String type, String prefix,
      String context, Preferences prefs)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("buildTypePrefsContext(String " + type + ", String " + prefix
          + ", String " + context + ", Preferences " + prefs + ")");
    }

    ResourceProperties props = prefs
        .getProperties(NotificationService.PREFS_TYPE + type);
    String value = "";
    value = props.getProperty(new Integer(NotificationService.NOTI_OPTIONAL)
        .toString());
    //		String value=(String)props.getProperty("2");
    return value;
  } // buildTypePrefsContext

  ////////////////////////////////////////			REFRESH		//////////////////////////////////////////
  private String selectedRefreshItem = "";
  protected boolean refreshUpdated=false ;
  /**
   * @return Returns the selectedRefreshItem.
   */
  public String getSelectedRefreshItem()
  {
    return selectedRefreshItem;
  }

  /**
   * @param selectedRefreshItem The selectedRefreshItem to set.
   */
  public void setSelectedRefreshItem(String selectedRefreshItem)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSelectedRefreshItem(String " + selectedRefreshItem + ")");
    }

    this.selectedRefreshItem = selectedRefreshItem;
  }

  /**
   * process saving of refresh 
   * @return navigation outcome to refresh page
   */
  public String processActionRefreshSave()
  {
    LOG.debug("processActionRefreshSave()");

    // get an edit
    setUserEditingOn();
    if (m_edit != null)
    {
      setStringPref(PortalService.SERVICE_NAME, "refresh", m_edit,
          getSelectedRefreshItem());
      // update the edit and release it
      m_preferencesService.commit(m_edit);
    }
    refreshUpdated=true;
    return "refresh";
  }

  /**
   * Process cancel and navigate to list page.
   * @return navigation outcome to refresh page
   */
  public String processActionRefreshCancel()
  {
    LOG.debug("processActionRefreshCancel()");

    loadRefreshData();
    return "refresh";
  }

  /**
   * Process cancel and navigate to list page.
   * @return navigation outcome to notification page
   */
  public String processActionNotiFrmRefresh()
  {
    LOG.debug("processActionNotiFrmRefresh()");

    loadNotiData();
    return "noti";
  }

  ///////////////////////////////////////		HELPER METHODS FOR REFRESH		/////////////////////////
  /**
   * Load refresh data from stored information. This is called when navigated into this page for first time.
   */
  protected void loadRefreshData()
  {
    LOG.debug("loadRefreshData()");

    selectedRefreshItem = "";
    refreshUpdated=false;
    if (!hasValue(selectedRefreshItem))
    {
      Preferences prefs = (PreferencesEdit) m_preferencesService
          .getPreferences(getUserId());
      String a = getStringPref(PortalService.SERVICE_NAME, "refresh", prefs);
      if (hasValue(a))
      {
        setSelectedRefreshItem(a); //load from saved data
      }
      else
      {
        setSelectedRefreshItem("2"); //default setting
      }
    }
  }

  /**
   * Set an integer preference.
   * @param pres_base The name of the group of properties (i.e. a service name)
   * @param type The particular property
   * @param edit An edit version of the full set of preferences for the current logged in user.
   * @param newval The string to be the new preference.
   */
  protected void setStringPref(String pref_base, String type,
      PreferencesEdit edit, String newval)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setStringPref(String " + pref_base + ", String " + type
          + ", PreferencesEdit " + edit + ", String " + newval + ")");
    }

    ResourcePropertiesEdit props = edit.getPropertiesEdit(pref_base);

    props.addProperty(type, newval);
  } // setStringPref

  /**
   * Retrieve a preference 
   * @param pres_base The name of the group of properties (i.e. a service name)
   * @param type The particular property
   * @param prefs The full set of preferences for the current logged in user.
   */
  protected String getStringPref(String pref_base, String type,
      Preferences prefs)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getStringPref(String " + pref_base + ", String " + type
          + ", PreferencesEdit " + prefs + ")");
    }

    ResourceProperties props = prefs.getProperties(pref_base);
    String a = props.getProperty(type);
    return a;
  } // getIntegerPref

  /** The html "peer" element to refresh on the next rendering. */
  protected String m_refreshElement = null;

  /**
   * Get, and clear, the refresh element
   * @return The html "peer" element to refresh on the next rendering, or null if none defined.
   */
  public String getRefreshElement()
  {
  	String rv = m_refreshElement;
  	m_refreshElement = null;
  	return rv;
  }

  /**
   * Set the "peer" html element to refresh on the next rendering.
   * @param element
   */
  public void setRefreshElement(String element)
  {
  	m_refreshElement = element;
  }
}



