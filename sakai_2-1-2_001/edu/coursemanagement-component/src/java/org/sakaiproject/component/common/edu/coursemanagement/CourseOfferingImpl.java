package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CourseOffering;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingStatusType;
import org.sakaiproject.api.edu.coursemanagement.CourseOfferingType;
import org.sakaiproject.api.edu.coursemanagement.CourseSectionType;
import org.sakaiproject.api.edu.coursemanagement.EnrollmentType;
import org.sakaiproject.api.edu.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.edu.coursemanagement.Session;

public class CourseOfferingImpl
    implements CourseOffering, Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /** The cached hash code value for this instance.  Settting to 0 triggers re-calculation. */
  private int hashValue = 0;

  /** The composite primary key value. */
  private java.lang.Long courseOfferingId;

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
  private Integer maximumStudents;

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

  private Set participationSet;

  private List defaultLeaderList;

  private List otherPeopleList;

  private Set equivalentSet;
  private String equivalentString;

  private Set defaultLeaderSet;

  private Set otherPeopleSet;

  private String defaultLocation;

  private String defaultMeetingTime;

  private String defaultSchedule;

  private CourseOfferingStatusType courseOfferingStatus;

  public CourseOfferingImpl() {}

  public CourseOfferingImpl(String title, String description,
      String offeringNumber, String canonicalCourseUuid,
      SessionImpl session, CourseOfferingType type){
    this.setTitle(title);
    this.setDescription(description);
    this.setOfferingNumber(offeringNumber);
    this.setCanonicalCourse(canonicalCourseUuid);
    this.setSession(session);
    this.setOfferingType(type);
  }

  public Long getCourseOfferingId() {
    return this.courseOfferingId;
  }

  public void setCourseOfferingId(Long courseOfferingId) {
    this.courseOfferingId = courseOfferingId;
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

  public String getOfferingNumber() {
    return offeringNumber;
  }

  public void setOfferingNumber(String offeringNumber) {
    this.offeringNumber = offeringNumber;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public Integer getMaximumStudents() {
    return maximumStudents;
  }

  public void setMaximumStudents(Integer maxStudents) {
    this.maximumStudents = maxStudents;
  }

  public String getDefaultLocation() {
    return defaultLocation;
  }

  public void setDefaultLocation(String location) {
    this.defaultLocation = location;
  }

  public String getDefaultMeetingTime() {
    return defaultMeetingTime;
  }

  public void setDefaultMeetingTime(String meetingTime) {
    this.defaultMeetingTime = meetingTime;
  }

  public String getDefaultSchedule() {
    return defaultSchedule;
  }

  public void setDefaultSchedule(String schedule) {
    this.defaultSchedule = schedule;
  }

  /* ***************** Cross Listing Methods ***************** */
  public Boolean isCrossListed() {
    return getIsCrossListed();
  }

  public void setIsCrossListed(Boolean isCrossListed) {
    this.isCrossListed = isCrossListed;
  }

  public Boolean getIsCrossListed() {
    return isCrossListed;
  }

  /**
   * Return the value of the TOPICS column.
   * @return String
   */
  public String getEquivalentString() {
    return equivalentString;
  }

  /**
   * Set the value of the Equivalent column.
   * @param equivalentString
   */
  public void setEquivalentString(String equivalentString) {
    this.equivalentString = equivalentString;
    equivalentSet = getEquivalents();
  }

  public Set getEquivalents() {
    equivalentSet = new HashSet();
    if (equivalentString != null) {
      String[] equivalentArray = equivalentString.split("|");
      for (int i = 0; i < equivalentArray.length; i++) {
        String t = equivalentArray[i];
        equivalentSet.add(t);
      }
    }
    return equivalentSet;
  }

  public void addEquivalent(String equivalent) {
    if (equivalentString == null) {
      equivalentString = equivalent;
    }
    else {
      equivalentString = equivalentString + "|" + equivalent;
    }
    if (equivalentSet == null) {
      equivalentSet = new HashSet();
    }
    equivalentSet.add(equivalent);
  }

  public void removeEquivalent(String equivalent) {
    equivalentString = "";
    Iterator i = equivalentSet.iterator();
    while (i.hasNext()) {
      String t = (String) i.next();
      if (t.equals(equivalent)) {
        equivalentSet.remove(t);
      }
      else {
        if ( ("").equals(equivalentString)) {
          equivalentString = t;
        }
        else {
          equivalentString = equivalentString + "|" + t;
        }
      }
    }
  }

  /* ***************** Typing Methods ***************** */
  public CourseOfferingType getOfferingType() {
    return courseOfferingType;
  }

  public void setOfferingType(CourseOfferingType type) {
    this.courseOfferingType = type;
  }

  public CourseOfferingStatusType getOfferingStatus() {
    return courseOfferingStatus;
  }

  public void setOfferingStatus(CourseOfferingStatusType status) {
    this.courseOfferingStatus = status;
  }

  /* ***************** Structural Methods ***************** */
  public String getCanonicalCourse() {
    return canonicalCourseUuid;
  }

  public void setCanonicalCourse(String uuid) {
    this.canonicalCourseUuid = uuid;
  }

  public Set getCourseSections() {
    return courseSectionSet;
  }

  public void setCourseSections(Set courseSectionSet) {
    this.courseSectionSet = courseSectionSet;
  }

    /*
  public void addCourseSection(String sectionUuid) {
    if (this.courseSectionSet == null) {
      this.courseSectionSet = new HashSet();
    }
    courseSectionSet.add(sectionUuid);
  }
    */

  public void removeCourseSection(String sectionUuid) {
    Iterator i = courseSectionSet.iterator();
    while (i.hasNext()) {
      String s = (String) i.next();
      if (s.equals(sectionUuid)) {
        courseSectionSet.remove(sectionUuid);
      }
    }
  }

  public Set getSectionsByType(CourseSectionType type) {
    // TODO Auto-generated method stub
    return null;
  }

  /* ***************** Grouping Methods ***************** */
  public Set getParticipationSet() {
    return participationSet;
  }

  public void setParticipationSet(Set participationSet) {
    defaultLeaderSet = new HashSet();
    otherPeopleSet = new HashSet();
    this.participationSet = new HashSet();
    this.participationSet = participationSet;
    Iterator i = participationSet.iterator();
    while (i.hasNext()) {
      ParticipationRecord p = (ParticipationRecord) i.next();
      participationSet.add(p);
      if (p.getIsLeader().booleanValue()) {
        defaultLeaderList.add(p);
      }
      if (p.getIsOtherPeople().booleanValue()) {
        otherPeopleList.add(p);
      }
    }
  }

  public Set getDefaultLeaders() {
    return defaultLeaderSet;
  }

  public void addDefaultLeader(ParticipationRecord participationRecord) {
    if (this.participationSet == null) {
      this.participationSet = new HashSet();
    }
    if (this.defaultLeaderSet == null) {
      this.defaultLeaderSet = new HashSet();
    }
    participationSet.add(participationRecord);
    defaultLeaderSet.add(participationRecord);
  }

  public void removeDefaultLeader(String agentUuid) {
    if (this.participationSet != null) {
      participationSet.remove(agentUuid);
    }
    if (this.defaultLeaderSet != null) {
      defaultLeaderSet.remove(agentUuid);
    }
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
    if (this.participationSet != null) {
      participationSet.remove(agentUuid);
    }
    if (this.otherPeopleSet != null) {
      otherPeopleList.remove(agentUuid);
    }
  }

  public Set getAllEnrollments() {
    // TODO Auto-generated method stub
    return null;
  }

  public Set getAggregatedEnrollments(CourseSectionType type) {
    // TODO Auto-generated method stub
    return null;
  }

  public EnrollmentType getEnrollmentType() {
    return enrollmentType;
  }

  public void setEnrollmentType(EnrollmentType type) {
    this.enrollmentType = type;
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
    if (! (rhs instanceof CourseOffering)) {
      return false;
    }
    CourseOfferingImpl that = (CourseOfferingImpl) rhs;
    if (this.getCourseOfferingId() == null || that.getCourseOfferingId() == null) {
      return false;
    }
    return (this.getCourseOfferingId().equals(that.getCourseOfferingId()));
  }

  /**
   * Implementation of the hashCode method conforming to the Bloch pattern with
       * the exception of array properties (these are very unlikely primary key types).
   * @return int
   */
  public int hashCode() {
    if (this.hashValue == 0) {
      int result = 17;
      int CourseOfferingIdValue = this.getCourseOfferingId() == null ? 0 :
          this.getCourseOfferingId().hashCode();
      result = result * 37 + CourseOfferingIdValue;
      this.hashValue = result;
    }
    return this.hashValue;
  }

}