/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.ui.web.action;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.SectionImpl;
import org.navigoproject.business.entity.assessment.model.SectionIteratorImpl;
import org.navigoproject.business.entity.assessment.model.SectionPropertiesImpl;
import org.navigoproject.business.entity.properties.AssessmentProperties;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.data.dao.UtilAccessObject;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;
import org.navigoproject.settings.PathInfo;
import org.navigoproject.ui.web.form.edit.DeleteConfirmForm;
import org.navigoproject.ui.web.form.edit.DescriptionForm;
import org.navigoproject.ui.web.form.edit.FileUploadForm;
import org.navigoproject.ui.web.form.edit.MediaInterface;
import org.navigoproject.ui.web.form.edit.PartForm;
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.edit.ReorderForm;

import java.io.*;

import java.sql.Timestamp;

import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.*;
import org.apache.log4j.Logger;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

/**
 * This class handles the link and file upload media methods, and includes the
 * method that actually displays the media items.  The various uploads are
 * each done differently because they have to be loaded into different forms
 * and point to different pages, but they each call the same file upload in
 * the end.  Media items are stored in the media table in the back end and
 * database, with isLink=true if they are "related media" and false if they
 * are "inline media."
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:huong.t.nguyen@stanford.edu">Huong Nguyen</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @author <a href="mailto:zqingru@stanford.edu">Qingru Zhang</a>
 */
