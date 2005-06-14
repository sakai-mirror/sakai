/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/InetOrgPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 ***********************************************************************************
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import org.sakaiproject.api.common.edu.person.InetOrgPerson;
import org.sakaiproject.api.common.edu.person.OrganizationalPerson;
import org.sakaiproject.api.common.edu.person.Person;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: InetOrgPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 */
public class InetOrgPersonImpl extends OrganizationalPersonImpl implements
    Person, OrganizationalPerson, InetOrgPerson
{

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getAudio()
   */
  public BufferedInputStream getAudio()
  {
    //TODO implement audio
    return null;
  }

  /**
   * @param audio
   *          The audio to set.
   */
  public void setAudio(BufferedOutputStream audio)
  {
    //TODO implement audio
    ;
  }

  protected String givenName;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getGivenName()
   */
  public String getGivenName()
  {
    return givenName;
  }

  /**
   * @param givenName
   *          The givenName to set.
   */
  public void setGivenName(String givenName)
  {
    this.givenName = givenName;
  }

  protected String homePhone;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getHomePhone()
   */
  public String getHomePhone()
  {
    return homePhone;
  }

  /**
   * @param homePhone
   *          The homePhone to set.
   */
  public void setHomePhone(String homePhone)
  {
    this.homePhone = homePhone;
  }

  protected String homePostalAddress;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getHomePostalAddress()
   */
  public String getHomePostalAddress()
  {
    return homePostalAddress;
  }

  /**
   * @param homePostalAddress
   *          The homePostalAddress to set.
   */
  public void setHomePostalAddress(String homePostalAddress)
  {
    this.homePostalAddress = homePostalAddress;
  }

  protected String initials;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getInitials()
   */
  public String getInitials()
  {
    return initials;
  }

  /**
   * @param initials
   *          The initials to set.
   */
  public void setInitials(String initials)
  {
    this.initials = initials;
  }

  protected byte[] jpegPhoto;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getJpegPhoto()
   */
  public byte[] getJpegPhoto()
  {
    return jpegPhoto;
  }

  /**
   * @param jpegPhoto
   *          The jpegPhoto to set.
   */
  public void setJpegPhoto(byte[] jpegPhoto)
  {
    this.jpegPhoto = jpegPhoto;
  }

  protected String labeledURI;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getLabeledURI()
   */
  public String getLabeledURI()
  {
    return labeledURI;
  }

  /**
   * @param labeledURI
   *          The labeledURI to set.
   */
  public void setLabeledURI(String labeledURI)
  {
    this.labeledURI = labeledURI;
  }

  protected String mail;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getMail()
   */
  public String getMail()
  {
    return mail;
  }

  /**
   * @param mail
   *          The mail to set.
   */
  public void setMail(String mail)
  {
    this.mail = mail;
  }

  protected String manager;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getManager()
   */
  public String getManager()
  {
    return manager;
  }

  /**
   * @param manager
   *          The manager to set.
   */
  public void setManager(String manager)
  {
    this.manager = manager;
  }

  protected String mobile;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getMobile()
   */
  public String getMobile()
  {
    return mobile;
  }

  /**
   * @param mobile
   *          The mobile to set.
   */
  public void setMobile(String mobile)
  {
    this.mobile = mobile;
  }

  protected String organization;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getOrganization()
   */
  public String getOrganization()
  {
    return organization;
  }

  /**
   * @param organization
   *          The organization to set.
   */
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }

  protected String pager;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getPager()
   */
  public String getPager()
  {
    return pager;
  }

  /**
   * @param pager
   *          The pager to set.
   */
  public void setPager(String pager)
  {
    this.pager = pager;
  }

  protected String preferredLanguage;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getPreferredLanguage()
   */
  public String getPreferredLanguage()
  {
    return preferredLanguage;
  }

  /**
   * @param preferredLanguage
   *          The preferredLanguage to set.
   */
  public void setPreferredLanguage(String preferredLanguage)
  {
    this.preferredLanguage = preferredLanguage;
  }

  protected String uid;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getUid()
   */
  public String getUid()
  {
    return uid;
  }

  /**
   * @param uid
   *          The uid to set.
   */
  public void setUid(String uid)
  {
    this.uid = uid;
  }

  protected byte[] userCertificate;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getUserCertificate()
   */
  public byte[] getUserCertificate()
  {
    return userCertificate;
  }

  /**
   * @param userCertificate
   *          The userCertificate to set.
   */
  public void setUserCertificate(byte[] userCertificate)
  {
    this.userCertificate = userCertificate;
  }

  protected byte[] userSMIMECertificate;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getUserSMIMECertificate()
   */
  public byte[] getUserSMIMECertificate()
  {
    return userSMIMECertificate;
  }

  /**
   * @param userSMIMECertificate
   *          The userSMIMECertificate to set.
   */
  public void setUserSMIMECertificate(byte[] userSMIMECertificate)
  {
    this.userSMIMECertificate = userSMIMECertificate;
  }

  protected String carLicense;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getCarLicense()
   */
  public String getCarLicense()
  {
    return carLicense;
  }

  /**
   * @param carLicense
   *          The carLicense to set.
   */
  public void setCarLicense(String carLicense)
  {
    this.carLicense = carLicense;
  }

  protected String displayName;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getDisplayName()
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * @param displayName
   *          The displayName to set.
   */
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  protected String departmentNumber;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getDepartmentNumber()
   */
  public String getDepartmentNumber()
  {
    return departmentNumber;
  }

  /**
   * @param departmentNumber
   *          The departmentNumber to set.
   */
  public void setDepartmentNumber(String departmentNumber)
  {
    this.departmentNumber = departmentNumber;
  }

  protected String employeeNumber;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getEmployeeNumber()
   */
  public String getEmployeeNumber()
  {
    return employeeNumber;
  }

  /**
   * @param employeeNumber
   *          The employeeNumber to set.
   */
  public void setEmployeeNumber(String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
  }

  protected String employeeType;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getEmployeeType()
   */
  public String getEmployeeType()
  {
    return employeeType;
  }

  /**
   * @param employeeType
   *          The employeeType to set.
   */
  public void setEmployeeType(String employeeType)
  {
    this.employeeType = employeeType;
  }

  protected byte[] userPKCS12;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getUserPKCS12()
   */
  public byte[] getUserPKCS12()
  {
    return userPKCS12;
  }

  /**
   * @param userPKCS12
   *          The userPKCS12 to set.
   */
  public void setUserPKCS12(byte[] userPKCS12)
  {
    this.userPKCS12 = userPKCS12;
  }

  protected String businessCategory;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getBusinessCategory()
   */
  public String getBusinessCategory()
  {
    return businessCategory;
  }

  /**
   * @param businessCategory
   *          The businessCategory to set.
   */
  public void setBusinessCategory(String businessCategory)
  {
    this.businessCategory = businessCategory;
  }

  protected String x500UniqueIdentifier;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getX500UniqueIdentifier()
   */
  public String getX500UniqueIdentifier()
  {
    return x500UniqueIdentifier;
  }

  /**
   * @param uniqueIdentifier
   *          The x500UniqueIdentifier to set.
   */
  public void setX500UniqueIdentifier(String uniqueIdentifier)
  {
    x500UniqueIdentifier = uniqueIdentifier;
  }

  protected String roomNumber;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getRoomNumber()
   */
  public String getRoomNumber()
  {
    return roomNumber;
  }

  /**
   * @param roomNumber
   *          The roomNumber to set.
   */
  public void setRoomNumber(String roomNumber)
  {
    this.roomNumber = roomNumber;
  }

  protected String secretary;

  /**
   * @see org.sakaiproject.service.profile.InetOrgPerson#getSecretary()
   */
  public String getSecretary()
  {
    return secretary;
  }

  /**
   * @param secretary
   *          The secretary to set.
   */
  public void setSecretary(String secretary)
  {
    this.secretary = secretary;
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/InetOrgPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/
