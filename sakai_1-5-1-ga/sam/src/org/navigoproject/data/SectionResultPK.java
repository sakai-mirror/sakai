/*
 * Copyright 2002 by Leland Stanford Junior University Board of Trustees.
 * The code contained herein is subject to license terms available at 
 * http://aboutcoursework.stanford.edu/opensource/terms.html
 */
package org.navigoproject.data;

import java.io.Serializable;

/**
 * @author Ron Alcasid
 */

/**
 * @hibernate.class
 *            table="SECTION_RESULT"
 */
public class SectionResultPK implements Serializable{

  private String assessmentId, sectionId;
  
  public SectionResultPK() {}

  public SectionResultPK (String assessmentId, String sectionId){
    this.assessmentId = assessmentId;
    this.sectionId = sectionId;     
  }
    
  /**
   * @hibernate.property
   *            column="ASSESSMENT_ID"
   */
  public String getAssessmentId(){
    return this.assessmentId;
  }

  public void setAssessmentId(String assessmentId){
    this.assessmentId = assessmentId;
  }


  /**
   * @hibernate.property
   *            column="SECTION_ID"
   */
  public String getSectionId(){
    return this.sectionId;
  }

  public void setSectionId(String sectionId){
    this.sectionId = sectionId;
  }
  
  public boolean equals(Object sectionResultPK){
    boolean returnValue=false;

    if (this == sectionResultPK)
      returnValue=true;
    if (sectionResultPK != null && sectionResultPK.getClass()==this.getClass()){
      SectionResultPK s = (SectionResultPK)sectionResultPK;
      if (this.getAssessmentId() == s.getAssessmentId()
      && this.getSectionId() == s.getSectionId())
        returnValue= true;
    }
    return returnValue;
  }

  public int hashCode(){
    return (this.getAssessmentId() + this.getSectionId()).hashCode();
  }  
}
