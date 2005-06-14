package org.sakaiproject.tool.assessment.data.dao.assessment;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;

import org.apache.log4j.*;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PublishedSectionData
    implements java.io.Serializable, SectionDataIfc, Comparable{
  static Category errorLogger = Category.getInstance("errorLogger");

  private static final long serialVersionUID = 7526471155622776147L;
  public static Integer ACTIVE_STATUS = new Integer(1);
  public static Integer INACTIVE_STATUS = new Integer(0);
  public static Integer ANY_STATUS = new Integer(2);

  private Long id;
  private Long assessmentId;
  private AssessmentIfc assessment;
  private Integer duration;
  private Integer sequence;
  private String title;
  private String description;
  private Long typeId;
  private Integer status;
  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;
  private Set itemSet;

  public PublishedSectionData() {}

  public PublishedSectionData(Integer duration, Integer sequence,
                     String title, String description,
                     Long typeId, Integer status,
                     String createdBy, Date createdDate,
                     String lastModifiedBy, Date lastModifiedDate){
    this.duration = duration;
    this.sequence = sequence;
    this.title = title;
    this.description = description;
    this.typeId = typeId;
    this.status = status;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
  }

  public Long getSectionId() {
    return this.id;
  }

  public void setSectionId(Long id) {
    this.id = id;
  }

  public Long getAssessmentId() {
    return this.assessmentId;
  }

  public void setAssessmentId(Long assessmentId) {
    this.assessmentId = assessmentId;
  }

  public void setAssessment(AssessmentIfc assessment)
  {
    this.assessment = assessment;
  }

  public AssessmentIfc getAssessment()
  {
      return assessment;
  }

/**
  public AssessmentDataIfc getAssessment()
  {
      return (AssessmentDataIfc)assessment;
  }
*/
  public Integer getDuration() {
    return this.duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getSequence() {
    return this.sequence;
  }

  public void setSequence(Integer sequence) {
    this.sequence = sequence;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getTypeId() {
    return this.typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getLastModifiedDate() {
    return this.lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Set getItemSet() {
    return itemSet;
  }

  public void setItemSet(Set itemSet) {
    this.itemSet = itemSet;
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

  public void addItem(ItemDataIfc item) {
    if (itemSet == null)
      itemSet = new HashSet();
    itemSet.add((ItemData) item);
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
  }

  public TypeIfc getType() {
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    TypeIfc type = typeFacadeQueries.getTypeFacadeById(this.typeId);
    TypeD typeD = new TypeD(type.getAuthority(), type.getDomain(),
                    type.getKeyword(), type.getDescription());
    typeD.setTypeId(this.typeId);
    return typeD;
  }

  public int compareTo(Object o) {
      PublishedSectionData a = (PublishedSectionData)o;
      return sequence.compareTo(a.sequence);
  }

}
