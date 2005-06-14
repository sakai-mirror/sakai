 
 /**
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: RealizationBean.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.util.Date;

/**
 * @hibernate.class
 *            table="QTI_ASSESSMENT_TAKEN"
 */
public class RealizationBean {
  
	private String assessmentTakenId, assessmentPublishedId, agentId, assessmentTitle;
	private Integer submitted, lateSubmission;  
	private Date submissionTime, beginTime, endTime;
	
  
	public RealizationBean(){		
	}

	public RealizationBean (String assessmentTakenId, String assessmentPublishedId,
	                        String agentId, Integer submitted, Integer lateSubmission,
	                        Date submissionTime, Date beginTime, Date endtime,
	                        String assessmentTitle){										
										                       	
		this.assessmentTakenId = assessmentTakenId;
		this.assessmentPublishedId = assessmentPublishedId;
		this.agentId = agentId;
		this.submitted = submitted;
		this.lateSubmission = lateSubmission;
		this.submissionTime = submissionTime;
		this.beginTime = beginTime;
		this.endTime = endtime;			
		this.assessmentTitle = assessmentTitle;
	}
	
	/**
	 * @hibernate.id
	 *            generator-class="assigned"
	 *            column="ASSESSMENT_TAKEN_ID"                
	 */	
	public String getAssessmentTakenId(){
		return this.assessmentTakenId;
	}

	public void setAssessmentTakenId(String assessmentTakenid){
		this.assessmentTakenId = assessmentTakenid;
	}
	
	/**
	 * @hibernate.property
	 *            column="ASSESSMENT_PUB_ID"
	 */
	public String getAssessmentPublishedId(){
		return this.assessmentPublishedId;
	}
  
	public void setAssessmentPublishedId(String assessmentPubId){
		this.assessmentPublishedId = assessmentPubId;
	}	
	
	/**
	 * @hibernate.property
	 *            column="AGENT_ID"
	 */
	public String getAgentId(){
		return this.agentId;
	}
  
	public void setAgentId(String agentId){
		this.agentId = agentId;
	}		
	
	/**
	 * @hibernate.property
	 *            column="SUBMITTED"
	 */
	public Integer getSubmitted(){
		return this.submitted;
	}
  
	public void setSubmitted(Integer submitted){
		this.submitted = submitted;
	}
	
	/**
	 * @hibernate.property
	 *            column="LATE_SUBMISSION"
	 */
	public Integer getLateSubmission(){
		if (this.lateSubmission == null){
			return new Integer(1);
		}
		else{
			return this.lateSubmission;
		}		
	}
  
	public void setLateSubmission(Integer lateSubmission){
		this.lateSubmission = lateSubmission;
	}	
	
		
	/**
	 * @hibernate.property
	 *            column="SUBMISSION_TIME"
	 */
	public Date getSubmissionTime(){
		return this.submissionTime;
	}
  
	public void setSubmissionTime(Date submissionTime){
		this.submissionTime = submissionTime;
	}	
	
	/**
	 * @hibernate.property
	 *            column="BEGIN_TIME"
	 */
	public Date getBeginTime(){
		return this.beginTime;
	}
  
	public void setBeginTime(Date beginTime){
		this.beginTime = beginTime;
	}	
	
	/**
	 * @hibernate.property
	 *            column="END_TIME"
	 */
	public Date getEndTime(){
		return this.endTime;
	}
  
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	
	/**
	 * @hibernate.property
	 *            column="ASSESSMENT_TITLE"
	 */
	public String getAssessmentTitle(){
		return this.assessmentTitle;
	}
  
	public void setAssessmentTitle(String assessmentTitle){
		this.assessmentTitle = assessmentTitle;
	}	
		
}
