package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
import org.sakaiproject.api.edu.coursemanagement.CanonicalCourseStatusType;

public class CanonicalCourseImpl implements CanonicalCourse, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
    private int hashValue = 0;

    /** The composite primary key value. */
    private Long canonicalCourseId;

    /** The value of the CanonicalCourseStatusType association. */
    private CanonicalCourseStatusType canonicalCourseStatus;

    /** The value of the equivalentCoursesSet one-to-many association. */
    private Set equivalentCourseSet;
    
    private List equivalentCourseList;

    /** The value of the preRequisites. */
    private String prerequisiteString;
    
    /** The list of the preRequisites. */
    private List prerequisiteList;

    /** The value of the simple title property. */
    private String title;

    /** The value of the simple description property. */
    private String description;

    /** The value of the simple courseNumber property. */
    private String courseNumber;

    /** The value of the simple uuid property. */
    private String uuid;

    /** The value of the simple defaultCredits property. */
    private String defaultCredits;

    /** The value of the simple parentId property. */
    private String parentId;
  
	private String topicsString;

	private List topicList;

    /** The value of the simple createdby property. */
    private String createdBy;

    /** The value of the simple createddate property. */
    private Date createdDate;

    /** The value of the simple lastmodifiedby property. */
    private String lastModifiedBy;

    /** The value of the simple lastmodifieddate property. */
    private Date lastModifiedDate;

	public Long getCanonicalCourseId() {
		return this.canonicalCourseId;
	}

	public void setCanonicalCourseId(Long canonicalCourseId) {
		this.canonicalCourseId = canonicalCourseId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public CanonicalCourseStatusType getCanonicalStatusType() {
		return canonicalCourseStatus;
	}

	public void setCanonicalStatusType(CanonicalCourseStatusType status) {
		this.canonicalCourseStatus = status;
	}

	public String getDefaultCredits() {
		return this.defaultCredits;
	}

	public void setDefaultCredits(String defaultCredits) {
		this.defaultCredits = defaultCredits;
	}

    /**
     * Return the value of the TOPICS column.
     * @return String
     */
    public String getTopicsString()
    {
        return this.topicsString;
    }

    /**
     * Set the value of the TOPICS column.
     * @param topics
     */
    public void setTopicsString(String topicsString)
    {
        this.topicsString = topicsString;
        String[] topicsArray = topicsString.split("|");
        for (int i=0;i<topicsArray.length;i++){
        	String t = topicsArray[i];
        	addTopic(t);
        }
    }
	
	public List getTopics() {
		return topicList;
	}

	public void addTopic(String topic) {
		if (topicList == null)
			topicList = new ArrayList();
		topicList.add(topic);
	}

	public void removeTopic(String topic) {
		for (int i=0;i<topicList.size();i++){
			String t = (String) topicList.get(i);
			if (t.equals(topic)){
				topicList.remove(i);
				break;
			}
		}
	}

	public Set getEquivalentsSet() {
		return equivalentCourseSet;
	}

	public void setEquivalentsSet(Set equivalentCourseSet){
		this.equivalentCourseSet = equivalentCourseSet;
	}
	
	public List getEquivalents() {
		return equivalentCourseList;
	}

	public void addEquivalent(String canonicalCourseUuid) {
		if (equivalentCourseList == null)
			equivalentCourseList = new ArrayList();
		equivalentCourseList.add(canonicalCourseUuid);
	}

	public void removeEquivalent(String canonicalCourseUuid) {
		// TODO Auto-generated method stub

	}

	   /**
     * Return the value of the TOPICS column.
     * @return String
     */
    public String getPrerequisiteString()
    {
        return this.prerequisiteString;
    }

    /**
     * Set the value of the TOPICS column.
     * @param topics
     */
    public void setPrerequisiteString(String prerequisiteString)
    {
        this.prerequisiteString = prerequisiteString;
        String[] pArray = prerequisiteString.split("|");
        for (int i=0;i<pArray.length;i++){
        	String p = pArray[i];
        	addPrerequisite(p);
        }
    }

	public List getPrerequisites() {
		// TODO Auto-generated method stub
		return prerequisiteList;
	}

	public void addPrerequisite(String prerequisite) {
		if (prerequisiteList == null)
			prerequisiteList = new ArrayList();
		prerequisiteList.add(prerequisite);
	}

	public void removePrerequisite(String prerequisite) {
		for (int i=0;i<prerequisiteList.size();i++){
			String t = (String) prerequisiteList.get(i);
			if (t.equals(prerequisite)){
				prerequisiteList.remove(i);
				break;
			}
		}
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentUuid) {
		this.parentId = parentUuid;
	}

	public List getOfferings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addOffering(String offeringUuid) {
		// TODO Auto-generated method stub

	}

	public void removeOffering(String offeringUuid) {
		// TODO Auto-generated method stub

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
        if (! (rhs instanceof CanonicalCourse))
            return false;
        CanonicalCourse that = (CanonicalCourse) rhs;
        if (this.getCanonicalCourseId() == null || that.getCanonicalCourseId() == null)
            return false;
        return (this.getCanonicalCourseId().equals(that.getCanonicalCourseId()));
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
            int canonicalcourseidValue = this.getCanonicalCourseId() == null ? 0 : this.getCanonicalCourseId().hashCode();
            result = result * 37 + canonicalcourseidValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }

}
