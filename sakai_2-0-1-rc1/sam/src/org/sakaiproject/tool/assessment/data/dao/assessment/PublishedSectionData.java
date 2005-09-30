/**********************************************************************************
* $HeadURL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.data.dao.assessment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Category;
import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueriesAPI;
import org.sakaiproject.tool.assessment.services.PersistenceService;

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
  private Set sectionMetaDataSet;
  private HashMap sectionMetaDataMap;

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

  public Set getSectionMetaDataSet() {
    return sectionMetaDataSet;
  }

  public void setSectionMetaDataSet(Set param) {
    this.sectionMetaDataSet= param;
    this.sectionMetaDataMap = getSectionMetaDataMap(sectionMetaDataSet);

  }

  public HashMap getSectionMetaDataMap(Set metaDataSet) {
    HashMap metaDataMap = new HashMap();
    if (metaDataSet != null){
      for (Iterator i = metaDataSet.iterator(); i.hasNext(); ) {
        PublishedSectionMetaData metaData = (PublishedSectionMetaData) i.next();
        metaDataMap.put(metaData.getLabel(), metaData.getEntry());
      }
    }
    return metaDataMap;
  }


  public void addSectionMetaData(String label, String entry) {
    if (this.sectionMetaDataSet== null) {
      setSectionMetaDataSet(new HashSet());
      this.sectionMetaDataMap= new HashMap();
    }
    this.sectionMetaDataMap.put(label, entry);
    this.sectionMetaDataSet.add(new SectionMetaData(this, label, entry));
  }

  public ArrayList getItemArray() {
    ArrayList list = new ArrayList();
    Iterator iter = itemSet.iterator();
    while (iter.hasNext()){
      list.add(iter.next());
    }
    return list;
  }

  public String getSectionMetaDataByLabel(String label) {
    return (String)this.sectionMetaDataMap.get(label);
  }

  public ArrayList getItemArraySortedForGrading() {
  // this returns all items, used for grading
    ArrayList list = getItemArray();
    Collections.sort(list);
    return list;
  }

  public ArrayList getItemArraySorted() {
    long seed = (long) AgentFacade.getAgentString().hashCode();
    return getItemArraySortedWithRandom(seed);
  }

  public ArrayList getItemArraySortedWithRandom(long seed) {

    ArrayList list = getItemArray();
    Integer numberToBeDrawn= null;

    if ((getSectionMetaDataByLabel(SectionDataIfc.AUTHOR_TYPE)!=null) && (getSectionMetaDataByLabel(SectionDataIfc.AUTHOR_TYPE).equals(SectionDataIfc.RANDOM_DRAW_FROM_QUESTIONPOOL.toString()))) {

      // same ordering for each student
      ArrayList randomsample = new ArrayList();
      //long seed = (long) AgentFacade.getAgentString().hashCode();
      Collections.shuffle(list,  new Random(seed));

      if (getSectionMetaDataByLabel(SectionDataIfc.NUM_QUESTIONS_DRAWN) !=null ) {
        numberToBeDrawn= new Integer(getSectionMetaDataByLabel(SectionDataIfc.NUM_QUESTIONS_DRAWN));
      }

      int samplesize = numberToBeDrawn.intValue();
      for (int i=0; i<samplesize; i++){
        randomsample.add(list.get(i));
      }
      return randomsample;

    }
    else if((getSectionMetaDataByLabel(SectionDataIfc.QUESTIONS_ORDERING)!=null ) && (getSectionMetaDataByLabel(SectionDataIfc.QUESTIONS_ORDERING).equals(SectionDataIfc.RANDOM_WITHIN_PART.toString())) ){
         // same ordering for each student
    //long seed = (long) AgentFacade.getAgentString().hashCode();
    Collections.shuffle(list,  new Random(seed));
    return list;

    }
    else {
    Collections.sort(list);
    return list;
    }
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
    TypeFacadeQueriesAPI typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
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
