package org.sakaiproject.tool.syllabus;

import org.sakaiproject.service.framework.log.Logger;
//import org.sakaiproject.jsf.component.FileInput;
//import org.sakaiproject.jsf.component.FileUpload;

import javax.servlet.http.HttpServletResponse;
import javax.faces.context.FacesContext;
/*
 * import javax.faces.context.ExternalContext; import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.RequestDispatcher;
 */
import javax.servlet.http.HttpServletResponse;
//add siteId
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
/*
 * import javax.faces.context.ExternalContext; import javax.servlet.http.HttpServletRequest; import
 * org.sakaiproject.util.ParameterParser;
 */

import org.apache.commons.fileupload.*;

//import org.apache.commons.fileupload.FileUploadBase;

import com.sun.faces.util.MessageFactory;

//import org.sakaiproject.component.syllabus.StringPool;
/*
 * import org.sakaiproject.service.syllabus.SyllabusEntry; import
 * org.sakaiproject.service.syllabus.SyllabusService;
 */
//for refactoring - use SyllabusService to get the comparator
//import org.sakaiproject.service.syllabus.EntryComparator;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Set;
import java.util.Iterator;

//get database work
import org.sakaiproject.service.syllabus.data.SyllabusData;
import org.sakaiproject.service.syllabus.data.SyllabusItem;
import org.sakaiproject.service.syllabus.data.SyllabusManager;
//import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.jsf.ToolBean;

//test wysiwyg for Lydia
/*
 * import javax.faces.event.ValueChangeEvent; import javax.faces.event.ActionEvent;
 */

/**
 * @author cwen TODO To change the template for this generated type comment go to Window - Preferences -
 *         Java - Code Style - Code Templates
 */
public class SyllabusTool implements ToolBean
{
  public class DecoratedSyllabusEntry
  {
    protected SyllabusData in_entry = null;

    protected boolean selected = false;

    protected boolean justCreated = false;

    public DecoratedSyllabusEntry(SyllabusData en)
    {
      in_entry = en;
    }

    public SyllabusData getEntry()
    {
      return in_entry;
    }

    public boolean isJustCreated()
    {
      return justCreated;
    }

    public boolean isSelected()
    {
      return selected;
    }

    public void setSelected(boolean b)
    {
      selected = b;
    }

    public void setJustCreated(boolean b)
    {
      justCreated = b;
    }

    public String processListRead()
    {
      logger.info(this + ".processListRead() in SyllabusTool.");

      entry = this;

      entries.clear();

      return "read";
    }

    public String processDownMove()
    {
      downOnePlace(this.getEntry());
      return "main_edit";
    }

    public String processUpMove()
    {
      upOnePlace(this.getEntry());
      return "main_edit";
    }
  }

  protected SyllabusManager syllabusManager;

  protected SyllabusItem syllabusItem;

  protected ArrayList entries;

  protected String userId;

  protected DecoratedSyllabusEntry entry = null;

  protected Logger logger = null;

  protected String filename = null;

  //add siteId
  protected String siteId = null;

  //	protected FileUpload fileUpload;

  //testing the access to control the "create/edit"
  //button showing up or not on main page.
  protected String editAble = null;

  protected String title = null;
  private boolean displayNoEntryMsg = false;

  public SyllabusTool()
  {
  }

  public boolean getdisplayNoEntryMsg()
  {
    return this.displayNoEntryMsg;
  }

