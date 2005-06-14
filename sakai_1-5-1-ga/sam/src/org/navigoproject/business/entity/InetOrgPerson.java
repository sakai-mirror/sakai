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
 * See RFC 2798.
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: InetOrgPerson.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public interface InetOrgPerson
  extends OrganizationalPerson
{
  /**
   * RFC 1274 notes that the proprietary format they recommend is "interim" only.
   * @return
   */
  public byte[] getAudio();

  /**
   * The name(s) that should appear in white-pages-like applications for this person.
   * From RFC 2798 description: "preferred name of a person to be used when displaying
   * entries."
   * @return
   */
  public String getDisplayName();

  /**
   * From RFC 2256 description:" The givenName attribute is used to hold the part of
   * a person's name which is not their surname nor middle name."
   * @return
   */
  public Collection getGivenName();

  /**
   * From RFC 1274 description: "The [homePhone] attribute type specifies a home
   * telephone number associated with a person.” Attribute values should follow the
   * agreed format for international telephone numbers: i.e., "+44 71 123 4567."
   * @return
   */
  public Collection getHomePhone();

  /**
   * From RFC 1274 description: "The Home postal address attribute type specifies a
   * home postal address for an object. This should be limited to up to 6 lines of
   * 30 characters each."
   * @return
   */
  public Collection getHomePostalAddress();

  /**
   * From RFC 2256 description: "The initials attribute contains the initials of
   * some or all of an individuals names, but not the surname(s)."
   * @return
   */
  public Collection getInitials();

  /**
   * Follow inetOrgPerson definition of RFC 2798: "Used to store one or more images
   * of a person using the JPEG File Interchange Format [JFIF]."
   * @return
   */
  public Collection getJpegPhoto();

  /**
   * Follow inetOrgPerson definition of RFC 2079: "Uniform Resource Identifier with
   * optional label."
   * <p>Most commonly a URL for a web site associated with this person.
   * @return
   */
  public Collection getLabeledURI();

  /**
   * Follow inetOrgPerson definition of RFC 1274: "The [mail] attribute type
   * specifies an electronic mailbox attribute following the syntax specified in
   * RFC 822. Note that this attribute should not be used for greybook or other
   * non-Internet order mailboxes."
   * @return
   */
  public Collection getMail();

  /**
   * Follow inetOrgPerson definition which refers to RFC 1274: "The manager attribute
   * type specifies the manager of an object represented by an entry." The value is a
   * DN.
   * @return
   */
  public Collection getManager();

  /**
   * Follow inetOrgPerson definition of RFC 1274: "The [mobile] attribute type
   * specifies a mobile telephone number associated with a person. Attribute values
   * should follow the agreed format for international telephone numbers:
   * i.e., "+44 71 123 4567."
   * @return
   */
  public Collection getMobile();

  /**
   * Standard name of the top-level organization (institution) with which this person
   * is associated.
   * @return
   */
  public Collection getOrganization();

  /**
   * Follow inetOrgPerson definition of RFC 1274: "The [pager] attribute type
   * specifies a pager telephone number for an object. Attribute values should
   * follow the agreed format for international telephone numbers:
   * i.e., "+44 71 123 4567."
   * @return
   */
  public Collection getPager();

  /**
   * Follow inetOrgPerson definition of RFC 2798: "preferred written or spoken
   * language for a person.”
   * <p>See RFC2068 and ISO 639 for allowable values in this field. Esperanto,
   * for example is EO in ISO 639, and RFC2068 would allow a value of en-US
   * for US English.
   * @return
   */
  public String getPreferredLanguage();

  /**
   * Follow inetOrgPerson definition of RFC 1274: "The [uid] attribute type
   * specifies a computer system login name."
   * @return
   */
  public Collection getUid();

  /**
   * A user's X.509 certificate
   * @return
   */
  public byte[] getUserCertificate();

  /**
   * An X.509 certificate specifically for use in S/MIME applications
   * (see RFCs 2632, 2633 and 2634).
   * @return
   */
  public byte[] getUserSMIMECertificate();

  /**
   * RFC 2798
   * <p>This multivalued field is used to record the values of the license or
   * registration plate associated with an individual
   * @return
   */
  public Collection getCarLicense();

  /**
   * RFC 2798
   * <p>Code for department to which a person belongs.  This can also be strictly
   * numeric (e.g., 1234) or alphanumeric (e.g., ABC/123).
   * @return
   */
  public Collection getDepartmentNumber();

  /**
   * RFC 2798
   * <p>Numeric or alphanumeric identifier assigned to a person, typically based on
   * order of hire or association with an organization.  Single valued.
   * @return
   */
  public String getEmployeeNumber();

  /**
   * RFC 2798
   * <p>Used to identify the employer to employee relationship.  Typical values used
   * will be "Contractor", "Employee", "Intern", "Temp", "External", and "Unknown"
   * but any value may be used.
   * @return
   */
  public String getEmployeeType();

  /**
   * PKCS #12 [PKCS12] provides a format for exchange of personal identity
   * information.  When such information is stored in a directory service, the
   * userPKCS12 attribute should be used. This attribute is to be stored and
   * requested in binary form, as 'userPKCS12;binary'. The attribute values are
   * PFX PDUs stored as binary data.
   * @return
   */
  public byte[] getUserPKCS12();

  /**
   * RFC 2798
   * @return
   */
  public String getBusinessCategory();

  /**
   * RFC 2798
   * <p>Likely to change if directory is reloaded with data.
   * @return
   */
  public String getX500UniqueIdentifier();

  /**
   * RFC 2798
   * @return
   */
  public String getRoomNumber();

  /**
   * RFC 2798
   * @return
   */
  public String getSecretary();
}
