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
 * @version $Id: Person.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public interface Person
{
  /**
   * Common name. According to RFC 2256, "This is the X.500 commonName attribute,
   * which contains a name of an object. If the object corresponds to a person, it
   * is typically the person's full name.�
   * @return
   */
  public Collection getCommonName();

  /**
   * Open-ended; whatever the person or the directory manager puts here. According
   * to RFC 2256, "This attribute contains a human-readable description of the object."
   * @return
   */
  public Collection getDescription();

  /**
   * Follow person object class definition: Identifies (by DN) another directory
   * server entry that may contain information related to this entry.
   * <p>According to X.520(2000), "The See Also attribute type specifies names of
   * other Directory objects which may be other aspects (in some sense) of the same
   * real world object."
   * @return
   */
  public Collection getSeeAlso();

  /**
   * Surname or family name. According to RFC 2256, "This is the X.500 surname
   * attribute, which contains the family name of a person."
   * @return
   */
  public Collection getSurname();

  /**
   * According to RFC 2256, "This attribute contains the physical address of the
   * object to which the entry corresponds, such as an address for package delivery
   * (streetAddress)."
   * @return
   */
  public Collection getStreet();

  /**
   * Office/campus phone number. Attribute values should follow the agreed format for
   * international telephone numbers: i.e., "+44 71 123 4567."
   * @return
   */
  public Collection getTelephoneNumber();

  //  public String getUserPassword();
}
