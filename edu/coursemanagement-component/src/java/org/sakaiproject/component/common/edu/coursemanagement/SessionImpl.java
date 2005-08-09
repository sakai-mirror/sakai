package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.api.edu.coursemanagement.Session;
import org.sakaiproject.api.edu.coursemanagement.SessionType;

public class SessionImpl implements Session, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
	private int hashValue = 0;
	
	/** The composite primary key value. */
	private java.lang.Long sessionId;
	
	private String title;
	private String abbreviation;
	private String year;
	private Boolean isCurrent;
	private String uuid;
	private SessionType sessionType;
	private String sessionTypeUuid;
	private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	
	public SessionImpl() {}
	
	public SessionImpl(String title, String abbreviation,
			String year, SessionType type, String uuid,
			Boolean isCurrent){
		this.setTitle(title);
		this.setAbbreviation(abbreviation);
		this.setYear(year);
		if (sessionType != null)
			this.setSessionTypeUuid(sessionType.getUuid());
		this.setUuid(uuid);
		this.setIsCurrent(isCurrent);
	}
	
	/**
	 * Return the simple primary key value that identifies this object.
	 * @return java.lang.Long
	 */
	public Long getSessionId()
	{
		return sessionId;
	}
	
	/**
	 * Set the simple primary key value that identifies this object.
	 * @param sessionId
	 */
	public void setSessionId(Long sessionId)
	{
		this.hashValue = 0;
		this.sessionId = sessionId;
	}
	
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public boolean isCurrent() {
		return getIsCurrent().booleanValue();
	}
	
	public void setIsCurrent(boolean isCurrent) {
		this.isCurrent = new Boolean(isCurrent);
	}
	
	public Boolean getIsCurrent() {
		return isCurrent;
	}
	public void setIsCurrent(Boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public SessionType getSessionType() {
		return sessionType;
	}
	
	public void setSessionType(SessionType type) {
		this.sessionType = type;
		this.sessionTypeUuid = type.getUuid(); 
	}
	
	public String getSessionTypeUuid() {
		return sessionTypeUuid;
	}
	
	public void setSessionTypeUuid(String sessionTypeUuid) {
		this.sessionTypeUuid = sessionTypeUuid;
		// need to set sessiontype
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
		if (! (rhs instanceof SessionImpl))
			return false;
		SessionImpl that = (SessionImpl) rhs;
		if (this.getSessionId() == null || that.getSessionId() == null)
			return false;
		return (this.getSessionId().equals(that.getSessionId()));
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
			int sessionIdValue = this.getSessionId() == null ? 0 : this.getSessionId().hashCode();
			result = result * 37 + sessionIdValue;
			this.hashValue = result;
		}
		return this.hashValue;
	}
	
}
