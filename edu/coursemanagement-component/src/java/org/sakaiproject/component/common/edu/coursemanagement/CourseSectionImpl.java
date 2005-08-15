package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CourseOfferingType;
import org.sakaiproject.api.edu.coursemanagement.CourseSection;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionType;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentRecord;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentStatusType;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;

public class CourseSectionImpl
    implements CourseSection, Serializable {

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
  private Integer maximumStudents;

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

  private Set enrollmentSet;

  private EnrollmentStatusType enrollmentStatus;

  private String courseOfferingUuid;

  private Long courseOfferingId;

  private String location;

  private String meetingTime;

  private Boolean isAllowSelfRegistration;

  private HashSet leaderSet;

  private HashSet otherPeopleSet;

  private Set allChildren;

  private Boolean holdingSection;

  private String schedule;

  private String parentUuid;

  private String sectionEventsString;

  private Set sectionEventSet;

  public CourseSectionImpl() {

  }

  public CourseSectionImpl(String title, String description,
      String sectionNumber, String courseOfferingUuid,
      SessionImpl session, CourseSectionType type){
    this.setTitle(title);
    this.setDescription(description);
    this.setSectionNumber(sectionNumber);
    this.setCourseOffering(courseOfferingUuid);
    this.setSession(session);
    this.setSectionType(type);
  }

  /**
   * Return the simple primary key value that identifies this object.
   * @return java.lang.Long
   */
  public Long getCourseSectionId() {
    return courseSectionId;
  }

  /**
   * Set the simple primary key value that identifies this object.
   * @param coursesectionid
   */
  public void setCourseSectionId(Long courseSectionId) {
    this.hashValue = 0;
    this.courseSectionId = courseSectionId;
  }

  /* ***************** Metadata Methods ***************** */
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

  public Integer getMaximumStudents() {
    return maximumStudents;
  }

  public void setMaximumStudents(Integer maxStudents) {
     this.maximumStudents = maxStudents;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  /**
   * Set the value of the SECTIONEVENTS column.
   * @param topics
   */
  public String getSectionEventsString() {
    return sectionEventsString;
  }

  public void setSectionEventsString(String sectionEventsString) {
    this.sectionEventsString = sectionEventsString;
    sectionEventSet = getSectionEvents();
  }

  public Set getSectionEvents() {
    sectionEventSet = new HashSet();
    String[] sectionEventsArray = sectionEventsString.split("|");
    for (int i = 0; i < sectionEventsArray.length; i++) {
      String t = sectionEventsArray[i];
      sectionEventSet.add(t);
    }
    return sectionEventSet;
  }

  public void addSectionEvent(String sectionEvent) {
    if (sectionEventsString == null) {
      sectionEventsString = sectionEvent;
    }
    else {
      sectionEventsString = sectionEventsString + "|" + sectionEvent;
    }
    if (sectionEventSet == null) {
      sectionEventSet = new HashSet();
    }
    sectionEventSet.add(sectionEvent);
  }

  public void removeSectionEvent(String sectionEvent) {
    sectionEventsString = "";
    Iterator i = sectionEventSet.iterator();
    while (i.hasNext()) {
      String t = (String) i.next();
      if (t.equals(sectionEvent)) {
        sectionEventSet.remove(t);
      }
      else {
        if ( ("").equals(sectionEventsString)) {
          sectionEventsString = t;
        }
        else {
          sectionEventsString = sectionEventsString + "|" + t;
        }
      }
    }
  }

  public String getLocation() {
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

  /* ***************** Typing Methods ***************** */
  public CourseSectionType getSectionType() {
    return courseSectionType;
  }

  public void setSectionType(CourseSectionType type) {
    this.courseSectionType = type;
  }

  public CourseSectionStatusType getSectionStatus() {
    return courseSectionStatus;
  }

  public void setSectionStatus(CourseSectionStatusType status) {
    this.courseSectionStatus = status;
  }

  public Boolean getHoldingSection() {
    return holdingSection;
  }

  public void setHoldingSection(Boolean holdingSection) {
    this.holdingSection = holdingSection;
  }

  /* ***************** Grouping Methods ***************** */
  public Set getParticipationSet() {
    return participationSet;
  }

  public void setParticipationSet(Set participationSet) {
    leaderSet = new HashSet();
    otherPeopleSet = new HashSet();
    this.participationSet = new HashSet();
    this.participationSet = participationSet;
    Iterator i = participationSet.iterator();
    while (i.hasNext()) {
      ParticipationRecord p = (ParticipationRecord) i.next();
      participationSet.add(p);
      if (p.getIsLeader().booleanValue()) {
        leaderSet.add(p);
      }
      if (p.getIsOtherPeople().booleanValue()) {
        otherPeopleSet.add(p);
      }
    }
  }

  public Set getLeaders() {
    return leaderSet;
  }

  public void addLeader(ParticipationRecord participationRecord) {
    if (this.participationSet == null) {
      this.participationSet = new HashSet();
    }
    participationSet.add(participationRecord);
    if (this.leaderSet == null) {
      this.leaderSet = new HashSet();
    }
    leaderSet.add(participationRecord);
  }

  public void removeLeader(String agentUuid) {
    participationSet.remove(agentUuid);
    leaderSet.remove(agentUuid);
  }

  public Set getEnrollmentRecords() {
    return enrollmentSet;
  }

  public void setEnrollmentRecords(Set enrollmentSet) {
    this.enrollmentSet = enrollmentSet;
  }

  public void addEnrollmentRecord(EnrollmentRecord enrollmentRecord) {
    if (this.enrollmentSet == null) {
      this.enrollmentSet = new HashSet();
    }
    enrollmentSet.add(enrollmentRecord);
  }

  public void removeEnrollmentRecord(EnrollmentRecord enrollmentRecord) {
    Iterator i = enrollmentSet.iterator();
    while (i.hasNext()) {
      EnrollmentRecord enrollment = (EnrollmentRecord) i.next();
      if (enrollment.equals(enrollmentRecord)) {
        enrollmentSet.remove(enrollment);
        enrollmentSet.remove(enrollment);
      }
    }
  }

  public void removeEnrollmentRecord(String agentUuid) {
    Iterator i = enrollmentSet.iterator();
    while (i.hasNext()) {
      EnrollmentRecord enrollment = (EnrollmentRecord) i.next();
      if (enrollment.getAgent().equals(agentUuid)) {
        enrollmentSet.remove(enrollment);
      }
    }
  }

  public EnrollmentStatusType getEnrollmentStatus() {
    return this.enrollmentStatus;
  }

  public void setEnrollmentStatus(EnrollmentStatusType status) {
    this.enrollmentStatus = status;
  }

  public Set getOtherPeople() {
    return otherPeopleSet;
  }

  public void addOtherPerson(ParticipationRecord participant) {
    if (this.participationSet == null) {
      this.participationSet = new HashSet();
    }
    participationSet.add(participant);
    if (this.otherPeopleSet == null) {
      this.otherPeopleSet = new HashSet();
    }
    otherPeopleSet.add(participant);
  }

  public void removeOtherPerson(String agentUuid) {
    Iterator i = participationSet.iterator();
    while (i.hasNext()) {
      ParticipationRecord participation = (ParticipationRecord) i.next();
      if (participation.getAgent().equals(agentUuid)) {
        participationSet.remove(participation);
        otherPeopleSet.remove(participation);
      }
    }
  }

  /* ***************** Structural Methods ***************** */
  public String getCourseOffering() {
    return this.courseOfferingUuid;
  }

  public void setCourseOffering(String courseOfferingUuid) {
    this.courseOfferingUuid = courseOfferingUuid;
  }

  public String getParentId() {
    return parentUuid;
  }

  public void setParentId(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public Set getAllSubSections() {
    return allChildren;
  }

  public Set getSubSectionSet() {
    return getAllSubSections();
  }

  public void setSubSectionSet(Set subSectionSet) {
    this.allChildren = subSectionSet;
  }

  public void addSection(String sectionUuid) {
    if (allChildren == null) {
      allChildren = new HashSet();
    }
    allChildren.add(sectionUuid);
  }

  public void removeSection(String sectionUuid) {
    if (allChildren != null) {
      allChildren.remove(sectionUuid);
    }
  }

  public Set getSubSectionsByType(CourseSectionType type) {
    return null;
  }

  /* ***************** Other Methods ***************** */
  public Boolean getAllowSelfRegistration() {
    return isAllowSelfRegistration;
  }

  public void setAllowSelfRegistration(Boolean allowSelfRegistration) {
    this.isAllowSelfRegistration = allowSelfRegistration;
  }

  /* ***************** Persistence Methods ***************** */
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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
  public boolean equals(Object rhs) {
    if (rhs == null) {
      return false;
    }
    if (! (rhs instanceof CourseSectionImpl)) {
      return false;
    }
    CourseSectionImpl that = (CourseSectionImpl) rhs;
    if (this.getCourseSectionId() == null || that.getCourseSectionId() == null) {
      return false;
    }
    return (this.getCourseSectionId().equals(that.getCourseSectionId()));
  }

  /**
   * Implementation of the hashCode method conforming to the Bloch pattern with
       * the exception of array properties (these are very unlikely primary key types).
   * @return int
   */
  public int hashCode() {
    if (this.hashValue == 0) {
      int result = 17;
      int coursesectionidValue = this.getCourseSectionId() == null ? 0 :
          this.getCourseSectionId().hashCode();
      result = result * 37 + coursesectionidValue;
      this.hashValue = result;
    }
    return this.hashValue;
  }

  public void setScheduleEvent(String scheduleEvent) {
    // TODO Auto-generated method stub

  }

  public String getScheduleEvent() {
    // TODO Auto-generated method stub
    return null;
  }

}
