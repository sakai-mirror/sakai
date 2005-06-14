/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/PersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
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

import org.sakaiproject.api.common.edu.person.Person;
import org.sakaiproject.component.common.manager.PersistableImpl;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class PersonImpl extends PersistableImpl implements Person
{
  protected String businessKey()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(agentUuid);
    sb.append(typeUuid);
    return sb.toString();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof PersonImpl)) return false;
    PersonImpl other = (PersonImpl) obj;
    return this.businessKey().equals(other.businessKey());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return businessKey().hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("{id=");
    sb.append(id);
    sb.append(", uuid=");
    sb.append(uuid);
    sb.append(", typeUuid=");
    sb.append(typeUuid);
    sb.append("}");
    return sb.toString();
  }

  protected String agentUuid;

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPerson#getAgentUuid()
   */
  public String getAgentUuid()
  {
    return agentUuid;
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPerson#setAgentUuid(java.lang.String)
   */
  public void setAgentUuid(String agentUuid)
  {
    this.agentUuid = agentUuid;
  }

  protected String typeUuid;

  /**
   * @return Returns the typeUuid.
   */
  public String getTypeUuid()
  {
    return typeUuid;
  }

  /**
   * @param typeUuid The typeUuid to set.
   */
  public void setTypeUuid(String typeUuid)
  {
    this.typeUuid = typeUuid;
  }

  protected String commonName;

  /**
   * @see org.sakaiproject.service.profile.Person#getCommonName()
   */
  public String getCommonName()
  {
    return commonName;
  }

  /**
   * @param commonName
   *          The commonName to set.
   */
  public void setCommonName(String commonName)
  {
    this.commonName = commonName;
  }

  protected String description;

  /**
   * @see org.sakaiproject.service.profile.Person#getDescription()
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description
   *          The description to set.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  protected String seeAlso;

  /**
   * @see org.sakaiproject.service.profile.Person#getSeeAlso()
   */
  public String getSeeAlso()
  {
    return seeAlso;
  }

  /**
   * @param seeAlso
   *          The seeAlso to set.
   */
  public void setSeeAlso(String seeAlso)
  {
    this.seeAlso = seeAlso;
  }

  protected String surname;

  /**
   * @see org.sakaiproject.service.profile.Person#getSurname()
   */
  public String getSurname()
  {
    return surname;
  }

  /**
   * @param surname
   *          The surname to set.
   */
  public void setSurname(String surname)
  {
    this.surname = surname;
  }

  protected String street;

  /**
   * @see org.sakaiproject.service.profile.Person#getStreet()
   */
  public String getStreet()
  {
    return street;
  }

  /**
   * @param street
   *          The street to set.
   */
  public void setStreet(String street)
  {
    this.street = street;
  }

  protected String telephoneNumber;

  /**
   * @see org.sakaiproject.service.profile.Person#getTelephoneNumber()
   */
  public String getTelephoneNumber()
  {
    return telephoneNumber;
  }

  /**
   * @param telephoneNumber
   *          The telephoneNumber to set.
   */
  public void setTelephoneNumber(String telephoneNumber)
  {
    this.telephoneNumber = telephoneNumber;
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/edu/person/PersonImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/
