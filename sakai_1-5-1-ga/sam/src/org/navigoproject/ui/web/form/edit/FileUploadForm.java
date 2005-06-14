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

package org.navigoproject.ui.web.form.edit;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: form for uploading media.
 * </p>
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 *
 * @author Rachel Gollub
 * @author Qingru Zhang
 * @author Ed Smiley
 * @version 1.0
 */
public class FileUploadForm
  extends ActionForm
  implements Serializable
{
  private String source;
  private FormFile newfile;
  private String link;
  private String name;
  private String title;
  private String description;
  private String author;
  private String filename;
  private String type;
  private boolean isEdit;
  private boolean isHtmlInline;
  private Collection mediaTypes;
  private String mapId;
  // displayed as image true or false
  private boolean isHtmlImage;
  // image attributes
  private String imageAlt;
  private String imageVspace;
  private String imageHspace;
  private String imageAlign;
  private String imageBorder;
  // item_ident_ref is the id of an answer (item_result) submitted by user
  private String itemIdentRef;
  private String userName;

  /**
   * Creates a new FileUploadForm object.
   */
  public FileUploadForm()
  {
    super();
    resetFields();
  }

  /**
   * Standard Struts method.  Validation.
   *
   * @param actionMapping the actionMapping
   * @param httpServletRequest the httpServletRequest
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();

    return errors;
  }

  /**
   * Standard Struts method.  Reset.
   *
   * @param actionMapping the actionMapping
   * @param httpServletRequest the httpServletRequest
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSource()
  {
    return source;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psource DOCUMENTATION PENDING
   */
  public void setSource(String psource)
  {
    source = psource;
  }

  /**
   * FormFile is a file upload file.
   *
   * @return FormFile file upload file.
   */
  public FormFile getNewfile()
  {
    return newfile;
  }

  /**
   * FormFile is a file upload file.
   *
   * @param pnewfile FormFile file upload file.
   */
  public void setNewfile(FormFile pnewfile)
  {
    newfile = pnewfile;
  }

  /**
   * Link true or false?
   *
   * @return link true or false?
   */
  public String getLink()
  {
    if(link != null)
    {
      return link;
    }
    else
    {
      return "";
    }
  }

  /**
   * Link true or false?
   *
   * @param plink link true or false?
   */
  public void setLink(String plink)
  {
    link = plink;
  }

  /**
   * Media Name.
   *
   * @return name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Media Name.
   *
   * @param pname name.
   */
  public void setName(String pname)
  {
    name = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptitle DOCUMENTATION PENDING
   */
  public void setTitle(String ptitle)
  {
    title = ptitle;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdescription DOCUMENTATION PENDING
   */
  public void setDescription(String pdescription)
  {
    description = pdescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pauthor DOCUMENTATION PENDING
   */
  public void setAuthor(String pauthor)
  {
    author = pauthor;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFileName()
  {
    if(filename != null)
    {
      return filename;
    }

    if((newfile != null) && (newfile.getFileName() != null))
    {
      return newfile.getFileName();
    }

    return "";
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   */
  public void setFileName(String pname)
  {
    filename = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsEdit()
  {
    return isEdit;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pedit DOCUMENTATION PENDING
   */
  public void setIsEdit(boolean pedit)
  {
    isEdit = pedit;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptype DOCUMENTATION PENDING
   */
  public void setType(String ptype)
  {
    type = ptype;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaTypes()
  {
    return mediaTypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param types DOCUMENTATION PENDING
   */
  public void setMediaTypes(Collection types)
  {
    mediaTypes = types;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMapId()
  {
    return mapId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmapId DOCUMENTATION PENDING
   */
  public void setMapId(String pmapId)
  {
    mapId = pmapId;
  }


  /**
   * Is this media item part of HTML code?
   * @param pIsHtmlInline sets it true if it is, else false.
   */
  public void setIsHtmlInline(boolean pIsHtmlInline)
  {
    isHtmlInline = pIsHtmlInline;
  }

  /**
   * Is this media item part of HTML code?
   * @return true if it is, else false.
   */
  public boolean getIsHtmlInline()
  {
    return isHtmlInline;
  }

  /**
   * If this media item part of HTML code, is it an image?
   * This property is only relevant if isHtmlInline.
   * @param pIsHtmlImage sets it true if it is, else false.
   */
  public void setIsHtmlImage(boolean pIsHtmlImage)
  {
    isHtmlImage = pIsHtmlImage;
  }

  /**
   * If this media item part of HTML code, is it an image?
   * This property is only relevant if isHtmlInline.
   * @return true if it is, else false.
   */
  public boolean getIsHtmlImage()
  {
    return isHtmlImage;
  }


  /**
   * If this media item part of an image HTML code
   * the value set is an image ALT attribute.
   * @param pimageAlt image ALT attribute.
   */
  public void setImageAlt(String pimageAlt)
  {
    imageAlt = pimageAlt;
  }

  /**
   * If this media item part of an image HTML code
   * the value is an image attribute.
   * @return the value of the image attribute
   */
  public String getImageAlt()
  {
    return imageAlt;
  }

  /**
   * If this media item part of an image HTML code
   * the value is an image VSPACE attribute.
   * @return the value of the image VSPACE attribute
   */
  public String getImageVspace()
  {
    return imageVspace;
  }

  /**
   * If this media item part of an image HTML code
   * the value is an image VSPACE attribute.
   * @param pIsHtmlInline sets image VSPACE attribute.
   */
  public void setImageVspace(String pimageVspace)
  {
    imageVspace = pimageVspace;
  }

  /**
   * If this media item part of an image HTML code
   * the value of HSPACE image attribute.
   * @return the value of the HSPACE image attribute
   */
  public String getImageHspace()
  {
    return imageHspace;
  }
  /**
   * If this media item part of an image HTML code
   * sets the value of HSPACE image attribute.
   * @param pimageHspace value of HSPACE image attribute.
   */
  public void setImageHspace(String pimageHspace)
  {
    imageHspace = pimageHspace;
  }

  /**
   * If this media item part of an image HTML code
   * the value of ALIGN image attribute.
   * @return the value of the ALIGN image attribute
   */
  public String getImageAlign()
  {
    return imageAlign;
  }
  /**
   * If this media item part of an image HTML code
   * sets the value of ALIGN image attribute.
   * @param pimageHspace value of ALIGN image attribute.
   */
  public void setImageAlign(String pimageAlign)
  {
    imageAlign = pimageAlign;
  }

  /**
   * If this media item part of an image HTML code
   * the value of BORDER image attribute.
   * @return the value of the BORDER image attribute
   */
  public String getImageBorder()
  {
    return imageBorder;
  }
  /**
   * If this media item part of an image HTML code
   * sets the value of BORDER image attribute.
   * @param pimageHspace value of BORDER image attribute.
   */
  public void setImageBorder(String pimageBorder)
  {
    imageBorder = pimageBorder;
  }


  public String getItemIdentRef()
  {
    if(itemIdentRef != null)
    {
      return itemIdentRef;
    }
    else
    {
      return "";
    }
  }

  public void setItemIdentRef(String pitemIdentRef)
  {
    itemIdentRef = pitemIdentRef;
  }


  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String puserName)
  {
    userName = puserName;
  }



  /**
   * Sets defaults, used by constructor.
   */
  public void resetFields()
  {
    if(source == null)
    {
      source = "0";
      newfile = null;
      link = "";
      name = "New media";
      description = "";
      author = "";
      type = "text";
      filename = "";
      mediaTypes = new ArrayList();
      mapId = "";
      imageAlt = "New image";
      imageVspace = "";
      imageHspace = "";
      imageAlign = "";
      imageBorder = "";
      isEdit = true;
      isHtmlInline = true;
      isHtmlImage = true;
      itemIdentRef = "";
      userName = "guest";
    }
  }
}
