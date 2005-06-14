/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/OrganizationalPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
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

import org.sakaiproject.api.common.edu.person.OrganizationalPerson;
import org.sakaiproject.api.common.edu.person.Person;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class OrganizationalPersonImpl extends PersonImpl implements Person,
    OrganizationalPerson
{
  protected String facsimileTelephoneNumber;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getFacsimileTelephoneNumber()
   */
  public String getFacsimileTelephoneNumber()
  {
    return facsimileTelephoneNumber;
  }

  /**
   * @param facsimileTelephoneNumber
   *          The facsimileTelephoneNumber to set.
   */
  public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber)
  {
    this.facsimileTelephoneNumber = facsimileTelephoneNumber;
  }

  protected String localityName;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getLocalityName()
   */
  public String getLocalityName()
  {
    return localityName;
  }

  /**
   * @param localityName
   *          The localityName to set.
   */
  public void setLocalityName(String localityName)
  {
    this.localityName = localityName;
  }

  protected String stateOrProvinceName;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getStateOrProvinceName()
   */
  public String getStateOrProvinceName()
  {
    return stateOrProvinceName;
  }

  /**
   * @param stateOrProvinceName
   *          The stateOrProvinceName to set.
   */
  public void setStateOrProvinceName(String stateOrProvinceName)
  {
    this.stateOrProvinceName = stateOrProvinceName;
  }

  protected String postalCode;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getPostalCode()
   */
  public String getPostalCode()
  {
    return postalCode;
  }

  /**
   * @param postalCode
   *          The postalCode to set.
   */
  public void setPostalCode(String postalCode)
  {
    this.postalCode = postalCode;
  }

  protected String postOfficeBox;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getPostOfficeBox()
   */
  public String getPostOfficeBox()
  {
    return postOfficeBox;
  }

  /**
   * @param postOfficeBox
   *          The postOfficeBox to set.
   */
  public void setPostOfficeBox(String postOfficeBox)
  {
    this.postOfficeBox = postOfficeBox;
  }

  protected String streetAddress;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getStreetAddress()
   */
  public String getStreetAddress()
  {
    return streetAddress;
  }

  /**
   * @param streetAddress
   *          The streetAddress to set.
   */
  public void setStreetAddress(String streetAddress)
  {
    this.streetAddress = streetAddress;
  }

  protected String physicalDeliveryOfficeName;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getPhysicalDeliveryOfficeName()
   */
  public String getPhysicalDeliveryOfficeName()
  {
    return physicalDeliveryOfficeName;
  }

  /**
   * @param physicalDeliveryOfficeName
   *          The physicalDeliveryOfficeName to set.
   */
  public void setPhysicalDeliveryOfficeName(String physicalDeliveryOfficeName)
  {
    this.physicalDeliveryOfficeName = physicalDeliveryOfficeName;
  }

  protected String postalAddress;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getPostalAddress()
   */
  public String getPostalAddress()
  {
    return postalAddress;
  }

  /**
   * @param postalAddress
   *          The postalAddress to set.
   */
  public void setPostalAddress(String postalAddress)
  {
    this.postalAddress = postalAddress;
  }

  protected String title;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getTitle()
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * @param title
   *          The title to set.
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  protected String organizationalUnit;

  /**
   * @see org.sakaiproject.service.profile.OrganizationalPerson#getOrganizationalUnit()
   */
  public String getOrganizationalUnit()
  {
    return organizationalUnit;
  }

  /**
   * @param organizationalUnit
   *          The organizationalUnit to set.
   */
  public void setOrganizationalUnit(String organizationalUnit)
  {
    this.organizationalUnit = organizationalUnit;
  }
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/OrganizationalPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/
