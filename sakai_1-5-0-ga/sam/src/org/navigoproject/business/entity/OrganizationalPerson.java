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

package org.navigoproject.business.entity;

import java.util.Collection;

/**
 * See ITU X.521 spec.
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: OrganizationalPerson.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public interface OrganizationalPerson
  extends Person
{
  /**
   * A fax number for the directory entry. Attribute values should follow the agreed
   * format for international telephone numbers: i.e., "+44 71 123 4567."
   * @return
   */
  public Collection getFacsimileTelephoneNumber();

  /**
   * According to RFC 2256, "This attribute contains the name of a locality, such as
   * a city, county or other geographic region (localityName)."
   * <p>X.520(2000) reads: "The Locality Name attribute type specifies a locality.
   * When used as a component of a directory name, it identifies a geographical area
   * or locality in which the named object is physically located or with which it is
   * associated in some other important way.”
   * @return
   */
  public Collection getLocalityName();

  /**
   * Abbreviation for state or province name.
   * <p>Format: The values should be coordinated on a national level and if well-known
   * shortcuts exist - like the two-letter state abbreviations in the US – these
   * abbreviations are preferred over longer full names.
   * <p>According to RFC 2256, "This attribute contains the full name of a state or
   * province (stateOrProvinceName)."
   * <p>Permissible values (if controlled)
   * <p> For states in the United States, U.S. Postal Service set of two-letter
   * state name abbreviations.
   * @return
   */
  public Collection getStateOrProvinceName();

  /**
   * Follow X.500(2001): "The postal code attribute type specifies the postal code of
   * the named object. If this attribute value is present, it will be part of the
   * object's postal address." Zip code in USA, postal code for other countries.
   * @return
   */
  public Collection getPostalCode();

  /**
   * Follow X.500(2001): "The Post Office Box attribute type specifies the Postal
   * Office Box by which the object will receive physical postal delivery. If
   * present, the attribute value is part of the object's postal address."
   * @return
   */
  public Collection getPostOfficeBox();

  /**
   * From X.521 spec: LocaleAttributeSet, PostalAttributeSet
   * @return
   */
  public String getStreetAddress();

  /**
   * From X.521 spec: PostalAttributeSet
   * @return
   */
  public String getPhysicalDeliveryOfficeName();

  /**
   * From X.521 spec: PostalAttributeSet
   * <p>Campus or office address. inetOrgPerson has a homePostalAddress that complements
   * this attribute. X.520(2000) reads: "The Postal Address attribute type specifies
   * the address information required for the physical postal delivery to an object."
   * @return
   */
  public Collection getPostalAddress();

  /**
   * Follow X.520(2001): "The Title attribute type specifies the designated position
   * or function of the object within an organization."
   * @return
   */
  public Collection getTitle();

  /**
   * Organizational unit(s). According to X.520(2000), "The Organizational Unit Name
   * attribute type specifies an organizational unit. When used as a component of a
   * directory name it identifies an organizational unit with which the named object
   * is affiliated.
   * <p>The designated organizational unit is understood to be part of an organization
   * designated by an OrganizationName [o] attribute. It follows that if an Organizational
   * Unit Name attribute is used in a directory name, it must be associated with an
   * OrganizationName [o] attribute.
   * <p>An attribute value for Organizational Unit Name is a string chosen by the
   * organization of which it is a part."
   * @return
   */
  public Collection getOrganizationalUnit();
}
