package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentStatusType;

public class EnrollmentRecordImpl
    implements EnrollmentRecord, Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
  private int hashValue = 0;

  /** The composite primary key value. */
  private Long enrollmentRecordId;

  private String agentUuid;
  private String role;
  private EnrollmentStatusType status;
  private String credits;
  private String courseSectionUuid;
  private String courseOfferingUuid;
  private String uuid;
  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;

  public EnrollmentRecordImpl() {
	}

	public EnrollmentRecordImpl(String agentUuid, String role,
			EnrollmentStatusType status, String courseSectionUuid) {
		this.agentUuid = agentUuid;
		this.role = role;
		this.status = status;
		this.courseSectionUuid = courseSectionUuid;
	}

  /**
	 * Return the simple primary key value that identifies this object.
	 * 
	 * @return java.lang.Long
	 */
  public Long getEnrollmentRecordId() {
    return enrollmentRecordId;
  }

  /**
   * Set the simple primary key value that identifies this object.
   * @param enrollmentrecordid
   */
  public void setEnrollmentRecordId(Long enrollmentRecordId) {
    this.hashValue = 0;
    this.enrollmentRecordId = enrollmentRecordId;
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

  public EnrollmentStatusType getStatus() {
    return status;
  }

  public void setStatus(EnrollmentStatusType status) {
    this.status = status;
  }

  public String getCredits() {
    return credits;
  }

  public void setCredits(String credits) {
    this.credits = credits;
  }

  public String getCourseReference() {
    return courseSectionUuid;
  }

  public void setCourseReference(String courseSectionUuid) {
    this.courseSectionUuid = courseSectionUuid;
  }

  public String getCourseOfferingUuid() {
    return courseOfferingUuid;
  }

  public void setCourseOfferingUuid(String courseOfferingUuid) {
    this.courseOfferingUuid = courseOfferingUuid;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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
  public boolean equals(Object rhs) {
    if (rhs == null) {
      return false;
    }
    if (! (rhs instanceof EnrollmentRecordImpl)) {
      return false;
    }
    EnrollmentRecordImpl that = (EnrollmentRecordImpl) rhs;
    if (this.getEnrollmentRecordId() == null || that.getEnrollmentRecordId() == null) {
      return false;
    }
    return (this.getEnrollmentRecordId().equals(that.getEnrollmentRecordId()));
  }

  /**
   * Implementation of the hashCode method conforming to the Bloch pattern with
       * the exception of array properties (these are very unlikely primary key types).
   * @return int
   */
  public int hashCode() {
    if (this.hashValue == 0) {
      int result = 17;
      int enrollmentrecordidValue = this.getEnrollmentRecordId() == null ? 0 :
          this.getEnrollmentRecordId().hashCode();
      result = result * 37 + enrollmentrecordidValue;
      this.hashValue = result;
    }
    return this.hashValue;
  }

}
