package org.sakaiproject.tool.assessment.data.ifc.assessment;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

import org.apache.log4j.*;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public interface SectionDataIfc
    extends java.io.Serializable{

   Long getSectionId() ;

   void setSectionId(Long sectionId);

   Long getAssessmentId() ;

   void setAssessmentId(Long assessmentId);

   AssessmentIfc getAssessment();
   //AssessmentData getAssessment();

   void setAssessment(AssessmentIfc assessment);

   Integer getDuration();

   void setDuration(Integer duration);

   Integer getSequence();

   void setSequence(Integer sequence);

   String getTitle();

   void setTitle(String title);

   String getDescription();

   void setDescription(String description);

   Long getTypeId();

   void setTypeId(Long typeId);

   Integer getStatus();

   void setStatus(Integer status);

   String getCreatedBy();

   void setCreatedBy(String createdBy);

   Date getCreatedDate();

   void setCreatedDate(Date createdDate);

   String getLastModifiedBy();

   void setLastModifiedBy(String lastModifiedBy);

   Date getLastModifiedDate();

   void setLastModifiedDate(Date lastModifiedDate);

   Set getItemSet();

   void setItemSet(Set itemSet);

   void addItem(ItemDataIfc item);

   TypeIfc getType();

   ArrayList getItemArray();

   ArrayList getItemArraySorted();
}
