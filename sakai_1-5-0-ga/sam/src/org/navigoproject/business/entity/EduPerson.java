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
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: OKI based implementation
 * </p>
 *
 * <p>
 * Copyright: Copyright 2003 Trustees of Indiana University
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: EduPerson.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public interface EduPerson
  extends InetOrgPerson
{
  /**
   * Specifies the person's relationship(s) to the institution in broad
   * categories such as student, faculty, staff, alum, etc. (See controlled
   * vocabulary). Permissible values (if controlled)
   * <pre>faculty, student, staff, alum, member, affiliate, employee</pre>
   *
   * @return
   */
  public Collection getAffiliation();

  /**
   * URI (either URN or URL) that indicates a set of rights to specific
   * resources.
   *
   * @return
   */
  public Collection getEntitlement();

  /**
   * Person's nickname, or the informal name by which they are accustomed to be
   * hailed.
   *
   * @return
   */
  public Collection getNickname();

  /**
   * The distinguished name (DN) of the of the directory entry representing the
   * institution with which the person is associated.
   *
   * @return
   */
  public String getOrgDn();

  /**
   * The distinguished name(s) (DN) of the directory entries representing the
   * person's Organizational Unit(s). May be multivalued, as for example, in
   * the case of a faculty member with appointments in multiple departments or
   * a person who is a student in one department and an employee in another.
   *
   * @return
   */
  public Collection getOrgUnitDn();

  /**
   * Specifies the person's PRIMARY relationship to the institution in broad
   * categories such as student, faculty, staff, alum, etc. (See controlled
   * vocabulary). Permissible values (if controlled)
   * <pre>faculty, student, staff, alum, member, affiliate, employee</pre>
   *
   * @return
   */
  public String getPrimaryAffiliation();

  /**
   * The distinguished name (DN) of the directory entry representing the
   * person's primary Organizational Unit(s).
   *
   * @return
   */
  public String getPrimaryOrgUnitDn();

  /**
   * The "NetID" of the person for the purposes of inter-institutional
   * authentication. Should be stored in the form of user_at_univ.edu, where
   * univ.edu is the name of the local security domain.
   *
   * @return
   */
  public String getPrincipalName();
}
