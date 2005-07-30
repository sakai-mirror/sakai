package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CourseOffering;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingType;
import org.sakaiproject.api.edu.coursemanagement.CourseSection;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentType;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;

public class CourseOfferingImpl implements CourseOffering, Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
    private int hashValue = 0;

    /** The composite primary key value. */
    private java.lang.Long courseOfferingId;

    /** The value of the cmCourseofferingstatustypeT association. */
    private CourseOfferingStatusType courseOfferingStatusType;

    /** The value of the cmCourseofferingtypeT association. */
    private CourseOfferingType courseOfferingType;

    /** The value of the cmEnrollmenttypeT association. */
    private EnrollmentType enrollmentType;

    /** The value of the cmSessionT association. */
    private Session session;

    /** The value of the simple title property. */
    private String title;

    /** The value of the simple description property. */
    private String description;

    /** The value of the simple offeringnumber property. */
    private String offeringNumber;

    /** The value of the simple uuid property. */
    private String uuid;

    /** The value of the simple iscrosslisted property. */
    private Boolean isCrossListed;

    /** The value of the simple maximumstudents property. */
    private Long maximumStudents;

    /** The value of the simple canonicalcourseuuid property. */
    private String canonicalCourseUuid;

    /** The value of the simple createdby property. */
    private String createdBy;

    /** The value of the simple createddate property. */
    private Date createdDate;

    /** The value of the simple lastmodifiedby property. */
    private java.lang.String lastModifiedBy;

    /** The value of the simple lastmodifieddate property. */
    private java.util.Date lastModifiedDate;

	private Set courseSectionSet;

	private List courseSectionList;

	private Set participationSet;

	private List participationList;
	
	private List defaultLeaderList;
	
	private List otherPeopleList;

	private List enrollmentList;

	private Set enrollmentSet;

	public Long getCourseOfferingId() {
		return this.courseOfferingId;
	}

	public void setCourseOfferingId(Long courseOfferingId) {
		this.courseOfferingId = courseOfferingId;
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

	public String getOfferingNumber() {
		return offeringNumber;
	}

	public void setOfferingNumber(String offeringNumber) {
		this.offeringNumber = offeringNumber;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean isCrossListed() {
		return getIsCrossListed().booleanValue();
	}
	
	public void setIsCrossListed(boolean isCrossListed) {
		this.isCrossListed = new Boolean(isCrossListed);
	}

	public Boolean getIsCrossListed() {
		return isCrossListed;
	}
	public void setIsCrossListed(Boolean isCrossListed) {
		this.isCrossListed = isCrossListed;
	}

	public List getEquivalents() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addEquivalent(String courseOfferingUuid) {
		// TODO Auto-generated method stub

	}

	public void removeEquivalent(String courseOfferingUuid) {
		// TODO Auto-generated method stub

	}

	public int getMaximumStudents() {
		return getMaximumStudentsLong().intValue();
	}

	public void setMaximumStudents(int maxStudents) {
		setMaximumStudentsLong(new Long(maxStudents));
	}
	
	public Long getMaximumStudentsLong() {
		return maximumStudents;
	}

	public void setMaximumStudentsLong(Long maxStudents) {
		this.maximumStudents = maxStudents;
	}

	public CourseOfferingType getOfferingType() {
		return courseOfferingType;
	}

	public void setOfferingType(CourseOfferingType type) {
		this.courseOfferingType = type;
	}

	public CourseOfferingStatusType getOfferingStatus() {
		return courseOfferingStatusType;
	}

	public void setOfferingStatusType(CourseOfferingStatusType status) {
		this.courseOfferingStatusType = status;
	}

	public Set getCourseSectionSet() {
		return courseSectionSet;
	}

	public void setCourseSectionSet(Set courseSectionSet) {
		courseSectionList = new ArrayList();
		if (this.courseSectionSet == null)
			this.courseSectionSet = new HashSet();
		this.courseSectionSet = courseSectionSet;
		Iterator i= courseSectionSet.iterator();
		while (i.hasNext()){
			courseSectionList.add(i.next());
		}
	}

	public List getCourseSections() {
		return courseSectionList;
	}

	public void addCourseSection(String sectionUuid) {
		if (this.courseSectionSet == null)
			this.courseSectionSet = new HashSet();
		courseSectionSet.add(sectionUuid);
		if (this.courseSectionList == null)
			this.courseSectionList = new ArrayList();
		courseSectionList.add(sectionUuid);
	}

	public void removeCourseSection(String sectionUuid) {
		Iterator i= courseSectionSet.iterator();
		while (i.hasNext()){
			String s = (String) i.next();
			if (s.equals(sectionUuid)){
				courseSectionSet.remove(sectionUuid);
				courseSectionList.remove(sectionUuid);				
			}
		}
	}

	public Set getParticipationSet() {
		return participationSet;
	}

	public void setParticipationSet(Set participationSet) {
		participationList = new ArrayList();
		defaultLeaderList = new ArrayList();
		otherPeopleList = new ArrayList();
		if (this.participationSet == null)
			this.participationSet = new HashSet();
		this.participationSet = participationSet;
		Iterator i= participationSet.iterator();
		while (i.hasNext()){
			ParticipationRecord p = (ParticipationRecord)i.next();
			participationList.add(p);
			if (p.getRole().equals(CourseOffering.LEADER))
				defaultLeaderList.add(p);
			else
				otherPeopleList.add(p);
		}
	}

	public Set getParticipationList() {
		return participationSet;
	}

	public List getDefaultLeaders() {
		return defaultLeaderList;
	}

	public void addDefaultLeader(ParticipationRecord participationRecord) {
		if (this.participationSet == null)
			this.participationSet = new HashSet();
		participationSet.add(participationRecord);
		if (this.participationList == null)
			this.participationList = new ArrayList();
		participationList.add(participationRecord);
		if (this.participationList == null)
			this.participationList = new ArrayList();
		defaultLeaderList.add(participationRecord);
	}

	public void removeDefaultLeader(String agentUuid) {
		Iterator i= participationSet.iterator();
		while (i.hasNext()){
			ParticipationRecord participation = (ParticipationRecord) i.next();
			if (participation.getAgent().equals(agentUuid)){
				participationSet.remove(participation);
				participationList.remove(participation);				
				defaultLeaderList.remove(participation);				
			}
		}
	}

	public Set getEnrollmentSet() {
		return participationSet;
	}

	public void setEnrollmentSet(Set enrollmentSet) {
		enrollmentList = new ArrayList();
		if (this.enrollmentSet == null)
			this.enrollmentSet = new HashSet();
		this.enrollmentSet = enrollmentSet;
		Iterator i= enrollmentSet.iterator();
		while (i.hasNext()){
			enrollmentList.add(i.next());
		}
	}
	public List getEnrollmentRecords() {
		return enrollmentList;
	}

	public void addEnrollmentRecord(EnrollmentRecord enrollmentRecord) {
		if (this.enrollmentSet == null)
			this.enrollmentSet = new HashSet();
		enrollmentSet.add(enrollmentRecord);
		if (this.enrollmentList == null)
			this.enrollmentList = new ArrayList();
		enrollmentList.add(enrollmentRecord);
	}

	public void removeEnrollmentRecord(String agentUuid) {
		Iterator i= enrollmentSet.iterator();
		while (i.hasNext()){
			EnrollmentRecord enrollment = (EnrollmentRecord) i.next();
			if (enrollment.getAgent().equals(agentUuid)){
				enrollmentSet.remove(enrollment);
				enrollmentList.remove(enrollment);				
			}
		}
	}
	
	public void removeEnrollmentRecord(EnrollmentRecord enrollmentRecord) {
		Iterator i= enrollmentSet.iterator();
		while (i.hasNext()){
			EnrollmentRecord enrollment = (EnrollmentRecord) i.next();
			if (enrollment.equals(enrollmentRecord)){
				enrollmentSet.remove(enrollment);
				enrollmentList.remove(enrollment);				
			}
		}
	}

	public EnrollmentType getEnrollmentType() {
		return enrollmentType;
	}

	public List getOtherPeople() {
		return otherPeopleList;
	}

	public void addOtherPerson(ParticipationRecord participant) {
		if (this.participationSet == null)
			this.participationSet = new HashSet();
		participationSet.add(participant);
		if (this.participationList == null)
			this.participationList = new ArrayList();
		participationList.add(participant);
		if (this.participationList == null)
			this.participationList = new ArrayList();
		otherPeopleList.add(participant);
	}

	public void removeOtherPerson(String agentUuid) {
		Iterator i= participationSet.iterator();
		while (i.hasNext()){
			ParticipationRecord participation = (ParticipationRecord) i.next();
			if (participation.getAgent().equals(agentUuid)){
				participationSet.remove(participation);
				participationList.remove(participation);				
				otherPeopleList.remove(participation);				
			}
		}
	}

	public String getCanonicalCourse() {
		return canonicalCourseUuid;
	}

	public void setCanonicalCourse(String canonicalCourseUuid) {
		this.canonicalCourseUuid = canonicalCourseUuid;
	}

	public List getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSection(String sectionUuid) {
		// TODO Auto-generated method stub

	}

	public void removeSection(String sectionUuid) {
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
        if (! (rhs instanceof CourseOffering))
            return false;
        CourseOffering that = (CourseOffering) rhs;
        if (this.getCourseOfferingId() == null || that.getCourseOfferingId() == null)
            return false;
        return (this.getCourseOfferingId().equals(that.getCourseOfferingId()));
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
            int CourseOfferingIdValue = this.getCourseOfferingId() == null ? 0 : this.getCourseOfferingId().hashCode();
            result = result * 37 + CourseOfferingIdValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }

}
