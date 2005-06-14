package org.sakaiproject.tool.assessment.facade;
import org.osid.assessment.AssessmentException;
import org.osid.assessment.Section;
import org.osid.shared.Type;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.osid.assessment.impl.SectionImpl;
//import org.sakaiproject.tool.assessment.service.PersistenceService;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.navigoproject.osid.impl.PersistenceService;


import org.apache.log4j.*;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * SectionFacade implements SectionDataIfc that encapsulates our out of bound (OOB)
 * agreement.
 * @author not attributable
 * @version 1.0
 */
public class SectionFacade implements Serializable, SectionDataIfc, Comparable {
  static Category errorLogger = Category.getInstance("errorLogger");

  private static final long serialVersionUID = 7526471155622776147L;

  private org.osid.assessment.Section section;
  // We have 2 sets of properties:
  // #1) properties according to org.osid.assessment.Section. However, we will
  // not have property "displayName" because I am not sure what it is.
  // Properties "description" will be persisted through data - daisyf 07/28/04
  private org.osid.shared.Id id;
  private String description;
  private SectionDataIfc data;
  private org.osid.shared.Type sectionType;
  // #2) properties according to SectionDataIfc
  private Long sectionId;
  private Long assessmentId;
  private AssessmentFacade assessment;
  private Integer duration;
  private Integer sequence;
  private String title;
  private Long typeId;
  private Integer status;
  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;
  private Set itemSet;
  private Set itemFacadeSet;

  /** SectionFacade is the class that is exposed to developer
   *  It contains some of the useful methods specified in
   *  org.osid.assessment.Section and it implements
   *  org.sakaiproject.tool.assessment.ifc.
   *  When new methods is added to osid api, this code is still workable.
   *  If signature in any of the osid methods that we mirrored changes,
   *  we only need to modify those particular methods.
   *  - daisyf
   */

  public SectionFacade(){
  // need to hook SectionFacade.data to SectionData, our POJO for Hibernate
  // persistence
   this.data = new SectionData();
   SectionImpl sectionImpl = new SectionImpl(); //<-- place holder
   section = (Section)sectionImpl;
   try {
     section.updateData(this.data);
   }
   catch (AssessmentException ex) {
     throw new DataFacadeException(ex.getMessage());
   }
  }

  /**
   * This is a very important constructor. Please make sure that you have
   * set all the properties (declared above as private) of SectionFacade using
   * the "data" supplied. "data" is a org.osid.assessment.Section properties
   * and I use it to store info about an section.
   * @param data
   */
  public SectionFacade(SectionDataIfc data){
    this.data = data;
    SectionImpl sectionImpl = new SectionImpl(); // place holder
    section = (Section)sectionImpl;
    try {
      section.updateData(this.data);
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    this.id = getId();
    this.description = getDescription();
    this.sectionType = getSectionType();
    this.sequence = getSequence();
    this.duration = getDuration();
    this.typeId = getTypeId();
    this.status = getStatus();
    this.createdBy = getCreatedBy();
    this.createdDate = getCreatedDate();
    this.lastModifiedBy = getLastModifiedBy();
    this.lastModifiedDate = getLastModifiedDate();
    this.itemSet = getItemSet();
    // *TODO* will work on returning itemFacade later, sorry! daisyf 11/22/04
  }

  // the following method's signature has a one to one relationship to
  // org.sakaiproject.tool.assessment.osid.section.SectionImpl
  // which implements org.osid.assessment.Section

  /**
   * Get the Id for this SectionFacade.
   * @return org.osid.shared.Id
   */
  org.osid.shared.Id getId(){
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    /**
    SectionFacadeQueries sectionFacadeQueries = new SectionFacadeQueries();
    return sectionFacadeQueries.getSectionId(this.data.getSectionId());
    */
    ItemFacadeQueries itemFacadeQueries = new ItemFacadeQueries();
    return itemFacadeQueries.getItemId(this.data.getSectionId());
  }

  /**
   * Get the Type for this SectionFacade.
   * @return org.osid.shared.Type
   */
  Type getSectionType() {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    return typeFacadeQueries.getTypeById(this.data.getTypeId());
  }

  /**
   * Get the data for this SectionFacade.
   * @return SectionDataIfc
   */
  public SectionDataIfc getData(){
    return this.data;
  }

  /**
   * Call setDate() to update data in SectionFacade
   * @param data
   */
  public void updateData(SectionDataIfc data) {
      setData(data);
  }

  /**
   * Set data for SectionFacade
   * @param data
   */
  public void setData(SectionDataIfc data) {
      this.data = data;
  }

  // the following methods implements
  // org.sakaiproject.tool.assessment.ifc.SectionDataIfc
  public Long getSectionId() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getSectionId();
  }

  /**
   * Set sectionId for SectionFacade
   * @param sectionId
   */
  public void setSectionId(Long sectionId) {
    this.sectionId = sectionId;
    this.data.setSectionId(sectionId);
  }

