/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/EduPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
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

import org.sakaiproject.api.common.edu.person.EduPerson;
import org.sakaiproject.api.common.edu.person.InetOrgPerson;
import org.sakaiproject.api.common.edu.person.OrganizationalPerson;
import org.sakaiproject.api.common.edu.person.Person;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: EduPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 */
public class EduPersonImpl extends InetOrgPersonImpl implements Person,
    OrganizationalPerson, InetOrgPerson, EduPerson
{

  protected String affiliation;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getAffiliation()
   */
  public String getAffiliation()
  {
    return affiliation;
  }

  /**
   * @param affiliation
   *          The affiliation to set.
   */
  public void setAffiliation(String affiliation)
  {
    this.affiliation = affiliation;
  }

  protected String entitlement;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getEntitlement()
   */
  public String getEntitlement()
  {
    return entitlement;
  }

  /**
   * @param entitlement
   *          The entitlement to set.
   */
  public void setEntitlement(String entitlement)
  {
    this.entitlement = entitlement;
  }

  protected String nickname;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getNickname()
   */
  public String getNickname()
  {
    return nickname;
  }

  /**
   * @param nickname
   *          The nickname to set.
   */
  public void setNickname(String nickname)
  {
    this.nickname = nickname;
  }

  protected String orgDn;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getOrgDn()
   */
  public String getOrgDn()
  {
    return orgDn;
  }

  /**
   * @param orgDn
   *          The orgDn to set.
   */
  public void setOrgDn(String orgDn)
  {
    this.orgDn = orgDn;
  }

  protected String orgUnitDn;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getOrgUnitDn()
   */
  public String getOrgUnitDn()
  {
    return orgUnitDn;
  }

  /**
   * @param orgUnitDn
   *          The orgUnitDn to set.
   */
  public void setOrgUnitDn(String orgUnitDn)
  {
    this.orgUnitDn = orgUnitDn;
  }

  protected String primaryAffiliation;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getPrimaryAffiliation()
   */
  public String getPrimaryAffiliation()
  {
    return primaryAffiliation;
  }

  /**
   * @param primaryAffiliation
   *          The primaryAffiliation to set.
   */
  public void setPrimaryAffiliation(String primaryAffiliation)
  {
    this.primaryAffiliation = primaryAffiliation;
  }

  protected String primaryOrgUnitDn;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getPrimaryOrgUnitDn()
   */
  public String getPrimaryOrgUnitDn()
  {
    return primaryOrgUnitDn;
  }

  /**
   * @param primaryOrgUnitDn
   *          The primaryOrgUnitDn to set.
   */
  public void setPrimaryOrgUnitDn(String primaryOrgUnitDn)
  {
    this.primaryOrgUnitDn = primaryOrgUnitDn;
  }

  protected String principalName;

  /**
   * @see org.sakaiproject.service.profile.EduPerson#getPrincipalName()
   */
  public String getPrincipalName()
  {
    return principalName;
  }

  /**
   * @param principalName
   *          The principalName to set.
   */
  public void setPrincipalName(String principalName)
  {
    this.principalName = principalName;
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/EduPersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/
