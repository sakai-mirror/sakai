package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.ParticipationStatusType;

public class ParticipationRecordImpl implements ParticipationRecord, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
    private int hashValue = 0;

    /** The composite primary key value. */
    private Long participationRecordId;

	private String agentUuid;
	private String role;
	private ParticipationStatusType status;
	private String courseSectionUuid;
	private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;

	private String courseOfferingUuid;

	private Boolean isLeader;

	private Boolean isOtherPeople;

    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Long
     */
    public Long getParticipationRecordId()
    {
        return participationRecordId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param participationRecordId
     */
    public void setParticipationRecordId(Long participationRecordId)
    {
        this.hashValue = 0;
        this.participationRecordId = participationRecordId;
    }

	public String getAgent() {
		return agentUuid;
	}

	public void setAgent(String agentUuid) {
		this.agentUuid = agentUuid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public ParticipationStatusType getParticipationStatus() {
		return status;
	}

	public void setParticipationStatus(ParticipationStatusType status) {
		this.status = status;
	}

	public String getCourseReference() {
		return courseSectionUuid;
	}

	public void setCourseReference(String courseSectionUuid) {
		this.courseSectionUuid = courseSectionUuid;
	}

	public String getCourseOffering() {
		return this.courseOfferingUuid;
	}

	public void setCourseOffering(String courseOfferingUuid) {
		this.courseOfferingUuid = courseOfferingUuid;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	   /**
     * Implementation of the equals comparison on the basis of equality of the primary key values.
     * @param rhs
     * @return boolean
     */
    public boolean equals(Object rhs)
    {
        if (rhs == null)
            return false;
        if (! (rhs instanceof ParticipationRecordImpl))
            return false;
        ParticipationRecordImpl that = (ParticipationRecordImpl) rhs;
        if (this.getParticipationRecordId() == null || that.getParticipationRecordId() == null)
            return false;
        return (this.getParticipationRecordId().equals(that.getParticipationRecordId()));
    }

    /**
     * Implementation of the hashCode method conforming to the Bloch pattern with
     * the exception of array properties (these are very unlikely primary key types).
     * @return int
     */
    public int hashCode()
    {
        if (this.hashValue == 0)
        {
            int result = 17;
            int participationRecordIdValue = this.getParticipationRecordId() == null ? 0 : this.getParticipationRecordId().hashCode();
            result = result * 37 + participationRecordIdValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }

	public Boolean getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

	public Boolean getIsOtherPeople() {
		return isOtherPeople;
	}

	public void setIsOtherPeople(Boolean isOtherPeople) {
		this.isOtherPeople = isOtherPeople;
	}

	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

}