  public Long getAssessmentId() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getAssessmentId();
  }

  /**
   * Set sectionId for SectionFacade
   * @param sectionId
   */
  public void setAssessmentId(Long assessmentId) {
      this.assessmentId = assessmentId;
    this.data.setAssessmentId(assessmentId);
  }


  // expect a return of AssessmentFacade from this method
  public AssessmentIfc getAssessment() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return new AssessmentFacade(this.data.getAssessment());
  }

  // section is AssessmentFacade not AssessmentData
  public void setAssessment(AssessmentIfc assessment) {
    this.assessment = (AssessmentFacade)assessment;
    AssessmentData d = (AssessmentData) this.assessment.getData();
    this.data.setAssessment(d);
  }

  public Integer getDuration() throws DataFacadeException {
  try {
    this.data = (SectionDataIfc) section.getData();
  }
  catch (AssessmentException ex) {
    throw new DataFacadeException(ex.getMessage());
  }
   return this.data.getDuration();
  }

  /**
   * Set duration for SectionFacade
   * @param duration
   */
  public void setDuration(Integer duration) {
    this.duration = duration;
    this.data.setDuration(duration);
  }

  public Integer getSequence() throws DataFacadeException {
  try {
    this.data = (SectionDataIfc) section.getData();
  }
  catch (AssessmentException ex) {
    throw new DataFacadeException(ex.getMessage());
  }
   return this.data.getSequence();
  }

  public void setSequence(Integer sequence) {
    this.sequence = sequence;
    this.data.setSequence(sequence);
  }

  public String getTitle() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getTitle();
  }

  /**
   * Set instruction for SectionFacade
   * e.g. "Match the following sentences", "In the score between 1-5, specify
   * your preference"
   * @param instruction
   */
  public void setTitle(String title) {
    this.title = title;
    this.data.setTitle(title);
  }

  public String getDescription() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getDescription();
  }

  /**
   * Set description for SectionFacade
   * @param description
   */
  public void setDescription(String description) {
      this.description = description;
    this.data.setDescription(description);
  }

  public Long getTypeId() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getTypeId();
  }

  /**
   * Set TypeId for SectionType. This property is used to indicate question type.
   * e.g. 1 = Multiple Choice, 2 = Multiple Correct. Please check out
   * ddl/02_TypeData.sql and table "type".
   * @param typeId
   */
  public void setTypeId(Long typeId) {
    this.typeId = typeId;
    this.data.setTypeId(typeId);
  }

  /**
   * Get status of SectionFacade. 1 = active, 0 = inactive
   * @return
   * @throws DataFacadeException
   */
  public Integer getStatus() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getStatus();
  }

  /**
   * Set status for SectionFacade. 1 = active, 0 = inactive
   * @param status
   */
  public void setStatus(Integer status) {
    this.status = status;
    this.data.setStatus(status);
  }

  /**
   * Get createdBy for SectionFacade. This represents the agentId of the person
   * who created the record
   * @return
   * @throws DataFacadeException
   */
  public String getCreatedBy() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getCreatedBy();
  }

  /**
   * Set createdBy for SectionFacade. This represents the agentId of the person
   * who created the record
   * @param createdBy
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    this.data.setCreatedBy(createdBy);
  }


  /**
   * Get the creation date of SectionFacade.
   * @return
   * @throws DataFacadeException
   */
  public Date getCreatedDate() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getCreatedDate();
  }

  /**
   * Set the creation date of SectionFacade
   * @param createdDate
   */
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
    this.data.setCreatedDate(createdDate);
  }

  /**
   * Get the agentId of the person who last modified SectionFacade
   * @return
   * @throws DataFacadeException
   */
  public String getLastModifiedBy() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getLastModifiedBy();
  }

  /**
   * set the agentId of the person who last modified sectionFacade
   * @param lastModifiedBy
   */
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
    this.data.setLastModifiedBy(lastModifiedBy);
  }

  /**
   * Get the date when SectionFacade where last modified By
   * @return
   * @throws DataFacadeException
   */
  public Date getLastModifiedDate() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getLastModifiedDate();
  }

  /**
   * Set the last modified date
   * @param lastModifiedBy
   */
  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    this.data.setLastModifiedDate(lastModifiedDate);
  }

  /**
   * Get section text set (question text set) from SectionFacade.data
   * @return
   * @throws DataFacadeException
   */
  public Set getItemFacadeSet() throws DataFacadeException {
    this.itemFacadeSet = new HashSet();
    try {
      this.data = (SectionDataIfc) section.getData();
      Set set = this.data.getItemSet();
      Iterator iter = set.iterator();
      while (iter.hasNext()){
        ItemFacade itemFacade = new ItemFacade((ItemDataIfc)iter.next());
        this.itemFacadeSet.add(itemFacade);
      }
      //this.sectionSet = data.getSectionSet();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.itemFacadeSet;
  }

  public Set getItemSet() throws DataFacadeException {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return this.data.getItemSet();
  }

  /**
   * Set section text (question text) in SectionFacade.data
   * @param sectionTextSet
   */
  public void setItemSet(Set itemSet) {
    this.itemSet = itemSet;
    this.data.setItemSet(itemSet);
  }

  public void addItem(ItemFacade itemFacade) {
    addItem(itemFacade.getData());
  }

  public void addItem(ItemDataIfc itemDataIfc) {
    if (this.itemSet == null) {
      setItemSet(new HashSet());
    }
    this.data.getItemSet().add(itemDataIfc);
    this.itemSet = this.data.getItemSet();
  }

  public TypeIfc getType() {
    return getSectionTypeFacade();
  }

  public TypeFacade getSectionTypeFacade() {
    try {
      this.data = (SectionDataIfc) section.getData();
    }
    catch (AssessmentException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    return typeFacadeQueries.getTypeFacadeById(this.data.getTypeId());
  }

  public ArrayList getItemArray() {
    ArrayList list = new ArrayList();
    Iterator iter = itemSet.iterator();
    while (iter.hasNext()){
      list.add(iter.next());
    }
    return list;
  }

  public ArrayList getItemArraySorted() {
    ArrayList list = getItemArray();
    Collections.sort(list);
    return list;
  }

  public int compareTo(Object o) {
      SectionFacade a = (SectionFacade)o;
      return sequence.compareTo(a.sequence);
  }

}
