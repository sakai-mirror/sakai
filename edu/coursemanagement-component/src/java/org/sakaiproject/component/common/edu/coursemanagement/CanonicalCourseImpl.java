package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
import org.sakaiproject.api.edu.coursemanagement.CanonicalCourseStatusType;

public class CanonicalCourseImpl implements CanonicalCourse, Serializable {

	private static final long serialVersionUID = 1L;
	
	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
	private int hashValue = 0;
	
	/** The composite primary key value. */
	private Long canonicalCourseId;
	
	/** The value of the CanonicalCourseStatusType association. */
	private CanonicalCourseStatusType canonicalCourseStatus;
	
	/** The value of the equivalentCoursesSet one-to-many association. */
	private Set equivalentCourseSet;
	
	/** The value of the preRequisites. */
	private String prerequisiteString;
	
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
	
	private Set prerequisiteSet;
	
	private Set courseOfferingSet;

	private String canonicalCourseStatusUuid;

	private EquivalentCoursesImpl equivalentCourses;
	
	public CanonicalCourseImpl() {}
	
	public CanonicalCourseImpl(String title, String description,
			String courseNumber, String uuid, CanonicalCourseStatusType status){
		this.setTitle(title);
		this.setDescription(description);
		this.setCourseNumber(courseNumber);
		this.setUuid(uuid);
		if (status != null)
			this.setCanonicalCourseStatus(status);
	}
	
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
	
	public CanonicalCourseStatusType getCanonicalCourseStatus() {
		return canonicalCourseStatus;
	}
	
	public void setCanonicalCourseStatus(CanonicalCourseStatusType status) {
		this.canonicalCourseStatus = status;
	}

	public String getCanonicalCourseStatusUuid() {
		return canonicalCourseStatus.getUuid();
	}
	
	public void setCanonicalCourseStatusUuid(String statusUuid) {
		this.canonicalCourseStatusUuid = statusUuid;
		CourseManagementManagerImpl manager = CourseManagementManagerImpl.getInstance();
		this.canonicalCourseStatus = manager.getCanonicalCourseStatusTypeByUuid(statusUuid);
	}

	
	public String getCanonicalCourseStatusUuId() {
		if (canonicalCourseStatus!=null)
		 return canonicalCourseStatus.getUuid();
		else
		 return null;
	}
	
	public void setCanonicalCourseStatusId(String statusUuid) {
		this.canonicalCourseStatusUuid = statusUuid;
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
		if (topicsString == null)
			topicsString = topic;
		else
			topicsString = topicsString + "|" + topic;
		if (topicList == null)
			topicList = new ArrayList();
		topicList.add(topic);
	}
	
	public void removeTopic(String topic) {
		topicsString = "";
		for (int i=0;i<topicList.size();i++){
			String t = (String) topicList.get(i);
			if (t.equals(topic))
				topicList.remove(i);
			else{
				if (("").equals(topicsString))
					topicsString = t;
				else
				  topicsString = topicsString + "|" + t;
			}
		}
	}
	
	public Set getEquivalents() {
		return equivalentCourses.getCanonicalCourseSet();
	}
	
	public EquivalentCoursesImpl getEquivalentCourses() {
		return equivalentCourses;
	}
	
	public void setEquivalentCourses(EquivalentCoursesImpl equivalentCourses){
		this.equivalentCourses = equivalentCourses;
	}
	
	public void addEquivalent(String canonicalCourseUuid) {
    HashSet set;
		if (equivalentCourses == null){
	    Date currentDate = new Date();
			equivalentCourses = new EquivalentCoursesImpl(
					"equivalent-title", "*uuid"+currentDate.getTime(),new HashSet());
		}
		equivalentCourses.addCanonicalCourse(uuid);
	}
	
	public void removeEquivalent(String canonicalCourseUuid) {
		Set set = equivalentCourses.getCanonicalCourseSet();
		Iterator i = set.iterator();
		while (i.hasNext()){
			String equivalentUuid = (String) i.next();
			if (equivalentUuid.equals(canonicalCourseUuid)){
				set.remove(canonicalCourseUuid);
				break;
			}
		}
		if ((set.size()==1)
				&& (((String)set.iterator().next()).equals(uuid))){
			removeEquivalent(uuid);
		}
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
	
	public Set getPrerequisites() {
		return prerequisiteSet;
	}
	
	public void addPrerequisite(String prerequisite) {
		if (prerequisiteString == null)
			prerequisiteString = prerequisite;
		else
			prerequisiteString = prerequisiteString + "|" + prerequisite;
		if (prerequisiteSet == null)
			prerequisiteSet = new HashSet();
		prerequisiteSet.add(prerequisite);
	}
	
	public void removePrerequisite(String prerequisite) {
		prerequisiteString ="";
		Iterator i = prerequisiteSet.iterator();
		while (i.hasNext()){
			String p = (String) i.next();
			if (p.equals(prerequisite))
				prerequisiteSet.remove(prerequisite);
			else{
				if (("").equals(prerequisiteString))
					prerequisiteString = p;
				else
					prerequisiteString = prerequisiteString + "|" + p;
			}
		}
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentUuid) {
		this.parentId = parentUuid;
	}
	
	public Set getOfferings() {
		return courseOfferingSet;
	}
	
	public void addOffering(String offeringUuid) {
		if (courseOfferingSet == null)
			courseOfferingSet = new HashSet();
		courseOfferingSet.add(offeringUuid);
	}
	
	public void removeOffering(String offeringUuid) {
		Iterator i = courseOfferingSet.iterator();
		while (i.hasNext()){
			String p = (String) i.next();
			if (p.equals(offeringUuid)){
				courseOfferingSet.remove(offeringUuid);
				break;
			}
		}
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
		if (! (rhs instanceof CanonicalCourseImpl))
			return false;
		CanonicalCourseImpl that = (CanonicalCourseImpl) rhs;
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
