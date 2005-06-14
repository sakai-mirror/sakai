/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/edu-person-api/src/java/org/sakaiproject/api/common/edu/person/OrganizationalPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.edu.person;

/**
 * See ITU X.521 spec.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: OrganizationalPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
 */
public interface OrganizationalPerson extends Person
{
  /**
   * A fax number for the directory entry. Attribute values should follow the 
   * agreed format for international telephone numbers: i.e., "+44 71 123 4567."
   * 
   * @return
   */
  public String getFacsimileTelephoneNumber();

  /**
   * A fax number for the directory entry. Attribute values should follow the 
   * agreed format for international telephone numbers: i.e., "+44 71 123 4567."
   * 
   * @return
   */
  public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber);

  /**
   * According to RFC 2256, "This attribute contains the name of a locality, 
   * such as a city, county or other geographic region (localityName)."
   * <p>
   * X.520(2000) reads: "The Locality Name attribute type specifies a locality. 
   * When used as a component of a directory name, it identifies a geographical 
   * area or locality in which the named object is physically located or with 
   * which it is associated in some other important way.”
   * 
   * @return
   */
  public String getLocalityName();

  /**
   * According to RFC 2256, "This attribute contains the name of a locality, 
   * such as a city, county or other geographic region (localityName)."
   * <p>
   * X.520(2000) reads: "The Locality Name attribute type specifies a locality. 
   * When used as a component of a directory name, it identifies a geographical 
   * area or locality in which the named object is physically located or with 
   * which it is associated in some other important way.”
   * 
   * @return
   */
  public void setLocalityName(String localityName);

  /**
   * Abbreviation for state or province name.
   * <p>
   * Format: The values should be coordinated on a national level and if 
   * well-known shortcuts exist - like the two-letter state abbreviations in 
   * the US – these abbreviations are preferred over longer full names.
   * <p>
   * According to RFC 2256, "This attribute contains the full name of a state or 
   * province (stateOrProvinceName)."
   * <p>
   * Permissible values (if controlled)
   * <p>
   * For states in the United States, U.S. Postal Service set of two-letter 
   * state name abbreviations.
   * 
   * @return
   */
  public String getStateOrProvinceName();

  /**
   * Abbreviation for state or province name.
   * <p>
   * Format: The values should be coordinated on a national level and if 
   * well-known shortcuts exist - like the two-letter state abbreviations in 
   * the US – these abbreviations are preferred over longer full names.
   * <p>
   * According to RFC 2256, "This attribute contains the full name of a state or 
   * province (stateOrProvinceName)."
   * <p>
   * Permissible values (if controlled)
   * <p>
   * For states in the United States, U.S. Postal Service set of two-letter 
   * state name abbreviations.
   * 
   * @return
   */
  public void setStateOrProvinceName(String stateOrProvinceName);

  /**
   * Follow X.500(2001): "The postal code attribute type specifies the postal 
   * code of the named object. If this attribute value is present, it will be 
   * part of the object's postal address." Zip code in USA, postal code for 
   * other countries.
   * 
   * @return
   */
  public String getPostalCode();

  /**
   * Follow X.500(2001): "The postal code attribute type specifies the postal 
   * code of the named object. If this attribute value is present, it will be 
   * part of the object's postal address." Zip code in USA, postal code for 
   * other countries.
   * 
   * @return
   */
  public void setPostalCode(String postalCode);

  /**
   * Follow X.500(2001): "The Post Office Box attribute type specifies the 
   * Postal Office Box by which the object will receive physical postal 
   * delivery. If present, the attribute value is part of the object's postal 
   * address."
   * 
   * @return
   */
  public String getPostOfficeBox();

  /**
   * Follow X.500(2001): "The Post Office Box attribute type specifies the 
   * Postal Office Box by which the object will receive physical postal 
   * delivery. If present, the attribute value is part of the object's postal 
   * address."
   * 
   * @return
   */
  public void setPostOfficeBox(String postOfficeBox);

  /**
   * From X.521 spec: LocaleAttributeSet, PostalAttributeSet
   * 
   * @return
   */
  public String getStreetAddress();

  /**
   * From X.521 spec: LocaleAttributeSet, PostalAttributeSet
   */
  public void setStreetAddress(String streetAddress);

  /**
   * From X.521 spec: PostalAttributeSet
   * 
   * @return
   */
  public String getPhysicalDeliveryOfficeName();

  /**
   * From X.521 spec: PostalAttributeSet
   */
  public void setPhysicalDeliveryOfficeName(String physicalDeliveryOfficeName);

  /**
   * From X.521 spec: PostalAttributeSet
   * <p>
   * Campus or office address. inetOrgPerson has a homePostalAddress that 
   * complements this attribute. X.520(2000) reads: "The Postal Address 
   * attribute type specifies the address information required for the physical 
   * postal delivery to an object."
   * 
   * @return
   */
  public String getPostalAddress();

  /**
   * From X.521 spec: PostalAttributeSet
   * <p>
   * Campus or office address. inetOrgPerson has a homePostalAddress that 
   * complements this attribute. X.520(2000) reads: "The Postal Address 
   * attribute type specifies the address information required for the physical 
   * postal delivery to an object."
   * 
   * @return
   */
  public void setPostalAddress(String postalAddress);

  /**
   * Follow X.520(2001): "The Title attribute type specifies the designated 
   * position or function of the object within an organization."
   * 
   * @return
   */
  public String getTitle();

  /**
   * Follow X.520(2001): "The Title attribute type specifies the designated 
   * position or function of the object within an organization."
   * 
   * @return
   */
  public void setTitle(String title);

  /**
   * Organizational unit(s). According to X.520(2000), "The Organizational Unit 
   * Name attribute type specifies an organizational unit. When used as a 
   * component of a directory name it identifies an organizational unit with 
   * which the named object is affiliated.
   * <p>
   * The designated organizational unit is understood to be part of an 
   * organization designated by an OrganizationName [o] attribute. It follows 
   * that if an Organizational Unit Name attribute is used in a directory name, 
   * it must be associated with an OrganizationName [o] attribute.
   * <p>
   * An attribute value for Organizational Unit Name is a string chosen by the 
   * organization of which it is a part."
   * 
   * @return
   */
  public String getOrganizationalUnit();

  /**
   * Organizational unit(s). According to X.520(2000), "The Organizational Unit 
   * Name attribute type specifies an organizational unit. When used as a 
   * component of a directory name it identifies an organizational unit with 
   * which the named object is affiliated.
   * <p>
   * The designated organizational unit is understood to be part of an 
   * organization designated by an OrganizationName [o] attribute. It follows 
   * that if an Organizational Unit Name attribute is used in a directory name, 
   * it must be associated with an OrganizationName [o] attribute.
   * <p>
   * An attribute value for Organizational Unit Name is a string chosen by the 
   * organization of which it is a part."
   * 
   * @return
   */
  public void setOrganizationalUnit(String organizationalUnit);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/edu-person-api/src/java/org/sakaiproject/api/common/edu/person/OrganizationalPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
 *
 **********************************************************************************/
