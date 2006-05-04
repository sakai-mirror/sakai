/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/edu-person-api/src/java/org/sakaiproject/api/common/edu/person/EduPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
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
 * See Internet2 EduPerson specification.
 * <p>
 * EduPerson is an auxiliary object class for campus directories designed to 
 * facilitate communication among higher education institutions. It consists of 
 * a set of data elements or attributes about individuals within higher 
 * education, along with recommendations on the syntax and semantics of the data 
 * that may be assigned to those attributes.
 * </p>
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: EduPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
 */
public interface EduPerson extends InetOrgPerson
{
  /**
   * Specifies the person's relationship(s) to the institution in broad 
   * categories such as student, faculty, staff, alum, etc. (See controlled 
   * vocabulary). Permissible values (if controlled) faculty, student, staff, 
   * alum, member, affiliate, employee.
   * 
   * @return
   */
  public String getAffiliation();

  /**
   * Specifies the person's relationship(s) to the institution in broad 
   * categories such as student, faculty, staff, alum, etc. (See controlled 
   * vocabulary). Permissible values (if controlled) faculty, student, staff, 
   * alum, member, affiliate, employee.
   * 
   * @return
   */
  public void setAffiliation(String affiliation);

  /**
   * URI (either URN or URL) that indicates a set of rights to specific 
   * resources.
   * 
   * @return
   */
  public String getEntitlement();

  /**
   * URI (either URN or URL) that indicates a set of rights to specific 
   * resources.
   */
  public void setEntitlement(String entitlement);

  /**
   * Person's nickname, or the informal name by which they are accustomed to be 
   * hailed.
   * 
   * @return
   */
  public String getNickname();

  /**
   * Person's nickname, or the informal name by which they are accustomed to be 
   * hailed.
   */
  public void setNickname(String nickname);

  /**
   * The distinguished name (DN) of the of the directory entry representing the 
   * institution with which the person is associated.
   * 
   * @return
   */
  public String getOrgDn();

  /**
   * The distinguished name (DN) of the of the directory entry representing the 
   * institution with which the person is associated.
   */
  public void setOrgDn(String orgDn);

  /**
   * The distinguished name(s) (DN) of the directory entries representing the 
   * person's Organizational Unit(s). May be multivalued, as for example, in the 
   * case of a faculty member with appointments in multiple departments or a 
   * person who is a student in one department and an employee in another.
   * 
   * @return
   */
  public String getOrgUnitDn();

  /**
   * The distinguished name(s) (DN) of the directory entries representing the 
   * person's Organizational Unit(s). May be multivalued, as for example, in the 
   * case of a faculty member with appointments in multiple departments or a 
   * person who is a student in one department and an employee in another.
   * 
   * @return
   */
  public void setOrgUnitDn(String orgUnitDn);

  /**
   * Specifies the person's PRIMARY relationship to the institution in broad 
   * categories such as student, faculty, staff, alum, etc. (See controlled 
   * vocabulary). Permissible values (if controlled) faculty, student, staff, 
   * alum, member, affiliate, employee.
   * 
   * @return
   */
  public String getPrimaryAffiliation();

  /**
   * Specifies the person's PRIMARY relationship to the institution in broad 
   * categories such as student, faculty, staff, alum, etc. (See controlled 
   * vocabulary). Permissible values (if controlled) faculty, student, staff, 
   * alum, member, affiliate, employee.
   * 
   * @return
   */
  public void setPrimaryAffiliation(String primaryAffiliation);

  /**
   * The distinguished name (DN) of the directory entry representing the 
   * person's primary Organizational Unit(s).
   * 
   * @return
   */
  public String getPrimaryOrgUnitDn();

  /**
   * The distinguished name (DN) of the directory entry representing the 
   * person's primary Organizational Unit(s).
   */
  public void setPrimaryOrgUnitDn(String primaryOrgUnitDn);

  /**
   * The "NetID" of the person for the purposes of inter-institutional 
   * authentication. Should be stored in the form of user_at_univ.edu, where 
   * univ.edu is the name of the local security domain.
   * 
   * @return
   */
  public String getPrincipalName();

  /**
   * The "NetID" of the person for the purposes of inter-institutional 
   * authentication. Should be stored in the form of user_at_univ.edu, where 
   * univ.edu is the name of the local security domain.
   * 
   * @return
   */
  public void setPrincipalName(String principalName);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/edu-person-api/src/java/org/sakaiproject/api/common/edu/person/EduPerson.java,v 1.1 2005/05/10 21:23:24 lance.indiana.edu Exp $
 *
 **********************************************************************************/