public class AssessmentMediaAction
  extends AAMAction
{
  // Initialize it here because it's static
  private static final String SETTINGS_FILE = "SAM.properties";
  public static HashMap mediaMap = new HashMap();
  public static HashMap mediaiconMap = new HashMap();
  static Logger LOG = Logger.getLogger(AssessmentMediaAction.class.getName());
  public static final int INVALID_ID = 0; // flags invalid media

  /**
   * This is the standard execute() method that handles action mappings and
   * calls the media-related methods.
   *
   * @param actionMapping the action mapping
   * @param actionForm the action form
   * @param httpServletRequest the request
   * @param httpServletResponse the response
   *
   * @return the ActionForward
   */
  public ActionForward execute(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse)
  {
    ActionForward forward = null;
    HttpSession session = httpServletRequest.getSession(true);

    LOG.info("action mapping: " + actionMapping.getPath());

    // using wysiwyg editor capture our form data and save it
    if(
      (actionMapping.getPath().indexOf("htmlInlineUpload") > -1) ||
        (actionMapping.getPath().indexOf("htmlInlineAnswer") > -1))
    {
      // log and list all parameters passed
      LOG.info("html inline from wysiwyg");
      Enumeration en = httpServletRequest.getParameterNames();
      while(en.hasMoreElements())
      {
        String key = (String) en.nextElement();
        String value = (String) httpServletRequest.getParameter(key);
        LOG.info(key + "='" + value + "'");
      }

      // now, process action form
      int id = doFileUploadInline(actionForm);
      FileUploadForm upload = (FileUploadForm) actionForm;
      upload.setMapId("" + id);
      LOG.info("Set form mapId='" + upload.getMapId() + "'");

      // returning to success screen
      return actionMapping.findForward("Success");
    }

    Assessment template =
      (Assessment) session.getAttribute("assessmentTemplate");
    AssessmentProperties properties = null;

    boolean isTemplate = false;
    if(template instanceof AssessmentTemplate)
    {
      isTemplate = true;
    }

    if(template != null)
    {
      try
      {
        properties = (AssessmentProperties) template.getData();
      }
      catch(AssessmentException ae)
      {
        LOG.error(ae);
        forward =
          forwardToFailure(
            "AssessmentMediaAction: " + ae.getMessage(), actionMapping,
            httpServletRequest);
      }
    }

    AssessmentTemplateProperties props = null;

    // Set a separate variable so we don't have to cast it each time.
    if(properties instanceof AssessmentTemplateProperties)
    {
      props = (AssessmentTemplatePropertiesImpl) properties;
    }

    // This displays the actual media item.
    if(actionMapping.getPath().indexOf("showMedia") > -1)
    {
      doShowMedia(httpServletRequest, httpServletResponse, template);
    }

    // This starts the file upload for part items
    else if(actionMapping.getPath().indexOf("startFileUploadPartItem") > -1)
    {
      try
      {
        doStartFileUploadPartItem(session, template, httpServletRequest);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }

      forward = actionMapping.findForward("fileUpload");
    }

    // This deletes a file upload from a part item
    else if(actionMapping.getPath().indexOf("deleteFileUploadPartItem") > -1)
    {
      try
      {
        DeleteConfirmForm form = new DeleteConfirmForm();
        if(httpServletRequest.getParameter("isLink").equals("false"))
        {
          form.setName("Inline Media");
        }
        else
        {
          form.setName("Related Files and Links");
        }

        session.setAttribute("deleteConfirm", form);
        session.setAttribute("mediaSource", "PartItem");
        session.setAttribute("assessmentTemplate", template);
        session.setAttribute("id", httpServletRequest.getParameter("id"));
        session.setAttribute("index", httpServletRequest.getParameter("index"));
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }

      forward = actionMapping.findForward("deleteConfirm");
    }

    // This starts a file upload for a part (not part item, as above)
    else if(
      (actionMapping.getPath().indexOf("startFileUploadPart") > -1) &&
        (actionMapping.getPath().indexOf("startFileUploadPartItem") == -1))
    {
      doStartFileUploadPart(session, httpServletRequest);
      forward = actionMapping.findForward("fileUpload");
    }

    // This deletes a file upload for a part
    else if(
      (actionMapping.getPath().indexOf("deleteFileUploadPart") > -1) &&
        (actionMapping.getPath().indexOf("deleteFileUploadPartItem") == -1))
    {
      DeleteConfirmForm form = new DeleteConfirmForm();
      if(httpServletRequest.getParameter("isLink").equals("false"))
      {
        form.setName("Inline Media");
      }
      else
      {
        form.setName("Related Files and Links");
      }

      session.setAttribute("deleteConfirm", form);
      if(session.getAttribute("isLink") == null)
      {
        session.setAttribute(
          "isLink", httpServletRequest.getParameter("isLink"));
      }

      session.setAttribute("index", httpServletRequest.getParameter("index"));
      session.setAttribute("mediaSource", "part");

      forward = actionMapping.findForward("deleteConfirm");
    }

    // This starts a file upload for a description
    else if(actionMapping.getPath().indexOf("startFileUploadDescription") > -1)
    {
      doStartFileUploadDescription(session, httpServletRequest);
      forward = actionMapping.findForward("fileUpload");
    }

    // This deletes a file upload from a description
    else if(actionMapping.getPath().indexOf("deleteFileUploadDescription") > -1)
    {
      DeleteConfirmForm form = new DeleteConfirmForm();
      if(httpServletRequest.getParameter("isLink").equals("false"))
      {
        form.setName("Inline Media");
      }
      else
      {
        form.setName("Related Files and Links");
      }

      session.setAttribute("deleteConfirm", form);
      if(session.getAttribute("isLink") == null)
      {
        session.setAttribute(
          "isLink", httpServletRequest.getParameter("isLink"));
      }

      session.setAttribute("index", httpServletRequest.getParameter("index"));
      session.setAttribute("mediaSource", "description");

      LOG.debug("Setting deleteConfirm forward");

      // This actually forwards to the description part of templateEditor
      forward = actionMapping.findForward("deleteConfirm");
    }

    // This really deletes a file upload for description or part
    else if(actionMapping.getPath().indexOf("deleteConfirm") > -1)
    {
      String source = (String) session.getAttribute("mediaSource");
      try
      {
        if(source.equals("description"))
        {
          doDeleteFileUploadDescription(session);
        }
        else if(source.equals("PartItem"))
        {
          doDeleteFileUploadPartItem(session);
        }
        else if(source.equals("part"))
        {
          doDeleteFileUploadPart(session);
        }
        else if(source.equals("question"))
        {
          doDeleteFileUploadQuestion(session);
        }
      }
      catch(Exception e)
      {
        LOG.error(e);
        forward =
          forwardToFailure(
            "AssessmentMediaAction: " + e.getMessage(), actionMapping,
            httpServletRequest);
      }

      if(source.equals("description"))
      {
        forward = actionMapping.findForward("description");
      }
      else if(source.equals("PartItem"))
      {
        forward = actionMapping.findForward("PartItem");
      }
      else if(source.equals("part"))
      {
        forward = actionMapping.findForward("part");
      }
      else if(source.equals("question"))
      {
        forward = actionMapping.findForward("question");
      }
    }

    // This starts the file upload for a question
    else if(actionMapping.getPath().indexOf("startFileUploadQuestion") > -1)
    {
      doStartFileUploadQuestion(session, httpServletRequest);
      forward = actionMapping.findForward("fileUpload");
    }

    // This deletes a file upload from a question
    else if(actionMapping.getPath().indexOf("deleteFileUploadQuestion") > -1)
    {
      try
      {
        DeleteConfirmForm form = new DeleteConfirmForm();
        if(httpServletRequest.getParameter("isLink").equals("false"))
        {
          form.setName("Inline Media");
        }
        else
        {
          form.setName("Related Files and Links");
        }

        session.setAttribute("deleteConfirm", form);
        session.setAttribute(
          "isLink", httpServletRequest.getParameter("isLink"));
        session.setAttribute("index", httpServletRequest.getParameter("index"));
        session.setAttribute("mediaSource", "question");
      }
      catch(Exception e)
      {
        LOG.error(e);
        forward =
          forwardToFailure(
            "AssessmentMediaAction: " + e.getMessage(), actionMapping,
            httpServletRequest);
      }

      forward = actionMapping.findForward("deleteConfirm");
    }

    // This has special handling for the mediaSource "partItem", because
    // it comes directly from a section, rather than from a form.
    else if(actionMapping.getPath().indexOf("fileUpload") > -1)
    {
      forward = actionMapping.findForward(doFileUpload(session, actionForm));
    }

    // Now we start the reorder stuff
    // ******************************
    // If it's a description reorder, upload the description media list
    else if(actionMapping.getPath().indexOf("startReorderDescription") > -1)
    {
      doStartReorderDescription(session, httpServletRequest);
      forward = actionMapping.findForward("reorder");
    }

    // If it's a part media reorder, upload the part media list
    else if(
      (actionMapping.getPath().indexOf("startReorderPart") > -1) &&
        (actionMapping.getPath().indexOf("startReorderPartItem") == -1))
    {
      //doStartReorderPart(session, httpServletRequest);
      //forward = actionMapping.findForward("reorder");
    }

    // If it's a part item media reorder, upload the section media list
    else if(actionMapping.getPath().indexOf("startReorderPartItem") > -1)
    {
      try
      {
        doStartReorderPartItem(session, httpServletRequest);
        forward = actionMapping.findForward("reorder");
      }
      catch(Exception e)
      {
        LOG.error(e);
        forward =
          forwardToFailure(
            "AssessmentMediaAction: " + e.getMessage(), actionMapping,
            httpServletRequest);
      }
    }

    // If it's a question media reorder, upload the question media list
    else if(actionMapping.getPath().indexOf("startReorderQuestion") > -1)
    {
      doStartReorderQuestion(session, httpServletRequest);
      forward = actionMapping.findForward("reorder");
    }

    // Do the actual reorder
    else if(actionMapping.getPath().indexOf("reorder") > -1)
    {
      forward =
        actionMapping.findForward(doReorder(session, httpServletRequest));
    }

    // If it's a delete, reorder or upload, save it.
    try
    {
      if(
        (actionMapping.getPath().indexOf("startAccess") == -1) &&
          (actionMapping.getPath().indexOf("showMedia") == -1) &&
          (actionMapping.getPath().indexOf("startReorder") == -1) &&
          (actionMapping.getPath().indexOf("startFileUpload") == -1))
      {
        ReadForms reader =
          new ReadForms(template, properties, props, session, isTemplate);
        if(
          (actionMapping.getPath().indexOf("deleteConfirm") > -1) &&
            ((String) session.getAttribute("mediaSource")).equals(
              "description"))
        {
          reader.doDescriptionForm();
        }

        if(
          (actionMapping.getPath().indexOf("deleteConfirm") > -1) &&
            ((String) session.getAttribute("mediaSource")).equals("part") &&
            isTemplate)
        {
          reader.doPartForm();
        }

        if(
          (httpServletRequest.getParameter("isMySubmit") == null) ||
            httpServletRequest.getParameter("isMySubmit").equals("true"))
        {
          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
          if(template instanceof AssessmentTemplate)
          {
            delegate.updateAssessmentTemplate((AssessmentTemplate) template);
          }
          else
          {
            delegate.updateAssessment(template);
          }
        }
      }
    }

    catch(Exception e)
    {
      LOG.error(e);
      forward =
        forwardToFailure(
          "AssessmentMediaAction: " + e.getMessage(), actionMapping,
          httpServletRequest);
    }

    if(forward == null)
    {
      // put the template object into the http session
      session.setAttribute("assessmentTemplate", template);

      forward = actionMapping.findForward("templateEditor");
    }

    return forward;
  }

  /**
   * Populates the file upload form.  This has special handling for "partItem",
   * because it doesn't come from a form, but comes directly from a section.
   *
   * @param session DOCUMENTATION PENDING
   * @param source DOCUMENTATION PENDING
   * @param indexString DOCUMENTATION PENDING
   */
  public void populateFileUpload(
    HttpSession session, String source, String indexString)
  {
    MediaInterface form = null;
    if(! source.equals("partItem"))
    {
      form = (MediaInterface) session.getAttribute(source);
    }

    boolean isLink =
      (((String) session.getAttribute("isLink")).equals("true") ? true : false);
    int index = new Integer(indexString).intValue();
    Collection media = null;
    if(form != null)
    {
      if(isLink)
      {
        media = form.getReversedRelatedMediaCollection();
      }
      else
      {
        media = form.getReversedMediaCollection();
      }
    }
    else
    {
      try
      {
        Section section = (Section) session.getAttribute("mediaSection");
        SectionPropertiesImpl sprops =
          (SectionPropertiesImpl) section.getData();
        media = sprops.getMediaCollection();
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }

    Iterator iter = media.iterator();
    for(int i = 0; i < index; i++)
    {
      iter.next();
    }

    MediaData data = (MediaData) iter.next();
    FileUploadForm upload = (FileUploadForm) session.getAttribute("fileUpload");
    if(upload == null)
    {
      upload = new FileUploadForm();
    }

    if(isLink)
    {
      upload.setTitle("Edit Related Files and Links");
    }
    else
    {
      upload.setTitle("Edit Inline Media");
    }

    upload.setName(data.getName());
    upload.setAuthor(data.getAuthor());
    upload.setDescription(data.getDescription());
    upload.setLink(data.getLocation());
    upload.setIsHtmlInline(data.getIsHtmlInline());
    upload.setFileName(data.getFileName());
    upload.setIsEdit(true);
    upload.setMapId(data.getMapId());
    session.setAttribute("getMedia", data);
    session.setAttribute("fileUpload", upload);
  }

  /**
   * Displays media; inline media are shown as-is, related as links. Media are
   * stored in the mediaMap, and retrieved as necessary. There is a trade-off
   * between filling up memory with large media objects, and loading the
   * server by loading each one from the database on request.  Either one will
   * scale badly; I tried to compromise by having a static map, so a gif that
   * shows up in an assessment will only be stored in memory once, and will be
   * pulled from memory by every student viewing that assessment.
   *
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param httpServletResponse DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   */
  public void doShowMedia(
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse, Assessment template)
  {
    String id = httpServletRequest.getParameter("id");

    if(id != null)
    {
      try
      {
        MediaData data = (MediaData) mediaMap.get(id);

        if(data == null)
        {
          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
          data = delegate.getMedia(Long.parseLong(id));
        }

        LOG.info("MediaData#=" + data.getId());
        LOG.info("MediaData is link=" + data.isLink());
        LOG.info("MediaData get is link=" + data.getIsLink());

        String location = data.getLocation();
        LOG.info("MediaData location =" + location);
        if((location != null) && location.startsWith("file://"))
        {
          // in file system, call the showMediaFile() helper method
          showMediaFile(data, httpServletResponse);
        }
        else
        {
          // otherwise display the raw data in content property
          showMediaRawContent(data, httpServletResponse);
        }
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }
  }

  /**
   * Helper method.
   * Displays media that is in the MediaData content property.
   *
   * @param md MediaData object to be shown
   * @param httpServletResponse the servlet response
   */
  private void showMediaRawContent(MediaData md, HttpServletResponse res)
    throws IOException
  {
    ServletOutputStream out = res.getOutputStream();
    String mimeType = md.getType();
    if (!("").equals(mimeType))
      res.setContentType(mimeType);
    else
      res.setContentType("application/octet-stream");

    byte[] dataBytes = md.getContent();
    res.setContentLength(dataBytes.length);
    res.flushBuffer();
    out.write(dataBytes);
    out.close();
  }

  /**
   * Helper method.
   * Displays media that is persisted to the file system with a file:// url
   *
   * @param md MediaData object to be shown
   * @param httpServletResponse the servlet response
   */
  private void showMediaFile(MediaData md, HttpServletResponse res)
    throws IOException
  {
    String location = md.getLocation();

    if((location == null) || ! location.startsWith("file://"))
    {
      LOG.warn("Unable to locate media.  Not a file url.");

      return;
    }

    String fileType = md.getType();
    String fileName = makeNameFromLocation(location);
    String fileToShowString = null;

    // get file to play by full filename
    if(! fileName.equals(""))
    {
      fileToShowString = fileName;
    }

    File fileToShow = null;
    String saveName = null;

    if(fileToShowString != null)
    {
      fileToShow = new File(fileToShowString);
      saveName = fileToShow.getName();
    }

    if((fileToShow == null) || fileToShow.exists() || fileToShow.canRead())
    {
      LOG.warn("Unable to locate media.  File cannot be read.");
    }

    res.setHeader(
      "Content-Disposition", fileType + "; filename=\"" + saveName + "\";");

    if(fileType != null)
    {
      res.setContentType(fileType);
    }
    else
    {
      //if mimetype is unknown set it to binary so it works fine in Netscape
      res.setContentType("application/octet-stream");
    }

    ServletOutputStream outputStream = res.getOutputStream();
    FileInputStream fileStream = null;

    try
    {
      fileStream = new FileInputStream(fileToShowString);
    }
    catch(FileNotFoundException e)
    {
      LOG.warn("File " + fileToShowString + " not found:" + e.getMessage());
    }

    //  Warning     Catch block is hidden by another one in the same try statement  MediaDataZipArchive.java  Navigo/src/org/navigoproject/business/entity/evaluation/model/util  line 216
    //    catch(IOException e)
    //    {
    //      LOG.warn(
    //        "File " + fileToShowString + " had IOException:" + e.getMessage());
    //    }
    // Some pdf files do not show up in the first attempt,
    // to fix this, we are sending the Content length to the response
    int i = 0;
    int count = 0;
    BufferedInputStream buf_inputStream = new BufferedInputStream(fileStream);
    BufferedOutputStream buf_outputStream =
      new BufferedOutputStream(outputStream);
    if(buf_inputStream != null)
    {
      while((i = buf_inputStream.read()) != -1)
      {
        buf_outputStream.write(i);
        count++;
      }
    }

    res.setContentLength(count);

    res.flushBuffer();

    buf_inputStream.close();

    buf_outputStream.close();
  }

  /**
   * Helper method.
   * Strip the "file://" or other 7 char heading
   * @param location
   * @return
   */
  private String makeNameFromLocation(String location)
  {
    String s = location + "            ";
    s = s.substring(7);
    s = s.trim();

    return s;
  }

  /**
   * The start methods just prepare the file upload windows as appropriate for
   * the calling screen.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doStartFileUploadPartItem(
    HttpSession session, Assessment template,
    HttpServletRequest httpServletRequest)
    throws Exception
  {
    session.setAttribute("mediaSource", "partItem");
    session.setAttribute("isLink", httpServletRequest.getParameter("isLink"));
    SectionIterator sii = template.getSections();
    Section section = null;
    while(sii.hasNext())
    {
      section = (Section) sii.next();
      if(
        section.getId().toString().equals(
            httpServletRequest.getParameter("id")))
      {
        break;
      }
    }

    session.setAttribute("mediaSection", section);
    if(httpServletRequest.getParameter("index") != null)
    {
      populateFileUpload(
        session, "partItem", (String) httpServletRequest.getParameter("index"));
    }
    else
    {
      FileUploadForm upload = new FileUploadForm();

      if((httpServletRequest.getParameter("isLink")).equals("true"))
      {
        upload.setTitle("Add Related Files and Links");
      }
      else
      {
        upload.setTitle("Add Inline Media");
      }

      session.setAttribute("getMedia", null);
      session.setAttribute("fileUpload", upload);
    }
  }

  /**
   * Delete media from a question template.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDeleteFileUploadPartItem(HttpSession session)
    throws Exception
  {
    Section section = null;
    Assessment template =
      (Assessment) session.getAttribute("assessmentTemplate");
    SectionIterator sii = template.getSections();
    while(sii.hasNext())
    {
      section = (Section) sii.next();
      if(section.getId().toString().equals(session.getAttribute("id")))
      {
        break;
      }
    }

    SectionPropertiesImpl sprops = (SectionPropertiesImpl) section.getData();
    int index =
      new Integer(((String) session.getAttribute("index"))).intValue();
    Collection newList = new ArrayList();
    int i = 0;
    Iterator iter = sprops.getMediaCollection().iterator();
    while(iter.hasNext())
    { // List is reversed
      if(i != (sprops.getMediaCollection().size() - 1 - index))
      {
        newList.add(iter.next());
      }
      else
      {
        iter.next();
      }

      i++;
    }

    // If this is a template, we need to fill in the PartForm as
    // well, so the info is visible.
    PartForm pform = (PartForm) session.getAttribute("part");
    if(pform == null)
    {
      pform = new PartForm();
    }

    iter = newList.iterator();
    Collection media = new ArrayList();
    Collection relatedMedia = new ArrayList();
    while(iter.hasNext())
    {
      MediaData md = (MediaData) iter.next();
      if(md.isLink())
      {
        relatedMedia.add(md);
      }
      else
      {
        media.add(md);
      }
    }

    pform.setRelatedMediaCollection(relatedMedia);
    pform.setMediaCollection(media);

    sprops.setMediaCollection(newList);
    section.updateData(sprops);
  }

  /**
   * The start methods just prepare the file upload windows as appropriate for
   * the calling screen.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartFileUploadPart(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    session.setAttribute("mediaSource", "part");
    session.setAttribute("isLink", httpServletRequest.getParameter("isLink"));
    if(httpServletRequest.getParameter("index") != null)
    {
      populateFileUpload(
        session, "part", (String) httpServletRequest.getParameter("index"));
    }
    else
    {
      FileUploadForm upload = new FileUploadForm();

      if((httpServletRequest.getParameter("isLink")).equals("true"))
      {
        upload.setTitle("Add Related Files and Links");
      }
      else
      {
        upload.setTitle("Add Inline Media");
      }

      session.setAttribute("getMedia", null);
      session.setAttribute("fileUpload", upload);
    }
  }

  /**
   * Delete media from a part.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDeleteFileUploadPart(HttpSession session)
    throws Exception
  {
    boolean isLink =
      (((String) session.getAttribute("isLink")).equals("true") ? true : false);
    PartForm form = (PartForm) session.getAttribute("part");
    int index =
      new Integer(((String) session.getAttribute("index"))).intValue();
    Collection media = null;
    if(isLink)
    {
      media = form.getRelatedMediaCollection();
    }
    else
    {
      media = form.getMediaCollection();
    }

    Iterator iter = media.iterator();
    for(int i = 0; i < (media.size() - 1 - index); i++)
    {
      iter.next();
    }

    MediaData data = (MediaData) iter.next();
    media.remove(data);
    if(isLink)
    {
      form.setRelatedMediaCollection(media);
    }
    else
    {
      form.setMediaCollection(media);
    }

    // This is only called from the template, so only one section.
    Assessment template =
      (Assessment) session.getAttribute("assessmentTemplate");
    SectionPropertiesImpl sprops = null;
    try
    {
      SectionIteratorImpl siter = (SectionIteratorImpl) template.getSections();
      SectionImpl section = (SectionImpl) siter.next();
      sprops = (SectionPropertiesImpl) section.getData();
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    sprops.setMediaCollection(new ArrayList());
    iter = form.getMediaCollection().iterator();
    while(iter.hasNext())
    {
      sprops.addMedia(iter.next());
    }

    iter = form.getRelatedMediaCollection().iterator();
    while(iter.hasNext())
    {
      sprops.addMedia(iter.next());
    }
  }

  /**
   * The start methods just prepare the file upload windows as appropriate for
   * the calling screen.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartFileUploadDescription(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    session.setAttribute("mediaSource", "description");
    session.setAttribute("isLink", httpServletRequest.getParameter("isLink"));
    if(httpServletRequest.getParameter("index") != null)
    {
      populateFileUpload(
        session, "description",
        (String) httpServletRequest.getParameter("index"));
    }
    else
    {
      FileUploadForm upload = new FileUploadForm();

      if((httpServletRequest.getParameter("isLink")).equals("true"))
      {
        upload.setTitle("Add Related Files and Links");
      }
      else
      {
        upload.setTitle("Add Inline Media");
      }

      session.setAttribute("getMedia", null);
      session.setAttribute("fileUpload", upload);
    }
  }

  /**
   * Delete media from a description.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDeleteFileUploadDescription(HttpSession session)
    throws Exception
  {
    boolean isLink =
      (((String) session.getAttribute("isLink")).equals("true") ? true : false);
    DescriptionForm form =
      (DescriptionForm) session.getAttribute("description");
    int index =
      new Integer(((String) session.getAttribute("index"))).intValue();
    Collection media = null;
    if(isLink)
    {
      media = form.getRelatedMediaCollection();
    }
    else
    {
      media = form.getMediaCollection();
    }

    Iterator iter = media.iterator();
    for(int i = 0; i < (media.size() - 1 - index); i++)
    {
      if(iter.hasNext())
      {
        iter.next();
      }
      else
      {
        LOG.debug(
          "Size = " + media.size() + ", i = " + i + ", index = " + index);
      }
    }

    if(iter.hasNext())
    {
      MediaData data = (MediaData) iter.next();
      media.remove(data);
    }
    else
    {
      LOG.debug("Size = " + media.size() + ", index = " + index);
    }

    if(isLink)
    {
      form.setRelatedMediaCollection(media);
    }
    else
    {
      form.setMediaCollection(media);
    }
  }

  /**
   * The start methods just prepare the file upload windows as appropriate for
   * the calling screen.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartFileUploadQuestion(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    session.setAttribute("mediaSource", "question");
    if(httpServletRequest.getParameter("isLink") != null)
    {
      session.setAttribute("isLink", httpServletRequest.getParameter("isLink"));
    }

    if(httpServletRequest.getParameter("index") != null)
    {
      populateFileUpload(
        session, "question", (String) httpServletRequest.getParameter("index"));
    }
    else
    {
      FileUploadForm upload = new FileUploadForm();
      if(
        (httpServletRequest.getParameter("isLink") != null) &&
          (httpServletRequest.getParameter("isLink")).equals("true"))
      {
        upload.setTitle("Add Related Files and Links");
      }
      else
      {
        upload.setTitle("Add Inline Media");
      }

      session.setAttribute("getMedia", null);
      session.setAttribute("fileUpload", upload);
    }
  }

  /**
   * Delete media from a question.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDeleteFileUploadQuestion(HttpSession session)
    throws Exception
  {
    boolean isLink =
      (((String) session.getAttribute("isLink")).equals("true") ? true : false);
    QuestionForm form = (QuestionForm) session.getAttribute("question");
    int index =
      new Integer(((String) session.getAttribute("index"))).intValue();
    Collection media = null;
    if(isLink)
    {
      media = form.getRelatedMediaCollection();
    }
    else
    {
      media = form.getMediaCollection();
    }

    Iterator iter = media.iterator();
    for(int i = 0; i < (media.size() - 1 - index); i++)
    {
      iter.next();
    }

    MediaData data = (MediaData) iter.next();
    media.remove(data);

    if(isLink)
    {
      form.setRelatedMediaCollection(media);
    }
    else
    {
      form.setMediaCollection(media);
    }

    // Update the question form
    AssessmentAction.doAssessmentQuestionForm(session, form);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param upload DOCUMENTATION PENDING
   * @param saveToDisk DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData saveFileToRepository(
    FileUploadForm upload, boolean saveToDisk)
  {
    MediaData md = new MediaData();
    if(saveToDisk)
    {
      boolean savedFile = false;
      String userName = null;
      try
      {
        userName = OsidManagerFactory.getAgent().getDisplayName();
        if((userName == null) || userName.equals(""))
        {
          userName = "guest";
        }
      }
      catch(Exception e)
      {
        LOG.info("will use guest as username");
      }

      java.util.Date date = new java.util.Date();
      String uploadFileName = upload.getNewfile().getFileName();

      // don't want any spaces in file name
      uploadFileName = uploadFileName.replaceAll(" ", "_");

      String fileName =
        "/tmp/" + userName + "_" + date.getTime() + "_" + uploadFileName;
      try
      {
        File tmpDir = new File("/tmp");
        if(! tmpDir.exists() || ! tmpDir.isDirectory())
        {
          tmpDir.mkdir();
        }

        File outFile = new File(fileName);
        DataOutputStream out =
          new DataOutputStream(new FileOutputStream(fileName));
        byte[] data = upload.getNewfile().getFileData();
        out.write(data, 0, data.length);
        out.flush();
        out.close();
        savedFile = true;
      }
      catch(IOException e)
      {
        LOG.info("failed to write uploaded file to disk: " + e.getMessage());
      }

      // exit immediately if file cannot be saved
      if(! savedFile)
      {
        return null;
      }

      md.setLocation("file://" + fileName);
      LOG.info("location=" + md.getLocation());
    }
    else
    {
      try
      {
        md.setContent(upload.getNewfile().getFileData());
        md.setLocation("");
      }
      catch(Exception e)
      {
        LOG.info("no file data to upload");

        return null;
      }
    }

    md.setFileName(upload.getNewfile().getFileName());
    String extension =
      md.getFileName().substring(md.getFileName().lastIndexOf(".") + 1);
    if(extension.equals("xls") || extension.equals("XLS"))
    {
      md.setType("application/msexcel");
    }
    else
    {
      md.setType(upload.getNewfile().getContentType());
    }

    return md;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param upload DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData saveLinkToRepository(FileUploadForm upload)
  {
    MediaData md = null;

    if(upload.getSource().equals("1") && (upload.getLink() != null))
    {
      md = new MediaData();
      md.setIsLink(true);
      md.setLocation(upload.getLink());

      if((upload.getType() != null) && (upload.getType().length() > 0))
      {
        md.setType(upload.getType());
      }
      else if(md.getLocation().indexOf(".") > -1) // Try guessing the type
      {
        String extension =
          md.getLocation().substring(md.getLocation().lastIndexOf(".") + 1);
        md.setType(getMimeType(extension));
      }
    }

    return md;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param upload DOCUMENTATION PENDING
   * @param saveToDisk DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData saveAudioToRepository(
    FileUploadForm upload, boolean saveToDisk)
  {
    MediaData md = null;
    String location = null;

    if(upload.getLink() != null)
    {
      md = new MediaData();
      md.setType("audio/basic");
      if(saveToDisk)
      { // audio file is already saved to /tmp, so nothing to do
        md.setIsLink(true);
        md.setLocation(upload.getLink());
      }
      else
      { // need to move file in /tmp to DB
        try
        {
          md.setIsLink(false);
          md.setLocation("");
          location = upload.getLink();
          location = location.replaceFirst("file:///","/");
          LOG.info("location=" + location);
          FileInputStream in = new FileInputStream(location);
          int len=0;
          while (in.read()!=-1)
	      len++;

          byte[] b = new byte[len];
          FileInputStream in2 = new FileInputStream(location);
          in2.read(b,0,len);
          md.setContent(b);
          in.close();
          in2.close();
        }
        catch(IOException e)
        {
	    LOG.warn("file not found at "+location);
          return null;
        }
      }

      if((upload.getType() != null) && (upload.getType().length() > 0))
      {
        md.setType(upload.getType());
      }
      else if(md.getLocation().indexOf(".") > -1) // Try guessing the type
      {
        String extension =
          md.getLocation().substring(md.getLocation().lastIndexOf(".") + 1);
        md.setType(getMimeType(extension));
      }
    }
    return md;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param upload DOCUMENTATION PENDING
   */
  public void removeTempAudioFile(FileUploadForm upload)
  {
    String location = null;
    try
    {
      location = upload.getLink();
      location = location.replaceFirst("file:///","/");
      LOG.info("location=" + location);
      File file = new File(location);
      if (file.delete())
        LOG.info("removed file");
    }
    catch(Exception e)
    {
      LOG.warn("cannot remove audio file " + location);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param extension DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String getMimeType(String extension)
  {
    String mimeType = null;
    if(extension.equals("gif"))
    {
      mimeType = "image/gif";
    }
    else if(extension.equals("jpg") || extension.equals("jpeg"))
    {
      mimeType = "image/jpeg";
    }
    else if(extension.equals("png"))
    {
      mimeType = "image/png";
    }
    else if(extension.equals("au"))
    {
      mimeType = "audio/basic";
    }
    else if(extension.equals("xls") || extension.equals("XLS"))
    {
      mimeType = "application/msexcel";
    }

    return mimeType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param upload DOCUMENTATION PENDING
   */
  private void logUpload(FileUploadForm upload)
  {
    LOG.info("logging action form");
    LOG.info("getIsHtmlImage()=" + upload.getIsHtmlImage());
    LOG.info("getIsHtmlInline()=" + upload.getIsHtmlInline());
    LOG.info("getAuthor()=" + upload.getAuthor());
    LOG.info("getDescription()=" + upload.getDescription());
    LOG.info("getFileName()=" + upload.getFileName());
    LOG.info("getIsEdit()=" + upload.getIsEdit());
    LOG.info("getIsHtmlInline()=" + upload.getIsHtmlInline());
    LOG.info("getLink()=" + upload.getLink());
    LOG.info("getMapId()=" + upload.getMapId());
    LOG.info("getMediaTypes()=" + upload.getMediaTypes());
    LOG.info("getName()=" + upload.getName());
    LOG.info("getNewfile()=" + upload.getNewfile());
    LOG.info("getSource()=" + upload.getSource());
    LOG.info("getTitle()=" + upload.getTitle());
    LOG.info("getType()=" + upload.getType());
    LOG.info("getImageAlign()=" + upload.getImageAlign());
    LOG.info("getImageAlt()=" + upload.getImageAlt());
    LOG.info("getImageBorder()=" + upload.getImageBorder());
    LOG.info("getImageHspace()=" + upload.getImageHspace());
    LOG.info("getImageVspace()=" + upload.getImageVspace());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param md DOCUMENTATION PENDING
   */
  private void logMediaData(MediaData md)
  {
    LOG.info("logging media data");
    LOG.info("md.getAuthor()=" + md.getAuthor());
    LOG.info("md.getContent()=" + md.getContent());
    LOG.info("md.getDateAdded()=" + md.getDateAdded());
    LOG.info("md.getDescription()=" + md.getDescription());
    LOG.info("md.getFileName()=" + md.getFileName());
    LOG.info("md.getHtmlInline()=" + md.getHtmlInline());
    LOG.info("md.getIconUrl()=" + md.getIconUrl());
    LOG.info("md.getId()=" + md.getId());
    LOG.info("md.getIsImage()=" + md.getIsImage());
    LOG.info("md.getIsLink()=" + md.getIsLink());
    LOG.info("md.getLocation()=" + md.getLocation());
    LOG.info("md.getMapId()=" + md.getMapId());
    LOG.info("md.getName()=" + md.getName());
    LOG.info("md.getPlayer()=" + md.getPlayer());
    LOG.info("md.getPosition()=" + md.getPosition());
    LOG.info("md.getRecorder()=" + md.getRecorder());
    LOG.info("md.getType()=" + md.getType());
    LOG.info("md.getTypeId()=" + md.getTypeId());
  }

  /**
   * Varient designed for inline multimedia editor
   * This does a file upload or a link.
   *
   * @param actionForm must be a FileUploadForm
   * @return the id of the media record
   *
   */
  public int doFileUploadInline(ActionForm actionForm)
  {
    int media_id = INVALID_ID;

    LOG.info("attempting file upload");
    FileUploadForm upload = (FileUploadForm) actionForm;

    // log info in the Action Form
    logUpload(upload);

    // unlike the old aam code we do not associate the media
    // with a media collection, rather, we update the media directly
    // and inline a showMedia URL.
    MediaData md = new MediaData();
    LOG.debug("New upload");

    boolean fileUpload = false;

    // check if site want to write to file to DB or file - 6/8/04 daisyf  
    boolean saveToDisk = true;
    try
    {
      Properties props =
        PathInfo.getInstance().getSettingsProperties(SETTINGS_FILE);
      String value = props.getProperty("SaveToDisk");
      if(("false").equals(value) || ("False").equals(value))
      {
        saveToDisk = false;
      }
    }
    catch(Exception e)
    {
      LOG.info(
        "Could not find " + SETTINGS_FILE + ", media will be saved to disk");
      throw new Error(
        "Could not find " + SETTINGS_FILE + ", media will be saved to disk");
    }

    LOG.info("will save media to disk " + saveToDisk);

    // source=0 => media is a file upload to repository which can be DB or file system
    if(upload.getSource().equals("0") && (upload.getNewfile() != null))
    {
      md = saveFileToRepository(upload, saveToDisk);
    }

    // source=1 => media is a link and will be save within the media record
    if(upload.getSource().equals("1"))
    {
      md = saveLinkToRepository(upload);
    }

    // source=2 => media is audio which has been "scp" to /tmp, 
    // If saveToDisk is false, move media to DB. Otherwise, leave it as is
    if(upload.getSource().equals("2"))
    {
      md = saveAudioToRepository(upload, saveToDisk);
    }

    if(md == null) // exits right away
    {
      return media_id;
    }

    md.setDateAdded(new Timestamp((new Date()).getTime()));
    LOG.warn("The dateadded is " + md.getDateAdded());

    if(md.getType() == null)
    {
      md.setType("unknown");
    }

    try //set uploadiconMap if it is empty
    {
      if(mediaiconMap.isEmpty())
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        mediaiconMap = delegate.getMediaIcon();
      }
    }
    catch(Exception e)
    {
      LOG.error(e);

      //      throw new Error(e);
      return media_id;
    }

    try //get iconurl from mediaiconMap by meidatype and set it
    {
      md.setIconUrl((String) mediaiconMap.get(md.getType()));
    }
    catch(Exception e)
    {
      LOG.error(e);

      //      throw new Error(e);
      return media_id;
    }

    md.setAuthor(upload.getAuthor());
    md.setName(upload.getName());
    md.setDescription(upload.getDescription());
    md.setIsHtmlInline(upload.getIsHtmlInline());

    // getMapId gets the actual ID if it exists, and otherwise creates
    // a unique key for the map.
    mediaMap.put(md.getMapId(), md);

    // log all our MediaData info
    logMediaData(md);

    // DIRECTLY USE UTILACCESSOBJECT SAVEMEDIADATA FOR NOW
    // TODO: PUT LEVEL OF SERVICE/SERVICE DELEGATE OVER THIS!
    media_id = UtilAccessObject.saveMedia(md);

    LOG.info("done with file upload: " + media_id);
    if(upload.getSource().equals("2") && (media_id > 0))
    {
      removeTempAudioFile(upload);
    }

    return media_id;
  }

  /**
   * This does a file upload.  It returns a String that holds the name of the
   * session item (and forward) to use to go to the next page.
   *
   * @param session DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String doFileUpload(HttpSession session, ActionForm actionForm)
  {
    String source = (String) session.getAttribute("mediaSource");
    boolean isLink =
      (((String) session.getAttribute("isLink")).equals("true") ? true : false);
    FileUploadForm media = (FileUploadForm) actionForm;
    MediaInterface form = null;

    if(! "partItem".equals(source))
    {
      form = (MediaInterface) session.getAttribute(source);
    }

    ArrayList collection = null;
    if(form != null)
    {
      if(isLink)
      {
        collection = (ArrayList) form.getRelatedMediaCollection();
      }
      else
      {
        collection = (ArrayList) form.getMediaCollection();
      }
    }
    else
    {
      try
      {
        Section section = (Section) session.getAttribute("mediaSection");
        SectionPropertiesImpl sprops =
          (SectionPropertiesImpl) section.getData();
        collection = (ArrayList) sprops.getMediaCollection();
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }

    MediaData data = new MediaData();
    data.setLink(isLink);

    MediaData old = null;
    if(
      (session.getAttribute("getMedia") != null) &&
        collection.contains(session.getAttribute("getMedia")))
    { // It's an edit, so move the data over to the new one.
      old = (MediaData) session.getAttribute("getMedia");
      LOG.debug("Editing " + old.getName());
      data.setId(old.getId());
      data.setContent(old.getContent());
      data.setFileName(old.getFileName());
      data.setLocation(old.getLocation());
      data.setIsHtmlInline(old.getIsHtmlInline());
      data.setType(old.getType());
      data.setIconUrl(old.getIconUrl());
      data.setDateAdded(old.getDateAdded());
    }
    else // it's a new upload, so save it
    {
      String extension = "";
      LOG.debug("New upload");
      try
      {
        if(media.getSource().equals("0") && (media.getNewfile() != null))
        {
          data.setContent(media.getNewfile().getFileData());
          data.setFileName(media.getNewfile().getFileName());
          extension =
            data.getFileName().substring(
              data.getFileName().lastIndexOf(".") + 1);
          if(extension.equals("xls") || extension.equals("XLS"))
          {
            data.setType("application/msexcel");
          }
          else
          {
            data.setType(media.getNewfile().getContentType());
          }
        }
      }
      catch(Exception e)
      {
        LOG.warn("No file source found.");
      }

      if(media.getSource().equals("1") && (media.getLink() != null))
      {
        data.setLocation(media.getLink());

        // Try guessing the type
        if(data.getLocation().indexOf(".") > -1)
        {
          extension =
            data.getLocation().substring(
              data.getLocation().lastIndexOf(".") + 1);
          if(extension.equals("gif"))
          {
            data.setType("image/gif");
          }
          else if(extension.equals("jpg") || extension.equals("jpeg"))
          {
            data.setType("image/jpeg");
          }
          else if(extension.equals("png"))
          {
            data.setType("image/png");
          }
          else if(extension.equals("xls") || extension.equals("XLS"))
          {
            data.setType("application/msexcel");
          }
        }
      }

      data.setDateAdded(new Timestamp((new Date()).getTime()));
      LOG.warn("The dateadded is " + data.getDateAdded());

      if(data.getType() == null)
      {
        data.setType("unknown");
      }

      try //set mediaiconMap if it is empty
      {
        if(mediaiconMap.isEmpty())
        {
          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
          mediaiconMap = delegate.getMediaIcon();
        }
      }
      catch(Exception e)
      {
        LOG.error(e);

        //        throw new Error(e);
        return null;
      }

      try //get iconurl from mediaiconMap by meidatype and set it
      {
        data.setIconUrl((String) mediaiconMap.get(data.getType()));
      }
      catch(Exception e)
      {
        LOG.error(e);

        //        throw new Error(e);
        return null;
      }
    }

    data.setAuthor(media.getAuthor());
    data.setName(media.getName());

    //    logger.debug("location is " + data.getLocation());
    //    logger.debug("link is " + media.getLink());
    //    logger.warn("Name set to " + data.getName());
    data.setDescription(media.getDescription());

    //    logger.warn("Location set to: " + media.getLink());
    data.setLocation(media.getLink());
    data.setIsHtmlInline(media.getIsHtmlInline());

    // getMapId gets the actual ID if it exists, and otherwise creates
    // a unique key for the map.
    mediaMap.put(data.getMapId(), data);

    if(old != null)
    {
      int i = collection.indexOf(old);
      collection.remove(old);
      collection.add(i, data);
    }
    else
    {
      collection.add(data);
    }

    if(form != null)
    {
      if(isLink)
      {
        form.setRelatedMediaCollection(collection);
      }
      else
      {
        form.setMediaCollection(collection);
      }

      session.setAttribute(source, form);

      // If it's a description, we need to populate the template
      // or assessment so it's actually saved.
      if(source.equals("description"))
      {
        try
        {
          Assessment template =
            (Assessment) session.getAttribute("assessmentTemplate");
          AssessmentProperties properties =
            (AssessmentProperties) template.getData();
          properties.setMediaCollection(new ArrayList());
          Iterator iter = form.getMediaCollection().iterator();
          while(iter.hasNext())
          {
            properties.addMedia(iter.next());
          }

          iter = form.getRelatedMediaCollection().iterator();
          while(iter.hasNext())
          {
            properties.addMedia(iter.next());
          }
        }
        catch(Exception e)
        {
          LOG.error(e);
          throw new Error(e);
        }
      }
      else if(source.equals("part"))
      {
        // This is only called from the template, so only one section.
        Assessment template =
          (Assessment) session.getAttribute("assessmentTemplate");
        SectionPropertiesImpl sprops = null;
        try
        {
          SectionIteratorImpl siter =
            (SectionIteratorImpl) template.getSections();
          SectionImpl section = (SectionImpl) siter.next();
          sprops = (SectionPropertiesImpl) section.getData();
        }
        catch(Exception e)
        {
          LOG.error(e);
          throw new Error(e);
        }

        sprops.setMediaCollection(new ArrayList());
        Iterator iter = form.getMediaCollection().iterator();
        while(iter.hasNext())
        {
          sprops.addMedia(iter.next());
        }

        iter = form.getRelatedMediaCollection().iterator();
        while(iter.hasNext())
        {
          sprops.addMedia(iter.next());
        }
      }
    }
    else
    {
      try
      {
        Section section = (Section) session.getAttribute("mediaSection");
        SectionPropertiesImpl sprops =
          (SectionPropertiesImpl) section.getData();

        // Fill in part form
        Iterator piter = collection.iterator();
        PartForm pform = (PartForm) session.getAttribute("part");
        Collection pmedia = new ArrayList();
        Collection relatedMedia = new ArrayList();
        while(piter.hasNext())
        {
          MediaData md = (MediaData) piter.next();
          if(md.isLink())
          {
            relatedMedia.add(md);
          }
          else
          {
            pmedia.add(md);
          }
        }

        pform.setMediaCollection(pmedia);
        pform.setRelatedMediaCollection(relatedMedia);

        // Fill in section
        sprops.setMediaCollection(collection);
        section.updateData(sprops);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }

    return source;
  }

  // ***********************
  // Now the reorder methods
  // ***********************

  /**
   * The start methods just prepare the reorder windows as appropriate for the
   * calling screen.  This one is for description media.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartReorderDescription(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    ReorderForm form = new ReorderForm();
    DescriptionForm df = (DescriptionForm) session.getAttribute("description");
    if(httpServletRequest.getParameter("isLink").equals("false"))
    {
      form.setList(df.getReversedMediaCollection());
      form.setFormId("description:false");
      form.setName("Inline Media");
    }
    else
    {
      form.setList(df.getReversedRelatedMediaCollection());
      form.setFormId("description:true");
      form.setName("Related Files and Links");
    }

    session.setAttribute("reorder", form);
  }

  /**
   * The start methods just prepare the reorder windows as appropriate for the
   * calling screen.  This one is for part item media.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doStartReorderPartItem(
    HttpSession session, HttpServletRequest httpServletRequest)
    throws Exception
  {
    ReorderForm form = new ReorderForm();
    boolean isLink =
      (httpServletRequest.getParameter("isLink").equals("true") ? true : false);
    SectionIterator sii =
      ((Assessment) session.getAttribute("assessmentTemplate")).getSections();
    Section section = null;

    //Find the right section
    while(sii.hasNext())
    {
      section = (Section) sii.next();
      if(
        section.getId().toString().equals(
            httpServletRequest.getParameter("id")))
      {
        break;
      }
    }

    ArrayList media = new ArrayList();
    try
    {
      SectionPropertiesImpl props = (SectionPropertiesImpl) section.getData();
      Iterator iter = props.getReversedMediaCollection().iterator();
      while(iter.hasNext())
      {
        MediaData data = (MediaData) iter.next();
        if(! isLink && ! data.getIsLink())
        {
          media.add(data);
        }

        if(isLink && data.getIsLink())
        {
          media.add(data);
        }
      }

      form.setFormId("partItem:" + httpServletRequest.getParameter("isLink"));
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    form.setList(media);
    session.setAttribute("mediaSection", section);
    if(! isLink)
    {
      form.setName("Inline Media");
    }
    else
    {
      form.setName("Related Files and Links");
    }

    session.setAttribute("reorder", form);
  }

  /**
   * The start methods just prepare the reorder windows as appropriate for the
   * calling screen.  This one is for question media.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartReorderQuestion(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    ReorderForm form = new ReorderForm();
    QuestionForm qf = (QuestionForm) session.getAttribute("question");
    if(httpServletRequest.getParameter("isLink").equals("false"))
    {
      form.setList(qf.getReversedMediaCollection());
      form.setFormId("question:false");
      form.setName("Inline Media");
    }
    else
    {
      form.setList(qf.getReversedRelatedMediaCollection());
      form.setFormId("question:true");
      form.setName("Related Files and Links");
    }

    session.setAttribute("reorder", form);
  }

  // This reorders the list and sticks it back in the form.
  public String doReorder(HttpSession session, HttpServletRequest request)
  {
    ReorderForm form = (ReorderForm) session.getAttribute("reorder");

    // This is only used if it's a reorder, not a submit
    if(request.getParameter("isMySubmit").equals("false"))
    {
      // Call the method to reset the list, but don't save it
      // anywhere.
      form.getReversedList();

      // Now go back to the reorder page.
      return "reorder";
    }

    // If we really meant to save it, move on.
    StringTokenizer st = new StringTokenizer(form.getFormId(), ":");
    String source = st.nextToken();
    String link = st.nextToken();
    if(source.equals("partItem"))
    {
      try
      {
        Section section = (Section) session.getAttribute("mediaSection");
        SectionPropertiesImpl sprops =
          (SectionPropertiesImpl) section.getData();
        boolean isLink = (link.equals("true") ? true : false);
        ArrayList newList = (ArrayList) form.getReversedList();
        Iterator iter = sprops.getMediaCollection().iterator();
        ArrayList oldList = new ArrayList();

        // If this is a template, we need to fill in the PartForm as
        // well, so the info is visible.
        PartForm pform = (PartForm) session.getAttribute("part");
        if(pform == null)
        {
          pform = new PartForm();
        }

        if(isLink)
        {
          pform.setRelatedMediaCollection(newList);
        }
        else
        {
          pform.setMediaCollection(newList);
        }

        // Now we have to filter out the other list, and put the new
        // list in the right place.
        while(iter.hasNext())
        {
          MediaData data = (MediaData) iter.next();
          if((isLink && ! data.getIsLink()) || (! isLink && data.getIsLink()))
          {
            oldList.add(data);
          }
        }

        if(isLink)
        {
          oldList.addAll(newList);
        }
        else
        {
          oldList.addAll(0, newList);
        }

        sprops.setMediaCollection(oldList);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }
    else
    {
      MediaInterface mf = (MediaInterface) session.getAttribute(source);
      if(link.equals("false"))
      {
        mf.setMediaCollection(form.getReversedList());
      }
      else
      {
        mf.setRelatedMediaCollection(form.getReversedList());
      }

      // If it's a description or part, we need to populate the template
      // or assessment so it's actually saved.
      if(source.equals("description"))
      {
        try
        {
          Assessment template =
            (Assessment) session.getAttribute("assessmentTemplate");
          AssessmentProperties properties =
            (AssessmentProperties) template.getData();
          properties.setMediaCollection(new ArrayList());
          Iterator iter = mf.getMediaCollection().iterator();
          while(iter.hasNext())
          {
            properties.addMedia(iter.next());
          }

          iter = mf.getRelatedMediaCollection().iterator();
          while(iter.hasNext())
          {
            properties.addMedia(iter.next());
          }
        }
        catch(Exception e)
        {
          LOG.error(e);
          throw new Error(e);
        }
      }
      else if(source.equals("part"))
      {
        // This is only called from the template, so only one section.
        Assessment template =
          (Assessment) session.getAttribute("assessmentTemplate");
        SectionPropertiesImpl sprops = null;
        try
        {
          SectionIteratorImpl siter =
            (SectionIteratorImpl) template.getSections();
          SectionImpl section = (SectionImpl) siter.next();
          sprops = (SectionPropertiesImpl) section.getData();
        }
        catch(Exception e)
        {
          LOG.error(e);
          throw new Error(e);
        }

        sprops.setMediaCollection(new ArrayList());
        Iterator iter = mf.getMediaCollection().iterator();
        while(iter.hasNext())
        {
          sprops.addMedia(iter.next());
        }

        iter = mf.getRelatedMediaCollection().iterator();
        while(iter.hasNext())
        {
          sprops.addMedia(iter.next());
        }
      }
    }

    return source;
  }
}
