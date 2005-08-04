package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.Date;
import org.sakaiproject.api.edu.coursemanagement.ParticipationStatusType;

public class ParticipationStatusTypeImpl implements ParticipationStatusType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
    private int hashValue = 0;

    /** The composite primary key value. */
    private Long participationStatusTypeId;

    /** The value of the simple authority property. */
    private String authority;

    /** The value of the simple domain property. */
    private String domain;

    /** The value of the simple keyword property. */
    private String keyword;

    /** The value of the simple displayname property. */
    private String displayName;

    /** The value of the simple description property. */
    private String description;

    /** The value of the simple uuid property. */
    private String uuid;

    /** The value of the simple status property. */
    private Integer status;

    /** The value of the simple lastmodifiedby property. */
    private String lastModifiedBy;

    /** The value of the simple lastmodifieddate property. */
    private Date lastModifiedDate;

    /** The value of the simple createdby property. */
    private String createdBy;

    /** The value of the simple createddate property. */
    private Date createdDate;

    public Long getParticipationStatusTypeId() {
    	return this.participationStatusTypeId;
	}

	public void setParticipationStatusTypeId(Long participationStatusTypeId) {
		this.participationStatusTypeId = participationStatusTypeId;
	}
    
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;

	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
        if (! (rhs instanceof ParticipationStatusTypeImpl))
            return false;
        ParticipationStatusTypeImpl that = (ParticipationStatusTypeImpl) rhs;
        if (this.getParticipationStatusTypeId() == null || that.getParticipationStatusTypeId() == null)
            return false;
        return (this.getParticipationStatusTypeId().equals(that.getParticipationStatusTypeId()));
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
            int participationStatusTypeIdValue = this.getParticipationStatusTypeId() == null ? 0 : this.getParticipationStatusTypeId().hashCode();
            result = result * 37 + participationStatusTypeIdValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }

}
