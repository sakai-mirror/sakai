package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourseStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseSet;

public class EquivalentCoursesImpl implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
	private int hashValue = 0;
	
	private Long equivalentId;
	private String title;
	private String uuid;
	private Set canonicalCourseUuidSet;
	
	/** The value of the simple createdby property. */
	private String createdBy;
	
	/** The value of the simple createddate property. */
	private Date createdDate;
	
	/** The value of the simple lastmodifiedby property. */
	private java.lang.String lastModifiedBy;
	
	/** The value of the simple lastmodifieddate property. */
	private java.util.Date lastModifiedDate;

	public EquivalentCoursesImpl() {}
	
	public EquivalentCoursesImpl(String title, String uuid, Set set){
		this.setTitle(title);
		this.setUuid(uuid);
		this.setCanonicalCourseSet(set);
	}

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return java.lang.Long
	 */
	public Long getEquivalentId()
	{
		return this.equivalentId;
	}
	
	/**
	 * Set the simple primary key value that identifies this object.
	 * @param coursesetid
	 */
	public void setEquivalentId(Long equivalentId)
	{
		this.equivalentId = equivalentId;
	}
		
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Set getCanonicalCourseSet() {
		return canonicalCourseUuidSet;
	}
	
	public void setCanonicalCourseSet(Set set) {
		this.canonicalCourseUuidSet = set;
	}
	
	public void addCanonicalCourse(String canonicalCourseUuid) {
		if (canonicalCourseUuidSet == null)
			canonicalCourseUuidSet = new HashSet();
		canonicalCourseUuidSet.add(canonicalCourseUuid);
	}
	
	public void removeCanonicalCourse(String canonicalCourseUuid) {
		if (canonicalCourseUuidSet != null)
			canonicalCourseUuidSet.remove(canonicalCourseUuid);
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
		if (! (rhs instanceof EquivalentCoursesImpl))
			return false;
		EquivalentCoursesImpl that = (EquivalentCoursesImpl) rhs;
		if (this.getEquivalentId() == null || that.getEquivalentId() == null)
			return false;
		return (this.getEquivalentId().equals(that.getEquivalentId()));
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
			int equivalentIdValue = this.getEquivalentId() == null ? 0 : this.getEquivalentId().hashCode();
			result = result * 37 + equivalentIdValue;
			this.hashValue = result;
		}
		return this.hashValue;
	}
	
}
