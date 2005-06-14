/*
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
*/

package org.sakaiproject.tool.assessment.data.dao.questionpool;
import org.sakaiproject.tool.assessment.data.ifc.questionpool.QuestionPoolDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.osid.agent.Agent;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolData.java,v 1.6 2005/02/09 23:53:04 lydial.stanford.edu Exp $
 */
public class QuestionPoolData
  implements Serializable, QuestionPoolDataIfc
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 9180085666292824370L;
  public static Long ACCESS_DENIED = new Long(30);
  public static Long READ_ONLY = new Long(31);
  public static Long READ_COPY = new Long(32);
  public static Long READ_WRITE = new Long(33);
  public static Long ADMIN = new Long(34);
  public static Long DEFAULT_TYPEID = new Long(0);
  public static Long DEFAULT_INTELLECTUAL_PROPERTYID = new Long(0);
  public static Long ROOT_POOL = new Long(0);

  private Long questionPoolId;
  private String title;
  private String description;
  private Long parentPoolId = ROOT_POOL;

  private String ownerId = null;
  private Agent owner;
  private Date dateCreated;
  private Date lastModified;
  private String lastModifiedById;
  private Agent lastModifiedBy;
  private Long accessTypeId = null;
  private TypeIfc accessType;
  private String objectives;
  private String keywords;
  private String rubric;
  private Long typeId;
  private TypeIfc type;
  private Long intellectualPropertyId;
  private String organizationName;
  private Set questionPoolItems;
  private Collection items = new ArrayList();

  private Integer subPoolSize;

  public QuestionPoolData(){
  }
  /**
   * This is a cheap object created for holding just the Id & title. This is
   * used by the pulldown list in authoring when we only need the Id & title
   * and nothing else. This object is not used for persistence.
   * @param poolId 
   * @param title
   */
  public QuestionPoolData(Long poolId, String title){
    this.questionPoolId= poolId;
    this.title = title;
  }


  public Long getQuestionPoolId()
  {
    return questionPoolId;
  }

  public void setQuestionPoolId(Long questionPoolId)
  {
    this.questionPoolId = questionPoolId;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  // added to faciliate Hibernate POJO requirement, parentPool is not part of the orginal
  // QuestionPoolProperties. Will see if I can make it part of it later  - daisyf on 8/25/04
  public Long getParentPoolId()
  {
    return parentPoolId;
  }

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public void setParentPoolId(Long parentPoolId)
  {
    this.parentPoolId = parentPoolId;
  }

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public String getOwnerId()
  {
    return ownerId;
  }

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public void setOwnerId(String ownerId)
  {
    this.ownerId = ownerId;
  }

  public Agent getOwner()
  {
    return owner;
  }

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public void setOwner(Agent owner)
  {
    this.owner = owner;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getDateCreated()
  {
    return dateCreated;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dateCreated DOCUMENTATION PENDING
   */
  public void setDateCreated(Date dateCreated)
  {
    this.dateCreated = dateCreated;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getLastModified()
  {
    return lastModified;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param lastModified DOCUMENTATION PENDING
   */
  public void setLastModified(Date lastModified)
  {
    this.lastModified = lastModified;
  }

  public String getLastModifiedById()
  {
    return lastModifiedById;
  }

  public void setLastModifiedById(String lastModifiedById)
  {
    this.lastModifiedById = lastModifiedById;
  }

  public Agent getLastModifiedBy()
  {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(Agent lastModifiedBy)
  {
    this.lastModifiedBy = lastModifiedBy;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public Long getAccessTypeId()
  {
    return this.accessTypeId;
  }

  // added to faciliate Hibernate POJO requirement, also for getting the Type without
  // making a trip to the DB - daisyf on 8/25/04
  public void setAccessTypeId(Long accessTypeId)
  {
    this.accessTypeId = accessTypeId;
  }

  public TypeIfc getAccessType()
  {
    return this.accessType;
  }

  public void setAccessType(TypeIfc accessType)
  {
    this.accessType = accessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives()
  {
    return objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objectives DOCUMENTATION PENDING
   */
  public void setObjectives(String objectives)
  {
    this.objectives = objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords()
  {
    return keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newKeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String keywords)
  {
    this.keywords = keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubric()
  {
    return rubric;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rubric DOCUMENTATION PENDING
   */
  public void setRubric(String rubric)
  {
    this.rubric = rubric;
  }

  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public Long getTypeId()
  {
    return typeId;
  }

  // added to faciliate Hibernate POJO requirement, also for getting the Type
  // without making a trip to the DB - daisyf on 8/25/04
  public void setTypeId(Long typeId)
  {
    this.typeId = typeId;
  }

  public TypeIfc getType()
  {
    return this.type;
  }

  public void setType(TypeIfc type)
  {
    this.type = type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Long getIntellectualPropertyId()
  {
    return intellectualPropertyId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param intellectualProperty DOCUMENTATION PENDING
   */
  public void setIntellectualPropertyId(Long intellectualPropertyId)
  {
    this.intellectualPropertyId = intellectualPropertyId;
  }

  public void setIntellectualProperty(String intellectualProperty)
  {
      setIntellectualPropertyId(new Long(intellectualProperty));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getOrganizationName()
  {
    return organizationName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param organizationName DOCUMENTATION PENDING
   */
  public void setOrganizationName(String organizationName)
  {
    this.organizationName = organizationName;
  }


  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public Set getQuestionPoolItems()
  {
    return questionPoolItems;
  }


  // added to faciliate Hibernate POJO requirement - daisyf on 8/25/04
  public void setQuestionPoolItems(Set questionPoolItems)
  {
    this.questionPoolItems = questionPoolItems;
  }

  public void addQuestionPoolItem(QuestionPoolItemData questionPoolItem){
    questionPoolItems.add(questionPoolItem);
  } 

  /**
   * DOCUMENTATION PENDING
   *
   * @return an ArrayList of org.navigoproject.business.entity.assessment.model.ItemImpl
   */
  public Collection getQuestions()
  {
    return items;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newItems DOCUMENTATION PENDING
   */
  public void setQuestions(Collection items)
  {
      this.items = items;
  }

  // for JSF, sigh - daisyf
  public Integer getQuestionSize()
  {
    return new Integer(items.size());
  }

  // for JSF, sigh - daisyf
  public void setSubPoolSize(Integer subPoolSize)
  {
    this.subPoolSize = subPoolSize;
  }

  // for JSF, sigh - daisyf
  public Integer getSubPoolSize()
  {
    return subPoolSize;
  }

}
