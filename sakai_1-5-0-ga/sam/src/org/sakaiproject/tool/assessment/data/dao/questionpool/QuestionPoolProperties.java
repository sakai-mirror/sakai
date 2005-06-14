
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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

import osid.shared.SharedManager;
import osid.shared.Agent;
import osid.shared.Type;
import org.sakaiproject.tool.assessment.queries.TypeImplQueries;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.osid.OsidManagerFactory;

import osid.assessment.Item;
import osid.assessment.AssessmentException;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPool;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolProperties.java,v 1.4 2004/10/08 21:27:03 daisyf.stanford.edu Exp $
 */
public class QuestionPoolProperties
  implements Serializable
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
  public static Long NO_PARENT_POOL = new Long(0);

  private Long id;
  private String title;
  private String description;
  private Long parentPoolId = NO_PARENT_POOL;
  private QuestionPool parentPool;

  private String ownerId;
  private Agent owner;
  private Date dateCreated;
  private Date lastModified;
  private String lastModifiedById;
  private Agent lastModifiedBy;
  private Long accessTypeId;
  private Type accessType; // This will change depending on who's
                           // requesting the question pool.
  private String objectives;
  private String keywords;
  private String rubric;
  private Long typeId;
  private Type type;
  private Long intellectualPropertyId;
  private String organizationName;
  private Set questionPoolItems;
  private Collection items = new ArrayList();

  private Integer subPoolSize;

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
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
    try{
      SharedManager sm = OsidManagerFactory.createSharedManager();
      this.owner = sm.getAgent(sm.getId(ownerId));
    }
    catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Agent getOwner()
  {
    return owner;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param owner DOCUMENTATION PENDING
   */
  public void setOwner(Agent owner)
  {
    this.owner = owner;
    try{
      this.ownerId = owner.getId().getIdString();
    }
    catch (Exception e){
      System.out.println(e.getMessage());
    }
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
    try{
      SharedManager sm = OsidManagerFactory.createSharedManager();
      this.lastModifiedBy = sm.getAgent(sm.getId(ownerId));
    }
    catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Agent getLastModifiedBy()
  {
    return lastModifiedBy;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param lastModifiedBy DOCUMENTATION PENDING
   */
  public void setLastModifiedBy(Agent lastModifiedBy)
  {
    this.lastModifiedBy = lastModifiedBy;
    try{
      this.lastModifiedById = lastModifiedBy.getId().getIdString();
    }
    catch (Exception e){
      System.out.println(e.getMessage());
    }
  }

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
    TypeImplQueries typeImplQueries = PersistenceService.getInstance().getTypeImplQueries();
    setAccessType(typeImplQueries.getTypeImplByStanfordId(accessTypeId));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getAccessType()
  {
    return accessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param accessType DOCUMENTATION PENDING
   */
  public void setAccessType(Type accessType)
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
    TypeImplQueries typeImplQueries = PersistenceService.getInstance().getTypeImplQueries();
    setType(typeImplQueries.getTypeImplByStanfordId(typeId));
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type DOCUMENTATION PENDING
   */
  public void setType(Type type)
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
