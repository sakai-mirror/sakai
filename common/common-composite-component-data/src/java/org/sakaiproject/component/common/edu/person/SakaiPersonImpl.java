/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.component.common.edu.person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.edu.person.InetOrgPerson;
import org.sakaiproject.api.common.edu.person.OrganizationalPerson;
import org.sakaiproject.api.common.edu.person.Person;
import org.sakaiproject.api.common.edu.person.SakaiPerson;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class SakaiPersonImpl extends EduPersonImpl implements Person,
    OrganizationalPerson, InetOrgPerson, SakaiPerson
{
  private static final Log LOG = LogFactory.getLog(SakaiPersonImpl.class);

  /**
   * Empty constuctor for hibernate
   */
  public SakaiPersonImpl()
  {
    super();
  }

  protected String pictureUrl;

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#getPictureUrl()
   */
  public String getPictureUrl()
  {
    return pictureUrl;
  }

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#setPictureUrl(java.lang.String)
   */
  public void setPictureUrl(String pictureURL)
  {
    this.pictureUrl = pictureURL;
  }

  protected Boolean systemPicturePreferred;

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#isSystemPicturePreferred()
   */
  public Boolean isSystemPicturePreferred()
  {
    return this.systemPicturePreferred;
  }

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#setSystemPicturePreferred(java.lang.Boolean)
   */
  public void setSystemPicturePreferred(Boolean systemPicturePreferred)
  {
    this.systemPicturePreferred = systemPicturePreferred;
  }

  protected String notes;

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#getNotes()
   */
  public String getNotes()
  {
    return this.notes;
  }

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#setNotes(java.lang.String)
   */
  public void setNotes(String notes)
  {
    this.notes = notes;
  }
  protected String campus;

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#getCampus()
   */
  public String getCampus()
  {
    return this.campus;
  }

  /*
   * @see org.sakaiproject.service.profile.SakaiPerson#setCampus(java.lang.String)
   */
  public void setCampus(String school)
  {
    this.campus = school;
  }

  /**
   * Comment for <code>isPrivateInfoViewable</code>
   */
  protected Boolean hidePrivateInfo;

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.service.profile.SakaiPerson#getIsPrivateInfoViewable()
   */
  public Boolean getHidePrivateInfo()
  {
    return hidePrivateInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.service.profile.SakaiPerson#setIsPrivateInfoViewable(java.lang.Boolean)
   */
  public void setHidePrivateInfo(Boolean hidePrivateInfo)
  {
    this.hidePrivateInfo = hidePrivateInfo;
  }

  protected Boolean hidePublicInfo;

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.service.profile.SakaiPerson#getIsPublicInfoViewable()
   */
  public Boolean getHidePublicInfo()
  {
    return hidePublicInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sakaiproject.service.profile.SakaiPerson#setIsPublicInfoViewable(java.lang.Boolean)
   */
  public void setHidePublicInfo(Boolean hidePublicInfo)
  {
    this.hidePublicInfo = hidePublicInfo;
  }

}



