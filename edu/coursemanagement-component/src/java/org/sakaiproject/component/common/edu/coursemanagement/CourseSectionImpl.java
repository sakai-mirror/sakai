package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CourseOffering;
import org.sakaiproject.api.edu.coursemanagement.CourseSection;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionType;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentStatusType;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;

public class CourseSectionImpl implements CourseSection, Serializable  {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	/** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
    private int hashValue = 0;
    
    /** The composite primary key value. */
    private Long courseSectionId;

	/** The value of the simple title property. */
    private String title;

    /** The value of the simple description property. */
    private String description;

    /** The value of the simple sectionnumber property. */
    private String sectionNumber;

    /** The value of the simple uuid property. */
    private String uuid;

    /** The value of the simple maximumstudents property. */
    private Long maximumStudents;
    
    /** The value of the cmSessionT association. */
    private Session session;

    /** The value of the simple createdby property. */
    private String createdBy;

    /** The value of the simple createddate property. */
    private Date createdDate;

    /** The value of the simple lastmodifiedby property. */
    private String lastModifiedBy;

    /** The value of the simple lastmodifieddate property. */
    private Date lastModifiedDate;

	private CourseSectionType courseSectionType;

	private CourseSectionStatusType courseSectionStatus;

	private Set participationSet;

	private List participationList;
	
	private List defaultLeaderList;
	
	private List otherPeopleList;

	private List enrollmentList;

	private Set enrollmentSet;

	private EnrollmentStatusType enrollmentStatus;

	private String courseOfferingUuid;

	private String scheduleUuid;

	private List sectionEventUuidList;
	
	private Set sectionEventUuidSet;

	private Boolean isCreatedLocally;

	private Boolean isHoldingSection;

	private String location;

	private String meetingTime;

	private Boolean isAllowSelfRegistration;

	public CourseSectionImpl(String uuid){
		
	}
	
	 /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Long
     */
    public Long getCourseSectionId()
    {
        return courseSectionId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param coursesectionid
     */
    public void setCourseSectionid(Long courseSectionId)
    {
        this.hashValue = 0;
        this.courseSectionId = courseSectionId;
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

	public String getSectionNumber() {
		return sectionNumber;
	}

	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public CourseSectionType getSectionType() {
		return courseSectionType;
	}

	public void setSectionType(CourseSectionType type) {
		this.courseSectionType = type;
	}

	public CourseSectionStatusType getSectionStatus() {
		return courseSectionStatus;
	}

	public void setSectionStatusType(CourseSectionStatusType status) {
		this.courseSectionStatus = status;
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

	public List getLeaders() {
		return defaultLeaderList;
	}

	public void addLeader(ParticipationRecord participationRecord) {
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

	public void removeLeader(String agentUuid) {
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
	
	public EnrollmentStatusType getEnrollmentStatus() {
		return this.enrollmentStatus;
	}

	public void setEnrollmentStatus(EnrollmentStatusType status) {
		this.enrollmentStatus = status;
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

	public String getCourseOffering() {
		return this.courseOfferingUuid;
	}

	public void setCourseOffering(String courseOfferingUuid) {
		this.courseOfferingUuid = courseOfferingUuid;
	}

	public String getSchedule() {
		return scheduleUuid;
	}

	public void setSchedule(String scheduleUuid) {
		this.scheduleUuid = scheduleUuid;
	}

	public List getSectionEvents() {
		return sectionEventUuidList;
	}
	public Set getSectionEventSet() {
		return sectionEventUuidSet;
	}
	
	public void setSectionEventSet(Set set) {
		this.sectionEventUuidSet = set;
		sectionEventUuidList = new ArrayList();
		Iterator i= set.iterator();
		while (i.hasNext()){
			String e = (String)i.next();
			sectionEventUuidList.add(e);
		}
	}

	public void addSectionEvent(String eventUuid) {
		if (sectionEventUuidList == null)
			sectionEventUuidList = new ArrayList();
		sectionEventUuidList.add(eventUuid);
	}

	public void removeSectionEvent(String eventUuid) {
		if (sectionEventUuidList != null)
    		sectionEventUuidList.remove(eventUuid);
	}

	public boolean getCreatedLocally() {
		return getIsCreatedLocally().booleanValue();
	}

	public void setCreatedLocally(boolean createdLocally) {
		this.isCreatedLocally = new Boolean(createdLocally);
	}

	public Boolean getIsCreatedLocally() {
		return isCreatedLocally;
	}

	public void setCreatedLocally(Boolean createdLocally) {
		this.isCreatedLocally = createdLocally;
	}

	public boolean getHoldingSection() {
		return getIsHoldingSection().booleanValue();
	}

	public void setHoldingSection(boolean holdingSection) {
		this.isHoldingSection = new Boolean(holdingSection);
	}

	public Boolean getIsHoldingSection() {
		return isHoldingSection;
	}

	public void setHoldingSection(Boolean holdingSection) {
		this.isHoldingSection = holdingSection;
	}
	
	public boolean getAllowSelfRegistration() {
		return getIsAllowSelfRegistration().booleanValue();
	}

	public void setAllowSelfRegistration(boolean allowSelfRegistration) {
		this.isAllowSelfRegistration = new Boolean(allowSelfRegistration);
	}

	public Boolean getIsAllowSelfRegistration() {
		return isAllowSelfRegistration;
	}

	public void setAllowSelfRegistration(Boolean allowSelfRegistration) {
		this.isAllowSelfRegistration = allowSelfRegistration;
	}

	public String getLocation() {
		// TODO Auto-generated method stub
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMeetingTime() {
		// TODO Auto-generated method stub
		return meetingTime;
	}

	public void setMeetingTime(String meetingTime) {
		this.meetingTime = meetingTime;
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
        if (! (rhs instanceof CourseSection))
            return false;
        CourseSection that = (CourseSection) rhs;
        if (this.getCourseSectionId() == null || that.getCourseSectionId() == null)
            return false;
        return (this.getCourseSectionId().equals(that.getCourseSectionId()));
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
            int coursesectionidValue = this.getCourseSectionId() == null ? 0 : this.getCourseSectionId().hashCode();
            result = result * 37 + coursesectionidValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }

}
