/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.sakaiproject.component.section.sakai20;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.section.CourseManager;
import org.sakaiproject.api.section.SectionAwareness;
import org.sakaiproject.api.section.coursemanagement.Course;
import org.sakaiproject.api.section.coursemanagement.CourseSection;
import org.sakaiproject.api.section.coursemanagement.ParticipationRecord;
import org.sakaiproject.api.section.coursemanagement.User;
import org.sakaiproject.api.section.facade.Role;
import org.sakaiproject.component.section.facade.impl.sakai20.AuthzSakaiImpl;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * A sakai 2.0 based implementation of the Section Awareness API, using hibernate
 * for section data and legacy services for users and site membership.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class SectionAwarenessHibernateImpl extends HibernateDaoSupport
        implements SectionAwareness {

	private static final Log log = LogFactory.getLog(SectionAwarenessHibernateImpl.class);
	
    /**
     * In Sakai, as of 9.26.05, no way of monitoring changes in site membership.
     * So, in order to remove orphaned section memberships, we will check for
     * orphans every time getSectionEnrollments() and getSectionTas() are called.
     */
    protected CourseManager courseManager;

    public void init() {
    	FunctionManager.registerFunction("section.ta");
    }
    
	/**
	 * @inheritDoc
	 */
	public Set getSections(final String siteContext) {
    	if(log.isDebugEnabled()) log.debug("Getting sections for context " + siteContext);
        HibernateCallback hc = new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.getNamedQuery("findSectionsBySiteContext");
                q.setParameter("context", siteContext);
                return new HashSet(q.list());
            }
        };
        return (Set)getHibernateTemplate().execute(hc);
    }

	/**
	 * @inheritDoc
	 */
	public List getSectionCategories(String siteContext) {
		ResourceBundle bundle = ResourceBundle.getBundle(SectionAwareness.CATEGORY_BUNDLE);

		Enumeration keys = bundle.getKeys();
		List categoryIds = new ArrayList();
		while(keys.hasMoreElements()) {
			categoryIds.add(keys.nextElement());
		}
		Collections.sort(categoryIds);
		return categoryIds;
	}
	
	/**
	 * @inheritDoc
	 */
	public CourseSection getSection(final String sectionUuid) {
    	if(log.isDebugEnabled()) log.debug("Getting section with uuid=" + sectionUuid);
        HibernateCallback hc = new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException {
            	return getSection(sectionUuid, session);
            }
        };
        return (CourseSection)getHibernateTemplate().execute(hc);
	}

	private CourseSection getSection(final String sectionUuid, Session session) throws HibernateException {
        Query q = session.getNamedQuery("loadSectionByUuid");
        q.setParameter("uuid", sectionUuid);
        Object section = q.uniqueResult();
        if(section == null) {
        	throw new IllegalArgumentException("No section exists with uuid=" + sectionUuid);
        } else {
        	return (CourseSection)section;
        }
	}

	/**
	 * @inheritDoc
	 */
	public List getSiteMembersInRole(final String siteContext, final Role role) {
		List sakaiMembers;
		String siteRef = SakaiUtil.getSiteReference();
        if(role.isInstructor()) {
            sakaiMembers = SecurityService.unlockUsers(AuthzSakaiImpl.INSTRUCTOR_PERMISSION, siteRef);
        } else if(role.isTeachingAssistant()) {
            sakaiMembers = SecurityService.unlockUsers(AuthzSakaiImpl.TA_PERMISSION, siteRef);
            sakaiMembers.removeAll(SecurityService.unlockUsers(AuthzSakaiImpl.INSTRUCTOR_PERMISSION, siteRef));
        } else if(role.isStudent()) {
            sakaiMembers = SecurityService.unlockUsers(AuthzSakaiImpl.STUDENT_PERMISSION, siteRef);
            sakaiMembers.removeAll(SecurityService.unlockUsers(AuthzSakaiImpl.INSTRUCTOR_PERMISSION, siteRef));
            sakaiMembers.removeAll(SecurityService.unlockUsers(AuthzSakaiImpl.TA_PERMISSION, siteRef));
        } else {
        	// Role.NONE should throw an error
        	throw new RuntimeException("Can not get site members in role " + role.getDescription());
        }
        List membersList = new ArrayList();

        // Get the course object associated with this site
        HibernateCallback hc = new HibernateCallback() {
        	public Object doInHibernate(Session session) throws HibernateException, SQLException {
        		return getCourse(siteContext, session);
        	}
        };
        
        Course course = (Course)getHibernateTemplate().execute(hc);
    	if(course == null) {
    		if(log.isInfoEnabled()) log.info("No course founf for siteContext " + siteContext);
    		return new ArrayList();
    	}
        
        for(Iterator iter = sakaiMembers.iterator(); iter.hasNext();) {
        	org.sakaiproject.service.legacy.user.User sakaiUser = (org.sakaiproject.service.legacy.user.User)iter.next();
        	User user = SakaiUtil.convertUser(sakaiUser);
        	if(role.isInstructor()) {
        		InstructorRecordImpl record = new InstructorRecordImpl(course, user);
        		membersList.add(record);
        	}
        	if(role.isTeachingAssistant()) {
        		TeachingAssistantRecordImpl record = new TeachingAssistantRecordImpl(course, user);
        		membersList.add(record);
        	}
        	if(role.isStudent()) {
        		// TODO Where do we get the enrollment status?
        		EnrollmentRecordImpl record = new EnrollmentRecordImpl(course, null, user);
        		membersList.add(record);
        	}
        }
        return membersList;
	}

    private Course getCourse(String siteContext, Session session) throws HibernateException {
        Query q = session.getNamedQuery("loadCourseBySiteContext");
        q.setParameter("siteContext", siteContext);
        Object course = q.uniqueResult();
    	return (Course)course;
	}
	
	/**
	 * The sakai implementation will not use the database to do this kind of searching,
	 * so I'll skip doing optimizations here.
	 * 
	 * @inheritDoc
	 */
	public List findSiteMembersInRole(final String siteContext, final Role role, final String pattern) {
		List fullList = getSiteMembersInRole(siteContext, role);
		List filteredList = new ArrayList();
		for(Iterator iter = fullList.iterator(); iter.hasNext();) {
			ParticipationRecord record = (ParticipationRecord)iter.next();
			User user = record.getUser();
			if(user.getDisplayName().toLowerCase().startsWith(pattern.toLowerCase()) ||
			   user.getSortName().toLowerCase().startsWith(pattern.toLowerCase()) ||
			   user.getDisplayId().toLowerCase().startsWith(pattern.toLowerCase())) {
				filteredList.add(record);
			}
		}
		return filteredList;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isSiteMemberInRole(String siteContext, String userUid, Role role) {
		List members = getSiteMembersInRole(siteContext, role);
		for(Iterator iter = members.iterator(); iter.hasNext();) {
			ParticipationRecord record = (ParticipationRecord)iter.next();
			if(record.getUser().getUserUid().equals(userUid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This code pops up a few places...
	 * 
	 * @param role The role to check
	 * @param list A list of participant records returned by hibernate
	 * @return Whether the list of participation record includes one record of
	 * the specified role.
	 */
	private Boolean checkRole(final Role role, List list) {
		if(list.size() == 1) {
        	ParticipationRecord record = (ParticipationRecord)list.get(0);
        	if(record.getRole().equals(role)) {
        		if(log.isDebugEnabled()) log.debug("This user is in role " + role.getDescription());
            	return new Boolean(true);
        	} else {
        		if(log.isDebugEnabled()) log.debug("This user is not in role " + role.getDescription());
            	return new Boolean(false);
        	}
        } else if(list.size() == 0){
    		if(log.isDebugEnabled()) log.debug("This user has no role in this learning context.");
        	return new Boolean(false);
        } else {
        	throw new RuntimeException("There are multiple participation records for this user in this learning context.");
        }
	}

	/**
	 * @inheritDoc
	 */
	public List getSectionMembers(final String sectionUuid) {
        HibernateCallback hc = new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException {
            	CourseSection section = getSection(sectionUuid, session);
                String siteContext = section.getCourse().getSiteContext();
                
                // Remove any records that are no longer in the site
                courseManager.removeOrphans(siteContext);

                Query q = session.getNamedQuery("findSectionMembers");
                q.setParameter("sectionUuid", sectionUuid);
                return q.list();
            }
        };
        return getHibernateTemplate().executeFind(hc);
	}

	/**
	 * @inheritDoc
	 */
	public List getSectionMembersInRole(final String sectionUuid, final Role role) {
        HibernateCallback hc = new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException {
            	CourseSection section = getSection(sectionUuid, session);
                String siteContext = section.getCourse().getSiteContext();
                
                // Remove any records that are no longer in the site
                courseManager.removeOrphans(siteContext);
            	
            	Query q;
                
            	if(role.isInstructor()) {
                	q = session.getNamedQuery("findSectionInstructors");
        		} else if(role.isStudent()) {
                    q = session.getNamedQuery("findSectionStudents");
        		} else if(role.isTeachingAssistant()) {
                    q = session.getNamedQuery("findSectionTAs");
        		} else {
        			throw new IllegalArgumentException("There are no users without a role in a section.");
        		}
                q.setParameter("section", section);
                return q.list();
            }
        };
        return getHibernateTemplate().executeFind(hc);
	}


	/**
	 * @inheritDoc
	 */
	public boolean isSectionMemberInRole(final String sectionUuid, final String userUid, final Role role) {
        HibernateCallback hc = new HibernateCallback(){
	        public Object doInHibernate(Session session) throws HibernateException {
	        	CourseSection section = getSection(sectionUuid, session);
	            Query q = session.getNamedQuery("checkForSectionMembershipInRole");
	            q.setParameter("section", section);
	            q.setParameter("userUid", userUid);
	            List list = q.list();
	            return checkRole(role, list);
        	}
        };
        return ((Boolean)getHibernateTemplate().execute(hc)).booleanValue();
	}

	/**
	 * @inheritDoc
	 */
	public String getSectionName(final String sectionUuid) {
        HibernateCallback hc = new HibernateCallback(){
	        public Object doInHibernate(Session session) throws HibernateException {
	            Query q = session.getNamedQuery("loadSectionName");
	            q.setParameter("sectionUuid", sectionUuid);
	            Object name = q.uniqueResult();
	            if(name != null) {
	            	if(log.isDebugEnabled()) log.debug("Section " + sectionUuid + " does not exist.");
	            }
	            return name;
        	}
        };
        return (String)getHibernateTemplate().execute(hc);
	}

	/**
	 * @inheritDoc
	 */
	public String getSectionCategory(final String sectionUuid) {
        HibernateCallback hc = new HibernateCallback(){
	        public Object doInHibernate(Session session) throws HibernateException {
	            Query q = session.getNamedQuery("loadSectionCategory");
	            q.setParameter("sectionUuid", sectionUuid);
	            Object category = q.uniqueResult();
	            if(category == null) {
	            	if(log.isDebugEnabled()) log.debug("Section " + sectionUuid + " does not exist.");
	            }
            	return category;
	        }
        };
        return (String)getHibernateTemplate().execute(hc);
	}

	/**
	 * @inheritDoc
	 */
	public List getSectionsInCategory(final String siteContext, final String categoryId) {
        HibernateCallback hc = new HibernateCallback(){
	        public Object doInHibernate(Session session) throws HibernateException {
	            Query q = session.getNamedQuery("findSectionsByCategory");
	            q.setParameter("categoryId", categoryId);
	            q.setParameter("siteContext", siteContext);
	            return q.list();
	        }
        };
        return getHibernateTemplate().executeFind(hc);
	}

	/**
	 * @inheritDoc
	 */
	public String getCategoryName(String categoryId, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(SectionAwareness.CATEGORY_BUNDLE, locale);
		String name;
		try {
			name = bundle.getString(categoryId);
		} catch(MissingResourceException mre) {
			if(log.isInfoEnabled()) log.info("Could not find the name for category id = " + categoryId + " in locale " + locale.getDisplayName());
			name = null;
		}
		return name;
	}

	/**
	 * @inheritDoc
	 */
	public List getUnassignedMembersInRole(final String siteContext, final Role role) {
		List siteMembers = getSiteMembersInRole(siteContext, role);
        HibernateCallback hc = new HibernateCallback(){
	        public Object doInHibernate(Session session) throws HibernateException {
	            Query q;
	            if(role.isStudent()) {
	            	q = session.getNamedQuery("findSectionedStudentUserUids");
	            } else if(role.isTeachingAssistant()) {
	            	q = session.getNamedQuery("findSectionedTaUserUids");
	            } else {
	            	if(log.isInfoEnabled()) log.info(role + " is never assigned to sections, so unsectioned members is empty.");
	            	return new ArrayList();
	            }
	            q.setParameter("siteContext", siteContext);
	            return q.list();
	        }
        };
        List sectionedUids = getHibernateTemplate().executeFind(hc);
        List unSectionedRecords = new ArrayList();
        for(Iterator iter = siteMembers.iterator(); iter.hasNext();) {
        	ParticipationRecord record = (ParticipationRecord)iter.next();
        	if(! sectionedUids.contains(record.getUser().getUserUid())) {
        		unSectionedRecords.add(record);
        	}
        }
        return unSectionedRecords;
	}

	// Dependency injection

	public void setCourseManager(CourseManager courseManager) {
		this.courseManager = courseManager;
	}

}


/**********************************************************************************
 * $Id$
 *********************************************************************************/