  public ArrayList getEntries() throws PermissionException
  {
    if (userId == null) userId = UserDirectoryService.getCurrentUser().getId();
    //add siteId
    //	  String currentSiteId = PortalService.getCurrentSiteId();
    String currentSiteId = PortalService.getCurrentSitePageId();

    if ((entries == null) || (entries.isEmpty())
        || ((currentSiteId != null) && (!currentSiteId.equals(siteId))))
    {
      logger.info(this + ".getEntries() in SyllabusTool");

      siteId = currentSiteId;
      try
      {
        if (entries == null)
          entries = new ArrayList();
        else
          entries.clear();
        //add siteId
        //				ArrayList tempEntries = syllabusService.findByName("");
        /*
         * HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
         * .getExternalContext().getRequest(); String siteId = ((ParameterParser)
         * req.getAttribute("sakai.wrapper.params")).getString("site");
         */
        //remove userId for siteContext
        //			  syllabusItem = syllabusManager.getSyllabusItemByUserAndContextIds(userId, siteId);
        syllabusItem = syllabusManager.getSyllabusItemByContextId(siteId);
        if (syllabusItem == null)
        {
          if (!this.checkAccess())
          {
            throw new PermissionException(UsageSessionService
                .getSessionUserId(), "syllabus_access_athz", "");
          }
          else
          {
            syllabusItem = syllabusManager.createSyllabusItem(userId, siteId,
                null);
          }
        }

        /*
         * if((syllabusItem.getRedirectURL()!=null) && (!syllabusItem.getRedirectURL().equals(""))) {
         * HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance()
         * .getExternalContext().getResponse(); res.sendRedirect(syllabusItem.getRedirectURL());
         * entries.clear(); entry = null; }
         */
        Set tempEntries = syllabusManager
            .getSyllabiForSyllabusItem(syllabusItem);

        if (tempEntries != null)
        {
          Iterator iter = tempEntries.iterator();
          while (iter.hasNext())
          {
            SyllabusData en = (SyllabusData) iter.next();
            if (this.checkAccess())
            {
              DecoratedSyllabusEntry den = new DecoratedSyllabusEntry(en);
              entries.add(den);
            }
            else
            {
              if (en.getStatus().equals("Posted"))
              {
                DecoratedSyllabusEntry den = new DecoratedSyllabusEntry(en);
                entries.add(den);
              }
            }
          }
        }
      }
      catch (Exception e)
      {
        logger.info(this + ".getEntries() in SyllabusTool " + e);
        FacesContext.getCurrentInstance().addMessage(
            null,
            MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                "error_permission", (new Object[] { e.toString() })));
      }
    }
    else
    {
      try
      {
        siteId = currentSiteId;
        if ((userId != null) && (siteId != null))
        {
          //		    remove userId for siteContext
          //		      syllabusItem = syllabusManager.getSyllabusItemByUserAndContextIds(userId, siteId);
          syllabusItem = syllabusManager.getSyllabusItemByContextId(siteId);
        }
        /*
         * if(syllabusItem != null) { if((syllabusItem.getRedirectURL()!=null) &&
         * (!syllabusItem.getRedirectURL().equals(""))) { HttpServletResponse res =
         * (HttpServletResponse) FacesContext.getCurrentInstance() .getExternalContext().getResponse();
         * res.sendRedirect(syllabusItem.getRedirectURL()); entries.clear(); entry = null; } }
         */
      }
      catch (Exception e)
      {
        logger.info(this + ".getEntries() in SyllabusTool for redirection" + e);
        FacesContext.getCurrentInstance().addMessage(
            null,
            MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                "error_permission", (new Object[] { e.toString() })));
      }
    }
    if (entries == null || entries.isEmpty())
    {
      this.displayNoEntryMsg = true;
    }
    else
    {
      this.displayNoEntryMsg = false;
    }
    return entries;
  }

  public DecoratedSyllabusEntry getEntry()
  {
    return entry;
  }

  public ArrayList getSelectedEntries()
  {
    ArrayList rv = new ArrayList();

    if ((entry != null) && (entry.isSelected()))
    {
      rv.add(entry);
    }
    else
    {
      for (int i = 0; i < entries.size(); i++)
      {
        DecoratedSyllabusEntry den = (DecoratedSyllabusEntry) entries.get(i);
        if (den.isSelected())
        {
          rv.add(den);
        }
      }
    }
    return rv;
  }

  public SyllabusManager getSyllabusManager()
  {
    return syllabusManager;
  }

  public void setSyllabusManager(SyllabusManager syllabusManager)
  {
    this.syllabusManager = syllabusManager;
  }

  public SyllabusItem getSyllabusItem() throws PermissionException
  {
    String currentSiteId = PortalService.getCurrentSitePageId();
    String currentUserId = UserDirectoryService.getCurrentUser().getId();
    try
    {
      //    remove userId for siteContext
      //      syllabusItem = syllabusManager.
      //      				getSyllabusItemByUserAndContextIds(currentUserId,
      //      				    currentSiteId);
      syllabusItem = syllabusManager.getSyllabusItemByContextId(currentSiteId);

      if (syllabusItem == null)
      {
        if (!this.checkAccess())
        {
          throw new PermissionException(UsageSessionService.getSessionUserId(),
              "syllabus_access_athz", "");
        }
        else
        {
          syllabusItem = syllabusManager.createSyllabusItem(currentUserId,
              currentSiteId, null);
        }
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".getSyllabusItem() in SyllabusTool " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }
    return syllabusItem;
  }

  public void setSyllabusItem(SyllabusItem syllabusItem)
  {
    this.syllabusItem = syllabusItem;
  }

  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }

  public String getFilename()
  {
    logger.info(this + ".getFilename() in SyllabusTool");
    return filename;
  }

  public void setFilename(String filename)
  {
    logger.info(this + ".setFilename() in SyllabusTool");
    this.filename = filename;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getSiteId()
  {
    return siteId;
  }

  public void setSiteId(String siteId)
  {
    this.siteId = siteId;
  }

  //testing the access to control the "create/edit"
  //button showing up or not on main page.
  public String getEditAble()
  {
    if (checkAccess())
    {
      editAble = "true";
    }
    else
    {
      editAble = null;
    }
    return editAble;
  }

  public void setEditAble(String editAble)
  {
    this.editAble = editAble;
  }

  /*
   * public FileUpload getFileUpload() { return fileUpload; } public void setFileUpload(FileUpload
   * fileUpload) { this.fileUpload = fileUpload; }
   */

  public String processDeleteCancel()
  {
    logger.info(this + ".processDeleteCancel() in SyllabusTool.");

    entries.clear();
    entry = null;
    syllabusItem = null;

    return "main_edit";
  }

  public String processDelete()
      throws org.sakaiproject.exception.PermissionException
  {
    logger.info(this + ".processDelete() in SyllabusTool");

    ArrayList selected = getSelectedEntries();
    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        Set dataSet = syllabusManager.getSyllabiForSyllabusItem(syllabusItem);
        for (int i = 0; i < selected.size(); i++)
        {
          DecoratedSyllabusEntry den = (DecoratedSyllabusEntry) selected.get(i);
          /*
           * Iterator iter = dataSet.iterator(); while(iter.hasNext()) { SyllabusData data =
           * (SyllabusData)iter.next(); if(data.getSyllabusId().equals(den.getEntry().getSyllabusId())) {
           * syllabusManager.removeSyllabusFromSyllabusItem(syllabusItem, data); break; } }
           */
          syllabusManager.removeSyllabusFromSyllabusItem(syllabusItem, den
              .getEntry());
        }
      }
      entries.clear();
      entry = null;
      syllabusItem = null;

      return "main_edit";
    }
    catch (Exception e)
    {
      logger.info(this + ".processDelete: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    entries.clear();
    entry = null;
    syllabusItem = null;

    return "permission_error";
  }

  public String processEditCancel()
  {
    logger.info(this + ".processEditCancel() in SyllabusTool ");

    entries.clear();
    entry = null;
    syllabusItem = null;

    return "main_edit";
  }

  public String processEditSave() throws PermissionException
  {
    logger.info(this + ".processEditSave() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        if (entry.justCreated == true)
        {
          //syllabusManager.saveSyllabusItem(syllabusItem);
          syllabusManager.addSyllabusToSyllabusItem(syllabusItem, getEntry()
              .getEntry());
        }
      }

      entries.clear();
      entry = null;
      syllabusItem = null;

      return "main_edit";
    }
    catch (Exception e)
    {
      logger.info(this + ".processEditSave in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public String processEditPost() throws PermissionException
  {
    logger.info(this + ".processEditPost() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        if (entry.justCreated == true)
        {
          getEntry().getEntry().setStatus("Posted");
          syllabusManager.addSyllabusToSyllabusItem(syllabusItem, getEntry()
              .getEntry());
          //syllabusManager.saveSyllabusItem(syllabusItem);

          entries.clear();
          entry = null;
          syllabusItem = null;

          return "main_edit";
        }
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".processEditPost in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public String processListDelete() throws PermissionException
  {
    logger.info(this + ".processListDelete() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        ArrayList selected = getSelectedEntries();
        if (selected.isEmpty())
        {
          FacesContext.getCurrentInstance().addMessage(
              null,
              MessageFactory.getMessage(FacesContext.getCurrentInstance(),
                  "error_delete_select", null));

          return null;
        }
        return "delete_confirm";
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".processListDelete in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public String processListNew() throws PermissionException
  {
    logger.info(this + ".processListNew() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        int initPosition = syllabusManager.findLargestSyllabusPosition(
            syllabusItem).intValue() + 1;
        SyllabusData en = syllabusManager.createSyllabusDataObject(null,
            new Integer(initPosition), null, null, "Draft", "none");
        en.setView("no");

        entry = new DecoratedSyllabusEntry(en);
        entry.setJustCreated(true);

        entries.clear();

        return "edit";
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".processListNew in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));

      return "permission_error";
    }
  }

  public String processReadCancel()
  {
    logger.info(this + ".processReadCancel() in SyllabusTool");

    entries.clear();
    entry = null;
    syllabusItem = null;

    return "main_edit";
  }

  public String processReadSave() throws PermissionException
  {
    logger.info(this + ".processReadSave() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        if (entry.justCreated == false)
        {
          getEntry().getEntry().setStatus("Draft");
          syllabusManager.saveSyllabus(getEntry().getEntry());
          //syllabusManager.fakeupdateSyllabusData(syllabusItem, newData);

          /*
           * Set dataSet = syllabusItem.getSyllabi(); Iterator iter = dataSet.iterator();
           * while(iter.hasNext()) { SyllabusData data = (SyllabusData)iter.next();
           * if(data.getSyllabusId().equals(getEntry().getEntry().getSyllabusId())) {
           * data.fakeUpdate(getEntry().getEntry()); } } syllabusItem.setSyllabi(dataSet);
           * syllabusManager.saveSyllabusItem(syllabusItem);
           */
        }
      }
      entries.clear();
      entry = null;
      syllabusItem = null;

      return "main_edit";
    }
    catch (Exception e)
    {
      logger.info(this + ".processReadSave in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public String processReadPost() throws PermissionException
  {
    logger.info(this + ".processReadPost() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        if (entry.justCreated == false)
        {
          getEntry().getEntry().setStatus("Posted");
          syllabusManager.saveSyllabus(getEntry().getEntry());
          //syllabusManager.fakesaveSyllabusItem(syllabusItem, getEntry().getEntry());

          entries.clear();
          entry = null;
          syllabusItem = null;

          return "main_edit";
        }
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".processReadPost in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public void downOnePlace(SyllabusData en)
  {
    logger.info(this + ".downOnePlace() in SyllabusTool");

    SyllabusData swapData = null;
    Iterator iter = syllabusManager.getSyllabiForSyllabusItem(syllabusItem)
        .iterator();
    while (iter.hasNext())
    {
      SyllabusData data = (SyllabusData) iter.next();
      if (en.equals(data))
      {
        if (iter.hasNext()) swapData = (SyllabusData) iter.next();
        break;
      }
    }

    if (swapData != null)
        syllabusManager.swapSyllabusDataPositions(syllabusItem, en, swapData);

    entries.clear();
    entry = null;
    syllabusItem = null;
  }

  public void upOnePlace(SyllabusData en)
  {
    logger.info(this + ".upOnePlace() in SyllabusTool");

    SyllabusData swapData = null;
    Iterator iter = syllabusManager.getSyllabiForSyllabusItem(syllabusItem)
        .iterator();
    while (iter.hasNext())
    {
      SyllabusData data = (SyllabusData) iter.next();
      if (en.equals(data))
      {
        break;
      }
      else
      {
        swapData = data;
      }
    }

    if (swapData != null)
        syllabusManager.swapSyllabusDataPositions(syllabusItem, en, swapData);

    entries.clear();
    entry = null;
    syllabusItem = null;
  }

  public String processEditPreview()
  {
    return "preview";
  }

  public String processEditPreviewBack()
  {
    return "edit";
  }

  public String processReadPreview()
  {
    return "read_preview";
  }

  public String processReadPreviewBack()
  {
    return "read";
  }

  public String processEditUpload()
  {
    //TODO let the filter work and upload...
    /*
     * try { FacesContext fc = FacesContext.getCurrentInstance(); ExternalContext exFc =
     * fc.getExternalContext(); HttpServletRequest currentRequest = (HttpServletRequest)
     * exFc.getRequest(); String[] fileNames ={filename}; org.apache.commons.fileupload.FileUploadBase
     * fu = new org.apache.commons.fileupload.DiskFileUpload(); HttpServletRequest req =
     * HttpServletRequestFactory.createValidHttpServletRequest(fileNames); java.util.List itemList =
     * fu.parseRequest(req); } catch(Exception e) { e.printStackTrace(); }
     */

    filename = null;
    return "edit";
  }

  public String processReadUpload()
  {
    //TODO let the filter work and upload...
    filename = null;
    return "read";
  }

  public String processRedirect() throws PermissionException
  {
    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        return "edit_redirect";
      }
    }
    catch (Exception e)
    {
      logger.info(this + ".processRedirect in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }
    return "permission_error";
  }

  public String processEditCancelRedirect()
  {
    logger.info(this + ".processEditCancelRedirect() in SyllabusTool ");

    entries.clear();
    entry = null;
    syllabusItem = null;

    return "main_edit";
  }

  public String processEditSaveRedirect() throws PermissionException
  {
    logger.info(this + ".processEditSaveRedirect() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        syllabusManager.saveSyllabusItem(syllabusItem);

        entries.clear();
        entry = null;
        syllabusItem = null;
      }

      return "main_edit";
    }
    catch (Exception e)
    {
      logger.info(this + ".processEditSaveRedirect in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
    }

    return "permission_error";
  }

  public String processCreateAndEdit()
  {
    logger.info(this + ".processCreateAndEdit() in SyllabusTool");

    try
    {
      if (!this.checkAccess())
      {
        throw new PermissionException(UsageSessionService.getSessionUserId(),
            "syllabus_access_athz", "");
      }
      else
      {
        syllabusManager.saveSyllabusItem(syllabusItem);

        entries.clear();
        entry = null;
        syllabusItem = null;
      }

      return "main_edit";
    }
    catch (Exception e)
    {
      logger.info(this + ".processCreateAndEdit() in SyllabusTool: " + e);
      FacesContext.getCurrentInstance().addMessage(
          null,
          MessageFactory.getMessage(FacesContext.getCurrentInstance(),
              "error_permission", (new Object[] { e.toString() })));
      return "permission_error";
    }
  }

  public String processStudentView()
  {
    return "main";
  }

  public boolean checkAccess()
  {
    return SiteService.allowUpdateSite(PortalService.getCurrentSiteId());
  }

  //test wysiwyg for Lydia
  /*
   * public void processTestWysiwyg(ValueChangeEvent event) { FacesContext context =
   * FacesContext.getCurrentInstance(); String type = (String) event.getNewValue(); } public void
   * processTestWysiwyg1() { }
   */
  public String getTitle()
  {
    return SiteService.findTool(PortalService.getCurrentToolId()).getTitle();
  }
}