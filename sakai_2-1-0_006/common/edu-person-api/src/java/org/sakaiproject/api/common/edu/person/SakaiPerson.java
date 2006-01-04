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

package org.sakaiproject.api.common.edu.person;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */

public interface SakaiPerson extends EduPerson
{
  /**
   * Person's pictureUrl, url to user picture provided.
   * 
   * @return
   */
  public String getPictureUrl();

  /**
   * Person's pictureUrl, url to user picture provided.
   * 
   * @param pictureURL
   */
  public void setPictureUrl(String pictureURL);

  /**
   * Person's preference to display system picture, if the institution provided 
   * picture is preferred for display.
   * 
   * @return
   */
  public Boolean isSystemPicturePreferred();

  /**
   * Person's preference to display system picture, if the institution provided 
   * picture is preferred for display.
   * 
   * @return
   */
  public void setSystemPicturePreferred(Boolean systemPicturePreferred);

  /**
   * Other information provided
   * 
   * @return
   */
  public String getNotes();

  /**
   * Other information provided
   * 
   * @param notes :
   *          set other information provided
   */
  public void setNotes(String notes);

  //TODO verify method with Lance
  /**
   * Person's campus
   * 
   * @return
   */
  public String getCampus();

  //TODO verify method with Lance
  /**
   * Person's campus
   * 
   * @param school
   */
  public void setCampus(String school);

  /**
   * Person's preference to allow read access to private information
   * 
   * @return
   */
  public Boolean getHidePrivateInfo();

  /**
   * Person's preference to allow read access to private information
   * 
   * @param hidePrivateInfo
   */
  public void setHidePrivateInfo(Boolean hidePrivateInfo);

  /**
   * Person's preference to allow read access to public information
   * 
   * @return
   */
  public Boolean getHidePublicInfo();

  /**
   * Person's preference to allow read access to public information
   * 
   * @param hidePublicInfo
   */
  public void setHidePublicInfo(Boolean hidePublicInfo);

  /**
   * Has the user invoked their FERPA rights?
   * 
   * @return
   */
  public Boolean getFerpaEnabled();

  /**
   * 
   * @param ferpaEnabled TRUE if FERPA rights have been invoked. If FALSE or 
   * NULL no FERPA rights have been invoked.
   */
  public void setFerpaEnabled(Boolean ferpaEnabled);
}
